/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.ModelElement;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ActionPermissionSet;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.HierarchicalPermissionsExplorer;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionSet;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionValue;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ResourcePermissionsSet;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

/**
 * Swing table model for the permission table displayed in the
 * SecureUML properties tab for roles.  Each row represents a
 * permission for a specific resource-action pair. The first column
 * describes the kind of permission (explicit, implicit, inherited,
 * etc), the second and third column are resource and action, resp.
 *
 *
 */
public class RolePermissionsTableModel extends AbstractTableModel
{
    /**
     *
     */
    public RolePermissionsTableModel(RoleWrapper roleWrapper)
    {
        super();

        setCurrentRole(roleWrapper);

    }

    private static Logger aLog = Logger.getLogger(RolePermissionsTableModel.class);

    RoleWrapper currentRoleWrapper;

    //ArrayList<ActionWrapper> actionWrappers = new ArrayList<ActionWrapper>();

    ArrayList<PermissionValue> permissionValues =
        new ArrayList<PermissionValue>();

    MultiContextLogger logger = new MultiContextLogger(MultiContextLogger.GUI);


    public void setCurrentRole(RoleWrapper roleWrapper)
    {

        String loggerString = "RolePermissionsTableModel.setCurrentRole with " ;
        if(roleWrapper.getPermissionWrapper() == null)
            loggerString += "0";
        else
            loggerString += roleWrapper.getPermissionWrapper().size();

        loggerString += " Permissions";

        //logger.info(loggerString);

        //actionWrappers.clear();
        permissionValues.clear();

        currentRoleWrapper = roleWrapper;
        try
        {
            HierarchicalPermissionsExplorer hps =
                new HierarchicalPermissionsExplorer();

            for (Iterator iter = currentRoleWrapper.getPermissionWrapper().iterator(); iter.hasNext();)
            {
                PermissionWrapper permission = (PermissionWrapper) iter.next();

                //actionWrappers.add(permission.getActionWrapper());

                PermissionValue pv =
                    PermissionValue.create(
                        PermissionValue.GRANTED,
                        permission);

                permissionValues.add(pv);

                //logger.info(logger.TARGET_EVENTS, "RolePermissionsTable: Added" +  actions.size() + " Action(s)");
//                for (Iterator iterator = permission.getAction().iterator(); iterator.hasNext();)
//                {
//                    Action action = (Action) iterator.next();
//
//                    actions.add(action);
////                    txPermissions.append(action.getResource()
////                            + ":  " + action.getName()
////                            + "\n");
//                }

                PermissionSet permissions = hps.getExplicitPermissions(permission.getActionWrapper().getResourceWrapper());

                hps.collectNonExplicitPermissions(permission.getActionWrapper().getResourceWrapper(), permissions);

                ResourcePermissionsSet rps =
                    permissions.getResourcePermissionsSet(roleWrapper);

                // collect implicit permissions
                for (Iterator iterator = rps.getActions().iterator(); iterator
                        .hasNext();)
                {
                    Object action = iterator.next();

                    ActionPermissionSet aps =
                        rps.getPermissions(action);

                    aLog.warn("setCurrentRole: which policy? using default policy");

                    for (Iterator it = aps.getDefaultPolicyPermissionSet().getPermissions().iterator(); it
                            .hasNext();)
                    {
                        PermissionValue permissionValue =
                            (PermissionValue) it.next();

                        if(!permissionValues.contains(permissionValue)
                                && !(permissionValue.getValue() == permissionValue.INHERITED.getValue()))
                            permissionValues.add(permissionValue);

                        //permissionValue.getPermissionWrapper().setAction(
                        //aps.getExplicitActionWrapper().getModelElement());
                        //action);
                        // aps.getExplicitActionWrapper().getModelElement());
                    }
                }



//                ActionPermissionSet aps = new ActionPermissionSet();
//                aps.setExplicitActionWrapper(permission.getActionWrapper());
//                aps.setExplicitRoleWrapper(roleWrapper);
//
//                aps.addPermission(
//                    PermissionValue.create(
//                        PermissionValue.GRANTED,
//                        permission));
            }



//          collect inherited permissions
            Collection superroles = hps.getSuperRoleWrappersDeep(roleWrapper);
            for (Iterator iterator = superroles.iterator(); iterator
                    .hasNext();)
            {
                RoleWrapper superrole = (RoleWrapper) iterator.next();

                Collection<PermissionWrapper> permissionWrappers =
                    superrole.getPermissionWrapper();

                for (Iterator iter = permissionWrappers.iterator(); iter.hasNext();)
                {
                    PermissionWrapper pw = (PermissionWrapper) iter.next();

                    PermissionValue pv =
                        PermissionValue.create(
                            PermissionValue.INHERITED, pw);

                    permissionValues.add(pv);
                }
            }

            sortByResource();
            sortByPermissionValue();
        }
        catch (Exception e)
        {
            logger.logException(e);
        }
    }

