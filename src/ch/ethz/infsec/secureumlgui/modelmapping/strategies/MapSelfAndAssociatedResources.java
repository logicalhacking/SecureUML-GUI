/**
 *
 */
package ch.ethz.infsec.secureumlgui.modelmapping.strategies;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.behavioralelements.statemachines.State;
import org.omg.uml.behavioralelements.statemachines.Transition;


import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.AssociationEnd;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.InterResourceAssociation;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClass;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;

/**
 *
 */
public class MapSelfAndAssociatedResources
    extends SecureUmlMappingScopeStrategy
{

    /**
     *
     */
    public MapSelfAndAssociatedResources(DialectMetaModelInfo mmInfo)
    {
        this.mmInfo = mmInfo;
    }

    DialectMetaModelInfo mmInfo;

    /* (non-Javadoc)
     * @see ch.ethz.infsec.secureumlgui.modelmapping.MappingScopeStrategy#getMappingScope(tudresden.ocl20.core.jmi.uml15.core.ModelElement)
     */

    private int currentNavigationDepth = 6;

    private int navigationDepth = 6;

    public int getNavigationDepth()
    {
        return navigationDepth;
    }

    public void setNavigationDepth(int navigationDepth)
    {
        this.navigationDepth = navigationDepth;
        this.currentNavigationDepth = navigationDepth;
    }

    Set<ModelElement> result;

    private static Logger aLog = Logger.getLogger(MapSelfAndAssociatedResources.class);

    @Override
    public Set<ModelElement> getMappingScope(
        ModelElement startingPoint)
    {

        System.out.println("---------------------------------");
        if(currentNavigationDepth == navigationDepth)
            result = super.getMappingScope(startingPoint);


        currentNavigationDepth--;
        // logger.info("navigationDepth = "+ navigationDepth);
        // logger.info("currentNavigationDepth = "+ currentNavigationDepth);

        if(currentNavigationDepth <= 0)
            return new LinkedHashSet<ModelElement>();

        result.add(startingPoint);

        ResourceType rt = helper.getResourceType(startingPoint);
        MetaModelClass mmc = null;
        if(rt != null)
            mmc = rt;
        else
            mmc = helper.getMetaModelClass(startingPoint);

        aLog.debug("getMappingScope: " + startingPoint + ";" + rt);

        if(helper.isSecureUmlRole(startingPoint))
        {
            Classifier roleClass = (Classifier) startingPoint;
            Collection ends = Model.getFacade().getAssociationEnds(roleClass);
            for (Iterator iter = ends.iterator(); iter.hasNext();)
            {
                org.omg.uml.foundation.core.AssociationEnd end = (org.omg.uml.foundation.core.AssociationEnd) iter.next();

                org.omg.uml.foundation.core.AssociationEnd otherEnd = null;

                Collection associationsEnds = end.getAssociation().getConnection();
                for (Iterator iterator = associationsEnds.iterator(); iterator
                        .hasNext();)
                {
                    org.omg.uml.foundation.core.AssociationEnd associationEnd = (org.omg.uml.foundation.core.AssociationEnd) iterator.next();

                    if(end != associationEnd)  otherEnd = associationEnd;
                }
                if(otherEnd != null)
                {
                    result.add(otherEnd);

                    Classifier otherParticipant = otherEnd.getParticipant();

                    //result.add(otherParticipant);
                    addMappingScope(otherParticipant, result);

                }
            }
            Collection g = Model.getFacade().getGeneralizations(roleClass);
            for(Iterator iter = g.iterator(); iter.hasNext();) {
                Generalization gen = (Generalization) iter.next();
                Classifier parent = (Classifier) gen.getParent();
                Classifier child  = (Classifier) gen.getChild();
                if(parent!=roleClass && helper.isSecureUmlRole(parent)) {
                    addMappingScope(parent, result);
                }
                if(child!=roleClass && helper.isSecureUmlRole(child)) {
                    addMappingScope(child, result);
                }
            }
        }
        else if (helper.isSecureUmlPolicy(startingPoint)) {
            Classifier policyClass = (Classifier) startingPoint;
            Collection<org.omg.uml.foundation.core.AssociationEnd> ends = Model.getFacade().getAssociationEnds(policyClass);
            for (org.omg.uml.foundation.core.AssociationEnd end : ends) {

                org.omg.uml.foundation.core.AssociationEnd otherEnd = null;
                for (org.omg.uml.foundation.core.AssociationEnd associationEnd : end.getAssociation().getConnection()) {
                    if  ( end != associationEnd ) {
                        otherEnd = associationEnd;
                    }
                }
                if ( otherEnd != null ) {
                    result.add(otherEnd);
                    Classifier otherParticipant = otherEnd.getParticipant();

                    addMappingScope(otherParticipant, result);
                }
            }
            Collection<Generalization> g = Model.getFacade().getGeneralizations(policyClass);
            for (Generalization gen : g) {
                Classifier parent = (Classifier) gen.getParent();
                Classifier child = (Classifier) gen.getChild();

                if ( parent != policyClass && helper.isSecureUmlPolicy(parent)) {
                    addMappingScope(parent, result);
                }
                if (child != policyClass && helper.isSecureUmlPolicy(child)) {
                    addMappingScope(child, result);
                }
            }
        }
        else if(helper.isSecureUmlPermission(startingPoint))
        {
            AssociationClass permissionAssociationClass =
                (AssociationClass) startingPoint;

            Collection ends = permissionAssociationClass.getConnection();
            for (Iterator iter = ends.iterator(); iter.hasNext();)
            {
                org.omg.uml.foundation.core.AssociationEnd end =
                    (org.omg.uml.foundation.core.AssociationEnd) iter.next();

                Classifier participant = end.getParticipant();

                if(participant != null)
                {
                    //result.add(participant);
                    addMappingScope(participant, result);
                }
            }
        }

        else if (mmc != null)
        {
            Collection associatedAssociations = helper.getDialectMetaModelInfo()
                                                .getInterResourceAssociations(mmc);

            //      logger.info("found "+associatedAssociations.size()+" associations");
            for (Iterator iter = associatedAssociations.iterator(); iter.hasNext();)
            {
                Object item = iter.next();

                if (item instanceof InterResourceAssociation)
                {
                    InterResourceAssociation association = (InterResourceAssociation) item;

                    AssociationEnd otherEnd = association.getOtherEnd(rt);

                    Collection c = helper.navigateAssociation(startingPoint, otherEnd);

//          if (c instanceof Collection)
//          {
                    if( c != null && c.size() > 0)
                        addModelElementsToScope(otherEnd, c);

//          }
//          else if (o instanceof ModelElement)
//          {
//            ModelElement associatedModelElement = (ModelElement) o;
//
//            try
//            {
//              addModelElementToScope(otherEnd, associatedModelElement);
//            }
//            catch (Exception e)
//            {
//              ;
//            }
//
//          }
                    else //if (c == null)
                    {
                        logger.info("Association could not be navigated");
                        try
                        {
                            // Association could not be navigated
                            // - try it the other way round

                            AssociationEnd end = association
                                                 .getOtherEnd((MetaModelClass) otherEnd.getType());

                            Collection allElementsInNamespace = new LinkedList();
                            Namespace n = startingPoint.getNamespace();
                            if(n==null) {
                                //startingPoint doesn't have a namespace, so it has to be contained somewhere else:
                                //not complete, of course.
                                if(startingPoint instanceof State) {
                                    n = ((State) startingPoint).getStateMachine().getNamespace();
                                } else if (startingPoint instanceof Transition) {
                                    n = ((Transition) startingPoint).getStateMachine().getNamespace();
                                }
                            }
                            Collection c1 = n.getOwnedElement();
                            for (Iterator iterator = c1.iterator(); iterator.hasNext();)
                            {
                                Object temp = (Object) iterator.next();

                                allElementsInNamespace.add(temp);
                            }
                            if(startingPoint instanceof Namespace) {
                                logger.info("starting points is instance of Namespace");
                                Collection c2 = ((Namespace) startingPoint).getOwnedElement();
                                for (Iterator iterator = c2.iterator(); iterator.hasNext();) {
                                    Object temp = (Object) iterator.next();
                                    allElementsInNamespace.add(temp);
                                }

                            }
//               logger.info(
//                   "try to navigate backwards from "
//                   + allElementsInNamespace.size()
//                   + " elements");
                            Set visitedElements =
                                tryNavigateBackwards(otherEnd, end,
                                                     allElementsInNamespace);//, getNavigationDepth());
                            logger.info("found "+visitedElements.size()+" elements by navigating backwards");
                            result.addAll(visitedElements);
                            //result.addAll(allElementsInNamespace);
                        }
                        catch (Exception e)
                        {
                            logger.logException(e);
                        }
                    }
                }
            }

            // TODO: follow the associations defined in the metamodel
            // along the umlGetter paths
        }
        else {
            //logger.error("mmc = null");
        }

//    try
//    {
//      Set additionalScope = new LinkedHashSet();
//      for (Iterator iter = result.iterator(); iter.hasNext();)
//      {
//        ModelElement item = (ModelElement) iter.next();
//
//        Collection subScope = getMappingScope(item);
//
//        additionalScope.addAll(subScope);
//      }
//      result.addAll(additionalScope);
//    }
//    catch (Exception e)
//    {
//      logger.logException(e);
//    }


        currentNavigationDepth++;
        return result;

    }

    /* recursively find all Elements in mappingScope
     *
     */
    public void addMappingScope(ModelElement modelElement, Set<ModelElement> mappingScope)
    {
        if(mappingScope.contains(modelElement))
            return;

        aLog.debug("adding modelElement "+  modelElement + " to mapping scope");
        mappingScope.add(modelElement);

        Collection<ModelElement> additionalMappingScope =
            getMappingScope(modelElement);

        //mappingScope.addAll(additionalMappingScope);

        for (Iterator iter = additionalMappingScope.iterator(); iter.hasNext();)
        {
            ModelElement m = (ModelElement) iter.next();

            if(mappingScope.contains(m))
                continue;
            else
            {
                aLog.debug("adding modelElement_"+  m + " to mapping scope");
                mappingScope.add(m);
                //addMappingScope(m, mappingScope);
            }
        }
    }

    /**
     * @param otherEnd
     * @param associatedModelElement
     */
    private void addModelElementToScope(AssociationEnd otherEnd, ModelElement associatedModelElement)
    {
        if (helper.hasType(associatedModelElement,
                           ((MetaModelClass) otherEnd.getType()).getUmlClassName()))
        {
            //result.add(associatedModelElement);
            addMappingScope(associatedModelElement, result);
            //addMappingScope(associatedModelElement, result);
        }
        else
            ; // not the right Type
    }

    /**
     * @param otherEnd
     * @param c
     */
    private void addModelElementsToScope(AssociationEnd otherEnd, Collection c)
    {
        //Collection c = (Collection) o;
        for (Iterator iterator = c.iterator(); iterator.hasNext();)
        {
            ModelElement associatedModelElement = (ModelElement) iterator
                                                  .next();

            try
            {
                addModelElementToScope(otherEnd, associatedModelElement);
            }
            catch (Exception e)
            {
                logger.logException(e);
            }
        }
    }


    /**
     * @param otherEnd
     * @param end
     * @param modelElements
     */
    private Set tryNavigateBackwards(
        AssociationEnd otherEnd,
        AssociationEnd end,
        Collection modelElements)//,int maxDepth)
    {
        Set visitedElements = new LinkedHashSet();
        tryNavigateBackwards(otherEnd, end, modelElements, visitedElements);

        return visitedElements;
    }

    /**
     * @param otherEnd
     * @param end
     * @param modelElements
     */
    private void tryNavigateBackwards(AssociationEnd otherEnd, AssociationEnd end, Collection modelElements, Set visitedElements)
    {

//    if(maxDepth <= 0)
//      return;

        if(modelElements == null || modelElements.size() == 0)
            return;

//    logger.info(logger.MODELMAPPER_DETAILLED,
//        "Try to navigate Association the other way round - "
//        + "iterating over all ModelElements in the Namespace - n = "
//        + modelElements.size());


        for (Iterator iterator = modelElements.iterator(); iterator
                .hasNext();)
        {
            Object object = iterator.next();

//      if (object instanceof Namespace)
//      {
//        Namespace ns = (Namespace) object;
//        tryNavigateBackwards(otherEnd, end, ns.getOwnedElement());
////
////        Util.addAllSave(allElementsInNamespace, ns.getOwnedElement());
//      }
            // TODO: replace this hardcoded hack by a generic solution

            if (object instanceof ModelElement)
            {

                ModelElement m = (ModelElement) object;

                if(visitedElements.contains(m))
                    return;
                else
                    visitedElements.add(m);


                if (helper.hasType(m, otherEnd.getType().getName()))
                {
                    logger
                    .info("try to navigate Association in reverse Direction " +
                          "from end: "
                          + otherEnd.getName());

                    Collection coll = helper.navigateAssociation(m, end);
                    String s = "... leads to: ";
                    if(coll.size()>1)
                        s = s + " Collection containing: ";

                    if(coll.size() > 0)
                        s = s + Util.getProperty(coll.iterator().next(), "name");//+ coll);

                    logger.info(s);
//          if (coll instanceof Collection)
//          {
                    if(coll != null)
                    {
                        //Collection c1 = (Collection) coll;

                        //addModelElementsToScope(otherEnd, coll);
                        addModelElementToScope(otherEnd, m);
                    }
//          else if (c1 instanceof ModelElement)
//          {
//            try
//            {
//              addModelElementToScope(otherEnd, m);
//            }
//            catch (Exception e)
//            {
//              ;
//            }
//
//          }

                }
                else
                {

                    //
//          if(m instanceof Association)
//          {
//            Association a = (Association) m;
//
//            tryNavigateBackwards(otherEnd, end, a.getConnection(), maxDepth/1);
//
////
////            Util.addAllSave(allElementsInNamespace, a.getConnection());
//          }


//          logger.info("wrong type: "
//              + m.getClass().getSimpleName()
//              + " : " + m.getName());

//          logger.info(
//              "can't reach the ModelElement directly, " +
//              "try to reach it in several hops...");

                    //  but can try to reach the source ModelElement via
                    // more than one step

                    MetaModelClass mmClass = helper.getMetaModelClass(m);

                    if(mmClass != null && mmClass.getUmlClassName() != null)
                    {
//            logger.info("...from: "
//                + m.getClass().getSimpleName()
//                + " : "
//                + m.getNameA());

                        Collection associations = mmInfo.getInterResourceAssociations(mmClass);

                        if(associations != null)
                        {
                            for (Iterator iter = associations.iterator(); iter.hasNext();)
                            {
                                InterResourceAssociation association = (InterResourceAssociation) iter.next();


                                Object result = helper.navigateAssociation(m, association.getOtherEnd(mmClass));
                                Collection coll;
                                if (result instanceof Collection)
                                {
                                    coll = (Collection) result;

                                }
                                else
                                {
                                    coll = new LinkedList();
                                    coll.add(result);
                                }


                                visitedElements.addAll(coll);

                                //tryNavigateBackwards(otherEnd, end, coll, maxDepth-1);
                                tryNavigateBackwards(otherEnd, end, coll, visitedElements);
                            }
                        }


                    }


                }
            }
        }
    }

    GenericDialectHelper helper = GenericDialectHelper.getInstance();

}
