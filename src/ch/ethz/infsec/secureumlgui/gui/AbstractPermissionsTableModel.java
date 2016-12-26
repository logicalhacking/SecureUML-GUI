/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import org.argouml.ui.targetmanager.TargetManager;
import org.omg.uml.foundation.core.UmlClass;

import ch.ethz.infsec.secureumlgui.ModuleController;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ActionPermissionSet;
//import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Permission;
//import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Resource;
//import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role;

import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;
import ch.ethz.infsec.secureumlgui.securemodelimpl.SecureModelFactory;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;


/**
 *
 */
public abstract class AbstractPermissionsTableModel
    extends AbstractTableModel
    implements MouseListener
{
    /**
     *
     */
    public AbstractPermissionsTableModel(Object resource)
    {
        super();

        //setResource(resource);
    }

    /**
     * @param resource
     */
    protected void setResource(Object resource)
    {
        roleWrappers.clear();

        for (Object role : ModuleController.getInstance().getAllRoles(resource)) {
            RoleWrapper rw = new RoleWrapper(role);
            roleWrappers.add(rw);
        }
    }


    protected String newPermissionSuffix = SecureUmlConstants.NEW_PERMISSION_SUFFIX;

    /**
     * @return the newPermissionSuffix
     */
    protected String getNewPermissionSuffix()
    {
        return newPermissionSuffix;// + newPermissionNumber++;
    }

    /**
     * @param newPermissionSuffix the newPermissionSuffix to set
     */
    protected void setNewPermissionSuffix(String newPermissionSuffix)
    {
        this.newPermissionSuffix = newPermissionSuffix;
    }

    protected List<RoleWrapper> roleWrappers =
        new ArrayList<RoleWrapper>();



    protected MultiContextLogger logger = new MultiContextLogger(
        MultiContextLogger.GUI);


    public int getColumnCount()
    {
        /* TODO: constant in first Version
         * (could do via reflection later)
         *
         * // old, swapped layout
         *
         * // 4 =   1 (for the role name)
         * //     + 3 (for the attribute Actions -read, change, full access)
         */

        return roleWrappers.size() + 1;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
//        if(columnIndex == 0)
//            return String.class;
        //else
        if(columnIndex >= 1)
            //    return Boolean.class;
            return ActionPermissionSet.class;
        else
            return super.getColumnClass(columnIndex);
    }

    @Override
    public String getColumnName(int column)
    {
        //logger.info(logger.GUI, "requesting Column Name " + column);

        if(column == 0)
            return "ACTION";
        else
        {
            try
            {
                RoleWrapper roleWrapper = roleWrappers.get(column-1);
                return roleWrapper.getName();
            }
            catch (Exception e)
            {

                e.printStackTrace();
                return super.getColumnName(column);
            }
        }
    }


    public boolean isCellEditable(int row, int col)
    {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    public void fireManually()
    {
        fireTableStructureChanged();
        fireTableDataChanged();
        fireTableChanged(new TableModelEvent(this));
    }

// helper methods


    /* creates a new Permission object connects it
     * to the role given as argument
     */
    protected PermissionWrapper createPermission(
        RoleWrapper/*Role*/ roleWrapper)
    {
        /*Permission*/ Object o =
            SecureModelFactory.getInstance().
            createPermission();

        PermissionWrapper p = new PermissionWrapper(o);
        p.setRoleWrapper(roleWrapper);


        return p;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
//      logger.info("Table Header Mouse Clicked"
//          + "\n source: " + e.getSource()
//          + "\n component: " + e.getComponent()
//          + "\n point:" + e.getPoint());

        if(e.getClickCount() == 2)
        {
            try
            {
                JTableHeader header = (JTableHeader) e.getSource();
                int clickedColumnIndex =
                    header.getColumnModel().getColumnIndexAtX(e.getX());

                Object clickedSuRole =
                    roleWrappers.get(clickedColumnIndex-1).
                    getModelElement();

                Object roleUml =
                    ModelMap.getDefault().getUmlElement(clickedSuRole);
                if (roleUml instanceof UmlClass)
                {
                    UmlClass roleUmlClass = (UmlClass) roleUml;
                    TargetManager.getInstance().setTarget(roleUmlClass);
                }
            }
            catch (Exception ex)
            {
                logger.logException(ex);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {

    }




}
