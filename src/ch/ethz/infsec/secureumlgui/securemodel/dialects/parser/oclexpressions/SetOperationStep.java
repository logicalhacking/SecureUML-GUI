/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 * set Operation between the current result set and
 * the set resulting from the evaluation of 'expression'
 *
 */
public class SetOperationStep extends OperationStep
{
    public SetOperationStep(String setOperation, OclExpression expression)
    {
        this.operation = setOperation;

        this.expression = expression;
    }

    private String operation;

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    private OclExpression expression;

    public OclExpression getExpression()
    {
        return expression;
    }

    public void setExpression(OclExpression expression)
    {
        this.expression = expression;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "SetOperationStep( " + operation + " "
               + expression +" )";
    }
}
