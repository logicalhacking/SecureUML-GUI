/**
 *
 */
package ch.ethz.infsec.secureumlgui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import ch.ethz.infsec.secureumlgui.DialectMetamodelSelectedListener;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;


/**
 *
 */
public class SelectMetamodelActionListener implements ActionListener
{
    /**
     *
     */
    public SelectMetamodelActionListener(
        File xmiFile,
        DialectMetamodelSelectedListener selectionListener)
    {
        this.xmiFile = xmiFile;
        this.selectionListener = selectionListener;
    }

    File xmiFile;
    DialectMetamodelSelectedListener selectionListener;

    /**
     * @return the xmiFile
     */
    public File getXmiFile()
    {
        return xmiFile;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0)
    {
        selectionListener.dialectMetamodelSelected(xmiFile);




    }

}
