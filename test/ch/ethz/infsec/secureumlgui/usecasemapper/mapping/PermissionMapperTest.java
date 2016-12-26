package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import org.apache.log4j.Logger;

import org.argouml.model.Model;

/**
 * JUnit test class for the mapping strategy
 * {@link PermissionMapper PermissionMapper}.
 *
 * @version 1.0
 * @see PermissionMapper
 */
public class PermissionMapperTest {

    /**
     * The log4j logger for this class.
     */
    private static final Logger LOGGER =
        Logger.getLogger(PermissionMapperTest.class);

    /**
     * The {@link MapperTestHelper MapperTestHelper} singleton
     * as a reference for enhanced readability.
     */
    private final MapperTestHelper mapperTestHelper = MapperTestHelper.
            getInstance();

    /**
     * The model being setup and which is used to test the
     * {@link PermissionMapper PermissionMapper}.
     */
    private Object model;

    /**
     * The permission mapper used for the tests.
     */
    private final PermissionMapper permissionMapper = new PermissionMapper();

    /**
     * Initalizes the model according to the class description.
     */
    @Before public final void setup() {

        model = Model.getModelManagementFactory().createModel();
        mapperTestHelper.initTestModel(model);
        permissionMapper.setModel(model);
    }

    /**
     * Checks if all SecureUML messages have been mapped.
     */
    @Test public final void mapsAllMessages() {
        try {
            permissionMapper.map();
        } catch (MapperException exception) {
            LOGGER.error("Permission mapper failed.");
            fail("The mapping of the permission mapper failed.");
        }
    }
}
