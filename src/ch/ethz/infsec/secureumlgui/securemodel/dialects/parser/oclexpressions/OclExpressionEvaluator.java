/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.LoggerContext;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 *
 */
public class OclExpressionEvaluator
{
    public OclExpressionEvaluator(
        OclExpression oclExpression,
        Object startPoint,
        Object self)
    {
        this.oclExpression = oclExpression;
        this.initialStartPoint = initialStartPoint;
        this.self = self;

        logger.disableLoggerContext(OCL_EXPRESSION_EVALUATOR_DETAILLED);
        logger.disableLoggerContext(OCL_EXPRESSION_EVALUATOR);
    }
    /**
     *
     */
    public OclExpressionEvaluator(
        OclExpression oclExpression,
        Object startPoint)
    {
        this(oclExpression, startPoint, startPoint);


    }

    public static LoggerContext OCL_EXPRESSION_EVALUATOR =
        new LoggerContext(
        "OclExpressionEvaluator",
        "OclExpressionEvaluator");
    public static LoggerContext OCL_EXPRESSION_EVALUATOR_DETAILLED =
        new LoggerContext(
        "OclExpressionEvaluatorDetailled",
        "OclExpressionEvaluatorDetailled");

    public static String OPERATOR_SELECT = "select";
    public static String SET_OPERATOR_UNION = "union";
    public static String SET_OPERATOR_INTERSECT = "intersect";
    public static String SET_OPERATOR_MINUS = "minus";



    private OclExpression oclExpression;
    private Object initialStartPoint;
    private Object self;

    MultiContextLogger logger = new MultiContextLogger(OCL_EXPRESSION_EVALUATOR);

    public Set evaluateExpression()
    throws OclEvaluatorException
    {
        return evaluateExpression(oclExpression, initialStartPoint, self);
    }

    protected Set evaluateExpression(
        OclExpression p, Object startPoint, Object self)
    throws OclEvaluatorException
    {
        Set startPoints = new LinkedHashSet();
        startPoints.add(startPoint);

        Set result = new LinkedHashSet();

        for (Iterator iter = p.getSteps().iterator(); iter.hasNext();)
        {
            PathStep step = (PathStep) iter.next();

            result = evaluateStep(step, startPoints);

            startPoints = result;
        }

        return result;
    }

    protected Set evaluateStep(
        PathStep step, Set startPoints)
    throws OclEvaluatorException
    {
        Set result = new LinkedHashSet();

        if(step instanceof Self)
        {
            result.add(self);
        }
        else if (step instanceof PropertyAccessStep)
        {
            PropertyAccessStep propertyAccessStep = (PropertyAccessStep) step;

            takePropertyAccessStep(result, propertyAccessStep, startPoints);

        }
        else if (step instanceof SelectionStep)
        {
            SelectionStep selectionStep = (SelectionStep) step;

            takeSelectionStep(result, selectionStep, startPoints);
        }
        else if (step instanceof SetOperationStep)
        {
            SetOperationStep setOperationStep = (SetOperationStep) step;

            takeSetOperationStep(result, setOperationStep, startPoints);
        }

        return result;
    }

    /**
     * @param result
     * @param paStep
     * @param startPoints
     */
    protected void takePropertyAccessStep(
        Set result, PropertyAccessStep paStep, Set startPoints)
    throws OclEvaluatorException
    {
        String propertyName = paStep.getPropertyName();
        propertyName = removeHeadingWhitespace(propertyName);
        propertyName = removeTrailingWhitespace(propertyName);

        if(propertyName == null
                || propertyName.length() == 0)
        {
            logger.warn(
                "propertyAccessStep with empty " +
                "propertyName, ignoring...");
            result.addAll(startPoints);
        }
        else
        {

            for (Iterator iter = startPoints.iterator(); iter.hasNext();)
            {
                Object startPoint = (Object) iter.next();


                try
                {
                    Object value =
                        Util.getProperty(startPoint, propertyName);

                    if (value instanceof Collection)
                    {
                        Collection values = (Collection) value;
                        result.addAll(values);
                    }
                    else if(value != null)
                    {
                        result.add(value);
                    }
                    else
                    {
                        logger.warn(OCL_EXPRESSION_EVALUATOR_DETAILLED,
                                    "accessing Property "
                                    + paStep.getPropertyName()
                                    + " on Object of Type: "
                                    + startPoint.getClass().getSimpleName()
                                    + " returned null");
                    }

                }
                catch (Exception e)
                {
                    logger.error(OCL_EXPRESSION_EVALUATOR_DETAILLED,
                                 "accessing Property "
                                 + paStep.getPropertyName()
                                 + " on Object of Type: "
                                 + startPoint.getClass().getSimpleName()
                                 + " failed");

                    //logger.logException(e);
                }
            }
        }
    }

    protected void takeSetOperationStep(
        Set result, SetOperationStep setOperationStep, Collection startPoints)
    throws OclEvaluatorException
    {
        result.addAll(startPoints);

        Set expressionResult =
            evaluateExpression(
                setOperationStep.getExpression(), self, self);

        // using bulk operations for the set operations as described on
        // http://java.sun.com/docs/books/tutorial/collections/interfaces/set.html
        if(setOperationStep.getOperation().equals(SET_OPERATOR_UNION))
        {
            result.addAll(expressionResult);
        }
        else if(setOperationStep.getOperation().equals(SET_OPERATOR_INTERSECT))
        {
            result.retainAll(expressionResult);
        }
        else if(setOperationStep.getOperation().equals(SET_OPERATOR_MINUS))
        {
            result.removeAll(expressionResult);
        }
    }

