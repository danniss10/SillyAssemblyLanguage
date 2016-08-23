/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assemblererrors;

/**
 *
 * @author Daniel Nissenbaum
 */
public class UnrecognizedInstructionError extends Error {

    public UnrecognizedInstructionError(int line, String givenInstruction) {
        super("Error line " + line + ": Unrecognized instruction. Given:" + givenInstruction);
    }
}
