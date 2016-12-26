/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.jmi.model.MofPackage;
import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;

/**
 *
 */
public class DialectMetaModelInfo
{
    MultiContextLogger logger = MultiContextLogger.getDefault();

    /* Invariants:
     * - Collections are never null
     * - for each ResourceType in resourceTypes, there is a Mapping
     *    in actionTypesOfResourceType with a non-null value (Collection<ActionType>)
     */


    /** the name of the design modeling language (e.g., ComponentUML, ControllerUML). */
    private String dialectName = null;

    public String getDialectName()
    {
        return dialectName;
    }

    public void setDialectName(String dialectName)
    {
        this.dialectName = dialectName;
    }

    /** the extent in which dialect metamodel instances are stored. */
    private Object dialectExtent;

    public Object getDialectExtent()
    {
        if(dialectExtent==null)
            logger.error("dialectExtent = null");
        return dialectExtent;

    }

    public void setDialectExtent(Object dialectExtent)
    {
        this.dialectExtent = dialectExtent;
    }

    /**
     * Get the <code>dialectMetaExtent</code> value.
     *
     * @return a <code>MofPackage</code> value
     */
    public final MofPackage getDialectMetaExtent() {
        return dialectMetaExtent;
    }

    /**
     * Set the <code>dialectMetaExtent</code> value.
     *
     * @param newDialectMetaExtent The new DialectMetaExtent value.
     */
    public final void setDialectMetaExtent(final MofPackage newDialectMetaExtent) {
        this.dialectMetaExtent = newDialectMetaExtent;
    }

    /**
     * The extent for the dialect metamodel. Use this as a parameter
     * for <code>MDRepository.createExtent()</code> to create an instanc of the
     * dialect metamodel.
     */
    private MofPackage dialectMetaExtent;

    private Collection<ResourceType> resourceTypes = new ArrayList<ResourceType>();

    public Collection<ResourceType> getResourceTypes()
    {
        return resourceTypes;
    }

    /**
     *
     */
    public ResourceType getResourceType(Object secureUmlElement)
    {
        if(secureUmlElement == null)
            return null;


        String umlClassName = secureUmlElement.getClass().getSimpleName();

        for (ResourceType rt : resourceTypes)
        {
            if(umlClassName.indexOf('$') >= 0)
                umlClassName = umlClassName.split("\\$")[0];

            if(rt.getName().equals(umlClassName))
                return rt;
        }
        return null;
    }

//  public void setResourceTypes(Collection<ResourceType> resourceTypes)
//  {
//      this.resourceTypes = resourceTypes;
//  }

    private Collection<MetaModelClass> metaModelClasses =
        new ArrayList<MetaModelClass>();

    public Collection<MetaModelClass> getMetaModelClasses()
    {
        return metaModelClasses;
    }
//    public Collection<MetaModelClass> getMappableMetaModelClasses()
//    {
//      Collection<MetaModelClass> result = new LinkedList<MetaModelClass>();
//
//      for (Iterator iter = metaModelClasses.iterator(); iter.hasNext();)
//      {
//        MetaModelClass m = (MetaModelClass) iter.next();
//
//        if(m.getUmlClassName() != null)
//          result.add(m);
//      }
//
//      return result;
//    }

    public Collection<MetaModelClass> getResourceTypesAndMetaModelClasses()
    {
        Collection result = new LinkedList<MetaModelClass>();
        Util.addAllSave(result, getMetaModelClasses());
        Util.addAllSave(result, resourceTypes);

        return result;
    }

//
//    public void setMetaModelClasses(Collection<MetaModelClass> metaModelClasses)
//    {
//      this.metaModelClasses = metaModelClasses;
//    }

    private Collection<ActionType> actionTypes = new ArrayList<ActionType>();

    public Collection<ActionType> getActionTypes()
    {
        return actionTypes;
    }

//    public void setActionTypes(Collection<ActionType> actionTypes)
//    {
//        this.actionTypes = actionTypes;
//    }

