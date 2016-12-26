package ch.ethz.infsec.secureumlgui.modelmapping;

import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jmi.model.MofPackage;
import javax.jmi.reflect.RefObject;
import javax.jmi.reflect.RefPackage;

import org.apache.log4j.Logger;
import org.argouml.model.ClassDiagram;
import org.argouml.model.CoreHelper;
import org.argouml.model.DiDiagram;
import org.argouml.model.DiagramInterchangeModel;
import org.argouml.model.Facade;
import org.argouml.model.Model;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.static_structure.ClassDiagramGraphModel;
import org.argouml.uml.diagram.static_structure.ui.ClassDiagramRenderer;
import org.argouml.uml.diagram.static_structure.ui.FigClass;
import org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram;
import org.argouml.uml.ui.ActionClassDiagram;
import org.netbeans.api.mdr.MDRManager;

import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.AssociationClass;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.modelmanagement.UmlPackage;
import org.tigris.gef.base.Layer;
import org.tigris.gef.base.LayerDiagram;
import org.tigris.gef.presentation.FigNode;

import ch.ethz.infsec.secureumlgui.Util;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.CompositeActionType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.DialectMetaModelInfo;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.InterResourceAssociation;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClass;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.MetaModelClassAttribute;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.metamodel.ResourceType;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclExpression;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclExpressionEvaluator;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.oclexpressions.OclExpressionsParser;
import ch.ethz.infsec.secureumlgui.wrapper.ActionWrapper;
import ch.ethz.infsec.secureumlgui.wrapper.PolicyWrapper;
import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants.SECUML_STEREOTYPES;
import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants.UML_OCL;
import ch.ethz.infsec.secureumlgui.modelmanagement.ModelConst;
//FIXME: is this missing intentionally? ask Marcel...
//import ch.ethz.infsec.secureumlgui.modelmapping.strategies.MapAll;
import ch.ethz.infsec.secureumlgui.modelmapping.permissions.HierarchicalPolicyExplorer;
import ch.ethz.infsec.secureumlgui.modelmapping.strategies.MapSelfAndAssociatedResources;
import ch.ethz.infsec.secureumlgui.modelmapping.strategies.MappingScopeStrategy;

import ch.ethz.infsec.secureumlgui.securemodelimpl.SecureModelFactory;

/**
 * A generic dialect mapper that maps dialect specific elements in the UML model
 * to dialect metamodel instances, according to the annotations in the dialect
 * metamodel.
 *
 *
 */
public class GenericDialectModelMapper extends SecureUmlModelMapper {

    private static GenericDialectModelMapper instance;

    private ModelWriter modelWriter;

    MappingScopeStrategy mappingScopeStrategy = null;

    /** all uml classes representing roles */
    Set<UmlClass> roleClasses = new LinkedHashSet<UmlClass>();

    /** all actions for resources in the model */
    Set<Object> actions = new LinkedHashSet<Object>();

    /** all uml classes representing polices */
    Set<UmlClass> policyClasses = new LinkedHashSet<UmlClass>();

    /** all association classes representing permissions */
    Set<AssociationClass> permissionsAssociations = new LinkedHashSet<AssociationClass>();

    /** all inheritance relations between roles */
    Set<Generalization> roleHierarchyGeneralizations = new LinkedHashSet<Generalization>();

    /** all inheritance relations between policies */
    Set<Generalization> policyHierarchyGeneralizations = new LinkedHashSet<Generalization>();

    // Set<Object> resources = new LinkedHashSet<Object>();

    /** all policy assignments: the key are permissions (Association Class), values are policies **/
    //Map<AssociationClass, UmlClass> policyAssignments = new HashMap<AssociationClass, UmlClass>();
    Map<String, UmlClass> policyAssignments = new HashMap<String, UmlClass>();



    Map<String, AssociationClass> permissionsPerPolicy = new HashMap<String, AssociationClass>();


    Map<UML_OCL, UmlClass> oclMappings = new HashMap<UML_OCL, UmlClass>();

    private static Logger aLog = Logger.getLogger(GenericDialectModelMapper.class);

    private Namespace root_namespace = null;

    private UmlPackage secUMLPackage = null;
    private UmlPackage permissionPackage = null;
    private UmlPackage oclPackage = null;

    /**
     *
     */
    public GenericDialectModelMapper(DialectMetaModelInfo dialectMetaModelInfo) {
        mappingScopeStrategy = new
        // MapAll(
        MapSelfAndAssociatedResources(dialectMetaModelInfo);

        this.dialectMetaModelInfo = dialectMetaModelInfo;

        if (instance != null)
            logger.error("GenericDialectModelMapper instantiated twice!");
        instance = this;

        logger.disableLoggerContext(logger.MODELMAPPER_DETAILLED);
    }


    /**
     * initialize ModelMapper: clear all cached mapped elements.
     *
     * Also deletes and recreated the dialect model extent, so that we don't
     * accumulate duplicate elements in the repository.
     */
    public void init() {
        super.init();

        roleClasses.clear();
        policyClasses.clear();
        permissionsAssociations.clear();
        roleHierarchyGeneralizations.clear();
        policyHierarchyGeneralizations.clear();
        policyAssignments.clear();

        actions.clear();

        permissionsPerPolicy.clear();

        // equivalent: dialectMetaModelInfo.getDialectExtent().refDelete();
        MDRManager.getDefault().getDefaultRepository().getExtent(
            "mySecureModel").refDelete();

        RefPackage model = null;
        try {
            model = MDRManager.getDefault().getDefaultRepository()
                    .createExtent("mySecureModel",
                                  dialectMetaModelInfo.getDialectMetaExtent());
        } catch (Exception e) {
            logger.logException(e);
        }

        dialectMetaModelInfo.setDialectExtent(model);

        aLog.debug("GenericDialectModelMapper cleared");
        // logger.info("GenericDialectModelMapper cleared");

        //ensureNeededElementsExist(null);
    }

