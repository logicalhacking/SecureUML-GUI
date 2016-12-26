/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.omg.uml.foundation.core.ModelElement;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;



/**
 *
 */
public class ResourceWrapper extends NamedModelElementWrapper
{
    /**
     *
     */
    public ResourceWrapper(Object secureModelElement)
    {
        super(secureModelElement);
    }

    public Collection getAction()
    {
        Object action =
            Util.getProperty(getModelElement(), "action");
        return (Collection) action;
    }

    public Collection<ActionWrapper> getActionWrapper()
    {
        Collection actions = getAction();

        Collection<ActionWrapper> result =
            new LinkedList<ActionWrapper>();

        if(actions != null)
        {
            for (Iterator iter = actions.iterator(); iter.hasNext();)
            {
                Object action = (Object) iter.next();

                result.add(ActionWrapper.createActionWrapper(action));
            }
        }

        return result;
    }

    public String getResourcePath()
    {
        String resourcePath = "";

        Object suResource = getModelElement();
        if(suResource != null)
        {
            ModelElement umlResource = (ModelElement)
                                       ModelMap.getDefault().getUmlElement(suResource);
            ModelElement anchor =
                GenericDialectHelper.getInstance().
                findAnchor(umlResource);

            resourcePath =
                GenericDialectHelper.getInstance().getResourcePath();
        }

        return resourcePath;
    }


    @Override
    public String toString()
    {

        if(getModelElement() == null)// || getName() == null)
            return "";
        return getModelElement().toString();//getName();
//    else
//    {
//      GenericDialectHelper helper = GenericDialectHelper.getInstance();
//      Object suElement = getModelElement();
//
//      ModelElement umlElement = (ModelElement)
//        ModelMap.getDefault().getUmlElement(suElement);
//      helper.findAnchor(umlElement);
//
//      if(umlElement != null)
//        return helper.getResourcePath();
//      else
//        return this.getName();
//    }
    }


}
