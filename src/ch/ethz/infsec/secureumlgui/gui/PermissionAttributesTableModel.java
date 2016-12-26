/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;

/**
 * Swing table model for the permission table displayed in the
 * SecureUML properties tab for permissions.
 * Each row is one resource-action pair.
 *
 */
public class PermissionAttributesTableModel extends AbstractTableModel
{
    /**
     *
     */
    public PermissionAttributesTableModel(
        List<PermissionWrapper> permissions)
    {
        super();

        setCurrentPermissionWrappers(permissions);

    }

    List<PermissionWrapper> currentPermissionWrappers;

    ArrayList<ActionWrapper> actionWrappers =
        new ArrayList<ActionWrapper>();

    MultiContextLogger logger = MultiContextLogger.getDefault();


    public void setCurrentPermissionWrappers(
        List<PermissionWrapper> permissionWrappers)
    {
        actionWrappers.clear();

        currentPermissionWrappers = permissionWrappers;
        try
        {
            for (Iterator iter = currentPermissionWrappers.iterator(); iter.hasNext();)
            {
                PermissionWrapper permissionWrapper = (PermissionWrapper) iter.next();

                actionWrappers.add(permissionWrapper.getActionWrapper());
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
            }

            sortByResource();
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
            return "RESOURCE";
        }
        else if(column == 1)
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
            return actionWrappers.size();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public int getColumnCount()
    {
        return 2;
    }

    public Object getValueAt(int row, int col)
    {
        if(col == 0)
        {
            String val = actionWrappers.get(row).getResourceWrapper().getResourcePath();
            if(val==null || val.equals(""))
                return "N/A";
            else
                return val;
        }
        else if(col == 1)
        {
            String val = actionWrappers.get(row).getName();
            if(val==null || val.equals(""))
                return "N/A";
            else
                return val;
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
        Collections.sort(actionWrappers, new actionResourceComparator());
    }

    private void sortByActionName()
    {
        Collections.sort(actionWrappers, new actionNameComparator());
    }

    public class actionResourceComparator implements Comparator<ActionWrapper>
    {
        public int compare(ActionWrapper a1, ActionWrapper a2)
        {
            try
            {
                if(a1 == null || a2 == null)
                {
                    if (a1 != null)
                        return a1.getResource().toString().compareTo("");
                    else if(a2 != null)
                        return a2.getResource().toString().compareTo("");
                    else
                        return 0;
                }
                else
                    return a1.getResource().toString().compareTo(a2.getResource().toString());
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }

    public class actionNameComparator implements Comparator<ActionWrapper>
    {
        public int compare(ActionWrapper a1, ActionWrapper a2)
        {
            try
            {
                if(a1 == null || a2 == null)
                {
                    if (a1 != null)
                        return a1.getResource().toString().compareTo("");
                    else if(a2 != null)
                        return a2.getResource().toString().compareTo("");
                    else
                        return 0;
                }
                else
                    return a1.getName().compareTo(a2.getName());
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }

}
