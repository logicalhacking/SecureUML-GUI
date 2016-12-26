package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.apache.log4j.Logger;

import org.argouml.model.Model;

/**
 * JUnit test the singleton class
 * {@link MapperHelper MapperHelper}.
 *
 * @version 1.0
 * @see MapperHelper
 */
public class MapperHelperTest {


    /**
     * Logger for the <code>MapperHelperTest</code> class.
     */
    private static final Logger LOGGER = Logger.
                                         getLogger(MapperHelperTest.class);

    /**
     * Global reference to the target model  the <code>MapperHelper</code>
     * is tested against.
     */
    private final Object model = Model.getModelManagementFactory().
                                 createModel();

    /**
     * Reference to the <code>MapperHelper</code> instance for
     * enhanced readability.
     */
    private final MapperHelper mapperHelper = MapperHelper.getInstance();

    /**
     * Setup the SecureUML package for the tests.
     */
    @Before public final void setup() {
        try {
            MapperHelper.getInstance().initSecureUML(model);
        } catch (MapperException exception) {
            LOGGER.fatal("Could not initialize model");
        }
    }

    /**
     * Checks of circular inheritance relationships are correctly detected.
     */
    @Test public final void detectsCircularInheritance() {
        final Object actorA = Model.getUseCasesFactory().createActor();
        final Object actorB = Model.getUseCasesFactory().createActor();
        final Object actorC = Model.getUseCasesFactory().createActor();
        Model.getCoreHelper().addOwnedElement(model, actorA);
        Model.getCoreHelper().addOwnedElement(model, actorB);
        Model.getCoreHelper().addOwnedElement(model, actorC);
        Model.getCoreFactory().buildGeneralization(actorA, actorB);
        Model.getCoreFactory().buildGeneralization(actorB, actorC);
        Model.getCoreFactory().buildGeneralization(actorC, actorA);

        assertFalse("Model contains circular inheritance",
                    mapperHelper.hasNoCircularInheritance(model));
    }

    /**
     * Checks if the complete SecureUML package is setup
     * after the invocation of {@link MapperHelper#initSecureUML(Object)}.
     */
    @Test public final void allStereosNActionsSetup() {
        for (String stereoName : mapperHelper.getSecumlSteNames()) {
            assertNotNull("Stereotype: " + stereoName + "should be present",
                          mapperHelper.getSecureUMLStereotype(model,
                                  stereoName));
        }

        for (String actionName : mapperHelper.getCompActTypNames()) {
            assertNotNull("Dialect action type: " + actionName
                          + "should be present", mapperHelper.
                          getSecureUMLActionType(model, actionName));
        }
    }
}
