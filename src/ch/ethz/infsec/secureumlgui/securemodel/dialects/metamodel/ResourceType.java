/**
*
*/
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 *
 */
public class ResourceType extends MetaModelClass
{
    /* ResourceType is directly derived from the SecureUML
     * Class "Resource" <==> parentResourceType == null */
    private ResourceType parentResourceType;

    public ResourceType getParentResourceType()
    {
        return parentResourceType;
    }

    public void setParentResourceType(ResourceType parentResourceType)
    {
        this.parentResourceType = parentResourceType;
    }

    private String modelElementStereotype;

    public String getModelElementStereotype()
    {
        return modelElementStereotype;
    }

    public void setModelElementStereotype(String anchorStereotype)
    {
        this.modelElementStereotype = anchorStereotype;
    }

    private String anchorPath;

    public String getAnchorPath()
    {
        return anchorPath;
    }

    public void setAnchorPath(String anchorPath)
    {
        this.anchorPath = anchorPath;
    }

    private String actionStereotype;

    public String getActionStereotype()
    {
        return actionStereotype;
    }

    public void setActionStereotype(String actionStereotype)
    {
        this.actionStereotype = actionStereotype;
    }

    private String resourcePath;

    public String getResoucePath()
    {
        return resourcePath;
    }

    public void setResoucePath(String resourcePath)
    {
        this.resourcePath = resourcePath;
    }



    public String toString()
    {
        String resourceString = this.getName();
        if(this.getParentResourceType() != null)
        {
            resourceString = resourceString
                             + " inherit "
                             + this.getParentResourceType().getName();
        }
        resourceString = resourceString
                         + "\n Tags: "
                         + "\n umlClassName: " + getUmlClassName()
                         + "\n modelelementStereotype: " + getModelElementStereotype()
                         + "\n anchorPath: " + getAnchorPath()
                         + "\n actionStereotype: " + getActionStereotype()
                         + "\n resourcePath: " + getResoucePath();


        resourceString = resourceString + "\n Attributes:";
        for (Iterator iter = getAttributes().iterator(); iter.hasNext();)
        {
            MetaModelClassAttribute rta = (MetaModelClassAttribute) iter.next();

            resourceString = resourceString + "\n  - " + rta.toString();
        }

        return resourceString;
    }

}
