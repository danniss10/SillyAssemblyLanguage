package virtualmachine;

import SillyBeans.*;
import assembler.Language;
import assembler.ParamType;
import errors.MemoryOutOfBounds;
import errors.RegisterOutOfBounds;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeSet;
import stack.Stack;

/**
 * Virtual Machine
 *
 * This is a virtual machine with debugging capabilities for a simplified
 * assembly language. It has the capabilities of running in the system console
 * or in a custom environment
 *
 * @author Daniel Nissenbaum
 */
public class VirtualMachine {

    private int compareRegister = 0;

    // Stack Register and possible values
    private Stack stack = new Stack(400);
    private int stackRegister = STACK_EMPTY;
    private static final int STACK_OK = 0;
    private static final int STACK_FULL = 1;
    private static final int STACK_EMPTY = 2;

    // Constant for when a memory address has no label
    private static final int NO_LABEL = -1;

    // Ten registers created for arithmetic operations
    private int register[] = new int[10];

    // Memory of the program
    private int memory[];

    // Program Counter for keeping track of location in memory
    private int pc = 1;

    // Constants to check for a scanning (reading from console) error
    private static final int SCAN_ERROR_FOUND = -1;
    private static final int NO_SCAN_ERROR_FOUND = 0;

    // Scanner to read from system console
    private Scanner scan = new Scanner(System.in);

    // Star location of the program
    private int start;

    // Hold breakpoints for debugging
    private TreeSet<Integer> breakPoints = new TreeSet<Integer>();

    // Symbols and corresponding addresses from the sym file
    private String symbols[];
    private int addresses[];

    // Language object to access the languages instruction set for debugging
    private Language lan = new Language();

    // boolean to determine whether system console or IDE console is in use
    private boolean console = false;

    // Custom console in IDE
    private Console con;

    // Debugging panel and boolean to note whether a debug session is active
    private DebuggingPanel dp;
    private boolean debugging = false;

    /**
     * Create a virtual machine
     *
     * @param memorySize Size of the memory
     * @param dp debugging panel from IDE
     */
    public VirtualMachine(int memorySize, DebuggingPanel dp) {
        memory = new int[memorySize];
        this.dp = dp;
        dp.setComponents(this, register, pc, stackRegister, compareRegister);
    }

    /**
     * Add a custom console
     *
     * @param c the console
     */
    public void setConsole(Console c) {
        con = c;
        console = true;
    }

    /**
     * Run an assembled bytecode file
     *
     * @param filePath path to bytecode file
     * @param toDebug whether or not to start a debug session
     */
    public void runBytecodeFile(String filePath, boolean toDebug) {
        try {
            try {
                readBin(filePath);
                readSym(filePath);
            } catch (IOException ex) {
                print("Error : " + ex.toString());
                return;
            }
            pc = start;
            if (!toDebug) {
                run();
                println("RUN COMPLETE");
            } else {
                debug();
                println("DEBUG COMPLETE");
            }
        } catch (Exception e) {
            print("Error : " + e.toString());

        } catch (Error e) {
            print("Error : " + e.getMessage());
        }

    }

    // reads bin file and returns true if error is found;
    private boolean readBin(String filePath) throws FileNotFoundException, IOException {
        try {
            BufferedReader dataIn = new BufferedReader(new FileReader(filePath + ".bin"));

            String next = dataIn.readLine();
            int length = Integer.parseInt(next);
            next = dataIn.readLine();
            start = Integer.parseInt(next);
            for (int i = 0; i < memory.length; i++) {
                next = dataIn.readLine();
                if (next == null) {
                    break;
                }
                memory[i] = Integer.parseInt(next);
            }
            dataIn.close();
        } catch (IOException ex) {
            print("File error: " + ex.toString());
            return true;
        }
        return false;

    }

    // reads sym file and returns true if an error is found
    private boolean readSym(String filePath) throws FileNotFoundException {
        Scanner fileScan;
        int numSymbols = 0;
        try {
            fileScan = new Scanner(new FileReader(filePath + ".sym"));
            while (fileScan.hasNextLine()) {
                fileScan.nextLine();
                numSymbols++;
            }
            fileScan.close();
            symbols = new String[numSymbols];
            addresses = new int[numSymbols];
            fileScan = new Scanner(new FileReader(filePath + ".sym"));
            for (int i = 0; i < symbols.length; i++) {
                symbols[i] = fileScan.next();
                addresses[i] = fileScan.nextInt();
            }
            fileScan.close();
        } catch (IOException ex) {
            print(filePath + ".sym not found");
            return true;
        }
        return false;
    }

