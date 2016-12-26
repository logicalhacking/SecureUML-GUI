package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * SubjectAssignment association proxy interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface SubjectAssignment extends javax.jmi.reflect.RefAssociation {
    /**
     * Queries whether a link currently exists between a given pair of instance
     * objects in the associations link set.
     * @param role Value of the first association end.
     * @param subject Value of the second association end.
     * @return Returns true if the queried link exists.
     */
    public boolean exists(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role role, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject subject);
    /**
     * Queries the instance objects that are related to a particular instance
     * object by a link in the current associations link set.
     * @param subject Required value of the second association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getRole(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject subject);
    /**
     * Queries the instance objects that are related to a particular instance
     * object by a link in the current associations link set.
     * @param role Required value of the first association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getSubject(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role role);
    /**
     * Creates a link between the pair of instance objects in the associations
     * link set.
     * @param role Value of the first association end.
     * @param subject Value of the second association end.
     */
    public boolean add(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role role, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject subject);
    /**
     * Removes a link between a pair of instance objects in the current associations
     * link set.
     * @param role Value of the first association end.
     * @param subject Value of the second association end.
     */
    public boolean remove(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Role role, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject subject);
}
