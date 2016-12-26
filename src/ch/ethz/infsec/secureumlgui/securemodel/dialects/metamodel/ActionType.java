/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

/**
 *
 */
public class ActionType extends MetaModelEntity
{
    /**
     *
     */
    protected ActionType()
    {

    }

    private String shortName = null;

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    private String subactionsDefinition = null;

    public String getSubactionsDefinition()
    {
        return subactionsDefinition;
    }

    public void setSubactionsDefinition(String subactionsDefinition)
    {
        this.subactionsDefinition = subactionsDefinition;
    }


    public String toString()
    {
        return getShortName();
    }
}
