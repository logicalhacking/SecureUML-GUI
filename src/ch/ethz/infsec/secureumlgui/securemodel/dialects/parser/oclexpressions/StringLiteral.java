/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 */
public class StringLiteral extends Literal
{
    /**
     *
     */
    public StringLiteral(String value)
    {
        this.value = value;
    }

    private String value;

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "StringLiteral("+value+")";
    }

}
