package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

/**
 * The interface for all mapping strategies.
 * The {@link ch.ethz.infsec.secureumlgui.usecasemapper.control.Controller
 * Controller}
 * uses a strategy pattern
 * to perform its mappings.
 *
 * @version 1.0
 * @see ch.ethz.infsec.secureumlgui.usecasemapper.control.Controller
 */
public interface MapperStrategy {

    /**
     * Performs the mapping and by this implements the strategy.
     *
     * @throws MapperException The <code>exception</code> to indicate
     *                         the mapping process failed.
     */
    void map() throws MapperException;

    /**
     * Sets the model the mapping is performed on.
     *
     * @param model The model object being the target model.
     */
    void setModel(Object model);
}
