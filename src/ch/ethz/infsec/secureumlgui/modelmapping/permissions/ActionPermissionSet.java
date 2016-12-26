/**
 *
 */
package ch.ethz.infsec.secureumlgui.modelmapping.permissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.ethz.infsec.secureumlgui.modelmapping.permissions.HierarchicalPermissionsExplorer.CHANGES;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;


/**
 * the set of permissions for role {@link #explicitRoleWrapper} on
 * action {@link #explicitActionWrapper}.
 *
 */
public class ActionPermissionSet
{
    Map<Object, PolicyPermissionSet> permissions = new HashMap<Object, PolicyPermissionSet>();

    private PolicyPermissionSet defaultPermissions;


    private static Logger aLog = Logger.getLogger(ActionPermissionSet.class);
    private boolean inserted_default = false; //Very, very bad hack..


    public ActionPermissionSet() {

        PolicyWrapper defaultPolicy = HierarchicalPolicyExplorer.getInstance().getDefaultPolicyWrapper();

        defaultPermissions = new PolicyPermissionSet(defaultPolicy);

        if ( defaultPolicy != null ) {
            permissions.put(defaultPolicy.getModelElement(), defaultPermissions);
            inserted_default = true;
        }
    }


    public void addExplicitDefaultPermission(PermissionValue permissionValue) {
        defaultPermissions.addExplicitPermission(permissionValue);
    }

    public void addExplicitPermission(PolicyWrapper policy, PermissionValue permissionValue) {
        getPolicyPermissionSet(policy).addExplicitPermission(permissionValue);
    }

    public void addDefaultPermission(PermissionValue permissionValue, CHANGES changeReason) {

        defaultPermissions.addPermission(permissionValue, changeReason);
    }


    public void addPermission(PolicyWrapper policy, PermissionValue permissionValue, CHANGES changeReason) {

        //aLog.debug("addPermission to policy " +( policy == null ? "NULL" : policy.getName()+  "_" + policy.getModelElement())  + "(" + permissions.size() + ")" );

        getPolicyPermissionSet(policy).addPermission(permissionValue, changeReason);
    }


    public PolicyPermissionSet getDefaultPolicyPermissionSet() {
        return defaultPermissions;
    }

    public PolicyPermissionSet getPolicyPermissionSet(PolicyWrapper policy) {

        if ( policy == null ) {
            return getDefaultPolicyPermissionSet();
        }

        if ( ! inserted_default ) {
            PolicyWrapper defaultPolicy = HierarchicalPolicyExplorer.getInstance().getDefaultPolicyWrapper();
            if ( defaultPolicy != null ) {
                permissions.put(defaultPolicy.getModelElement(), defaultPermissions);
                inserted_default = true;
            }
        }


        if ( permissions.containsKey(policy.getModelElement())) {
            return permissions.get(policy.getModelElement());
        } else {
            PolicyPermissionSet policyPermissionSet = new PolicyPermissionSet(policy);
            permissions.put(policy.getModelElement(), policyPermissionSet);
            return policyPermissionSet;
        }
    }


    public Collection<PermissionValue> getPermissions(PolicyWrapper policy) {

        //aLog.debug("getPermissions of policy " +( policy == null ? "NULL" : policy.getName()+  "_" + policy.getModelElement()) + "(" + permissions.size() + ")" );


        return getPolicyPermissionSet(policy).getPermissions();
    }



    private RoleWrapper explicitRoleWrapper;
    private ActionWrapper explicitActionWrapper;


    public RoleWrapper getExplicitRoleWrapper()
    {
        return explicitRoleWrapper;
    }

    public void setExplicitRoleWrapper(RoleWrapper explicitRoleWrapper)
    {
        this.explicitRoleWrapper = explicitRoleWrapper;
    }



    public ActionWrapper getExplicitActionWrapper()
    {
        return explicitActionWrapper;
    }

    public void setExplicitActionWrapper(ActionWrapper explicitActionWrapper)
    {
        this.explicitActionWrapper = explicitActionWrapper;
    }

    public boolean isExplicitPermitted(PolicyWrapper policy) {
        return getPolicyPermissionSet(policy).isExplicitPermitted();
    }

    public PermissionWrapper getExplicitPermittedPermission(PolicyWrapper policy) {
        return getPolicyPermissionSet(policy).getExplicitPermittedPermission();
    }


//  public boolean isPermitted() {
//	  for (PermissionValue permission : permissions ) {
//		  if ( ! permission.isConstrained()) {
//			  return true;
//		  }
//	  }
//	  return false;
//  }
//
//  public boolean isConstrainedPermitted() {
//	  if (permissions.size() > 0 )
//		  return true;
//	  else
//		  return false;
//
//  }


}
