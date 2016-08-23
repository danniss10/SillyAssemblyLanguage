package SillyBeans;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * This is a JTextArea that functions as a console.
 *
 * @author Daniel Nissenbaum
 */
public class Console extends JTextArea implements KeyListener {

    // keep track of last user location
    private int promptPosition = 0;

    private String nextLine = "";
    private boolean scanning = false;

    // use a monospaced font
    Font font = new Font("Monospaced", Font.PLAIN, 13);

    // document and filter
    AbstractDocument doc;
    ConsoleFilter filter;

    /**
     * Console constructor, sets up Console
     */
    public Console() {
        doc = (AbstractDocument) this.getDocument();
        filter = new ConsoleFilter(this);
        doc.setDocumentFilter(filter);
        this.setFont(font);
        this.addKeyListener(this);
        this.setBackground(new Color(234, 234, 234));
        this.setVisible(true);

    }

    /**
     * print a line to the console
     *
     * @param s string to print
     */
    public void cPrintln(String s) {
        cPrint(s + "\n");
    }

    /**
     * print string to the console
     *
     * @param s string to print
     */
    public void cPrint(String s) {

        try {
            doc.setDocumentFilter(null);
            this.getDocument().insertString(promptPosition, s, null);
        } catch (BadLocationException ex) {
        }
        promptPosition += s.length();
        this.setCaretPosition(promptPosition);
        doc.setDocumentFilter(filter);
    }

    /**
     * @return next line from console
     */
    public String nextLine() {
        scanning = true;
        while (scanning) {
            Thread.yield();
        }
        if (nextLine.startsWith("\n")) {
            nextLine = nextLine.substring(1);
        }
        return nextLine;
    }

    /**
     *
     * @return next token in console
     */
    public String next() {
        String s = nextLine();
        Scanner scan = new Scanner(s);
        s = scan.next();
        scan.close();
        return s;
    }

    /**
     *
     * @return next integer in the console
     * @throws InputMismatchException Int not provided
     */
    public int nextInt() throws InputMismatchException {
        String s = nextLine();
        Scanner scan = new Scanner(s);
        int i = scan.nextInt();
        scan.close();
        return i;
    }

    /**
     * clears the console
     */
    public void clear() {
        promptPosition = 0;
        setText("");
        nextLine = "";
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * sets console prompt position when user hits enter
     *
     * @param e KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                nextLine = this.getText(promptPosition, this.getDocument().getLength() - promptPosition);
            } catch (BadLocationException ex) {
            }

            try {
                e.consume();
                this.getDocument().insertString(this.getDocument().getLength(), "\n", null);
            } catch (BadLocationException ex) {
            }
            promptPosition = this.getText().length();
            this.setCaretPosition(promptPosition);
            if (!nextLine.trim().isEmpty()) {
                scanning = false;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /*
     * private class that filter input to prevent typing in uneditable area
     */
    private class ConsoleFilter extends DocumentFilter {

        private JTextArea ta;

        /*
         * Create the console filter with a text area
         */
        public ConsoleFilter(JTextArea jta) {
            ta = jta;
        }

        // hanlde insertion of text
        public void insertString(final DocumentFilter.FilterBypass fb, final int offset, final String string, final AttributeSet attr)
                throws BadLocationException {

            // only allow if user is editing in the editable area (after promptPosition)
            if (offset >= promptPosition) {
                super.insertString(fb, offset, string, attr);
            } else {
                ta.setCaretPosition(promptPosition);
                super.insertString(fb, promptPosition, string, attr);
            }
        }

        // handle removing of text
        public void remove(final DocumentFilter.FilterBypass fb, final int offset, final int length) throws BadLocationException {

            // only allow if user is editing in the editable area (after promptPosition)
            if (offset >= promptPosition) {
                super.remove(fb, offset, length);
            } else {
                ta.setCaretPosition(promptPosition);
                Toolkit.getDefaultToolkit().beep();
            }
        }

        // handle replacement of text
        public void replace(final DocumentFilter.FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs)
                throws BadLocationException {

            // only allow if user is editing in the editable area (after promptPosition)
            if (offset >= promptPosition) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                ta.setCaretPosition(promptPosition);
                super.replace(fb, promptPosition, length, text, attrs);
            }

        }
    }
}
