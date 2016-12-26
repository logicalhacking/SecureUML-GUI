package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import java.util.Collection;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.argouml.model.Model;
import org.argouml.model.Facade;

import ch.ethz.infsec.secureumlgui.main.SecureUmlConstants;
import ch.ethz.infsec.secureumlgui.modelmanagement.ModelConst;

/**
 * Mapping strategy for mapping sequence diagrams
 * and role hierarchies  to SecureUML permissions.
 *
 * @version 1.0
 */
public class PermissionMapper implements MapperStrategy {

    /**
     * The <code>log4j</code>-logger of this class.
     */
    private static final Logger LOGGER = Logger.
                                         getLogger(PermissionMapper.class);

    /**
     * Name constant for the action EXECUTE_NAME.
     */
    private static final String EXECUTE_NAME = "execute";
    /**
     * Name constant for the action "read".
     */
    private static final String READ_NAME = "read";
    /**
     * Name constant for the action "update".
     */
    private static final String UPDATE_NAME = "update";

    /**
     * A global reference to the model facade of ArgoUML for more readability.
     */
    private final Facade facade = Model.getFacade();

    /**
     * A global reference to the MapperHelper for more readability.
     */
    private final MapperHelper mapperHelper = MapperHelper.getInstance();

    /**
     * A global reference to the target model.
     */
    private Object model = null;

    /**
     * Maps the sequence diagrams and role hierarchies
     * to SecureUML permissions.
     * Uses {@link MapperHelper#getCollabMessages(Object) getCollabMessages}
     * from {@link MapperHelper MapperHelper} to determine the
     * messages of the collaboration.
     *
     * @throws MapperException The <code>exception</code> to indicate
     *                         the mapping process failed.
     * @see MapperHelper#getCollabMessages(Object)
     */
    public final void map() throws MapperException {

        if (model == null) {
            throw new MapperException("Target model undefined.");
        } else {
            LOGGER.debug("Getting use case diagrams of root level:");

            final Collection constraints = new ArrayList();

            final Collection usecases = Model.getUseCasesHelper().
                                        getAllUseCases(model);

            for (Object usecase : usecases) {

                LOGGER.info("Analyzing usecase: " + facade.getName(usecase));

                final Collection actors = Model.getCoreHelper().
                                          getAssociatedClassifiers(usecase);

                for (Object collab : facade.getCollaborations(usecase)) {
                    for (Object message : mapperHelper.
                            getCollabMessages(collab)) {
                        constraints.clear();
                        mapMessageToPermissions(message, actors, constraints);
                    }
                    analyzeExtends(usecase, collab);
                }

                analyzeIncludes(usecase);

                LOGGER.debug("Analyzing specializations of usecase: "
                             + facade.getName(usecase));

                for (Object child : Model.getCoreHelper().
                        getChildren(usecase)) {

                    LOGGER.debug("Analyzing specialization: "
                                 + facade.getName(child));
                    for (Object coll : facade.getCollaborations(child)) {
                        for (Object message : mapperHelper.
                                getCollabMessages(coll)) {
                            constraints.clear();
                            mapMessageToPermissions(message, actors,
                                                    constraints);
                        }
                    }
                }
            }
            cleanup();
        }
    }

    /**
     * Sets the model the mapping is performed on.
     *
     * @param targetModel The object being the target model.
     */
    public final void setModel(final Object targetModel) {
        model = targetModel;
    }

