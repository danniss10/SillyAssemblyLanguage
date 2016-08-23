
package assemblererrors;

/**
 *
 * @author Daniel Nissenbaum
 */
public class BadStringError extends Error{
    
    public BadStringError(int line){
        super("Error line " + line + ": Badly formatted String.");
    }
}
