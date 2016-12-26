package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * CompositeAction object instance interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface CompositeAction extends ch.ethz.infsec.secureumlgui.securemodel.secureuml.Action {
    /**
     * Returns the value of reference subactions.
     * @return Value of reference subactions. Element type: {@link ch.ethz.infsec.secureumlgui.securemodel.secureuml.Action}
     */
    public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.Action>*/ getSubactions();
}
