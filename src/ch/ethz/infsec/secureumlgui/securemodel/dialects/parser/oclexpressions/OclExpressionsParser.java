/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

import java.util.Iterator;
import java.util.LinkedList;

import org.omg.uml.foundation.core.AssociationEnd;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 *
 */
public class OclExpressionsParser
{
//  public static String OPENING_BRACKET = "[";
//  public static String CLOSING_BRACKET = "]";

    public static String OPENING_PARANTHESIS_REGEX = "\\(";
    public static String CLOSING_PARANTHESIS_REGEX = "\\)";

    public static char OPENING_PARANTHESIS_CHAR= '(';
    public static char CLOSING_PARANTHESIS_CHAR = ')';

    public static String EQUAL_SIGN = "=";
    public static String NOT_EQUAL_SIGN = "<>";

    public static String ARROW = "->";
    public static String DOT = ".";

    public static String SELF= "self";

    public static String OPERATOR_SELECT = "select";
    public static String SET_OPERATOR_UNION = "union";
    public static String SET_OPERATOR_INTERSECT = "intersect";
    public static String SET_OPERATOR_MINUS = "minus";

    public static String booleanOrRegexp = " or ";
    public static String booleanAndRegexp = " and ";
    public static String booleanNotRegexp = "not ";

    MultiContextLogger logger = MultiContextLogger.getDefault();

//  public Collection<Path> parsePathList(String s)
//  {
//    LinkedList<Path> paths = new LinkedList<Path>();
//
//    String[] pathStrings = s.split(";");
//
//    for (int i = 0; i < pathStrings.length; i++)
//    {
//      Path p = parsePath(pathStrings[i]);
//
//      paths.add(p);
//    }
//
//    return paths;
//  }

    public OclExpression parseOclExpression(String expressionString)
    {
        //logger.info("ocl = '"+expressionString+"'");
        String s = expressionString;

        s = removeHeadingWhitespace(s);


        OclExpression oclExpression = new OclExpression();

        if(s.startsWith(SELF))
        {
            oclExpression.appendStep(new Self());

            s = s.substring(SELF.length());
        }
        else
        {
            s = "." + s;
        }

        s = removeHeadingWhitespace(s);
        String rest = s;

        while(s.length() > 0)
        {
            String operator = null;
            if(s.startsWith(DOT))
            {
                operator = DOT;
                rest = s.substring(DOT.length());

            }
            else if(s.startsWith(ARROW))
            {
                operator = ARROW;
                rest = s.substring(ARROW.length());

            }
            else
            {
                logger.error("invalid path String: '"
                             + s + "'");
                break;
            }


            if(operator == DOT)
                // parse PropertyAccessStep
            {
                int nextDotIndex = rest.indexOf(DOT);
                int nextEqualSignIndex = rest.indexOf(ARROW);


                int stepEnd =
                    getMinIndex(nextDotIndex, nextEqualSignIndex);
                if(stepEnd == -1)
                {
                    if(rest.length()>0)
                        // case of the last step in expression
                        stepEnd = rest.length();
                    else
                        logger.error("invalid rest of expression: " + s);
                }

                String stepString = rest.substring(0, stepEnd);

                PropertyAccessStep step =
                    parsePropertyAccessStep(stepString);
                //new PropertyAccessStep(propertyName);

                oclExpression.appendStep(step);

                s = rest.substring(stepEnd);
                s.toString();
            }
            else if(operator == ARROW)
                // parse OperationStep
            {
                int contentStart = 1 +
                                   rest.indexOf(OPENING_PARANTHESIS_CHAR);
                // note, when using OPENING_PARANTHESIS_REGEX, it does not work
                // (result is always index = -1)

                String operatorName = rest.substring(0, contentStart-1);

                // cut start including the opening bracket
                rest = rest.substring(contentStart);

                int closingBracketIndex =
                    findMatchingClosingParanthesesIndex(rest);

                String contentString =
                    rest.substring(0, closingBracketIndex);

                OperationStep selectionStep = null;

                selectionStep =
                    parseSetOperationStep(
                        operatorName, contentString);

                //parseSelectionStep(contentString);

                oclExpression.appendStep(selectionStep);

                s = rest.substring(closingBracketIndex+1);
            }
        }

//    logger.info("OCL Expression parsed: "
//        + oclExpression.toString());
        return oclExpression;
    }

