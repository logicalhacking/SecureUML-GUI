/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.UmlClass;

import ch.ethz.infsec.secureumlgui.ModuleController;
import ch.ethz.infsec.secureumlgui.ResourceFilesManager;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ActionPermissionSet;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionValue;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;

/**
 *
 */
public class ActionPermissionsTableCellRendererComponent
    extends JPanel
    implements ActionListener
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private PolicyWrapper policy;
    //private UmlClass policy;


    private static Logger aLog = Logger.getLogger(ActionPermissionsTableCellRendererComponent.class);
    /**
     *
     */
    public ActionPermissionsTableCellRendererComponent(
        ActionPermissionSet actionPermissions, PolicyWrapper policy)
    {
        //
        this.actionPermissions = actionPermissions;
        this.policy = policy;

        this.add(cbExplicitPermission);

        initIconLabels();



        //this.add(lbOtherPermissions);

//    container.addMouseListener(
//        new ActionPermissionTableCellListener(
//            cbExplicitPermission));

//    cbExplicitPermission.addMouseListener(
//        new ActionPermissionTableCellListener(
//            cbExplicitPermission));
        processPermissions(actionPermissions);

        cbExplicitPermission.addActionListener(this);

//    lbOtherPermissions.setText(
//        actionPermissions.toString());

//    this.setToolTipText(
//        "Description of the origin of all permissions");
//
    }



    static ResourceFilesManager resourceFilesManager =
        new ResourceFilesManager();


    ActionPermissionSet actionPermissions = null;

    MultiContextLogger logger =
        MultiContextLogger.getDefault();

    JCheckBox cbExplicitPermission = new JCheckBox();


    //JLabel lbOtherPermissions = new JLabel();
    JLabel lbConstrained = new JLabel();
    JLabel lbImplicit = new JLabel();
    JLabel lbImplicitConstrained = new JLabel();
    JLabel lbInheritedRole = new JLabel();
    JLabel lbInheritedPolicy = new JLabel();
    JLabel lbInheritedConstrained = new JLabel();
    JLabel lbCompositeFull = new JLabel();
    JLabel lbCompositeFullConstrained = new JLabel();
    JLabel lbImplicitByComposite = new JLabel();


    //PermissionValue explicitPermissionValue = null;


    protected void initIconLabels()
    {

        lbConstrained.setIcon(resourceFilesManager.getConstrainedIcon());
        lbInheritedRole.setIcon(resourceFilesManager.getInheritedRoleIcon());
        lbInheritedPolicy.setIcon(resourceFilesManager.getInheritedPolicyIcon());
        lbInheritedConstrained.setIcon(resourceFilesManager.getConstrainedIcon());
        lbImplicit.setIcon(resourceFilesManager.getImplicitIcon());
        lbImplicitConstrained.setIcon(resourceFilesManager.getConstrainedIcon());
        lbCompositeFull.setIcon(resourceFilesManager.getCompositeFullIcon());
        lbCompositeFullConstrained.setIcon(resourceFilesManager.getConstrainedIcon());
        lbImplicitByComposite.setIcon(resourceFilesManager.getImplicitByInheritedIcon());

        this.add(lbConstrained);
        this.add(lbInheritedRole);
        this.add(lbInheritedPolicy);
        this.add(lbInheritedConstrained);
        this.add(lbImplicit);
        this.add(lbImplicitConstrained);
        this.add(lbCompositeFull);
        this.add(lbCompositeFullConstrained);
        this.add(lbImplicitByComposite);

    }

    /**
     * @param actionPermissions
     */
    private void processPermissions(ActionPermissionSet actionPermissions)
    {
        cbExplicitPermission.setSelected(actionPermissions.isExplicitPermitted(policy));
        // XXX
//    explicitPermissionValue =
//      actionPermissions.getExplicitPermission();
//
//    cbExplicitPermission.setSelected(
//        actionPermissions.getExplicitPermission().
//        getValue() ==
//          PermissionValue.GRANTED.getValue());

        // first set all icons invisible
        lbConstrained.setVisible(false);
        lbInheritedRole.setVisible(false);
        lbInheritedPolicy.setVisible(false);
        lbInheritedConstrained.setVisible(false);
        lbImplicit.setVisible(false);
        lbImplicitConstrained.setVisible(false);
        lbCompositeFull.setVisible(false);
        lbCompositeFullConstrained.setVisible(false);
        lbImplicitByComposite.setVisible(false);

        String tooltip = "";

        // and then show the ones for the defined permissions
        for (Iterator iter = actionPermissions.getPermissions(policy).iterator(); iter.hasNext();)
        {
            PermissionValue pv = (PermissionValue) iter.next();

            tooltip += pv.getDescription() + " *** \n";

            int flags = pv.getFlags();

            aLog.debug("permission (0x" + Integer.toHexString(flags) + ") " + pv.getPermissionWrapper().getName());

            if ( ( flags & PermissionValue.INT_GRANTED & PermissionValue.INT_CONSTRAINED) > 0 ) {
                if(pv.isConstrained())
                    lbConstrained.setVisible(true);
            }

            if ( (flags & PermissionValue.INT_INHERITED_POLICY ) > 0) {
                //ignore all types from super polices
                lbInheritedPolicy.setVisible(true);
            } else {
                if ( (flags & PermissionValue.INT_INHERITED) > 0 ) {
                    lbInheritedRole.setVisible(true);
                    if ( (flags & PermissionValue.INT_CONSTRAINED) > 0 ) {
                        lbInheritedConstrained.setVisible(true);
                    }
                }


                if ( (flags & (PermissionValue.INT_IMPLICIT | PermissionValue.INT_COMPOSITE) ) > 0 ) {
                    lbImplicitByComposite.setVisible(true);
                }
                else {
                    if ( (flags & PermissionValue.INT_IMPLICIT) > 0 ) {
                        lbImplicit.setVisible(true);
                        if ( (flags & PermissionValue.INT_CONSTRAINED) > 0 ) {
                            lbImplicitConstrained.setVisible(true);
                        }
                    }

                    if ( (flags & PermissionValue.INT_COMPOSITE) > 0 ) {
                        lbCompositeFull.setVisible(true);
                        if ( (flags & PermissionValue.INT_CONSTRAINED) > 0 ) {
                            lbCompositeFullConstrained.setVisible(true);
                        }
                    }
                }
            }



//      int value = pv.getValue();
//
//      if(value == pv.GRANTED.getValue())
//      {
//        // this is the explicit permission
//        // -> checkbox is displayed and initialized already
//        if(pv.isConstrained())
//          lbConstrained.setVisible(true);
//      }
//      else if(value == pv.DENIED.getValue())
//      {
//        // denied permission =^= no permission
//        // do nothing
//      }
//      else if(value == pv.INHERITED.getValue())
//      {
//        lbInherited.setVisible(true);
//        if(pv.isConstrained())
//          lbInheritedConstrained.setVisible(true);
//      }
//      else if(value == pv.IMPLICIT.getValue())
//      {
//        lbImplicit.setVisible(true);
//        if(pv.isConstrained())
//          lbImplicitConstrained.setVisible(true);
//      }
//      else if(value == pv.COMPOSITE.getValue())
//      {
//        lbCompositeFull.setVisible(true);
//        if(pv.isConstrained())
//          lbCompositeFullConstrained.setVisible(true);
//      }
//      else
//      {
//    	  aLog.warn("unhandled permission value: " + pv);
//        logger.error(
//            "unhandled permission value: " + pv);
//      }
        }

        this.setToolTipText(tooltip);
    }

    /**
     * @return the cbExplicitPermission
     */
    public JCheckBox getCbExplicitPermission()
    {
        return cbExplicitPermission;
    }


    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
