/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 *
 */
public class OclExpression extends ExpressionFragment
{
    /**
     *
     */
    public OclExpression()
    {

    }

    private ArrayList<PathStep> steps = new ArrayList<PathStep>();

    public Collection<PathStep> getSteps()
    {
        return steps;
    }

//  public void setSteps(Collection<PathStep> steps)
//  {
//    this.steps = steps;
//  }

    public void appendStep(PathStep step)
    {
        steps.add(step);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String resultString = "Path(";

        for (Iterator iter = steps.iterator(); iter.hasNext();)
        {
            PathStep pathStep = (PathStep) iter.next();

            resultString += "  " + pathStep;
        }

        resultString += "  )";

        return resultString;
    }

}
