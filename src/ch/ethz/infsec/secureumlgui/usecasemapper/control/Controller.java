package ch.ethz.infsec.secureumlgui.usecasemapper.control;

import ch.ethz.infsec.secureumlgui.usecasemapper.gui.View;
import ch.ethz.infsec.secureumlgui.usecasemapper.mapping.MapperStrategy;
import ch.ethz.infsec.secureumlgui.usecasemapper.mapping.ActorMapper;
import ch.ethz.infsec.secureumlgui.usecasemapper.mapping.PermissionMapper;
import ch.ethz.infsec.secureumlgui.usecasemapper.mapping.MapperException;
import ch.ethz.infsec.secureumlgui.usecasemapper.mapping.MapperHelper;

import java.util.Collection;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.argouml.model.Model;

/**
 * <code>Controller</code> handles the mapping process on
 * a high level. It is the client in a strategy pattern,
 * which uses concrete strategies for different mapping
 * tasks.
 *
 * @version 1.0
 */
public class Controller {

    /**
     * The <code>log4j</code>-logger of this class.
     *
     */
    private static final Logger LOGGER = Logger.
                                         getLogger(MenuActionListener.class);

    /**
     * The reference to the
     * {@link ch.ethz.infsec.secureumlgui.usecasemapper.gui.View}
     * used for visual feedback.
     */
    private final View view = new View();

    /**
     * The mapping strategies used in
     * {@link ch.ethz.infsec.secureumlgui.usecasemapper.control.Controller#map()}.
     */
    private final Collection<MapperStrategy> mappers =
        new ArrayList<MapperStrategy>();

    /**
     * Initializes the mapper collection with the desired mapping
     * strategies.
     */
    public Controller() {
        mappers.add(new ActorMapper());
        mappers.add(new PermissionMapper());

        LOGGER.debug("Mappers added");
    }

    /**
     * Starts the mapping process.
     * First checks, whether there is a cyclic dependency in the
     * role hierarchy, as this could prevent the mapping strategies
     * from working as expected.
     * Then the control is passed to the registered mapping
     * algorithms.
     */
    public final void map() {

        final Object model = Model.getModelManagementFactory().getRootModel();

        if (MapperHelper.getInstance().hasNoCircularInheritance(model)) {
            try {
                MapperHelper.getInstance().initSecureUML(model);

                for (MapperStrategy strategy : mappers) {
                    LOGGER.debug("Starting mapping strategy: " + strategy);

                    strategy.setModel(model);
                    strategy.map();
                }
                view.showInfo("Mapping complete.");
            } catch (MapperException me) {
                view.showException("Mapping exception ocurred:", me);
            }
        } else {
            view.showException("Circular dependency in "
                               + "role hierarchy detected.");
        }
    }
}
