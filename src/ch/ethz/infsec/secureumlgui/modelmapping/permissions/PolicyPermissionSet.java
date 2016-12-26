package ch.ethz.infsec.secureumlgui.modelmapping.permissions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.ethz.infsec.secureumlgui.modelmapping.permissions.HierarchicalPermissionsExplorer.CHANGES;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

public class PolicyPermissionSet {

    Collection<PermissionValue> permissions =
        new LinkedList<PermissionValue>();

    private static Logger aLog = Logger.getLogger(PolicyPermissionSet.class);

    private PolicyWrapper policy;


    private PolicyWrapper explicitPolicyWrapper;


    public PolicyPermissionSet(PolicyWrapper policy) {
        this.policy = policy;
    }

    public void addPermission(PermissionValue permissionValue, CHANGES changeReason)
    {
        aLog.debug("add permission: " + permissionValue.getPermissionWrapper().getActionWrapper().getName() + " " + permissionValue.getPermissionWrapper().getRoleWrapper().getName() + " " + changeReason);
        for (PermissionValue value : permissions ) {
            if ( value.equals(permissionValue)) {
                aLog.debug("Omitting redundant permission: already \nsaved: " + value + " \nnew:   " + permissionValue);
                return; //alredy here...
            }
        }
        if ( changeReason == CHANGES.IMPLICIT_SUPER //if this is a new permission
                && isPermitted()
                && (permissionValue.getFlags() & PermissionValue.INT_GRANTED) == 0 ) {
            aLog.debug("Omitting an implicit super permission: action already permitted and implicit super permission is not explicit!");
            return;
        }
        permissions.add(permissionValue);
    }

    public void addExplicitPermission(PermissionValue permissionValue) {
        addPermission(permissionValue);
    }

    private void addPermission(PermissionValue permissionValue) {
        permissions.add(permissionValue);
    }

    public void addPermission(PermissionValue permissionValue, PermissionSet permissions_next, CHANGES changeReason, ActionWrapper action, RoleWrapper role) {

        for (PermissionValue value : permissions ) {
            if ( value.equals(permissionValue)) {
                aLog.debug("Omitting redundant permission: already \nsaved: " + value + " \nnew:   " + permissionValue);
                return; //alredy here...
            }
        }

        if ( changeReason == CHANGES.IMPLICIT_SUPER //if this is a new permission
                && isPermitted()
                && (permissionValue.getFlags() & PermissionValue.INT_GRANTED) == 0 ) {
            aLog.debug("Omitting an implicit super permission: action already permitted and implicit super permission is not explicit!");
            return;
        }

        if ( aLog.isDebugEnabled() ) {
            aLog.debug("        addPermission to action: " + permissionValue + " changeReason: " + changeReason + " isPermitted: " + isPermitted());
        }

        PermissionWrapper permission = permissionValue.getPermissionWrapper();

        switch ( changeReason ) {
        case INHERITED:
            //if change was caused by inerhitance, the implicit may change, i.e.,
            Set<ActionWrapper> relatedActions = HierarchicalPermissionsExplorer.getSubAndSuperActionWrappersDeep(permission.getActionWrapper());
            //RoleWrapper inh_role = permission.getRoleWrapper();
            for ( ActionWrapper inh_action : relatedActions) {
                aLog.debug("          Adding permission to next; action = " + inh_action.getName() + " role = " + role.getName());
                //permissions_next.getResourcePermissionsSet(inh_role).getPermissions(inh_action).addPermission(permissionValue);
                //TODO is CHANGES.INHERITED correct? not sure for now...
                permissions_next.getResourcePermissionsSet(role).getPermissions(inh_action).addPermission(policy, permissionValue, changeReason);
            }

            break;
        case IMPLICIT_SUPER://TODO for IMPLICIT_SUB we could "reroute" back and add a listener to do same stuff as inherited?
//			relatedActions = HierarchicalPermissionsExplorer.getSubActionWrappersDeep(permission.getActionWrapper());
//			inh_role = permission.getRoleWrapper();
//			for ( ActionWrapper inh_action : relatedActions) {
//			aLog.debug("          Adding permission to next; action = " + inh_action.getName());
//			permissions_next.getResourcePermissionsSet(inh_role).getPermissions(inh_action).addPermission(permissionValue);
//			}

//			//NO BREAK! need to add "indicators" for sub roles too!
        case IMPLICIT_SUB:
            //if change was caused by an implicit, the inheritance may change, i.e.,
            Set<RoleWrapper> superRoles = HierarchicalPermissionsExplorer.getSubRoleWrapperDeep(permission.getRoleWrapper());
            //ActionWrapper imp_action = permission.getActionWrapper();
            for ( RoleWrapper imp_role : superRoles ) {
                aLog.debug("          Adding permission to next; role = " + imp_role.getName() + " action = " + action.getName());
                //permissions_next.getResourcePermissionsSet(imp_role).getPermissions(imp_action).addPermission(permissionValue);
                //TODO as above - is changeReson correct? not sure
                permissions_next.getResourcePermissionsSet(imp_role).getPermissions(action).addPermission(policy, permissionValue, changeReason);
            }
            break;
        }

        //finally, add permission
        permissions.add(permissionValue);
    }




//	/** returns GRANTED if the permission was explicitly defined,
//	 * DENIED in all other cases.
//	 */
//	public PermissionValue getExplicitPermission()
//	{
//		PermissionValue result = PermissionValue.DENIED;
//
//		for (PermissionValue pv : permissions)
//		{
//			//TODO ordering????? if ordering is not relevant, function can return in case of GRANTED
//			if(pv.getValue() == PermissionValue.GRANTED.getValue()
//					|| pv.getValue() == PermissionValue.DENIED.getValue()) {
//				result = pv;
//			}
//		}
//		return result;
//	}

    public boolean isExplicitPermitted() {
        for ( PermissionValue permission : permissions) {
            if ( permission.getFlags() == PermissionValue.INT_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public PermissionWrapper getExplicitPermittedPermission() {
        for ( PermissionValue permission : permissions) {
            if ( permission.getFlags() == PermissionValue.INT_GRANTED) {
                return permission.getPermissionWrapper();
            }
        }
        return null;
    }


    public boolean isPermitted() {
        //TODO only on default permissions
        if (permissions.size() > 0 )
            return true;
        else
            return false;

    }


    public Collection<PermissionValue> getPermissions()
    {
        //TODO only default permissions
        return permissions;
    }




    public PolicyWrapper getExplicitPolicyWapper() {
        return explicitPolicyWrapper;
    }

    public void setExplicitPolicyWrapper(PolicyWrapper eplicitPolicyWrapper) {
        this.explicitPolicyWrapper = eplicitPolicyWrapper;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String resultString = "**";

        for (Iterator iter = getPermissions().iterator(); iter.hasNext();)
        {
            PermissionValue pv = (PermissionValue) iter.next();

            if(pv.equals(pv.GRANTED))
            {
                if(pv.isConstrained())
                    resultString += "(?)";
            }
        }

        for (Iterator iter = getPermissions().iterator(); iter.hasNext();)
        {
            PermissionValue pv = (PermissionValue) iter.next();

            if(!pv.equals(pv.GRANTED))
            {
                resultString += pv.getName().charAt(0);
                if(pv.isConstrained())
                    resultString += "(?)";
            }
        }
        return resultString;
    }

}
