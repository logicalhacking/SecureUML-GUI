package ch.ethz.infsec.secureumlgui.wrapper;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import ch.ethz.infsec.secureumlgui.Util;

public class PolicyWrapper extends NamedModelElementWrapper {

    private static Logger aLog = Logger.getLogger(PolicyWrapper.class);

    public PolicyWrapper(Object modelElement) {
        super(modelElement);
    }

    //public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.Role>*/ getSuperroles();
    public Collection getRefinedBy() {
        Collection policies = (Collection) Util.getProperty(
                                  getModelElement(), "refinedBy");

        return policies;
    }

    public Collection getRefines() {
        Collection policies = (Collection) Util.getProperty(
                                  getModelElement(), "refines");

        return policies;
    }

    public Collection<PolicyWrapper> getRefinedByWrappers()
    {
        Collection<PolicyWrapper> policies = createWrapper(getRefinedBy());

        if (aLog.isDebugEnabled()) {

            StringBuffer buff = new StringBuffer("policy " + this.getName() + " refined by ");

            for (PolicyWrapper policy : policies ) {
                buff.append(policy.getName());
                buff.append(":");
            }

            aLog.debug(buff.toString());

        }

        return policies;

    }

    public Collection<PolicyWrapper> getRefinesWrappers() {
        return createWrapper(getRefines());
    }




    private Collection<PolicyWrapper> createWrapper(Collection policies) {

        Collection<PolicyWrapper> result = new LinkedList<PolicyWrapper>();

        if(policies != null)
        {
            for ( Object policy : policies ) {
                result.add(new PolicyWrapper(policy));
            }
            return result;
        }
        else
            return null;
    }

}
