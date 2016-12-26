/**
 *
 */
package ch.ethz.infsec.secureumlgui.modelmapping.strategies;

import java.util.LinkedHashSet;
import java.util.Set;

import org.omg.uml.foundation.core.ModelElement;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 *
 */
public abstract class MappingScopeStrategy
{

    /** Method returns a Collection containing all ModelElements
     * that need to be mapped when analyzing Permissions on the
     * ModelElement @param modelElement
     *
     *
     * @param startingPoint The analyzed ModelElement
     * @return All ModelElements that need to be mapped
     */
    public Set<ModelElement> getMappingScope(ModelElement startingPoint)
    {
        LinkedHashSet<ModelElement> result =
            new LinkedHashSet<ModelElement>();

        return result;
    }


    MultiContextLogger logger = MultiContextLogger.getDefault();
}
