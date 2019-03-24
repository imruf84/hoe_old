package hoe.designer;

import hoe.designer.ServerPanel.SERVER_TYPE;

public class MachinePanel extends javax.swing.JPanel {

    public MachinePanel() {
        initComponents();

        for (SERVER_TYPE type : SERVER_TYPE.values()) {
            serversPanel.add(new ServerPanel(type));
        }
    }

    public void setMachineName(String name) {
        nameTextField.setText(name);
    }

    public String getMachineName() {
        return nameTextField.getText();
    }

    public void setMachineAddress(String address) {
        addressTextField.setText(address);
    }

    public String getMachineAddress() {
        return addressTextField.getText();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        serversPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        serversPanel.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setText("Name:");
        serversPanel.add(jLabel1);
        serversPanel.add(nameTextField);

        jLabel2.setText("Address:");
        serversPanel.add(jLabel2);
        serversPanel.add(addressTextField);

        add(serversPanel);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));
        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPanel serversPanel;
    // End of variables declaration//GEN-END:variables
}
