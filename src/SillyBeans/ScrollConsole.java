/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SillyBeans;

import java.util.InputMismatchException;
import javax.swing.JScrollPane;

/**
 * Scroll that holds a console area. All methods are cover methods for console
 * @author Daniel Nissenbaum
 */
public class ScrollConsole extends JScrollPane {

    Console con;

    /**
     * constructor for ScrollConsole
     * @param c 
     */
    public ScrollConsole(Console c) {
        con = c;
        getViewport().setView(con);
        this.setVisible(true);
    }

    /**
     * sets console
     * @param c 
     */
    public void setConsole(Console c) {
        con = c;
    }

    /**
     * print a line to the console
     *
     * @param s string to print
     */
    public void cPrintln(String s) {
        con.cPrintln(s);
    }

    /**
     * print string to the console
     *
     * @param s string to print
     */
    public void cPrint(String s) {
        con.cPrint(s);
    }

    public String nextLine() {
        return con.nextLine();
    }

    public String next() {
        return con.next();
    }

    public int nextInt() throws InputMismatchException {
        return con.nextInt();
    }

    public Console getConsole() {
        return con;
    }

    public void clear() {
        con.clear();
    }
}
