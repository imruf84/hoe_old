package prototype;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.LinkedList;
import java.util.Random;
import physics.Vector3D;

public class VPlayer2 extends Player {

    private float color[] = {1, 1, 1, 1};
    private LinkedList<CurvePoint> pathPoints;

    public VPlayer2(String name, Vector3D position, double radius, double maxStep) {
        super(name, position, radius, maxStep);
    }

    public static float[] getRandomColor() {
        Random rnd = new Random();
        return new float[]{rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 1f};
    }

    public void setFillColor(float[] c) {
        this.color = c;
    }

    @Override
    protected void recalculatePath(Vector3D newPos) {
        super.recalculatePath(newPos);
        updatePath();
    }

    public void updatePath() {

        pathPoints = getPath().generatePathPointsByLength(getMaxStep());
    }

    @Override
    public void addNavigationPoint(Vector3D p) {

        super.addNavigationPoint(p);
        updatePath();

    }

    public void render(GL2 gl, GLUT glut, int prog) {

        int col = gl.glGetUniformLocation(prog, "col");

        gl.glPushMatrix();
        CurvePoint p = getPosition();
        gl.glTranslated(p.x, p.y, p.z);
        double h = 0;

        gl.glUniform4f(col, 1, 1, 1, 1);
        glut.glutSolidCylinder(getRadius(), h, 16, 1);

        gl.glUniform4f(col, color[0], color[1], color[2], color[3]);
        glut.glutWireCylinder(getRadius() + getMaxStep(), h / 2, 16, 1);

        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glTranslated(0, 0, h * 1.01);

        gl.glUniform4f(col, 0, 0, 0, 1);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f((float) -getRadius() / 2, 0);
        gl.glVertex2f((float) getRadius() / 2, 0);
        gl.glVertex2f(0, (float) -getRadius() / 2);
        gl.glVertex2f(0, (float) getRadius() / 2);
        gl.glEnd();

        gl.glPopMatrix();

        gl.glPushMatrix();

        gl.glUniform4f(col, 1, 0, 0, 1);
        gl.glPointSize(2);
        gl.glBegin(GL2.GL_POINTS);
        for (CurvePoint cp : pathPoints) {
            gl.glVertex2f((float) cp.x, (float) cp.y);
        }
        gl.glEnd();

        gl.glEnable(GL2.GL_DEPTH_TEST);

        gl.glPopMatrix();

    }
}