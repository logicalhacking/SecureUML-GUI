package ch.ethz.infsec.secureumlgui.securemodel.dialects.parser;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.jmi.model.Association;
import javax.jmi.model.AssociationEnd;
import javax.jmi.model.Attribute;
import javax.jmi.model.Classifier;
import javax.jmi.model.Import;
import javax.jmi.model.ModelElement;
import javax.jmi.model.MofClass;
import javax.jmi.model.MofPackage;
import javax.jmi.model.Namespace;
import javax.jmi.model.Tag;
import javax.jmi.reflect.RefPackage;

import org.apache.log4j.Logger;
import org.omg.uml.foundation.core.Stereotype;

//import ch.ethz.infsec.secureumlgui.GenericDialectTester;
import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.AtomicActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.CompositeActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.InterResourceAssociation;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClass;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClassAttribute;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelConst;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelEntity;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelFactory;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.TaggedValue;
import ch.ethz.infsec.secureumlgui.logging.LoggerContext;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.modelmanagement.ModelConst;
//import ch.ethz.infsec.secureumlgui.modelmapping.Util;
import ch.ethz.infsec.secureumlgui.transformation.MetaModelMap;
import ch.ethz.infsec.secureumlgui.wrapper.AttributeWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.ModelElementWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.MofClassWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.MofPackageWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.NamedModelElementWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.RefPackageWrapper;

/**
 * Analyzes a dialect metamodel.
 *
 */
public class DialectMetaModelAnalyzer
{


    /**
     *
     */
    public DialectMetaModelAnalyzer(
        Object dialectMetamodelPackage)
    {
        this.dialectMetamodelPackage = dialectMetamodelPackage;
    }


    MultiContextLogger logger = MultiContextLogger.getDefault();
    public static LoggerContext metamodelExamination = new LoggerContext("Examination of Metamodel Elements and Associations", " MetamodelExamination");
    public static LoggerContext metamodelValidation = new LoggerContext("Validation of Metamodel Structure and needed Elements", " MetamodelValidation");

    File xmiFile;

    String metaModelName = "MOF14";
    //    String metaModelName = tudresden.ocl20.core.MetaModelConst.MOF14;

    MetaModelMap modelmap = MetaModelMap.getDefault();


    /** the (SecureModel) package containing the dialect metamodel */
    Object dialectMetamodelPackage = null;

    MofPackageWrapper topPkg = null;

    MofPackageWrapper secureUmlPackageWrapper = null;
    Object secureUmlPackage = null;

    private static Logger aLog = Logger.getLogger(DialectMetaModelAnalyzer.class);

    public String prefix = "-";

    // Helper method. move somewhere else?
    public MofClassWrapper findMofClassByName(MofPackageWrapper mofPackageWrapper, String name)
    {
        if(mofPackageWrapper == null || name == null) {
            aLog.debug("findMofClassByName: wrapper or name == null");
            return null;
        }

        //name = name;// + "Class";
        try
        {
            Collection elements = mofPackageWrapper.getContents();

            aLog.debug("searching Class '" + name + "' among " + elements.size() + " Elements ... ");

//             logger.info(metamodelValidation, "searching Class '"
//                 + name + "' among "
//                 + elements.size() + " Elements ... ");

            Iterator it = elements.iterator();
            while(it.hasNext())
            {
                Object me = (Object) it.next();
                NamedModelElementWrapper meWrapper =
                    new NamedModelElementWrapper(me);

                //if (Util.hasType(me, "MofClass"))//(me instanceof MofClass)
                //{
                String meName =  meWrapper.getName();
                //me.getClass().getSimpleName();

                meName = meName.split(MetaModelConst.MDR_IMPL_SUFFIX_REGEXP)[0];

                //Util.getProperty(me, "name").toString();
                try
                {

                    if(name.equals(meName))
                        //((MofClass)me).getName().equals(name))
                    {
                        Object resource = me;
                        aLog.debug( "found Class: "+name);
                        //logger.info(metamodelValidation, "found Class: "+name);
                        return new MofClassWrapper(resource);
                    }
                    else
                    {
//                           logger.error(metamodelValidation,  " ! "
//                                        + meName);
                    }
                }
                catch (Exception e)
                {

                }


                //}
//                else
//                {
//                    //logger.info("... not: " + me.getNameA());
//                }
            }
        }
        catch (Exception e)
        {
            logger.logException(e);
            aLog.error(e);
        }
        return null;
    }

    // Helper method. move somewhere else?
    public MofClass findMofClassByNamePart(MofPackageWrapper secureUmlPackage, String namePart)
    {
        try
        {
            Collection elements = secureUmlPackage.getContents();

            aLog.debug("searching Class *" + namePart + "* among "
                       + elements.size() + " Elements ... ");

            logger.info("searching Class *" + namePart + "* among "
                        + elements.size() + " Elements ... ");

            Iterator it = elements.iterator();
            while(it.hasNext())
            {
                ModelElement me = (ModelElement) it.next();

                if(me instanceof MofClass)
                {
                    if(((MofClass)me).getName().toLowerCase().contains(namePart.toLowerCase()))
                    {
                        MofClass resource = (MofClass)me;

                        logger.info("... " +
                                    me.getName() + " found");
                        return resource;
                    }

                }
                else
                {
                    //logger.info("... not: " + me.getNameA());
                }
            }
        }
        catch (Exception e)
        {
            logger.logException(e);
        }
        return null;
    }


