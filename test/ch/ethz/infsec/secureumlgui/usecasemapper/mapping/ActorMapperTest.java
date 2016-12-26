package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.argouml.model.Model;
import org.argouml.model.CoreHelper;

/**
 * JUnit test class for the mapping strategy
 * {@link ActorMapper ActorMapper}.
 *
 * @version 1.0
 * @see ActorMapper
 */
public class ActorMapperTest {

    /**
     * Reference to the ArgoUML <code>CoreHelper</code> for
     * enhanced readability.
     */
    private final CoreHelper helper = Model.getCoreHelper();

    /**
     * The actor mapper used for the tests.
     */
    private final ActorMapper actorMapper = new ActorMapper();

    /**
     * The model being setup and which is used to test the
     * {@link ActorMapper ActorMapper}.
     */
    private Object model;

    /**
     * The {@link MapperHelper MapperHelper} singleton as
     * a reference for enhanced readability.
     */
    private final MapperHelper mapperHelper = MapperHelper.getInstance();

    /**
     * The {@link MapperTestHelper MapperTestHelper} singleton
     * as a reference for enhanced readability.
     */
    private final MapperTestHelper mapperTestHelper = MapperTestHelper.
            getInstance();

    /**
     * Initalizes the model according to the class description.
     * Sets the model as current model in the actor mapper.
     */
    @Before public final void setup() {
        model = Model.getModelManagementFactory().createModel();
        mapperTestHelper.initTestModelActors(model);
        actorMapper.setModel(model);
    }

    /**
     * Checks if the MapperException is correctly thrown if there
     * is no model set.
     */
    @Test public final void noMapWithoutModel() {
        actorMapper.setModel(null);
        boolean exceptionOccured = false;
        try {
            actorMapper.map();
        } catch (MapperException exception) {
            exceptionOccured = true;
        }
        assertTrue(exceptionOccured);
    }

    /**
     * Checks if only the non abstract actors are mapped to
     * SecureUML roles.
     */
    @Test public final void mapOnlyNonAbstractActors() {
        try {
            actorMapper.map();
        } catch (MapperException exception) {
            fail();
        }
        assertNotNull("User not mapped", mapperHelper.
                      getRoleClassNS(mapperTestHelper.USER_NAME,
                                     mapperTestHelper.getTestPackage()));
        assertNotNull("SuperUser not mapped", mapperHelper.
                      getRoleClassNS(MapperTestHelper.SUPER_USER_NAME,
                                     mapperTestHelper.getTestPackage()));
        assertNotNull("OtherUser not mapped", mapperHelper.
                      getRoleClassNS(MapperTestHelper.OTHER_USER_NAME,
                                     mapperTestHelper.getTestPackage()));
        assertNotNull("Test not mapped", mapperHelper.
                      getRoleClassNS(MapperTestHelper.TEST_NAME,
                                     mapperTestHelper.getTestPackage()));
        assertNotNull("Guest not mapped", mapperHelper.
                      getRoleClassNS(MapperTestHelper.GUEST_NAME,
                                     mapperTestHelper.getTestPackage()));
        assertNull("EntryOwner mapped", mapperHelper.
                   getRoleClassNS(MapperTestHelper.ENTRY_OWNER_NAME,
                                  mapperTestHelper.getTestPackage()));
        assertNull("OtherOwner mapped", mapperHelper.
                   getRoleClassNS(MapperTestHelper.OTHER_OWNER_NAME,
                                  mapperTestHelper.getTestPackage()));
        assertNull("TestOwner mapped", mapperHelper.
                   getRoleClassNS(MapperTestHelper.TEST_OWNER_NAME,
                                  mapperTestHelper.getTestPackage()));
    }

