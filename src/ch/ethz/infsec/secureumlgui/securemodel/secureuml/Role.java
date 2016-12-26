package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * Role object instance interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface Role { //extends javax.jmi.reflect.RefObject
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
     * Returns the value of reference subject.
     * @return Value of reference subject. Element type: {@link ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject}
     */
    public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.Subject>*/ getSubject();
    /**
     * Returns the value of reference superroles.
     * @return Value of reference superroles. Element type: {@link ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role}
     */
    public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.Role>*/ getSuperroles();
    /**
     * Returns the value of reference permission.
     * @return Value of reference permission.
     */
    /* changed manually 69 */
    public java.util.Collection /*<org.argouml.ui.secureuml.securemodel.secureuml.Permission>*/
    getPermission();
    /* uncommented manually 69 */
//    /**
//     * Sets the value of reference permissions. See {@link #getPermission} for
//     * description on the reference.
//     * @param newValue New value to be set.
//     */
//    public void setPermission(org.argouml.ui.secureuml.securemodel.secureuml.Permission newValue);
    /**
     * Returns the value of reference subroles.
     * @return Value of reference subroles. Element type: {@link ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role}
     */
    public java.util.Collection/*<org.argouml.ui.secureuml.securemodel.secureuml.Role>*/ getSubroles();
}
