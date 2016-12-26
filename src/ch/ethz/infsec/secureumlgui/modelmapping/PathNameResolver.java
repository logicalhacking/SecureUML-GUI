package ch.ethz.infsec.secureumlgui.modelmapping;

import java.util.Collection;

import org.argouml.model.Model;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.behavioralelements.statemachines.StateMachine;
import org.omg.uml.behavioralelements.statemachines.Transition;

//import tudresden.ocl20.core.MetaModelConst.MetaModelInfo;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;

@Deprecated
public class PathNameResolver {

    private static MultiContextLogger logger = new MultiContextLogger();




    /**
     * Finds an element with the given name inside its containing
     * object.
     *
     * @param elem
     * @param name
     * @return the found element
     */
    @SuppressWarnings("unchecked")
    private static ModelElement findInAnchor(ModelElement elem, String name) {
        if (elem instanceof Classifier) {
            Classifier anchor = (Classifier) elem;
            // search attributes

            /* TODO: inserted manually 5 */
            Collection attributes =  Model.getCoreHelper().getAllAttributes(anchor);
            ModelElement result = find(attributes, name);
            //ModelElement result = find(anchor.allAttributes(), name);
            if (result != null) {
                return result;
            }
            // search operations
            /* TODO: inserted manually 6 */
            Collection operations =  Model.getFacade().getOperations(anchor);
            result = find(operations, name);

            //result = find(anchor.allOperations(), name);

            if (result != null) {
                return result;
            }
            // search statemachine states and transitions
            for (ModelElement ownedElem : (Collection<ModelElement>) anchor
                    .getOwnedElement()) {
                if (ownedElem instanceof StateMachine) {
                    StateMachine sm = (StateMachine) ownedElem;
                    for (Transition t : (Collection<Transition>) sm
                            .getTransitions()) {
                        // search transitions
                        // transitions are addressed by their trigger (event) name
                        if (t.getTrigger() != null && t.getTrigger().getName().equals(name)) {
                            return t;
                        }
                        // search states
                        if (t.getSource() != null &&t.getSource().getName().equals(name)) {
                            return t.getSource();
                        }
                        if (t.getTarget() != null && t.getTarget().getName().equals(name)) {
                            return t.getTarget();
                        }
                    }
                }
            }
        } else {
            logger.error("unknown anchor type "
                         + elem.getClass().getSimpleName());
        }
        return null;
    }

    private static ModelElement find(Collection<ModelElement> elems, String name) {
        for (ModelElement elem : elems) {
            if (elem.getName().equals(name)) {
                return elem;
            }
        }
        return null;
    }

    /**
     * Find a resource in a modelelement according to the given pathName.
     *
     * @param pathName
     * @param anchor
     * @return the found element
     */
    @SuppressWarnings("unchecked")
    public static ModelElement resolve(String pathName, ModelElement anchor) {
        String[] components = pathName.split("\\.");
        if (components.length == 0) {
            logger.error("invalid path expression: " + pathName);
            return null;
        }
        int pathOffset = 0;
        if (components[pathOffset].equals(anchor.getName())) {
            if (components.length == 1) {
                // path name denotes the anchor, return it
                return anchor;
            } else {
                // path name has an anchor prefix, skip it
                pathOffset = 1;
            }
        }
        ModelElement result = anchor;
        // follow path to find resource
        while (pathOffset < components.length) {
            result = findInAnchor(result, components[pathOffset]);
            pathOffset++;
        }
        if (result == null) {
            logger.error("invalid path expression: " + pathName);
        }
        return result;
    }

}
