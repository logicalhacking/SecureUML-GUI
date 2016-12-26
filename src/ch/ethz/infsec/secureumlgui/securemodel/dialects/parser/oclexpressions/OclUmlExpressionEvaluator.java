/**
 *
 */
package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.ModelElement;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmapping.GenericDialectHelper;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.InterResourceAssociation;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClass;

/**
 *  Evaluate an OCL Expression
 *  on an UML ModelElement
 */
public class OclUmlExpressionEvaluator extends OclExpressionEvaluator
{
    public OclUmlExpressionEvaluator(
        OclExpression oclExpression,
        ModelElement startPoint,
        Object self,
        DialectMetaModelInfo dialectMetaModelInfo,
        MetaModelClass metaModelClass)
    {
        super(oclExpression, startPoint, self);

        this.dialectMetaModelInfo = dialectMetaModelInfo;
        this.metaModelClass = metaModelClass;

        //resourcePath = startPoint.getName();
        resourcePath.add(0, startPoint);

        resourcePaths.put(startPoint, startPoint.getName());
    }

    //String resourcePathString = null;
    ArrayList<ModelElement> resourcePath =
        new ArrayList<ModelElement>();

    // Resource -> ResourcePath
    Map<ModelElement, String> resourcePaths =
        new LinkedHashMap<ModelElement, String>();

    DialectMetaModelInfo dialectMetaModelInfo;
    MetaModelClass metaModelClass;