    /**
     * Checks if the generalize relations are only to the next
     * concrete actor and not to any children thereof.
     */
    @Test public final void onlyInheritNextConcrete() {
        try {
            actorMapper.map();
        } catch (MapperException exception) {
            fail();
        }
        final Object testRole = mapperHelper.
                                getRoleClassNS(MapperTestHelper.TEST_NAME,
                                               mapperTestHelper.getTestPackage());
        final Object guestRole = mapperHelper.
                                 getRoleClassNS(MapperTestHelper.GUEST_NAME,
                                                mapperTestHelper.getTestPackage());
        final Object userRole = mapperHelper.
                                getRoleClassNS(MapperTestHelper.USER_NAME,
                                               mapperTestHelper.getTestPackage());
        final Object otherUserRole = mapperHelper.
                                     getRoleClassNS(MapperTestHelper.OTHER_USER_NAME,
                                             mapperTestHelper.getTestPackage());
        final Object superUserRole = mapperHelper.
                                     getRoleClassNS(MapperTestHelper.SUPER_USER_NAME,
                                             mapperTestHelper.getTestPackage());

        assertNotNull("Generalization: Test <-- OtherUser should be present",
                      helper.getGeneralization(otherUserRole, testRole));
        assertNotNull("Generalization: Guest <-- User should be present",
                      helper.getGeneralization(userRole, guestRole));
        assertNotNull("Generalization: "
                      + "OtherUser <-- SuperUser should be present",
                      helper.getGeneralization(superUserRole, otherUserRole));
        assertNotNull("Generalization: Test <-- SuperUser should be present",
                      helper.getGeneralization(superUserRole, testRole));
        assertNull("No Generalization: Guest <-- SuperUser as no direct heir",
                   helper.getGeneralization(superUserRole, guestRole));
    }

    /**
     * Checks if the mapper is able to determine the next
     * concrete heir, even if there are multiple abstract
     * actors inbetween in the hierarchy.
     */
    @Test public final void findConcreteChild() {
        try {
            actorMapper.map();
        } catch (MapperException exception) {
            fail();
        }
        final Object testRole = mapperHelper.
                                getRoleClassNS(MapperTestHelper.TEST_NAME,
                                               mapperTestHelper.getTestPackage());
        final Object superUserRole = mapperHelper.
                                     getRoleClassNS(MapperTestHelper.SUPER_USER_NAME,
                                             mapperTestHelper.getTestPackage());
        assertNotNull("Generalization Test <-- SuperUser not present",
                      helper.getGeneralization(superUserRole, testRole));
    }

    /**
     * Ensures multiple mapping runs do not change the model
     * anymore after the first run.
     */
    @Test public final void multipleMapsDontChange() {
        try {
            actorMapper.map();
        } catch (MapperException exception) {
            fail();
        }
        final int classCount = helper.
                               getAllClasses(mapperTestHelper.getTestPackage()).size();

        try {
            actorMapper.map();
        } catch (MapperException exception) {
            fail();
        }
        assertEquals("old class count != class count after 2nd map",
                     classCount, helper.
                     getAllClasses(mapperTestHelper.getTestPackage()).size());
    }

    /**
     * Checks if only the non existing roles are mapped and the
     * existing ones are not changed.
     */
    @Test public final void onlyMapNonExisting() {
        final Object testRole = Model.getCoreFactory().
                                buildClass(MapperTestHelper.TEST_NAME,
                                           mapperTestHelper.getTestPackage());
        final Object superUserRole = Model.getCoreFactory().
                                     buildClass(mapperTestHelper.SUPER_USER_NAME, mapperTestHelper.
                                                getTestPackage());

        final Object stereotype = MapperHelper.getInstance().
                                  getSecureUMLStereotype(model, "secuml.role");

        helper.addStereotype(testRole, stereotype);
        helper.addStereotype(superUserRole, stereotype);

        try {
            actorMapper.map();
        } catch (MapperException exception) {
            fail();
        }
        assertEquals("concrete role count != class count",
                     MapperTestHelper.CONCRETE_ROLE_SUM, helper.
                     getAllClasses(mapperTestHelper.getTestPackage()).size());
    }
}
