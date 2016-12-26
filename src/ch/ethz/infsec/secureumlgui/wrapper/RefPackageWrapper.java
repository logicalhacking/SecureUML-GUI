/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelConst;

/**
 *
 */
public class RefPackageWrapper extends ModelElementWrapper
{
    /**
     *
     */
    public RefPackageWrapper(Object modelElement)
    {
        super(modelElement);
    }

    public String getName()
    {
        String name = getModelElement().getClass().getSimpleName();

        name = name.split(MetaModelConst.MDR_IMPL_SUFFIX)[0];

        return name;
    }

//  public Collection getContents()
//  {
//    Object contents =
//      Util.getProperty(getModelElement(), "contents");
//
//    return (Collection) contents;
//  }
//
//  public Object getContainer()
//  {
//    Object container =
//      Util.getProperty(getModelElement(), "container");
//
//    return container;
//  }

    public Collection allClasses()
    {
        Collection allClasses = (Collection)
                                Util.invokeParameterlessMethod(
                                    modelElement,
                                    "refAllClasses");

        return allClasses;
    }

    public Collection allAssociations()
    {
        Collection allAssociations = (Collection)
                                     Util.invokeParameterlessMethod(
                                         modelElement,
                                         "refAllAssociations");

        return allAssociations;
    }

    public Collection allPackages()
    {
        Collection allPackages = (Collection)Util.invokeParameterlessMethod(
                                     modelElement,
                                     "refAllPackages");

        return allPackages;
    }

    public Collection getContents()
    {

        Collection contents = new LinkedList();

        Util.addAllSave(contents, allClasses());
        Util.addAllSave(contents, allAssociations());
        Util.addAllSave(contents, allPackages());

        for (Iterator iter = allPackages().iterator(); iter.hasNext();)
        {
            Object pkg = (Object) iter.next();

            RefPackageWrapper pkgWrapper = new RefPackageWrapper(pkg);
            Collection pkgContents = pkgWrapper.getContents();

            Util.addAllSave(contents, pkgContents);
        }

        return contents;
    }

}