    public static GenericDialectModelMapper getInstance() {
        if (instance == null)
            logger.info("GenericDialectModelMapper not yet instantiated!");
        return instance;
    }

    public ModelWriter getModelWriter() {
        if (modelWriter == null && map != null)
            modelWriter = new ModelWriter(map);
        return modelWriter;
    }

    public void setModelWriter(ModelWriter modelWriter) {
        this.modelWriter = modelWriter;
    }

    // = new MapNamespaceContents();
    /**
     * @return the mappingScopeStrategy
     */
    public MappingScopeStrategy getMappingScopeStrategy() {
        return mappingScopeStrategy;
    }

    /**
     * @param mappingScopeStrategy
     *            the mappingScopeStrategy to set
     */
    public void setMappingScopeStrategy(
        MappingScopeStrategy mappingScopeStrategy) {
        this.mappingScopeStrategy = mappingScopeStrategy;
    }

    /**
     * If <code>modelElement</code> represents a resource, map it, and create
     * all actions for this resource.
     *
     * Does nothing otherwise.
     */
    public void examineModelElement(ModelElement modelElement) {
        if ( modelElement == null ) {
            aLog.error("#######################################");
            aLog.error("modelElement == null!!!!");
            aLog.error("#######################################");
            System.out.println("#######################################");
            System.out.println("modelElement == null!!!!");
            System.out.println("#######################################");
//			try {
//				throw new Exception();
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
            return;
        }
        else {
            aLog.debug("examineModelElement: " + modelElement.getName());
            //ensureNeededElementsExist(modelElement);
        }
        ResourceType resourceType = helper.getResourceType(modelElement);
        MetaModelClass metaModelClass = resourceType;

        if (metaModelClass == null) {
            metaModelClass = helper.getMetaModelClass(modelElement);
            logger.info("MetaModelClass for "
                        + modelElement.getName()
                        + ": "
                        + (metaModelClass == null ? "null" : metaModelClass
                           .getName()));

            if ( modelElement instanceof Stereotype ) {
                map.putStereotype((Stereotype)modelElement);
            }

//			aLog.debug("MetaModelClass for "
//					+ modelElement.getName()
//					+ ": "
//					+ (metaModelClass == null ? "null" : metaModelClass
//							.getName()) + " ::: "+ modelElement.getClass() + " (" + (modelElement instanceof Stereotype) + "), ");
//			//aLog.debug("Modelmap.put(" + src.getClass() + " (" + (src instanceof Stereotype) + "), " + target.getClass() +" (" + (target instanceof Stereotype) + ")");
        }

        RefObject metaModelObject = null;
        if (metaModelClass != null) {
            aLog.info("mapping model element " + modelElement.getName() + " metaModelClass: " + metaModelClass.getName());
            logger.info("mapping model element " + modelElement.getName());
            metaModelObject = createMetaModelClass(metaModelClass, modelElement);
            setProperties(metaModelClass, metaModelObject, modelElement);
            instantiateAllActions(resourceType, metaModelObject);
            map.put(modelElement, metaModelObject);
            // resources.add(metaModelObject);
        }
    }

    /**
     * create a resource as an instance of <code>metaModelClass</code>,
     * corresponding to the UML element <code>modelElement</code>.
     */
    public RefObject createMetaModelClass(MetaModelClass metaModelClass,
                                          ModelElement modelElement) {
        RefObject newMetaModelClassObject = null;
        Object metaModelClassObject = null;
        try {
            RefPackage dialectPackage = getDialectPackage();
            metaModelClassObject = Util.invokeParameterlessMethod(
                                       getDialectPackage(), "get" + metaModelClass.getName());

            newMetaModelClassObject = (RefObject) Util
                                      .invokeParameterlessMethod(metaModelClassObject, "create"
                                              + metaModelClass.getName());

        } catch (Exception e) {
            logger.logException(e);
            Util.printInterfaces(metaModelClassObject.getClass());
        }
        return newMetaModelClassObject;
    }

    /**
     * returns the action specified by reference from the given resource. If the
     * Action has been created yet (i.e. corresponding Property of the Resource
     * is set), the Action is just returned.
     *
     * Otherwise (if not there), the Action is created (lazy), the corresponding
     * Property of the Resource is set and then the Action is returned.
     *
     * @param resource
     *            the resource for which the action is to be created.
     * @param shortActionName
     *            the name by which the action is referenced from the resource
     * @return the action
     */
    @Override
    public RefObject getOrCreateAction(RefObject resource,
                                       String shortActionName) {
        RefObject newActionObject = null;
        if (resource == null) {
            logger.error("resource = null in getOrCreateAction");
        }
        ResourceType resourceType = dialectMetaModelInfo
                                    .getResourceType(resource);

        if (resourceType == null) {
            logger.error("could not get resourceType for resource " + resource);
            return null;
        }
        ActionType actionType = dialectMetaModelInfo.getActionType(
                                    resourceType, shortActionName);

        if (actionType != null) {
            if (!actionType.getShortName().equals(shortActionName))
                logger.error("actionType.getShortName() = "
                             + actionType.getShortName() + ", shortActionName = "
                             + shortActionName + ".");

            newActionObject = (RefObject) Util.getProperty(resource, actionType
                              .getShortName());

            if (newActionObject == null) {
                newActionObject = SecureModelFactory.getInstance()
                                  .createAction(actionType);
                if (newActionObject == null)
                    logger.error("createAction failed");
                Util.setProperty(resource, actionType.getShortName(),
                                 newActionObject);

                Collection resourceActions = (Collection) Util.getProperty(
                                                 resource, "action");
                if (newActionObject != null
                        && !resourceActions.contains(newActionObject)) {
                    resourceActions.add(newActionObject);
                    actions.add(newActionObject);
                }
            }
        } else {
            logger.error("did not get actiontype for " + shortActionName
                         + " on " + resourceType.getName());
        }
        return newActionObject;
    }

