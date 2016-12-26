/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A Metamodel Class which corresponds t an UML Class
 *
 * the corresponding UMLClass needs to be specified in the
 * Metamodel by the Tag 'umlClassName'
 *
 *
 */
public class MetaModelClass extends MetaModelEntity
{

    private String umlClassName;

    public String getUmlClassName()
    {
        return umlClassName;
    }

    public void setUmlClassName(String umlClassName)
    {
        this.umlClassName = umlClassName;
    }

    private Collection<MetaModelClassAttribute> attributes =
        new LinkedList<MetaModelClassAttribute>();

    public Collection<MetaModelClassAttribute> getAttributes()
    {
        return attributes;
    }

//  public void setAttributes(Collection<ResourceTypeAttribute> attributes)
//  {
//      this.attributes = attributes;
//  }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String resourceString = getName();

        resourceString = resourceString
                         + "\n umlClassName: " + getUmlClassName();

        resourceString = resourceString + "\n Attributes: \n";
        for (Iterator iter = getAttributes().iterator(); iter.hasNext();)
        {
            MetaModelClassAttribute rta = (MetaModelClassAttribute) iter.next();

            resourceString = resourceString + "  - " + rta.toString() + "\n";
        }

        return resourceString;
    }

}
