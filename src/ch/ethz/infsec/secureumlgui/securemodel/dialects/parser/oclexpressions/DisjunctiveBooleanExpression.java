/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 */
public class DisjunctiveBooleanExpression extends BooleanExpression
{
    private Collection<ExpressionFragment> terms
        = new LinkedList<ExpressionFragment>();

    public Collection<ExpressionFragment> getTerms()
    {
        return terms;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String s = "DisjunctiveBooleanExpression( ";

        for (Iterator iter = terms.iterator(); iter.hasNext();)
        {
            ExpressionFragment term = (ExpressionFragment) iter.next();

            s += term.toString();
        }

        s += " )";
        return s;
    }
}
