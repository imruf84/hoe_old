package hoe.designer;

import java.awt.FlowLayout;
import javax.swing.JFrame;

public class NetworkDesigner extends javax.swing.JFrame {

    public NetworkDesigner() {
        initComponents();

        machinesPanel.setLayout(new WrapLayout(FlowLayout.LEFT));
        
        pack();
        setLocationRelativeTo(null);
        setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        machinesScrollPane = new javax.swing.JScrollPane();
        machinesPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        addMachineButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(10, 32767));
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(10, 32767));
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Network Designer");
        setMinimumSize(new java.awt.Dimension(500, 500));

        machinesScrollPane.setBorder(null);
        machinesScrollPane.setAlignmentX(0.0F);
        machinesScrollPane.setAlignmentY(0.0F);

        machinesPanel.setLayout(new java.awt.GridLayout(0, 5));
        machinesScrollPane.setViewportView(machinesPanel);

        getContentPane().add(machinesScrollPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout(0, 20));

        java.awt.GridBagLayout jPanel2Layout = new java.awt.GridBagLayout();
        jPanel2Layout.rowHeights = new int[] {20, 20};
        jPanel2.setLayout(jPanel2Layout);

        addMachineButton.setText("Add Machine");
        addMachineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMachineButtonActionPerformed(evt);
            }
        });
        jPanel2.add(addMachineButton, new java.awt.GridBagConstraints());
        jPanel2.add(filler2, new java.awt.GridBagConstraints());

        jButton1.setText("Open");
        jPanel2.add(jButton1, new java.awt.GridBagConstraints());

        jButton2.setText("Save");
        jPanel2.add(jButton2, new java.awt.GridBagConstraints());
        jPanel2.add(filler1, new java.awt.GridBagConstraints());

        jButton3.setText("Export");
        jPanel2.add(jButton3, new java.awt.GridBagConstraints());

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);
        jPanel1.add(jSeparator1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addMachineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMachineButtonActionPerformed
        machinesPanel.add(new MachinePanel());
        machinesPanel.updateUI();
    }//GEN-LAST:event_addMachineButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMachineButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel machinesPanel;
    private javax.swing.JScrollPane machinesScrollPane;
    // End of variables declaration//GEN-END:variables
}
