package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * SecureUML package interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface SecureUmlPackage extends javax.jmi.reflect.RefPackage {
    /**
     * Returns Action class proxy object.
     * @return Action class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.ActionClass getAction();
    /**
     * Returns Group class proxy object.
     * @return Group class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.GroupClass getGroup();
    /**
     * Returns User class proxy object.
     * @return User class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.UserClass getUser();
    /**
     * Returns Subject class proxy object.
     * @return Subject class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.SubjectClass getSubject();
    /**
     * Returns CompositeAction class proxy object.
     * @return CompositeAction class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.CompositeActionClass getCompositeAction();
    /**
     * Returns AtomicAction class proxy object.
     * @return AtomicAction class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.AtomicActionClass getAtomicAction();
    /**
     * Returns AuthorizationConstraint class proxy object.
     * @return AuthorizationConstraint class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.AuthorizationConstraintClass getAuthorizationConstraint();
    /**
     * Returns Resource class proxy object.
     * @return Resource class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.ResourceClass getResource();
    /**
     * Returns Permission class proxy object.
     * @return Permission class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.PermissionClass getPermission();
    /**
     * Returns Role class proxy object.
     * @return Role class proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.RoleClass getRole();
    /**
     * Returns ActionHierarchy association proxy object.
     * @return ActionHierarchy association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.ActionHierarchy getActionHierarchy();
    /**
     * Returns ActionResourceAssignment association proxy object.
     * @return ActionResourceAssignment association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.ActionResourceAssignment getActionResourceAssignment();
    /**
     * Returns PermissionActionAssignment association proxy object.
     * @return PermissionActionAssignment association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.PermissionActionAssignment getPermissionActionAssignment();
    /**
     * Returns SubjectAssignment association proxy object.
     * @return SubjectAssignment association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.SubjectAssignment getSubjectAssignment();
    /**
     * Returns RoleHierarchy association proxy object.
     * @return RoleHierarchy association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.RoleHierarchy getRoleHierarchy();
    /**
     * Returns SubjectGroup association proxy object.
     * @return SubjectGroup association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.SubjectGroup getSubjectGroup();
    /**
     * Returns ConstraintAssignment association proxy object.
     * @return ConstraintAssignment association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.ConstraintAssignment getConstraintAssignment();
    /**
     * Returns PermissionAssignment association proxy object.
     * @return PermissionAssignment association proxy object.
     */
    public ch.ethz.infsec.secureumlgui.securemodel.secureuml.PermissionAssignment getPermissionAssignment();
}
