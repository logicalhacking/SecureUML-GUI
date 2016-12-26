/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;


import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 *
 */
public class ModelElementWrapper
{
    /**
     *
     */
    public ModelElementWrapper(Object modelElement)
    {
        this.modelElement = modelElement;
    }

    MultiContextLogger logger = MultiContextLogger.getDefault();

    Object modelElement;

    /**
     * @return the secureModelElement
     */
    public Object getModelElement()
    {
        return modelElement;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj.getClass() == this.getClass())
        {
            ModelElementWrapper other = (ModelElementWrapper) obj;

            if(getModelElement() == null)
            {
                if(other.getModelElement() == null)
                    return true;
                else
                    return false;
            }
            else
            {
                if(other.getModelElement() == null)
                    return false;
                else
                {
                    boolean result = getModelElement()==other.getModelElement();

                    logger.info(this + " ?= "
                                + other + ": "+ result);

                    return result;
                }
            }
        }
        else
            return super.equals(obj);
    }



}
