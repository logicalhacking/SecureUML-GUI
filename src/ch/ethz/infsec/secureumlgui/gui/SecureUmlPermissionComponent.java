/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BoxLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ch.ethz.infsec.secureumlgui.ModuleController;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.securemodel.secureuml.Permission;
import ch.ethz.infsec.secureumlgui.util.PermissionDummy;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;

/**
 * SecureUML Properties component for permissions.
 *
 * Displays the role which has this permission, as well as the action,
 * this permission permits.
 *
 */
public class SecureUmlPermissionComponent
    extends AbstractSecureUmlComponent
    //implements DocumentListener
{
    /**
     *
     */
    public SecureUmlPermissionComponent()
    {
        super();
        initComponents();
    }

    JPanel textboxesPanel;

    JLabel lbName;
    JTextField txName;

    JLabel lbAnchor;
    JTextField txAnchor;

    JLabel lbRole;
    JTextField txRole;

    JLabel lbConstraint;
    JTextArea txConstraint;

    JLabel lbPermissionAttributes;

    JScrollPane scpPermissionAttributes;
    JTable tblPermissionAttributes;



    public void initComponents()
    {

        lbName = new JLabel("Permission Name: ");
        txName = new JTextField();
        txName.setEditable(false);

        lbAnchor = new JLabel("Anchor: ");
        txAnchor = new JTextField();
        txAnchor.setEditable(false);

        lbRole = new JLabel("Role: ");
        txRole = new JTextField();
        txRole.setEditable(false);

        lbConstraint = new JLabel("OCL Constraint: ");
        txConstraint = new JTextArea(10,60);
        txConstraint.setEditable(true);
        JScrollPane scpConstr = new JScrollPane(txConstraint);
        tblPermissionAttributes = new JTable();
        scpPermissionAttributes = new JScrollPane(tblPermissionAttributes);
        // scpPermissionAttributes.setMinimumSize(
        //     new Dimension(200, 140));

        JPanel names = new JPanel();
        names.setLayout(new BoxLayout(names, BoxLayout.LINE_AXIS));
        names.setMaximumSize(new Dimension(1000,20));
        names.add(lbName);
        names.add(txName);
        JPanel roles = new JPanel();
        roles.setLayout(new BoxLayout(roles, BoxLayout.LINE_AXIS));
        roles.setMaximumSize(new Dimension(1000,20));
        roles.add(lbRole);
        roles.add(txRole);
        JPanel anchors = new JPanel();
        anchors.setLayout(new BoxLayout(anchors, BoxLayout.LINE_AXIS));
        anchors.setMaximumSize(new Dimension(1000,20));

        anchors.add(lbAnchor);
        anchors.add(txAnchor);

        anchors.setAlignmentX(LEFT_ALIGNMENT);
        names.setAlignmentX(LEFT_ALIGNMENT);
        roles.setAlignmentX(LEFT_ALIGNMENT);
        lbConstraint.setAlignmentX(LEFT_ALIGNMENT);
        scpConstr.setAlignmentX(LEFT_ALIGNMENT);

        JPanel boxes = new JPanel();
        boxes.setLayout(new BoxLayout(boxes, BoxLayout.PAGE_AXIS));

        addAdditionalPanels(boxes);
        boxes.add(names);
        boxes.add(roles);
        boxes.add(anchors);

        boxes.add(lbConstraint);
        boxes.add(scpConstr);

        txConstraint.getDocument().addDocumentListener(new ConstraintListener());

        this.add(boxes, BorderLayout.LINE_START);
        this.add(scpPermissionAttributes, BorderLayout.CENTER);
    }

    //hack...
    protected void addAdditionalPanels(JPanel boxes) {
        ;
    }



    private PermissionDummy displayedPermission;

    public PermissionDummy getDisplayedPermission()
    {
        return displayedPermission;
    }

    List<PermissionWrapper> displayedPermissionAttributes;

    public void setDisplayedPermission(PermissionDummy displayedPermission)
    {
        if (displayedPermission == null) logger.error("null in setDisplayedPermission");
        this.displayedPermission = displayedPermission;

//        logger.info(logger.TARGET_EVENTS, "displayedPermission set to: "
//                + displayedPermission
//                + "\n named: " + displayedPermission.getName()
//                + ",\n for anchor: "
//                + displayedPermission.getAnchor().getName()
//                + ",\n and role: "

//                + displayedPermission.getRole().getName());

        try
        {
            if (displayedPermission.getAnchor() == null) logger.error("displayed Permission has no anchor set");
            txName.setText(displayedPermission.getName());
            txAnchor.setText(displayedPermission.
                             getAnchorWrapper().getName());
            txRole.setText(displayedPermission.
                           getRoleWrapper().getName());
            if(displayedPermission.
                    getAuthorizationConstraintWrapper().
                    getModelElement() != null)
            {
                txConstraint.setText(displayedPermission.
                                     getAuthorizationConstraintWrapper().getConstraint());
            }
            else
                txConstraint.setText("");

            //            setTitle(displayedPermission.getName()
            //    + " - SecureUML Permission");

            this.invalidate();
        }
        catch (Exception e)
        {
            logger.logException(e);
        }

        displayPermissionAttributes(
            displayedPermission.getPermissionAttributeWrappers());
    }

    private void onAuthorizationConstraintChanged()
    {
        // too verbose -
        // 2 lines for each single character change in Textbox
//        logger.info(logger.GUI,
//                "AuthorizationConstraint changed to: '"
//                + txConstraint.getText() + "'");
        ModuleController.getInstance().
        setAuthorizationConstraint(displayedPermission,
                                   txConstraint.getText());
    }

    private void onPermissionNameChanged()
    {
//        ModuleController.getInstance().
//            setPermissionName(displayedPermission,
//                    txName.getText());
    }

    protected void displayPermissionAttributes(
        List<PermissionWrapper> permissionAttributes)
    {
        this.displayedPermissionAttributes = permissionAttributes;
        try
        {
            PermissionAttributesTableModel tableModel =
                new PermissionAttributesTableModel(
                displayedPermissionAttributes);

            //tblPermissions.setModel(tableModel);

            TableSorter sorter = new TableSorter(tableModel);
            tblPermissionAttributes.setModel(sorter);
            //sorter.addMouseListenerToHeaderInTable(tblPermissions);
            sorter.setTableHeader(
                tblPermissionAttributes.getTableHeader());
            // http://java.sun.com/docs/books/tutorial/uiswing/components/table.html#sorting

            tableModel.fireManually();
            sorter.fireTableStructureChanged();
            sorter.fireTableDataChanged();
            //tblPermissions.validate();
            //scpPermissions.validate();
            //tblPermissions.setTableHeader(new JTableHeader(tableModel))
            // moved to RolePermissionsTableModel
//            for (Iterator iter = displayedRole.getPermission().iterator(); iter.hasNext();)
//            {
//                Permission permission = (Permission) iter.next();
//
//                for (Iterator iterator = permission.getAction().iterator(); iterator.hasNext();)
//                {
//                    Action action = (Action) iterator.next();
//
//                    txPermissions.append(action.getResource()
//                            + ":  " + action.getName()
//                            + "\n");
//                }
//            }
        }
        catch (Exception e)
        {
            logger.logException(e);
        }
    }





    private class ConstraintListener implements DocumentListener
    {

        /* Document Listener Handlers */

        /**
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent e)
        {
            onAuthorizationConstraintChanged();
        }

        /**
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent e)
        {
            onAuthorizationConstraintChanged();
        }

        /**
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent e)
        {
            onAuthorizationConstraintChanged();
        }

    }

    private class PermissionNameListener implements DocumentListener
    {

        /* Document Listener Handlers */

        /**
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent e)
        {
            onPermissionNameChanged();


            //txConstraint.getDocument().get


            //e.getChange(null).
        }

        /**
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent e)
        {
            onPermissionNameChanged();

        }

        /**
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent e)
        {
            onPermissionNameChanged();

        }

    }

    /* (non-Javadoc)
     * @see ch.ethz.infsec.secureumlgui.gui.AbstractSecureUmlComponent#setDisplayedSecureUmlElement(java.lang.Object, ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType)
     */
    @Override
    public void setDisplayedSecureUmlElement(Object suElement, ResourceType rt)
    {
        //setTitle("SecureUML Permission");

        // TODO Auto-generated method stub
        if (suElement == null) logger.error("null in setDisplayedSecureUmlElement");
        if (rt == null) logger.error("null in setDisplayedSecureUmlElement");
        super.setDisplayedSecureUmlElement(suElement, rt);

        setDisplayedPermission((PermissionDummy)suElement);

    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#setMinimumSize(java.awt.Dimension)
     */
    @Override
    public void setMinimumSize(Dimension minimumSize)
    {
        // TODO Auto-generated method stub
        //super.setMinimumSize(minimumSize);

//      minimumSize.height -= 100;
//      minimumSize.height /= 2;
//
//      if(minimumSize.height >0)
//        scpPermissionAttributes.setMinimumSize(minimumSize);
    }

}
