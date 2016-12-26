/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPanel;

import org.tigris.swidgets.LabelledLayout;

import ch.ethz.infsec.secureumlgui.gui.RolePermissionsTableModel;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionValue;

import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

/**
 * SecureUML Properties component for Roles.
 *
 * Displays all actions this role has (explicit, implicit, or inherited)
 *
 */
public class SecureUmlRoleComponent extends AbstractSecureUmlComponent
{
    /**
     *
     */
    public SecureUmlRoleComponent()
    {
        super();//"SecureUml Role Permissions");//Properties");

        initComponents();
    }

    JLabel lbName;
    JTextField txName;

    JLabel lbPermissions;

    JScrollPane scpPermissions;
    JTable tblPermissions;

    JButton btNewRole;

    public void initComponents()
    {
        lbName = new JLabel("Role Name: ");
        txName = new JTextField();
        txName.setEditable(false);


        tblPermissions= new JTable();

        // already displayed by the default PropPanel
        // but here for testing purposes

        JPanel labels = new JPanel();

        labels.add(lbName,BorderLayout.LINE_START);
        labels.add(txName, BorderLayout.CENTER);
        /* NOTE: If a JTable is put on a JScrollPane, the Header
         * is displayed automatically (otherwise this needs to be
         * done manually). The Column Names are fetched from the
         * Method 'TableModel.getColumnName(int col)'
         */
        scpPermissions = new JScrollPane(tblPermissions);

        tblPermissions.setRowHeight(26);
        //scpPermissions.setMinimumSize(new Dimension(0, 120));

        //this.add(tblPermissions);
        this.add(labels, BorderLayout.LINE_START);
        this.add(scpPermissions, BorderLayout.CENTER);
        //this.validate();

    }


    private RoleWrapper displayedRoleWrapper;

    public RoleWrapper getDisplayedRoleWrapper()
    {
        return displayedRoleWrapper;
    }

    public void setDisplayedRoleWrapper(RoleWrapper displayedRoleWrapper)
    {
        tblPermissions.setDefaultRenderer(
            PermissionValue.class,
            new PermissionIconTableCellRenderer());

//      tblPermissions.getColumnModel().
//        getColumn(0).setCellRenderer(
//            new PermissionIconTableCellRenderer());

        this.displayedRoleWrapper = displayedRoleWrapper;

        if(displayedRoleWrapper != null)
        {
            txName.setText(displayedRoleWrapper.getName());

            //            setTitle(displayedRoleWrapper.getName()
            //  + " - SecureUML Role");
        }

        displayPermissions();
    }

    protected void displayPermissions()
    {
        try
        {
            RolePermissionsTableModel tableModel =
                new RolePermissionsTableModel(
                displayedRoleWrapper);

            //tblPermissions.setModel(tableModel);

            TableSorter sorter = new TableSorter(tableModel);
            tblPermissions.setModel(sorter);
            //sorter.addMouseListenerToHeaderInTable(tblPermissions);
            sorter.setTableHeader(tblPermissions.getTableHeader());
            // http://java.sun.com/docs/books/tutorial/uiswing/components/table.html#sorting


            tableModel.fireManually();
            sorter.fireTableStructureChanged();
            sorter.fireTableDataChanged();
        }
        catch (Exception e)
        {
            logger.logException(e);
        }
    }

    /* (non-Javadoc)
     * @see ch.ethz.infsec.secureumlgui.gui.AbstractSecureUmlComponent#setDisplayedSecureUmlElement(java.lang.Object, ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType)
     */
    @Override
    public void setDisplayedSecureUmlElement(Object suElement, ResourceType rt)
    {

        //setTitle("SecureUML Role");

        // TODO Auto-generated method stub
        super.setDisplayedSecureUmlElement(suElement, rt);

        setDisplayedRoleWrapper(new RoleWrapper(suElement));

    }

}
