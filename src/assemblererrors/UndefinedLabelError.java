/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assemblererrors;

/**
 *
 * @author Daniel Nissenbaum
 */
public class UndefinedLabelError extends Error{
    
    public UndefinedLabelError(int line, String label){
        super("Error line " + line + ": Undefined label. The label: \"" + label + "\" is not declared in the program.");
    }
}
