/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SillyBeans;

import assembler.*;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.Scanner;
import java.util.TreeSet;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Class that syntax highlights text.
 * @author Daniel Nissenbaum
 */
public class SyntaxHighlighter {

    private StyledDocument newDoc;
    private StyledDocument oldDoc;
    private String docString;
    private Scanner scan;
    private SimpleAttributeSet sas;
    private Language lan = new Language();
    private int offset;
    private int length;
    private JTextPane edit;
    private boolean lineEnd = false;
    private TxtScrollPane tsp;
    private TreeSet<String> symbols = new TreeSet<String>();
    private String searchTerm = "";
    private DefaultHighlighter.DefaultHighlightPainter highlight =
            new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    private boolean matchCase = false;

    /**
     * Constructor
     * @param edit - JTExtPane to edit
     */
    public SyntaxHighlighter(JTextPane edit) {
        this.edit = edit;
    }

    /**
     * sets search term
     * @param term term to search
     */
    public void setSearchTerm(String term) {
        searchTerm = term;
    }
/**
     * sets match case
     * @param on 
     */
    public void setMatchCase(boolean on) {
        matchCase = on;
    }

    private String getNextWord() {
        offset += length;
        length = 0;
        String word = "";
        lineEnd = false;
        boolean isQuote = false;
        boolean isComment = false;
        try {
            while (newDoc.getText(offset, 1).trim().isEmpty()) {
                offset++;
            }
            boolean isChar = true;

            while (isChar) {
                String c = newDoc.getText(offset + length++, 1);
                if (isComment || isQuote || !c.trim().isEmpty()) {
                    if (!isComment && c.equals("\"")) {
                        isQuote = !isQuote;
                        if (!isQuote) {
                            word += c;
                            return word;
                        }
                    }
                    if (c.equals(";")) {
                        isComment = true;
                    }
                    if (isQuote && c.equals("\n")) {
                        while (newDoc.getText(offset, 1).trim().isEmpty()) {
                            length++;
                        }
                    }
                    if (c.equals("\n")) {
                        lineEnd = true;
                        isQuote = false;
                        isComment = false;
                        isChar = false;
                    }
                    word += c;
                } else {
                    isChar = false;
                }
            }
        } catch (BadLocationException ex) {
            edit.repaint();
            edit.revalidate();
            return null;
        }
        return word;
    }

    public void setTSP(TxtScrollPane tsp) {
        this.tsp = tsp;
    }

    public void highlightAll() {
        setSymbols();
        setUpDocs();
        sas = new SimpleAttributeSet();
        offset = 0;
        String word = getNextWord();
        while (word != null) {
            handleWord(word);
            word = getNextWord();
        }
        //oldDoc = newDoc;
        edit.setStyledDocument(newDoc);
        if (searchTerm.length() > 0) {
            highlight();
        }
        edit.revalidate();
        //ud.AddUndoRedo(edit);

    }

    private void setUpDocs() {
        newDoc = new DefaultStyledDocument();
        oldDoc = edit.getStyledDocument();
        docString = edit.getText();
        try {
            newDoc.insertString(0, docString, null);
        } catch (BadLocationException ex) {
        }
    }

    private void insert(String s) {
        newDoc.setCharacterAttributes(offset, length, sas, true);
    }

    private void highlight() {
        boolean found = false;
        for (int i = 0; i < edit.getDocument().getLength() - searchTerm.length(); i++) {
            String word = "";

            try {
                word = edit.getDocument().getText(i, searchTerm.length());
            } catch (BadLocationException ex) {
            }
            String term = searchTerm;
            if(!matchCase){
                word = word.toLowerCase();
                term = searchTerm.toLowerCase();
            }
            if (word.equals(term)) {
                StyleConstants.setBackground(sas, Color.YELLOW);
                newDoc.setCharacterAttributes(i, term.length(), sas, true);
                found = true;
                StyleConstants.setBackground(sas, Color.WHITE);
            }
        }
        if(!found){
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void handleWord(String word) {
        if (word.startsWith(";")) {
            StyleConstants.setForeground(sas, Color.GRAY);
            insert(word);
            StyleConstants.setForeground(sas, Color.BLACK);
        } else if (lan.isDirective(word)) {
            StyleConstants.setForeground(sas, new Color(0, 0, 230));
            insert(word);
            StyleConstants.setForeground(sas, Color.BLACK);
        } else if (word.startsWith("\"")) {
            StyleConstants.setForeground(sas, new Color(206, 123, 0));
            insert(word);
            StyleConstants.setForeground(sas, Color.BLACK);
        } else if (lan.isInstruction(word)) {
            StyleConstants.setBold(sas, true);
            insert(word);
            StyleConstants.setBold(sas, false);
        } else if (word.endsWith(":")) {
            StyleConstants.setItalic(sas, true);
            insert(word);
            StyleConstants.setItalic(sas, false);
        } else if (symbols.contains(word)) {
            StyleConstants.setItalic(sas, true);
            insert(word);
            StyleConstants.setItalic(sas, false);
        } else if (word.matches("r\\d")) {
            StyleConstants.setForeground(sas, new Color(0, 153, 0));
            insert(word);
            StyleConstants.setForeground(sas, Color.BLACK);
        } else if (word.matches("#\\d+")) {
            StyleConstants.setForeground(sas, new Color(0, 153, 0));
            insert(word);
            StyleConstants.setForeground(sas, Color.BLACK);
        } else {
            StyleConstants.setItalic(sas, true);
            StyleConstants.setForeground(sas, Color.RED.darker());
            insert(word);
            StyleConstants.setItalic(sas, true);
            StyleConstants.setForeground(sas, Color.BLACK);
        }
        StyleConstants.setForeground(sas, Color.BLACK);
    }

    private void setSymbols() {
        symbols.clear();
        String text = edit.getText();
        String lines[] = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            Scanner scan = new Scanner(lines[i]);
            String firstWord = "";
            if (scan.hasNext()) {
                firstWord = scan.next();
            }
            if (firstWord.endsWith(":")) {
                symbols.add(firstWord.substring(0, firstWord.length() - 1));
            }
        }
    }
}