    private Collection<AtomicActionType> atomicActionTypes = new ArrayList<AtomicActionType>();

    public Collection<AtomicActionType> getAtomicActionTypes()
    {
        return atomicActionTypes;
    }

//    public void setAtomicActionTypes(Collection<AtomicActionType> atomicActionTypes)
//    {
//        this.atomicActionTypes = atomicActionTypes;
//    }

    private Collection<CompositeActionType> compositeActionTypes = new ArrayList<CompositeActionType>();

    public Collection<CompositeActionType> getCompositeActionTypes()
    {
        return compositeActionTypes;
    }


    public ActionType getActionTypeByName(String name)
    {
        for (Iterator iter = actionTypes.iterator(); iter.hasNext();)
        {
            ActionType actiontype = (ActionType) iter.next();

            if(actiontype.getName().equals(name))
            {
                return actiontype;
            }
        }
        logger.info("action type "+name+" not found in DialectMetaModelInfo");
        return null;
    }

//    public void setCompositeActionTypes(Collection<CompositeActionType> compositeActionTypes)
//    {
//        this.compositeActionTypes = compositeActionTypes;
//    }

    private Map<ResourceType, Collection<ActionResourceAssociation>> actionAssociationsOfResourceType = new LinkedHashMap<ResourceType, Collection<ActionResourceAssociation>>();

    public Collection<ActionResourceAssociation> getResourceActionAssociations(ResourceType resourceType)
    {
        return actionAssociationsOfResourceType.get(resourceType);
    }

    public ResourceType getResourceTypeOfActionType(ActionType actionType)
    {
        return getResourceActionDependency(actionType).getResourceType();
    }

    private Map<MetaModelEntity, Set<InterResourceAssociation>>
    interResourceAssociationsByEntity =
        new LinkedHashMap<MetaModelEntity,
    Set<InterResourceAssociation>>();


    /**
     * @return the interResourceAssociations
     */
    public Collection<InterResourceAssociation> getInterResourceAssociations(MetaModelClass metaModelClass)
    {
        if(metaModelClass == null)
            return null;

        Set<InterResourceAssociation> result =
            interResourceAssociationsByEntity.get(metaModelClass);
//      if(result == null)
//      {
//        result = new LinkedHashSet<InterResourceAssociation>();
//        interResourceAssociationsByEntity.put(metaModelClass, result);
//      }

        return result;
    }

    /**
     * @return all InterResourceAssociations
     * - in a newly created Collection
     * (copied references to all Values from
     * the Map 'interResourceAssociationsByEntity')
     */
    public Collection<InterResourceAssociation> getInterResourceAssociations()
    {
        Collection<InterResourceAssociation> interResourceAssociations = new LinkedList<InterResourceAssociation>();

        for (Iterator iter = interResourceAssociationsByEntity.keySet().iterator(); iter
                .hasNext();)
        {
            MetaModelEntity key = (MetaModelEntity) iter.next();

            Util.addAllSave(interResourceAssociations, interResourceAssociationsByEntity.get(key));
        }

        return interResourceAssociations;
    }

    /**
     *
     * @param name
     * @return the created InterResourceAssociation
     */
    public InterResourceAssociation addInterResourceAssociation(String name, MetaModelEntity anchor1, MetaModelEntity anchor2)
    {
        //logger.info("Adding inter-resource Association: " + name);
        AssociationEnd end1 = new AssociationEnd();//containerEndName);
        AssociationEnd end2 = new AssociationEnd();//contentsEndName);


        // Default values - valid for UMLClasses
//        containerEnd.setUmlPropertyGetter("getOwner");
//        contentsEnd.setUmlPropertyGetter("getFeature");

        InterResourceAssociation m = new InterResourceAssociation(name, end1, end2);

        // add mapping for anchor 1
        Set<InterResourceAssociation> mas = interResourceAssociationsByEntity.get(anchor1);

        if(mas == null)
        {
            mas = new LinkedHashSet<InterResourceAssociation>();
            interResourceAssociationsByEntity.put(anchor1, mas);
        }
        mas.add(m);

        // add mapping for anchor 2
        mas = interResourceAssociationsByEntity.get(anchor2);
        if(mas == null)
        {
            mas = new LinkedHashSet<InterResourceAssociation>();
            interResourceAssociationsByEntity.put(anchor2, mas);
        }
        mas.add(m);



        return m;
        //logger.info("added ContainmentAssociation: " + name);
    }




//    public void setActionTypesOfResourceTypes(Collection<ActionType> actionTypesOfResourceType)
//    {
//        this.actionTypesOfResourceType = actionTypesOfResourceType;
//    }

