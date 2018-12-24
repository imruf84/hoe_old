package prototype;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.LinkedList;
import java.util.Random;
import physics.Vector3D;

public class VPlayer2 extends Player {

    private float color[] = {1, 1, 1, 1};
    private LinkedList<CurvePoint> pathPoints;
    private boolean showNextPoint = !true;
    private boolean showMaxStepCircle = !true;
    private boolean showCollisionCircle = !true;
    private boolean showPathPoints = true;
    private boolean showNavigationPoints = !true;

    public VPlayer2(String name, Vector3D position, double radius, double maxStep) {
        super(name, position, radius, maxStep);
    }

    public static float[] getRandomColor() {
        Random rnd = new Random();
        return new float[]{rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 1f};
    }

    public static float[] getRandomLightColor() {
        Random rnd = new Random();
        return new float[]{.66f + rnd.nextFloat() / 3f, .66f + rnd.nextFloat() / 3f, .66f + rnd.nextFloat() / 3f, 1f};
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

    public void renderPath(GL2 gl, GLUT glut, int prog) {
        int col = gl.glGetUniformLocation(prog, "col");

        gl.glPushMatrix();
        CurvePoint p = getPosition();
        gl.glTranslated(p.x, p.y, p.z);
        double h = getRadius() * 4;

        gl.glDisable(GL2.GL_DEPTH_TEST);

        // Main collision circle.
        if (showCollisionCircle) {
            gl.glUniform4f(col, 1, 0, 0, 1);
            glut.glutWireCylinder(getRadius(), 0, 16, 1);
        }

        //gl.glTranslated(0, 0, h * 1.01);
        //gl.glRotated(90, 0, 0, 1);
        gl.glRotated(getOrientation(), 0, 0, 1);

        // Direction.
        gl.glUniform4f(col, 1, 1, 0, 1);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(0, 0);
        gl.glVertex2f((float) getRadius(), 0);
        gl.glEnd();

        gl.glPopMatrix();

        // Path points.
        gl.glPushMatrix();
        if (showPathPoints) {
            gl.glUniform4f(col, 1, 0, 0, 1);
            gl.glPointSize(2);
            gl.glBegin(GL2.GL_POINTS);
            for (CurvePoint cp : pathPoints) {
                gl.glVertex2f((float) cp.x, (float) cp.y);
            }
            gl.glEnd();
        }

        // Navigation points.
        gl.glPushMatrix();
        if (showNavigationPoints) {
            gl.glUniform4f(col, 0, 0, 1, 1);
            gl.glPointSize(10);
            gl.glBegin(GL2.GL_POINTS);
            for (Vector3D cp : getPath().getPoints()) {
                gl.glVertex2f((float) cp.x, (float) cp.y);
            }
            gl.glEnd();
        }

        // Next point.
        gl.glPushMatrix();
        if (showNextPoint) {
            gl.glUniform4f(col, 0, 1, 0, 1);
            gl.glPointSize(6);
            gl.glBegin(GL2.GL_POINTS);
            CurvePoint currentPosOnPath = getPath().pointAt(getPosition().t);
            gl.glVertex2f((float) currentPosOnPath.x, (float) currentPosOnPath.y);
            gl.glEnd();
            // Next path point-current path point line
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex2f((float) currentPosOnPath.x, (float) currentPosOnPath.y);
            gl.glVertex2f((float) getPosition().x, (float) getPosition().y);
            gl.glEnd();
        }

        gl.glEnable(GL2.GL_DEPTH_TEST);

        gl.glPopMatrix();
        gl.glPopMatrix();
    }

    public void render(GL2 gl, GLUT glut, int prog) {

        int col = gl.glGetUniformLocation(prog, "col");

        gl.glPushMatrix();
        CurvePoint p = getPosition();
        gl.glTranslated(p.x, p.y, p.z);
        double h = getRadius() * 4;

        // Player.
        gl.glPushMatrix();
        //gl.glRotated(90, 0, 0, 1);
        gl.glRotated(getOrientation(), 0, 0, 1);
        //gl.glUniform4f(col, 0, 0, 0, 1);
        //glut.glutSolidCylinder(getRadius()*.99, h*.99, 12, 1);
        gl.glUniform4f(col, 1, 1, 1, 1);
        glut.glutWireCylinder(getRadius(), h, 3, 1);
        gl.glPopMatrix();

        // Max steps area.
        if (showMaxStepCircle) {
            gl.glUniform4f(col, color[0], color[1], color[2], color[3]);
            glut.glutWireCylinder(getRadius() + getMaxStep(), 0, 16, 1);
        }

        gl.glPopMatrix();
        gl.glPopMatrix();

    }
}
