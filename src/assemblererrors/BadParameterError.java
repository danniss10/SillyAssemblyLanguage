package assemblererrors;

/**
 *
 * @author Daniel Nissenbaum
 */
public class BadParameterError extends Error {

    public BadParameterError(int line, String given, String expected) {
        super("Error line " + line + ": Bad parameter. Given: \"" + given + "\". Expected a " + expected);
    }
}