    // Start running the program
    private void run() {
        int code = memory[pc];
        while (code != 0) {
            nextCode(code);
            code = memory[++pc];
        }
    }

    // Executes the next code in memory
    // This is where all the action happens
    private void nextCode(int code) {
        int r1;
        int r2;
        int r3;
        int label;
        char c;
        String s;
        int constant;
        int length;

        // Each case corresponds to a different instruction
        switch (code) {
            case 0:
                break;
            case 1:
                // 1: Clears the contents of r1
                r1 = checkRegister(memory[++pc]);
                register[r1] = 0;
                break;
            case 2:
                // 2: Clears the memory location specified by r1
                r1 = checkRegister(memory[++pc]);
                int memLoc = register[r1];
                checkMemLocation(memLoc);
                memory[memLoc] = 0;
                break;
            case 3:
                // 3: Clears the memory location specified by label
                label = checkMemLocation(memory[++pc]);
                memory[label] = 0;
                break;
            case 4:
                // 4: Clear a block of memory. The starting location is specified by r1, the count is in r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                checkMemLocation(register[1] + register[r2] - 1);
                for (int i = register[r1]; i < register[r2]; i++) {
                    memory[i] = 0;
                }
                break;
            case 5:
                // 5: Move the specified constant to register r1
                constant = memory[++pc];
                r1 = checkRegister(memory[++pc]);
                register[r1] = constant;
                break;
            case 6:
                // 6: Move the contents of register r1 to r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                register[r2] = register[r1];
                break;
            case 7:
                // 7: Move the contents of register r1 to the memory location specified by label;
                r1 = checkRegister(memory[++pc]);
                label = checkMemLocation(memory[++pc]);
                memory[label] = register[r1];
                break;
            case 8:
                // 8: Move the contents of memory location label to register r1
                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                register[r1] = memory[label];
                break;
            case 9:
                // 9: Move the memory location specified by r1 to r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                register[r2] = memory[register[r1]];
                break;
            case 10:
                // 10: Move the address of label to register r1

                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                register[r1] = label;
                break;
            case 11:
                // 11: Move a block of memory. The source is specified by r1, the destination by r2, the count is in r3
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                r3 = checkRegister(memory[++pc]);
                // check mem locations of first and last elements to be copied
                checkMemLocation(register[r1]);
                checkMemLocation(register[1] + register[r3] - 1);
                int toMove[] = new int[register[r3]];
                int movIndex = 0;
                for (int i = register[r1]; i < toMove.length; i++) {
                    toMove[movIndex] = memory[i];
                    movIndex++;
                }
                movIndex = 0;
                for (int i = register[r2]; i < toMove.length; i++) {
                    memory[i] = toMove[movIndex];
                    movIndex++;
                }
                break;
            case 12:
                // 12: add constant 123 to r1
                constant = memory[++pc];
                r1 = checkRegister(memory[++pc]);
                register[r1] += constant;

                break;
            case 13:
                // 13: Add the contents of register r1 to r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                register[r2] += register[r1];
                break;
            case 14:
                // 14: Add the contents of memory location label to r1
                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                register[r1] += memory[label];
                break;
            case 15:
                // 15: Add the contents of the memory location specified by r1 to r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                register[r2] += memory[register[r1]];
                break;
            case 16:
                // 16: Subtract the constant 123 from r1;
                constant = memory[++pc];
                r1 = checkRegister(memory[++pc]);
                register[r1] -= constant;
                break;
            case 17:
                // 17: Subtract the contents of register r1 from r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                register[r2] -= register[r1];
                break;
            case 18:
                // 18: Subtract the contents of memory location label from r1
                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                register[r1] -= memory[label];
                break;
            case 19:
                // 19: Subtract the contents of the memory location specified by r1 from r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                register[r2] -= memory[register[r1]];
                break;
            case 20:
                // 20: Multiply the constant 123 into r1;
                constant = memory[++pc];
                r1 = checkRegister(memory[++pc]);
                register[r1] *= constant;
                break;
            case 21:
                // 21: Multiply the contents of register r1 into r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                register[r2] *= register[r1];
                break;
            case 22:
                // 22: Multiply the contents of memory location label into r1
                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                register[r1] *= memory[label];
                break;
            case 23:
                // 23: Multiply the contents of the memory location specified by r1 into r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                register[r2] *= memory[register[r1]];
                break;
            case 24:
                // 24: Divide the constant 123 into r1;
                constant = memory[++pc];
                r1 = checkRegister(memory[++pc]);
                register[r1] /= constant;
                break;
            case 25:
                // 25: Divide the contents of register r1 into r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                register[r2] /= register[r1];
                break;
            case 26:
                // 26: Divide the contents of memory location label into r1
                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                register[r1] /= memory[label];
                break;
            case 27:
                // 27: Divide the contents of the memory location specified by r1 into r2

                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                register[r2] /= memory[register[r1]];
                break;
            case 28:
                // 28: Jump to memory location label
                label = checkMemLocation(memory[++pc]);
                // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                pc = label - 1;
                break;
            case 29:
                // 29: Subtract one from register r1. Jump to label if result is 0
                r1 = checkRegister(memory[++pc]);
                register[r1]--;
                if (register[r1] == 0) {
                    label = checkMemLocation(memory[++pc]);
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
            case 30:
                // 30: Subtract one from register r1. Jump to label if result is not equal 0
                r1 = checkRegister(memory[++pc]);
                register[r1]--;
                if (register[r1] != 0) {
                    label = checkMemLocation(memory[++pc]);
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
            case 31:
                // 31: Add one to register r1. Jump to label if result is 0
                r1 = checkRegister(memory[++pc]);
                register[r1]++;
                if (register[r1] == 0) {
                    label = checkMemLocation(memory[++pc]);
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
            case 32:
                // 32: Add one to register r1. Jump to label if result is not equal 0
                r1 = checkRegister(memory[++pc]);
                register[r1]++;
                if (register[r1] != 0) {
                    label = checkMemLocation(memory[++pc]);
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
            case 33:
                // 33: Compares constant to r1. Set status flags
                constant = memory[++pc];
                r1 = checkRegister(memory[++pc]);
                compareRegister = constant - register[r1];
                break;
            case 34:
                // 34: Compares r1 to r2. Set status flags
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                compareRegister = register[r1] - register[r2];
                break;
            case 35:
                // 35: Compares memory location label to r1. Set status flags
                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                compareRegister = memory[label] - register[r1];
                break;
            case 36:
                // 36: Jump to label if last compare was negative
                label = checkMemLocation(memory[++pc]);
                if (compareRegister < 0) {
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
            case 37:
                // 37: Jump to label if last compare was zero
                label = checkMemLocation(memory[++pc]);
                if (compareRegister == 0) {
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
            case 38:
                // 38: Jump to label if last compare was positive
                label = checkMemLocation(memory[++pc]);
                if (compareRegister > 0) {
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
            case 39:
                // 39: Jump to subroutine label. The contents of registers r5 - r9 are pushed onto the stack
                label = checkMemLocation(memory[++pc]);
                for (int i = 5; i <= 9; i++) {
                    stack.push(register[i]);
                }
                stack.push(pc);
                // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                pc = label - 1;
                break;
            case 40:
                // 40: return from subroutine. The contents of registers r5 - r9 are popped from the stack
                pc = stack.pop();
                for (int i = 9; i >= 5; i--) {
                    register[i] = stack.pop();
                }
                break;
            case 41:
                // 41: Push r1 onto the stack
                r1 = checkRegister(memory[++pc]);
                stack.push(register[r1]);
                break;
            case 42:
                // 42: Pop the stack onto r1
                r1 = checkRegister(memory[++pc]);
                register[r1] = stack.pop();
                break;
            case 43:
                // 43: Puts stack condition into register r1. 0 - Okay, 1 - Full, 2 - Empty
                if (stack.isEmpty()) {
                    stackRegister = STACK_EMPTY;
                } else if (stack.isFull()) {
                    stackRegister = STACK_FULL;
                } else {
                    stackRegister = STACK_OK;
                }
                r1 = checkRegister(memory[++pc]);
                register[r1] = stackRegister;
                break;
            case 44:
                // 44: Output the character 123 to the console
                c = (char) memory[++pc];
                print("" + c);
                break;
            case 45:
                // 45: Output the character in r1 to the console
                r1 = checkRegister(memory[++pc]);
                c = (char) register[r1];
                print("" + c);
                break;
            case 46:
                // 46: Output the character whose memory location is indicated by r1 to the console
                r1 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                c = (char) memory[register[r1]];
                print("" + c);
                break;
            case 47:
                // 47: Output a block of characters. The starts is indicated by r1, the count by r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                checkMemLocation(register[r1] + register[r2] - 1);
                length = register[r2];
                s = "";
                for (int i = register[r1]; i < length; i++) {
                    s += (char) memory[i];
                }
                print(s);
                break;
            case 48:
                // 48: read an integer from the console to r1, error code in r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                try {
                    register[r1] = nextInt();
                    register[r2] = NO_SCAN_ERROR_FOUND;
                } catch (InputMismatchException e) {
                    register[r2] = SCAN_ERROR_FOUND;
                }
                break;
            case 49:
                // 49: Print the integer in r1 to the console
                r1 = checkRegister(memory[++pc]);
                print("" + register[r1]);
                break;
            case 50:
                // 50: Read a character from console to r1;
                r1 = checkRegister(memory[++pc]);
                register[r1] = next().charAt(0);
                break;
            case 51:
                // 51: Read a line of text to the memory location starting at label. The count is placed in r1
                label = checkMemLocation(memory[++pc]);
                r1 = checkRegister(memory[++pc]);
                s = nextLine();
                checkMemLocation(label + s.length() - 1);
                for (int i = 0; i < s.length(); i++) {
                    memory[label] = s.charAt(i);
                    label++;
                }
                register[r1] = s.length();
                break;
            case 52:
                // 52: Break into debugger
                debug();
                break;
            case 53:
                // 53: Move the contents of register r1 into the memory location specified by r2
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r2]);
                memory[register[r2]] = register[r1];
                break;
            case 54:
                // 54: Move the contents of the memory location specified by r1
                r1 = checkRegister(memory[++pc]);
                r2 = checkRegister(memory[++pc]);
                checkMemLocation(register[r1]);
                checkMemLocation(register[r2]);
                memory[register[r2]] = memory[register[r1]];
                break;
            case 55:
                // 55: Output a string stored in label
                label = checkMemLocation(memory[++pc]);
                length = memory[label];
                s = "";
                for (int i = 1; i < length + 1; i++) {
                    s += (char) memory[label + i];
                }
                print(s);
                break;
            case 56:

                // nop: No operation. Does nothing
                break;
            case 57:
                // 57: jump if compare was not equal
                label = checkMemLocation(memory[++pc]);
                if (compareRegister != 0) {
                    // subtracts 1 because before every instruction is executed, the pc is automatically incremented
                    pc = label - 1;
                }
                break;
        }
    }

    // Start visual debugging
    private void debug() {
        debugging = true;
        while (debugging) {
            Thread.yield();
        }
        dp.refresh(pc, stackRegister, compareRegister);
    }

    // Start console based debugging
    private void debugInConsole() {
        String command = "";
        do {
            try {
                command = getCommandWithPrompt();
                doDebugCommand(command);
            } catch (Error e) {
                println(e.getMessage());
            }
        } while (!command.equals("exit"));
    }

    // Execute a given debug command
    // For use in console debugging
    private void doDebugCommand(String command) {
        Scanner comScan = new Scanner(command);
        int loc1 = 0;
        int loc2 = 0;
        int r = 0;
        int value = 0;
        String s;

        // Get the debugging command and execute it
        try {
            String com = comScan.next();
            if ("exit".equals(com)) {
                return;
            }
            // Run the program
            if ("go".equals(com)) {
                if (comScan.hasNext()) {
                    s = comScan.next();
                    loc1 = getAddress(s);
                    if (loc1 == NO_LABEL) {
                        loc1 = Integer.parseInt(s);
                    }
                    go(loc1);
                } else {
                    go();
                }
                return;
            }

            // Step: execute one instruction
            if ("step".equals(com)) {
                step();
                return;
            }

            // Dump memory at given location
            if ("dump".equals(com)) {
                s = comScan.next();
                loc1 = getAddress(s);
                if (loc1 == NO_LABEL) {
                    loc1 = Integer.parseInt(s);
                }
                s = comScan.next();
                loc2 = getAddress(s);
                if (loc2 == NO_LABEL) {
                    loc2 = Integer.parseInt(s);
                }
                dump(loc1, loc2);
                return;
            }

            // Dump the registers at a given location
            if ("dumpr".equals(com)) {
                dumpr();
                return;
            }

            // Deasemble memory from given locations
            if ("deas".equals(com)) {
                s = comScan.next();
                loc1 = getAddress(s);
                if (loc1 == NO_LABEL) {
                    loc1 = Integer.parseInt(s);
                }
                s = comScan.next();
                loc2 = getAddress(s);
                if (loc2 == NO_LABEL) {
                    loc2 = Integer.parseInt(s);
                }
                deas(loc1, loc2);
                return;
            }

            // Print the breakpoint table
            if ("brkt".equals(com)) {
                brkt();
                return;
            }

            // Set a breakpoint
            if ("sbrk".equals(com)) {
                s = comScan.next();
                loc1 = getAddress(s);
                if (loc1 == NO_LABEL) {
                    loc1 = Integer.parseInt(s);
                }
                sbrk(loc1);
                return;
            }

            // Clear a breakpoint
            if ("cbrk".equals(com)) {
                s = comScan.next();
                loc1 = getAddress(s);
                if (loc1 == NO_LABEL) {
                    loc1 = Integer.parseInt(s);
                }
                cbrk(loc1);
                return;
            }

            // Clear the breakpoint table
            if ("cbrkt".equals(com)) {
                cbrkt();
                return;
            }

            // Print out the help menu
            if ("help".equals(com)) {
                help();
                return;
            }

            // Change a given register to a given value
            if ("chngr".equals(com)) {
                r = Integer.parseInt(comScan.next());
                value = Integer.parseInt(comScan.next());
                chngr(r, value);
                return;
            }

            // Change a given location in memory to a given value
            if ("chngm".equals(com)) {
                s = comScan.next();
                loc1 = getAddress(s);
                if (loc1 == NO_LABEL) {
                    loc1 = Integer.parseInt(s);
                }
                value = Integer.parseInt(comScan.next());
                chngm(loc1, value);
                return;
            }
            println("Invalid debbugger input");

        } catch (NoSuchElementException e) {

            // Occurs when user calls a command but doent give the correct locations or value
            println("Invalid debbugger input");
        } catch (NumberFormatException e) {

            // Occurs when user call a command but provide bad input
            // Such a giving a string when asking for an int
            println("Number format error");
        }
    }

    // get the prompt string with pc for console debugging
    private String getCommandWithPrompt() {
        print("dbg<" + pc + getLabel(pc) + ">");
        return nextLine();
    }

    // Gets the label of an address
    private String getLabel(int address) {
        int key = Arrays.binarySearch(addresses, address);
        if (key >= 0) {
            return "(" + symbols[key] + ")";
        }
        return "";
    }

    // Gets the label of an given address for deassembly
    private String getDeasLabel(int address) {
        int key = Arrays.binarySearch(addresses, address);
        if (key >= 0) {
            return symbols[key] + ": ";
        }
        return "";
    }

    // Returns the address of a given label
    private int getAddress(String label) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i].equals(label)) {
                return addresses[i];
            }
        }
        return NO_LABEL;
    }

