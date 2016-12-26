/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 *
 */
public class ActionPermissionTableCellSelectionListener
    implements ListSelectionListener
{
    MultiContextLogger logger = MultiContextLogger.getDefault();

    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        logger.info("value changed " + e.getSource());
        ListSelectionModel lsm =
            (ListSelectionModel)e.getSource();

        if (!lsm.isSelectionEmpty())
        {
            int selectedCol = lsm.getMinSelectionIndex();


        }
    }

}
