/**
 *
 */
package ch.ethz.infsec.secureumlgui.modelmapping.strategies;

import java.util.LinkedHashSet;
import java.util.Set;

import org.omg.uml.foundation.core.ModelElement;

/**
 *
 * Mapping only the ModelElement itself - too simple.
 * This way, composite actions cannot be considered
 */
public class MapSelf extends MappingScopeStrategy
{
    /**
     * @see ch.ethz.infsec.secureumlgui.modelmapping.strategies.MappingScopeStrategy#getMappingScope(org.omg.uml.foundation.core.ModelElement)
     *
     * @return Collection containing the @param modelElement as the only Member
     */
    @Override
    public Set<ModelElement> getMappingScope(ModelElement modelElement)
    {
        LinkedHashSet<ModelElement> result = new LinkedHashSet<ModelElement>();

        result.add(modelElement);

        return result;
    }
}
