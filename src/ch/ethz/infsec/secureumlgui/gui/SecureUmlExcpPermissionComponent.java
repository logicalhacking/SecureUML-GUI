package ch.ethz.infsec.secureumlgui.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.ethz.infsec.secureumlgui.util.ExcpPermissionDummy;
import ch.ethz.infsec.secureumlgui.util.PermissionDummy;


/**
 *
 *
 */
public class SecureUmlExcpPermissionComponent extends
    SecureUmlPermissionComponent {


    JLabel lbExcpLevel;
    JTextField txExcpLevel;


    public SecureUmlExcpPermissionComponent()
    {
        super();
        initExcpComponents();
    }

    private void initExcpComponents() {

        super.lbName.setText("Exception Permission Name: ");

        lbExcpLevel = new JLabel("Exception Level: ");
        txExcpLevel = new JTextField();
        txExcpLevel.setEditable(false);



    }

    //hack...
    protected void addAdditionalPanels(JPanel boxes) {
        lbExcpLevel = new JLabel("Exception Level: ");
        txExcpLevel = new JTextField();
        txExcpLevel.setEditable(false);

        JPanel excpLevel = new JPanel();
        excpLevel.setLayout(new BoxLayout(excpLevel, BoxLayout.LINE_AXIS));
        excpLevel.setMaximumSize(new Dimension(1000,20));
        excpLevel.add(lbExcpLevel);
        excpLevel.add(txExcpLevel);

        boxes.add(excpLevel);
    }

    public void setDisplayedPermission(ExcpPermissionDummy displayedPermission) {
        super.setDisplayedPermission(displayedPermission);

    }


}
