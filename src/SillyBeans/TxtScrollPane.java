/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SillyBeans;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

/**
 *
 * @author Daniel Nissenbaum
 */
public class TxtScrollPane extends JScrollPane {

    private JTextPane jep;
    private JTextArea lines;
    private Font font = new Font("Monospaced", Font.PLAIN, 13);
    private SyntaxHighlighter fsh;
    private Thread highlight;

    public TxtScrollPane() {
        jep = new JTextPane();
        fsh = new SyntaxHighlighter(jep);
        lines = new JTextArea("1");
        jep.setFont(font);
        lines.setFont(font);
        lines.setBackground(new Color(234, 234, 234));
        lines.setEditable(false);
        jep.setBorder(null);
        jep.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (highlight == null || !highlight.isAlive()) {
                    highlight = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setVerticalScroll(false);
                            try {
                                int pos = jep.getCaretPosition();

                                fsh.highlightAll();
                                jep.setCaretPosition(pos);
                            } catch (Exception e) {
                            }
                            setVerticalScroll(true);
                        }
                    });
                    highlight.start();

                }
                //}
                while (highlight != null && highlight.isAlive()) {
                    Thread.yield();
                }
                reval();
            }
        });

        jep.getDocument().addDocumentListener(new DocumentListener() {
            public String getText() {
                int caretPosition = jep.getDocument().getLength();
                Element root = jep.getDocument().getDefaultRootElement();
                String text = "1" + System.getProperty("line.separator");
                for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text += i + System.getProperty("line.separator");
                }
                return text;
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                lines.setText(getText());
            }

            @Override
            public void insertUpdate(DocumentEvent de) {
                lines.setText(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                lines.setText(getText());
            }
        });
        getViewport().add(jep);
        setRowHeaderView(lines);
    }

    public void setSearchTerm(String term) {
        fsh.setSearchTerm(term);
        if (highlight == null || !highlight.isAlive()) {
            highlight = new Thread(new Runnable() {
                @Override
                public void run() {
                    setVerticalScroll(false);
                    try {
                        int pos = jep.getCaretPosition();

                        fsh.highlightAll();
                        jep.setCaretPosition(pos);
                    } catch (Exception e) {
                    }
                    setVerticalScroll(true);
                }
            });
            highlight.start();

        }
        //}
        while (highlight != null && highlight.isAlive()) {
            Thread.yield();
        }
        reval();
    }

    public void setMatchCase(boolean on) {
        fsh.setMatchCase(on);
        if (highlight == null || !highlight.isAlive()) {
            highlight = new Thread(new Runnable() {
                @Override
                public void run() {
                    setVerticalScroll(false);
                    try {
                        int pos = jep.getCaretPosition();

                        fsh.highlightAll();
                        jep.setCaretPosition(pos);
                    } catch (Exception e) {
                    }
                    setVerticalScroll(true);
                }
            });
            highlight.start();

        }
        //}
        while (highlight != null && highlight.isAlive()) {
            Thread.yield();
        }
        reval();
    }

    public String getText() {
        return jep.getText();
    }

    public void setText(String s) {
        jep.setText(s);

        Thread highlight = new Thread(new Runnable() {
            @Override
            public void run() {
                setVerticalScroll(false);
                try {
                    int pos = jep.getCaretPosition();
                    fsh.highlightAll();
                    jep.setCaretPosition(pos);
                } catch (Exception e) {
                }
                setVerticalScroll(true);
            }
        });
        highlight.start();

        reval();


    }

    protected void reval() {
        this.revalidate();
    }

    protected void setVerticalScroll(boolean b) {
        if (!b) {
            this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        } else {
            this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }
    }

    public JTextPane getTextPane() {
        return jep;
    }
}
