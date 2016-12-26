/**
 *
 */
package ch.ethz.infsec.secureumlgui.modelmapping.permissions;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

/**
 * A mapping from Roles to {@link ResourcePermissionsSet}s. In the
 * end, this gives a mapping from roles and actions to the list of
 * permissions the role has for the action.
 *
 * <pre>
 * PermissionSet
 *
 *    ||
 *
 * Role -> {@link ResourcePermissionsSet}
 *
 *                 ||
 *
 *          Action -> {@link ActionPermissionSet}
 *
 *                           ||
 *
 *                    List&lt;{@link PermissionValue}&gt;
 *                    {@link RoleWrapper}
 *                    {@link ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper}
 * </pre>
 *
 */
public class PermissionSet
{
    Map<Object, ResourcePermissionsSet> permissions =
        new LinkedHashMap<Object, ResourcePermissionsSet>();


    /**
     * returns the resource permission set associated to the roleWrapper's UML model element.
     * creates a new, empty one, if necessary.
     */
    public ResourcePermissionsSet getResourcePermissionsSet(RoleWrapper roleWrapper)
    {
        ResourcePermissionsSet result = permissions.get(roleWrapper.getModelElement());
        if(result == null)
        {
            result = new ResourcePermissionsSet();
            permissions.put(roleWrapper.getModelElement(), result);
        }


        return result;
    }

    /**
     * creates a (copy) of the set of all role wrappers.
     */
    public Set<RoleWrapper> getAllRoleWrappers()
    {
        Set<RoleWrapper> roleWrappers =
            new LinkedHashSet<RoleWrapper>();

        for (Iterator iter = permissions.keySet().iterator(); iter.hasNext();)
        {
            Object role = (Object) iter.next();

            roleWrappers.add(new RoleWrapper(role));
        }

        return roleWrappers;
    }
}
