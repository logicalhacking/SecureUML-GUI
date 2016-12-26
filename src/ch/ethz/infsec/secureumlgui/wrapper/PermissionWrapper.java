/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;

/**
 *
 */
public class PermissionWrapper extends NamedModelElementWrapper
{

    private static Logger aLog = Logger.getLogger(PermissionWrapper.class);
    /**
     *
     */
    public PermissionWrapper(Object secureModelElementWrapper)
    {
        super(secureModelElementWrapper);
    }

    public Object getAction()
    {
        Object me = getModelElement();
        if (me == null) logger.error("PermissionWrapper.getAction: me = null");

        Object action = Util.getProperty(me, "Action");

        return /*(Collection)*/ action;
    }

    public void setAction(Object action)
    {
        logger.info("setAction("+action +")");
        Util.setProperty(getModelElement(), "Action", action);
    }

    public /*Collection<*/ActionWrapper getActionWrapper()
    {
        Object action = getAction();
        if(action == null) {
            logger.error("PermissionWrapper.getActionWrapper: action = null, this="+this);
            //return null;
        }
        logger.info("getAction() = "+action +" in getActionWrapper()");
        return ActionWrapper.createActionWrapper(action);

//    Collection<ActionWrapper> actionWrappers = null;
//
//    if(result instanceof Collection)
//    {
//      Collection<Object> actions = (Collection) result;
//
//      actionWrappers =
//        new LinkedList<ActionWrapper>();
//
//      for (Iterator iter = actions.iterator(); iter.hasNext();)
//      {
//        Object action = (Object) iter.next();
//
//        actionWrappers.add(ActionWrapper.createActionWrapper(action));
//      }
//
//    }
//    return actionWrappers;
    }

    public Object getRole()
    {
        Object roleObject = Util.getProperty(modelElement, "role");

        return roleObject;
    }

    public void setRole(Object role)
    {
        Util.setProperty(getModelElement(), "role", role);
    }

    public RoleWrapper getRoleWrapper()
    {
        Object roleObject = getRole();

        //if (roleObject instanceof Object)
        if(roleObject != null)
        {
            Object role = (Object) roleObject;

            return new RoleWrapper(role);

        }
        return null;
    }

    public void setRoleWrapper(RoleWrapper roleWrapper)
    {
        Object roleObject = null;
        if(roleWrapper != null)
            roleObject = roleWrapper.getModelElement();

        setRole(roleObject);
    }

    public Object getPolicies() {
        Object policyObject = Util.getProperty(getModelElement(), "policy");

//	  if ( policyObject == null ) {
//		  policyObject = GenericDialectModelMapper.getInstance().getDefaultPolicy();
//		  Util.setProperty(modelElement, "policy", policyObject);
//	  }
        return policyObject;
    }

    public void setPolicy(Object policy) {
//	  if (aLog.isDebugEnabled() && Util.getProperty(Util.getProperty(modelElement, "policy"), "name").equals(SecureUmlConstants.DEFAULT_POLICY_NAME)) {
//		  aLog.warn("A default policy is set to a permission!");
//	  }
        Util.setProperty(getModelElement(), "policy", policy);
    }



//  public Set<ActionWrapper> getSuperActionWrappers()
//  {
//    Collection actions = getSuperActions();
//
//    Set<ActionWrapper> actionWrappers = new LinkedHashSet<ActionWrapper>();
//
//    for (Iterator iter = actions.iterator(); iter.hasNext();)
//    {
//      Object a = (Object) iter.next();
//
//      actionWrappers.add(new ActionWrapper(a));
//    }
//
//    return actionWrappers;
//  }



    public Set<PolicyWrapper> getPolicyWrappers() {

        Collection policies = (Collection) getPolicies();

        if ( policies == null ) {
            return null;
        } else {
            Set<PolicyWrapper> policyWrappers = new HashSet<PolicyWrapper>();

            for ( Object obj : policies ) {
                policyWrappers.add(new PolicyWrapper(obj));
            }
            return policyWrappers;
        }













//		  if ( policy instanceof org.netbeans.mdr.handlers.AEIndexSetWrapper) {
//	  //if ( policy == null ) {
//		  aLog.warn("policy is AEIndexSetWrapper: " + policy);
//		  return null;
//	  } else {
//		  return new PolicyWrapper(getPolicy());
//	  }
    }

    public void setPolicyWrapper(PolicyWrapper policyWrapper) {
        if( policyWrapper != null ) {
            setPolicy(policyWrapper.getModelElement());
        } else {
            setPolicy(GenericDialectModelMapper.getInstance().getDefaultPolicy());
        }
    }


    public Object getAuthorizationConstraint()
    {
        Object authorizationConstraint = Util.getProperty(getModelElement(), "authorizationConstraint");

        return authorizationConstraint;
    }

    public void setAuthorizationConstraint(Object authorizationConstraint)
    {
        Util.setProperty(getModelElement(), "authorizationConstraint", authorizationConstraint);
    }


    public AuthorizationConstraintWrapper getAuthorizationConstraintWrapper()
    {
        Object authorizationConstraint = getAuthorizationConstraint();

        return new AuthorizationConstraintWrapper(authorizationConstraint);
    }

    public void setAuthorizationConstraintWrapper(
        AuthorizationConstraintWrapper authorizationConstraintWrapper)
    {
        setAuthorizationConstraint(
            authorizationConstraintWrapper.getModelElement());

    }

    public boolean isConstrained() {
        Object oConstr = getAuthorizationConstraint();
        if (oConstr == null ) {
            return false;
        }
        AuthorizationConstraintWrapper wConstr = new AuthorizationConstraintWrapper(oConstr);
        if ( wConstr.getConstraint() != null && wConstr.getConstraint().length() == 0 ) {
            return false;
        } else {
            return true;
        }
    }



}
