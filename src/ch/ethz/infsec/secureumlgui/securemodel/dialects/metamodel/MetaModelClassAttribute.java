/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

import ch.ethz.infsec.secureumlgui.Util;



/**
 *
 */
public class MetaModelClassAttribute extends MetaModelEntity
{
    /**
     *
     */
    public MetaModelClassAttribute(String name)
    {
        super(name);
    }

//    /**
//     * @return The name of the Getter Method of this Attribute
//     */
//    public String getUmlGetterName()
//    {
//      String getterPrefix;
//      if(getTypeName().equals(MetaModelConst.TYPE_NAME_BOOLEAN))
//      {
//        getterPrefix = "is";
//      }
//      else
//      {
//        getterPrefix = "get";
//      }
//      if(getUmlName() != null && getUmlName().length() > 0)
//          return getterPrefix + Util.capitalize(getUmlName());
//      else if(getName() != null && getName().length() > 0)
//         return getterPrefix + Util.capitalize(getName());
//      else
//          return null;
//    }
//    /**
//     * @return The name of the Setter Method of this Attribute
//     */
//    public String getSetterName()
//    {
//        return "set" + Util.capitalize(getName());
//    }

    private String umlName;

    public String getUmlName()
    {
        return umlName;
    }

    public void setUmlName(String umlName)
    {
        this.umlName = umlName;
    }


    private String typeName;

    /** Name of the Type of the Attribute (e.g. 'String')
     *
     * @return the name of the type of the attribute
     */
    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName() + " : " + getTypeName();

    }

}