    /**
     * Stop debugging program
     */
    public void exit() {
        debugging = false;
    }

    /**
     * Begin execution at Start or resume execution from current pc location
     */
    public void go() {
        try {
            go(pc);
            dp.refresh(pc, stackRegister, compareRegister);
        } catch (Exception e) {
            con.cPrintln("Error : " + e.toString());
        } catch (Error e) {
            con.cPrintln("Error : " + e.getMessage());
        }
    }

    /**
     * Begin execution at loc1
     *
     * @param loc1 mem location to begin execution
     */
    protected void go(int loc1) {
        checkMemLocation(loc1);
        pc = loc1;
        int code = memory[pc];
        do {
            nextCode(code);
            code = memory[++pc];
            if (code == 0) {
                debugging = false;
            }
        } while (code != 0 && !breakPoints.contains(pc));
    }

    /**
     * Get the current location of the program counter
     *
     * @return Current program count
     */
    public int getPC() {
        return pc;
    }

    /**
     * Sets the program counter
     *
     * @param loc - new location in memory for program to run from
     */
    public void setPC(int loc) {
        pc = loc;
    }

    /**
     *
     * @return The value of the stack register
     */
    public int getStack() {
        return stackRegister;
    }

    /**
     * sets the value of the stack register
     *
     * @param loc - new value for stack register
     */
    public void setStack(int loc) {
        stackRegister = loc;
    }

