/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 *
 *
 */
public class SelectionStep extends OperationStep
{
    /**
     *
     */
    public SelectionStep()//PathFragment condition)
    {
        //this.condition = condition;
    }

    private ExpressionFragment condition;

    /** can be a BooleanLiteral,
     * a OCL-Expression that evaluates to a boolean
     * or an equation
     *
     * @return the OCL condition expression
     */
    public ExpressionFragment getCondition()
    {
        return condition;
    }

    public void setCondition(ExpressionFragment condition)
    {
        this.condition = condition;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "SelectionStep("+condition+")";
    }
}