    /**
     * Only String Properties are supported
     *
     * @param metaModelClass
     * @param resourceObject
     * @param modelElement
     */
    public void setProperties(MetaModelClass metaModelClass,
                              RefObject resourceObject, ModelElement modelElement) {
        for (Iterator iter = metaModelClass.getAttributes().iterator(); iter
                .hasNext();) {
            try {
                MetaModelClassAttribute rta = (MetaModelClassAttribute) iter
                                              .next();

                // get
                // Class[] argTypes = new Class[0];

                // String getter = rta.getUmlGetterName();

                String propertyName = rta.getUmlName();

                if (propertyName == null || propertyName.length() == 0)
                    // if no UMLPropertyName specified,
                    // use the attribute name as default
                    propertyName = rta.getName();

                // logger.info("setting property '"
                // + metaModelClass.getName() + "." + propertyName
                // + " of "
                // + resourceObject.getClass().getSimpleName());

                if (propertyName != null) {
                    Object attributeValue = Util.tryGetProperty(modelElement,
                                            propertyName);

                    // logger.info("... to Value " + attributeValue);

                    // Method m = modelElement.getClass().getMethod(getter,
                    // argTypes);

                    // Object attributeValue = m.invoke(modelElement, new
                    // Object[0]);

                    // String name = modelElement.getName();

                    // set

                    /*
                     * this way it doesn't work for value-type Properties like
                     * 'bool', 'int', ... because the call below results in a
                     * java.lang.NoSuchMethodException, because e.g. for int,
                     * argTypes = {Integer.class} instead of {int.class}
                     */

                    // argTypes = new Class[] {attributeValue.getClass()};
                    // //String.class
                    // String setterName = //rta.getSetterName();
                    // m = Util.findMethodByName(resourceObject.getClass(),
                    // setterName);
                    // m =
                    // resourceObject.getClass().getMethod(rta.getSetterName(),
                    // argTypes);
                    if (attributeValue != null)// && m != null)
                    {
                        // argTypes = m.getParameterTypes();

                        // Object[] args = new Object[]
                        // {attributeValue};//.toString()

                        Util.setProperty(resourceObject, propertyName,
                                         attributeValue);

                        // logger.info(//logger.MODELMAPPER_DETAILLED,
                        // logger.MODELMAPPER,
                        // "set ResourceObject Property "
                        // + propertyName
                        // + " with Value: "
                        // + attributeValue);
                        //
                        // try
                        // {
                        //
                        // //m.invoke(resourceObject, args);
                        // }
                        // catch (Exception e)
                        // {
                        // logger.error(
                        // "failed to set Property '"
                        // + setterName
                        // + " with Value "
                        // + attributeValue.getClass().getSimpleName()
                        // + attributeValue.toString());
                        // logger.logException(e);
                        // }

                    }

                }

            } catch (Exception e) {
                logger.logException(e);
            }

            // UmlClass c;
            // c.

            // AssociationEnd a;
            // a.getTypeA()

        }

    }



