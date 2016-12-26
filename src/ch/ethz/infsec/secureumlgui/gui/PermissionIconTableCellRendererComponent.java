/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.ethz.infsec.secureumlgui.ResourceFilesManager;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionValue;

/**
 *
 */
public class PermissionIconTableCellRendererComponent
    extends JPanel
{

    static ResourceFilesManager resourceFilesManager =
        new ResourceFilesManager();

    MultiContextLogger logger =
        MultiContextLogger.getDefault();

    JCheckBox cbExplicitPermission = new JCheckBox();
    JLabel lbConstrained = new JLabel();
    JLabel lbImplicit = new JLabel();
    JLabel lbInherited = new JLabel();

    //JLabel lbValue = new JLabel();

    /**
     *
     */
    public PermissionIconTableCellRendererComponent(PermissionValue pv)
    {
        initIconLabels();
        //lbValue.setText(pv.getName());

        setVisible(true);


        //this.add(lbValue);

        String tooltip = pv.getDescription();

        if(pv.getValue() == pv.GRANTED.getValue())
        {
            this.add(cbExplicitPermission);
            cbExplicitPermission.setVisible(true);
        }
        else if(pv.getValue() == pv.IMPLICIT.getValue())
        {
            this.add(lbImplicit);
            lbImplicit.setVisible(true);
        }
        else if(pv.getValue() == pv.INHERITED.getValue())
        {
            this.add(lbInherited);
            lbImplicit.setVisible(true);
        }
        else
        {
            this.add(new JLabel("error"));
        }

        if(pv.isConstrained())
        {
            this.add(lbConstrained);
            lbConstrained.setVisible(true);
        }

        setToolTipText(tooltip);

    }


    protected void initIconLabels()
    {
        cbExplicitPermission.setSelected(true);
        lbConstrained.setIcon(resourceFilesManager.getConstrainedIcon());
        lbInherited.setIcon(resourceFilesManager.getInheritedRoleIcon());
        lbImplicit.setIcon(resourceFilesManager.getImplicitIcon());
    }

}
