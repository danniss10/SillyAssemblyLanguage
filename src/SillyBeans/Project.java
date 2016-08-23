package SillyBeans;

import virtualmachine.*;
import assembler.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Class that represents one assembly project. It holds all the data and components it needs
 * @author Daniel Nissenbaum
 */
public class Project {

    private String name;
    private VirtualMachine vm;
    private Assembler asm;
    private String path;
    private TxtScrollPane txt;
    private JTextArea lst;
    private Console con;
    private ScrollConsole scon;
    private IDE ide;
    private File file;
    private JTabbedPane outputPane;
    private JTabbedPane debugPane;
    private boolean isSaved;
    private boolean conVisable;
    private boolean dbgVisable;
    private Thread running;
    private DebuggingPanel dbgPanel;
    private boolean loading = false;

    
    /**
     * Constructs and new project
     * @param f file location
     * @param ide IDE that will hold the project
     * @param tsp TxtScrollPane
     * @param lstArea listing textArea
     * @param sc console
     * @param out Tabbed pane
     * @param debug debugging tab pane
     */
    public Project(File f, final IDE ide, TxtScrollPane tsp, JTextArea lstArea, ScrollConsole sc, JTabbedPane out, JTabbedPane debug) {

        file = f;
        this.ide = ide;
        name = f.getName();
        txt = tsp;
        lst = lstArea;
        int i = file.getPath().lastIndexOf('.');
        if (i >= 2) {
            path = file.getPath().substring(0, i);
        } else {
            path = file.getPath();
        }
        scon = sc;
        con = scon.getConsole();
        outputPane = out;
        dbgPanel = new DebuggingPanel();
        vm = new VirtualMachine(10000, dbgPanel);
        dbgPanel.setVM(vm);
        vm.setConsole(con);
        asm = new Assembler();
        asm.setConsole(con);
        isSaved = true;
        conVisable = false;
        debugPane = debug;
        tsp.getTextPane().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                addDocListeners();
            }
        });
        addDocListeners();

    }

    private void addDocListeners() {
        txt.getTextPane().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!loading) {
                    ide.setUnsavedTabFont();
                    isSaved = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ide.setUnsavedTabFont();
                isSaved = false;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                System.out.println(e.getLength());
//                System.out.println(e.getType());
//                if (!isSaved) {
//                    ide.setUnsavedTabFont();
//                }
//                isSaved = false;
            }
        });
    }
/**
 * 
 * @return true if project is saved
 */
    public boolean isSaved() {
        return isSaved;
    }
/**
 * 
 * @return project name
 */
    public String getName() {
        return name;
    }
/**
 * sets search term and does search
 * @param term 
 */
    public void setSearchTerm(String term) {
        txt.setSearchTerm(term);
    }
/**
 * sets search case to be on or off
 * @param on case match true or false?
 */
    public void setMatchCase(boolean on) {
        txt.setMatchCase(on);
    }
