/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;


import ch.ethz.infsec.secureumlgui.Util;

/**
 *
 */
public class NamedModelElementWrapper extends ModelElementWrapper
{
    /**
     *
     */
    public NamedModelElementWrapper(Object modelElement)
    {
        super(modelElement);
    }


    public String getName()
    {
        if (getModelElement() == null) logger.error(" null in getName()");
        try
        {
            return (String) Util.getProperty(getModelElement(), "name");
        }
        catch (Exception e)
        {
            logger.error(" cannot read Property 'name' on Object "
                         + getModelElement().getClass().getSimpleName());
            return null;
        }
    }

    public void setName(String name)
    {
        try
        {
            Util.setProperty(modelElement, "name", name);
        }
        catch (Exception e)
        {
            logger.error(" cannot write Property 'name' on Object "
                         + modelElement.getClass().getSimpleName());
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName();
    }

}
