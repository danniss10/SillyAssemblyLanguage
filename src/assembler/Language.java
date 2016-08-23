package assembler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Language class contains all of the instructions, code, and parameters of
 * the assembly language along with methods to access information about the
 * assembly language
 *
 * @author Daniel Nissenbaum
 */
public class Language {

    private HashMap instructions = new HashMap(58);
    private HashMap codes = new HashMap(58);
    private HashMap params = new HashMap(58);
    private ArrayList directives = new ArrayList(5);
    public static final int INVALID_INSTRUCTION = -1;
    public static final String INSTRUCTION_NOT_FOUND = "instruction not found";

    /**
     * constructor of the Language class. Sets up the instructions and params
     * maps
     */
    public Language() {
        setInstructions();
        setCodes();
        setParams();
        setDirectives();
    }

    /*
     * Sets the insructions HashMap with the instructions as the key and the corresponding
     * code as the value
     */
    private void setInstructions() {
        instructions.put("halt", 0);
        instructions.put("clrr", 1);
        instructions.put("clrx", 2);
        instructions.put("clrm", 3);
        instructions.put("clrb", 4);
        instructions.put("movir", 5);
        instructions.put("movrr", 6);
        instructions.put("movrm", 7);
        instructions.put("movmr", 8);
        instructions.put("movxr", 9);
        instructions.put("movar", 10);
        instructions.put("movb", 11);
        instructions.put("addir", 12);
        instructions.put("addrr", 13);
        instructions.put("addmr", 14);
        instructions.put("addxr", 15);
        instructions.put("subir", 16);
        instructions.put("subrr", 17);
        instructions.put("submr", 18);
        instructions.put("subxr", 19);
        instructions.put("mulir", 20);
        instructions.put("mulrr", 21);
        instructions.put("mulmr", 22);
        instructions.put("mulxr", 23);
        instructions.put("divir", 24);
        instructions.put("divrr", 25);
        instructions.put("divmr", 26);
        instructions.put("divxr", 27);
        instructions.put("jmp", 28);
        instructions.put("sojz", 29);
        instructions.put("sojnz", 30);
        instructions.put("aojz", 31);
        instructions.put("aojnz", 32);
        instructions.put("cmpir", 33);
        instructions.put("cmprr", 34);
        instructions.put("cmpmr", 35);
        instructions.put("jmpn", 36);
        instructions.put("jmpz", 37);
        instructions.put("jmpp", 38);
        instructions.put("jsr", 39);
        instructions.put("ret", 40);
        instructions.put("push", 41);
        instructions.put("pop", 42);
        instructions.put("stackc", 43);
        instructions.put("outci", 44);
        instructions.put("outcr", 45);
        instructions.put("outcx", 46);
        instructions.put("outcb", 47);
        instructions.put("readi", 48);
        instructions.put("printi", 49);
        instructions.put("readc", 50);
        instructions.put("readln", 51);
        instructions.put("brk", 52);
        instructions.put("movrx", 53);
        instructions.put("movxx", 54);
        instructions.put("outs", 55);
        instructions.put("nop", 56);
        instructions.put("jmpne", 57);

    }

    // sets codes map with the inverse of instructions
    private void setCodes() {
        codes.put(0, "halt");
        codes.put(1, "clrr");
        codes.put(2, "clrx");
        codes.put(3, "clrm");
        codes.put(4, "clrb");
        codes.put(5, "movir");
        codes.put(6, "movrr");
        codes.put(7, "movrm");
        codes.put(8, "movmr");
        codes.put(9, "movxr");
        codes.put(10, "movar");
        codes.put(11, "movb");
        codes.put(12, "addir");
        codes.put(13, "addrr");
        codes.put(14, "addmr");
        codes.put(15, "addxr");
        codes.put(16, "subir");
        codes.put(17, "subrr");
        codes.put(18, "submr");
        codes.put(19, "subxr");
        codes.put(20, "mulir");
        codes.put(21, "mulrr");
        codes.put(22, "mulmr");
        codes.put(23, "mulxr");
        codes.put(24, "divir");
        codes.put(25, "divrr");
        codes.put(26, "divmr");
        codes.put(27, "divxr");
        codes.put(28, "jmp");
        codes.put(29, "sojz");
        codes.put(30, "sojnz");
        codes.put(31, "aojz");
        codes.put(32, "aojnz");
        codes.put(33, "cmpir");
        codes.put(34, "cmprr");
        codes.put(35, "cmpmr");
        codes.put(36, "jmpn");
        codes.put(37, "jmpz");
        codes.put(38, "jmpp");
        codes.put(39, "jsr");
        codes.put(40, "ret");
        codes.put(41, "push");
        codes.put(42, "pop");
        codes.put(43, "stackc");
        codes.put(44, "outci");
        codes.put(45, "outcr");
        codes.put(46, "outcx");
        codes.put(47, "outcb");
        codes.put(48, "readi");
        codes.put(49, "printi");
        codes.put(50, "readc");
        codes.put(51, "readln");
        codes.put(52, "brk");
        codes.put(53, "movrx");
        codes.put(54, "movxx");
        codes.put(55, "outs");
        codes.put(56, "nop");
        codes.put(57, "jmpne");

    }

