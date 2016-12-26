/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.ModelElement;

import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ActionPermissionSet;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.HierarchicalPermissionsExplorer;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.HierarchicalPolicyExplorer;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionSet;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionValue;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ResourcePermissionsSet;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.ResourceWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

/**
 * Swing table model for the permission table displayed in the
 * SecureUML properties tab for resources.
 * Columns are roles, rows are actions on the resource.
 *
 */
public class GenericResourcePermissionsTableModel extends AbstractPermissionsTableModel
{
    private static final long serialVersionUID = 1L;

    ResourceWrapper resourceWrapper;
    PermissionSet permissions = new PermissionSet();
    Collection<ActionWrapper> actionWrappers;
    private static Logger aLog = Logger.getLogger(GenericResourcePermissionsTableModel.class);


    public GenericResourcePermissionsTableModel(
        ResourceWrapper resourceWrapper)
    {
        super(resourceWrapper.getModelElement());

        setResource(resourceWrapper);
    }


    /**
     * @param resourceWrapper
     */
    protected void setResource(ResourceWrapper resourceWrapper)
    {
        super.setResource(resourceWrapper);

        this.resourceWrapper = resourceWrapper;

        initActionPermissionSets();

        // init
//		TODO realy needed????
//		Object suModelElement = resourceWrapper.getModelElement();
//		ModelElement umlModelElement = (ModelElement) ModelMap.getDefault().getUmlElement(suModelElement);
//		ResourceType rt = GenericDialectHelper.getInstance().getResourceType(umlModelElement);
//		DialectMetaModelInfo mmInfo = GenericDialectHelper.getInstance().getDialectMetaModelInfo();

        actionWrappers = resourceWrapper.getActionWrapper();

        initExplicitPermissions(resourceWrapper);

        HierarchicalPermissionsExplorer permissionsExplorer = new HierarchicalPermissionsExplorer();

        permissionsExplorer.collectNonExplicitPermissions(resourceWrapper, permissions);
    }


    /**
     * creates the objects that will be filled with explicit and interhited/implicit permissions
     *
     */
    protected void initActionPermissionSets() {
        for (RoleWrapper role : roleWrappers) {
            ResourcePermissionsSet resourcePermissions = permissions.getResourcePermissionsSet(role);

            for (ActionWrapper action : resourceWrapper.getActionWrapper()) {
                ActionPermissionSet actionPermissions = resourcePermissions.getPermissions(action);

                actionPermissions.setExplicitRoleWrapper(role);
                actionPermissions.setExplicitActionWrapper(action);


            }
        }
    }


    private void initExplicitPermissions(ResourceWrapper resource) {

        PolicyWrapper defaultPolicy =  HierarchicalPolicyExplorer.getInstance().getDefaultPolicyWrapper();

        for (ActionWrapper action : resource.getActionWrapper()) {
            for (PermissionWrapper permission : action.getPermissionWrappers()) {
                RoleWrapper role = permission.getRoleWrapper();

                if(role != null) {
                    Set<PolicyWrapper> policies = permission.getPolicyWrappers();

                    PolicyWrapper policy = null;

                    if (policies != null || policies.size() > 0) {
                        policy = policies.iterator().next();

                        if (policies.size() > 1 ) {
                            aLog.error("ignoring all policies except first one.. TODO");
                        }

                    }

                    aLog.debug("policy: " + (policies == null ? "NULL" : policy.getModelElement()));

//					permissions.getResourcePermissionsSet(role).addPermission(
//							action, PermissionValue.create(PermissionValue.GRANTED, permission));

                    if ( policy == null ) {
                        policy = defaultPolicy;
                    }

                    //policy = defaultPolicy;

                    aLog.debug("G: add explicit permission: " + role.getName() + " on "  + action.getName() + " on policy " + (policy == null ? "NULL" : policy.getName()));

                    permissions.getResourcePermissionsSet(role).addPermission(
                        action, PermissionValue.createGranted(permission), policy);


                    permissions.getResourcePermissionsSet(role).getPermissions(action).
                    addExplicitPermission(policy, PermissionValue.createGranted(permission));



//					if ( defaultPolicy == null ) {
//						permissions.getResourcePermissionsSet(role).getPermissions(action).
//								addDefaultPermission(PermissionValue.createGranted(permission));
//					} else {
////						permissions.getResourcePermissionsSet(role).getActionPermissionSet(action).
////								addPermission(policy, PermissionValue.createGranted(permission));
//					}

                    ResourcePermissionsSet rps = permissions.getResourcePermissionsSet(
                                                     new RoleWrapper(role.getModelElement()));

                    //hel ActionPermissionSet aps = rps.getPermissions(actionName);
                    ActionPermissionSet aps = rps.getPermissions(action.getName());

                    aps.setExplicitRoleWrapper(role);
//					logger.info("Test: fetch the added Permission: " + aps.getFlatPermission());
                }
                else {
                    aLog.warn("Permission without role");
                }
            }
        }
    }




    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return actionWrappers.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col)
    {
        ActionWrapper aw = (ActionWrapper)  actionWrappers.toArray()[row];

        if(col == 0) {
            String actionName = aw.getName();
            return aw;
        }
        else  { // other columns - i.e. return ROLEs and the assigned permissions
            RoleWrapper roleWrapper = roleWrappers.get(col-1);

            ResourcePermissionsSet resourcePermissions =
                permissions.getResourcePermissionsSet(roleWrapper);

            ActionPermissionSet actionPermissions =
                resourcePermissions.getPermissions(aw.getName());

            return actionPermissions;
        }
    }


    /* (non-Javadoc)
     * @see ch.ethz.infsec.secureumlgui.gui.AbstractPermissionsTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int col)	{
        if(col == 0)
            return false;
        else
            return true;
    }

    /* (non-Javadoc)
     * @see ch.ethz.infsec.secureumlgui.gui.AbstractPermissionsTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if(columnIndex == 0)
            return ActionWrapper.class;
        else
            return ActionPermissionSet.class;
    }
}
