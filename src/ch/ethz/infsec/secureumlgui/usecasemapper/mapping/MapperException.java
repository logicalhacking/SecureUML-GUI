package ch.ethz.infsec.secureumlgui.usecasemapper.mapping;

/**
 * The <code>exception</code> for handling
 * errors during the mapping process.
 * {@link MapperStrategy#map() MapperStrategy.map()} throws
 * this <code>exception</code> in case of an error.
 *
 * @version 1.0
 */
public final class MapperException extends Exception {

    /**
     * Overidden default constructor to pass the
     * the <code>exception</code>-message.
     *
     * @param message String containing the message.
     */
    public MapperException(final String message) {
        super(message);
    }
}
