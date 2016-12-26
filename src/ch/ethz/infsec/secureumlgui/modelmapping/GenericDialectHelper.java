/**
 *
 */
package ch.ethz.infsec.secureumlgui.modelmapping;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.jmi.reflect.RefObject;


import org.apache.log4j.Logger;
import org.argouml.model.Model;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.UmlClass;
import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.AssociationEnd;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.InterResourceAssociation;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClass;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelConst;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclEvaluatorException;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclExpression;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclExpressionEvaluator;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclExpressionsParser;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclUmlExpressionEvaluator;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.transformation.MetaModelMap;
import ch.ethz.infsec.secureumlgui.transformation.ModelMap;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;

/**
 *
 */
public class GenericDialectHelper
{
    /**
     *
     */
    //    public GenericDialectHelper(DialectMetaModelInfo mmInfo)//, MetaModelMap map)
    //    {
    //        this.dialectMetaModelInfo = mmInfo;
    //
    //        this.map = MetaModelMap.getDefault();
    //    }
    /**
     *
     */
    private GenericDialectHelper()
    {
        this.map = MetaModelMap.getDefault();

        logger.disableLoggerContext(logger.MODELMAPPER_DETAILLED);

    }

    static private GenericDialectHelper instance;

    static public GenericDialectHelper getInstance()
    {
        if (instance == null)
            instance = new GenericDialectHelper();

        return instance;
    }

    DialectMetaModelInfo dialectMetaModelInfo;

    MetaModelMap map;

    MultiContextLogger logger = MultiContextLogger.getDefault();

    private static Logger aLog = Logger.getLogger(GenericDialectHelper.class);

    public MetaModelClass getMetaModelClass(ModelElement m)
    {
        if (m == null || dialectMetaModelInfo == null) {
            aLog.debug("getMetaModelClass: ModelElement or dialectMetaModelInfo == null (" + m + ")");
            return null;
        }

        Collection<MetaModelClass> mmcs = dialectMetaModelInfo
                                          .getMetaModelClasses();


        for (MetaModelClass mmc : mmcs)
        {
            String umlClassNameTag = mmc.getUmlClassName();

            if (hasType(m, umlClassNameTag))
            {
                return mmc;
            }
        }

        // if mmc not found until here

        return getResourceType(m);
    }


    /** If the UML Modelelement m represents a SecureUML element
     * (Role, Permission, Policy or Resource), this function returns it's
     * Resourcetype.
     *
     * For roles and permissions, dummy resourcetypes
     * are created.
     */
    public ResourceType getSecureUmlType(ModelElement m)
    {
        if(isSecureUmlRole(m))  {
            return SecureUmlConstants.getRoleResourceTypeDummy();
        }
        else if(isSecureUmlPermission(m)) {
            return SecureUmlConstants.getPermissionResourceTypeDummy();
        }
        else if(isSecureUmlPolicy(m)) {
            return SecureUmlConstants.getPolicyResourceTypeDummy();
        }
        else { // is the target a resource?
            return getResourceType(m);
        }
    }

    /**
     * If the UML ModelElement m represents a SecureUML-Resource, this function
     * examines and returns it's ResourceType - otherwise returns null.
     *
     * @param m
     *            UML ModelElement
     * @return The ResourceType of m if exists, null otherwise
     */
    public ResourceType getResourceType(ModelElement m) {

        if (m == null || dialectMetaModelInfo == null)
            return null;

        for (ResourceType resourceType : dialectMetaModelInfo.getResourceTypes() ) {
            // Assume m is of Resource-Type r

            logger.info(MultiContextLogger.MODELMAPPER_DETAILLED, "is "
                        + m.getName() + " a " + resourceType.getName() + "?");

            if (!hasType(m, resourceType.getUmlClassName())) {
                logger.info(MultiContextLogger.MODELMAPPER_DETAILLED,
                            "no! (wrong class: " + m.getClass().getSimpleName());
                continue;
            }
            // if anchorPath == 'self'

            String anchorPath = resourceType.getAnchorPath();
            String modelElementStereotype = resourceType.getModelElementStereotype();

            if (hasStereotype(m, modelElementStereotype)) {
                resourcePath = m.getName();
                logger.info(MultiContextLogger.MODELMAPPER_DETAILLED,
                            "ModelElement " + m.getName() + " is of ResourceType "
                            + resourceType.getName());
                logger.info(MultiContextLogger.MODELMAPPER_DETAILLED,
                            "... yes, marked explicitly!");
                // found the right resource Type
                return resourceType;
            } else if (modelElementStereotype == null
                       || modelElementStereotype.length() == 0) {
                ModelElement anchor = findAnchor(m, resourceType);

                ResourceType anchorType = getResourceType(anchor);

                if (anchor != null
                        && anchorType != null
                        && hasStereotype(anchor, anchorType
                                         .getModelElementStereotype())) {
                    String anchorName = Util.getProperty(anchor, "name")
                                        .toString();
                    // logger.info("ModelElement "
                    // + m.getName()
                    // + " is of ResourceType "
                    // + r.getName());
                    // found the right Resource Type
                    logger.info(MultiContextLogger.MODELMAPPER_DETAILLED,
                                "... yes, implicitly " + "- because the anchor '"
                                + anchorName + "' is a resource!");
                    return resourceType;
                }
            }
        }
        // else
        // - if arrived here,
        // the ModelElement m is not a ResourceType

        // Collection<MetaModelClass> mmClasses =
        // dialectMetaModelInfo.getMetaModelClasses();
        // for (Iterator iter = mmClasses.iterator(); iter.hasNext();)
        // {
        // MetaModelClass mmClass = (MetaModelClass) iter.next();

        // logger.info(MultiContextLogger.MODELMAPPER_DETAILLED, "is "
        // + m.getName() + " a " + mmClass.getName() + "?");

        // if (hasType(m, mmClass.getUmlClassName()))
        // {
        // logger.info(MultiContextLogger.MODELMAPPER_DETAILLED,
        // "ModelElement " + m.getName()
        // + " is of MetamodelClass " + mmClass.getName());

        // }
        // }

        logger.info(MultiContextLogger.MODELMAPPER_DETAILLED, "... no!");

        return null;
    }

