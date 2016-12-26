/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import java.security.Permission;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.infsec.secureumlgui.Util;


/**
 *
 */
public class RoleWrapper extends NamedModelElementWrapper
{
    /**
     *
     */
    public RoleWrapper(Object secureModelElement)
    {
        super(secureModelElement);
    }

    public Collection<RoleWrapper> getSuperrolesWrappers()
    {
        return createWrapper(getSuperroles());
    }

    public Collection<RoleWrapper> getSubrolesWrappers() {
        return createWrapper(getSubroles());
    }

    private Collection<RoleWrapper> createWrapper(Collection roles) {

        Collection<RoleWrapper> result = new LinkedList<RoleWrapper>();

        if(roles != null)
        {
            for (Iterator iter = roles.iterator(); iter.hasNext();)
            {
                Object role = (Object) iter.next();

                result.add(new RoleWrapper(role));

            }
            return result;
        }
        else
            return null;
    }

    public Collection getSuperroles()
    {
        Collection roles = (Collection) Util.getProperty(
                               getModelElement(), "superroles");

        return roles;
    }

    public Collection getSubroles() {
        return (Collection) Util.getProperty(getModelElement(), "subroles");
    }


    public Collection getPermission()
    {
        Collection permission = (Collection) Util.getProperty(
                                    getModelElement(), "permission");

        return permission;
    }


    public void addPermission(Object permission)
    {
        Collection permissions = (Collection) Util.getProperty(
                                     getModelElement(), "permission");

        permissions.add(permission);
    }

    public Collection<PermissionWrapper> getPermissionWrapper()
    {
        Collection permissions = getPermission();

        Collection<PermissionWrapper> permissionWrapper = new LinkedList<PermissionWrapper>();

        for (Iterator iter = permissions.iterator(); iter.hasNext();)
        {
            Object permission = (Object) iter.next();

            permissionWrapper.add(new PermissionWrapper(permission));
        }
        return permissionWrapper;
    }
}
