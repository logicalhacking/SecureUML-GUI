package ch.ethz.infsec.secureumlgui.usecasemapper.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <code>MenuActionListener</code> is the <code>listener</code> used
 * in for the custom <code>ArgoUML</code> menu for the <code>SecureUML</code>
 * module.
 *
 * @version 1.0
 */
public class MenuActionListener implements ActionListener {

    /**
     * The reference to the @see Controller.
     */
    private final Controller controller = new Controller();

    /**
     * Responds to the <code>ActionEvents</code> from the module menu.
     *
     * @param event the <code>ActionEvent</code> to be handled.
     */
    public final void actionPerformed(final ActionEvent event) {
        controller.map();
    }
}
