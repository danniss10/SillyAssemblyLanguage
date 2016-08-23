/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assemblererrors;

/**
 *
 * @author Daniel Nissenbaum
 */
public class PoorlyFormedLineError extends Error{
    
    public PoorlyFormedLineError(int line){
        super("Error line " + line + ": Poorly formed assembly line");
    }
    
}