/**
 * assembles project
 */
    public void assembleProject() {
        if (!conVisable) {
            outputPane.add("Console - " + name + "(asm)", scon);
        } else {
            outputPane.remove(con);
            outputPane.add("Console - " + name + "(asm)", scon);
        }
        outputPane.setSelectedComponent(scon);
        outputPane.setTabComponentAt(outputPane.getSelectedIndex(), new ButtonTabComponent(outputPane));
        conVisable = true;
        int n = 0;
        if (running != null && running.isAlive()) {
            n = JOptionPane.showConfirmDialog(null,
                    "Do you want to terminate " + running.getName() + " and assmble " + name,
                    "Cancel",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (n == JOptionPane.YES_OPTION) {
                running.interrupt();
                running.stop();
                con.setEditable(false);
            }
        }
        con.clear();
        con.setEditable(true);
        asm.assembleFile(path);
        con.setEditable(false);
    }
/**
 * runs project
 */
    public void runProject() {
        if (!conVisable) {
            outputPane.add("Console - " + name + "(run)", scon);
        } else {
            outputPane.remove(con);
            outputPane.add("Console - " + name + "(run)", scon);
        }
        outputPane.setSelectedComponent(scon);
        outputPane.setTabComponentAt(outputPane.getSelectedIndex(), new ButtonTabComponent(outputPane));
        conVisable = true;
        int n = 0;
        if (running != null && running.isAlive()) {
            n = JOptionPane.showConfirmDialog(null,
                    "Do you want to terminate " + running.getName() + " and run " + name,
                    "Cancel",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (n == JOptionPane.YES_OPTION) {
                running.interrupt();
                running.stop();
                con.setEditable(false);
            }
        }
        con.clear();
        running = new Thread(new Runnable() {
            @Override
            public void run() {
                con.setEditable(true);
                vm.runBytecodeFile(path, false);
                con.setEditable(false);
            }
        }, name);
        running.setDaemon(true);
        running.start();

    }
/**
 * stars project debug
 */
    public void debugProject() {

        if (!conVisable) {
            outputPane.add("Console - " + name + "(dbg)", scon);

        } else {
            outputPane.remove(con);
            outputPane.add("Console - " + name + "(dbg)", scon);
        }
        if (!dbgVisable) {
            debugPane.add("Debug- " + name, dbgPanel);
        }
        outputPane.setSelectedComponent(scon);
        outputPane.setTabComponentAt(outputPane.getSelectedIndex(), new ButtonTabComponent(outputPane));
        conVisable = true;
        dbgVisable = true;
        int n = 0;
        if (running != null && running.isAlive()) {
            n = JOptionPane.showConfirmDialog(null,
                    "Do you want to terminate " + running.getName() + " and debug " + name,
                    "Cancel",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (n == JOptionPane.YES_OPTION) {
                running.interrupt();
                running.stop();
                con.setEditable(false);
            }
        }
        con.clear();
        running = new Thread(new Runnable() {
            @Override
            public void run() {
                Component[] com = dbgPanel.getComponents();
                setComponents(com, true);
                con.setEditable(true);
                vm.runBytecodeFile(path, true);
                con.setEditable(false);
                setComponents(com, false);
            }
        }, name + " (dbg)");
        running.setDaemon(true);
        running.start();
    }

//    public void stop(){
//        vm.exit();
//        if(running.isAlive()){
//            String t = con.getText();
//            running.interrupt();
//            running.stop();
//            con.clear();
//            con.cPrintln(t);
//            con.cPrintln("Run Stopped");
//        }
//    }
    /**
     * sets components to be enabled or disabled
     * @param com  component array
     * @param enabled enabled or disabled
     */
    private void setComponents(Component com[], boolean enabled) {
        for (int a = 0; a < com.length; a++) {
            com[a].setEnabled(enabled);
            if (com[a] instanceof Container) {
                Container c = (Container) com[a];
                setComponents(c.getComponents(), enabled);
            }
        }
    }
/**
 * saves project
 */
    public void saveProject() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(txt.getText());
            out.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Error saving: " + file.getPath());
        }
        ide.setSavedTabFont();
        isSaved = true;
    }

    /**
     * load project to text areas
     */
    public void loadProject() {
        loading = true;
        Scanner scan;
        String s = "";
        try {
            scan = new Scanner(file);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(new JFrame(), "Error loading: .txt file was not found.");
            return;
        }
        while (scan.hasNextLine()) {
            s += scan.nextLine() + "\n";
        }
        scan.close();
        txt.setText(s);
        s = "";
        File listing = new File(path + ".lst");
        if (listing.isFile()) {
            try {
                scan = new Scanner(listing);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(new JFrame(), "Error reading listing file");
            }
            while (scan.hasNextLine()) {
                s += scan.nextLine() + "\n";
            }
            lst.setText(s);
            scan.close();
        }
        loading = false;
    }

    /*
     * This origional Class is from a java tutorial provided by Oracle and can be found at
     * http://docs.oracle.com/javase/tutorial/uiswing/components/tabbedpane.html
     * 
     * I have modified it slightly to check if its contents have been saved
     */
    public class ButtonTabComponent extends JPanel {

        private final JTabbedPane pane;

        public ButtonTabComponent(final JTabbedPane pane) {
            //unset default FlowLayout' gaps
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            if (pane == null) {
                throw new NullPointerException("TabbedPane is null");
            }
            this.pane = pane;
            setOpaque(false);

            //make JLabel read titles from JTabbedPane
            JLabel label = new JLabel() {
                public String getText() {
                    int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                    if (i != -1) {
                        return pane.getTitleAt(i);
                    }
                    return null;
                }
            };

            add(label);
            //add more space between the label and the button
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            //tab button
            JButton button = new TabButton();
            add(button);
            //add more space to the top of the component
            setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        }

        private class TabButton extends JButton implements ActionListener {

            public TabButton() {
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("close this tab");
                //Make the button looks the same for all Laf's
                setUI(new BasicButtonUI());
                //Make it transparent
                setContentAreaFilled(false);
                //No need to be focusable
                setFocusable(false);
                setBorder(BorderFactory.createEtchedBorder());
                setBorderPainted(false);
                //Making nice rollover effect
                //we use the same listener for all buttons
                addMouseListener(buttonMouseListener);
                setRolloverEnabled(true);
                //Close the proper tab by clicking the button
                addActionListener(this);
            }

            public void actionPerformed(ActionEvent e) {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                // int j = debugPane.indexOfComponent()
                if (i != -1) {
                    if (running != null && running.isAlive()) {
                        JOptionPane.showMessageDialog(null, "You cannot close the console while " + running.getName() + " is running or debugging", name, JOptionPane.WARNING_MESSAGE);
                        return;

                    }
                    pane.remove(i);
                    debugPane.remove(i);
                    conVisable = false;
                    dbgVisable = false;
                }
            }

            //we don't want to update UI for this button
            public void updateUI() {
            }

            //paint the cross
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                //shift the image for pressed buttons
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLACK);
                if (getModel().isRollover()) {
                    g2.setColor(Color.DARK_GRAY);
                }
                int delta = 6;
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                g2.dispose();
            }
        }
        private final MouseListener buttonMouseListener = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                Component component = e.getComponent();
                if (component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    button.setBorderPainted(true);
                }
            }

            public void mouseExited(MouseEvent e) {
                Component component = e.getComponent();
                if (component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    button.setBorderPainted(false);
                }
            }
        };
    }
}