    /**
     * Maps a <code>UML</code>-message to permissions.
     *
     * @param message       The object containing the message.
     * @param usecaseActors The collection containing the actors of
     *                        the usecase the message is from.
     * @param comments      The collection containing any comments
     *                        to be added as constraints.
     *
     * @throws MapperException If the message could not be mapped.
     *                         More precisely the <code>exception</code>
     *                         originates form
     *                         {@link #createPermission(Permission)
     *                          createPermission} if there is an error.
     * @see #createPermission(Permission)
     */
    private void mapMessageToPermissions(final Object message,
                                         final Collection usecaseActors,
                                         final Collection comments)
    throws MapperException {

        LOGGER.debug("Analyzing message: " + facade.getName(message));

        final Collection senderBases = facade.getBases(facade.
                                       getSender(message));
        final Collection receiverBases = facade.
                                         getBases(facade.getReceiver(message));
        final Permission permission = new Permission();

        for (Object basis : senderBases) {
            if (usecaseActors.contains(basis)) {
                for (Object receiverBasis : receiverBases) {

                    permission.setSender(basis);
                    permission.setMessage(message);
                    permission.setReceiver(receiverBasis);
                    permission.setConstraints(comments);

                    addAuthConstraint(permission);

                    createPermission(permission);
                }
            }
        }
        // iterate over receiver bases for return messages and
        // constrained reads
        if (facade.isAReturnAction(facade.getAction(message))) {
            for (Object receiverBasis : receiverBases) {
                if (usecaseActors.contains(receiverBasis)) {
                    for (Object senderBasis : senderBases) {

                        permission.setSender(receiverBasis);
                        permission.setMessage(message);
                        permission.setReceiver(senderBasis);
                        permission.setConstraints(comments);

                        LOGGER.debug("Generating sequenced read: "
                                     + facade.getName(permission.getSender())
                                     + " "
                                     + permission.getPermAttribute());

                        addAuthConstraint(permission);

                        createPermission(permission);
                    }
                }
            }
        }
    }

    /**
     * Creates the given permission in the ArgoUML model.
     * If the sender of the {@link Permission Permission}
     * is abstract, the creation is dispatched to
     * {@link #createPermissionAActor(Permission) createPermissionAActor}.
     * Afterwards it is checked whether the permission
     * already exists and if not, if at least the
     * permission class exists.
     * The permission class is created if necessary and
     * finally the permission is added.
     *
     * @param permission The permission to be created.
     *
     * @throws MapperException If the given permission could not be
     *                         created.
     */
    private void createPermission(final Permission permission)
    throws MapperException {
        if (permission.isSecureUMLPermission() && permission.isValid()) {
            if (facade.isAbstract(permission.getSender())) {
                createPermissionAActor(permission);
            } else if (hasPermission(permission.getSender(),
                                     permission.getPermAttribute(),
                                     permission.getReceiver(),
                                     permission.getConstraints())) {
                LOGGER.debug("Perm already exists.");
            } else {
                Object permissionClass =
                    getPermissionClass(permission.getSender(),
                                       permission.getReceiver(),
                                       permission.getConstraints());
                if (permissionClass == null) {
                    LOGGER.debug("New permission class needed");

                    final Object subjectClass = mapperHelper.
                                                getRoleClassNS(facade.getName(permission.getSender()),
                                                        facade.getNamespace(permission.
                                                                getSender()));
                    if (subjectClass == null) {
                        LOGGER.error("Could not find role class for: "
                                     + facade.getName(permission.getSender()));
                    } else {
                        permissionClass = Model.getCoreFactory().
                                          buildAssociationClass(subjectClass, permission.
                                                                getReceiver());
                        Model.getCoreHelper().setName(permissionClass,
                                                      permission.getName());

                        Model.getCoreHelper().
                        addStereotype(permissionClass, mapperHelper.
                                      getSecureUMLStereotype(model,
                                                             SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION));

                        Model.getCoreHelper().
                        addOwnedElement(facade.getNamespace(permission.
                                                            getSender()),
                                        permissionClass);

                        for (Object comment : permission.getConstraints()) {
                            Model.getCoreHelper().addComment(permissionClass,
                                                             comment);
                        }
                    }
                }
                LOGGER.debug("Adding permission: " + permission.
                             getPermAttribute());
                addPermission(permissionClass, permission.getPermAttribute());
            }
        } else if (!permission.isValid()) {
            throw new MapperException(permission.getErrorMessage());
        }
    }

