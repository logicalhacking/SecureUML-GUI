package ch.ethz.infsec.secureumlgui.usecasemapper.gui;

import javax.swing.JOptionPane;

import org.argouml.ui.ProjectBrowser;

/**
 * The <code>View</code> class contains all functionality
 * for direct user feedback.
 *
 * @version 1.0
 */
public class View {

    /**
     * The reference to the project browser ArgoUML main window.
     */
    private final ProjectBrowser projectBrowser =
        ProjectBrowser.getInstance();

    /**
     * Displays an info dialog in ArgoUml.
     *
     * @param message the {@link java.lang.String}
     *                containing the info message.
     */
    public final void showInfo(final String message) {
        JOptionPane.showMessageDialog(projectBrowser, message,
                                      "Use case mapper",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an exception dialog.
     *
     * @param message the {@link java.lang.String} containing
     *                the exception message.
     */
    public final void showException(final String message) {
        JOptionPane.showMessageDialog(projectBrowser, message,
                                      "Use case mapper",
                                      JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an exception dialog with an
     * {@link java.lang.Exception}-Object.
     *
     * @param message   the {@link java.lang.String} containing
     *                  the exception message.
     * @param exception the {@link java.lang.Exception} that occured.
     */
    public final void showException(final String message,
                                    final Exception exception) {
        showException(message + "\n\n" + exception.toString());
    }
}
