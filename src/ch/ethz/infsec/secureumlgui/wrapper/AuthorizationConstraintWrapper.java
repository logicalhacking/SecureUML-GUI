/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import ch.ethz.infsec.secureumlgui.Util;


/**
 *
 */
public class AuthorizationConstraintWrapper extends NamedModelElementWrapper
{
    /**
     *
     */
    public AuthorizationConstraintWrapper(Object secureModelElementWrapper)
    {
        super(secureModelElementWrapper);
    }

    public String getConstraint()
    {
        Object constraint = Util.getProperty(getModelElement(), "constraint");

        return constraint.toString();
    }

    public void setConstraint(String constraintString)
    {
        Util.setProperty(getModelElement(), "constraint", constraintString);
    }

}
