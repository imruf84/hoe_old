package hoe.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

abstract public class PanZoomPanel extends JPanel {

    public static final int DEFAULT_MIN_ZOOM_LEVEL = -20;
    public static final int DEFAULT_MAX_ZOOM_LEVEL = 10;
    public static final double DEFAULT_ZOOM_MULTIPLICATION_FACTOR = 1.2;

    private int zoomLevel = 0;
    private final int minZoomLevel = DEFAULT_MIN_ZOOM_LEVEL;
    private final int maxZoomLevel = DEFAULT_MAX_ZOOM_LEVEL;
    private final double zoomMultiplicationFactor = DEFAULT_ZOOM_MULTIPLICATION_FACTOR;
    private Point dragStartScreen;
    private Point dragEndScreen;
    private final AffineTransform coordTransform = new AffineTransform();
    private boolean init = true;

    public PanZoomPanel() {

        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(300, 300));

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dragStartScreen = e.getPoint();
                dragEndScreen = null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                moveCamera(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        addMouseWheelListener((MouseWheelEvent e) -> {
            zoomCamera(e);
        });

    }

    private java.awt.geom.Point2D.Float transformPoint(Point p1) throws NoninvertibleTransformException {
        AffineTransform inverse = coordTransform.createInverse();

        java.awt.geom.Point2D.Float p2 = new java.awt.geom.Point2D.Float();
        inverse.transform(p1, p2);
        return p2;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    private void moveCamera(MouseEvent e) {

        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }

        try {
            dragEndScreen = e.getPoint();
            java.awt.geom.Point2D.Float dragStart = transformPoint(dragStartScreen);
            java.awt.geom.Point2D.Float dragEnd = transformPoint(dragEndScreen);
            double dx = dragEnd.getX() - dragStart.getX();
            double dy = dragEnd.getY() - dragStart.getY();
            coordTransform.translate(dx, dy);
            dragStartScreen = dragEndScreen;
            dragEndScreen = null;
            repaintComponent();
        } catch (NoninvertibleTransformException ex) {
        }
    }

    public void repaintComponent() {
        revalidate();
        repaint();
    }

    private void zoomCamera(MouseWheelEvent e) {
        try {
            int wheelRotation = e.getWheelRotation();
            Point p = e.getPoint();
            if (wheelRotation > 0) {
                if (zoomLevel < maxZoomLevel) {
                    zoomLevel++;
                    java.awt.geom.Point2D p1 = transformPoint(p);
                    coordTransform.scale(1 / zoomMultiplicationFactor, 1 / zoomMultiplicationFactor);
                    java.awt.geom.Point2D p2 = transformPoint(p);
                    coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
                    repaintComponent();
                }
            } else {
                if (zoomLevel > minZoomLevel) {
                    zoomLevel--;
                    java.awt.geom.Point2D p1 = transformPoint(p);
                    coordTransform.scale(zoomMultiplicationFactor, zoomMultiplicationFactor);
                    java.awt.geom.Point2D p2 = transformPoint(p);
                    coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
                    repaintComponent();
                }
            }
        } catch (NoninvertibleTransformException ex) {
        }
    }

    @Override
    public void paintComponent(Graphics pg) {
        super.paintComponent(pg);
        Graphics2D g = (Graphics2D) pg;

        /*g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);*/

        if (init) {
            init = false;
            Dimension d = getSize();
            int xc = d.width / 2;
            int yc = d.height / 2;
            g.translate(xc, yc);
            g.scale(1, -1);
            coordTransform.setToIdentity();
            coordTransform.setTransform(g.getTransform());
        } else {
            g.setTransform(coordTransform);
        }

        g.setColor(Color.BLACK);

        drawScene(g);
    }

    protected void drawImage(Graphics2D g, BufferedImage image) {

        if (image == null) {
            return;
        }

        AffineTransform tr = g.getTransform();
        g.scale(1, -1);
        g.translate(0, -image.getHeight());
        g.drawImage(image, 0, 0, null);
        g.drawRect(0, 0, image.getWidth(), image.getHeight());
        g.setTransform(tr);
    }

    protected abstract void drawScene(Graphics2D g);
}