/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

import ch.ethz.infsec.secureumlgui.Util;

/**
 *
 */
public class AssociationEnd extends MetaModelEntity
{
    public AssociationEnd()
    {

    }
    /**
     *
     */
    public AssociationEnd(String name)
    {
        this.setName(name);
    }


    private boolean multiple;

    public boolean isMultiple()
    {
        return multiple;
    }

    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }

    private InterResourceAssociation owner;

    public InterResourceAssociation getOwner()
    /** The Association the AssociationEnd belongs to
     *
     */
    {
        return owner;
    }

    public void setOwner(InterResourceAssociation association)
    {
        this.owner = association;
    }


    private MetaModelClass type;

    public MetaModelClass getType()
    /** The Classifier the Association End
     * is attached to
     *
     */
    {
        return type;
    }

    public void setType(MetaModelClass type)
    {
        this.type = type;
    }

    private String umlPropertyGetter;

    public String getUmlPropertyGetter()
    {
        return umlPropertyGetter;
    }

    public void setUmlPropertyGetter(String umlPropertyGetter)
    {
        this.umlPropertyGetter = umlPropertyGetter;
    }


    /**
     * @return The name of the Getter Method of this AssociationName
     * (for  the SecureUML Model)
     */
    public String getGetterName()
    {
        if(getName() != null && getName().length() > 0)
            return "get" + Util.capitalize(getName());
        else
            return null;
    }

    /**
     * @return The name of the Setter Method of this AssociationEnd
     * (for  the SecureUML Model)
     */
    public String getSetterName()
    {
        if(isMultiple())
            // AssociationEnd is a Collection -> no Setter
            return null;
        else
            return "set" + Util.capitalize(getName());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String string = "";
        if(isMultiple())
            string = string + "(*)";
        else
            string = string + "(1)";

        string = string
                 + "(" + getName() + ") ["
                 + getUmlPropertyGetter()
                 + "]" ;

        return string;
    }
}
