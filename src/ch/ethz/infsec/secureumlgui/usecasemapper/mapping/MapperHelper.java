package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.argouml.model.Model;
import org.argouml.model.Facade;

import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.modelmanagement.ModelConst;

/**
 * Provides common tools for the mapping routines.
 * The class uses the singleton pattern to provide
 * a global accesspoint an ensure uniqueness.
 *
 * @version 1.0
 */
public final class MapperHelper {

    /**
     * The <code>log4j</code>-logger of this class.
     *
     */
    private static final Logger LOGGER = Logger.
                                         getLogger(MapperHelper.class);

    /**
     * The singleton reference.
     */
    private static MapperHelper singleton = new MapperHelper();

    /**
     * The collection with the predefined names of the
     * stereotypes in the SecureUML package.
     */
    private final Collection<String> secumlStereoNames =
        new ArrayList<String>(6);

    /**
     * The collection with the predefined names of the
     * stereotypes of the ComponentUMLDialect package.
     */
    private final Collection<String> compUMLDiSteNames =
        new ArrayList<String>(7);

    /**
     * The collection with the predefined names of the
     * action types in the ComponentUMLDialect package.
     */
    private final Collection<String> compUMLActTyNames =
        new ArrayList<String>(6);

    /**
     * Name constant for the SecureUML package.
     */
    private static final String SECUML_NS_NAME = "SecureUML";

    /**
     * Name constant for the ComponentUML dialect package.
     */
    private static final String CUML_DIAL_NS_NAME = "ComponentUMLDialect";

    /**
     * Name constant for the SecureUML stereotype "secuml.actiontype"
     * identifying action types in the ComponentUML dialect package.
     */
    @Deprecated
    private static final String ACT_STEREO_NAME = SecureUmlConstants.STEREOTYPE_SECUML_ACTIONTYPE;

    /**
     * Name constant for the SecureUML stereotype "secuml.permission"
     * identifiying permission classes.
     */
    //@Deprecated
    //private static final String PERM_STEREO_NAME = SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION; //"secuml.permission";

    /**
     * Hash map to cache access to the stereo types.
     */
    private final Map<String, Object> secumlStereotypes =
        new HashMap<String, Object>();

    /**
     * Reference in member variable makes the code
     * more readable.
     */
    private final Facade facade = Model.getFacade();

    /**
     * Private constructor due to singleton pattern.
     */
    private MapperHelper() { };

    /**
     * Gets the MapperHelper instance.
     *
     * @return the <code>MapperHelper</code>-instance
     */
    public static MapperHelper getInstance() {
        return singleton;
    }

    /**
     * Returns the collection of all SecureUML stereotype names.
     *
     * @return A collection of strings being the stereotype's names.
     */
    public Collection<String> getSecumlSteNames() {
        final Collection names = new ArrayList<String>(secumlStereoNames);
        names.addAll(compUMLDiSteNames);
        return names;
    }

    /**
     * Returns the collection of all component dialect action types.
     *
     * @return A collection of strings being the action type's names.
     */
    public Collection<String> getCompActTypNames() {
        return new ArrayList<String>(compUMLActTyNames);
    }

    /**
     * Gets the SecureUML stereotype for a passed name.
     * Returns <code>null</code> if not successful.
     *
     * @param model The object holding the target model.
     * @param name The string containing the
     *             name of the desired SecureUML stereotype.
     * @return The object containing stereotype if found
     *             or <code>null</code> otherwise.
     */
    public Object getSecureUMLStereotype(final Object model,
                                         final String name) {

        System.out.println("Getting SecureUML stereotype: " + name);

        if (secumlStereotypes.containsKey(name)) {
            return secumlStereotypes.get(name);
        } else {
            LOGGER.debug("Getting SecureUML stereotype: " + name);

            final Collection stereotypes = new ArrayList();

            stereotypes.addAll(Model.getExtensionMechanismsHelper().
                               getStereotypes(getNamespace(model,
                                              SECUML_NS_NAME)));

            stereotypes.addAll(Model.getExtensionMechanismsHelper().
                               getStereotypes(getNamespace(model,
                                              CUML_DIAL_NS_NAME)));

            for (Object stereotype : stereotypes) {
                if (facade.getName(stereotype) != null
                        && facade.getName(stereotype).equals(name)) {
                    LOGGER.debug("stereotype: " + name + " found");
                    return stereotype;
                }
            }
            return null;
        }
    }