    /**
     *
     * @return The value of the compare register
     */
    public int getCmp() {
        return compareRegister;
    }

    /**
     * sets the compare register to loc
     *
     * @param loc - new value for compare register
     */
    public void setCmp(int loc) {
        compareRegister = loc;
    }

    /**
     * Step forward in debugging the program
     */
    public void step() {
        try {
            int code = memory[pc];
            nextCode(code);
            pc++;
            dp.refresh(pc, stackRegister, compareRegister);
        } catch (Exception e) {
            con.cPrintln("Error : " + e.toString());

        } catch (Error e) {
            con.cPrintln("Error : " + e.getMessage());
        }
    }

    /**
     * Gets the contents of memory from loc1 to loc2
     *
     * @param loc1 First location in memory
     * @param loc2 Second location in memory
     * @return
     */
    public String getMem(int loc1, int loc2) {
        String mem = "";
        try {

            // Make sure given locs are valid
            checkMemLocation(loc1);
            checkMemLocation(loc2);
            int i;

            // collect each memory location in a string
            for (i = loc1; i < loc2 + 1; i++) {
                mem += i + ": " + memory[i] + " " + getLabel(memory[i]) + "\n";
            }
        } catch (Exception e) {
            con.cPrintln("Error : " + e.toString());
            return "Bad Locations";

        } catch (Error e) {
            con.cPrintln("Error : " + e.getMessage());
            return "Bad Locations";
        }
        return mem;
    }