    /**
     * @param int1
     * @param int2
     * @return the minimum
     */
    private int getMinIndex(int int1, int int2)
    {
        int minIndex = -1;

        if(int1 == -1)
        {
            minIndex = int2;
        }
        else if(int2 == -1)
        {
            minIndex = int1;
        }
        else
        {
            return Math.min(int1, int2);
        }
        return minIndex;
    }

    public int findMatchingClosingParanthesesIndex(String s)
    {
        int depth = 1;

        int startIndex = 0;

        while(s.length() > 0 && depth > 0)
        {
            int paranthesesIndex = getMinIndex(
                                       s.indexOf(OPENING_PARANTHESIS_CHAR, startIndex),
                                       s.indexOf(CLOSING_PARANTHESIS_CHAR, startIndex));

            if(paranthesesIndex == -1)
            {
                logger.error("invalid rest of expression: " + s);
            }
            else
                startIndex = paranthesesIndex;

            //s = s.substring(paranthesesIndex);

            if(s.substring(startIndex).
                    charAt(0) == OPENING_PARANTHESIS_CHAR)
            {
                depth++;
            }
            else if(s.substring(startIndex).
                    charAt(0) == (CLOSING_PARANTHESIS_CHAR))
            {
                depth--;

                if(depth == 0)
                    return startIndex;
            }
            startIndex++;

        }
        return -1;
    }



    public PropertyAccessStep parsePropertyAccessStep(String stepString)
    {
        PropertyAccessStep step = new PropertyAccessStep(stepString);

        int asterixIndex = stepString.lastIndexOf("*");


        if(asterixIndex < 0)
            // there is no *
        {
            step.setRepetition(1);
        }
        else // there is a *
        {
            String afterAsterix = stepString.substring(asterixIndex + 1);

            if(afterAsterix == null || afterAsterix.length()==0)
                // * the last character -> n times
            {
                step.setRepetition(-1);

                // asterix is handled, cut it
                stepString = stepString.substring(0, asterixIndex);

            }
            else
                // i.e. the * corresponds to this step,
            {
                int repetition = Integer.parseInt(afterAsterix);
                step.setRepetition(repetition);

                // asterix and number handled, cut'em
                stepString = stepString.substring(0, asterixIndex);
            }
        }

        // repetition handled if there


        step.setPropertyName(stepString);

        // step itself handled


        return step;
    }

    public OperationStep parseSetOperationStep(
        String operatorName, String contentString)
    {
        if(operatorName.equals(OPERATOR_SELECT))
        {
            return parseSelectionStep(contentString);
        }
        else
        {
            OclExpression expression = parseOclExpression(contentString);

            SetOperationStep step = null;

            if(operatorName.equals(SET_OPERATOR_UNION))
            {
                step = new SetOperationStep(SET_OPERATOR_UNION, expression);
            }
            else if(operatorName.equals(SET_OPERATOR_INTERSECT))
            {
                step = new SetOperationStep(SET_OPERATOR_INTERSECT, expression);
            }
            else if(operatorName.equals(SET_OPERATOR_MINUS))
            {
                step = new SetOperationStep(SET_OPERATOR_MINUS, expression);
            }
            else
            {
                logger.error("invalid Set Operator: " + operatorName);
            }

            return step;
        }

    }

    public SelectionStep parseSelectionStep(
        String conditionString)
    // throws OclParserException
    {
        SelectionStep result = new SelectionStep();

        ExpressionFragment condition = parseCondition(conditionString);

        result.setCondition(condition);

        //Literal literal = PathFactory.createLiteral(conditionString);

        return result;
    }

