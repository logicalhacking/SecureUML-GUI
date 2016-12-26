/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 */
public class ExpressionFactory
{
    public static String TRUE = "true";
    public static String FALSE = "false";

    /** If s represents a literal, a literal of the corresponding
     * type is created and returned
     * - null is returned otherwise.
     *
     * @param s
     * @return the created Literal
     */
    public static Literal createLiteral(String s)
    {
        if(s == null | s.length() == 0)
            return null;
        else if(s.equals(TRUE))
        {
            return new BooleanLiteral(true);
        }
        else if(s.equals(FALSE))
        {
            return new BooleanLiteral(false);
        }
        else if(Character.isDigit(s.charAt(0)))
            // Int or Decimal Literal
        {
            String[] parts = s.split("\\.");
            if(parts.length == 1)
                // doesn't contain a dot
            {
                int intValue = Integer.valueOf(parts[0]);
                return new IntLiteral(intValue);
            }
            else if (parts.length == 2)
            {
                double doubleValue = Double.valueOf(s);
                return new DecimalLiteral(doubleValue);
            }
        }
        else if(isQuoteOrDoubleQuote(s.charAt(0))
                && isQuoteOrDoubleQuote(s.charAt(s.length()-1)))
        {
            String literalString = s.substring(1, s.length()-1);
            return new StringLiteral(literalString);
        }

        return null;
    }

    public static boolean isQuoteOrDoubleQuote(char c)
    {
        Character character = Character.valueOf(c);
        Character doubleQuoteChar = "\"".charAt(0);
        Character quoteChar = "'".charAt(0);

        boolean result = character.equals(quoteChar)
                         || character.equals(doubleQuoteChar);

        return result;
    }

}