    /**
     * Dump (prints) memory locations from (including) loc1 - loc2
     *
     * @param loc1 first location
     * @param loc2 last location
     */
    protected void dump(int loc1, int loc2) {
        try {
            // Check given locs are valid
            checkMemLocation(loc1);
            checkMemLocation(loc2);
            int i;

            // Print each memory location
            for (i = loc1; i < loc2 - 1; i++) {
                println(memory[i] + ", ");
            }
            println("" + memory[++i]);
        } catch (Exception e) {
            con.cPrintln("Error : " + e.toString());

        } catch (Error e) {
            con.cPrintln("Error : " + e.getMessage());
        }
    }

    /**
     * Dump a table of registers
     */
    protected void dumpr() {
        println(Arrays.toString(register));
        println("Compare register: " + compareRegister);
        println("StackRegister: " + stackRegister);
    }

    /**
     * Deassemble memory locations from (including) loc1 - loc2
     *
     * @param loc1 first location
     * @param loc2 last location
     * @return Deassembled memory as string
     */
    public String deas(int loc1, int loc2) {
        String deas = "";
        try {

            // Check validity of given locs
            checkMemLocation(loc1);
            checkMemLocation(loc2);

            // Deassembly counter
            int deaspc;

            // go through each command and reverse engineer the code from the language obj
            for (deaspc = loc1; deaspc < loc2 - 1; deaspc++) {
                deas += getDeasLabel(deaspc);

                // If an instruction is found
                if (!lan.getInstruction(memory[deaspc]).equals(Language.INSTRUCTION_NOT_FOUND)) {
                    // get the instruction
                    deas += lan.getInstruction(memory[deaspc]) + " ";

                    // get the param types
                    ParamType params[] = lan.getParams(memory[deaspc]);

                    // if params are needed
                    if (params != null) {

                        // add the params to the text
                        for (int i = 0; i < params.length; i++) {
                            ParamType param = params[i];
                            switch (param) {
                                case REGISTER_PARAM:
                                    deas += "r" + memory[++deaspc] + " ";
                                    break;
                                case LABEL_PARAM:
                                    deas += memory[++deaspc] + getLabel(memory[deaspc]) + " ";
                                    break;
                                case IMMEDIATE_PARAM:
                                    deas += "#" + memory[++deaspc] + " ";
                                    break;
                            }
                        }
                        params = lan.getParams(memory[deaspc]);
                    }
                }
                deas += "\n";
            }
            //println(deas);
        } catch (Exception e) {
            con.cPrintln("Error : " + e.toString());

        } catch (Error e) {
            con.cPrintln("Error : " + e.getMessage());
        }
        return deas;
    }