    /**
     * Dispatcher for
     * {@link #createPermission(Permission) createPermission}
     * to handle abstract actors.
     * Makes a backup of the old constraints and uses a different
     * collection for the forks, which is reset in every iteration.
     * This ensures if a future manipulation of the permission's
     * constraints makes changes, the intial state will be restored.
     *
     * @param permission The permission to be created.
     *
     * @throws MapperException If the given permission could not be
     *                         created. More precisely the
     *                         <code>exception</code> is thrown by the
     *                         {@link #createPermission(Permission)
     *                          createPermission} method.
     *
     * @see #createPermission(Permission)
     */
    private void createPermissionAActor(final Permission permission)
    throws MapperException {
        final Collection oldConstraints = permission.getConstraints();
        final Collection newConstraints = new ArrayList();

        for (Object specialization : facade.
                getSpecializations(permission.getSender())) {

            final Object child = facade.getChild(specialization);
            LOGGER.debug("Dispatch from " + facade.
                         getName(permission.getSender())
                         + " to " + facade.getName(child));

            newConstraints.clear();
            newConstraints.addAll(oldConstraints);
            newConstraints.addAll(facade.getComments(permission.getSender()));
            permission.setName(permission.getName());
            permission.setSender(child);
            permission.setConstraints(newConstraints);

            createPermission(permission);
        }
    }

    /**
     * Retrieves the permission class for a subject and object if existing.
     * Takes the subject and object pair and constraints
     * and looks for an appropriate SecureUML permission class.
     * If there is no such class, <code>null</code> is returned.
     *
     * @param subject     The Object containing the subject.
     * @param object      The Object containing the object.
     * @param comments    The Collection containing optional constraints.
     * @return The permission class or <code>null</code> if none found.
     */
    private Object getPermissionClass(final Object subject,
                                      final Object object,
                                      final Collection comments) {

        final Collection permissionClasses =
            getPermissionClassesNS(facade.getNamespace(subject));

        for (Object currClass : permissionClasses) {
            final Collection connections = facade.getConnections(currClass);

            final Object association = facade.getAssociation(connections.
                                       iterator().next());
            /* Check if source and destination match
               the subject and the object */
            final String sourceName = facade.getName(Model.getCoreHelper().
                                      getSource(association));
            final String destName = facade.getName(Model.getCoreHelper().
                                                   getDestination(association));
            final Collection currComments = facade.getComments(currClass);

            if (sourceName != null && destName != null
                    && sourceName.equals(facade.getName(subject))
                    && destName.equals(facade.getName(object))
                    && currComments.equals(comments)) {
                LOGGER.debug("Permission class already exists: "
                             + facade.getName(currClass));
                return currClass;
            }
        }
        return null;
    }

    /**
     * Retrieves the permission classes for a subject and object
     * which have the same or less comments (=constraints).
     * Takes the subject and object pair and constraints
     * and looks for an appropriate SecureUML permission class.
     * If there is no such class, <code>null</code> is returned.
     * Also tries to find a appropriate permission among the
     * parents of the subject by traversing the role hierarchy.
     *
     * @param subject     The Object containing the subject.
     * @param predicate   The string holding the predicate.
     * @param object      The Object containing the object.
     * @param comments    The Collection containing optional constraints.
     * @return a collection of equal permissions or <code>null</code>
     *                    if none found.
     *            is already present.
     */
    private Collection getPermissions(final Object subject,
                                      final String predicate,
                                      final Object object,
                                      final Collection comments) {

        final Collection permissions = new ArrayList();
        final Collection permissionClasses =
            getPermissionClassesNS(facade.getNamespace(subject));

        for (Object currClass : permissionClasses) {
            final Collection connections = facade.getConnections(currClass);

            final Object association = facade.getAssociation(connections.
                                       iterator().next());
            /* Check if source and destination match
               the subject and the object */
            final Collection reachableActors = Model.getCoreHelper().
                                               getAllSupertypes(subject);
            reachableActors.add(subject);

            for (Object actor : reachableActors) {
                final String sourceName = facade.
                                          getName(Model.getCoreHelper().getSource(association));
                final String destName = facade.
                                        getName(Model.getCoreHelper().getDestination(association));
                final Collection currComments = facade.getComments(currClass);

                if (!facade.isAbstract(actor)
                        && sourceName != null && destName != null
                        && sourceName.equals(facade.getName(actor))
                        && destName.equals(facade.getName(object))
                        && comments.containsAll(currComments)) {

                    permissions.addAll(getClassPermissions(currClass,
                                                           predicate));
                }
            }
        }
        return permissions;
    }