    /*
     * Sets the params HashMap with the instructions as the key and ParamType[]
     * containing the parameter types as values
     */
    private void setParams() {
        params.put("halt", new ParamType[]{});
        params.put("clrr", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("clrx", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("clrm", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("clrb", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("movir", new ParamType[]{ParamType.IMMEDIATE_PARAM, ParamType.REGISTER_PARAM});
        params.put("movrr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("movrm", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.LABEL_PARAM});
        params.put("movmr", new ParamType[]{ParamType.LABEL_PARAM, ParamType.REGISTER_PARAM});
        params.put("movxr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("movar", new ParamType[]{ParamType.LABEL_PARAM, ParamType.REGISTER_PARAM});
        params.put("movb", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("addir", new ParamType[]{ParamType.IMMEDIATE_PARAM, ParamType.REGISTER_PARAM});
        params.put("addrr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("addmr", new ParamType[]{ParamType.LABEL_PARAM, ParamType.REGISTER_PARAM});
        params.put("addxr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("subir", new ParamType[]{ParamType.IMMEDIATE_PARAM, ParamType.REGISTER_PARAM});
        params.put("subrr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("submr", new ParamType[]{ParamType.LABEL_PARAM, ParamType.REGISTER_PARAM});
        params.put("subxr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("mulir", new ParamType[]{ParamType.IMMEDIATE_PARAM, ParamType.REGISTER_PARAM});
        params.put("mulrr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("mulmr", new ParamType[]{ParamType.LABEL_PARAM, ParamType.REGISTER_PARAM});
        params.put("mulxr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("divir", new ParamType[]{ParamType.IMMEDIATE_PARAM, ParamType.REGISTER_PARAM});
        params.put("divrr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("divmr", new ParamType[]{ParamType.LABEL_PARAM, ParamType.REGISTER_PARAM});
        params.put("divxr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("jmp", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("sojz", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.LABEL_PARAM});
        params.put("sojnz", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.LABEL_PARAM});
        params.put("aojz", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.LABEL_PARAM});
        params.put("aojnz", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.LABEL_PARAM});
        params.put("cmpir", new ParamType[]{ParamType.IMMEDIATE_PARAM, ParamType.REGISTER_PARAM});
        params.put("cmprr", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("cmpmr", new ParamType[]{ParamType.LABEL_PARAM, ParamType.REGISTER_PARAM});
        params.put("jmpn", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("jmpz", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("jmpp", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("jsr", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("ret", new ParamType[]{});
        params.put("push", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("pop", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("stackc", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("outci", new ParamType[]{ParamType.IMMEDIATE_PARAM});
        params.put("outcr", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("outcx", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("outcb", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("readi", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("printi", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("readc", new ParamType[]{ParamType.REGISTER_PARAM});
        params.put("readln", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("brk", new ParamType[]{});
        params.put("movrx", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("movxx", new ParamType[]{ParamType.REGISTER_PARAM, ParamType.REGISTER_PARAM});
        params.put("outs", new ParamType[]{ParamType.LABEL_PARAM});
        params.put("nop", new ParamType[]{});
        params.put("jmpne", new ParamType[]{ParamType.LABEL_PARAM});

    }

    // Adds directives to ArrayList
    private void setDirectives() {
        directives.add(".start");
        directives.add(".end");
        directives.add(".integer");
        directives.add(".string");
        directives.add(".allocate");
    }

    /**
     * Checks if a given word is a directive
     *
     * @param word - word to check
     * @return true if word is a directive
     */
    public boolean isDirective(String word) {
        return directives.contains(word.toLowerCase());
    }

    /**
     * Method that checks if a string is a valid assembly instruction
     *
     * @param instruction string to check
     * @return true if given string is an instruction
     */
    public boolean isInstruction(String instruction) {
        return instructions.containsKey(instruction.toLowerCase());
    }

    /**
     * Gets the integer code of the a given instruction after checking if the
     * provided string is a valid instruction
     *
     * @param instruction instruction to get code for
     * @return given instructions code or -1 if the provide string is not a
     * valid instruction
     */
    public int getInstructionCode(String instruction) {
        if (isInstruction(instruction)) {
            return (Integer) instructions.get(instruction);
        }
        return INVALID_INSTRUCTION;
    }

    /**
     * Gets an instruction coressponding to a given code
     *
     * @param code code matching instruction
     * @return the found instruction or INSTRUCTION_NOT_FOUND
     */
    public String getInstruction(int code) {
        if (code >= 0 && code < 58) {
            return codes.get(code).toString();
        }
        return INSTRUCTION_NOT_FOUND;
    }

    /**
     * Returns the number of parameters a give instruction takes. Checks first
     * to see if provided string is a valid instruction
     *
     * @param instruction instruction to get number of parameters for
     * @return number of parameters the provided instruction takes or a -1 if
     * the instruction is invalid
     */
    public int numberOfParams(String instruction) {
        if (isInstruction(instruction)) {
            String prms[] = (String[]) params.get(instruction);
            return prms.length;
        }
        return INVALID_INSTRUCTION;
    }

    /**
     * method that gets the parameter types an instruction takes after first
     * checking if the instruction is valid
     *
     * @param instruction instruction to get parameter types for
     * @return parameter types in a ParamType[] or an empty ParamType[]
     */
    public ParamType[] getParams(String instruction) {
        if (isInstruction(instruction)) {
            ParamType prms[] = (ParamType[]) params.get(instruction);
            return prms;
        }
        return new ParamType[0];
    }

    /**
     * method that gets the parameter types an instruction takes after first
     * checking if the instruction is valid
     *
     * @param code code to get parameters for
     * @return parameter types in a ParamType[] or an empty ParamType[]
     */
    public ParamType[] getParams(int code) {
        if (isInstruction(getInstruction(code))) {
            ParamType prms[] = (ParamType[]) params.get(getInstruction(code));
            return prms;
        }
        return new ParamType[0];
    }
}
