package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import java.util.Collection;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.argouml.model.Model;
import org.argouml.model.Facade;

/**
 * Mapping strategy for mapping non abstract actors to SecureUML
 * roles. The details of the strategy are described in the
 * comments for the method {@link #map() map}.
 *
 * @version 1.0
 */
public class ActorMapper implements MapperStrategy {

    /**
     * The <code>log4j</code>-logger of this class.
     *
     */
    private static final Logger LOGGER = Logger.getLogger(ActorMapper.class);

    /**
     * Global reference to the target model.
     */
    private Object model;

    /**
     * Global reference to the ArgoUML facade to enhance readability.
     */
    private final Facade facade = (Facade) Model.getFacade();

    /**
     * Maps the non abstract actors to SecureUML roles.
     * The mapping process consists of two steps:
     * 1. For all actors SecureUML role classes are created
     *    if they are not already present.
     * 2. The hierarchy of the actors is mapped on the role clases.
     *    In this step the gaps which may have been introduced into
     *    the hierarchy by ignoring the abstract actors are filled
     *    with the appropriate non abstract actors.
     * @throws MapperException Thrown if the mapping process fails.
     */
    public final void map() throws MapperException {

        if (model == null) {
            LOGGER.fatal("Target model undefined");
            throw new MapperException("Target model undefined");
        } else {
            LOGGER.debug("Getting all non abstract actors");

            for (Object actor : getNonAbstractActors()) {
                final String actorName = facade.getName(actor);

                LOGGER.debug("Current actor: " + actorName);

                if (isActorAlreadyMapped(actor)) {
                    LOGGER.debug("Actor: " + actorName + " already mapped");
                } else {
                    createSecureUMLRole(actor);

                    LOGGER.debug("Actor: " + actorName
                                 + " mapped to SecureUML role");
                }
            }

            for (Object actor : getNonAbstractActors()) {
                for (Object parent : getNonAbstractParents(actor)) {
                    LOGGER.debug("Current generalize: "
                                 + facade.getName(parent) + " <-- "
                                 + facade.getName(actor));
                    final Object parentClass = getRoleClassByActor(parent);
                    final Object actorClass = getRoleClassByActor(actor);

                    if (facade.getGeneralization(actorClass,
                                                 parentClass) == null) {
                        Model.getCoreFactory().
                        buildGeneralization(actorClass, parentClass);
                        LOGGER.debug("Added generalize: "
                                     + facade.getName(parent) + " <-- "
                                     + facade.getName(actor));
                    }
                }
            }
        }
    }

    /**
     * Sets the model the mapping is performed on.
     *
     * @param targetModel The model being the target model.
     */
    public final void setModel(final Object targetModel) {
        model = targetModel;
    }

    /**
     * Creates a class with stereotype secuml.&nbsp;role for a given
     * actor. The role class is created in the namespace of the actor.
     *
     * @param actor actor object for which the role should be created
     */
    private void createSecureUMLRole(final Object actor) {

        final Object targetNamespace = facade.getNamespace(actor);
        final String roleClassName = facade.getName(actor);

        final Object roleClass = Model.getCoreFactory().buildClass(
                                     roleClassName,
                                     targetNamespace);

        // Add the SecureUML stereotype
        final Object stereotype = MapperHelper.getInstance().
                                  getSecureUMLStereotype(model, "secuml.role");

        Model.getCoreHelper().addStereotype(roleClass, stereotype);
    }

    /**
     * Gets the corresponding SecureUML role class for a given
     * actor or returns null if not found.
     * Checks if there is already a SecureUML role with the
     * name of the actor in its namespace.
     *
     * @param actor An object representing an actor.
     * @return an object representing the role class or null
     *         if not present.
     */
    private Object getRoleClassByActor(final Object actor) {
        if (facade.getName(actor) != null) {
            final Collection allClasses = Model.
                                          getCoreHelper().getAllClasses(facade.getNamespace(actor));

            for (Object currClass : allClasses) {

                if (facade.getName(actor).equals(facade.getName(currClass))
                        && Model.getExtensionMechanismsHelper().
                        hasStereoType(currClass, "secuml.role")) {

                    return currClass;
                }
            }
        }
        return null;
    }

    /**
     * Helper function to check whether the actors was already
     * mapped. Checks if there is already a SecureUML
     * role with the name of the actor in its namespace.
     * This function is only a dispatcher to
     * {@link #getRoleClassByActor(Object) getRoleClassByActor}.
     *
     * @param actor An object representing an actor.
     * @return <code>true</code> if the <code>actor</code> has been
     *        mapped already. This means the corresponding SecureUML role
     *        class with stereotype &lt;&lt;secuml.role&gt;&gt; is present.
     * @see #getRoleClassByActor(Object)
     */
    private boolean isActorAlreadyMapped(final Object actor) {
        return getRoleClassByActor(actor) != null;
    }

    /**
     * Gets all non abstract parents of a given actor. If there
     * is a abstract actor among the direct parents, the non
     * abstract parents of the abstract actor are added until
     * there are no more abstract actors in the current iteration.
     *
     * @param actor An object representing an actor.
     * @return A collection containing all non abstract parents.
     */
    private Collection getNonAbstractParents(final Object actor) {
        final Collection parents = new ArrayList();

        for (Object generalize : facade.getGeneralizations(actor)) {
            final Object parent = facade.getParent(generalize);
            if (facade.isAbstract(parent)) {
                parents.addAll(getNonAbstractParents(parent));
            } else {
                parents.add(parent);
            }
        }
        return parents;
    }

    /**
     * Gets all non abstract actors among all actors.
     *
     * @return A collection containing all non abstract actors.
     */
    private Collection getNonAbstractActors() {
        final Collection actors = Model.getUseCasesHelper().
                                  getAllActors(model);
        final Collection nonAbstractActors = new ArrayList();

        for (Object actor : actors) {
            if (!facade.isAbstract(actor)) {
                nonAbstractActors.add(actor);
            }
        }
        return nonAbstractActors;
    }
}