    // moved to 'examineMetamodelClasses'
//    public Package findPackageBySubstring(Package startPackage, String namePart)
//    {
//        try
//
//        {
//            Collection elements = startPackage.getOwnedElement();
//
//            logger.info("searching Package *" + namePart + "* among "
//                    + elements.size() + " Elements ... ");
//
//            Iterator it = elements.iterator();
//            while(it.hasNext())
//            {
//                ModelElement me = (ModelElement) it.next();
//
//                if(me instanceof Package)
//                {
//                    Package p = (Package)me;
//                    logger.info("(" + p.getName() +")");
//
//                    String packageName = p.getName().toLowerCase();
//                    namePart = namePart.toLowerCase();
//                    if(packageName.indexOf(namePart) != -1)
//                    {
//                        logger.info("... found: " + p.getName() + "/" + p.getNameA());
//                        return p;
//
//                    }
//                    else
//                        return findPackageBySubstring(p, namePart);
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            logger.logException(e);
//        }
//        return null;
//    }


    DialectMetaModelInfo mmInfo = null;


    MofClassWrapper resourceMofClassWrapper = null;
    MofClassWrapper atomicActionMofClassWrapper = null;
    MofClassWrapper compositeActionMofClassWrapper = null;
    Object secureModelPackage = null;
    MofPackageWrapper secureModelPackageWrapper = null;
//    Object dialectPackage = null;
//    RefPackageWrapper dialectPackageWrapper = null;
    Object dialectDialectPackage = null;
    MofPackageWrapper dialectDialectPackageWrapper = null;
    Stereotype StereotypeSecumlAction = null;

    public DialectMetaModelInfo analyzeDialect(Object startPackage)
    {
        MofPackageWrapper startPackageWrapper =
            new MofPackageWrapper(startPackage);

        // logger.info(metamodelExamination,"Analyzing Dialect Metamodel in Package: "
        //             +  startPackageWrapper.getName());
        //startPackage.getClass().getSimpleName());

        boolean found = findDialectPackages(startPackageWrapper);

        if(found) // found
        {

            found = findSecureUmlClasses();

            if(found)
            {
                mmInfo = new DialectMetaModelInfo();
                //MofPackage mp; mp.getName()
                String s = dialectDialectPackageWrapper.getName();
                //dialectDialectPackage.getClass().getSimpleName();
                s = s.split(MetaModelConst.MDR_IMPL_SUFFIX)[0];
                s = s.split(ModelConst.DIALECT_PACKAGE_SUFFIX)[0];

                mmInfo.setDialectName(s);
                //s.substring(0, s.length() - ModelConst.DIALECT_PACKAGE_SUFFIX.length()));

                // logger.info(metamodelExamination,"Dialect Name: " + mmInfo.getDialectName());

                //examineMetamodel(startPackage);
                examineMetamodel(secureModelPackageWrapper);
            }
            //        Collection<ActionType> actionTypes = new LinkedList<ActionType>();
            //
            //        actionTypes.addAll(mmInfo.getAtomicActionTypes());
            //        actionTypes.addAll(mmInfo.getCompositeActionTypes());
            //

            //        printResourcesTypes(mmInfo.getResourceTypes());
            //
            //        printActionsTypes(mmInfo.getActionTypes());


            return mmInfo;
        }
        else
            return null;
    }

    /**
     *
     */
    private boolean findSecureUmlClasses()
    {
        boolean found = true;

        resourceMofClassWrapper = findMofClassByName(secureUmlPackageWrapper, ModelConst.SECUREUML_RESOURCE_NAME);
        found = found &&
                ensureModelElementExistance(resourceMofClassWrapper);

        //MofClass actionMofClass = findMofClassByName(secureUmlPackage, ModelConst.SECUREUML_ACTION_NAME);
        atomicActionMofClassWrapper = findMofClassByName(secureUmlPackageWrapper, ModelConst.SECUREUML_ATOMIC_ACTION_NAME);
        found = found &&
                ensureModelElementExistance(atomicActionMofClassWrapper);

        compositeActionMofClassWrapper = findMofClassByName(secureUmlPackageWrapper, ModelConst.SECUREUML_COMPOSITE_ACTION_NAME);
        found = found &&
                ensureModelElementExistance(compositeActionMofClassWrapper);

        return found;
    }

    /**
     * @param startPackage
     */
    private boolean findDialectPackages(MofPackageWrapper startPackage)
    {
        boolean result = true;

        secureUmlPackage = findPackage(startPackage, ModelConst.SECUREUML_PACKAGE_NAME);
        result = result && ensureModelElementExistance(
                     secureUmlPackage);//Wrapper.getModelElement());
        secureUmlPackageWrapper = new MofPackageWrapper(secureUmlPackage);


        dialectDialectPackage = findPackage(startPackage, ModelConst.DIALECT_PACKAGE_NAME_PART);
        result = result && ensureModelElementExistance(
                     dialectDialectPackage);

        dialectDialectPackageWrapper = new MofPackageWrapper(dialectDialectPackage);

        String dialectName = dialectDialectPackageWrapper.getName();
        dialectName = dialectName.split(MetaModelConst.MDR_IMPL_SUFFIX)[0];
        dialectName = dialectName.split(ModelConst.DIALECT_PACKAGE_SUFFIX)[0];
        // now, e.g. name = "ComponentUml"

//        dialectPackage = findPackage(startPackage, dialectName);
//        result = result && ensureModelElementExistance(
//            dialectDialectPackage);
//
//        dialectPackageWrapper = new RefPackageWrapper(dialectDialectPackage);
//
//        dialectPackage = ((RefPackage)dialectDialectPackage).refOutermostPackage();
//        dialectPackageWrapper = new RefPackageWrapper(dialectPackage);
//        logger.info("DialectDialectPackage.refOutermostPackage: "
//            + dialectPackageWrapper.getName());

        secureModelPackage = findPackage(startPackage, ModelConst.SECUREMODEL_PACKAGE_NAME);
        if(secureModelPackage == null)
        {
            secureModelPackage =
                ((RefPackage)dialectDialectPackage).refOutermostPackage();
            //dialectPackageWrapper.getContainer();

        }

        secureModelPackageWrapper = new MofPackageWrapper(secureModelPackage);
        // logger.info(metamodelValidation,"found Package: "
        //     + secureModelPackageWrapper.getName());

//        result = result && ensureModelElementExistance(
//            secureModelPackage);//Wrapper.getModelElement());
//
        return result;
    }

