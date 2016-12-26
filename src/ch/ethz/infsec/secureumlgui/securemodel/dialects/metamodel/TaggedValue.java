/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

/**
 *
 */
public class TaggedValue extends MetaModelEntity
{
    /**
     *
     */
    public TaggedValue(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String value;

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}