    public ActionType getActionType(Object suAction)
    {
        if (suAction == null || dialectMetaModelInfo == null)
            return null;


        for (ActionType at : dialectMetaModelInfo.getActionTypes() )
        {
//		  logger.info(MultiContextLogger.MODELMAPPER_DETAILLED, "is "
//		  + suAction + " actionType " + at.getName() + "?");

            ActionWrapper suActionWrapper =  ActionWrapper.createActionWrapper(suAction);

            if(at.getShortName().equals(suActionWrapper.getName()))
            {
//			  logger.info("...yes");
                return at;
            }
            else
            {
//			  logger.info("...no");

            }
        }

        return null;
    }

    public boolean isSecureUmlRole(ModelElement m)
    {
        try
        {
            String umlClassName =
                SecureUmlConstants.getRoleResourceTypeDummy().getUmlClassName();
            String modelElementStereotype =
                SecureUmlConstants.getRoleResourceTypeDummy().getModelElementStereotype();

//		  logger.info(
//		  "isSecureUmlRole("
//		  //+ m.get
//		  + umlClassName
//		  + " ?= "
//		  + m.getClass().getSimpleName()
//		  + ", "
//		  + modelElementStereotype
//		  + " ?= "
//		  + ((Stereotype) m.getStereotype().iterator().next()).getName());

            if (hasType(m, umlClassName)
                    && hasStereotype(m, modelElementStereotype))
            {
                return true;
            }
        }
        catch (Exception e)
        {

        }

        return false;
    }

    public boolean isSecureUmlPermission(ModelElement m)
    {
//	  logger.info(
//	  "isSecureUmlPermission(umlClassname: "
//	  + umlClassName
//	  + ", stereotype: "
//	  + modelElementStereotype);

        if (hasType(m, SecureUmlConstants.PERMISSION_CLASSNAME)
                && hasStereotype(m, SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION))
        {
            return true;
        }
        else
            return false;
    }

