package ch.ethz.infsec.secureumlgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectModelMapper;

import java.io.File;
import javax.jmi.reflect.RefPackage;
import javax.swing.JFileChooser;

import org.netbeans.api.xmi.XMIWriter;
import org.netbeans.api.xmi.XMIWriterFactory;

import java.io.FileOutputStream;

/**
 * ActionListener for the Write Xmi Menu item (FIXME: move to proper
 * place).  This is a hack at the moment. We just write the current
 * contents of the repository. We should be more careful here to make
 * sure that really everything gets mapped. I.e., transform with a
 * "MapAll" strategie into a separate extent, and write from there.

 * @version 1.0
 */
// FIXME (JD): this can be made an inner class of SecureUmlModule?
public class WriteXmiActionListener implements ActionListener {


    JFileChooser fc;

    WriteXmiActionListener() {
        fc = new JFileChooser();
    }

    /**
     * Responds to the <code>ActionEvents</code> from the module menu.
     *
     * @param event the <code>ActionEvent</code> to be handled.
     */
    public final void actionPerformed(final ActionEvent event) {
        int val = fc.showSaveDialog(null);

        if (val == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            RefPackage model = (RefPackage) GenericDialectModelMapper.getInstance().dialectMetaModelInfo.getDialectExtent();
            //System.out.print(mof);

            XMIWriter writer = XMIWriterFactory.getDefault().createXMIWriter();
            try {
                FileOutputStream out = new FileOutputStream(file);
                writer.write(out, model, null);
            } catch (Exception e) {
                System.out.println("Fatal error writing XMI.");
                e.printStackTrace();
            }
        }
    }
}
