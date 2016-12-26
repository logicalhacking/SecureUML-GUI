package ch.ethz.infsec.secureumlgui.modelmapping.permissions;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.securemodelimpl.SecureModelFactory;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

/**
 * The value of a Permission (i.e., denied, granted, inherited, etc).
 *
 */
public class PermissionValue implements Comparable<PermissionValue>
{
    /**
     * not permitted
     */
    public static final int INT_DENIED = 0;
    /**
     * is permitted
     */
    public static final int INT_GRANTED = 0x1;
    /**
     * is permitted, if the corresponding policy is active
     */
    public static final int INT_EMERGENCY = 0x2;
    /**
     * inherited from a super role
     */
    public static final int INT_INHERITED_ROLE = 0x10;
    /**
     * inherited from a super policy
     */
    public static final int INT_INHERITED_POLICY = 0x100; //inherited
    /**
     * is inherited == INT_INHERITED_ROLE | INT_INHERITED_POLICY
     */
    public static final int INT_INHERITED = INT_INHERITED_ROLE | INT_INHERITED_POLICY;
    /**
     * implicit, as a super action is permitted
     */
    public static final int INT_IMPLICIT = 0x20;
    /**
     * implicit, as all sub actions are permitted
     */
    public static final int INT_COMPOSITE = 0x40;
    /**
     * to the permission a constrained is assigned
     */
    public static final int INT_CONSTRAINED = 0x80000000;


    private int flags;

    protected MultiContextLogger logger = MultiContextLogger.getDefault();

    protected PermissionWrapper permission;

    // may only be != null if INT_COMPOSITE
    private Set<ActionWrapper> permittedSubActions;
    /*
     * may only be != null if
     *  => permission is NOT an explicit permission
     *  => OR cannot be DIRECTLY derivated form a explicit permissions
     *  ATTENTION: may be null AND permission cannot be derivated form a explicit permissions:
     *  this is the case, if the permission is COMPOSITE => permittedSubActions must be != null
     */
    private LinkedHashMap<PermissionValue, Integer> permissionDerivation;

    private static Logger aLog = Logger.getLogger(PermissionValue.class);
    //counter for tmp permissions
    private static int tmp_perm = 0;


