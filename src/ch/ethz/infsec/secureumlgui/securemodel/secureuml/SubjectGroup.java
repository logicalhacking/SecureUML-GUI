package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * SubjectGroup association proxy interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface SubjectGroup extends javax.jmi.reflect.RefAssociation {
    /**
     * Queries whether a link currently exists between a given pair of instance
     * objects in the associations link set.
     * @param members Value of the first association end.
     * @param group Value of the second association end.
     * @return Returns true if the queried link exists.
     */
    public boolean exists(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Group members, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject group);
    /**
     * Queries the instance objects that are related to a particular instance
     * object by a link in the current associations link set.
     * @param group Required value of the second association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getMembers(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject group);
    /**
     * Queries the instance objects that are related to a particular instance
     * object by a link in the current associations link set.
     * @param members Required value of the first association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getGroup(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Group members);
    /**
     * Creates a link between the pair of instance objects in the associations
     * link set.
     * @param members Value of the first association end.
     * @param group Value of the second association end.
     */
    public boolean add(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Group members, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject group);
    /**
     * Removes a link between a pair of instance objects in the current associations
     * link set.
     * @param members Value of the first association end.
     * @param group Value of the second association end.
     */
    public boolean remove(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Group members, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Subject group);
}
