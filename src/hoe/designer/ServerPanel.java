package hoe.designer;

public class ServerPanel extends javax.swing.JPanel {

    public static enum SERVER_TYPE {Game, Database, Redirect, Content, Render};
    
    public ServerPanel(SERVER_TYPE type) {
        initComponents();
        
        enabledCheckBox.setText(type.toString());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enabledCheckBox = new javax.swing.JCheckBox();
        portSpinner = new javax.swing.JSpinner();

        setLayout(new java.awt.GridLayout());

        enabledCheckBox.setText("jCheckBox1");
        enabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enabledCheckBoxActionPerformed(evt);
            }
        });
        add(enabledCheckBox);

        portSpinner.setEnabled(false);
        add(portSpinner);
    }// </editor-fold>//GEN-END:initComponents

    private void enabledCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enabledCheckBoxActionPerformed
        portSpinner.setEnabled(enabledCheckBox.isSelected());
    }//GEN-LAST:event_enabledCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JSpinner portSpinner;
    // End of variables declaration//GEN-END:variables
}