    private boolean ensureModelElementExistance(Object modelElement)
    //throws Exception
    {
        if(modelElement == null ||
                (modelElement instanceof ModelElementWrapper
                 && ((ModelElementWrapper)
                     modelElement).getModelElement() == null))
        {
            logger.error(metamodelValidation,
                         "... NOT found - this ModelElement needs to exist in the Dialect Metamodel! exiting...");
            //System.exit(0);
            return false;
        }
        else
            return true;
    }

    /**
     * @param startPackage
     */
    private void examineMetamodel(MofPackageWrapper startPackage)
    {
        // workaround for a bug at unknown location
        if(startPackage.getModelElement()
                instanceof MofPackageWrapper)
            startPackage = (MofPackageWrapper)
                           startPackage.getModelElement();

        Collection<Association> actionResourceAssociations
            = new LinkedList<Association>();
        Collection<Association> otherAssociations
            = new LinkedList<Association>();

        examineMetamodelElements(startPackage, mmInfo, actionResourceAssociations, otherAssociations);

        //logger.info(metamodelExamination,"Examining Metamodel Associations in Package: " + startPackage.getName());
        examineMetamodelAssociations(actionResourceAssociations, otherAssociations, mmInfo);

        examineMofClassTags(mmInfo);
    }

    public boolean inheritsDirectlyFrom(Object mofClass, Object parentClass)
    {
        Object parent = null;
        try
        {
            Collection parents = (Collection)
                                 Util.getProperty(mofClass, "supertypes");
            parent = parents.iterator().next();
            //getParents().get(0);
        }
        catch (Exception e)
        {
            logger.logException(e);
        }

        if(parent!= null && parent == parentClass)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean inheritsFrom(MofClassWrapper mofClassWrapper, MofClassWrapper parentClassWrapper)
    {
        MofClassWrapper currentClassWrapper = mofClassWrapper;
        MofClassWrapper firstParentWrapper = null;



        String parentClassName = parentClassWrapper.getName();
        //Util.getProperty(parentClassWrapper, "name").toString();
        String mofClassName = mofClassWrapper.getName();

        do
        {
            try
            {
                /* Note: getParents() and getSuperTypes()
                 *  both return the superClasses of a MofClass
                 */
                Collection parents = (Collection)
                                     currentClassWrapper.getSupertypes();
                //.getSupertypes().get(0);
                //Collection supertypes = currentClass.getSupertypes();

                if(parents != null && parents.size() > 0 )
                    firstParentWrapper = new MofClassWrapper(
                        parents.iterator().next());
                else
                {
                    //firstParent = null;
                    break;
                }
                //MofClass firstSupertype = (MofClass) supertypes.iterator().next();

                String firstParentName = firstParentWrapper.getName();
                //String firstSupertypeString = firstSupertype.getName();
                // logger.info(metamodelExamination,
                //     "first Parent of '" + mofClassName
                //     + "' is: '" + firstParentName);


                // In case of Single Inheritance, there is only one Parent

                if(parents.contains(parentClassWrapper.getModelElement()))
                    //        || supertypes.contains(parentClass))
//                parent = (MofClass) currentClass.getParents().get(0);
//                if(parent!= null
//                        && parent != currentClass
//                        && parent == parentClass)
                {
                    return true;
                }
                else
                {
                    currentClassWrapper = firstParentWrapper;
                }

            }
            catch (Exception e)
            {
                logger.logException(e);
            }
        }
        while(firstParentWrapper != null);

        return false;
    }


    /* Traverses all the UML-Classes in the Metamodel-Package,
     * extracts the Resource-Types, Action-Types,
     * needed Stereotypes and the Dialect Package
     * and stores these to the MetaModelInfo Instance passed as Argument mmInfo
     */
    public void examineMetamodelElements(MofPackageWrapper startPackage, DialectMetaModelInfo mmInfo, Collection actionResourceAssociations, Collection otherAssociations)
    {
        // logger.info(metamodelExamination,
        //     "Examining Metamodel Elements in Package: "
        //     + startPackage.getName());

        Iterator it = startPackage.getContents().iterator();

        while(it.hasNext())
        {
            try
            {
                Object item = it.next();
                //logger.info(" - " + item);

                Object me = (Object) item ;

                NamedModelElementWrapper meWrapper = new NamedModelElementWrapper(me);


                if ( Util.hasType(me, "Association"))
                {
                    // logger.info(metamodelExamination,
                    //         "Association: " + meWrapper.getName() + " found");

                    Association association = (Association) me;

                    if(meWrapper.getName().startsWith(ModelConst.ACTION_RESOURCE_ASSOCIATION_NAME))
                    {
                        actionResourceAssociations.add(association);
                    }
                    else
                    {
                        otherAssociations.add(association);
                    }
                    // not of interest here
                }
//            // Stereotypes don't exist in MOF Model
//            else if (me instanceof Stereotype)
//            {
//
//                logger.info("STEREOTYPE found: " + me.getNameA());
//                if(me.getNameA().equals(ModelConst.STEREOTYPE_SECUML_ACTION))
//                {
//                    StereotypeSecumlAction = (Stereotype) me;
//                    logger.info("Stereotype: "+ ModelConst.STEREOTYPE_SECUML_ACTION + " found");
//                }
//            }
                else  if (Util.hasType(me, "MofClass"))
                    //( me instanceof MofClass )
                {
                    // logger.info(metamodelExamination,
                    //        "MofClass: " + meWrapper.getName() + " found");

                    Object mofClass = me;

                    MofClassWrapper mofClassWrapper = new MofClassWrapper(me);

                    //mofClass.allSupertypes()

                    //String name = mofClass.getNameA();

                    if(inheritsFrom(mofClassWrapper, this.resourceMofClassWrapper))
                    {
                        if(mofClassWrapper.isAbstract())
                            logger.info(metamodelExamination,
                                        "Abstract Resource Class found: "
                                        + mofClassWrapper.getName()
                                        + " (-> don't create ResourceType)");
                        else
                        {
                            ResourceType resourcetype = parseResourceTypeClass(mofClassWrapper);

                            // todo: extract method and set parentResourceType Property

                            mmInfo.addResourceType(resourcetype);
                        }
                    }
                    else if(inheritsFrom(mofClassWrapper, this.atomicActionMofClassWrapper))
                    {
                        AtomicActionType actiontype = MetaModelFactory.getInstance().
                                                      createAtomicActionType();
                        actiontype.setName(mofClassWrapper.getName());

                        modelmap.put(mofClassWrapper.getModelElement(), actiontype);

                        mmInfo.addAtomicActionType(actiontype);
                    }
                    else if(inheritsFrom(
                                mofClassWrapper,
                                this.compositeActionMofClassWrapper))
                    {
                        CompositeActionType actiontype = MetaModelFactory.getInstance().
                                                         createCompositeActionType();
                        actiontype.setName(mofClassWrapper.getName());

                        modelmap.put(mofClassWrapper.getModelElement(), actiontype);

                        mmInfo.addCompositeActionType(actiontype);
                    }
                    else
                    {
                        MetaModelClass metaModelClass = new MetaModelClass();
                        initMetaModelClass(mofClassWrapper, metaModelClass);

                        modelmap.put(mofClassWrapper.getModelElement(), metaModelClass);

                        mmInfo.addMetaModelClass(metaModelClass);
                    }
                    //newNode = caseClassifier( (Classifier) me);
                }
                else if (Util.hasType(me, "Tag"))
                    //(me instanceof Tag)
                {
                    Tag tag = (Tag) me;

                    String name = tag.getName();
                    String value = (String) tag.getValues().get(0);

                    TaggedValue taggedValue = new TaggedValue(name, value);

                    // logger.info(metamodelExamination,
                    //     "Tag found: " + name + " -> " + value);

                    modelmap.put(tag, taggedValue);


                    Collection elements = tag.getElements();


                    for (Iterator iter = elements.iterator(); iter.hasNext();)
                    {
                        Object element = (Object) iter.next();

                        if(element instanceof ModelElement)
                            modelmap.putModelElementTaggedValue((ModelElement)element, taggedValue);

//                    if(element instanceof ModelElement)
//                        modelmap.putModelElementTag((ModelElement)element, tag);

//                    if (element instanceof Association)
//                    {
//                        Association association = (Association) element;
//
//                        modelmap.putAssociationTag(association, tag);
//                    }
//                    else if (element instanceof AssociationEnd)
//                    {
//                        AssociationEnd associationEnd =
//                            (AssociationEnd) element;
//
//                        modelmap.putAssociationEndTag(associationEnd, tag);
//                    }
//                    else if (element instanceof MofClass)
//                    {
//                        MofClass mofClass = (MofClass) element;
//                        modelmap.putMofClassTag((MofClass)element, tag);
//                    }
                    }
                }
                else if (Util.hasType(me, "MofPackage"))
                    //( me instanceof RefPackage )
                {
                    try
                    {
                        MofPackage p = (MofPackage)me;

                        //String packageName = p.getName().toLowerCase();
                        //String namePart = ModelConst.DIALECT_PACKAGE_NAME_PART;

//                    // if dialectPackage not found yet && ...
//                    if(mmInfo.getDialectName() == null &&
//                            packageName.indexOf(namePart) != -1)
//                    {
//                        logger.info("Dialect Package found: " + p.getName());
//                        mmInfo.setDialectName(p.getName());
//                    }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    examineMetamodelElements(
                        new MofPackageWrapper((MofPackage)me),
                        mmInfo,
                        actionResourceAssociations,
                        otherAssociations);
                }
                else
                {
                    //newNode = new DefaultMutableTreeNode("[???] " + me.getNameA() + " (" + me.getClass().getName() + ")");
                    //logger.info(me.getNameA() + " skipped");
                }
            }
            catch (Exception e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    private void examineMofClassTags(DialectMetaModelInfo mmInfo)
    {
        try
        {
            //logger.info("### examining Tags of " + mmInfo.getResourceTypes().size() + " ResourceTypes");
            for (Iterator iter = mmInfo.getResourceTypesAndMetaModelClasses().iterator(); iter.hasNext();)
            {
                try
                {
                    MetaModelClass metaModelClass = (MetaModelClass) iter.next();

                    if(!modelmap.containsReverseMapping(metaModelClass))
                        continue;

                    // logger.info(metamodelExamination,
                    //     "### examining Tags of " + metaModelClass.getName());


                    MofClass metaModelClassMofClass = (MofClass) modelmap.getMofElement(metaModelClass);

                    Collection<TaggedValue> tags =
                        modelmap.getModelElementTaggedValues(metaModelClassMofClass);
                    //modelmap.getMofClassTags(resourceTypeClass);

                    if(tags == null)
                        continue;
                    // else

                    for (Iterator iterator = metaModelClass.getAttributes().iterator(); iterator
                            .hasNext();)
                    {
                        MetaModelClassAttribute rta = (MetaModelClassAttribute) iterator.next();

                        Collection<TaggedValue> attributeTags =
                            modelmap.getModelElementTaggedValues(metaModelClassMofClass);

                        for (Iterator it = attributeTags.iterator(); it.hasNext();)
                        {
                            TaggedValue taggedValue = (TaggedValue) it.next();

                            String name = taggedValue.getName();
                            String value = taggedValue.getValue().toString();

                            if(MetaModelConst.TAG_ATTRIBUTE_UML_PROPERTY_NAME.equals(name))
                            {
                                rta.setUmlName(value);
                                //rta.setTypeName(typeName)
                                logger.info("found MMClass Attribute Tag with UML Property Name: " + value);
                            }

                        }

                    }


                    for (Iterator iterator = tags.iterator(); iterator.hasNext();)
                    {
                        TaggedValue taggedValue = (TaggedValue) iterator.next();

                        String name = taggedValue.getName();
                        String value = taggedValue.getValue().toString();

                        if(name.equals(MetaModelConst.TAG_UML_CLASS_NAME))
                        {
                            metaModelClass.setUmlClassName(value);
                        }

                        if (metaModelClass instanceof ResourceType)
                        {
                            ResourceType resourceType = (ResourceType) metaModelClass;

                            if(name.equals(MetaModelConst.TAG_ACTION_STEREOTYPE))
                            {
                                resourceType.setActionStereotype(value);
                            }
                            else if(name.equals(MetaModelConst.TAG_ANCHOR_PATH))
                            {
                                resourceType.setAnchorPath(value);
                            }
                            else if(name.equals(MetaModelConst.TAG_MODELELEMENT_STEREOTYPE))
                            {
                                resourceType.setModelElementStereotype(value);
                            }
                            else if(name.equals(MetaModelConst.TAG_RESOURCE_PATH))
                            {
                                resourceType.setResoucePath(value);
                            }
                        }
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //logger.logException(e);
                }
            }

            for (Iterator iter = mmInfo.getActionTypes().iterator(); iter.hasNext();)
            {
                ActionType actiontype = (ActionType) iter.next();

                MofClass actiontypeMofClass = (MofClass) modelmap.getMofElement(actiontype);


                TaggedValue subactionsDefinitionTag =
                    modelmap.getModelElementTaggedValue(
                        actiontypeMofClass,
                        MetaModelConst.TAG_COMPOSITE_ACTION_SUBACTIONS_DEFINITION);
                if(subactionsDefinitionTag != null && actiontype instanceof CompositeActionType)
                {
                    CompositeActionType compositeActiontype =
                        (CompositeActionType) actiontype;
                    String oclExpression = subactionsDefinitionTag.getValue();
                    compositeActiontype.setSubactionsDefinition(oclExpression);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * @param mofClassWrapper
     * @return the resource type
     */
    private ResourceType parseResourceTypeClass(MofClassWrapper mofClassWrapper)
    {
        ResourceType resourcetype = new ResourceType();


        initMetaModelClass(mofClassWrapper, resourcetype);


        modelmap.put(mofClassWrapper.getModelElement(), resourcetype);

        return resourcetype;
    }



    /**
     * @param mofClass
     * @param metaModelClass
     */
    private void initMetaModelClass(MofClassWrapper mofClass, MetaModelClass metaModelClass)
    {


        metaModelClass.setName(mofClass.getName());

        // parse the Attributes of the ResourceTypes
        Collection attributes = mofClass.getContents();//allAttributes()
        for (Iterator iter = attributes.iterator(); iter.hasNext();)
        {
            try
            {
                Object o = iter.next();
                if (o instanceof Attribute)
                {
                    Attribute attribute = (Attribute) o;

                    if(attribute.getContainer() == mofClass.getModelElement())
                        // don't consider inherited attributes
                    {

                        AttributeWrapper attributeWrapper =
                            new AttributeWrapper(attribute);

                        MetaModelClassAttribute rta =
                            new MetaModelClassAttribute(attributeWrapper.getName());

                        modelmap.put(attributeWrapper.getModelElement(), rta);


                        metaModelClass.getAttributes().add(rta);

                        Object attributeType = attributeWrapper.getType();
                        String attributeTypeName = attributeType.getClass().getSimpleName();
                        attributeTypeName = attributeTypeName.split(MetaModelConst.MDR_IMPL_SUFFIX_REGEXP)[0];
                        rta.setTypeName(attributeTypeName);
                    }

                }

            }
            catch (Exception e)
            {
                logger.logException(e);
            }
        }
    }

    /* Parses & caches all Associations between Resources
     * and Actions (with name "actionResource")
     */
    protected void examineMetamodelAssociations(Collection<Association> actionResourceAssociations, Collection<Association> otherAssociations, DialectMetaModelInfo mmInfo)
    {
        examineActionResourceAssociations(actionResourceAssociations, mmInfo);

        examineResourceTypeHierarchies(mmInfo.getResourceTypes(), mmInfo);

        examineInterResourceAssociations(otherAssociations ,mmInfo);
    }

    /**
     * @param actionResourceAssociations
     * @param mmInfo
     */
    private void examineActionResourceAssociations(Collection<Association> actionResourceAssociations, DialectMetaModelInfo mmInfo)
    {
        try
        {
            for (Iterator iter = actionResourceAssociations.iterator(); iter
                    .hasNext();)
            {
                Association association = (Association) iter.next();

                // logger.info(metamodelExamination,
                //         "### Association Contents: ");


                ActionType actionType = null;
                ResourceType resourceType = null;


                // an Association contains 2 AssociationEnds
                for (Iterator iterator = association.getContents().iterator(); iterator
                        .hasNext();)
                {
                    Object item = iterator.next();
                    if (item instanceof AssociationEnd)
                    {
                        AssociationEnd associationEnd = (AssociationEnd) item;

                        try
                        {
                            Classifier classifier = associationEnd.getType();
                            //                            logger.info("AssociationEnd.getTypeA().getNameA(): "
                            //                                    + classifier.getNameA());
                            //
                            MetaModelEntity metaModelEntity =
                                modelmap.getElement(classifier);

                            if (metaModelEntity instanceof ResourceType)
                            {
                                resourceType = (ResourceType) metaModelEntity;
                                //logger.info(" - ResourceType: " + resourceType.toString());
                            }
                            else if (metaModelEntity instanceof ActionType)
                            {
                                actionType = (ActionType) metaModelEntity;
                                //logger.info(" - ActionType: " + actionType.getName());
                            }

                            //Object[] qualifiers = associationEnd.getQualifiersA().toArray();
                            //logger.info("");
                        }
                        catch (Exception e)
                        {
                            logger.logException(e);
                        }
                    }
                    else
                    {
                        // logger.info(metamodelExamination,
                        //         "- " + item.toString());
                    }

                    //logger.info("- " + element.toString());
                }


                if(actionType != null && resourceType != null)
                {
                    String shortname = actionType.getName();

                    //Collection<Tag> tags = modelmap.getModelElementTags(association);
                    //modelmap.getAssociationTag(association);

                    TaggedValue taggedValue = modelmap.getModelElementTaggedValue(association, MetaModelConst.TAG_SHORTNAME);

                    // use the short name if available
                    try
                    {
                        if(taggedValue == null || taggedValue.getValue() == null || taggedValue.getValue().length() == 0)
                        {
                            logger.warn(metamodelExamination,
                                        "no shortname defined for Association "
                                        + association.getName());
                        }
                        else //if(tag.getName().equals(ModelConst.TAG_SHORTNAME))
                            // this is the case, because checked above
                        {
                            shortname = (String)taggedValue.getValue();
                        }
                        actionType.setShortName(shortname);
                    }
                    catch (Exception e)
                    {
                        //logger.info("problem with Association " + association.getName() +
                        //        " and Tag " + tag);
                        logger.logException(e);
                    }

                    // logger.info(metamodelExamination,
                    //     "ResourceAction Association: "
                    //     + resourceType.getName()
                    //     + " -> "
                    //     + actionType.getName());

                    mmInfo.addResourceActionAssociation(shortname, resourceType, actionType);
                }
            }
        }
        catch (Exception e)
        {
            logger.logException(e);
        }
    }

    private void examineResourceTypeHierarchies(Collection<ResourceType> resourceTypes, DialectMetaModelInfo mmInfo)
    {
        for (Iterator iter = resourceTypes.iterator(); iter.hasNext();)
        {
            try
            {
                ResourceType resourceType = (ResourceType) iter.next();
                MofClass resourceClass = (MofClass)
                                         modelmap.getMofElement(resourceType);

                MofClass parentResourceClass = (MofClass)
                                               resourceClass.getSupertypes().get(0);//getParents().get(0);

                if(modelmap.containsMapping(parentResourceClass)
                        && modelmap.getElement(parentResourceClass)
                        instanceof ResourceType)
                {
                    ResourceType parentResourceType = (ResourceType)
                                                      modelmap.getElement(parentResourceClass);
                    resourceType.setParentResourceType(parentResourceType);
                }
                else
                {
                    // parent == "Resource" or null
                    resourceType.setParentResourceType(null);
                }
            }
            catch (Exception e)
            {
                logger.logException(e);
            }

        }

    }

    private void examineInterResourceAssociations(Collection<Association> associations, DialectMetaModelInfo mmInfo)
    /** Relations between ResourceTypes are examined
     *  and stored in a Map
     *
     */
    {
        for (Iterator iter = associations.iterator(); iter.hasNext();)
        {
            Association mofAssociation = (Association) iter.next();

            AssociationEnd end1 = null;
            AssociationEnd end2 = null;

            // logger.info(metamodelExamination,
            // "### Association '" + mofAssociation.getName() +
            //     "' between: ");
//
            String name = mofAssociation.getName();

            MofClass end1Class = null;
            MofClass end2Class = null;
            String end1Name = null;
            String end2Name = null;

            MetaModelEntity end1Type = null;
            MetaModelEntity end2Type = null;

            // an Association contains 2 AssociationEnds
            for (Iterator iterator = mofAssociation.getContents().iterator();
                    iterator.hasNext();)
            {
                Object item = iterator.next();
                if (item instanceof AssociationEnd)
                {
                    AssociationEnd associationEnd = (AssociationEnd) item;

                    try
                    {

                        end1 = associationEnd;
                        end1Class = (MofClass)
                                    associationEnd.getType();
                        end1Name = associationEnd.getName();
                        // logger.info(metamodelExamination,
                        //         " - (end1)" +
                        //         end1Class.getName());


                        associationEnd = (AssociationEnd) iterator.next();


                        end2 = associationEnd;
                        end2Class = (MofClass)
                                    associationEnd.getType();
                        end2Name = associationEnd.getName();
                        // logger.info(metamodelExamination,
                        //         " - (end2) " +
                        //         end2Class.getName());
                    }
                    catch (Exception e)
                    {
                        logger.logException(e);
                    }
                }
            }

            if(end1Class != null && end2Class != null)
            {
                if(modelmap.containsMapping(end1Class))
                    end1Type = modelmap.getElement(end1Class);
                if(modelmap.containsMapping(end2Class))
                    end2Type = modelmap.getElement(end2Class);

                //logger.info(metamodelExamination, "... mapped");

                if(        end1Type != null
                           && end2Type != null
                           && end1Type instanceof MetaModelClass
                           && end2Type instanceof MetaModelClass
                  )
                {
                    InterResourceAssociation interResourceAssociation =
                        mmInfo.addInterResourceAssociation(name, end1Type, end2Type);

                    interResourceAssociation.getEnd1().setName(end1Name);
                    interResourceAssociation.getEnd2().setName(end2Name);

                    interResourceAssociation.getEnd1().setType((MetaModelClass)end1Type);
                    interResourceAssociation.getEnd2().setType((MetaModelClass)end2Type);

                    interResourceAssociation.getEnd1().setMultiple(end1.getMultiplicity().getUpper()>1);//isMultiple());
                    interResourceAssociation.getEnd2().setMultiple(end2.getMultiplicity().getUpper()>1);//isMultiple());

                    try
                    {
                        TaggedValue end1TaggedValue = (TaggedValue)
                                                      modelmap.getModelElementTaggedValue(
                                                          end1,
                                                          MetaModelConst.TAG_UML_PROPERTY_GETTER);
                        logger.info("uml property getter found: "+end1TaggedValue.getValue());
                        interResourceAssociation.getEnd1().
                        setUmlPropertyGetter(
                            end1TaggedValue.
                            getValue());
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        /* don't care when tag is not there because it's optional,
                         * has a default value and is not always needed
                         */
                    }

                    try
                    {
                        TaggedValue end2TaggedValue = (TaggedValue)
                                                      modelmap.getModelElementTaggedValue(
                                                          end2,
                                                          MetaModelConst.TAG_UML_PROPERTY_GETTER);

                        logger.info("uml property getter found: "+end2TaggedValue.getValue());
                        interResourceAssociation.getEnd2().
                        setUmlPropertyGetter(
                            end2TaggedValue.
                            getValue());
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        /* don't care when tag is not there because it's optional,
                         * has a default value and is not always needed
                         */
                    }
                    //associations.add(containmentAssociation);
                }
            }
            else
            {
                logger.info(" ... is no containment association");
            }
        }
    }
    /** Breadth first search of the package named name "SecureUML"
     *
     */
    public Object findPackage(MofPackageWrapper mofPackageWrapper, String namePart)
    {

        if(mofPackageWrapper == null || namePart == null)
            return null;



        //***
        String nofElements = "??";

        //***



        //        logger.info(metamodelValidation,
        //      "Searching Package *" + namePart);// +
//                "* among "
//                + nofElements
        //+ mofPackageWrapper.getContents().size()
//                + " Elements...");

        if(mofPackageWrapper.getName()
                //.getModelElement().getClass().getSimpleName()
                .toLowerCase().contains(namePart.toLowerCase()))
            return mofPackageWrapper;

        Object result = null;

        Object pk = mofPackageWrapper.getModelElement();
        //Package pk = (Package) pkg;


        LinkedList myElements =
            new LinkedList();

        int index = 0;
        do
        {
            // save Variant of "myElements.addAll(pk.getOwnedElement)"
            if(pk != null)
            {
                //Util.printInterfaces(pk.getClass());
//                Collection contents = (Collection)
//                  Util.getProperty(pk, "contents");
//                  //pk.getContents();
                searchAndAddAllElements(mofPackageWrapper, myElements);
            }


            Object modelelement = myElements.get(index++);

            //if (Util.hasType(modelelement, "MofPackage"))
            //modelelement instanceof MofPackage )
            //{
            pk = /*(MofPackage)*/ modelelement;
            MofPackageWrapper pkWrapper =
                new MofPackageWrapper(pk);

            //String name = Util.getProperty(pk, "name").toString();

            String name =  pkWrapper.getName();
            //pk.getClass().getSimpleName();

            //pk.getName();


            //newNode = casePackage( (Package) me);
//                logger.info(metamodelExamination, prefix +
//                        //" Package: " +
//                        modelelement.getClass().getInterfaces()[0].getSimpleName() +
//                        ": " +
//                        name);

            if(name.toLowerCase().contains(namePart.toLowerCase()))
            {

                //logger.info(metamodelValidation, "Package '" + namePart + "' found: " + pk.getNameA());
                //secureUmlPackage =
                if(result == null)
                {
                    // logger.info(metamodelValidation,
                    //     "found Package: "
                    //     +  pkWrapper.getName());
                    //pk.getClass().getSimpleName());

                    result = pk;
                }
                else
                {
                    logger.warn(metamodelValidation,"... but more than one match for findPackage(" + namePart + ")");
                }
            }
            else
            {
                //logger.info("some Package found: " + pk.getNameA());

                //return findSecureUmlPackage((MofPackageWrapper)modelelement);
            }
            //}
            /*else */
            if (Util.hasType(modelelement, "Import"))
                //(modelelement instanceof Import)
            {
                //Import im = (Import) modelelement;
                Object importedNamespace =
                    Util.getProperty(modelelement, "importedNamespace");

                searchAndAddAllElements(new MofPackageWrapper(importedNamespace), myElements);

//                Collection contents = (Collection)
//                  Util.getProperty(importedNamespace, "contents");
//
                //= im.getImportedNamespace().getContents();

                //addAllWithoutDuplicates(myElements, contents);
            }
//            else
//            {
//                logger.info("NOT SecureUML Package: " + modelelement.getClass().getSimpleName());
//                pk = null;
//            }


        }
        while (myElements.size() > index);

        return result;
    }

    /**
     * @param pkg
     * @param myElements
     */
    private void searchAndAddAllElements(RefPackageWrapper pkg, LinkedList<ModelElement> myElements)
    {
        String nofElements;

        Collection allClasses = pkg.allClasses();
        if(allClasses != null)
        {
            nofElements = "" + allClasses.size();
            //logger.info("allClasses: " + nofElements);
            addAllWithoutDuplicates(myElements, allClasses);
        }


        Collection allAssociations = pkg.allAssociations();
        if(allClasses != null)
        {
            nofElements = "" + allAssociations.size();
            //logger.info("allAssociations: " + nofElements);
            addAllWithoutDuplicates(myElements, allAssociations);
        }


        Collection allPackages = pkg.allPackages();
        if(allPackages != null)
        {
            nofElements = "" + allPackages.size();
            //logger.info("allPackages: " + nofElements);
            addAllWithoutDuplicates(myElements, allPackages);
        }
    }

    private void searchAndAddAllElements(MofPackageWrapper pkg, LinkedList<ModelElement> myElements)
    {
        Collection contents = pkg.getContents();
        if(contents != null)
        {
            String nofElements = "" + contents.size();
            //logger.info("allPackages: " + nofElements);
            addAllWithoutDuplicates(myElements, contents);
        }
    }



    /** Add all elements of additionalElements to
     * collection - catch possible Exceptions for each
     * Element seperately
     *
     * @param collection
     * @param additionalElements
     */
    private void addAllWithoutDuplicates(LinkedList collection, Collection additionalElements)
    {
        for (Iterator iter = additionalElements.iterator(); iter.hasNext();)
        {
            try
            {
                Object element = (Object) iter.next();
                if(!collection.contains(element))
                    collection.add(element);
            }
            catch (Exception e)
            {
                logger.logException(e);
            }
        }
    }


//
//    public void traverseGenericPackage(Package pkg)
//    {
//        prefix = prefix + "-";
//
//
//        Package pk = (Package) pkg;
//
//        Collection elements = pk.getOwnedElement();
//        Iterator it = elements.iterator();
//        while ( it.hasNext() )
//        {
//            ModelElement me = (ModelElement) it.next();
//
//            if ( me instanceof Association )
//            {
//                logger.info(prefix + " Association: " + me.getNameA());
//
//                //newNode = caseAssociation( (Association) me);
//            }
//            else  if ( me instanceof Classifier )
//            {
//                logger.info(prefix + " Classifier: " + me.getNameA());
//                //newNode = caseClassifier( (Classifier) me);
//            }
//            else if ( me instanceof Package )
//            {
//                //newNode = casePackage( (Package) me);
//                logger.info(prefix + " Package: " + me.getNameA());
//                traverseGenericPackage((Package)me);
//            }
//            else
//            {
//                //newNode = new DefaultMutableTreeNode("[???] " + me.getNameA() + " (" + me.getClass().getName() + ")");
//            }
//        }
//
//        if(prefix.length() >1)
//            prefix = prefix.substring(0, prefix.length()-1);
//
//    }

    public void traverseMofPackage(ModelElement pk)
    {

        Association association;
        Tag tag;
        Object mofPackage;

        // tag.getName() == stereotype
        // tag.getValues().get(0) == secuml.action
        // tag.getElements().iterator().next() ==
        // "the Association to which the Tag belongs"



        prefix = prefix + "-";


        Collection elements = (Collection)
                              Util.getProperty(pk, "contents");
        //pk.getContents();

        Iterator it = elements.iterator();

        //logger.info("##" + elements.size());

        while ( it.hasNext() )
        {
            ModelElement me = (ModelElement) it.next();
            logger.info("###" + me.getName() + ": "
                        + me.getClass().getCanonicalName());
            logger.info(me.toString());


            if ( me instanceof Association )
            {
                logger.info(prefix + " Association: " + me.getName());

                //newNode = caseAssociation( (Association) me);
            }
            else  if ( me instanceof Classifier )
            {
                logger.info(prefix + " Classifier: " + me.getName());
                //newNode = caseClassifier( (Classifier) me);
            }
//            else if ( me instanceof Package )
//            {
//                //newNode = casePackage( (Package) me);
//                logger.info(prefix + " Package: " + me.getNameA());
//                traverseMofPackageWrapper((MofPackageWrapper)me);
//            }
            else if (Util.hasType(me, "MofPackage"))//(me instanceof MofPackage)
            {
                logger.info(prefix + " MofPackageWrapper: " + me.getName());
                traverseMofPackage(me);


                //newNode = new DefaultMutableTreeNode("[???] " + me.getNameA() + " (" + me.getClass().getName() + ")");

            }
            else if(me instanceof Import)
            {
                Import im = (Import)me;

                logger.info(prefix + " Import: " + me.getName());

                traverseMofPackage(im.getImportedNamespace());
            }
            else if (me instanceof Namespace)
            {
                logger.info(prefix + " Namespace: " + me.getName());
                traverseMofPackage((Namespace) me);

                //newNode = new DefaultMutableTreeNode("[???] " + me.getNameA() + " (" + me.getClass().getName() + ")");

            }
            else if (me instanceof Tag)
            {
                Tag t = (Tag)me;
                logger.info(prefix + " Tag: " +
                            t.getName() + " ( " + t.getValues().get(0) + " ) ");
            }
            else if(me instanceof Association)
            {
                Association a = (Association) me;

            }
            else
            {
                logger.info(prefix + " else: " + me.getName());
                logger.info(me.toString());
            }


        }

        if(prefix.length() >1)
            prefix = prefix.substring(0, prefix.length()-1);

    }







}