    @Override
    public String toString() {
        if (permission == null ) {
            return "NO_PERMISSION";
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(permission.getName());

        buffer.append(" Role: ");
        RoleWrapper role = permission.getRoleWrapper();
        if ( role == null ) {
            buffer.append("null");
        } else {
            buffer.append(role.getName());
        }

        buffer.append(" Action: ");
        ActionWrapper action = permission.getActionWrapper();
        if ( action  == null ) {
            buffer.append("null");
        } else {
            buffer.append(action.getName());
        }

        buffer.append(" flags: 0x");
        buffer.append(Integer.toHexString(flags));

        buffer.append(" subActions: ");
        if (permittedSubActions == null) {
            buffer.append("null");
        } else {
            buffer.append(permittedSubActions.size());
        }

        buffer.append(" derivationStack: ");
        if (permissionDerivation == null) {
            buffer.append("null");
        } else {
            buffer.append(permissionDerivation.size());
        }

        return buffer.toString();
    }

    /**
     * creates a new GANTED permission
     * @param permission
     */
    public PermissionValue(PermissionWrapper permission) {
        this.flags = INT_GRANTED;
        this.permission = permission;
        checkConstrained();
    }

    /**
     * creates a new permission
     * @param flags
     * @param permission
     */
    public PermissionValue(int flags, PermissionWrapper permission) {
        this.flags = flags;
        this.permission = permission;
        checkConstrained();
    }

    private PermissionValue() {

    }

    /**
     * create a new GRANTED Permission
     * @param permission
     * @return
     */
    public static PermissionValue createGranted(PermissionWrapper permission) {
        return new PermissionValue(permission);
    }
    /**
     * creates a INHERITED BY ROLE Permission
     * @param template
     * @return
     */
    public static PermissionValue createInheritedRole(PermissionValue template) {
        return createDerived(template, INT_INHERITED_ROLE);
    }
    /**
     * creates a INHERITED BY POLICY Permission
     * @param template
     * @return
     */
    public static PermissionValue createInheritedPolicy(PermissionValue template) {
        return createDerived(template, INT_INHERITED_POLICY);
    }
    /**
     * creates a IMPLICIT Permission
     * @param template
     * @return
     */
    public static PermissionValue createImplicite(PermissionValue template) {
        return createDerived(template, INT_IMPLICIT);
    }
    /**
     * creates a COMPOSITE Permission
     * @param resourcePermissions currently unsed, but could be needed to calcuclate a correct flags
     * @param action
     * @param role
     * @return
     */
    public static PermissionValue createComposite(ResourcePermissionsSet resourcePermissions, ActionWrapper action, RoleWrapper role) {
        //create a new SecureUML Permission and assign role and action
        PermissionWrapper newPermission = new PermissionWrapper(SecureModelFactory.getInstance().createPermission("tmp_perm_" + ++tmp_perm));
        newPermission.setAction(action.getModelElement());
        newPermission.setRoleWrapper(role);

        PermissionValue newPermVal = new PermissionValue();
        newPermVal.permittedSubActions = action.getSubActionWrappers();
        newPermVal.flags = INT_COMPOSITE;
        newPermVal.permission = newPermission;

//		//TODO collect the flags of the subactions! is not distinct...
//		for ( ActionWrapper subaction : newPermVal.permittedSubActions ) {
//			ActionPermissionSet actionPermissions = resourcePermissions.getPermissions(subaction);
//			for ( PermissionValue permValue : actionPermissions.getPermissions()  ) {
//				newPermVal.flags |= permValue.flags;
//			}
//		}
//		newPermVal.flags =  ~(~newPermVal.flags | INT_GRANTED);

        newPermVal.checkConstrained();

        return newPermVal;
    }


    private static PermissionValue createDerived(PermissionValue template, int flag) {
        int templ_flags = template.getFlags();
        if ( (templ_flags & INT_GRANTED) > 0 ) { //can be directly derived from a explicit permission
            return createByTemplate(template, ~(~templ_flags | INT_GRANTED) | flag);
        }
        else {
            PermissionValue newPermVal = new PermissionValue();

            newPermVal.permission = template.permission;
            newPermVal.flags = template.flags | flag;
            if (template.permissionDerivation != null ) {
                newPermVal.permissionDerivation = (LinkedHashMap<PermissionValue, Integer>) template.permissionDerivation.clone();
            } else {
                newPermVal.permissionDerivation = new LinkedHashMap<PermissionValue, Integer>();
            }
            newPermVal.permissionDerivation.put(template, new Integer(flag));

            return newPermVal;
        }
    }

    private static PermissionValue createByTemplate(PermissionValue template, int flag) {
        return new PermissionValue(template.flags | flag, template.getPermissionWrapper());
    }




//	public static PermissionValue createInheritedRole(PermissionValue template) {
//	int templ_flags = template.getFlags();
//	if ( (templ_flags & INT_GRANTED) > 0 ) { //can be directly derived from a explicit permission
//		return createByTemplate(template, ~(~templ_flags | INT_GRANTED) | INT_INHERITED_ROLE);
//	}
//	else { //
//		PermissionValue newPermVal = new PermissionValue();
//		newPermVal.flags = template.flags | INT_INHERITED_ROLE;
//		//newPermVal.permissionDerivation = new LinkedHashMap<PermissionValue>();
//		if (template.permissionDerivation != null ) {
//			newPermVal.permissionDerivation = (LinkedHashMap<PermissionValue, Integer>) template.permissionDerivation.clone();
////			for (PermissionValue permVal : template.permissionDerivation) {
////				newPermVal.permissionDerivation.add(permVal);
////			}
//		} else {
//			newPermVal.permissionDerivation = new LinkedHashMap<PermissionValue, Integer>();
//		}
//		newPermVal.permissionDerivation.put(template, new Integer(INT_INHERITED_ROLE));
//
//
//
//	}
//
//
//	return createByTemplate(template, INT_INHERITED_ROLE);
//}
//public static PermissionValue createInheritedPolicy(PermissionValue template) {
//return createByTemplate(template, INT_INHERITED_POLICY);
//}




    public int getFlags() {
        return flags;
    }

    @Deprecated
    public int getValue() {
        if ( (flags &  INT_IMPLICIT ) > 0) {
            return INT_IMPLICIT;
        } else if ( (flags & INT_COMPOSITE) > 0) {
            return INT_COMPOSITE;
        } else if ( (flags & INT_INHERITED_ROLE) > 0) {
            return INT_INHERITED_ROLE;
        } else if ( (flags & INT_GRANTED ) > 0) {
            return INT_GRANTED;
        }
        return flags;
    }

//	public void setFlags(int value)
//	{
//		this.flags = value;
//	}

//	public void addFlags(int flags) {
//		this.flags |= flags;
//	}

    /**
     * @return the suPermission
     */
    public PermissionWrapper getPermissionWrapper()
    {
        return permission;
    }

    /**
     * @param permissionWrapper the PermissionWrapper to set
     */
    public void setPermissionWrapper(PermissionWrapper permissionWrapper)
    {
        this.permission = permissionWrapper;
    }


    /**
     * checks if the encapsulated permission has a constrained
     */
    private void checkConstrained() {
        if ( (flags & INT_COMPOSITE) > 0 || permittedSubActions != null) {
            boolean anyActionContrained = false;
            for ( ActionWrapper action : permittedSubActions ) {
                boolean anyPermissionUnConstrained = false;
                for ( PermissionWrapper permission : action.getPermissionWrappers() ) {
                    if ( ! permission.isConstrained() ) {
                        anyPermissionUnConstrained = true;
                        break;
                    }
                }
                if ( ! anyPermissionUnConstrained ) {
                    anyActionContrained = true;
                    break;
                }
            }
            if ( anyActionContrained ) {
                flags |= INT_CONSTRAINED;
            }
        }


        if ( permission.getAuthorizationConstraint() != null ) {
            String constraint = permission.getAuthorizationConstraintWrapper().getConstraint();
            if(constraint != null && constraint.length() != 0)
            {
                flags |= INT_CONSTRAINED;
            }
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(PermissionValue o)
    {
        return new Integer(flags).compareTo(new Integer(o.getValue()));

    }

//	/* (non-Javadoc)
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj)
//	{
//		if (obj instanceof PermissionValue) {
//			PermissionValue pv = (PermissionValue) obj;
//
//			return (getValue() == pv.getValue()); //TODO realy? no matter of the permission?
//		}
//		else {
//			return super.equals(obj);
//		}
//	}

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof PermissionValue) {
            PermissionValue pv = (PermissionValue) obj;
            return (flags == pv.flags && permission.equals(pv.permission));
        }
        else {
            return super.equals(obj);
        }
    }





