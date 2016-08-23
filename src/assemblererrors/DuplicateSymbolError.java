/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assemblererrors;

/**
 *
 * @author Daniel Nissenbaum
 */
public class DuplicateSymbolError extends Error{
    
    public DuplicateSymbolError(int line, String label){
        super("Error line " + line + ": Duplicate Symbols defined: \"" + label + "\"");
    }
}
