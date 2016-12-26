/**
 *
 */
package ch.ethz.infsec.secureumlgui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import ch.ethz.infsec.secureumlgui.securemodel.componentuml.Entity;
//import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Permission;
import ch.ethz.infsec.secureumlgui.wrapper.AuthorizationConstraintWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.NamedModelElementWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;
//import ch.ethz.infsec.secureumlgui.securemodelimpl.secureuml.PermissionImpl;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;

/**
 * represents the permission association class in a UML model.
 *
 * The main difference to a SecureUML permission therefore is that it
 * can have more than one action. (which are represented by permission
 * wrappers here).
 *
 *
 */
public class PermissionDummy
/*extends PermissionImpl*/
{
    private String name;
    private Object /*Entity*/anchor;
    private List/*<Permission>*/permissionAttributes = new ArrayList();
    private Object authorizationConstraint;
    private Object role;
    private Object policy;


    /**
     *
     */
    public PermissionDummy(String name) {
        //super(name);
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object /*Resource*/getAnchor() {
        return anchor;
    }

    public void setAnchor(Object anchor) {
        this.anchor = anchor;
    }

    public Object getPolicy() {
        return this.policy;
    }

    public void setPolicy(Object policy) {
        this.policy = policy;
    }

    public void setAnchorWrapper(NamedModelElementWrapper anchorWrapper) {
        this.anchor = anchorWrapper.getModelElement();
    }

    public NamedModelElementWrapper /*Resource*/getAnchorWrapper() {
        return new NamedModelElementWrapper(anchor);
    }

    public List/*<Permission>*/getPermissionAttributes() {
        return permissionAttributes;
    }

    public void addPermissionAttribute(Object permissionAttribute) {
        permissionAttributes.add(permissionAttribute);
    }

    public List<PermissionWrapper> getPermissionAttributeWrappers() {
        List<PermissionWrapper> permissionAttributeWrappers = new ArrayList<PermissionWrapper>();

        for (Iterator iter = permissionAttributes.iterator(); iter.hasNext();) {
            Object permissionAttribute = (Object) iter.next();

            permissionAttributeWrappers.add(new PermissionWrapper(
                                                permissionAttribute));
        }
        return permissionAttributeWrappers;
    }

    public void addPermissionWrapper(PermissionWrapper pw) {
        Object permissionAttribute = pw.getModelElement();

        permissionAttributes.add(permissionAttribute);
    }

    public Object getAuthorizationConstraint() {
        return authorizationConstraint;
    }

    public void setAuthorizationConstraint(Object authorizationConstraint) {
        this.authorizationConstraint = authorizationConstraint;
    }

    public AuthorizationConstraintWrapper getAuthorizationConstraintWrapper() {
        return new AuthorizationConstraintWrapper(authorizationConstraint);
    }

    public void setAuthorizationConstraintWrapper(
        AuthorizationConstraintWrapper authorizationConstraintWrapper) {
        this.authorizationConstraint = authorizationConstraintWrapper
                                       .getModelElement();
    }

    public Object getRole() {
        return role;
    }

    public void setRole(Object role) {
        this.role = role;
    }

    public RoleWrapper getRoleWrapper() {
        return new RoleWrapper(role);
    }

    public void setRoleWrapper(RoleWrapper roleWrapper) {
        this.role = roleWrapper.getModelElement();
    }
}
