package hoe.designer;

import java.awt.HeadlessException;
import javax.swing.JButton;
import javax.swing.JFrame;

public class NetworkDesigner extends JFrame {

    private final String title = "Network builder tool";
    
    public NetworkDesigner() throws HeadlessException {
        super();
        
        setTitle(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button = new JButton("btn");
        getContentPane().add(button);

        pack();
        setLocationRelativeTo(null);
    }

}
