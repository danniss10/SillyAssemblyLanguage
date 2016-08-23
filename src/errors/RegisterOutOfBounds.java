/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package errors;

/**
 *
 * @author Daniel Nissenbaum
 */
public class RegisterOutOfBounds extends Error{
        public RegisterOutOfBounds(int register){
        super("RegisterLoactionOutOfBounds.\nRegister bounds: 0-9. Attempted access: " + register + "."  );
    }
}
