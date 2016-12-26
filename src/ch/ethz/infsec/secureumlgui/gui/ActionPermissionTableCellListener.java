/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import javax.swing.JCheckBox;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;;

/**
 *
 */
public class ActionPermissionTableCellListener
    implements MouseListener
{
    /**
     *
     */
    public ActionPermissionTableCellListener(JCheckBox editorCheckbox)
    {
        this.editorCheckbox = editorCheckbox;
    }

    MultiContextLogger logger =
        MultiContextLogger.getDefault();

    JCheckBox editorCheckbox;

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
        editorCheckbox.doClick();

        logger.info("mouse clicked");
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
        // TODO Auto-generated method stub


    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
        // TODO Auto-generated method stub


    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
        logger.info("mouse Pressed");

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
        // TODO Auto-generated method stub


    }



}
