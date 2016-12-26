/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import java.util.Collection;

import ch.ethz.infsec.secureumlgui.Util;

/**
 *
 */
public class MofClassWrapper extends NamedModelElementWrapper
{
    /**
     *
     */
    public MofClassWrapper(Object modelElement)
    {
        super(modelElement);
    }

    public boolean isAbstract()
    {
        Boolean isAbstract = (Boolean)
                             Util.getProperty(getModelElement(), "abstract");
        return isAbstract.booleanValue();
    }

    public Collection getContents()
    {
        return (Collection)
               Util.getProperty(getModelElement(), "contents");
    }

    public Collection getSupertypes()
    {
        return (Collection)
               Util.getProperty(getModelElement(), "supertypes");
    }

//  public Collection allAttributes()
//  {
//    Collection allAttributes = (Collection)
//      Util.invokeParameterlessMethod(
//          getModelElement(), "refAllAttributes");
//
//    return allAttributes;
//  }

}
