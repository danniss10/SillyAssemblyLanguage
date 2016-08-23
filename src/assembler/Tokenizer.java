package assembler;

import assemblererrors.*;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 *
 * @author Daniel Nissenbaum
 */
public class Tokenizer {

    private Scanner scan;
    private String nextToken = "";
    private TokenType type;
    private Language lan = new Language();
    private LinkedHashMap symbols;
    private int lineNum;
    private boolean toThrow = false;

    public Tokenizer(LinkedHashMap sym, boolean toThrow) {
        symbols = sym;
        this.toThrow = toThrow;
    }

    public void setLine(String line, int lineNumber) {
        scan = new Scanner(line);
        lineNum = lineNumber;
        setNextToken();
    }

    private void setNextToken() {
        if (scan.hasNext()) {
            nextToken = scan.next();
            if (nextToken.startsWith(";")) {
                if (scan.hasNextLine()) {
                    nextToken += scan.nextLine();
                }
                type = TokenType.COMMENT;
                return;
            }
            if (nextToken.toLowerCase().equals(".start")) {
                type = TokenType.START;
                return;
            }
            if (nextToken.toLowerCase().equals(".allocate")) {
                type = TokenType.ALLOCATE;
                return;
            }
            if (nextToken.toLowerCase().equals(".integer")) {
                type = TokenType.INTEGER;
                return;
            }
            if (nextToken.toLowerCase().equals(".string")) {
                type = TokenType.STRING;
                return;
            }
            if (nextToken.startsWith("\"")) {
                nextToken += scan.nextLine();
                nextToken = nextToken.trim();
                if (nextToken.matches("\".*\"(| +|;.*| +;.*)")) {
                    nextToken = nextToken.substring(1, nextToken.length() - 1);
                    type = TokenType.FORM_STRING;
                    return;
                }
            }
            if (lan.isInstruction(nextToken)) {
                type = TokenType.INSTRUCTION;
                return;
            }
            if (nextToken.matches("r\\d")) {
                type = TokenType.REGISTER;
                return;
            }
            if (nextToken.endsWith(":")) {
                type = TokenType.SYMBOL;
                nextToken = nextToken.substring(0, nextToken.length() - 1);
                return;
            }
            if (nextToken.matches("#\\d+")) {
                type = TokenType.IMMEDIATE;
                return;
            }
            if (nextToken.toLowerCase().equals(".end")) {
                type = TokenType.END;
                return;
            }
            if (symbols.containsKey(nextToken)) {
                type = TokenType.LABEL;
                return;
            }
            if (nextToken.matches("\\d+")) {
                type = TokenType.LOCATION;
                return;
            }

            type = TokenType.UNKNOWN;
        } else {
            nextToken = "END_LINE";
            type = TokenType.END_LINE;

        }
    }

    public TokenType getTokenType() {
        return type;
    }

    public String getNextToken() {
        String s = nextToken;
        setNextToken();
        return s;
    }

    public int getNextRegister() {
        if (type != TokenType.REGISTER) {
            setNextToken();
            if (toThrow) {
                throw new BadParameterError(lineNum, nextToken, "Register");
            }
        }
        int r = Integer.parseInt(nextToken.substring(1, nextToken.length()));
        setNextToken();
        return r;
    }

    public int getNextImmediate() {
        if (type != TokenType.IMMEDIATE) {
            setNextToken();
            if (toThrow) {
                throw new BadParameterError(lineNum, nextToken, "immediate");
            }
        }
        int i = Integer.parseInt(nextToken.substring(1, nextToken.length()));
        setNextToken();
        return i;
    }

    public int getNextLocation() {

        if (type == TokenType.LOCATION) {
            int i = Integer.parseInt(nextToken);
            setNextToken();
            return i;
        }
        if (type == TokenType.LABEL) {
            int i = (Integer) symbols.get(nextToken);
            setNextToken();
            return i;
        }
        String tok = nextToken;
        setNextToken();
        throw new BadParameterError(lineNum, tok, "label or location");
    }
}
