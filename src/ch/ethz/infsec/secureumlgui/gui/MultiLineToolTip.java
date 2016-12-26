/**
 *  http://www.codeguru.com/java/articles/122.shtml
 *
 *  Author: Zafir Anjum
 */
package ch.ethz.infsec.secureumlgui.gui;

import javax.swing.*;
import javax.swing.plaf.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.*;


/**
 * @author Zafir Anjum
 */


public class MultiLineToolTip extends JToolTip
{
    private static final String uiClassID = "ToolTipUI";

    String tipText;
    JComponent component;

    public MultiLineToolTip() {
        updateUI();
    }

    public void updateUI() {
        setUI(MultiLineToolTipUI.createUI(this));
    }

    public void setColumns(int columns)
    {
        this.columns = columns;
        this.fixedwidth = 0;
    }

    public int getColumns()
    {
        return columns;
    }

    public void setFixedWidth(int width)
    {
        this.fixedwidth = width;
        this.columns = 0;
    }

    public int getFixedWidth()
    {
        return fixedwidth;
    }

    protected int columns = 20;
    protected int fixedwidth = 20;
}
