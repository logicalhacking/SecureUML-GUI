package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * Action object instance interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface Action { //extends javax.jmi.reflect.RefObject
    /**
     * Returns the value of attribute name.
     * @return Value of attribute name.
     */
    public java.lang.String getName();
    /**
     * Sets the value of name attribute. See {@link #getName} for description
     * on the attribute.
     * @param newValue New value to be set.
     */
    public void setName(java.lang.String newValue);
    /**
     * Returns the value of reference superActions.
     * @return Value of reference superActions. Element type: {@link ch.ethz.infsec.secureumlgui.securemodel.secureuml.CompositeAction}
     */
    public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.CompositeAction>*/ getSuperActions();
    /**
     * Returns the value of reference permission.
     * @return Value of reference permission. Element type: {@link ch.ethz.infsec.secureumlgui.securemodel.secureuml.Permission}
     */
    public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.Permission>*/ getPermission();
    /**
     * Returns the value of reference resource.
     * @return Value of reference resource.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.Resource getResource();
    /**
     * Sets the value of reference resource. See {@link #getResource} for description
     * on the reference.
     * @param newValue New value to be set.
     */
    public void setResource(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Resource newValue);
}
