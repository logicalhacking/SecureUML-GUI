/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.omg.uml.foundation.core.UmlClass;

import ch.ethz.infsec.secureumlgui.ModuleController;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.ActionPermissionSet;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;

/**
 *
 */
public class ActionPermissionTableCellRenderer
    implements TableCellEditor,
    TableCellRenderer
{

    //private PolicyWrapper currentPolicy;
    private UmlClass currentPolicy;
    /**
     *
     */
//  public ActionPermissionTableCellRenderer(PolicyWrapper currentPolicy) {
//	  this.currentPolicy = currentPolicy;
//  }
    public ActionPermissionTableCellRenderer(UmlClass currentPolicy) {
        this.currentPolicy = currentPolicy;
    }

    MultiContextLogger logger =
        MultiContextLogger.getDefault();

//  static int globalN = 0;
//  int n;

    ActionPermissionsTableCellRendererComponent
    container = null;

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(
        JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int row, int column)
    {


        if (value instanceof ActionPermissionSet)
        {
            ActionPermissionSet actionPermissions =
                (ActionPermissionSet) value;

            container = new
            ActionPermissionsTableCellRendererComponent(
                actionPermissions, new PolicyWrapper(ModuleController.getInstance().getModelMap().getElement(currentPolicy)));

            return container;


        }
        else
            return new JLabel("error");

    }


    /* (non-Javadoc)
     * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        // TODO Auto-generated method stub
        //return super.getTableCellEditorComponent(table, value, isSelected, row, column);

        return getTableCellRendererComponent(
                   table, value,
                   isSelected, false,
                   row, column);
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
        if(container.getCbExplicitPermission().hasFocus())
            return !(container.getCbExplicitPermission().isSelected());
        else
            return container.getCbExplicitPermission().isSelected();

//    if(cbExplicitPermission.hasFocus())
//      return !(cbExplicitPermission.isSelected());
//    else
//      return cbExplicitPermission.isSelected();
        // TODO Auto-generated method stub

        //return null;


    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        // TODO Auto-generated method stub
        return true;


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
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing()
    {
        //return cbExplicitPermission.hasFocus();
        // TODO Auto-generated method stub
        return true;
    }



}
