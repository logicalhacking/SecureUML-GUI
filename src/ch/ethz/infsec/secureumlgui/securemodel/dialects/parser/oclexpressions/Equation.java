/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/** LeftPart and RightPart must evaluate to comparable Literal Types
 *
 */
public class Equation extends ExpressionFragment
{
    /**
     *
     */
    public Equation()
    {
        ;
    }

    /**
     *
     */
    public Equation(
        ExpressionFragment leftPart,
        ExpressionFragment rightPart,
        boolean isEqual)
    {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.isEqual = isEqual;
    }


    /** true if ... = ...,
     *  false if ... <> ...
     *
     */
    private boolean isEqual;

    public boolean isEqual()
    {
        return isEqual;
    }

    public void setEqual(boolean isEqual)
    {
        this.isEqual = isEqual;
    }

    private ExpressionFragment leftPart;

    public ExpressionFragment getLeftPart()
    {
        return leftPart;
    }

    public void setLeftPart(ExpressionFragment leftPart)
    {
        this.leftPart = leftPart;
    }

    private ExpressionFragment rightPart;

    public ExpressionFragment getRightPart()
    {
        return rightPart;
    }

    public void setRightPart(ExpressionFragment rightPart)
    {
        this.rightPart = rightPart;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Equation("
               + leftPart + ", "
               + rightPart + ")";
    }
}
