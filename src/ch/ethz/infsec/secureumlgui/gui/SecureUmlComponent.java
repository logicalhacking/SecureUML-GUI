/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.omg.uml.foundation.core.UmlClass;

import ch.ethz.infsec.secureumlgui.ModuleController;
import ch.ethz.infsec.secureumlgui.ResourceFilesManager;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ActionPermissionSet;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.HierarchicalPolicyExplorer;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.AtomicActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.CompositeActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.wrapper.ResourceWrapper;

/**
 * generic SecureUML properties component for Resources.
 *
 * Displays which roles have which permissions for which actions on this resource.
 * distinguishes between explicit, implicit, and inherited permissions.
 *
 */
public class SecureUmlComponent
    extends AbstractSecureUmlComponent
    //implements MouseListener
    implements ChangeListener
{

    /**
     *
     */
    public SecureUmlComponent()
    {
        super();
        initComponents();
    }

    private String NEW_ROLE_NAME = "NewRole";

    private JTabbedPane tabPane;
    //private Map<String, PolicyWrapper> polices;

    //private List<PolicyWrapper> policies = new ArrayList<PolicyWrapper>();
    private List<UmlClass> policies = new ArrayList<UmlClass>();
    private List<JTable> tables = new ArrayList<JTable>();



    private PolicyLevelCreator policyCreator;

//	private JScrollPane jScrollPane;
//	private JTable tblPermissions;

    private TableSorter tableSorter;

    private JTextField txName;
    private JLabel lbType;
    private JLabel lbName;

    private ResourceWrapper displayedResourceWrapper = null;

    private GenericResourcePermissionsTableModel tableModel;

    private static Logger aLog = Logger.getLogger(SecureUmlComponent.class);





    public void initComponents()
    {

        JButton btNewRole = new JButton("New Role");
        btNewRole.setToolTipText("New Role");
        btNewRole.addActionListener(new createRoleActionListener());

        try
        {   //SecureUmlComponent.class

            ResourceFilesManager resourceFilesManager =
                new ResourceFilesManager();

            ImageIcon icon = resourceFilesManager.getCreateRoleIcon();

            if (icon != null)
            {
                btNewRole.setIcon(icon);
            }
            else
            {
                logger.warn(logger.STARTUP,
                            "Image File for Button 'New Role' " +
                            "not found - showing Text only");

            }
        }
        catch (Exception e)
        {
            logger.logException(e);
        }


        /* NOTE: If a JTable is put on a JScrollPane, the Header
         * is displayed automatically (otherwise this needs to be
         * done manually). The Column Names are fetched from the
         * Method 'TableModel.getColumnName(int col)'
         */

//        tblPermissions = new JTable();
//        tblPermissions.setColumnSelectionAllowed(false);
//        tblPermissions.setDragEnabled(false);

        tableSorter = new TableSorter();


        //tblPermissions.getTableHeader().setSize(200, 50);

        tabPane = new JTabbedPane(SwingConstants.LEFT);
        tabPane.addChangeListener(this);

        //JScrollPane jScrollPane = new JScrollPane(tblPermissions);


        tabPane.add("Create Policy", policyCreator);


//        int dummyPol = 4;
//        policies = new PolicyWrapper[dummyPol+1];
//        for ( int i = 0 ; i < dummyPol; ++i ) {
//        	tabPane.add("policy Name " + i, new JScrollPane(tblPermissions) );
//        	policies[i+1] = null;
//        }

        policyCreator = new PolicyLevelCreator();

        //        tblPermissions.setFillsViewportHeight(true);

        lbName=new JLabel("SecureUML Resource:");
        txName= new JTextField();
        txName.setEditable(false);

        lbType = new JLabel("unknown Resource");


        JPanel topPane = new JPanel();
        JPanel labels = new JPanel();

        labels.add(lbName,BorderLayout.LINE_START);
        labels.add(lbType,BorderLayout.LINE_START);
        labels.add(txName,BorderLayout.LINE_START);

        topPane.add(labels,BorderLayout.LINE_START);
        topPane.add(btNewRole,BorderLayout.CENTER);

        this.add(topPane,BorderLayout.PAGE_START);
        //this.add(jScrollPane,BorderLayout.CENTER);
        this.add(tabPane,BorderLayout.CENTER);

    }



    //protected void setDisplayedResourceWrapper(AbstractPermissionsTableModel tableModel)
    protected void setDisplayedResourceWrapper()
    {
        int selectedTab = tabPane.getSelectedIndex();
        if ( selectedTab < 0 || selectedTab >= policies.size()) {
            //no policies, create Policy is selected
            return;
        }
        //PolicyWrapper currentPolicy = policies.get(selectedTab); //policies[tabPane.getSelectedIndex()];
        UmlClass currentPolicy = policies.get(selectedTab); //policies[tabPane.getSelectedIndex()];
        JTable tblPermissions = tables.get(selectedTab);
        txName.setText(displayedResourceWrapper.getResourcePath());

        aLog.debug("setDisplayedResourceWrapper tab: " + selectedTab + " policy: " + currentPolicy.getName());

        try
        {
            //tblPermissions.setModel(tableModel);
            tableSorter.setTableModel(tableModel);
            tblPermissions.setModel(tableSorter);
            tableSorter.setTableHeader(tblPermissions.getTableHeader());

            tblPermissions.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
            //tblPermissions.setAutoResizeMode(
            //  JTable.AUTO_RESIZE_OFF);
            tblPermissions.setCellSelectionEnabled(true);
            tblPermissions.setColumnSelectionAllowed(false);
            tblPermissions.setRowSelectionAllowed(false);

            initTableCellDimensions(tblPermissions);


//            tblPermissions.getColumnModel().getColumn(0).
//              setCellRenderer(
//                  new ActionNameTableCellRenderer());
//            tblPermissions.getColumnModel().getColumn(1).
//            setCellRenderer(
//                new ActionPermissionTableCellRenderer());

            tblPermissions.setDefaultRenderer(
                ActionPermissionSet.class,
                new ActionPermissionTableCellRenderer(currentPolicy));
            tblPermissions.setDefaultEditor(
                ActionPermissionSet.class,
                new ActionPermissionTableCellRenderer(currentPolicy));

            tblPermissions.setDefaultRenderer(
                ActionWrapper.class,
                new ActionNameTableCellRenderer());
            tblPermissions.setDefaultEditor(
                ActionWrapper.class,
                new ActionNameTableCellRenderer());

            tblPermissions.setDefaultRenderer(
                AtomicActionWrapper.class,
                new ActionNameTableCellRenderer());
            tblPermissions.setDefaultRenderer(
                CompositeActionWrapper.class,
                new ActionNameTableCellRenderer());

            tblPermissions.getColumnModel().
            getColumn(0).setCellRenderer(
                new ActionNameTableCellRenderer());

            //(new ActionPermissionTableCellRenderer()).addCellEditorListener(l)
            //tblPermissions.addMouseListener(l);

            tblPermissions.getTableHeader().
            setToolTipText("SecureUML Roles");

//            logger.info("tableCellRenderer(1,0): "
//                + tblPermissions.getCellRenderer(1, 0));
//
//            int columnCount = tableModel.getColumnCount();
//
            tableModel.fireManually();


            tblPermissions.getTableHeader().addMouseListener(tableModel);

//            tblPermissions.repaint();

//            tblPermissions.setValueAt(tblPermissions.getValueAt(1, 0),1,0);
//
            // throws NullpointerException only when a first
            // UML Element is selected
            //jScrollPane.getColumnHeader().setVisible(true);
        }
        catch (Exception e)
        {
            logger.logException(e);
        }
    }

    /**
     *
     */
    private void initTableCellDimensions(JTable tblPermissions)
    {
        // hack
        tblPermissions.setRowHeight(26);
        tblPermissions.getColumnModel().getColumn(0).setMaxWidth(52);

        for (Enumeration<TableColumn> cols = tblPermissions.getColumnModel().getColumns(); cols.hasMoreElements();)
        {
            TableColumn col = (TableColumn) cols.nextElement();
            col.setMinWidth(525);
        }
    }


    private class createRoleActionListener implements ActionListener
    {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            ModuleController.getInstance().addRole(
                NEW_ROLE_NAME,
                displayedResourceWrapper);
        }
    }

    private void updatePolicyTabs () {

        aLog.debug("updatePolicyTabs");

        boolean rebuild = false;

        //List<PolicyWrapper> sortedPolicies = HierarchicalPolicyExplorer.getInstance().getSortedPolicies();
        List<UmlClass> sortedPolicies = HierarchicalPolicyExplorer.getInstance().getSortedPolicies();

        int policiesCount = 0;
        int tabCount = tabPane.getTabCount();

        if ( sortedPolicies != null ) {
            policiesCount = sortedPolicies.size();
        }

        if ( tabPane.getTabCount()-1 != policiesCount ) {
            rebuild = true;
        } else {
            for (int i = 0; i < policiesCount && i < tabCount; ++i) {
                if ( ! sortedPolicies.get(i).getName().equals(tabPane.getTitleAt(i))) {
                    rebuild = true;
                    break;
                }
            }
        }

        aLog.debug("updatePolicyTabs: tabCount: " + tabCount+ ", policiesCount: " + policiesCount + " rebuild: " + rebuild);

        if ( rebuild ) {
            for ( int i = 0; i < policiesCount && i < tabCount - 1; ++i) {
                tabPane.setTitleAt(i, sortedPolicies.get(i).getName());
                policies.set(i, sortedPolicies.get(i));
                aLog.debug("set permissonPanel at index " + i +" for policy " + sortedPolicies.get(i).getName());
            }
            if ( policiesCount > (tabCount - 1)) {
                //remove (the last) create policy tab
                tabPane.remove(tabCount - 1);

                //for (int i = policiesCount; i < (tabCount - 1); ++i) {
                for (int i = tabCount - 1; i < policiesCount; ++i) {
                    JTable tblPermissions;

                    if (tables.size() > i && tables.get(i) != null) {
                        tblPermissions = tables.get(i);
                    } else {
                        tblPermissions = new JTable();
                        tblPermissions.setColumnSelectionAllowed(false);
                        tblPermissions.setDragEnabled(false);
                    }

                    JScrollPane jScrollPane = new JScrollPane(tblPermissions);
                    tabPane.add(sortedPolicies.get(i).getName(), jScrollPane);

                    policies.add(i, sortedPolicies.get(i)); //#
                    tables.add(i, tblPermissions);

                    aLog.debug("added permissonPanel at index " + i +" for policy " + sortedPolicies.get(i).getName());
                }
                tabPane.add("Create Policy", policyCreator);
            } else if ( policiesCount < (tabCount - 1) ) {
                for (int i = policiesCount; i <  (tabCount - 1); ++i) {
                    tabPane.remove(i);
                    policies.remove(i);
                    aLog.debug("removed permission panel from index " + i);
                }
            }
            aLog.debug("updatePolicyTabs: done");
        }
//    	if ( sortedPolicies != null) {
//    		policies = new PolicyWrapper[sortedPolicies.size()];
//    		for (int i = 0 ; i < sortedPolicies.size(); ++i) {
//    			tabPane.add(sortedPolicies.get(i).getName(), new JScrollPane(tblPermissions));
//    			policies[i] = sortedPolicies.get(i);
//    		}
//    	}
    }

    public void setDisplayedSecureUmlElement(
        Object suElement, ResourceType rt)
    {
        aLog.debug("setDisplayedSecureUmlElement");
        updatePolicyTabs();
        super.setDisplayedSecureUmlElement(suElement, rt);
        lbType.setText(rt.getName());
        this.displayedResourceWrapper = new ResourceWrapper(suElement);
        this.tableModel = new GenericResourcePermissionsTableModel(displayedResourceWrapper);

        //setDisplayedResourceWrapper(tableModel);
        setDisplayedResourceWrapper();
    }



    public void stateChanged(ChangeEvent arg0) {
        if (tableModel != null) {
            setDisplayedResourceWrapper();
        }
    }

}
