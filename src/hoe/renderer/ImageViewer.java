package hoe.renderer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class ImageViewer extends JFrame {
    
    private final PanZoomPanel zoomPanel;
    private final BufferedImage image;

    public ImageViewer(BufferedImage image) {
        this.image = image;
        
        setTitle("Image viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    System.exit(0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        setLayout(new BorderLayout());
        zoomPanel = new PanZoomPanel() {
            @Override
            protected void drawScene(Graphics2D g) {
                g.translate(-image.getWidth() / 2, -image.getHeight() / 2);
                drawImage(g, image);
            }
        };
        add(zoomPanel, BorderLayout.CENTER);
        zoomPanel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        pack();
        setLocationRelativeTo(null);
    }

    public PanZoomPanel getZoomPanel() {
        return zoomPanel;
    }

    public BufferedImage getImage() {
        return image;
    }

}