/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 */
public class IntLiteral extends Literal
{
    /**
     *
     */
    public IntLiteral(int value)
    {
        this.value = value;
    }

    private int value;

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "IntLiteral("+value+")";
    }

}
