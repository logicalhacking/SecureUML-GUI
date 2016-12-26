/**
 *
 */
package ch.ethz.infsec.secureumlgui.gui;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.openide.util.datatransfer.ExTransferable.Multi;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelEntity;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.wrapper.PermissionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.ResourceWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RoleWrapper;


/**
 * Abstract base class for all SecureUML properties components to be
 * displayed in the Details Pane.
 *
 * Holds stuff that is common to all SecureUml components, but
 * independent of the type of the resource that is displayed.
 *
 * {@link ch.ethz.infsec.secureumlgui.TabSecureUml} displays instances
 * of this class based on the selected model element.
 *
 *
 */
public abstract class AbstractSecureUmlComponent
    extends JPanel
{
    /**
     *
     */
    public AbstractSecureUmlComponent()
    {
        this.setLayout(new BorderLayout());
    }

    public void setDisplayedSecureUmlElement(
        Object suElement, ResourceType rt)
    {

    }

    /**
     *
     */
    private void initLayout()
    {
    }

    // public AbstractSecureUmlComponent(String titl)
    // {
    //   initLayout();
    //   //initBorder();
    //   //setTitle(title);
    // }


    MultiContextLogger logger = new MultiContextLogger(
        MultiContextLogger.GUI);


    LayoutManager defaultLayout;

    // public LayoutManager getDefaultLayout()
    // {
    //     return defaultLayout;
    // }

}
