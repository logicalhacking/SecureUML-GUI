package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * Permission object instance interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface Permission extends javax.jmi.reflect.RefObject {
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
     * Returns the value of reference action.
     * @return Value of reference action. Element type: {@link ch.ethz.infsec.secureumlgui.securemodel.secureuml.Action}
     */
    public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.Action>*/ getAction();
    /**
     * Returns the value of reference role.
     * @return Value of reference role.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role getRole();
    /**
     * Sets the value of reference role. See {@link #getRole} for description
     * on the reference.
     * @param newValue New value to be set.
     */
    public void setRole(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role newValue);
    /**
     * Returns the value of reference authorizationConstraint.
     * @return Value of reference authorizationConstraint.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.AuthorizationConstraint getAuthorizationConstraint();
    /**
     * Sets the value of reference authorizationConstraint. See {@link #getAuthorizationConstraint}
     * for description on the reference.
     * @param newValue New value to be set.
     */
    public void setAuthorizationConstraint(ch.ethz.infsec.secureumlgui.securemodel.secureuml.AuthorizationConstraint newValue);
}