    /**
     * @param conditionString
     */
    private ExpressionFragment parseCondition(String conditionString)
    //, SelectionStep result)
    {
        conditionString = removeOuterParanthesis(conditionString);

        LinkedList<String> orParts =
            splitConditionString(conditionString, booleanOrRegexp);

        if(orParts.size() > 1)
        {
            DisjunctiveBooleanExpression orExpression =
                new DisjunctiveBooleanExpression();
            for (Iterator iter = orParts.iterator(); iter.hasNext();)
            {
                String orPartString = (String) iter.next();

                ExpressionFragment orPart =
                    parseCondition(orPartString);

                //parseExpressionOrLiteral(orPartString);

                orExpression.getTerms().add(orPart);


            }

            return orExpression;
        }
        else
        {
            LinkedList<String> andParts =
                splitConditionString(conditionString, booleanAndRegexp);

            if(andParts.size() > 1)
            {
                ConjunctiveBooleanExpression andExpression =
                    new ConjunctiveBooleanExpression();
                for (Iterator iter = orParts.iterator(); iter.hasNext();)
                {
                    String andPartString = (String) iter.next();

                    ExpressionFragment andPart =
                        //parseExpressionOrLiteral(andPartString);
                        parseCondition(andPartString);

                    andExpression.getTerms().add(andPart);
                }
                return andExpression;
            }
            else
            {
                if(conditionString.startsWith(booleanNotRegexp))
                {
                    conditionString =
                        conditionString.substring(booleanNotRegexp.length());

                    ExpressionFragment expr =
                        parseCondition(conditionString);
                    //  parseExpressionOrLiteral(conditionString);

                    ConjunctiveBooleanExpression negatedExpression =
                        new ConjunctiveBooleanExpression();
                    negatedExpression.setNegated(true);
                    negatedExpression.getTerms().add(expr);

                    return negatedExpression;
                }
                else
                {
                    // now, all ands-, ors and such stuff is out

                    String equalitySignsRegexp = "=|\\<\\>";


                    LinkedList<String> equalityExpressions =
                        splitConditionString(conditionString, equalitySignsRegexp);

                    ExpressionFragment expressionFragment =
                        parseEquation(conditionString, equalityExpressions);

                    return expressionFragment;
                }
            }
        }



    }



    /**
     * @param conditionString
     * @param equalityExpressions
     */
    private ExpressionFragment parseEquation(
        String conditionString, LinkedList<String> equalityExpressions)
    //throws OclParserException
    {
        if(equalityExpressions.size() == 1)
            // no equality sign on this level
            // -> expressionOrLiteral
        {
            ExpressionFragment expressionFragment =
                parseExpressionOrLiteral(
                    equalityExpressions.get(0));

            return expressionFragment;
        }
        else if (equalityExpressions.size() == 2)
        {
            ExpressionFragment pathFragment1 =
                parseExpressionOrLiteral(
                    equalityExpressions.get(0));

            ExpressionFragment pathFragment2 =
                parseExpressionOrLiteral(
                    equalityExpressions.get(1));

            String comparatorSign =
                conditionString.substring(equalityExpressions.get(0).length(),
                                          conditionString.length() - equalityExpressions.get(1).length());

            boolean isEqual = true;
            if(comparatorSign.equals(EQUAL_SIGN))
                isEqual = true;
            else if(comparatorSign.equals(NOT_EQUAL_SIGN))
                isEqual = false;
            else
            {
                logger.error("invalid selection Condition Equation: "
                             + " comparatorSign is: " + comparatorSign);
            }

            Equation equation =
                new Equation(pathFragment1, pathFragment2, isEqual);

            return equation;
        }
        else
        {
            logger.error("invalid PathSelector Condition: "
                         + conditionString);
//      throw new OclParserException(
//          "invalid PathSelector Condition: "
//          + conditionString);
            return null;
        }

    }

    public String removeHeadingWhitespace(String s)
    {
        while(s != null && s.length() > 0
                && ((s.charAt(0) == ' ')
                    || (s.charAt(0) == '\n')))
        {
            s = s.substring(1);
        }
        return s;
    }

    public String removeTrailingWhitespace(String s)
    {
        while(s != null && s.length() >0
                && ((s.charAt(s.length()-1) == ' ')
                    || (s.charAt(s.length()-1) == '\n')))
        {
            s = s.substring(0, s.length()-1);
        }
        return s;
    }



