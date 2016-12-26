/**
 *
 */
package ch.ethz.infsec.secureumlgui.modelmapping.strategies;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.argouml.model.Model;
import org.omg.uml.foundation.core.ModelElement;


/**
 *
 */
public class SecureUmlMappingScopeStrategy
    extends MappingScopeStrategy
{

    /* (non-Javadoc)
     * @see ch.ethz.infsec.secureumlgui.modelmapping.MappingScopeStrategy#getMappingScope(tudresden.ocl20.core.jmi.uml15.core.ModelElement)
     */
    @Override
    public Set<ModelElement> getMappingScope(
        ModelElement startingPoint)
    {
        //if(modelElement != null)
        {
            // TODO: search for the Roles / Permissions to lie in Namespaces, too
            Set<ModelElement> result =  super.getMappingScope(startingPoint);

            //Collection elementsInNamespace = modelElement.getNamespace().getOwnedElement();


            //    logger.info("There are " + elementsInNamespace.size()
            //        + " ModelElements in the Namespace where the Permissions / Roles lie");
            //
            //    for (Iterator iter = elementsInNamespace.iterator(); iter.hasNext();)
            //    {
            //
            //
            //      ModelElement m = (ModelElement) iter.next();
            //
            //      logger.info(" - " + m.getName());
            //
            //    }


            return result;
        }
        //else return null;
    }

}