    public boolean isSecureUmlPolicy(ModelElement m) {
        try
        {
//		  logger.info(
//		  "isSecureUmlRole("
//		  //+ m.get
//		  + umlClassName
//		  + " ?= "
//		  + m.getClass().getSimpleName()
//		  + ", "
//		  + modelElementStereotype
//		  + " ?= "
//		  + ((Stereotype) m.getStereotype().iterator().next()).getName());

            if (hasType(m, SecureUmlConstants.POLICY_CLASSNAME)
                    && hasStereotype(m, SecureUmlConstants.STEREOTYPE_SECUML_POLICY))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSecureUmlExcpLevel(ModelElement m)
    {
        String umlClassName = "";

        if (hasType(m, umlClassName) && hasStereotype(m, SecureUmlConstants.STEREOTYPE_SECUML_POLICY)) {
            return true;
        } else {
            return false;
        }

    }


    public Classifier findAnchor(AssociationClass permission)
    {
        Collection ends = permission.getConnection();
        for (Iterator iter = ends.iterator(); iter.hasNext();)
        {
            org.omg.uml.foundation.core.AssociationEnd end =
                (org.omg.uml.foundation.core.AssociationEnd) iter.next();

            ResourceType rt = getResourceType(end.getParticipant());
            if(rt != null)
                return end.getParticipant();
        }
        return null;
    }

    public Classifier getPermissionRole(AssociationClass permission)
    {
        Collection ends = permission.getConnection();
        for (Iterator iter = ends.iterator(); iter.hasNext();)
        {
            org.omg.uml.foundation.core.AssociationEnd end =
                (org.omg.uml.foundation.core.AssociationEnd) iter.next();

            boolean isRole = isSecureUmlRole(end.getParticipant());
            if(isRole)
                return end.getParticipant();
        }
        return null;
    }

    public UmlClass findAnchor(ModelElement resource)
    {
        logger.info("find Anchor for resource "+resource.getName());
        ResourceType startPointResourceType =
            getResourceType(resource);

        return findAnchor(resource, startPointResourceType);
    }

    String resourcePath = null;

    /** finds the Anchor of a Resource and calculates
     * the Resource's resourcePath.
     *
     * @param resource
     * @param startPointResourceType
     * @return the anchor of the resource
     */
    public UmlClass findAnchor(ModelElement resource,
                               ResourceType startPointResourceType)
    {
//    org.omg.uml.foundation.core.AssociationEnd end;
//    end.

        if (resource == null || startPointResourceType == null)
        {
            resourcePath = "";
            return null;
        }

        resourcePath = resource.getName();

        ResourceType resourceType = startPointResourceType;

        ModelElement modelElement = resource;

        // TODO: follow startPointResourceType.getAnchorPath()
        // from startPoint

        String anchorPath = startPointResourceType.getAnchorPath();

        ModelElement result =
            //evaluateUniquePathExpression(
            evaluateUniqueOclExpression(
                resource, resourceType, anchorPath);

        if (result instanceof UmlClass)
            return (UmlClass) result;
        else
            return null;
    }


    private ModelElement evaluateUniqueOclExpression(
        ModelElement startpoint,
        MetaModelClass metaModelClass,
        /*ModelElement modelElement,*/
        String oclExpression)
    {
        //Object startpoint = map.getElement(modelElement);

        logger.info("parsing OCL-Expression: "
                    + oclExpression);

        OclExpressionsParser parser =
            new OclExpressionsParser();

        OclExpression expression =
            parser.parseOclExpression(oclExpression);

//    logger.info("OCL-Expression parsed - starting at "
//        + startpoint.getClass().getSimpleName() + " "
//        + startpoint.getName()
//        + " with evaluation of: "
//        + expression);

        OclUmlExpressionEvaluator evaluator =
            new OclUmlExpressionEvaluator(
            expression, startpoint, startpoint,
            dialectMetaModelInfo, metaModelClass);

        try
        {
            Set result = evaluator.evaluateExpression();

            this.resourcePath = evaluator.getResourcePath();

            if(result == null || result.size() == 0)
            {
                logger.error("result set is empty");
                logger.error("no anchor found for: " + startpoint.getName());
                return null;
            }
            if(result.size() == 1)
            {
                Object o = result.iterator().next();
                if (o instanceof ModelElement)
                {
                    ModelElement me = (ModelElement) o;

//          logger.info("anchor of "
//              + startpoint.getName()
//              + " is: "
//              + me.getName());

                    return me;
                }
            }
            else
            {
                logger.error("more than one Object in result set");
                logger.error("no anchor found for: " + startpoint.getName());
            }
        }
        catch (Exception e)
        {
            logger.logException(e);
        }

        return null;
    }

    public static final char OPENING_BRACKET_CHAR = '[';
    public static final char CLOSING_BRACKET_CHAR = ']';

    /**
     * not working correctly,
     * and using pathExpressions instead of OCL-Expressions
     *
     * @param startPoint
     * @param metaModelClass
     * @param maxdepth
     * @param pathExpression
     *
     */
    private ModelElement evaluateUniquePathExpression(ModelElement startPoint, MetaModelClass metaModelClass, /*ModelElement modelElement,*/ String pathExpression, int maxdepth)
    {
        if(maxdepth < 0)
        {
            logger.error("evaluateUniquePathExpression recursion too deep");
            return null;
        }
        maxdepth--;

        ModelElement modelElement = startPoint;

        String[] pathSteps = null;
        if (pathExpression != null)
        {
            pathSteps = pathExpression.split("\\.");
        }

//    logger.info("evaluating path expression: " + pathExpression);
        //ModelElement anchor;

        if (pathSteps != null && pathExpression.length() > 0)
        {
            int startIndex = 0;
            if(pathSteps[0].equals(MetaModelConst.SELF_PATH))
                startIndex = 1;

            int bracketsdepth = 0;

            for (int i = startIndex; i < pathSteps.length; i++)
            {
                String step = pathSteps[i];

                logger.info("evaluatePathExpression - step: " + step );

                //                boolean stepTaken = false;

                Collection associations = dialectMetaModelInfo
                                          .getInterResourceAssociations(metaModelClass);

                for (Iterator iter = associations.iterator(); iter.hasNext();)
                {
                    InterResourceAssociation association = (InterResourceAssociation) iter
                                                           .next();

                    ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
                    AssociationEnd otherEnd =
                        association.getOtherEnd(metaModelClass);

                    //logger.info("evaluatePathExpression - otherEnd: " + otherEnd);

                    if(step.charAt(step.length()-1)
                            == CLOSING_BRACKET_CHAR)
                        // step is a selection
                    {
                        bracketsdepth = 0;
                        try
                        {
                            String condition =
                                step.substring(0, step.length()
                                               -1);
                            // remove CLOSING_BRACKET_CHAR

                            if(condition.charAt(0)==OPENING_BRACKET_CHAR)
                                condition = condition.substring(1);

                            logger.info(
                                "evaluatePathExpression: " +
                                "evaluating selection with condition"
                                + condition);

                            Collection c = null;
                            c = navigateAssociation(modelElement, otherEnd);

                            String leftpart = "";
                            String rightpart = "";
                            boolean equality = false;

                            if(condition.contains("="))
                            {
                                leftpart = condition.split("=")[0];
                                rightpart = condition.split("=")[1];

                                equality = true;
                            }
                            else if(condition.contains("<>"))
                            {
                                leftpart = condition.split("<>")[0];
                                rightpart = condition.split("<>")[1];

                                equality = false;
                            }
                            else
                            {
                                logger.error("evaluatePathExpression: invalid path selection condition");
                                return null;
                            }

                            for (Iterator iterator = c.iterator(); iterator.hasNext();)
                            {
                                ModelElement me = (ModelElement) iterator.next();

                                ModelElement leftModelElement = null;


                                if(leftpart.startsWith(MetaModelConst.SELF_PATH))
                                {
//                  logger.info(
//                      "would evaluate left part recursively, here: "
//                      + rightpart);
                                    evaluateUniquePathExpression(
                                        startPoint,
                                        getResourceType(startPoint),
                                        leftpart, maxdepth);
                                }
                                else
                                {
//                  logger.info(
//                      "would evaluate left part recursively, here: "
//                      + leftpart);
                                    evaluateUniquePathExpression(
                                        me,
                                        getResourceType(me),
                                        leftpart, maxdepth);
                                }
                                ModelElement rightModelElement = null;
                                if(rightpart.startsWith(MetaModelConst.SELF_PATH))
                                {
//                  logger.info(
//                      "would evaluate right part recursively, here: "
//                      + rightpart);
                                    evaluateUniquePathExpression(
                                        startPoint,
                                        getResourceType(startPoint),
                                        rightpart, maxdepth);
                                }
                                else
                                {
//                  logger.info(
//                      "would evaluate right part recursively, here: "
//                      + rightpart);
                                    evaluateUniquePathExpression(
                                        me,
                                        getResourceType(me),
                                        rightpart, maxdepth);
                                }

                                logger.info("Condition compares "
                                            + leftModelElement
                                            + " and "
                                            + rightModelElement);

                                boolean equals =
                                    leftModelElement.equals(rightModelElement);

                                if(equals == equality)
                                {
                                    modelElement = me;
                                    // continue
                                }
                                //return me;
                            }
                            if(modelElement == null)
                            {
                                logger.error("there is no ModelElement " +
                                             "satisfying the selection condition");
                                return null;
                            }
                        }
                        catch (Exception e)
                        {
                            logger.logException(e);
                        }
                    }
                    else if(step.indexOf(OPENING_BRACKET_CHAR) != -1
                            || bracketsdepth > 0)
                        // step is first part of a selection
                    {
                        bracketsdepth = 1;
                        pathSteps[i+1] = step + pathSteps[i+1];
                        break;
                    }
                    else if (step.endsWith("*"))
                        // step is a repetition
                    {
                        step = step.substring(0, step.length() - 1);

                        if(otherEnd != null
                                && otherEnd.getName().equals(
                                    step.substring(0, step.length() - 1)))
                        {
                            //Collection c = navigateAssociation(modelElement, otherEnd);

                            ModelElement temp = null;

                            do // take this step 0-n times, as long as it leads somewhere else
                            {
                                temp =
                                    takeAnchorPathStep(modelElement, step, otherEnd);

                                if(temp != null && temp != modelElement)
                                    modelElement = temp;
                                else
                                    temp = null;

                            } while (temp != null);

                        }
                    }
                    else if (otherEnd != null
                             && otherEnd.getName().equals(step))
                    {
                        // take the step via
                        try
                        {
                            //String startPointName = startPoint.getName();

                            modelElement = takeAnchorPathStep(modelElement, step, otherEnd);

                            metaModelClass = /*(ResourceType)*/ otherEnd.getType();
                            metaModelClass.toString();
                            // stepTaken = true;
                        }
                        catch (Exception e)
                        {
                            logger.logException(e);
                            // do nothing - assumption was wrong & try next
                        }
                        // here was a problem because
                        // 'stepTaken = true' was here, before
                    }
                    else
                    {
                        // do nothing
                    }
                }
                //                if (!stepTaken)
                //                    return null;
            }
            /* until now, all steps have been taken
             * but the first one in the array
             * which is == 'self' (has been verified above)
             *
             * --> this is the anchor
             */

            if (modelElement instanceof UmlClass)
            {
                logger.info("anchor of "
                            + startPoint.getName()
                            + " is: "
                            + modelElement.getName());

                return (UmlClass) modelElement;
            }
            else
            {
                logger.error("no anchor found for: " + startPoint.getName());
                return null;
            }
        }
        else
        {
            logger.error(MultiContextLogger.MODELMAPPER, "Invalid anchorPath: "
                         + pathExpression);
        }

        return null;

    }

    /** finds the Anchor of a Resource and calculates
     * the Resource's resourcePath
     *
     * @param resource
     * @param startPointResourceType
     * @return the anchor of the resource
     */
    public UmlClass findAnchorOld(ModelElement resource,
                                  ResourceType startPointResourceType)
    {
        if (resource == null || startPointResourceType == null)
        {
            resourcePath = "";
            return null;
        }

        resourcePath = resource.getName();

        ResourceType resourceType = startPointResourceType;

        ModelElement modelElement = resource;

        // TODO: follow startPointResourceType.getAnchorPath()
        // from startPoint

        String anchorPath = startPointResourceType.getAnchorPath();
        String[] anchorPathSteps = null;
        if (anchorPath != null)
        {
            anchorPathSteps = anchorPath.split("\\.");
        }
        //ModelElement anchor;

        if (anchorPathSteps != null && anchorPath.length() > 0
                && anchorPathSteps[0].equals(MetaModelConst.SELF_PATH))
        {

            for (int i = 1; i < anchorPathSteps.length; i++)
            {
                String step = anchorPathSteps[i];

                //                boolean stepTaken = false;

                Collection associations = dialectMetaModelInfo
                                          .getInterResourceAssociations(resourceType);

                for (Iterator iter = associations.iterator(); iter.hasNext();)
                {
                    InterResourceAssociation association = (InterResourceAssociation) iter
                                                           .next();

                    ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.AssociationEnd otherEnd =
                        association.getOtherEnd(resourceType);

                    if (step.endsWith("*"))
                    {
                        step = step.substring(0, step.length() - 1);

                        //Collection c = navigateAssociation(modelElement, otherEnd);

                        ModelElement temp = null;

                        do // take this step 0-n times, as long as it leads somewhere else
                        {
                            temp =
                                takeAnchorPathStep(modelElement, step, otherEnd);

                            if(temp != null && temp != modelElement)
                                modelElement = temp;
                            else
                                temp = null;

                        } while (temp != null);

                    }
                    else if (otherEnd.getName().equals(step))
                    {
                        // take the step via
                        try
                        {
                            String startPointName = resource.getName();

                            modelElement = takeAnchorPathStep(modelElement, step, otherEnd);

                            resourceType = (ResourceType) otherEnd.getType();
                            resourceType.toString();
                            // stepTaken = true;
                        }
                        catch (Exception e)
                        {
                            logger.logException(e);
                            // do nothing - assumption was wrong & try next
                        }
                        // here was a problem because
                        // 'stepTaken = true' was here, before
                    }
                    else
                    {
                        // do nothing
                    }
                }
                //                if (!stepTaken)
                //                    return null;
            }
            /* until now, all steps have been taken
             * but the first one in the array
             * which is == 'self' (has been verified above)
             *
             * --> this is the anchor
             */

            if (modelElement instanceof UmlClass)
                return (UmlClass) modelElement;
            else
                return null;
        }
        else
        {
            logger.error(MultiContextLogger.MODELMAPPER, "Invalid anchorPath: "
                         + anchorPath);
        }

        return null;
    }

    /**
     * @param modelElement
     * @param step
     * @param otherEnd
     * @return ModelElement which is reached by taking the step
     */
    private ModelElement takeAnchorPathStep(
        ModelElement modelElement,
        String step,
        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.AssociationEnd otherEnd)
    {
        Collection c = null;
        c = navigateAssociation(modelElement, otherEnd);

        if (c == null)
            return null;
        else
            // if (c instanceof Collection)
        {
            /*
             * consider only the first element - anchor path must be unique
             */
            Collection collection = (Collection) c;

            if (collection.size() > 1)
            {
                logger.error(MultiContextLogger.MODELMAPPER,
                             "Anchor Path Step: " + step + " not unique.");
                //return null;
            }
            else if (collection.size() == 0)
            {
                logger.error(MultiContextLogger.MODELMAPPER,
                             "Anchor Path Step: " + step + "could not be followed.");
                //return null;
            }
            else
                // collection.size == 1
            {
                Object o = collection.iterator().next();
                if (o instanceof ModelElement)
                {
                    modelElement = (ModelElement) o;

                    if (resourcePath == null || resourcePath.length() == 0)
                    {
                        resourcePath = modelElement.getName();
                    }
                    else
                    {
                        resourcePath = modelElement.getName() + "."
                                       + resourcePath;
                    }

//          logger.info("step taken: " + step
//              + ", resourcePath so far: " + resourcePath);
                    //break;
                }
            }
        }
        return modelElement;
    }

//  public String getResourcePath(ModelElement resourceUml)
//  {
//
//    UmlClass anchor = findAnchor(resourceUml);
//
//    return resourcePath;
//  }

//  public UmlClass findClass(RefPackage umlPackage, String name)
//  {
//    if (umlPackage == null || umlPackage.getOwnedElement() == null)
//      return null;
//
//    //tudresden.ocl20.core.jmi.uml15.core.Namespace n;
//    for (Iterator iter = umlPackage.getOwnedElement().iterator(); iter
//        .hasNext();)
//    {
//      Object item = iter.next();
//
//      if (item instanceof UmlClass)
//      {
//        UmlClass umlClass = (UmlClass) item;
//
//        if (umlClass.getName().equals(name))
//          return (UmlClass) umlClass;
//
//        logger.info(" ! " + umlClass.getName());
//      }
//      else if (item instanceof RefPackage)
//      {
//        UmlClass result = findClass((RefPackage) item, name);
//
//        if (result != null)
//          return result;
//      }
//
//      //            else
//      //            {
//      //                  try
//      //                  {
//      //                      Class[] argTypes = new Class[0];
//      //                      Object[] args = new Object[0];
//      //
//      //                      Method m = item.getClass().getMethod("getName", argTypes);
//      //                      logger.info(" ! " + m.invoke(item, args) + " : " + item.getClass());
//      //                  }
//      //                  catch (Exception e)
//      //                  {
//      //                      //logger.logException(e);
//      //                      //logger.info(" ! " + item.getClass());
//      //                  }
//    }
//
//    //        for (Iterator iterator = umlNamespace.getOwnedElement().iterator(); iter.hasNext();)
//    //        {
//    //            RefPackage refPackage = (RefPackage) iterator.next();
//    //
//    //            UmlClass result = findClass(refPackage, name);
//    //            if(result != null)
//    //                return result;
//    //
//    //        }
//
//    return null;
//  }

//  public RefPackage findPackage(Collection packages, String name)
//  {
//    for (Iterator iter = packages.iterator(); iter.hasNext();)
//    {
//      Object item = iter.next();
//      if (item instanceof Package)
//      {
//        RefPackage p = (RefPackage) item;
//        String packagename = p.getName();
//
//        if (packagename.equals(name))
//          return p;
//
//        RefPackage result = findPackage(p, name);
//        if (result != null)
//          return result;
//      }
//    }
//    return null;
//  }
//
//  public RefPackage findPackage(RefPackage umlPackage, String name)
//  {
//    if (umlPackage == null || umlPackage.getOwnedElement() == null)
//      return null;
//
//    for (Iterator iter = umlPackage.getOwnedElement().iterator(); iter
//        .hasNext();)
//    {
//      Object item = iter.next();
//
//      if (item instanceof RefPackage)
//      {
//        RefPackage p = (RefPackage) item;
//
//        String pName = p.getName();
//        if (pName.equals(name))
//          return p;
//        else
//        {
//          logger.info(" ! " + p.getName());
//          RefPackage result = findPackage((RefPackage) item, name);
//        }
//      }
//    }
//    return null;
//  }

    public String findPackage(ModelElement elem)
    {
        Namespace result = null;
        while (result == null)
        {
            result = elem.getNamespace();
            elem = (ModelElement) elem.refImmediateComposite();
        }
        if (elem instanceof Classifier)
        {
            result = elem.getNamespace();
        }
        return result.getName();
    }

    public Namespace findNamespaceByName(ModelElement startpoint, String name)
    {
        //Namespace result = null;
        try
        {
            Collection elements = startpoint.getNamespace().getOwnedElement();

            for (Iterator iter = elements.iterator(); iter.hasNext();)
            {
                Object item = (Object) iter.next();

                if (item instanceof Namespace)
                {
                    Namespace ns = (Namespace) item;

                    if(ns.getName().equals(name))
                        return ns;
                }
            }


        }
        catch (Exception e)
        {
            logger.logException(e);
        }

        return null;
    }

    public Collection navigateAssociation(ModelElement from,
                                          InterResourceAssociation association)
    {
        MetaModelClass mmc = getMetaModelClass(from);
        if (mmc != null)
        {
            AssociationEnd otherEnd = association.getOtherEnd(mmc);
            if (otherEnd != null)
                return navigateAssociation(from, otherEnd);
        }

        return null;
    }

    /**
     * @param from
     * @param targetAssociationEnd
     * @return if the Association could not be navigated,
     *    'null' is returned
     */
    public Collection navigateAssociation(
        ModelElement from,
        ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.
        AssociationEnd targetAssociationEnd)
    {
        if(from == null || targetAssociationEnd == null)
        {
            logger.error("navigateAssociation: null argument ("
                         + from + ", " + targetAssociationEnd + ")");
            return null;
        }

        String methodName = targetAssociationEnd.getUmlPropertyGetter();
        Object result = null;

        try
        {
            Method umlPropertyGetter = null;
            if(methodName != null && methodName.length() != 0)
            {
                umlPropertyGetter =
                    from.getClass().getMethod(
                        methodName, new Class[0]);

                result = umlPropertyGetter.invoke(from, new Object[0]);
            }
            // else
            if(result == null ||
                    (result instanceof Collection
                     && ((Collection)result).size() == 0))
                // try to navigate backwards
            {
                //        logger.info(
                //            "navigateAssociation - Navigating backwards:"
                // + " using Method "
                //+ targetAssociationEnd.getOwner());
//        AssociationEnd otherEnd = targetAssociationEnd.
//          getOwner().getOtherEnd(targetAssociationEnd.getType());

                AssociationEnd otherEnd =
                    targetAssociationEnd.getOwner().getEnd1();
                if(otherEnd == targetAssociationEnd)
                    otherEnd =
                        targetAssociationEnd.getOwner().getEnd2();

                methodName =
                    otherEnd.getUmlPropertyGetter();

                if(methodName != null && methodName.length() != 0)
                {
//          logger.info(
//              "navigateAssociation - Navigating backwards:"
//              + " using Method " + methodName);
                    Collection/*<ModelElement>*/ relatedElements =
                        new LinkedList/*<ModelElement>*/();
                    result = relatedElements;

                    Set<RefObject> allUmlElements =
                        ModelMap.getDefault().getAllUmlElements();

                    //          logger.info("trying to start from "
                    //+ allUmlElements.size() + "  Uml ModelElements "
                    //+ " via Method " + methodName);
                    for (Iterator iter = allUmlElements.iterator(); iter.hasNext();)
                    {
                        RefObject modelElement = (RefObject) iter.next();

                        //MetaModelClass mmc = getMetaModelClass(modelElement);

                        //if(mmc == )
                        //result == null;
                        try
                        {
                            umlPropertyGetter = modelElement.getClass().
                                                getMethod(methodName,new Class[0]);

                            Object res = umlPropertyGetter.invoke(
                                             modelElement, new Object[0]);

                            if (res instanceof ModelElement)
                            {
                                ModelElement resMe = (ModelElement) res;

                                logger.info(" ... invoked - method returned: "
                                            + resMe.getName());

                            }

                            if (res instanceof Collection)
                            {
                                Collection resultCollection = (Collection) result;
                                if(resultCollection.contains(from))
                                {
                                    //result = modelElement;
                                    //break;
                                    relatedElements.add(modelElement);
                                }
                            }
                            else if (res != null)
                            {
                                if(res == from)
                                {
//                  result = modelElement;
//                  break;
                                    relatedElements.add(modelElement);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            //logger.logException(e);
                        }
                        //result = null;
                    }
                }
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
        catch (Exception e)
        {
            logger.error("Problem navigating AssociationEnd "
                         + targetAssociationEnd
                         + " from " + from.getName());
            logger.logException(e);
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

    /* utility methods */

    public boolean hasType(ModelElement modelElement, String className)
    {
        className += MetaModelConst.MDR_IMPL_SUFFIX;

//    logger.info("hasType("
//        + modelElement.getClass().getSimpleName()
//        + ", "
//        + className);

        if (modelElement != null && className != null)
        {
            boolean result = modelElement.getClass().getSimpleName().
                             startsWith(className);
//      logger.info("... " + result);

            return result;
        }
        else
            return false;
    }

    public boolean hasStereotype(ModelElement element, String stereotype)
    {
        if (element == null || stereotype == null || stereotype.length() == 0)
            return false;

        Collection stereotypes = element.getStereotype();
        int nofStereotypes = stereotypes.size();

//    logger.info("hasStereotype(sterotype:" + stereotype
//        + " - searching among " + nofStereotypes);

        if (stereotypes == null || stereotypes.size() == 0)
            return false;

        for (Iterator it = stereotypes.iterator(); it.hasNext();)
        {
            Stereotype s = (Stereotype) it.next();
            if (s.getName().equals(stereotype))
            {
//        logger.info("... found");
                return true;
            }
        }
        return false;
    }

    public boolean haveStereotype(Collection<ModelElement> elements,
                                  String stereotype)
    {
        for (ModelElement elem : elements)
        {
            if (!hasStereotype(elem, stereotype))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the map
     */
    public MetaModelMap getMap()
    {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(MetaModelMap map)
    {
        this.map = map;
    }

    /**
     * @return the dialectMetaModelInfo
     */
    public DialectMetaModelInfo getDialectMetaModelInfo()
    {
        return dialectMetaModelInfo;
    }

    /**
     * @param dialectMetaModelInfo the dialectMetaModelInfo to set
     */
    public void setDialectMetaModelInfo(DialectMetaModelInfo dialectMetaModelInfo)
    {
        this.dialectMetaModelInfo = dialectMetaModelInfo;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath()
    {
        return resourcePath;
    }

    public String getResourcePath(ModelElement resourceUml)
    {
        UmlClass anchor = findAnchor(resourceUml);

        // generic solution, doesn't work correctly
//    MetaModelClass anchorMetaModelClass = getMetaModelClass(anchor);
//
//    ResourceType resourceType = getResourceType(resourceUml);
//
//    if(resourceType != null && resourceType.getResoucePath() != null)
//    {
//      OclExpressionsParser parser = new OclExpressionsParser();
//
//      try
//      {
//        OclExpression resourcePathExpression =
//          parser.parseOclExpression(resourceType.getResoucePath());
//
//        OclUmlExpressionEvaluator evaluator =
//          new OclUmlExpressionEvaluator(
//              resourcePathExpression, anchor, anchor,
//              dialectMetaModelInfo, anchorMetaModelClass);
//
//        Set reachedObjects = evaluator.evaluateExpression();
//
//        Object resourceUmlObject = findElementByName(reachedObjects, resourceUml.getName());
//        if(evaluator.getResourcePaths().containsKey(resourceUmlObject))
//        {
//          resourcePath = evaluator.getResourcePaths().get(resourceUmlObject);
//          logger.info("##resourcePath for "
//              + resourceUml.getName()
//              + " recalucalated: "
//              + resourcePath);
//        }
//        else
//          // ugly, non-generic hack
//        {
//          String[] resourcePathFragments = resourcePath.split("\\.");
//          resourcePath = resourcePathFragments[0]
//                    + "." + resourcePathFragments[1];
//        }
//      }
//      catch (Exception e)
//      {
//        e.printStackTrace();
//      }
//    }

//  ugly, non-generic hack
        String[] resourcePathFragments = resourcePath.split("\\.");
        if(resourcePathFragments.length == 1)
            return resourcePathFragments[0];
        else
        {
            resourcePath = resourcePathFragments[0]
                           + "." + resourcePathFragments[1];
            return resourcePathFragments[0]
                   + "." + resourcePathFragments[1];
        }

        //return resourcePath;
    }

    /**
     * Find a resource in a modelelement according to the given pathName.
     *
     * @param pathName
     * @param anchor
     * @return the found element
     */
    @SuppressWarnings("unchecked")
    public ModelElement resolvePath(
        String pathName, ModelElement anchor)
    {
        logger.info("resolving resource Path: '" + pathName +
                    "' starting at " + anchor.getName());

        String[] components = pathName.split("\\.");
        if (components.length == 0)
        {
            logger.error("invalid path expression: " + pathName);
            return null;
        }
        int pathOffset = 0;
        if (components[pathOffset].equals(anchor.getName()))
        {
            if (components.length == 1)
            {
                // path name denotes the anchor, return it
                return anchor;
            }
            else
            {
                // path name has an anchor prefix, skip it
                pathOffset = 1;
            }
        }
        ModelElement result = anchor;
        // follow path to find resource
        while (pathOffset < components.length)
        {
            result = findRelatedElement(
                         result, components[pathOffset]);
            pathOffset++;
        }

        if (result == null)
        {
            logger.error("invalid path expression - target not found: " + pathName);
        }
        return result;
    }

    /**
     * Finds an element with the given name inside its containing object.
     *
     * @param startPoint
     * @param name
     * @return the found element
     */
    @SuppressWarnings("unchecked")
    private ModelElement findRelatedElement(
        ModelElement startPoint,
        String name)
    {

        if(startPoint == null)
        {
            logger.error("findRelatedElement - start point null, name = "+name);
            return null;
        }



        logger.info("searching related Element with Name '" + name +
                    "' starting from "+startPoint.getName() + "'");

        MetaModelClass metaModelClass =
            getMetaModelClass(startPoint);


        if(metaModelClass == null)
        {

            logger.error(startPoint.getName()
                         + " is not a metaModelClass Instance");
            return null;
        } else {
            logger.info("startPoint is a '"+metaModelClass.getName()+"' instance");
        }

        for (Iterator iter =
                    dialectMetaModelInfo.getInterResourceAssociations(
                        metaModelClass).iterator(); iter.hasNext();)
        {
            InterResourceAssociation association =
                (InterResourceAssociation) iter.next();

//      logger.info("... along " + association.getName());

            Collection result = navigateAssociation(startPoint, association);

//      if(result != null)
//        logger.info("...among " + result.size() + " elements");

            ModelElement relatedElement = findElementByName(result, name);

            if(relatedElement != null)
                return relatedElement;
            else
            {
                result = new LinkedList();

                Set<RefObject> allUmlElements =
                    ModelMap.getDefault().getAllUmlElements();

                for (Iterator it = allUmlElements.iterator(); it.hasNext();)
                {
                    RefObject item = (RefObject) it.next();

                    if (item instanceof ModelElement)
                    {
                        ModelElement modelElement = (ModelElement) item;

                        Classifier anchor = findAnchor(modelElement);

                        if(anchor == startPoint)
                        {
                            result.add(modelElement);
                        }
                    }
                }

                relatedElement = findElementByName(result, name);

                if(relatedElement != null)
                    return relatedElement;
            }
//      else
//      {
//        AssociationEnd otherEnd =
//          association.getOtherEnd(metaModelClass);
//        AssociationEnd end =
//          association.getOtherEnd(otherEnd.getType());
//
//        Collection<ModelElement> relatedElements =
//          new LinkedList<ModelElement>();
////          tryNavigateBackwards(otherEnd, end,
////            Model.getCoreHelper().
////              getAllClassifiers(startPoint.getNamespace())
////            , startPoint);
//        Set<RefObject> allUmlElements =
//          ModelMap.getDefault().getAllUmlElements();
//
//
//        return findElementByName(relatedElements, name);
//
//        //return relatedElement;
//      }
        }
//    org.omg.uml.foundation.core.AssociationEnd a;
//    a.getParticipant();

        return null;
    }

    private ModelElement findElementByName(Collection<ModelElement> elems, String name) {
        if(elems == null)
            return null;

        for (ModelElement elem : elems)
        {
            if (elem == null)
            {
                logger.info("elem==null in findElementByName");
            }
            if(elem.getName()==null) {
                logger.error("elem.getName() == null in findElementByName");
                if ("".equals(name))
                    return elem;
            } else if (elem.getName().equals(name))
            {
                return elem;
            }
            else
            {
                //        logger.info("... is not: " + elem.getName());
            }
        }
        return null;
    }

    private Collection<ModelElement> tryNavigateBackwards(
        AssociationEnd otherEnd,
        AssociationEnd end,
        Collection modelElements,
        ModelElement target)//,int maxDepth)
    {
        Set visitedElements = new LinkedHashSet();
        return tryNavigateBackwards(
                   otherEnd, end, modelElements,
                   visitedElements, target);
    }

    /**
     * @param otherEnd
     * @param end
     * @param modelElements
     */
    private Collection<ModelElement> tryNavigateBackwards(
        AssociationEnd otherEnd, AssociationEnd end,
        Collection modelElements, Set visitedElements,
        ModelElement target)
    {
        Collection<ModelElement> res = new LinkedList();
//    if(maxDepth <= 0)
//      return;

        if(modelElements == null || modelElements.size() == 0)
            return null;

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
                    return null;
                else
                    visitedElements.add(m);


                if (hasType(m, otherEnd.getType().getName()))
                {
                    logger
                    .info("try to navigate Association in reverse Direction " +
                          "from end: "
                          + otherEnd.getName());

                    Collection coll = navigateAssociation(m, end);
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

                        if(coll.contains(target))
                        {
                            res.add(m);
                        }
                        // TODO: do something here
                        //addModelElementToScope(otherEnd, m);
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

                    MetaModelClass mmClass = getMetaModelClass(m);

                    if(mmClass != null && mmClass.getUmlClassName() != null)
                    {
//            logger.info("...from: "
//                + m.getClass().getSimpleName()
//                + " : "
//                + m.getNameA());

                        Collection associations = dialectMetaModelInfo.getInterResourceAssociations(mmClass);

                        if(associations != null)
                        {
                            for (Iterator iter = associations.iterator(); iter.hasNext();)
                            {
                                InterResourceAssociation association = (InterResourceAssociation) iter.next();


                                Collection<ModelElement> result =
                                    navigateAssociation(
                                        m, association.getOtherEnd(mmClass));
//                Collection coll;
//                if (result instanceof Collection)
//                {
//                  coll = (Collection) result;
//
//                }
//                else
//                {
//                  coll = new LinkedList(); coll.add(result);
//                }

                                //tryNavigateBackwards(otherEnd, end, coll, maxDepth-1);
                                return tryNavigateBackwards(otherEnd, end, result, visitedElements, target);
                            }
                        }


                    }


                }
            }
        }
        return res;
    }

}
