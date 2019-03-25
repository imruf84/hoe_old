package hoe.designer;

import hoe.designer.ServerPanel.SERVER_TYPE;
import java.awt.Component;
import java.util.LinkedList;

public class MachinePanel extends javax.swing.JPanel {

    public MachinePanel() {
        initComponents();

        for (SERVER_TYPE type : SERVER_TYPE.values()) {
            serversPanel.add(new ServerPanel(type));
        }
    }

    public MachinePanel(String name, String address) {
        this();

        setMachineName(name);
        setMachineAddress(address);
    }

    public final void setMachineName(String name) {
        nameTextField.setText(name);
    }

    public String getMachineName() {
        return nameTextField.getText();
    }

    public final void setMachineAddress(String address) {
        addressTextField.setText(address);
    }

    public String getMachineAddress() {
        return addressTextField.getText();
    }

    public boolean isDebugEnabled() {
        return debugCheckBox.isSelected();
    }

    public void enableDebug() {
        debugCheckBox.setSelected(true);
    }

    public void enableServer(SERVER_TYPE type, int port) {
        for (Component c : serversPanel.getComponents()) {
            ServerPanel sp = (ServerPanel) c;

            if (sp.getServerType().equals(type)) {
                sp.enableServer();
                sp.setServerPort(port);
            }
        }
    }

    public LinkedList<ServerPanel> getServers(SERVER_TYPE type) {
        LinkedList<ServerPanel> result = new LinkedList<>();

        for (Component c : serversPanel.getComponents()) {
            ServerPanel sp = (ServerPanel) c;
            if (sp.getServerType().equals(type) && sp.isServerEnabled()) {
                result.add(sp);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        debugCheckBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        serversPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel1.setText("Name:");
        jPanel1.add(jLabel1);
        jPanel1.add(nameTextField);

        jLabel2.setText("Address:");
        jPanel1.add(jLabel2);
        jPanel1.add(addressTextField);

        debugCheckBox.setText("Debug");
        jPanel1.add(debugCheckBox);
        jPanel1.add(jSeparator1);

        add(jPanel1);

        serversPanel.setLayout(new java.awt.GridLayout(0, 1));
        add(serversPanel);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressTextField;
    private javax.swing.JCheckBox debugCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTextField nameTextField;
    public javax.swing.JPanel serversPanel;
    // End of variables declaration//GEN-END:variables
}