    /* (non-Javadoc)
     * @see ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclExpressionEvaluator#takePropertyAccessStep(java.util.Set, ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.PropertyAccessStep, java.util.Set)
     */
    @Override
    protected void takePropertyAccessStep(Set result, PropertyAccessStep paStep, Set startPoints) throws OclEvaluatorException
    {
        // TODO Auto-generated method stub
        //super.takePropertyAccessStep(result, paStep, startPoints);

        String propertyName = paStep.getPropertyName();
        propertyName = removeHeadingWhitespace(propertyName);
        propertyName = removeTrailingWhitespace(propertyName);

//    logger.info("evaluatePathExpression - step: " + paStep );

//    logger.info(
//        "evaluating OclUmlExpression on Resource of type '"
//        +  metaModelClass.getName());

        //                boolean stepTaken = false;


        for (Iterator iter = startPoints.iterator(); iter.hasNext();)
        {
            ModelElement startPoint = (ModelElement) iter.next();

            Collection/*<ModelElement>*/ iterationResults =
                new LinkedList/*<ModelElement>*/();

            String resourcePathSoFar = resourcePaths.get(startPoint);

//      MetaModelClass metaModelClass =
//        GenericDialectHelper.getInstance().
//          getMetaModelClass(startPoint);

//      logger.info("metamodelClass = "
//          + metaModelClass.getName());

            Collection associations = dialectMetaModelInfo
                                      .getInterResourceAssociations(metaModelClass);

            Collection res = null;
            //modelElement = null;

            if(associations == null)
                return;

            for (Iterator it = associations.iterator(); it.hasNext();)
            {
                InterResourceAssociation association =
                    (InterResourceAssociation) it.next();

//        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
//        AssociationEnd otherEnd =
//          association.getOtherEnd(metaModelClass);

                ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
                AssociationEnd end1 = association.getEnd1();
                ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
                AssociationEnd end2 = association.getEnd2();


                try
                {
                    if(paStep.getPropertyName().equals(
                                end1.getName()))
                    {
                        res = takePathStep(
                                  startPoint, propertyName, end1, null);
                        if(res==null) {
                            logger.error("takePathStep failed.");
                        } else {
                            iterationResults.addAll(res);
                        }
                    }
                    else if(paStep.getPropertyName().equals(
                                end2.getName()))
                    {
                        //modelElement
                        res = takePathStep(
                                  startPoint, propertyName, end2, null);
                        if(res==null) {
                            logger.error("takePathStep failed.");
                        } else {
                            iterationResults.addAll(res);
                        }
//            else if(res instanceof ModelElement)
//            {
//              iterationResults.add(res);
//            }
                        //else
                        {
                            //            logger.warn("accessing Property "
                            //              + paStep.getPropertyName()
                            //              + " on Object of Type: "
                            //              + startPoint.getClass().getSimpleName()
                            //              + " returned null");
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("accessing Property "
                                 + paStep.getPropertyName()
                                 + " on Object of Type: "
                                 + startPoint.getClass().getSimpleName()
                                 + " failed");

                    logger.logException(e);
                }
            }
            if(iterationResults.size() == 0)
                /* if this is the case, the property is not a
                 * defined association,
                 * but it could be an attribute -> try it
                 */
            {
                try
                {
                    Object o = Util.tryGetProperty(
                                   startPoint, paStep.getPropertyName());

                    if (o instanceof Collection)
                    {
                        Collection values = (Collection) o;
                        iterationResults.addAll(values);
                    }
                    else if(o != null)
                    {
//            logger.info(startPoint.getName()
//                + "." + paStep.getPropertyName()
//                + " = " + o);
                        iterationResults.add(/*(ModelElement)*/o);
                    }
                }
                catch (Exception e)
                {
                    //logger.logException(e);
                }
            }
            if(iterationResults.size() == 0)
            {
                logger.warn(
                    "accessing Property "
                    + paStep.getPropertyName()
                    + " on Object of Type: "
                    + startPoint.getClass().getSimpleName()
                    + " " + startPoint.getName()
                    + " returned null");
            }

            result.addAll(iterationResults);

            // uptdate ResourcePaths

            for (Iterator iterator = iterationResults.iterator(); iter.hasNext();)
            {
                Object item = iterator.next();

                if(item instanceof ModelElement)
                {
                    ModelElement m = (ModelElement) item;

                    resourcePaths.put(m, resourcePathSoFar + m.getName());

                    logger.info("put resourcePath: "
                                + m.getName() + " -> " + resourcePathSoFar+m.getName());
                }
            }
        }
    }

    /**
     * @param modelElement
     * @param step
     * @param end1
     * @param end2
     * @return ModelElement which is reached by taking the step
     */
    private Collection takePathStep(
        ModelElement modelElement,
        String step,
        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
        AssociationEnd end1,
        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
        AssociationEnd end2)
    {
        Collection c = null;
        c = navigateAssociation(modelElement, end1, end2);


        if (c == null) {
            logger.error("navigateAssociation results in null");
            return null;
        }
        else
            // if (c instanceof Collection)
        {
            /*
             * consider only the first element - anchor path must be unique
             */
            Collection collection = (Collection) c;

//      if (collection.size() > 1)
//      {
//        logger.error(MultiContextLogger.MODELMAPPER,
//            "Anchor Path Step: " + step + " not unique.");
//        //return null;
//      }
            /*else*/
            if (collection.size() == 0)
            {
                logger.error(MultiContextLogger.MODELMAPPER,
                             "Anchor Path Step: " + step + "could not be followed.");
                //return null;
            }
            else
                // collection.size >= 1
            {
                Object o = collection.iterator().next();
                if (o instanceof ModelElement)
                {
                    modelElement = (ModelElement) o;

                    resourcePath.add(0, modelElement);

//          if (resourcePath == null
//              || resourcePath.length() == 0)
//          {
//            //resourcePath = modelElement.getName();
//
//          }
//          else
//          {
////            resourcePath = modelElement.getName() + "."
////                + resourcePath;
//          }

                    logger.info(OCL_EXPRESSION_EVALUATOR_DETAILLED,
                                "step taken: "
                                + step
                                + ", resourcePath so far: "
                                + getResourcePath());
                    //break;
                }
            }
        }
        return c;
    }

    /**
     * @param from
     * @param end1
     * @param end2
     * @return if the Association could not be navigated,
     *    'null' is returned
     */
    public Collection navigateAssociation(
        ModelElement from,
        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
        AssociationEnd end1,
        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
        AssociationEnd end2
    )
    {
        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
        AssociationEnd usedEnd = null;

        logger.info("from = "+from.getName());
        logger.info("end1 = "+end1.getName());

        Object result = null;
        try
        {
            String methodName = null;
            Method umlPropertyGetter = null;

            try
            {
                methodName = end1.getUmlPropertyGetter();
                umlPropertyGetter = from.getClass().getMethod(methodName,
                                    new Class[0]);
                logger.info(//OCL_EXPRESSION_EVALUATOR_DETAILLED,
                    "found UMLPropertyGetter: "
                    + from.getName() + "."
                    + umlPropertyGetter);
                usedEnd = end1;
            }
            catch (Exception e1)
            {
                logger.error("no method '"
                             + methodName + "' on " + from.getClass());
                // TODO: handle exception
                //e.printStackTrace();
                try
                {
                    methodName = end2.getUmlPropertyGetter();
                    umlPropertyGetter = from.getClass().getMethod(methodName,
                                        new Class[0]);
                    logger.info(//OCL_EXPRESSION_EVALUATOR_DETAILLED,
                        "found UMLPropertyGetter: "
                        + from.getName() + "."
                        + umlPropertyGetter);
                    usedEnd = end2;
                }
                catch (Exception e2)
                {
                    logger.error("no method '"
                                 + methodName + "' on " + from.getClass());
                    // TODO: handle exception
                    //e.printStackTrace();
                }
            }

//      AssociationEnd end;
//      end.getParticipant();


            result = umlPropertyGetter.invoke(from, new Object[0]);

            if(usedEnd != null)
            {
                logger.info(//OCL_EXPRESSION_EVALUATOR_DETAILLED,
                    "navigated AssociationEnd successfully: "
                    + usedEnd);

                if(result instanceof Collection)
                    logger.info(//OCL_EXPRESSION_EVALUATOR_DETAILLED,
                        "... returned a result set of size "
                        + ((Collection)result).size());
                else
                    logger.info(//OCL_EXPRESSION_EVALUATOR_DETAILLED,
                        "... returned result: " + result);


                metaModelClass = usedEnd.getType();


            }
            else
            {
                logger.error(
                    "could not navigate Association: "
                    + end1.getOwner());
            }

            //            if (result instanceof Collection)
            //            {
            //                // only consider the first element
            //                result = ((Collection) result).iterator().next();
            //            }

            //            if (result instanceof ModelElement)
            //            {
            //                modelElement = (ModelElement) result;
            //
            //            }
        }
        catch (Exception e1)
        {
            //logger.logException(e1);

            // try the other associationEnd
//      try
//      {
//        targetAssociationEnd =
//          targetAssociationEnd.getOwner().getOtherEnd(targetAssociationEnd.getType());
//        methodName =
//          targetAssociationEnd.getUmlPropertyGetter();
//
//        Method umlPropertyGetter = from.getClass().getMethod(methodName,
//            new Class[0]);
//
//        result = umlPropertyGetter.invoke(from, new Object[0]);
//      }
//      catch (Exception e2)
//      {
//        if(e1 != null && e2 != null)
//        {
//          logger.logException(e1);
//          logger.logException(e2);
//        }
//
//      }


        }

        if (result instanceof Collection)
            return (Collection) result;
        else if (result != null)
        {
            LinkedList<Object> l = new LinkedList<Object>();

            l.add(result);

            return l;
        }
        else
            return null;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath()
    {
        if(resourcePath.size() == 0)
        {
            return "";
        }

        // ugly, non-generic hack
        //int nofSteps = 2;

        // generic solution, does not work correctly
        int i = 0;
        int nofSteps = resourcePath.size();
        for (Iterator iter = resourcePath.iterator();
                iter.hasNext();)
        {
            ModelElement m = (ModelElement) iter.next();

            int firstOccurrence = resourcePath.indexOf(m);

            if(firstOccurrence < i)
                // i.e. m occurred before
            {
                nofSteps = firstOccurrence+1;
                break;
            }
            i++;
        }

        String resourcePathString =
            resourcePath.get(0).getName();
        for (int j = 1; j < nofSteps; j++)
        {
            resourcePathString += "." + resourcePath.get(j).getName();
        }

//  ugly, non-generic hack
        // gets Association-End Permissions' resource Path always right
        if(nofSteps >2)
        {
            resourcePathString = resourcePath.get(0).getName()
                                 + "." + resourcePath.get(nofSteps-1).getName();
        }
        return resourcePathString;
    }

    /**
     * @return the resourcePaths
     */
    public Map<ModelElement, String> getResourcePaths()
    {
        return resourcePaths;
    }

    /**
     * @param resourcePath the resourcePath to set
     */
//  public void setResourcePath(String resourcePath)
//  {
//    this.resourcePath = resourcePath;
//  }

}
