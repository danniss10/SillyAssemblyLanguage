/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualmachine;

import simple08.*;

public class Driver {

    public static void main(String args[]) {

        VirtualMachine vm = new VirtualMachine(10000,null);
        assembleronly.Assembler asm = new assembleronly.Assembler();
        assembler.Assembler testasm = new assembler.Assembler();
        SimpleConsoleIO scio = new SimpleConsoleIO();
        String command = "";
        String parameter = "";
        String line = "";


        do {
            command = "";
            parameter = "";
            line = "";
            
            line = scio.getEntireLinePrompt("sap>");
            if (line.indexOf(' ') > 0) {
                command = line.substring(0, line.indexOf(' '));
                parameter = line.substring(line.indexOf(' ') + 1, line.length());
            } else {
                command = line;
            }
            if (command.equals("asm")) {

                System.out.println("Assembling" + parameter + ".txt");
                //asm.assembleFile(parameter);

                testasm.assembleFile(parameter);
                

                //System.out.println(testasm.getErrMess());
            } else if (command.equals("run")) {

                System.out.println("Running " + parameter);
                vm.runBytecodeFile(parameter, false);
//                System.out.println("Done!");
            } else if (command.equals("debug")) {
                System.out.println("Debugging " + parameter);
                vm.runBytecodeFile(parameter, true);
//                System.out.println("Done!");
            } else if (!command.equals("exit")) {
                System.out.println("Unrecognized command '" + command + "'");
            }
        } while (!command.equals("exit"));
        System.out.println("SAP Assembler exited successfully");
    }
}