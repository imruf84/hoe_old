package hoe.physics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;


/* 
https://slideplayer.com/slide/11009469/
http://vlab.amrita.edu/?sub=1&brch=68&sim=189&cnt=1 
http://output.to/sideway/default.asp?qno=141200004 ütközésválasz
http://www.euclideanspace.com/physics/dynamics/collision/twod/index.htm
 */
public class Verlet2 extends JPanel {

    static Scene scene = new Scene();
    static final AtomicBoolean isPlaying = new AtomicBoolean(false);
    
    
    public static void main__(String[] args) {
        /*Vector3D P = new Vector3D(0, 0, 100);
        System.out.println(new Transform()
                //.lookAt(0, 0, 500, 0, 0, 0, 0, 0, 1)
                .orthographic(0, 500, 500, 0, 1, 100)
                .lookAt(0, 100, 500, 0, 0, 0, 0, 0, 1)
                .transform(P));*/
        
        Matrix4f m = new Matrix4f()
                .ortho2D(-500, 500, 500, -500)
     //.perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f)
     .lookAt(0.0f, 0.0f, 10.0f,
             0.0f, 0.0f, 0.0f,
             0.0f, 0.0f, 1.0f);

        Vector3f v = new Vector3f(0,0,0);
        System.out.println(m.transformProject(v).toString());
        Vector3f v2 = new Vector3f(0,0,10);
        System.out.println(m.transformProject(v2).toString());
    }

    public static void main_(String[] args) throws ArrayLengthException {

        JFrame frame = new JFrame("HoE Physics");
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(new Verlet2(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonsPanel = new JPanel();
        frame.add(buttonsPanel, BorderLayout.EAST);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JButton playPauseButton = new JButton("Play/Pause");
        buttonsPanel.add(playPauseButton);
        playPauseButton.addActionListener((ActionEvent ae) -> {
            isPlaying.set(!isPlaying.get());
        });

        JButton oneStepButton = new JButton("One step");
        buttonsPanel.add(oneStepButton);
        oneStepButton.addActionListener((ActionEvent ae) -> {
            scene.update();
            frame.repaint();
        });

        frame.setVisible(true);

        // Jelenet létrehozása.
        // Tetraéder.
        double mass = 10d;
        double size = 100d;

        // Egy pontból álló test.
        Mesh onePointMesh = Mesh.createOnePointMesh(new Point(0, 0, 0, mass * 10), new Transform().translate(0, 0, 500));
        for (int i = 0; i < onePointMesh.points.size(); i++) {
            onePointMesh.points.get(i).oldPos.set(Vector3D.add(onePointMesh.points.get(i).curPos, new Vector3D(0, 0, 8)));
        }
        scene.meshes.add(onePointMesh);
        
        // Kocka.
        //Mesh cube1 = Mesh.createCube(size, new Transform().rotate(0, 0.2, 0).scale(4, 1, .6).translate(20, 0, 400));
        Mesh cube1 = Mesh.createCube(size, new Transform().rotate(0, 0, 0).scale(0, 0, 0).translate(0, 0, 0));
        //scene.meshes.add(cube1);

        // Padló.
        //size *= 4;
        Mesh plane1 = Mesh.createPlane(size, new Transform());
        scene.meshes.add(plane1);

        // Szimuláció futtatása.
        new Thread(() -> {
            while (true) {
                // Kirajzolása.
                frame.repaint();
                frame.setTitle(scene.frames.toString());
                try {
                    //Thread.sleep((long) (scene.dt * 1000));
                    //Thread.sleep((long) (5));
                    Thread.sleep((long) (1));
                    //Thread.sleep((long) (8)); //25fps
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /*AffineTransform currentTransform = g2.getTransform();
        AffineTransform viewTransform = new AffineTransform(currentTransform);
        viewTransform.scale(1, -1);
        viewTransform.translate(getWidth() / 2, -getHeight() * 7 / 8);
        g2.setTransform(viewTransform);*/

        // Léptetés.
        if (isPlaying.get()) {
            scene.update();
        }

        // Jelenet kirajzolása.
        // Pontok.
    
        Transform camera = new Transform()
                .orthographic(0, 500, 500, 0, 1, 100)
                .lookAt(0, 0, 200, 0, 0, 0, 0, 0, 1);

        double particleRadius = 10;
        for (Mesh m : scene.meshes) {
            for (Point p : m.points) {
                g2.setColor(Color.BLACK);
                //g2.drawOval((int) (p.curPos.x - particleRadius / 2d), (int) (p.curPos.z - particleRadius / 2d), (int) particleRadius, (int) particleRadius);
                
                Vector3D tp = camera.transform(p.curPos);
                g2.drawOval((int) (tp.x - particleRadius / 2d), (int) (tp.y - particleRadius / 2d), (int) particleRadius, (int) particleRadius);
                System.out.println(tp.toString());

                /*g2.setColor(Color.RED);
                g2.drawOval((int) (p.oldPos.x - particleRadius / 2d), (int) (p.oldPos.z - particleRadius / 2d), (int) particleRadius, (int) particleRadius);
*/
            }
            System.out.println("----");
        }

        // Élek.
/*        for (Mesh m : scene.meshes) {
            for (Face f : m.getFaces()) {
                for (int i = 0; i < f.indexes.length; i++) {
                    Point p0 = m.points.get(f.indexes[i]);
                    Point p1 = m.points.get(f.indexes[i == f.indexes.length - 1 ? 0 : i + 1]);
                    g2.setColor(Color.BLACK);
                    g2.drawLine((int) p0.curPos.x, (int) p0.curPos.z, (int) p1.curPos.x, (int) p1.curPos.z);
                }
            }
        }*/

    }

}