    public boolean isConstrained()
    {
        if ( (flags & PermissionValue.INT_CONSTRAINED) > 0  )
            return true;
        else {
            return false;
        }
    }

    public void setConstrained(boolean constrained)
    {
        if ( constrained ) {
            flags |= INT_CONSTRAINED;
        } else {
            flags = ~( (~flags) | INT_CONSTRAINED); //TODO there must be a better way
        }
    }

    public String getDescription() {
        StringBuffer buffer = new StringBuffer();

        String name;

        if ( (flags & INT_COMPOSITE) > 0 ) {
            buffer.append("COMPOSITE Permission, ");
        } else if ( permission != null && (name = permission.getName()) != null && name.length() > 0 ) {
            buffer.append(name);
            buffer.append(" Permission, ");
        }

        if ( (flags & INT_INHERITED_ROLE) > 0 ) {
            buffer.append("inherited from role: ");
            buffer.append(permission.getRoleWrapper().getName());
            buffer.append(", ");
        }

        if ( (flags & INT_INHERITED_POLICY) > 0 ) {
            buffer.append("inherited from policy: ");
            //buffer.append(permission.getPolicyWrapper().getName()); //TODO hel fix nullpointer
            buffer.append(", ");
        }

        if ( (flags & INT_IMPLICIT) > 0 ) {
            buffer.append("implicit from action: ");
            buffer.append(permission.getActionWrapper().getResourceWrapper().getResourcePath());
            buffer.append(".");
            buffer.append(permission.getActionWrapper().getName());
            buffer.append(", ");
        }

        if ( (flags & INT_COMPOSITE) > 0 ) {
            buffer.append("implicit from all permitted subactions");
        }

        if(isConstrained()) {
            buffer.append(" with Authorization Constraint");
        }

        return buffer.toString();
    }

