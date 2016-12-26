/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 */
public class BooleanLiteral extends Literal
{
    /**
     *
     */
    public BooleanLiteral(boolean value)
    {
        this.value = value;
    }

    private boolean value;

    public boolean getValue()
    {
        return value;
    }

    public void setValue(boolean value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "BooleanLiteral("
               + (value?"true":"false")
               + ")";
    }
}