    /**
     * Tests whether a permission is already given.
     * This method is different from
     * {@link #getPermissions(Object,String,Object,Collection) getPermissions}
     * in the way that only the direct subjects are compared
     * and the role hierarchy is ignored. Permissions
     * having less constraints are also ignored.
     * These checks are delegated to <code>getPermissions</code>
     * by calling it after the comments have been resolved.
     * This methode checks only for exact matches and
     * is used in
     * {@link #createPermission(Permission) createPermission}
     * to make sure, such a permission does not
     * already exist.
     * In contrast to <code>getPermissions</code> this method
     * can also deal with comments which are not attached to
     * a permission yet by resolving these to already existing
     * ones.
     *
     * @param subject An object being the subject of the permission.
     * @param predicate A string describing the action.
     * @param object An object being the object of the permission.
     * @param comments A collection of comments of the permission.
     * @return <code>true</code> if the permission already exists.
     * @see #getPermissions(Object,String,Object,Collection)
     * @see #createPermission(Permission)
     */
    private boolean hasPermission(final Object subject,
                                  final String predicate,
                                  final Object object,
                                  final Collection comments) {
        final Collection permClasses = getPermissionClassesNS(model);
        final Collection newComments = new ArrayList();
        final Collection existComments = new ArrayList(comments);
        final Collection classComments = new ArrayList();
        Collection foundComments = new ArrayList();

        // Split the comments into existing ones and ones to be created
        for (Object comment : comments) {
            if (facade.getNamespace(comment) == null) {
                existComments.remove(comment);
                newComments.add(comment);
            }
        }
        for (Object permClass : permClasses) {
            LOGGER.debug(facade.getName(permClass));
            if (facade.getComments(permClass).containsAll(existComments)) {
                LOGGER.debug("has all perms: " + facade.getName(permClass));
                classComments.clear();
                classComments.addAll(facade.getComments(permClass));
                classComments.removeAll(existComments);
                foundComments = getMatchingComments(newComments,
                                                    classComments);
                classComments.addAll(foundComments);
                classComments.addAll(existComments);
                if (newComments.size() == foundComments.size()
                        && getPermissions(subject, predicate, object,
                                          classComments).size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Compares the comments of a given collection <code>newComments</code>
     * with another collection <code>oldComments</code> and returns
     * a collection of objects from <code>oldComments</code> which
     * match by <code>String</code> comparison.
     *
     * @param newComments A collection of comments objects.
     * @param oldComments Another collection of comments objects.
     * @return The collection of matching comments in terms of
     *         <code>oldComments</code>.
     */
    private Collection getMatchingComments(final Collection newComments,
                                           final Collection oldComments) {
        final Collection foundComments = new ArrayList();

        for (Object newComment : newComments) {
            boolean isCommentAdded = false;
            for (Object oldComment : oldComments) {
                if (!isCommentAdded
                        && ((String) facade.getBody(newComment)).
                        equals((String) facade.getBody(oldComment))) {
                    foundComments.add(oldComment);
                    isCommentAdded = true;
                }
            }
        }
        return foundComments;
    }

    /**
     * Checks whether a given permission class has a predicate.
     *
     * @param permClass  The Object containing the permission class.
     * @param predicate  The String formulating the access rights.
     * @return the object reference to the permission or <code>null</code>
     *                   if not found.
     */
    private Collection getClassPermissions(final Object permClass,
                                           final String predicate) {
        final Collection permissions = new ArrayList();
        final Collection attributes = facade.getAttributes(permClass);
        String predicateTarget = predicate;
        String permissionType = "";

        if (predicate.indexOf(':') != -1) {
            permissionType = predicate.split(":")[1];
            predicateTarget = predicate.split(":")[0];
        }
        for (Object attribute : attributes) {
            LOGGER.debug(predicate + " vs. " + facade.getName(attribute));
            if (facade.getType(attribute) != null && permissionType.
                    equals(facade.getName(facade.getType(attribute)))) {
                if ((permissionType.equals(READ_NAME)
                        || permissionType.equals(UPDATE_NAME))
                        &&  facade.getName(attribute).indexOf('.') == -1) {
                    LOGGER.debug(facade.getName(permClass)
                                 + "has predicate " + predicate);
                    permissions.add(attribute);
                } else if (predicateTarget.equals(facade.
                                                  getName(attribute))) {
                    LOGGER.debug(facade.getName(permClass)
                                 + " has predicate " + predicate);
                    permissions.add(attribute);
                }
            }
        }
        return permissions;
    }

    /**
     * Adds a given predicate to a given permission class object.
     *
     * @param permClass An object being the target permission class.
     * @param predicate A string containing the new predicate.
     */
    private void addPermission(final Object permClass,
                               final String predicate) {
        String target = predicate;
        String action = "";
        Object attribute;

        if (predicate.indexOf(':') == -1) {
            attribute = Model.getCoreFactory().createAttribute();
            Model.getCoreHelper().addFeature(permClass, attribute);
        } else {
            target = predicate.split(":")[0];
            action = predicate.split(":")[1];

            attribute = Model.getCoreFactory().
                        buildAttribute(permClass, model, mapperHelper.
                                       getSecureUMLActionType(model, action));
            if (target.indexOf('.') == -1) {
                Model.getCoreHelper().
                addStereotype(attribute, mapperHelper.
                              getSecureUMLStereotype(model, "dialect.entityaction"));
            } else if (READ_NAME.equals(action) || UPDATE_NAME.equals(action)) {
                Model.getCoreHelper().
                addStereotype(attribute, mapperHelper.
                              getSecureUMLStereotype(model, "dialect.entityattributeaction"));
            } else if (EXECUTE_NAME.equals(action)) {
                Model.getCoreHelper().
                addStereotype(attribute, mapperHelper.
                              getSecureUMLStereotype(model, "dialect.entitymethodaction"));
            }
        }
        Model.getCoreHelper().setName(attribute, target);
    }

    /**
     * Analyzes the includes of a given use-case.
     *
     * @param usecase An object being a use-case.
     *
     * @throws MapperException If the analyzation of the given usecase fails.
     */
    private void analyzeIncludes(final Object usecase)
    throws MapperException {
        LOGGER.debug("Analyzing includes of usecase: "
                     + facade.getName(usecase));
        final Collection connections = facade.getIncludes(usecase);

        for (Object connection : connections) {
            final Object connected = Model.getCoreHelper().
                                     getDestination(connection);

            LOGGER.debug("Analyzing include: " + facade.getName(connected));
            for (Object incCollab : facade.getCollaborations(connected)) {
                analyzeExtendInclude(usecase, incCollab,
                                     facade.getComments(connection));
            }
        }
    }

    /**
     * Analyzes a given collaboration of a given use-case in terms
     * of its extenders.
     *
     * @param usecase The object of the use-case being analyzed.
     * @param collaboration An object holding a collaboration of the
     *                      given use-case.
     *
     * @throws MapperException If the analyzation of the extend fails.
     */
    private void analyzeExtends(final Object usecase,
                                final Object collaboration)
    throws MapperException {
        LOGGER.debug("Analyzing extends of usecase: "
                     + facade.getName(usecase));
        final Collection extenders = facade.getExtends(usecase);
        final Collection constraints = new ArrayList();

        for (Object extend : extenders) {
            LOGGER.debug("Analyzing extend to: "
                         + facade.getName(Model.getCoreHelper().
                                          getDestination(extend)));
            constraints.clear();
            String extendCondition = "";
            if (facade.getCondition(extend) != null) {
                extendCondition = (String) facade.
                                  getBody(facade.getCondition(extend));
            }

            if (!"".equals(extendCondition)) {
                final Object comment = Model.getCoreFactory().createComment();
                Model.getCoreHelper().setBody(comment, (String)
                                              facade.getBody(facade.
                                                      getCondition(extend)));
                constraints.add(comment);
            }
            analyzeExtendInclude(Model.getCoreHelper().getDestination(extend),
                                 collaboration, constraints);
        }
    }

    /**
     * Analyze extend or include of a given usecase by looking at a given
     * collaboration and possible constraints.
     * Uses {@link MapperHelper#getCollabMessages(Object) getCollabMessages}
     * from {@link MapperHelper MapperHelper} to determine the
     * messages of the collaboration.
     *
     * @param usecase An object being a use case.
     * @param collaboration An object being the collaboration
     *                      to look at.
     * @param comments A collection of comments being possible constraints.
     *
     * @throws MapperException If the analyzation of the include or exclude
     *                         fails.
     *
     * @see MapperHelper#getCollabMessages(Object)
     */
    private void analyzeExtendInclude(final Object usecase,
                                      final Object collaboration,
                                      final Collection comments)
    throws MapperException {
        final Collection actors = Model.getCoreHelper().
                                  getAssociatedClassifiers(usecase);

        LOGGER.debug("Mapping rights for associated usecase.");
        for (Object message : mapperHelper.
                getCollabMessages(collaboration)) {
            mapMessageToPermissions(message, actors, comments);
        }
    }

    /**
     * Removes all duplicate permissions and empty permission classes.
     */
    private void cleanup() {
        final Collection permissionClasses = getPermissionClassesNS(model);

        for (Object permissionClass : permissionClasses) {
            for (Object attribute : facade.getAttributes(permissionClass)) {

                final String permissionType = facade.getName(facade.
                                              getType(attribute));
                final String predicateTarget = facade.getName(attribute);

                final Collection connections = facade.
                                               getConnections(permissionClass);

                final Object association = facade.getAssociation(connections.
                                           iterator().next());
                final Object subject = Model.getCoreHelper().
                                       getSource(association);
                final Object object = Model.getCoreHelper().
                                      getDestination(association);
                final String predicate = predicateTarget + ":"
                                         + permissionType;

                final Collection permissions = getPermissions(subject,
                                               predicate,
                                               object, facade.
                                               getComments(permissionClass));
                LOGGER.debug("For permission: " + predicate + " of role "
                             + facade.getName(subject) + " found "
                             + permissions.size() + " equal permissions");
                if (permissions.size() > 1) {
                    LOGGER.debug("removing permission for: "
                                 + facade.getName(subject));
                    Model.getCoreHelper().
                    removeFeature(permissionClass, attribute);
                }
            }
            // remove empty permission classes
            if (facade.getAttributes(permissionClass).isEmpty()) {
                Model.getCoreHelper().
                removeOwnedElement(facade.getNamespace(permissionClass),
                                   permissionClass);
            }
        }
    }

    /**
     * Retrieves all <code>SecureUML</code> permission classes
     * from a given namespace.
     *
     * @param namespace The Object containing the target namespace.
     * @return A Collection containing all permission classes found.
     */
    private Collection getPermissionClassesNS(final Object namespace) {
        final Collection permissionClasses = new ArrayList();
        final Collection allClasses = Model.getCoreHelper().
                                      getAllClasses(namespace);

        for (Object currClass : allClasses) {
            if (Model.getExtensionMechanismsHelper().
                    hasStereoType(currClass, SecureUmlConstants.STEREOTYPE_SECUML_PERMISSION)) {
                permissionClasses.add(currClass);
            }
        }
        return permissionClasses;
    }

    /**
     * Adds the authorization constraint to the given permission.
     * If the authorization constraint is invalid or empty,
     * no comment will be created in the model.
     *
     * @param permission The object being a permission.
     */
    private void addAuthConstraint(final Permission permission) {
        if (permission.isSecureUMLPermission()
                && permission.isValid()
                && permission.isAuthValid()
                && !"".equals(permission.getAuthConstraint())) {

            final Object comment = Model.
                                   getCoreFactory().createComment();
            Model.getCoreHelper().
            setBody(comment, permission.getAuthConstraint());
            permission.addConstraint(comment);
        }
    }
}