    public Set<ActionWrapper> getPermittedSubActions() {

        return permittedSubActions;
    }

    public void setPermittedSubActions(Set<ActionWrapper> permittedSubActions) {
        if ( (flags & INT_COMPOSITE ) == 0 ) {
            aLog.warn("Setting permittedSubActions, but Permission is not permitted through permitted subactions!");
        }
        this.permittedSubActions = permittedSubActions;
    }






    @Deprecated
    public static PermissionValue create(PermissionValue template,
                                         PermissionWrapper permissionWrapper, boolean constrained) {
        PermissionValue pv = null;

        pv = new PermissionValue(
            template.getName(), template.getValue());

        pv.setPermissionWrapper(permissionWrapper);

        pv.setConstrained(constrained);

        return pv;
    }

    @Deprecated
    public static PermissionValue create(PermissionValue template,
                                         PermissionWrapper permissionWrapper)  {
        boolean constrained = false;

        if(permissionWrapper.getAuthorizationConstraint() != null)
        {
            String constraint = permissionWrapper.getAuthorizationConstraintWrapper().getConstraint();
            if(constraint != null && constraint.length() != 0)
            {
                constrained = true;
            }
        }
        return create(template, permissionWrapper, constrained);
    }

    @Deprecated
    public String getName()
    {
        if ( (flags &  INT_GRANTED ) > 0) {
            return "Explicit";
        } else if ( (flags & INT_INHERITED_ROLE) > 0) {
            return "Inherited";
        } else if ( (flags & INT_IMPLICIT) > 0) {
            return "Implicit";
        } else if ( (flags & INT_COMPOSITE ) > 0) {
            return "Composite";
        }
        return "TODO_UNDEFIEND";
    }

    @Deprecated
    public void setName(String name)
    {
        this.name = name;
    }

    @Deprecated
    private String name;

    @Deprecated
    private boolean constrained = false;


    @Deprecated
    protected PermissionValue(String name, int value) {
        this.name = name;
        this.flags = value;
    }

    @Deprecated
    public static final PermissionValue GRANTED =
        new PermissionValue("Explicit", INT_GRANTED);

    @Deprecated
    public static final PermissionValue DENIED =
        new PermissionValue("Denied", INT_DENIED);

    // inherited from a super-role
    @Deprecated
    public static final PermissionValue INHERITED =
        new PermissionValue("Inherited", INT_INHERITED_ROLE);

    // implicitly granted by a composite action
    @Deprecated
    public static final PermissionValue IMPLICIT =
        new PermissionValue("Implicit", INT_IMPLICIT);

    // implicitly there, because permissions for all subactions are there (but not the composite action itself)
    @Deprecated
    public static final PermissionValue COMPOSITE =
        new PermissionValue("Composite", INT_COMPOSITE);


}