//    PermissionWrapper pw =
//      explicitPermissionValue.getPermissionWrapper();
//
//    logger.info("Editing a Permission, old PermissionValue: "
//        + explicitPermissionValue + "\n");

//    if(pw == null)
//    {
//      logger.error("PermissionWrapper == null");
//    }
//    else
//    {
//      logger.info("Action: "
//          + pw.getActionWrapper().getName()
//          + ", Role: "
//          + pw.getRoleWrapper().getName());

//    }

        logger.info("Action: "
                    + actionPermissions.getExplicitActionWrapper()
                    + ", Role: "
                    + actionPermissions.getExplicitRoleWrapper());


        PermissionWrapper pw = actionPermissions.getExplicitPermittedPermission(policy);
        //explicitPermissionValue.getPermissionWrapper();
        if(pw == null)
        {
            logger.info("creating permission...");

            Set<PolicyWrapper> policies = new HashSet<PolicyWrapper>();
            policies.add(policy);

            logger.info("existing permissions: "+actionPermissions);
            ModuleController.getInstance().addPermission(
                actionPermissions.getExplicitActionWrapper(),
                actionPermissions.getExplicitRoleWrapper(),
                policies);
        }
        else
        {
            logger.info("deleting permission...");

            ModuleController.getInstance().deletePermission(
                pw);
        }

    }

    @Override
    public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
    }



}