    public void ensureNeededElementsExist(ModelElement startPoint) {
        //get namespace of current element
        Namespace ns = startPoint.getNamespace();

        //search for root namespace (do not jet save it to class variable, as we need to check if root namespace changed
        Namespace rootns = ns;
        while ( rootns.getNamespace() != null ) {
            aLog.debug("step back from " + rootns.getName() + " to " + rootns.getNamespace().getName());
            rootns = rootns.getNamespace();
        }


        if ( root_namespace == null || ! root_namespace.getName().equals(ns.getName()) ) {

            CoreHelper cHelper = Model.getCoreHelper();

            //did we already find our packages directly under root namespace?
            if ( secUMLPackage == null || oclPackage == null ) {

                Collection<UmlPackage> root_packages = Model.getModelManagementHelper().getAllModelElementsOfKind(rootns, UmlPackage.class.getCanonicalName());

                Collection<String> root_packageNames = new HashSet<String>();
                aLog.debug("existing packages: " + root_packages.size());
                for ( UmlPackage package_ : root_packages ) {
                    aLog.debug(package_.getName());
                    root_packageNames.add(package_.getName());
                }

                /** get or create secUMLPackage **/
                if ( ! root_packageNames.contains(SecureUmlConstants.PACKAGE_SECUML)) {
                    aLog.debug(SecureUmlConstants.PACKAGE_SECUML + " does not exists... create it");

                    secUMLPackage = (UmlPackage) Model.getModelManagementFactory().createPackage();
                    cHelper.setName(secUMLPackage, SecureUmlConstants.PACKAGE_SECUML);
                    cHelper.setNamespace(secUMLPackage, rootns);
                }  else {
                    for ( UmlPackage package_ : root_packages ) {
                        if ( SecureUmlConstants.PACKAGE_SECUML.equals(package_.getName())) {
                            secUMLPackage = package_;
                            aLog.debug("found existing " + SecureUmlConstants.PACKAGE_SECUML + " package");
                            break;
                        }
                    }
                }


                /** get or create OCL_UML **/
                if ( ! root_packageNames.contains(SecureUmlConstants.PACKAGE_OCL)) {
                    aLog.debug(SecureUmlConstants.PACKAGE_OCL + " does not exists... create it");

                    oclPackage = (UmlPackage) Model.getModelManagementFactory().createPackage();

                    cHelper.setName(oclPackage, SecureUmlConstants.PACKAGE_OCL);
                    cHelper.setNamespace(oclPackage, rootns);
                }  else {
                    for ( UmlPackage package_ : root_packages ) {
                        if ( SecureUmlConstants.PACKAGE_OCL.equals(package_.getName())) {
                            oclPackage = package_;
                            aLog.debug("found existing " + SecureUmlConstants.PACKAGE_OCL + " package");
                            break;
                        }
                    }
                }


                //oclMappings


                Collection<UmlClass> oclTypes = Model.getModelManagementHelper().getAllModelElementsOfKind(oclPackage, UmlClass.class.getCanonicalName());
                UmlClass oclTypesA[] = new UmlClass[oclTypes.size()];
                oclTypes.toArray(oclTypesA);

                ArrayList<String> oclTypeNames = new ArrayList<String>();

                for ( UmlClass umlClass : oclTypesA ) {
                    oclTypeNames.add(umlClass.getName());
                }

                //create OCL types
                if ( modelWriter == null ) {
                    getModelWriter();
                }

                for ( UML_OCL ocl : UML_OCL.values() ) {
                    aLog.debug("OCL Type: " + ocl.toString());
                    if ( oclTypeNames.contains(ocl.toString()) ) { //at least, the type already exists.. we do not need to create it
                        if ( ! oclMappings.containsKey(ocl) ) {
                            oclMappings.put(ocl, oclTypesA[oclTypeNames.indexOf(ocl.toString())]);
                            aLog.debug("added existing UMLClass to mapper");
                        }
                    } else { // create uml class for ocl type
                        //createOclType
                        aLog.debug("craete OCL Type with " + ocl.getSuperTypes().length + " super Types");
                        Set<UmlClass> superTypes = new HashSet<UmlClass>();
                        for ( UML_OCL superType : ocl.getSuperTypes() ) {
                            superTypes.add(oclMappings.get(superType));
                            aLog.debug("class: " + oclMappings.get(superType).getName());
                        }
                        oclMappings.put(ocl, modelWriter.createOclType(ocl.toString(), superTypes));
                    }
                }
            }

            //check for permission package under secuml packages
            if ( permissionPackage == null ) {

                if ( secUMLPackage == null) {
                    throw new RuntimeException("could not find secUML Package");
                }

                Collection<UmlPackage> secuml_packages = Model.getModelManagementHelper().getAllModelElementsOfKind(secUMLPackage, UmlPackage.class.getCanonicalName());

                for ( UmlPackage package_ : secuml_packages) {
                    if ( SecureUmlConstants.PACKAGE_PERMISSIONS.equals(package_.getName())) {
                        permissionPackage = package_;
                        break;
                    }
                }

                if ( permissionPackage == null ) {
                    permissionPackage = (UmlPackage) Model.getModelManagementFactory().createPackage();

                    cHelper.setName(permissionPackage, SecureUmlConstants.PACKAGE_PERMISSIONS);
                    cHelper.setNamespace(permissionPackage, secUMLPackage);
                }
            }












            if ( modelWriter == null ) {
                getModelWriter();
            }
            /** create stereotypes **/
            try {
                for ( SECUML_STEREOTYPES stereotype : SecureUmlConstants.SECUML_STEREOTYPES.values() ) {
                    modelWriter.getOrCreateStereotype(stereotype.toString(), secUMLPackage, stereotype.getBase() );
                }
                //aLog.debug("Successfully created " + SecureUmlConstants.SECUML_STEREOTYPES.values().length + " stereotypes");
                modelWriter.getOrCreateStereotype("compuml.entity", secUMLPackage, SecureUmlConstants.BASE_CLASS);
                //modelWriter.getOrCreateStereotype(SecureUmlConstants.STEREOTYPE_OCL_TYPE, oclPackage, SecureUmlConstants.BASE_CLASS);

                aLog.debug("checked or created stereotypes");
            } catch (Exception e) {
                aLog.error("Could not create stereotype: " + e.getMessage(), e);
            }


            ActionClassDiagram actionClassDiagram = new ActionClassDiagram();



            UMLClassDiagram policyClassDiag = (UMLClassDiagram) actionClassDiagram.createDiagram(secUMLPackage);
            try {
                policyClassDiag.setName("PolicyHierarchy");
            } catch (PropertyVetoException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

//			//DiagramInterchangeModel diagFact = Model.getDiagramInterchangeModel();
//			//ClassDiagram policyClassDiag = (ClassDiagram) diagFact.createDiagram(ClassDiagram.class, ns);
//			//policyClassDiag.
//
//
////			Create new Fig classes to represent the element on the diagram and add them to the graph model
////			(org.argouml.uml.diagram.xxxx.XxxxDiagramGraphModel.java)                and           renderer
////			(org.argouml.uml.diagram.xxxx.ui.XxxxDiagramRenderer.java).
//
//			//get default Policy (model element)
//			UmlClass defaultPolicy = HierarchicalPolicyExplorer.getInstance().getDefaultPolicy();
//			//create Fig class of model element
//			FigClass defaultPolicyFig = new FigClass(defaultPolicy, 40, 40, 40, 40);
//
//
//			ClassDiagramGraphModel classDiagGModel = new ClassDiagramGraphModel();
//
//			classDiagGModel.addNode(defaultPolicyFig);
//
//			//ClassDiagramRenderer classDiagRenderer = new ClassDiagramRenderer();
//
//			//FigNode figNode = classDiagRenderer.getFigNodeFor(classDiagGModel, new LayerDiagram(), defaultPolicy, new HashMap());
//
//
//			//classDiagGModel.addNode(figNode);
//
//
//
//
//
//			ActionClassDiagram actionClassDiagram = new ActionClassDiagram();
//
//			aLog.debug("create class diagram for namespace " + ns.getName());
//
//			UMLClassDiagram policyClassDiag = (UMLClassDiagram) actionClassDiagram.createDiagram(ns);
//
//			policyClassDiag.add(defaultPolicyFig);
//
//			try {
//				policyClassDiag.setName("PolicyHierarchy");
//
//				//policyClassDiag.
//
//				aLog.debug("set name to class diagram");
//
//
//
//				aLog.debug("got defaultPolicy");
//
//				policyClassDiag.setModelElementNamespace(defaultPolicy, ns);
//
//				aLog.debug("added defaultPolicy");
//
//				//policyClassDiag.
//
//			} catch (PropertyVetoException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//
        }
        root_namespace = rootns;
    }

    public Namespace getInitNamespace() {
        return root_namespace;
    }

    public UmlPackage getSecUMLPackage() {
        return secUMLPackage;
    }

    public UmlPackage getPermissionPackage() {
        return permissionPackage;
    }

    public UmlPackage getOclPackage() {
        return this.oclPackage;
    }


    /**
     * Maps UML model elements to the dialect model, starting from
     * <code>startPoint</code>.
     *
     * @param startPoint
     * @return the transformed object
     */
    public Object transform(ModelElement startPoint) // , Package
    // sourcePackage)
    {
        // logger.info("transform started at "+startPoint.getName());


        if (startPoint != null) {
            // (JD) this is a hack. apparently, the navigationDepth in the
            // mappingScopeStrategy gets messed up sometimes. So we just create
            // a new one
            // each time...
            mappingScopeStrategy = new MapSelfAndAssociatedResources(
                dialectMetaModelInfo);
            Collection<ModelElement> mappingScope = mappingScopeStrategy
                                                    .getMappingScope(startPoint);
            logger.info("found_" + mappingScope.size()
                        + " elements to transform");
            for (ModelElement me : mappingScope) {
                if (me == null) {
                    logger.info("model element = null");
                } else if (me.getName() == null) {
                    logger.info("model element name = null. type = "
                                + me.getClass().getName());
                } else {
                    logger.info(me.getName());
                }
            }
            transformModelElements(mappingScope);
            transformAssociations(mappingScope);
            initActionHierarchies();

            // TODO: transform only relevant permissions
            // (such attached to Resources in the mappingScope)
            findSecumlModelElements(startPoint);
            transformAllRoles();
            tramsformAllPolicies();
            transformAllPermisssions();

            ensureNeededElementsExist(startPoint);

            return map.getElement(startPoint);
        } else
            return null;
    }

    /**
     * @return the permissionsAssociations
     */
    public Set<AssociationClass> getPermissionsAssociations() {
        return permissionsAssociations;
    }

    /**
     * @return the roleClasses
     */
    public Set<UmlClass> getRoleClasses() {
        return roleClasses;
    }

    public Set<UmlClass> getPolicyClasses() {
        return policyClasses;
    }

    /**
     * @return the roleHierarchyGeneralizations
     */
    public Set<Generalization> getRoleHierarchyGeneralizations() {
        return roleHierarchyGeneralizations;
    }

    /**
     * @return the policyHierarchyGeneralizations
     */
    public Set<Generalization> getPolicyHierarchyGeneralizations() {
        return this.policyHierarchyGeneralizations;
    }

    /**
     *
     * @param permission a permission which is assigned to a policy
     * @return the assigned policy
     */
    public UmlClass getPolicy(UmlAssociation permission) {
        StringBuffer buff = new StringBuffer();

        for ( String key : policyAssignments.keySet()) {
            buff.append(key + "::");
        }

        aLog.debug("policyAssignments contains " + policyAssignments.size() + " entries: " + buff.toString() + "\n search for " + permission.getName());
        if ( policyAssignments.containsKey(permission.getName()) ) {
            return policyAssignments.get(permission.getName());
        } else {
            aLog.warn("TODO: assigned default policy to permission?");
            return (UmlClass) this.getDefaultPolicy();
        }
    }


    /**
     * returns the package inside the dialect metamodel containing the design
     * modeling language elements. E.g., the ComponentUML package inside the
     * dialect metamodel for ComponentUML. FIXME: should this function really be
     * here, or somewhere else?
     */
    private RefPackage getDialectPackage() {
        return (RefPackage) Util.invokeParameterlessMethod(dialectMetaModelInfo
                .getDialectExtent(), "get"
                + dialectMetaModelInfo.getDialectName());
    }



    /**
     * create all action instances that are defined for
     * <code>metaModelObject</code> of type <code>resourceType</code>.
     *
     * @param resourceType
     * @param metaModelObject
     */
    private void instantiateAllActions(ResourceType resourceType,
                                       RefObject metaModelObject) {
        if (metaModelObject == null) {
            logger.error("metaModelObject = null");
        }
        try {
            for (Iterator iter = dialectMetaModelInfo
                                 .getActionTypesOfResourceType(resourceType).iterator(); iter
                    .hasNext();) {

                ActionType at = (ActionType) iter.next();

                Object action = getOrCreateAction(metaModelObject, at
                                                  .getShortName());
            }

        } catch (Exception e) {
            logger.logException(e);
        }
    }

    private void initActionHierarchies() {
        for (Iterator iter = actions.iterator(); iter.hasNext();) {
            try {

                Object action = (Object) iter.next();

                ActionWrapper actionWrapper = new ActionWrapper(action);

                Object resource = actionWrapper.getResource();
                ModelElement resourceUml = (ModelElement) map
                                           .getUmlElement(resource);

                ResourceType rt = helper.getResourceType(resourceUml);

                ActionType at = dialectMetaModelInfo.
                                // getActionTypeByName(actionWrapper.getName());
                                getActionType(rt, actionWrapper.getName());

                if (at != null && at.getSubactionsDefinition() != null) {
                    logger.info(logger.MODELMAPPER_DETAILLED,
                                "examining Action Hierarchy of " + at.getName());
                    try {
                        // CompositeActionType compositeActionType =
                        // (CompositeActionType) at;

                        String subactionsDefinitionString = at
                                                            .getSubactionsDefinition();

                        OclExpressionsParser parser = new OclExpressionsParser();

                        OclExpression subactionsDefinition = parser
                                                             .parseOclExpression(subactionsDefinitionString);

                        OclExpressionEvaluator evaluator = new OclExpressionEvaluator(
                            subactionsDefinition, actionWrapper
                            .getModelElement());
                        //
                        Set subactions = evaluator.evaluateExpression();

                        for (Iterator iterator = subactions.iterator(); iterator
                                .hasNext();) {
                            Object subaction = (Object) iterator.next();

                            logger.info(logger.MODELMAPPER_DETAILLED,
                                        "found Action Hierarchy: "
                                        + actionWrapper.getName()
                                        + "->"
                                        + Util.getProperty(subaction,
                                                           "name"));

                            Collection actionSubactions = (Collection) Util
                                                          .getProperty(action, "subactions");
                            Collection subactionSuperactions = (Collection) Util
                                                               .getProperty(subaction, "superactions");

                            actionSubactions.add(subaction);
                            subactionSuperactions.add(action);
                        }

                    } catch (Exception e) {
                        logger.logException(e);
                    }
                }
            } catch (Exception e) {
                logger.logException(e);
            }
        }
    }

    /**
     * Maps all resources in the <code>mappingScope</code>.
     *
     * @param mappingScope
     */
    private void transformModelElements(Collection<ModelElement> mappingScope) {
        if (mappingScope.size() == 0) {
            logger.error("no model elements to transform!");
            return;
        }

        for (Iterator iter = mappingScope.iterator(); iter.hasNext();) {
            try {
                ModelElement modelElement = (ModelElement) iter.next();
                examineModelElement(modelElement);
            } catch (Exception e) {
                logger.logException(e);
            }
        }

        //ensureNeededElementsExist(mappingScope.iterator().next());
    }

    private void transformAssociations(Collection<ModelElement> mappingScope) {
        for (Iterator iter = mappingScope.iterator(); iter.hasNext();) {
            try {
                ModelElement umlModelElement = (ModelElement) iter.next();

                Object secureUmlModelElement = map.getElement(umlModelElement);

                MetaModelClass metaModelClass = helper
                                                .getMetaModelClass(umlModelElement);

                Collection<InterResourceAssociation> associations = dialectMetaModelInfo
                        .getInterResourceAssociations(metaModelClass);

                if (associations != null) {
                    for (Iterator iterator = associations.iterator(); iterator
                            .hasNext();) {
                        try {
                            InterResourceAssociation association = (InterResourceAssociation) iterator
                                                                   .next();

                            Collection values = helper.navigateAssociation(
                                                    umlModelElement, association);

                            MetaModelClass mmClass = helper
                                                     .getMetaModelClass(umlModelElement);

                            // String umlGetterName =
                            // association.getOtherEnd(mmClass).
                            // getUmlPropertyGetter();
                            //
                            Class[] argTypes = new Class[0];
                            //
                            // Method umlGetter = umlModelElement.getClass().
                            // getMethod(umlGetterName, argTypes);

                            String secureUmlGetterName = association
                                                         .getOtherEnd(mmClass).getGetterName();

                            Method secureUmlGetter = secureUmlModelElement
                                                     .getClass().getMethod(secureUmlGetterName,
                                                             argTypes);

                            if (values != null // otherwise, there is nothing
                                    // to do
                                    // because no associated elements to map
                                    && secureUmlGetter != null
                                    && secureUmlGetter != null) {
                                Object value = secureUmlGetter.invoke(
                                                   secureUmlModelElement, new Object[0]);

                                Class propertyType = secureUmlGetter
                                                     .getReturnType();

                                if (value instanceof Collection) // ->
                                    // propertyType.equals(Collection.class)
                                    // i.e. Association is multiple
                                    // -> Collection Property -> no Setter
                                {
                                    if (value == null) {
                                        logger
                                        .error("Collection Property not initialized: "
                                               + secureUmlGetterName);
                                        continue;
                                    }
                                    // else

                                    Collection valueCollection = (Collection) value;

                                    for (Iterator it = values.iterator(); it
                                            .hasNext();) {
                                        try {
                                            RefObject umlValue = (RefObject) it
                                                                 .next();

                                            if (umlValue instanceof ModelElement
                                                    && helper
                                                    .hasType(
                                                        (ModelElement) umlValue,
                                                        association
                                                        .getOtherEnd(
                                                            mmClass)
                                                        .getType()
                                                        .getUmlClassName())
                                                    && map
                                                    .mapContainsKey(umlValue)) {
                                                Object secureUmlValue = map
                                                                        .getElement(umlValue);
                                                if (secureUmlValue != null) {
                                                    try {

                                                        valueCollection
                                                        .add(secureUmlValue);
                                                    } catch (Exception e) {
                                                        logger
                                                        .error("failed to add Object of Type "
                                                               + secureUmlValue
                                                               .getClass()
                                                               + "to"
                                                               + secureUmlGetterName);
                                                        // logger.logException(e);
                                                    }

                                                } else {
                                                    logger
                                                    .warn("Association value not modelmapped: "
                                                          + umlValue
                                                          .getClass()
                                                          .getSimpleName());
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.logException(e);
                                        }
                                    }
                                } else
                                    // no Collection Property -> there must be a
                                    // Setter
                                {

                                    String setterName = association
                                                        .getOtherEnd(
                                                            helper
                                                            .getMetaModelClass(umlModelElement))
                                                        .getSetterName();

                                    if (setterName != null) {
                                        argTypes = new Class[1];
                                        argTypes[0] = propertyType;

                                        Method setter = secureUmlModelElement
                                                        .getClass().getMethod(
                                                            setterName, argTypes);

                                        if (setter != null) {
                                            Object[] args = new Object[1];
                                            Object umlValue = null;
                                            if (values.iterator().hasNext()) {
                                                umlValue = values.iterator()
                                                           .next();
                                            }
                                            if (umlValue instanceof RefObject
                                                    && map
                                                    .mapContainsKey((RefObject) umlValue)) {
                                                args[0] = map
                                                          .getElement((RefObject) umlValue);
                                                try {
                                                    setter
                                                    .invoke(
                                                        secureUmlModelElement,
                                                        args);
                                                } catch (Exception e) {
                                                    logger
                                                    .error("problem executing "
                                                           + setterName
                                                           + " on object "
                                                           + secureUmlModelElement);
                                                    logger.logException(e);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.logException(e);
                        }
                    }
                }
            }

            // AssociationEnd a;a.getp
            catch (Exception e) {
                logger.logException(e);
            }
        }
    }

    // protected void findSecumlModelElements()
    // {
    // findSecumlModelElements();//modelPackage);
    // }
    /**
     * Finds all the SecureUML Elements, i.e., roles, permissions and action
     * types, etc. Goes to the top-most namespace and calls
     * {@link #findSecumlModelElements(Collection)} from there.
     */
    protected void findSecumlModelElements(ModelElement resource) {
        if (resource == null) {
            return;
        }
        Namespace n = null;

        if (resource instanceof Namespace)
            n = (Namespace) resource;
        else
            n = resource.getNamespace();

        if (n == null) {
            Classifier anchor = helper.findAnchor(resource);
            n = anchor.getNamespace();
        }

        if (n == null) {
            logger.error("couldn't find namespace for resource " + resource);
            return;
        }

        Namespace outerNs = n.getNamespace();
        while (outerNs != null && n != outerNs) {
            n = outerNs;
            outerNs = n.getNamespace();
        }

        Collection modelElements = n.getOwnedElement();

        findSecumlModelElements(modelElements);

        // RefPackage p = n.refImmediatePackage();//refOutermostPackage();

        // findSecumlModelElements(p);
    }

    /**
     * Finds the SecureUML elements in the given collection of model elements.
     * Found elements are stored in {@link #roleClasses},
     * {@link #permissionsAssociations}, {@link #roleHierarchyGeneralizations},
     * {@link ch.ethz.infsec.secureumlgui.transformation.ModelMap#stereotypeMap}
     * {@link ch.ethz.infsec.secureumlgui.transformation.ModelMap#actionMap}
     * Recurses into packages and namespaces.
     *
     * @param modelElements
     */
    private void findSecumlModelElements(Collection modelElements)
    {


        //for (Iterator iter = modelElements.iterator(); iter.hasNext();)
        for ( Object item : modelElements )
        {
            //Object item = iter.next();

            if (item instanceof Stereotype)
            {
                Stereotype stereotype = (Stereotype) item;

                map.putStereotype(stereotype);

                aLog.debug("Found Stereotype: " + stereotype.getName());
            }
            else if (item instanceof AssociationClass)
            {
                AssociationClass association = (AssociationClass) item;

                if(isOfType(association, SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION))
                {
                    aLog.debug("Found SecureUml Permission Association: "
                               + association.getName() + " : "
                               + association.getClass().getSimpleName());

                    permissionsAssociations.add(association);

                    //TODO assign this association to the corresponding policy and save via policy as key in permissionsPerPolicy
                    //	when a permission is not assigned to a policy, assign to DEFAULT_POLICY_IDENTIFIER
                }
//				else
//				logger.info("this is not a SecureUml Permission: " + association.getName());
            }
            else if(item instanceof UmlClass)
            {
                UmlClass umlClass = (UmlClass) item;

                if(isOfType(umlClass, SecureUmlConstants.STEREOTYPE_SECUML_ROLE))
                {
                    aLog.debug("Found a SecureUML Role: " + umlClass.getName());
                    roleClasses.add(umlClass);
                }
                else if (isOfType(umlClass, SecureUmlConstants.STEREOTYPE_SECUML_POLICY))
                {
                    aLog.debug("Found a SecureUML Policy: " + umlClass.getName());
                    policyClasses.add(umlClass);
                }
                else if(isOfType(umlClass, SecureUmlConstants.STEREOTYPE_SECUML_ACTIONTYPE))
                {
                    aLog.debug("Found a SecureUML ActionType: " + umlClass.getName());
                    map.putActionClass(umlClass.getName(), umlClass);
                }
//				else
//				logger.info("this is not a SecureUml Role: " + umlClass.getName());
            }
            else if (item instanceof Generalization)
            {
                Generalization generalization = (Generalization) item;
                if (isOfType(generalization.getChild(),SecureUmlConstants.STEREOTYPE_SECUML_ROLE)
                        && isOfType(generalization.getParent(), SecureUmlConstants.STEREOTYPE_SECUML_ROLE)) {
                    roleHierarchyGeneralizations.add(generalization);
                }

                if (isOfType(generalization.getChild(),SecureUmlConstants.STEREOTYPE_SECUML_POLICY)
                        && isOfType(generalization.getParent(), SecureUmlConstants.STEREOTYPE_SECUML_POLICY)) {
                    this.policyHierarchyGeneralizations.add(generalization);
                }
            }
            else if (item instanceof Namespace)
            {
                Namespace namespace = (Namespace) item;

                findSecumlModelElements(namespace.getOwnedElement());

                // findSecumlModelElements(namespace);
            }
            else if(item instanceof org.omg.uml.UmlPackage)
            {
                org.omg.uml.UmlPackage p = (org.omg.uml.UmlPackage) item;

                logger.info("UmlPackage Element found!!");

                Collection me = new LinkedList();

                me.addAll(p.refAllAssociations());
                me.addAll(p.refAllClasses());
                me.addAll(p.refAllPackages());
                findSecumlModelElements(me);
            }
            else if ( item instanceof UmlAssociation) {
                UmlAssociation asso = (UmlAssociation) item;

                List<AssociationEnd> assoEnds = asso.getConnection();

                boolean perm_pol = true;

                UmlClass policy = null;
                AssociationClass permission = null;

                if ( assoEnds.size() == 2 ) {
                    for ( AssociationEnd end : assoEnds ) {
                        Classifier classf = end.getParticipant();

                        if ( isOfType(classf, SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION) ) {
                            permission = (AssociationClass) classf;
                        } else if ( isOfType(classf, SecureUmlConstants.STEREOTYPE_SECUML_POLICY) ) {
                            policy = (UmlClass) classf;
                        } else {
                            perm_pol = false;
                            break;
                        }
                    }
                } else {
                    perm_pol = false;
                }

                if ( perm_pol ) {
                    aLog.debug("found a UmlAssociation between policy " + policy.getName() + " and permission " + permission.getName());
                    policyAssignments.put(permission.getName(), policy);
                }

            }
            else
            {
                aLog.warn(item.getClass() + " Element not handled!");
            }
        }
    }

    /**
     * maps all roles in {@link #roleClasses}. Takes into account the role
     * inheritance from {@link #roleHierarchyGeneralizations}
     */
    protected void transformAllRoles() {
        for ( UmlClass roleClass : roleClasses ) {
            transformRole(roleClass);
        }
        for (Generalization roleHierarchyGeneralization : roleHierarchyGeneralizations ) {
            transformRoleInheritance(roleHierarchyGeneralization);
        }
    }

    /** maps all permissions in {@link #permissionsAssociations}. */
    protected void transformAllPermisssions() {
        for (AssociationClass permissionAssociation : permissionsAssociations ) {
            transformPermissionClass(permissionAssociation);
        }
    }

    protected void tramsformAllPolicies() {
        for ( UmlClass policyClass : policyClasses ) {
            transformPolicy(policyClass);
        }

        for (Generalization policyHierarchyGeneralization : policyHierarchyGeneralizations ) {
            transformPolicyInheritance(policyHierarchyGeneralization);
        }

    }


    private RefObject defaultPolicy = null;
    private PolicyWrapper defaultPolicyWrapper = null;

    public Object getDefaultPolicy() {


        return null;
//		if ( defaultPolicy == null ) {
//			defaultPolicy = SecureModelFactory.getInstance().createPolicy(SecureUmlConstants.DEFAULT_POLICY_NAME);
//		}
//		return defaultPolicy;
    }

    public PolicyWrapper getDefaultPolicyWrapper() {
//		if ( defaultPolicyWrapper == null ) {
//			defaultPolicyWrapper = new PolicyWrapper(getDefaultPolicy());
//		}
//		return defaultPolicyWrapper;
        return null;
    }



    // public void examineUmlClass(UmlClass umlClass)
    // {
    // // no special treatment -> this method not needed here
    // super.examineUmlClass(umlClass);
    // }

}
