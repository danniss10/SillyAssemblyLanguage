
package errors;

/**
 *  MemoryOutOfBounds is an exception thrown when a non existing memory location is accessed.
 * @author Daniel Nissenbaum
 */
public class MemoryOutOfBounds extends Error{
    
    public MemoryOutOfBounds(int memorySize, int location){
        super("MemoryLoactionOutOfBounds.\nMemory bounds: 0-" + (memorySize-1) + ". Attempted access: " + location + "."  );
    }
}
