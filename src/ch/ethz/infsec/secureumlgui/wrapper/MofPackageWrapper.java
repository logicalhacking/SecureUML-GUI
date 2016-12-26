/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import java.util.Collection;

import ch.ethz.infsec.secureumlgui.Util;

/**
 *
 */
public class MofPackageWrapper extends NamedModelElementWrapper
{
    /**
     *
     */
    public MofPackageWrapper(Object modelElement)
    {
        super(modelElement);
    }

    public Collection getContents()
    {
        return (Collection)
               Util.getProperty(getModelElement(), "contents");
    }
}