    private Map<ActionType, ActionResourceAssociation> dependencyOfActionType = new LinkedHashMap<ActionType, ActionResourceAssociation>();


    public ActionResourceAssociation getResourceActionDependency(ActionType actionType)
    {
        return dependencyOfActionType.get(actionType);
    }

    public Collection<ActionType> getExplicitActionTypesOfResourceType(ResourceType resourceType)
    {
        Collection<ActionResourceAssociation> associations = getResourceActionAssociations(resourceType);

        Collection<ActionType> actionTypes = new ArrayList<ActionType>();

        if(associations != null)
        {
            for (Iterator iter = associations.iterator(); iter.hasNext();)
            {
                ActionResourceAssociation dependency = (ActionResourceAssociation) iter.next();

                actionTypes.add(dependency.getActionType());
            }
        }
        return actionTypes;
    }

    /** returns all ActionTypes defined for the
     * resourceType, including the ActionTypes
     * inherited from its parent ResourceType
     *
     */
    public Collection<ActionType> getActionTypesOfResourceType(ResourceType resourceType)
    {
        Collection<ActionType> actionTypes = getExplicitActionTypesOfResourceType(resourceType);

        if(resourceType != null && resourceType.getParentResourceType() != null)
        {
            Collection<ActionType> inheritedActionTypes =
                getActionTypesOfResourceType(resourceType.getParentResourceType());

            actionTypes.addAll(inheritedActionTypes);
        }
        return actionTypes;
    }

    public ActionType getActionType(ResourceType resourceType, String shortname)
    {
        Collection<ActionType> actionTypes = getActionTypesOfResourceType(resourceType);

        for (Iterator iter = actionTypes.iterator(); iter.hasNext();)
        {
            ActionType actionType = (ActionType) iter.next();
            if(actionType.getShortName().equals(shortname))
                return actionType;
        }
        return null;
    }

    public void addResourceType(ResourceType r)
    {
        resourceTypes.add(r);

        actionAssociationsOfResourceType.put(r, new ArrayList<ActionResourceAssociation>());

        //interResourceAssociationsByEntity.put(r, new LinkedHashSet<InterResourceAssociation>());
    }

    public void addMetaModelClass(MetaModelClass c)
    {
        metaModelClasses.add(c);

        //actionAssociationsOfResourceType.put(c, new ArrayList<ResourceActionAssociation>());

        interResourceAssociationsByEntity.put(c, new LinkedHashSet<InterResourceAssociation>());
    }

    public void addAtomicActionType(AtomicActionType a)
    {
        atomicActionTypes.add(a);

        actionTypes.add(a);
    }

    public void addCompositeActionType(CompositeActionType c)
    {
        compositeActionTypes.add(c);

        actionTypes.add(c);
    }

    public void addResourceActionAssociation(String name, ResourceType r, ActionType a)
    {
        if(dependencyOfActionType.containsKey(a))
        {
            logger.warn("Overwriting existing ResourceActionDependency!");
        }

        ActionResourceAssociation dependency = new ActionResourceAssociation(name, r, a);

        dependencyOfActionType.put(a, dependency);

        actionAssociationsOfResourceType.get(r).add(dependency);
    }




}
