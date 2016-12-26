/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.Component;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;

/**
 *
 */
public class ActionNameTableCellRenderer extends Object
    implements TableCellRenderer, TableCellEditor
{
    /**
     *
     */
    public ActionNameTableCellRenderer()
    {

    }

    MultiContextLogger logger = MultiContextLogger.getDefault();


    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
//    logger.info("executing " +
//        "ActionNameTableCellRenderer.getTableCellRendererComponent("
//        + value + ")");

        if (value instanceof ActionWrapper)
        {
            ActionWrapper actionWrapper = (ActionWrapper) value;

            ActionNameTableCellRendererComponent container =
                new ActionNameTableCellRendererComponent(
                actionWrapper);

            return container;
        }
        else
            return new JTextField("error");

    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void addCellEditorListener(CellEditorListener l)
    {
        // TODO Auto-generated method stub


    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing()
    {
        // TODO Auto-generated method stub


    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue()
    {
        // TODO Auto-generated method stub
        return null;


    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        return getTableCellRendererComponent(
                   table, value, isSelected, false, row, column);

    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void removeCellEditorListener(CellEditorListener l)
    {
        // TODO Auto-generated method stub


    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(EventObject anEvent)
    {
        // TODO Auto-generated method stub
        return false;


    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing()
    {
        // TODO Auto-generated method stub
        return false;


    }




}
