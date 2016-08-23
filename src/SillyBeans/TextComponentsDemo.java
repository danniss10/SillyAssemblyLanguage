/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SillyBeans;

/**
 *
 * @author Daniel Nissenbaum
 */
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
 
import javax.swing.*;
import javax.swing.text.*;
 
public class TextComponentsDemo extends JFrame {
    JTextPane textPane;
    AbstractDocument doc;
    static final int MAX_CHARACTERS = 300;
    JTextArea changeLog;
    String newline = "\n";
    HashMap<Object, Action> actions;
 
    //undo helpers
 
    public TextComponentsDemo() {
        super("TextComponentDemo");
 
        //Create the text pane and configure it.
        textPane = new JTextPane();
        textPane.setCaretPosition(0);
        textPane.setMargin(new Insets(5,5,5,5));
        StyledDocument styledDoc = textPane.getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            doc = (AbstractDocument)styledDoc;
        } else {
            System.err.println("Text pane's document isn't an AbstractDocument!");
            System.exit(-1);
        }
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(200, 200));
 
        //Create the text area for the status log and configure it.
        changeLog = new JTextArea(5, 30);
        changeLog.setEditable(false);
        JScrollPane scrollPaneForLog = new JScrollPane(changeLog);
 
        //Create a split pane for the change log and the text area.
        JSplitPane splitPane = new JSplitPane(
                                       JSplitPane.VERTICAL_SPLIT,
                                       scrollPane, scrollPaneForLog);
        splitPane.setOneTouchExpandable(true);
 
 
        //Add the components.
        getContentPane().add(splitPane, BorderLayout.CENTER);
 
        //Set up the menu bar.
        actions=createActionTable(textPane);
        JMenu styleMenu = createStyleMenu();
        JMenuBar mb = new JMenuBar();
        mb.add(styleMenu);
        setJMenuBar(mb);
 
        //Add some key bindings.
        addBindings();
 
        //Put the initial text into the text pane.
        initDocument();
        textPane.setCaretPosition(0);
 
    }
 
    
 

 
    //Add a couple of emacs key bindings for navigation.
    protected void addBindings() {
        InputMap inputMap = textPane.getInputMap();
 
        //Ctrl-b to go backward one character
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.backwardAction);
 
        //Ctrl-f to go forward one character
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.forwardAction);
 
        //Ctrl-p to go up one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.upAction);
 
        //Ctrl-n to go down one line
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.downAction);
    }
 
    //Create the edit menu.
 
    //Create the style menu.
    protected JMenu createStyleMenu() {
        JMenu menu = new JMenu("Style");
 
        Action action = new StyledEditorKit.BoldAction();
        action.putValue(Action.NAME, "Bold");
        menu.add(action);
 
        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Italic");
        menu.add(action);
 
        action = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.NAME, "Underline");
        menu.add(action);
 
        menu.addSeparator();
 
        menu.add(new StyledEditorKit.FontSizeAction("12", 12));
        menu.add(new StyledEditorKit.FontSizeAction("14", 14));
        menu.add(new StyledEditorKit.FontSizeAction("18", 18));
 
        menu.addSeparator();
 
        menu.add(new StyledEditorKit.FontFamilyAction("Serif",
                                                      "Serif"));
        menu.add(new StyledEditorKit.FontFamilyAction("SansSerif",
                                                      "SansSerif"));
 
        menu.addSeparator();
 
        menu.add(new StyledEditorKit.ForegroundAction("Red",
                                                      Color.red));
        menu.add(new StyledEditorKit.ForegroundAction("Green",
                                                      Color.green));
        menu.add(new StyledEditorKit.ForegroundAction("Blue",
                                                      Color.blue));
        menu.add(new StyledEditorKit.ForegroundAction("Black",
                                                      Color.black));
 
        return menu;
    }
 
    protected void initDocument() {
        String initString[] =
                { "Use the mouse to place the caret.",
                  "Use the edit menu to cut, copy, paste, and select text.",
                  "Also to undo and redo changes.",
                  "Use the style menu to change the style of the text.",
                  "Use the arrow keys on the keyboard or these emacs key bindings to move the caret:",
                  "Ctrl-f, Ctrl-b, Ctrl-n, Ctrl-p." };
 
        SimpleAttributeSet[] attrs = initAttributes(initString.length);
 
        try {
            for (int i = 0; i < initString.length; i ++) {
                doc.insertString(doc.getLength(), initString[i] + newline,
                        attrs[i]);
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text.");
        }
    }
 
    protected SimpleAttributeSet[] initAttributes(int length) {
        //Hard-code some attributes.
        SimpleAttributeSet[] attrs = new SimpleAttributeSet[length];
 
        attrs[0] = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs[0], "SansSerif");
        StyleConstants.setFontSize(attrs[0], 16);
 
        attrs[1] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setBold(attrs[1], true);
 
        attrs[2] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setItalic(attrs[2], true);
 
        attrs[3] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[3], 20);
 
        attrs[4] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontSize(attrs[4], 12);
 
        attrs[5] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setForeground(attrs[5], Color.red);
 
        return attrs;
    }
 
    //The following two methods allow us to find an
    //action provided by the editor kit by its name.
    private HashMap<Object, Action> createActionTable(JTextComponent textComponent) {
        HashMap<Object, Action> actions = new HashMap<Object, Action>();
        Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
    return actions;
    }
 
    private Action getActionByName(String name) {
        return actions.get(name);
    }
 
   
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final TextComponentsDemo frame = new TextComponentsDemo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    //The standard main method.
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
        createAndShowGUI();
            }
        });
    }
}