    public String getColumnName(int column)
    {
        //logger.info("requesting Column Name " + column);

        if(column == 0)
        {
            return "PERMISSION";
        }
        if(column == 1)
        {
            return "RESOURCE";
        }
        else if(column == 2)
        {
            return "ACTION";
        }
        else
            return "#ERROR#";
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */

    public int getRowCount()
    {
        try
        {
            return permissionValues.size();
            //return actionWrappers.size();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public int getColumnCount()
    {
        return 3;
    }



    public Object getValueAt(int row, int col)
    {
        if(col == 0)
        {
            return permissionValues.get(row);
        }
        else if(col == 1)
        {
            if(permissionValues.get(row) == null)
                return "null";
            else
            {
                //return actionWrappers.get(row).getResource().toString();
                String resourcePath =
                    permissionValues.get(row).getPermissionWrapper().
                    getActionWrapper().getResourceWrapper().
                    getResourcePath();
                //actionWrappers.get(row).getResourceWrapper().getResourcePath();

                return resourcePath;
            }
        }
        else if(col == 2)
        {
            if(permissionValues.get(row) == null)
                return "null";
            else
            {
                return permissionValues.get(row).getPermissionWrapper().
                       getActionWrapper().getName();
                //return actionWrappers.get(row).getName();
            }
        }
        else
            return null;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if(columnIndex == 0)
            return PermissionValue.class;
        else
            return String.class;
    }


    public void fireManually()
    {
        fireTableStructureChanged();
        fireTableDataChanged();
        fireTableChanged(new TableModelEvent(this));

    }


    private void sortByResource()
    {
        Collections.sort(permissionValues, new permissionActionResourceComparator());
        //Collections.sort(actionWrappers, new actionResourceComparator());
    }

    private void sortByActionName()
    {
        Collections.sort(//actionWrappers,  new actionNameComparator()
            permissionValues, new permissionActionNameComparator());
    }

    private void sortByPermissionValue()
    {
        Collections.sort(permissionValues,
                         new permissionValuesComparator());
    }

//    public class actionResourceComparator implements Comparator<ActionWrapper>
//    {
//        public int compare(ActionWrapper a1, ActionWrapper a2)
//        {
//          try
//          {
//            if(a1 == null || a2 == null)
//            {
//              if (a1 != null)
//                return a1.getResource().toString().compareTo("");
//              else if(a2 != null)
//                return a2.getResource().toString().compareTo("");
//              else
//                return 0;
//            }
//            else
//              return a1.getResource().toString().
//              compareTo(a2.getResource().toString());
//          }
//          catch (Exception e)
//          {
//            return 0;
//          }
//        }
//    }

    public class permissionActionResourceComparator
        implements Comparator<PermissionValue>
    {
        public int compare(PermissionValue pv1, PermissionValue pv2)
        {
            try
            {
                if(pv1 == null || pv2 == null)
                {
                    if (pv1 != null)
                        return pv1.getPermissionWrapper().getActionWrapper().
                               getResource().toString().compareTo("");
                    else if(pv2 != null)
                        return pv2.getPermissionWrapper().getActionWrapper().
                               getResource().toString().compareTo("");
                    else
                        return 0;
                }
                else
                    return pv1.getPermissionWrapper().getActionWrapper().
                           getResource().toString().
                           compareTo(pv2.getPermissionWrapper().getActionWrapper().
                                     getResource().toString());
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }

//    public class actionNameComparator implements Comparator<ActionWrapper>
//    {
//        public int compare(ActionWrapper a1, ActionWrapper a2)
//        {
//          try
//          {
//            if(a1 == null || a2 == null)
//            {
//              if (a1 != null)
//                return a1.getResource().toString().compareTo("");
//              else if(a2 != null)
//                return a2.getResource().toString().compareTo("");
//              else
//                return 0;
//            }
//            else
//              return a1.getName().compareTo(a2.getName());
//          }
//          catch (Exception e)
//          {
//            return 0;
//          }
//        }
//    }

    public class permissionActionNameComparator
        implements Comparator<PermissionValue>
    {
        public int compare(PermissionValue pv1, PermissionValue pv2)
        {
            try
            {
                if(pv1 == null || pv2 == null)
                {
                    if (pv1 != null)
                        return pv1.getPermissionWrapper().getActionWrapper().
                               getResource().toString().compareTo("");
                    else if(pv2 != null)
                        return pv2.getPermissionWrapper().getActionWrapper().
                               getResource().toString().compareTo("");
                    else
                        return 0;
                }
                else
                    return pv1.getPermissionWrapper().getActionWrapper().
                           getName().compareTo(pv2.getName());
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }


    // sorts descending
    public class permissionValuesComparator
        implements Comparator<PermissionValue>
    {
        public int compare(PermissionValue pv1, PermissionValue pv2)
        {
            try
            {
                if(pv1 == null || pv2 == null)
                {
                    if (pv1 != null)
                        return -pv1.getValue();
                    else if(pv2 != null)
                        return pv2.getValue();
                    else
                        return 0;
                }
                else
                    return -pv1.compareTo(pv2);
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }

}
