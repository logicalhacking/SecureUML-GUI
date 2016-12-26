package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import org.apache.log4j.Logger;

import org.argouml.model.Model;
import org.argouml.model.ModelManagementFactory;
import org.argouml.model.CoreHelper;
import org.argouml.model.CoreFactory;
import org.argouml.model.UseCasesFactory;

/**
 * Provides helper methods for the JUnit tests
 * of the mapping strategies.
 * Sets up the test model for the tests.
 *
 * Creates the following structure to test the mappers against:
 * <h2>Role hierarchy</h2>
 * <pre>
 *                       Test
 *                         ^
 *                         |
 *                         |
 * Guest "EntryOwner"  "OtherOwner"
 *  ^     ^        ^     ^      ^
 *  |     |        |     |      |
 *  \-----|        |     |      |
 *       User    OtherUser    "TestOwner"
 *         ^      ^             ^
 *         |      |            /
 *         |      |------------
 *        SuperUser
 * </pre>
 * The actor names in double quotes are abstract actors.
 *
 * @version 1.0
 */
public final class MapperTestHelper {

    /**
     * The <code>log4j</code>-logger of this class.
     *
     */
    private static final Logger LOGGER = Logger.
                                         getLogger(MapperTestHelper.class);

    /**
     * The singleton reference.
     */
    private static MapperTestHelper singleton = new MapperTestHelper();

    /**
     * The number of non abstract roles in the predefined model.
     */
    public static final int CONCRETE_ROLE_SUM = 5;
    /**
     * The name of the role "Test".
     */
    public static final String TEST_NAME = "Test";
    /**
     * The name of the role "Guest".
     */
    public static final String GUEST_NAME = "Guest";
    /**
     * The name of the role "EntryOwner".
     */
    public static final String ENTRY_OWNER_NAME = "EntryOwner";
    /**
     * The name of the role "OtherOwner".
     */
    public static final String OTHER_OWNER_NAME = "OtherOwner";
    /**
     * The name of the role "User".
     */
    public static final String USER_NAME = "User";
    /**
     * The name of the role "OtherUser".
     */
    public static final String OTHER_USER_NAME = "OtherUser";
    /**
     * The name of the role "TestOwner".
     */
    public static final String TEST_OWNER_NAME = "TestOwner";
    /**
     * The name of the role "SuerUser".
     */
    public static final String SUPER_USER_NAME = "SuperUser";

    /**
     * Reference to the ArgoUML <code>ModelManagementFactory</code>
     * for enhanced readability.
     */
    private final ModelManagementFactory manager = Model.
            getModelManagementFactory();

    /**
     * Reference to the ArgoUML <code>CoreHelper</code> for
     * enhanced readability.
     */
    private final CoreHelper helper = Model.getCoreHelper();

    /**
     * Reference to the ArgoUML <code>CoreFactory</code> for
     * enhanced readability.
     */
    private final CoreFactory factory = Model.getCoreFactory();
    /**
     * Reference to the ArgoUML <code>UseCasesFactory</code> for
     * enhanced readybility.
     */
    private final UseCasesFactory ucFactory = Model.getUseCasesFactory();

    /**
     * Test package to be created in the test model.
     */
    private Object testPackage;

    /**
     * The object for the actor of the first level of the hierarchy.
     */
    private Object testActor;
    /**
     * An object for an actor of the second level of the hierarchy.
     */
    private Object guestActor, entryOwnerActor, otherOwnerActor;
    /**
     * An object for an actor of the third level of the hierarchy.
     */
    private Object userActor, otherUserActor, testOwnerActor;
    /**
     * The object for the actor of the forth level of the hierarchy.
     */
    private Object superUserActor;

    /**
     * Private constructor due to singleton pattern.
     */
    private MapperTestHelper() { };

    /**
     * Gets the MapperTestHelper instance.
     *
     * @return the <code>MapperTestHelper</code>-instance
     */
    public static MapperTestHelper getInstance() {
        return singleton;
    }

    /**
     * Gets the test package object.
     *
     * @return The object reference to the test package.
     */
    public Object getTestPackage() {
        return testPackage;
    }

    /**
     * Creates an actor with a given name, puts it
     * into a given namespace and returns it.
     *
     * @param name      The string containing the name of the new actor.
     * @param namespace The object being the target namespace.
     * @return The object being the actor just added.
     */
    public Object addActorNS(final String name,
                             final Object namespace) {
        final Object actor = ucFactory.createActor();
        helper.setName(actor, name);
        helper.addOwnedElement(namespace, actor);

        return actor;
    }

    /**
     * Initializes the whole test model.
     * For a description see the class documentation.
     *
     * @param model The object holding the model to be initialized.
     */
    public void initTestModel(final Object model) {
        try {
            MapperHelper.getInstance().initSecureUML(model);
        } catch (MapperException exception) {
            LOGGER.fatal("Could not initialize test model");
        }
        initTestModelActors(model);
    }

    /**
     * Initializes all the actors and inheritance relations
     * of the test model.
     *
     * @param model The object holding the model which actors are initialized.
     */
    public void initTestModelActors(final Object model) {
        try {
            MapperHelper.getInstance().initSecureUML(model);
        } catch (MapperException exception) {
            LOGGER.fatal("Could not initialize test model");
        }

        testPackage = manager.createPackage();
        helper.setName(testPackage, "testpackage");
        helper.addOwnedElement(model, testPackage);

        testActor = addActorNS(TEST_NAME, testPackage);
        guestActor = addActorNS(GUEST_NAME, testPackage);
        entryOwnerActor = addActorNS(ENTRY_OWNER_NAME, testPackage);
        otherOwnerActor = addActorNS(OTHER_OWNER_NAME, testPackage);
        userActor = addActorNS(USER_NAME, testPackage);
        otherUserActor = addActorNS(OTHER_USER_NAME, testPackage);
        testOwnerActor = addActorNS(TEST_OWNER_NAME, testPackage);
        superUserActor = addActorNS(SUPER_USER_NAME, testPackage);

        helper.setAbstract(otherOwnerActor, true);
        helper.setAbstract(entryOwnerActor, true);
        helper.setAbstract(testOwnerActor, true);

        factory.buildGeneralization(otherOwnerActor, testActor);
        factory.buildGeneralization(userActor, guestActor);
        factory.buildGeneralization(userActor, entryOwnerActor);
        factory.buildGeneralization(otherUserActor, entryOwnerActor);
        factory.buildGeneralization(otherUserActor, otherOwnerActor);
        factory.buildGeneralization(testOwnerActor, otherOwnerActor);
        factory.buildGeneralization(superUserActor, userActor);
        factory.buildGeneralization(superUserActor, otherUserActor);
        factory.buildGeneralization(superUserActor, testOwnerActor);
    }
}
