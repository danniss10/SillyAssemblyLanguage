/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SillyBeans;

import assembler.Language;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 *
 * @author Daniel Nissenbaum
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SyntaxHighlighterTest implements Runnable {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String NAME = "";
    private JFrame frame;
    private JTextPane textPane = new JTextPane();
    private int keysPressed = -1; /* Keys pressed */

    private ArrayList<Integer> keyCode = new ArrayList<Integer>(); /* List of our keyCodes */

    private Language lan = new Language();

    public void run() {
        frame = new JFrame(NAME);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        frame.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(textPane, BorderLayout.CENTER);
        textPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        textPane.setEditable(true);
        textPane.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int previousPos; /* Previous position of the caret */
                int length; /* Length of our grabbed string */

                int currentPos; /* Current position of the caret */

                String text; /* The entire text of the JTextPane */
                String subText; /* Our sub-string-text we're using */
                String subTextP; /* Our sub-string-text plus one character*/

                boolean first = true; /* Be default, it's the first letter */

                StyledDocument doc = textPane.getStyledDocument();
                SimpleAttributeSet sas = new SimpleAttributeSet(); /* So we can set bold, and such */

                keyCode.add(new Integer(e.getKeyCode())); /* The key pressed */
                keysPressed++;
                currentPos = textPane.getCaretPosition(); /* The current position of the caret */

                text = textPane.getText(); /* Grabbing the text on the text pane */
                previousPos = text.lastIndexOf(" ", currentPos); /* Getting the last position of a space */
                if (previousPos <= 0) /* If the position if before or equal to 0 */ {
                    previousPos = 0; /* Then the position is 0 */
                }
                length = currentPos - previousPos; /* The length of the string we're messing with, is between the two positions */
                subText = text.substring(previousPos, currentPos); /* Grabbing the string between our two positions */
                if (first) /* If this is the first letter, or insert */ {
                    if (keyCode.contains(KeyEvent.VK_SHIFT)) {
                        first = true;
                        subTextP = text.substring(0, 0); /* Then we want to grab it, at 0, 0 */
                    } else {
                        subTextP = text.substring(0, 0); /* Then we want to grab it, at 0, 0 */
                        first = false; /* it's no longer the first */
                    }
                } else /* If it isn't */ {
                    subTextP = text.substring(previousPos + 1, currentPos); /* Then we want to grab the usual */
                }
                subText = subText.replaceAll("[\\n\\t\\r]", ""); /* Getting rid of all the tabs and newlines */
                subTextP = subTextP.replaceAll("[\\n\\t\\r]", ""); /*Getting rid of all the tabs and new lines */

                if (keyCode.contains(KeyEvent.VK_3)) {
                    if (keyCode.contains(KeyEvent.VK_SHIFT)) {
                        System.out.println("Number sign hit!");
                        StyleConstants.setForeground(sas, Color.GREEN); /* Anything following a number sign will be green */
                        doc.setCharacterAttributes(previousPos, length, sas, false);  /* Turning it green */
                    }
                }
                if (keyCode.contains(KeyEvent.VK_SPACE)) /* If a space has been hit! */ {
                    /* This is were we'll do all text coloring and such */
                    if (subText.equals(" if") || subText.equals("if") || subTextP.equals("if")) /* All things to be bolded */ {
                        StyleConstants.setForeground(sas, Color.GRAY); /* All of these statements will be gray... */
                        StyleConstants.setBold(sas, true); /* ... and bold */
                        doc.setCharacterAttributes(previousPos, length, sas, false); /* Making them so! */
                        StyleConstants.setBold(sas, false); /* We don't want these attributes to remain... */
                        StyleConstants.setForeground(sas, Color.black); /* ... So we're removing them. */
                    }
                    if (lan.isInstruction(subText.trim()) ||lan.isInstruction(subTextP)) {
                        StyleConstants.setForeground(sas, Color.RED); /* All of these statements will be gray... */
                        StyleConstants.setBold(sas, true); /* ... and bold */
                        doc.setCharacterAttributes(previousPos, length, sas, false); /* Making them so! */
                        StyleConstants.setBold(sas, false); /* We don't want these attributes to remain... */
                        StyleConstants.setForeground(sas, Color.black); /* ... So we're removing them. */
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
                for (int i = keysPressed; i >= 0; i--) /* For loop to remove all keyPresses from our list */ {
                    keyCode.remove(i); /* Removing the specified keyPress */
                }
                keysPressed = -1; /* Because the first index is 0, and we want to add one to keysPressed, we need to start below 0 */
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        new Thread(this).start();
    }

    public final static void main(String args[]) {
        new SyntaxHighlighterTest().start();
    }
}