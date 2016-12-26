package ch.ethz.infsec.secureumlgui.securemodel.secureuml;

/**
 * ActionHierarchy association proxy interface.
 *
 * <p><em><strong>Note:</strong> This type should not be subclassed or implemented
 * by clients. It is generated from a MOF metamodel and automatically implemented
 * by MDR (see <a href="http://mdr.netbeans.org/">mdr.netbeans.org</a>).</em></p>
 */
public interface ActionHierarchy extends javax.jmi.reflect.RefAssociation {
    /**
     * Queries whether a link currently exists between a given pair of instance
     * objects in the associations link set.
     * @param superActions Value of the first association end.
     * @param subactions Value of the second association end.
     * @return Returns true if the queried link exists.
     */
    public boolean exists(ch.ethz.infsec.secureumlgui.securemodel.secureuml.CompositeAction superActions, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Action subactions);
    /**
     * Queries the instance objects that are related to a particular instance
     * object by a link in the current associations link set.
     * @param subactions Required value of the second association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getSuperActions(ch.ethz.infsec.secureumlgui.securemodel.secureuml.Action subactions);
    /**
     * Queries the instance objects that are related to a particular instance
     * object by a link in the current associations link set.
     * @param superActions Required value of the first association end.
     * @return Collection of related objects.
     */
    public java.util.Collection getSubactions(ch.ethz.infsec.secureumlgui.securemodel.secureuml.CompositeAction superActions);
    /**
     * Creates a link between the pair of instance objects in the associations
     * link set.
     * @param superActions Value of the first association end.
     * @param subactions Value of the second association end.
     */
    public boolean add(ch.ethz.infsec.secureumlgui.securemodel.secureuml.CompositeAction superActions, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Action subactions);
    /**
     * Removes a link between a pair of instance objects in the current associations
     * link set.
     * @param superActions Value of the first association end.
     * @param subactions Value of the second association end.
     */
    public boolean remove(ch.ethz.infsec.secureumlgui.securemodel.secureuml.CompositeAction superActions, ch.ethz.infsec.secureumlgui.securemodel.secureuml.Action subactions);
}
