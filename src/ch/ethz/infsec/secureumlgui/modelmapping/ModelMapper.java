package ch.ethz.infsec.secureumlgui.modelmapping;

import javax.jmi.reflect.RefPackage;

// import tudresden.ocl20.core.jmi.uml15.core.Association;
// import tudresden.ocl20.core.jmi.uml15.core.Generalization;
// import tudresden.ocl20.core.jmi.uml15.core.UmlAssociationClass;
// import tudresden.ocl20.core.jmi.uml15.core.UmlClass;

import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlAssociationClass;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;


/**
 * Base class that doesn't do much. The real functionality is in the subclasses.
 *
 * @version 1.0
 */
public class ModelMapper
{

    public ModelMapper()
    {
    }

    public void init()
    {
        map.clear();
    }

    protected static MultiContextLogger logger = new MultiContextLogger(MultiContextLogger.MODELMAPPER);

    protected ModelMap map = ModelMap.getDefault();

    @SuppressWarnings("unchecked")
    public void transform()
    {
        // empty
    }

    protected void examineUmlClass(UmlClass umlClass)
    {
        // empty
    }

    protected void examineUmlGeneralization(Generalization generalization)
    {
        // empty
    }

    protected void examineUmlAssociationClass(
        UmlAssociationClass associationClass)
    {
        // empty
    }

    protected void examineUmlAssociation(UmlAssociation assoc)
    {
        // empty
    }

    public ModelMap getModelMap()
    {
        return map;
    }
}
