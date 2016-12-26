/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

/**
 *
 */
public class ActionResourceAssociation extends MetaModelAssociation
{
    /**
     *
     */
    public ActionResourceAssociation(String shortName, ResourceType resourceType, ActionType actionType)
    {
        super(shortName);

        this.shortname = shortName;
        this.resourceType = resourceType;
        this.actionType = actionType;
    }

    private String shortname;

    public String getShortname()
    {
        return shortname;
    }

    public void setShortname(String name)
    {
        this.shortname = name;
    }

    private ResourceType resourceType;

    public ResourceType getResourceType()
    {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType)
    {
        this.resourceType = resourceType;
    }

    private ActionType actionType;

    public ActionType getActionType()
    {
        return actionType;
    }

    public void setActionType(ActionType actionType)
    {
        this.actionType = actionType;
    }
}