    /** remove the outer paranthesis of a String,
     *  if represent a pair
     *
     * @param string
     * @return the transformed string
     */
    public String removeOuterParanthesis(String string)
    {
        String s = string;

        if(s.length() > 1
                && s.charAt(0) == OPENING_PARANTHESIS_CHAR
                && s.charAt(s.length()-1) == CLOSING_PARANTHESIS_CHAR)
        {
            int depth = 0;

            int indexOpen = s.indexOf(OPENING_PARANTHESIS_REGEX);
            int indexClose = s.indexOf(CLOSING_PARANTHESIS_REGEX);

            while(indexOpen > 0 && indexClose > 0)
            {
                if(indexOpen<indexClose)
                {
                    s = s.substring(indexOpen+1);
                    indexOpen = s.indexOf(OPENING_PARANTHESIS_REGEX);

                    depth ++;
                }
                else if(indexClose<indexOpen)
                {
                    s = s.substring(indexClose+1);
                    indexClose = s.indexOf(CLOSING_PARANTHESIS_REGEX);

                    depth --;

                    if(depth < 0)
                        return s;
                }
            }

            if(depth == 0)
                return s.substring(1, s.length());
            else
                return s;
        }
        else
            return s;
    }

    public LinkedList<String> splitConditionString(
        String conditionString, String splitRegexp)
    {
        String[] conditionPartStrings =
            conditionString.split(splitRegexp);


        LinkedList<String> result = new LinkedList<String>();

        int bracketDepth = 0;

        if(conditionPartStrings.length == 1)
            // no equality sign
            // -> expressionOrLiteral
        {
            result.add(conditionPartStrings[0]);
//      ExpressionFragment condition =
//        parseExpressionOrLiteral(equationPartStrings[0]);
//
//      result.setCondition(condition);
        }
        if(conditionPartStrings.length >= 2)
            // one or more equality signs
        {
            for (int i = 0; i < conditionPartStrings.length; i++)
            {
                String partString = conditionPartStrings[i];


                bracketDepth = countBrackets(partString);
                if(bracketDepth == 0)
                    result.add(partString);
                else
                {
                    // join with n next segments, until bracketDepth is 0

                    while(bracketDepth != 0)
                    {
                        // the whole part is stored at allparts[i]
                        bracketDepth += countBrackets(conditionPartStrings[i+1]);

                        conditionPartStrings[i+1] = conditionPartStrings[i] + conditionPartStrings[i+1];
                        // now the whole part is stored at allparts[i+1]
                        i++;

                    }
                    // the whole part is stored at allparts[i]

                    result.add(conditionPartStrings[i]);

                }
            }
        }

        return result;
    }

    /**
     * @param s
     */
    public ExpressionFragment parseExpressionOrLiteral(String s)
    {
        s = removeHeadingWhitespace(s);
        s = removeTrailingWhitespace(s);

        Literal literal = tryParseLiteral(s);
        if(literal != null)
            return literal;
        else
        {
            OclExpression p = parseOclExpression(s);

            return p;
        }
    }

    public Literal tryParseLiteral(String s)
    {
        Literal l = ExpressionFactory.createLiteral(s);
        return l;
    }


    public int countBrackets(String s)
    {
        if(s == null || s.length() == 0)
            return 0;

        int depth = 0;

        int indexOpen = s.indexOf(OPENING_PARANTHESIS_REGEX);
        int indexClose = s.indexOf(CLOSING_PARANTHESIS_REGEX);

        while(indexOpen > 0 && indexClose > 0)
        {
            if(indexOpen<indexClose)
            {
                s = s.substring(indexOpen+1);
                indexOpen = s.indexOf(OPENING_PARANTHESIS_REGEX);

                depth ++;
            }
            else if(indexClose<indexOpen)
            {
                s = s.substring(indexClose+1);
                indexClose = s.indexOf(CLOSING_PARANTHESIS_REGEX);

                depth --;
            }
            else
            {
                //
                //throw new Exception("not possible, [] at the same position");
            }
        }

        return depth;

    }

}
