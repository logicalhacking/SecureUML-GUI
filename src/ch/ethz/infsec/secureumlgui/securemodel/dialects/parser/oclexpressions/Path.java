/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 */
public class Path extends PathFragment
{
    /**
     *
     */
    public Path()
    {

    }

    private LinkedList<PathStep> steps = new LinkedList<PathStep>();

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

}
