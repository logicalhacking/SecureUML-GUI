/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

/**
 *
 */
public abstract class BooleanExpression extends ExpressionFragment
{
    private boolean negated = false;

    /** is the expression of form (not (...)
     *  - Default is 'false'
     * @return true if the expression is negated.
     */

    public boolean isNegated()
    {
        return negated;
    }

    public void setNegated(boolean negated)
    {
        this.negated = negated;
    }
}
