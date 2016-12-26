/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 */
public class DecimalLiteral extends Literal
{
    /**
     *
     */
    public DecimalLiteral(double value)
    {
        this.value = value;
    }

    private double value;

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "DecimalLiteral("+value+")";
    }
}