    protected void takeSelectionStep(
        Set result, SelectionStep selectionStep, Collection startPoints)
    throws OclEvaluatorException
    {
        for (Iterator iter = startPoints.iterator(); iter.hasNext();)
        {
            Object startPoint = (Object) iter.next();

            try
            {
                boolean conditionResult =
                    evaluateCondition(
                        selectionStep.getCondition(),
                        startPoint, self);

                if(conditionResult)
                {
                    result.add(startPoint);
                }

                // - if true, add result.add(startPoint)
            }
            catch (Exception e)
            {
                logger.logException(e);
            }
        }
    }

    protected boolean evaluateCondition(
        ExpressionFragment condition, Object startpoint, Object self)
    throws OclEvaluatorException
    {
        if (condition instanceof Equation)
        {
            Equation equation = (Equation) condition;

            Object leftValue = null;
            Object rightValue = null;

            // evaluate left part
            if(equation.getLeftPart() instanceof Literal)
            {
                leftValue = evaluateLiteral((Literal)equation.getLeftPart());
            }
            else
            {
                Set leftResult =
                    evaluateExpression(
                        (OclExpression)equation.getLeftPart(),
                        startpoint, self);

                if(leftResult.size() == 1)
                    leftValue = leftResult.iterator().next();
                else
                    throw new OclEvaluatorException(
                        "left part of the equation does not evaluate to a literal"
                        + equation.toString());


            }
            // evaluate right part
            if(equation.getRightPart() instanceof Literal)
            {
                rightValue = evaluateLiteral((Literal)equation.getRightPart());
            }
            else
            {
//        if(equation.getRightPart() instanceof OclExpression)
//        {
                Set rightResult =
                    evaluateExpression(
                        (OclExpression)equation.getRightPart(),
                        startpoint, self);
                if(rightResult.size() == 1)
                    rightValue = rightResult.iterator().next();
                else
                    throw new OclEvaluatorException(
                        "right part of the equation does not evaluate to a literal: "
                        + equation.toString());
//        }
            }

            // compare left and right part
            if(equation.isEqual())
                return leftValue.equals(rightValue);
            else
                return !leftValue.equals(rightValue);
        }
        else if (condition instanceof BooleanLiteral)
        {
            BooleanLiteral booleanLiteral = (BooleanLiteral) condition;

            return booleanLiteral.getValue();
        }
        else if (condition instanceof OclExpression)
        {
            OclExpression expression = (OclExpression) condition;

            Set result = evaluateExpression(expression, startpoint, self);
            if(result.size() == 1)
            {
                Object item = result.iterator().next();
                if(item.equals(true))
                    return true;
                else if(item.equals(false))
                    return false;
                else
                {
                    throw new OclEvaluatorException(
                        "condition OCL-Expression " +
                        "does not evaluate to a boolean: "
                        + item);
                }
            }
            else
            {
                if(result.size() == 0)
                    throw new OclEvaluatorException(
                        "condition OCL-Expression " +
                        "evaluation returned no result object: "
                        + result.size());
                else
                    throw new OclEvaluatorException(
                        "condition OCL-Expression " +
                        "evaluation returned more than one result object: "
                        + result.size());
            }
        }
        else if (condition instanceof ConjunctiveBooleanExpression)
        {
            ConjunctiveBooleanExpression booleanExpression =
                (ConjunctiveBooleanExpression) condition;

            for (Iterator iter = booleanExpression.getTerms().iterator(); iter.hasNext();)
            {
                ExpressionFragment term = (ExpressionFragment) iter.next();

                boolean termValue = evaluateCondition(term, startpoint, self);

                if(!termValue)
                    // one term false -> AND of all is false
                    return booleanExpression.isNegated();
            }
            // if arrived here, all terms evaluate to 'true'
            return !booleanExpression.isNegated();
        }
        else if (condition instanceof DisjunctiveBooleanExpression)
        {
            DisjunctiveBooleanExpression booleanExpression =
                (DisjunctiveBooleanExpression) condition;

            for (Iterator iter = booleanExpression.getTerms().iterator(); iter.hasNext();)
            {
                ExpressionFragment term = (ExpressionFragment) iter.next();

                boolean termValue = evaluateCondition(term, startpoint, self);

                if(termValue)
                    // one term true -> OR of all is true
                    return !booleanExpression.isNegated();
            }
            // if arrived here, all terms evaluate to 'false'
            return booleanExpression.isNegated();
        }
        else
            throw new OclEvaluatorException(
                "Selection Condition has invalid type: "
                + condition);
    }

    protected Object evaluateLiteral(Literal literal)
    throws OclEvaluatorException
    {
        if (literal instanceof BooleanLiteral)
        {
            BooleanLiteral booleanLiteral = (BooleanLiteral) literal;

            return booleanLiteral.getValue();
        }
        else if (literal instanceof IntLiteral)
        {
            IntLiteral intLiteral = (IntLiteral) literal;

            return intLiteral.getValue();
        }
        else if (literal instanceof StringLiteral)
        {
            StringLiteral stringLiteral = (StringLiteral) literal;

            return stringLiteral.getValue();
        }
        else
            throw new OclEvaluatorException(
                "literal of unknown type: "
                + literal);

    }


    public String removeHeadingWhitespace(String s)
    {
        while(s!= null && s.length()>0
                && ((s.charAt(0) == ' ')
                    || (s.charAt(0) == '\n')))
        {
            s = s.substring(1);
        }
        return s;
    }

    public String removeTrailingWhitespace(String s)
    {
        while(s!= null  && s.length()>0
                && ((s.charAt(s.length()-1) == ' ')
                    || (s.charAt(s.length()-1) == '\n')))
        {
            s = s.substring(0, s.length()-1);
        }
        return s;
    }



}
