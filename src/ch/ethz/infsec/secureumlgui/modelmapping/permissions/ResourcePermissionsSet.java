package ch.ethz.infsec.secureumlgui.modelmapping.permissions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;

/**
 * A mapping from actions to {@link ActionPermissionSet}s
 *
 *
 */
public class ResourcePermissionsSet {
    Map<Object, ActionPermissionSet> resourcePermissions = new LinkedHashMap<Object, ActionPermissionSet>();

    MultiContextLogger logger = MultiContextLogger.getDefault();

    public void addPermission(ActionWrapper actionWrapper,
                              PermissionValue permissionValue, PolicyWrapper policy) {
        ActionPermissionSet actionPermissions = getPermissions(actionWrapper);

        if (actionPermissions == null) {
            actionPermissions = new ActionPermissionSet();
            resourcePermissions.put(actionWrapper.getModelElement(),
                                    actionPermissions);
        }
        actionPermissions.addPermission(policy, permissionValue, HierarchicalPermissionsExplorer.CHANGES.EXPLICIT);
    }

    public ActionPermissionSet getPermissions(Object action) {
        ActionPermissionSet result = resourcePermissions.get(action);

        if (result == null) {
            result = new ActionPermissionSet();
            resourcePermissions.put(action, result);

            result.setExplicitActionWrapper(new ActionWrapper(action));
        }

        return result;
    }

    public ActionPermissionSet getPermissions(ActionWrapper actionWrapper) {
        ActionPermissionSet result = resourcePermissions.get(actionWrapper
                                     .getModelElement());

        if (result == null) {
            result = new ActionPermissionSet();
            resourcePermissions.put(actionWrapper.getModelElement(), result);
        }

        return result;
    }

    public Collection<Object> getActions() {
        return resourcePermissions.keySet();
    }

    public ActionPermissionSet getPermissions(String actionShortname) {
        for (Iterator iter = resourcePermissions.keySet().iterator(); iter
                .hasNext();) {
            ActionWrapper actionWrapper = ActionWrapper
                                          .createActionWrapper(iter.next());
            if (actionWrapper == null)
                logger.error("actionWrapper == null");
            if (actionShortname != null
                    && actionShortname.equals(actionWrapper.getName())) {
                return getPermissions(actionWrapper);
            }
        }

        return new ActionPermissionSet();
    }

}
