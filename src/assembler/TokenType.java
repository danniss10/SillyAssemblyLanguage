package assembler;

/**
 *
 * @author Daniel Nissenbaum
 */
public enum TokenType {

    INSTRUCTION,
    REGISTER, IMMEDIATE,
    START, STRING, ALLOCATE, INTEGER, END, SYMBOL,
    LABEL, LOCATION, COMMENT, UNKNOWN, END_LINE,
    FORM_STRING //string that has been formatted
}
