package hoe.designer;

import javax.swing.JSpinner;

public class ServerPanel extends javax.swing.JPanel {

    public static enum SERVER_TYPE {

        Game, DatabaseWeb, DatabaseTCP, Redirect, Content, Render
    };

    private final SERVER_TYPE type;

    public ServerPanel(SERVER_TYPE type) {
        initComponents();

        portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner, "#"));
        this.type = type;
        enabledCheckBox.setText(getServerType().toString());
    }

    public boolean isServerEnabled() {
        return enabledCheckBox.isSelected();
    }

    public void enableServer() {
        enabledCheckBox.setSelected(true);
        portSpinner.setEnabled(true);
    }

    public int getServerPort() {
        return (int) portSpinner.getValue();
    }

    public void setServerPort(int port) {
        portSpinner.setValue(port);
    }

    public final SERVER_TYPE getServerType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enabledCheckBox = new javax.swing.JCheckBox();
        portSpinner = new javax.swing.JSpinner();

        setLayout(new java.awt.GridLayout(1, 0));

        enabledCheckBox.setText("jCheckBox1");
        enabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enabledCheckBoxActionPerformed(evt);
            }
        });
        add(enabledCheckBox);

        portSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(portSpinner, ""));
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