    /**
     * List the current break point table
     */
    protected void brkt() {
        println(breakPoints.toString());
    }

    /**
     * Set break point at loc1
     *
     * @param loc1 location to set breakpoint
     */
    public void sbrk(int loc1) {
        try {
            checkMemLocation(loc1);
            breakPoints.add(loc1);
        } catch (Error e) {
        }
    }

    /**
     * Clear break point at loc1
     *
     * @param loc1 location to remove from breakpoint
     */
    public void cbrk(int loc1) {
        try {
            checkMemLocation(loc1);
            breakPoints.remove(loc1);
        } catch (Error e) {
        }
    }

    /**
     * Clear breakpoint table
     */
    public void cbrkt() {
        breakPoints.clear();
    }

    /**
     *
     * @return A string contating all the breakpoint currently set
     */
    public String getBRKT() {
        String brkt = "";
        Iterator it = breakPoints.iterator();
        while (it.hasNext()) {
            int i = (Integer) it.next();
            brkt += i + " " + getLabel(i) + "\n";
        }
        return brkt;
    }

    /**
     * Print a summary of debugger commands
     */
    protected void help() {
        println("go -  Begin execution at Start or resume execution from current pc location");
        println("go <loc1> - Begin execution at <loc1>");
        println("step - Execute next step");
        println("dump <loc1> <loc2> - Dump memory locations from (including) <loc1> - <loc2>");
        println("dumpr - Dump a table of registers");
        println("exit - Exit debugger");
        println("deas <loc1> <loc2> - Deassmble memory locations from (including) <loc1> - <loc2>");
        println("brkt - List the current break point table");
        println("sbrk <loc1> - Set break point at <loc1>");
        println("cbrk <loc1> - Clear break point at <loc1>");
        println("cbrkt - Clear breakpoint table");
        println("help - Print a summary of debugger commands");
        println("chngr <r#> <value> - Change the value of register <r#> to <value>");
        println("chngm <loc1 <value> - Change the value of memory loaction <loc1> to <value>");
    }

