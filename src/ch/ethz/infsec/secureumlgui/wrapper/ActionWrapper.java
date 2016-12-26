/**
 *
 */
package ch.ethz.infsec.secureumlgui.wrapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;


/**
 *
 */
public class ActionWrapper extends NamedModelElementWrapper
{
    /**
     *
     */
    public ActionWrapper(Object secureModelElementWrapper)
    {
        super(secureModelElementWrapper);
    }

    static MultiContextLogger logger = MultiContextLogger.getDefault();

    /* creates either
     * an AtomicActionWrapper
     * (if the argument is a direct descendant
     *  of Type with name 'AtomicAction')
     *
     * or a CompositeActionWrapper Instance
     * (if the argument is a direct descendant
     *  of Type with name 'CompositeAction')
     *
     *  and 'null' otherwise
     */

    public static ActionWrapper createActionWrapper(Object secureModelElement)
    {
        if(secureModelElement == null)
        {
            logger.error("createActionWrapper(null) -> cannot create");
            return null;
        }
//    String actionSuperclassName =
//      secureModelElement.getClass().getSuperclass().
//        getSimpleName();

        else {
            logger.info("create action wrapper for "+secureModelElement);

            return new ActionWrapper(secureModelElement);
        }
//    if(Util.isInstanceof(secureModelElement.getClass(), "AtomicAction"))
//        //actionSuperclassName.equals("AtomicAction"))
//    {
//      return new AtomicActionWrapper(secureModelElement);
//    }
//    else if(Util.isInstanceof(secureModelElement.getClass(), "CompositeAction"))
//        //actionSuperclassName.equals("CompositeAction"))
//    {
//      return new CompositeActionWrapper(secureModelElement);
//    }
//    else
//    {
//      logger.error("cannot create ActionWrapper, action has type: " + secureModelElement.getClass()
//          + ", with invalid (non Action) supertype: " + secureModelElement.getClass().getSuperclass());
//    }
//      return null;
    }

    public Object getResource()
    {
        Object resource =
            Util.getProperty(getModelElement(), "resource");
        return resource;
    }
    public void setResourceWrapper(Object resource)
    {
        Util.setProperty(getModelElement(), "resource", resource);
    }

    public ResourceWrapper getResourceWrapper()
    {
        Object resource = null;
        try {
            resource =
                Util.getProperty(getModelElement(), "resource");
        } catch (Exception e) {
            logger.error("could not get Resource for "+getModelElement());
        }

        return new ResourceWrapper(resource);
    }

    public Collection getSuperActions()
    {
        return (Collection)
               Util.getProperty(getModelElement(), "superactions");
    }

    public Set<ActionWrapper> getSuperActionWrappers()
    {
        Collection actions = getSuperActions();

        Set<ActionWrapper> actionWrappers = new LinkedHashSet<ActionWrapper>();

        for (Iterator iter = actions.iterator(); iter.hasNext();)
        {
            Object a = (Object) iter.next();

            actionWrappers.add(new ActionWrapper(a));
        }

        return actionWrappers;
    }

    public Collection getSubActions()
    {
        return (Collection)
               Util.tryGetProperty(getModelElement(), "subactions");

        // using tryGet.. here,
        // because 'subactions' does not exist on AtomicActions
    }

    public Set<ActionWrapper> getSubActionWrappers()
    {
        Collection actions = getSubActions();

        Set<ActionWrapper> actionWrappers = new LinkedHashSet<ActionWrapper>();

        if(actions == null)
            return actionWrappers;


        for (Iterator iter = actions.iterator(); iter.hasNext();)
        {
            Object a = (Object) iter.next();

            actionWrappers.add(new ActionWrapper(a));
        }

        return actionWrappers;
    }

    public boolean hasSubActions() {
        Collection subActions = getSubActions();
        if (subActions != null && subActions.size() > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    public void setResourceWrapper(ResourceWrapper resourceWrapper)
    {
        Util.setProperty(getModelElement(), "resource", resourceWrapper.getModelElement());
    }

    public Collection getPermission()
    {
        Object permission = Util.getProperty(getModelElement(), "permission");

        return (Collection) permission;
    }

    public void addPermission(Object permission)
    {
        Collection permissions = getPermission();

        permissions.add(permission);
    }

    public Collection<PermissionWrapper> getPermissionWrappers()
    {
        Collection permissions = getPermission();

        Collection<PermissionWrapper> permissionWrappers = new LinkedList<PermissionWrapper>();

        for (Iterator iter = permissions.iterator(); iter.hasNext();)
        {
            Object p = (Object) iter.next();

            permissionWrappers.add(new PermissionWrapper(p));
        }

        return permissionWrappers;
    }
}
