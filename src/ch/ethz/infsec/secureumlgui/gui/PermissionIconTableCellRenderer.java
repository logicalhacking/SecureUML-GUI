/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import ch.ethz.infsec.secureumlgui.modelmapping.permissions.PermissionValue;

/**
 *
 */
public class PermissionIconTableCellRenderer
    implements TableCellRenderer
{
    public Component getTableCellRendererComponent(
        JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int row, int column)
    {
        //Container container = new JPanel();

        if (value instanceof PermissionValue)
        {
            PermissionValue pv = (PermissionValue) value;

            return new PermissionIconTableCellRendererComponent(pv);
        }
        else
            return new JLabel("error");
    }
}