    /**
     * Gets the SecureUML action type for a passed name.
     * Returns <code>null</code> if not successful.
     *
     * @param name The string containing the
     *             name of the desired SecureUML action type.
     * @param model The object holding the target model.
     * @return The object of the action type if found or <code>null</code>
     *             otherwise.
     */
    public Object getSecureUMLActionType(final Object model,
                                         final String name) {

        LOGGER.debug("Getting SecureUML action type: " + name);

        final Collection classes = Model.getCoreHelper().
                                   getAllClasses(model);

        for (Object currClass : classes) {
            if (facade.getName(currClass) != null
                    && facade.getName(currClass).equals(name)
                    && Model.getExtensionMechanismsHelper().
                    hasStereoType(currClass, ACT_STEREO_NAME)) {

                LOGGER.debug("action type: " + name + " found");
                return currClass;
            }
        }
        LOGGER.debug("action type: " + name + " NOT found");
        return null;
    }

    /**
     * Tests for circular inheritance in the role hierachies of
     * all use case diagrams.
     *
     * @param model The object holding the target model.
     * @return <code>true</code> if there is no circular inheritance
     */
    public boolean hasNoCircularInheritance(final Object model) {
        final Collection actors = Model.getUseCasesHelper().
                                  getAllActors(model);

        for (Object actor : actors) {
            for (Object sup : Model.getCoreHelper().getAllSupertypes(actor)) {
                if (facade.getUUID(actor) == facade.getUUID(sup)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Gets the messages belonging to a passed use case. Iterates over all
     * collaborations and interactions thereof to finally  put all messages
     * of each interaction into a collection which is returned.
     *
     * @param   collaboration The object conaining the collaboration.
     * @return The collection of the associated messages.
     */
    public Collection getCollabMessages(final Object collaboration) {
        final Collection messages = new ArrayList();

        for (Object interaction : facade.getInteractions(collaboration)) {
            messages.addAll(facade.getMessages(interaction));
        }
        return messages;
    }

    /**
     * Gets the attibutes of a given target class as a collection of strings.
     * Only returns the names which could be resolved.
     *
     * @param targetClass A object being the target class.
     * @return A collection of strings containing the attribute names.
     */
    public Collection<String> getAttributes(final Object targetClass) {
        final Collection<String> attributes = new  ArrayList<String>();
        for (Object attribute : facade.getAttributes(targetClass)) {
            if (facade.getName(attribute) != null) {
                attributes.add(facade.getName(attribute));
            }
        }
        return attributes;
    }

    /**
     * Gets the methods of a given target class as a collection of strings.
     * Only returns the names  which could be resolved.
     *
     * @param targetClass A object being the target class.
     * @return A collection of strings containing the method names.
     */
    public Collection<String> getMethods(final Object targetClass) {
        final Collection<String> methods = new ArrayList<String>();
        for (Object method : facade.getOperations(targetClass)) {
            if (facade.getName(method) != null) {
                methods.add(facade.getName(method));
            }
        }
        return methods;
    }

    /**
     * Gets the SecureUML role class for a given name in a given
     * namespace or returns <code>null</code> if not found.
     *
     * @param name      The string containing the name of the role.
     * @param namespace The object holding the namespace of the role.
     * @return The object being the role class or <code>null</code>
     *         if not found.
     */
    public Object getRoleClassNS(final String name,
                                 final Object namespace) {
        final Collection classes = Model.getCoreHelper().
                                   getAllClasses(namespace);

        for (Object currClass : classes) {
            if (name.equals(facade.getName(currClass))
                    && Model.getExtensionMechanismsHelper().
                    hasStereoType(currClass, "secuml.role")) {
                return currClass;
            }
        }
        return null;
    }

    /**
     * Initializes the SecureUML package of the UML model.
     * Checks if all necessary elements are present and have the
     * correct type. This routine should be invoked before every
     * mapping run to ensure, the mapping strategies will find
     * the SecureUML elements they need.
     * Therefore this routine is invoked by the
     * {@link ch.ethz.infsec.secureumlgui.usecasemapper.control.Controller
     * Controller} in the method
    * {@link ch.ethz.infsec.secureumlgui.usecasemapper.control.Controller#map()
     *  map}.
     *
     * @param model The object holding the target model.
     * @throws MapperException If the SecureUML package could not be set up
     *                         correctly.
     * @see ch.ethz.infsec.secureumlgui.usecasemapper.control.Controller
     */
    public void initSecureUML(final Object model) throws MapperException {

        LOGGER.info("Initializing SecureUML packages");

        secumlStereoNames.add(ACT_STEREO_NAME);
        secumlStereoNames.add(SecureUmlConstants.STEREOTYPE_SECUML_CONSTRAINT);
        secumlStereoNames.add(SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION);
        secumlStereoNames.add(SecureUmlConstants.STEREOTYPE_SECUML_POLICY);
        secumlStereoNames.add(SecureUmlConstants.STEREOTYPE_SECUML_RESOURCE);
        secumlStereoNames.add(SecureUmlConstants.STEREOTYPE_SECUML_ROLE);
        secumlStereoNames.add(SecureUmlConstants.STEREOTYPE_SECUML_USER);
        secumlStereoNames.add(SecureUmlConstants.STEREOTYPE_SECUML_POLICY);

        compUMLDiSteNames.add("dialect.entityaction");
        compUMLDiSteNames.add("dialect.entityattributeaction");
        compUMLDiSteNames.add("dialect.entitymethodaction");
        compUMLDiSteNames.add("isCreate");
        compUMLDiSteNames.add("isDelete");
        compUMLDiSteNames.add("isExecute");
        compUMLDiSteNames.add("isQuery");
        compUMLDiSteNames.add("isUpdate");

        compUMLActTyNames.add("create");
        compUMLActTyNames.add("delete");
        compUMLActTyNames.add("execute");
        compUMLActTyNames.add("fullAccess");
        compUMLActTyNames.add("read");
        compUMLActTyNames.add("update");

        setupNamespace(SECUML_NS_NAME, model);
        final Object secumlNamespace = getNamespace(model, SECUML_NS_NAME);
        setupStereotypesNS(secumlNamespace, secumlStereoNames);

        setupNamespace(CUML_DIAL_NS_NAME, secumlNamespace);
        final Object compUMLDiaNSpace = getNamespace(model, CUML_DIAL_NS_NAME);
        setupStereotypesNS(compUMLDiaNSpace, compUMLDiSteNames);

        for (String currClassName : compUMLActTyNames) {
            LOGGER.debug("Looking for action type: " + currClassName);
            Object foundClass = null;
            for (Object currClass : Model.getCoreHelper().
                    getAllClasses(compUMLDiaNSpace)) {
                if (currClassName.equals(facade.getName(currClass))) {
                    foundClass = currClass;
                }
            }
            if (foundClass == null) {
                foundClass = Model.getCoreFactory().
                             buildClass(currClassName, compUMLDiaNSpace);
            }
        }

        for (Object currClass : Model.getCoreHelper().
                getAllClasses(compUMLDiaNSpace)) {
            setupActClassStereo(currClass,
                                getSecureUMLStereotype(model,
                                        ACT_STEREO_NAME));
        }
    }

    /**
     * Looks if a given target namespace contains a namespace
     * with the given name and creates it if not found.
     *
     * @param namespaceName The string representing the name of the
     *                      namespace to be looked for.
     * @param targetNamespace The object being the namespace in which
     *                        the namespace with the name
     *                        <code>namespaceName</code> will be searched.
     * @throws MapperException If the given namespace could not be set up
     *                         correctly.
     */
    private void setupNamespace(final String namespaceName,
                                final Object targetNamespace)
    throws MapperException {

        Object newNamespace = getNamespace(targetNamespace, namespaceName);

        if (newNamespace == null) {
            LOGGER.debug(namespaceName + " package not found.");
            newNamespace = Model.getModelManagementFactory().
                           createPackage();
            Model.getCoreHelper().setName(newNamespace,
                                          namespaceName);
            Model.getCoreHelper().
            addOwnedElement(targetNamespace, newNamespace);
            LOGGER.debug(namespaceName + " package created.");
        }

        if (getNamespace(targetNamespace, namespaceName) == null) {
            LOGGER.fatal("Could not get or create Namespace: "
                         + namespaceName);
            throw new MapperException("Could not get or create Namespace: "
                                      + namespaceName);
        }
    }

    /**
     * Ensures all SecureUML stereotypes given by their
     * names as a collection are present in a given namespace
     * and have the correct base class.
     * The base class is checked with the method
     * {@link #setupBaseclass(Object) setupBaseclass}.
     *
     * @param namespace The object being the namespace which stereotypes
     *                  will be checked.
     * @param stereonames A collection of strings containing the names of the
     *                    stereotypes to be checked.
     *
     * @see #setupBaseclass(Object)
     */
    private void setupStereotypesNS(final Object namespace,
                                    final Collection<String> stereonames) {

        for (String stereotyName : stereonames) {
            LOGGER.debug("Looking for stereotype: " + stereotyName);
            Object foundStereotype = null;
            for (Object stereotype : Model.getExtensionMechanismsHelper().
                    getStereotypes(namespace)) {
                if (stereotyName.equals(facade.getName(stereotype))) {
                    foundStereotype = stereotype;
                }
            }
            if (foundStereotype == null) {
                foundStereotype = Model.getExtensionMechanismsFactory().
                                  buildStereotype(stereotyName, namespace);
            }
            setupBaseclass(foundStereotype);
        }
    }

    /**
     * Sets the stereotype of a given action class to the
     * SecureUML stereotype "secuml.actiontype".
     * The name of this stereotype is set in the class constant
     * <code>ACT_STEREO_NAME</code>.
     *
     * @param actionClass The object being a UML class which stereotype
     *                    should be set.
     * @param actionType  The object holding the type for the action class.
     */
    private void setupActClassStereo(final Object actionClass,
                                     final Object actionType) {

        if (facade.getStereotypes(actionClass).size() > 1) {
            final Collection stereotypes =
                new ArrayList(facade.getStereotypes(actionClass));
            for (Object stereotype : stereotypes) {
                Model.getCoreHelper().removeStereotype(actionClass,
                                                       stereotype);
            }
        }
        if (!facade.getStereotypes(actionClass).contains(actionType)) {
            if (facade.getStereotypes(actionClass).size() == 1) {
                final Object stereotype = facade.getStereotypes(actionClass).
                                          iterator().next();
                Model.getCoreHelper().removeStereotype(actionClass,
                                                       stereotype);
            }
            Model.getCoreHelper().addStereotype(actionClass, actionType);
        }
    }

    /**
     * Gets the namespace object for a given namespace name
     * or returns <code>null</code> if not found.
     *
     * @param model The object holding the target model.
     * @param nsName A string representing the name of the wanted namespace.
     * @return The object being the wanted namespace or null if not found.
     */
    private Object getNamespace(final Object model, final String nsName) {
        final Collection namespaces = Model.getModelManagementHelper().
                                      getAllNamespaces(model);

        for (Object namespace : namespaces) {
            if (nsName.equals(facade.getName(namespace))) {
                return namespace;
            }
        }
        return null;
    }

    /**
     * Sets the correct base class for a given stereotype
     * depending on its name.
     * Maybe the variable <code>basetypeName</code> is not
     * necessary, as there should be a possiblity to
     * determine the name of a base in the ArgoUML API.
     * This method is used by
     * {@link #setupStereotypesNS(Object,Collection) setupStereotypesNS}
     * to set the base types during the setup.
     *
     * @param stereotype An object being a stereotype.
     *
     * @see #setupStereotypesNS(Object,Collection)
     */
    private void setupBaseclass(final Object stereotype) {
        final Collection bases = new ArrayList(facade.
                                               getBaseClasses(stereotype));
        Object basetype;
        String basetypeName;

        // Dummy classes to be assigned as stereotype bases
        final Object dummyClass = Model.getCoreFactory().createClass();
        final Object dummyAssocClass = Model.getCoreFactory().
                                       createAssociationClass();
        final Object dummyMessage = Model.getCollaborationsFactory().
                                    createMessage();
        final Object dummyAttribute = Model.getCoreFactory().createAttribute();

        String stereotype_name = facade.getName(stereotype);

        if (SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION.equals(stereotype_name)) {
            basetype = dummyAssocClass;
            basetypeName = "AssociationClass";
            //} else if (stereotype_name.startsWith("dialect.")) {
        } else if (stereotype_name.startsWith(ModelConst.DIALECT_PACKAGE_NAME_PART + ".")) {
            basetype = dummyAttribute;
            basetypeName = "Attribute";
        } else if (stereotype_name.startsWith("is")) {
            basetype = dummyMessage;
            basetypeName = "Message";
        } else {
            basetype = dummyClass;
            basetypeName = "Class";
        }



        if (bases.size() == 1
                && !basetypeName.
                equals((String) (facade.getBaseClasses(stereotype).
                                 iterator().next()))) {
            final Object base = facade.getBaseClasses(stereotype).
                                iterator().next();
            Model.getExtensionMechanismsHelper().
            removeBaseClass(stereotype, base);
            Model.getExtensionMechanismsHelper().addBaseClass(stereotype,
                    basetype);
        } else if (bases.size() != 1) {
            for (Object base : bases) {
                Model.getExtensionMechanismsHelper().
                removeBaseClass(stereotype, base);
            }
            Model.getExtensionMechanismsHelper().addBaseClass(stereotype,
                    basetype);
        }
    }
}