    /**
     * Change the value of register r# to value
     *
     * @param r register to change
     * @param value value to change to
     */
    protected void chngr(int r, int value) {
        checkRegister(r);
        register[r] = value;
    }

    /**
     * Change the value of memory location loc1 to value
     *
     * @param loc1 mem location to change
     * @param value value to change to
     */
    public void chngm(int loc1, int value) {
        checkMemLocation(loc1);
        memory[loc1] = value;
    }

    /*
     * Check if register is between 0 and 9 inclusive. Throws ResterOutOfBounds
     * error if it is not.
     *
     */
    private int checkRegister(int register) {
        if (register <= 9 && register >= 0) {
            return register;
        }
        throw new RegisterOutOfBounds(register);
    }

    /*
     * Checks if given memory location is inbounds. Throws MemoryOutOfBounds
     * error if it is not.
     *
     */
    private int checkMemLocation(int location) {
        if (location < memory.length && location >= 0) {
            return location;
        }
        throw new MemoryOutOfBounds(memory.length, location);
    }

    // Determine correct console to print to and then print
    private void print(String s) {
        if (console) {
            con.cPrint(s);
            return;
        }
        System.out.print(s);

    }

    // Determine correct console to print to and the println
    private void println(String s) {
        if (console) {
            con.cPrintln(s);
            return;
        }
        System.out.println(s);

    }

    // Get the next typed word from the console in use
    private String next() {
        if (console) {
            return con.next();
        }
        return scan.next();
    }

    // Get the next typed line from the console in use
    private String nextLine() {
        if (console) {
            return con.nextLine();
        }
        return scan.nextLine();

    }

    // Get the next typed integer from the console in use
    private int nextInt() {
        if (console) {
            return con.nextInt();
        }
        return scan.nextInt();
    }
}
