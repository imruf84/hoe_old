package hoe.designer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;
import static hoe.designer.JsonFileFilter.JSON_FILE_EXTENSION;
import hoe.designer.ServerPanel.SERVER_TYPE;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class NetworkDesigner extends javax.swing.JFrame {

    public NetworkDesigner() {
        initComponents();

        machinesPanel.setLayout(new WrapLayout(FlowLayout.LEFT));

        pack();
        setLocationRelativeTo(null);
        //setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public JsonArray toJson() {
        JsonArray machinesJson = new JsonArray();

        for (Component c : machinesPanel.getComponents()) {
            MachinePanel mp = (MachinePanel) c;
            if (mp.getMachineName().isEmpty()) {
                continue;
            }

            JsonObject machineJson = new JsonObject();
            machineJson.add("name", new JsonPrimitive(mp.getMachineName()));
            machineJson.add("address", new JsonPrimitive(mp.getMachineAddress()));
            machineJson.add("debug", new JsonPrimitive(mp.isDebugEnabled()));

            JsonArray serversJson = new JsonArray();
            machineJson.add("servers", serversJson);

            for (Component c2 : mp.serversPanel.getComponents()) {
                ServerPanel sp = (ServerPanel) c2;
                if (!sp.isServerEnabled()) {
                    continue;
                }

                SERVER_TYPE type = sp.getServerType();
                int port = sp.getServerPort();
                JsonObject server = new JsonObject();
                server.add("type", new JsonPrimitive(type.toString()));
                server.add("port", new JsonPrimitive(port));
                serversJson.add(server);
            }

            if (serversJson.size() > 0) {
                machinesJson.add(machineJson);
            }
        }

        return machinesJson;
    }

    public void fromJson(JsonArray json) {
        clearMachines();

        for (int i = 0; i < json.size(); i++) {
            JsonObject machineJson = json.get(i).getAsJsonObject();
            String name = machineJson.get("name").getAsString();
            String address = machineJson.get("address").getAsString();
            boolean debug = machineJson.get("debug").getAsBoolean();

            MachinePanel mp = new MachinePanel(name, address);
            machinesPanel.add(mp);

            if (debug) {
                mp.enableDebug();
            }

            JsonArray serversJson = machineJson.get("servers").getAsJsonArray();
            for (int j = 0; j < serversJson.size(); j++) {
                JsonObject serverJson = serversJson.get(j).getAsJsonObject();
                String type = serverJson.get("type").getAsString();
                int port = serverJson.get("port").getAsInt();
                mp.enableServer(SERVER_TYPE.valueOf(type), port);
            }
        }
    }

    public void clearMachines() {
        machinesPanel.removeAll();
        machinesPanel.updateUI();
    }

    public LinkedList<MachinePanel> getMachines(SERVER_TYPE type) {
        LinkedList<MachinePanel> result = new LinkedList<>();

        for (Component c : machinesPanel.getComponents()) {
            MachinePanel mp = (MachinePanel) c;
            if (!mp.getServers(type).isEmpty()) {
                result.add(mp);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        machinesScrollPane = new javax.swing.JScrollPane();
        machinesPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        addMachineButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
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

        addMachineButton.setText("Add");
        addMachineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMachineButtonActionPerformed(evt);
            }
        });
        jPanel2.add(addMachineButton, new java.awt.GridBagConstraints());

        jButton4.setText("Delete");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton4, new java.awt.GridBagConstraints());
        jPanel2.add(filler2, new java.awt.GridBagConstraints());

        jButton1.setText("Open...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new java.awt.GridBagConstraints());

        jButton2.setText("Save...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2, new java.awt.GridBagConstraints());
        jPanel2.add(filler1, new java.awt.GridBagConstraints());

        jButton3.setText("Export...");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3, new java.awt.GridBagConstraints());

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);
        jPanel1.add(jSeparator1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addMachineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMachineButtonActionPerformed
        MachinePanel mp = new MachinePanel();
        machinesPanel.add(mp);
        machinesPanel.updateUI();
        mp.nameTextField.requestFocus();
    }//GEN-LAST:event_addMachineButtonActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        LinkedList<Component> components = new LinkedList<>();
        for (Component c : machinesPanel.getComponents()) {
            MachinePanel mp = (MachinePanel) c;
            if (mp.getMachineName().isEmpty()) {
                components.add(c);
            }
        }

        for (Component c : components) {
            machinesPanel.remove(c);
        }

        machinesPanel.updateUI();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setFileFilter(new JsonFileFilter());
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.getPath().toLowerCase().endsWith(JSON_FILE_EXTENSION)) {
            } else {
                file = new File(file.toString() + JSON_FILE_EXTENSION);
            }

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(toJson().toString());
                bw.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setFileFilter(new JsonFileFilter());
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                StringBuilder jsonString;
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    jsonString = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        jsonString.append(line);
                    }
                }

                JsonParser parser = new JsonParser();
                JsonArray json = parser.parse(jsonString.toString()).getAsJsonArray();
                fromJson(json);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();

            MachinePanel databaseTCPMachine = getMachines(SERVER_TYPE.DatabaseTCP).getFirst();
            ServerPanel databaseTCPServer = databaseTCPMachine.getServers(SERVER_TYPE.DatabaseTCP).getFirst();
            MachinePanel databaseWebMachine = getMachines(SERVER_TYPE.DatabaseWeb).getFirst();
            ServerPanel databaseWebServer = databaseWebMachine.getServers(SERVER_TYPE.DatabaseWeb).getFirst();
            MachinePanel redirectMachine = getMachines(SERVER_TYPE.Redirect).getFirst();
            ServerPanel redirectServer = redirectMachine.getServers(SERVER_TYPE.Redirect).getFirst();
            MachinePanel gameMachine = getMachines(SERVER_TYPE.Game).getFirst();
            ServerPanel gameServer = gameMachine.getServers(SERVER_TYPE.Game).getFirst();
            
            
            
            for (Component c : machinesPanel.getComponents()) {
                MachinePanel mp = (MachinePanel) c;

                try {
                    File file = new File(dir.getAbsolutePath() + File.separator + mp.getMachineName() + ".preset");
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                        bw.append("name="+mp.getMachineName());
                        bw.newLine();
                        bw.append("ip="+mp.getMachineAddress());
                        bw.newLine();
                        bw.append("debug="+mp.isDebugEnabled());
                        bw.newLine();
                        // TODO: extend the application to set this key instead of this hardcoding
                        bw.append("secretkey=abcd1234");
                        bw.newLine();
                        
                        // If this machine contains the database server...
                        if (mp.getMachineName().equals(databaseTCPMachine.getMachineName())) {
                            bw.append("startdbserver=true");
                            bw.newLine();
                            bw.append("dbserverwebport="+databaseWebServer.getServerPort());
                            bw.newLine();
                            bw.append("dbservertcpport="+databaseTCPServer.getServerPort());
                            bw.newLine();
                        }
                        
                        // If this machine contains the redirect server...
                        if (mp.getMachineName().equals(redirectMachine.getMachineName())) {
                            bw.append("startredirectserver=true");
                            bw.newLine();
                            bw.append("redirectserverport="+redirectServer.getServerPort());
                            bw.newLine();
                        }
                        
                        // If this machine contains the game server...
                        if (mp.getMachineName().equals(gameMachine.getMachineName())) {
                            bw.append("startgameserver=true");
                            bw.newLine();
                            bw.append("gameserverport="+gameServer.getServerPort());
                            bw.newLine();
                        }
                        
                        for (Component c2 : mp.getServers(SERVER_TYPE.Content)) {
                            ServerPanel sp = (ServerPanel)c2;
                            bw.append("startcontentserver=true");
                            bw.newLine();
                            bw.append("clearcontentservercache=true");
                            bw.newLine();
                            bw.append("contentserverport="+sp.getServerPort());
                            bw.newLine();
                        }
                        
                        for (Component c2 : mp.getServers(SERVER_TYPE.Render)) {
                            ServerPanel sp = (ServerPanel)c2;
                            bw.append("startrenderserver=true");
                            bw.newLine();
                            bw.append("renderserverport="+sp.getServerPort());
                            bw.newLine();
                        }
                        
                        String redirectServerUrl = "http://"+redirectMachine.getMachineAddress()+":"+redirectServer.getServerPort();
                        String userDbAddress = databaseTCPMachine.getMachineAddress()+":"+databaseTCPServer.getServerPort();
                        String sceneDbAddress = userDbAddress;
                        
                        bw.append("redirectserverurl="+redirectServerUrl);
                        bw.newLine();
                        bw.append("userdbip="+userDbAddress);
                        bw.newLine();
                        bw.append("scenedbip="+sceneDbAddress);
                        bw.newLine();
                        
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMachineButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel machinesPanel;
    private javax.swing.JScrollPane machinesScrollPane;
    // End of variables declaration//GEN-END:variables
}
