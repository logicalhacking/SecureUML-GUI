/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import ch.ethz.infsec.secureumlgui.Util;

/**
 *
 */
public class AttributeWrapper extends NamedModelElementWrapper
{
    /**
     *
     */
    public AttributeWrapper(Object modelElement)
    {
        super(modelElement);
    }

    public Object getType()
    {
        return Util.getProperty(getModelElement(), "type");
    }

    public String getName()
    {
        return
            Util.getProperty(getModelElement(), "name").toString();
    }

}
