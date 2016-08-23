package assembler;

import SillyBeans.Console;
import assemblererrors.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 *
 * @author Daniel Nissenbaum
 */
public class Assembler {

    private boolean firstPass = true;
    private ArrayList memory = new ArrayList(2);
    private LinkedHashMap<String, Integer> symbols = new LinkedHashMap<String, Integer>();
    private Scanner scan;
    private String fileName;
    private int lineCount = 1;
    private boolean endReached = false;
    private int pointer = 0;
    private int errorCount = 0;
    private Language lan = new Language();
    private String toLst = "";
    private String nextToLst = "";
    private Tokenizer tkn = new Tokenizer(symbols,true);
    private boolean console = false;
    Console con;

    public Assembler() {
        memory.add(0);
        memory.add(0);
    }

    public void setConsole(Console c) {
        con = c;
        console = true;
    }

    private void reset() {
        memory.clear();
        memory.add(0);
        memory.add(0);
        symbols.clear();
        lineCount = 1;
        pointer = 0;
        errorCount = 0;
        toLst = "";
        nextToLst = "";
        endReached = false;
        firstPass = true;
    }

    public void assembleFile(String fileName) {
        reset();
        this.fileName = fileName;
        try {
            scan = new Scanner(new File(fileName + ".txt"));

        } catch (IOException ex) {
            println(fileName + ".txt was not found.");
            return;
        }
        while (scan.hasNextLine() && !endReached) {
            try {
                String line = scan.nextLine();
                tkn.setLine(line, lineCount);
                handleLine();
            } catch (Error e) {
                errorCount++;
            }
            lineCount++;
        }
        firstPass = false;
        pointer = 0;
        lineCount = 1;
        endReached = false;
        try {
            scan = new Scanner(new File(fileName + ".txt"));
        } catch (IOException ex) {
            println(fileName + ".txt was not found.");
            return;
        }
        while (scan.hasNextLine() && !endReached) {
            String line = "";
            nextToLst = "";
            nextToLst += lineCount + " " + (pointer) + ":  ";
            try {
                line = scan.nextLine();
                tkn.setLine(line, lineCount);
                handleLine();
            } catch (Error e) {
                println(e.getMessage());
                errorCount++;
            }
            lineCount++;
            if (nextToLst.length() > 35) {
                nextToLst = nextToLst.substring(0, 35);
            } else {
                for (int i = nextToLst.length(); i < 35; i++) {
                    nextToLst += " ";
                }
            }
            nextToLst += " " + line.trim();
            toLst += nextToLst + "\n";
        }
        toLst += "\n\n" + "Symbols  Table\n" + getSymbolTable();
        toLst += "Number of Assembly Errors: " + errorCount;
        if (errorCount == 0) {
        }
        writebin();
        writelst();
        writesym();
        println("Number of Assembly Errors: " + errorCount);
        println("FILE ASSEMBLY COMPLETE");
    }

    public void handleLine() {
        TokenType t = tkn.getTokenType();
        String next = tkn.getNextToken();
        while (t != TokenType.END_LINE) {
            switch (t) {
                case COMMENT:
                    break;
                case START:
                    if (!firstPass) {
                        int loc = tkn.getNextLocation();
                        memory.set(1, loc);
                    } else {
                        tkn.getNextToken();
                    }
                    break;
                case END:
                    endReached = true;
                    memory.set(0, pointer);
                    return;
                case STRING:
                    String s = tkn.getNextToken();
                    char c[] = s.toCharArray();
                    writeToMemory(c.length);
                    for (int i = 0; i < c.length; i++) {
                        writeToMemory((int) c[i]);

                    }
                    break;
                case INTEGER:
                    writeToMemory(tkn.getNextImmediate());
                    break;
                case ALLOCATE:
                    int imm = tkn.getNextImmediate();
                    for (int i = 0; i < imm; i++) {
                        writeToMemory(0);
                    }
                    break;
                case INSTRUCTION:
                    int code = lan.getInstructionCode(next);
                    writeToMemory(code);
                    ParamType prms[] = lan.getParams(code);
                    if (firstPass) {
                        for (int i = 0; i < prms.length; i++) {
                            writeToMemory(-1);
                        }
                        return;
                    }
                    for (int i = 0; i < prms.length; i++) {
                        if (prms[i] == ParamType.REGISTER_PARAM) {
                            writeToMemory(tkn.getNextRegister());
                        }
                        if (prms[i] == ParamType.IMMEDIATE_PARAM) {
                            writeToMemory(tkn.getNextImmediate());
                        }
                        if (prms[i] == ParamType.LABEL_PARAM) {
                            writeToMemory(tkn.getNextLocation());
                        }
                    }

                    break;
                case SYMBOL:
                    if (firstPass == true) {
                        if (symbols.containsKey(next)) {
                            throw new DuplicateSymbolError(lineCount, next);
                        } else {
                            symbols.put(next, pointer);
                        }
                    }
                    break;
                default:
                    throw new UnrecognizedInstructionError(lineCount, next);
            }
            t = tkn.getTokenType();
            next = tkn.getNextToken();
        }
    }

    private void writeToMemory(int i) {
        if (!firstPass) {
            memory.add(i);
            //println(i);
            nextToLst += i + " ";
        }
        pointer++;
    }

    private String getSymbolTable() {
        String s = "";
        Iterator<String> it = symbols.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            int loc = symbols.get(key);
            s += loc;
            for (int j = 0; j < 8 - (String.valueOf(loc).length()); j++) {
                s += " ";
            }
            s += key + "\n";
        }
        return s;

    }

    private void writebin() {
        try {
            File f = new File(fileName + ".bin");
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            for (int i = 0; i < memory.size(); i++) {
                out.write(memory.get(i).toString() + "\n");
            }
            out.close();
        } catch (IOException ex) {
            println("Error writing .bin file");

        }
    }

    private void writelst() {
        File f = new File(fileName + ".lst");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            out.write(toLst);
            out.close();
        } catch (IOException ex) {
            println("Error writing .bin file");
        }
    }

    private void writesym() {
        File f = new File(fileName + ".sym");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            Iterator<String> it = symbols.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                out.write(key + " " + symbols.get(key) + "\n");
            }
            out.close();
        } catch (IOException ex) {
            println("Error writing .bin file");
        }
    }

    private void print(String s) {
        if (console) {
            con.cPrint(s);
            return;
        }
        System.out.print(s);

    }

    private void println(String s) {
        if (console) {
            con.cPrintln(s);
            return;
        }
        System.out.println(s);

    }
}
