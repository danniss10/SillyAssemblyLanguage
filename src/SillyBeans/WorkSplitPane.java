/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SillyBeans;

import java.awt.Font;
import javax.swing.JTextArea;

/**
 *
 * @author Daniel Nissenbaum
 */
public class WorkSplitPane extends javax.swing.JPanel {

    Font font = new Font("Monospaced", Font.PLAIN, 13);

    /**
     * Creates new form WorkSplitPane
     */
    public WorkSplitPane() {
        initComponents();
        lstArea.setFont(font);
        mainSplitPane.setDividerLocation(.5);
        mainSplitPane.setResizeWeight(.5);
    }

    public JTextArea getLstArea() {
        return lstArea;
    }

    public TxtScrollPane getTxtPane() {
        return txtScrollPane;
    }

    public void setDividerLocation50() {
        mainSplitPane.setDividerLocation(.5);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainSplitPane = new javax.swing.JSplitPane();
        txtScrollPane = new SillyBeans.TxtScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstArea = new javax.swing.JTextArea();

        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setLeftComponent(txtScrollPane);

        lstArea.setEditable(false);
        lstArea.setColumns(20);
        lstArea.setRows(5);
        jScrollPane1.setViewportView(lstArea);

        mainSplitPane.setRightComponent(jScrollPane1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea lstArea;
    private javax.swing.JSplitPane mainSplitPane;
    private SillyBeans.TxtScrollPane txtScrollPane;
    // End of variables declaration//GEN-END:variables
}
