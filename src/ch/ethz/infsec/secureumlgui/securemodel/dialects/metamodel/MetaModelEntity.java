/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

/**
 *
 */
public class MetaModelEntity
{
    /**
     *
     */
    public MetaModelEntity()
    {

    }

    /**
     *
     */
    public MetaModelEntity(String name)
    {
        this.name = name;
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

}
