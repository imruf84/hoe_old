package hoe.editor;

import hoe.Player;
import hoe.curves.CurvePoint;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.LinkedList;
import java.util.Random;
import hoe.physics.Vector3D;

public class VPlayer extends Player {

    private float color[] = {1, 1, 1, 1};
    private LinkedList<CurvePoint> pathPoints;
    private final boolean showNextPoint = !true;
    private final boolean showMaxStepCircle = !true;
    private final boolean showCollisionCircle = !true;
    private final boolean showPathPoints = true;
    private final boolean showNavigationPoints = !true;

    public VPlayer(String name, Vector3D position, double radius, double maxStep) {
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
        
        gl.glUseProgram(prog);
        
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

        //int col = gl.glGetUniformLocation(prog, "col");

        //gl.glUseProgram(0);
        
        // Player.
        /*gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[]{.1f,.1f,.1f,0},0);
        
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{.4f,.4f,.4f},0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{1000,1000,1000},0);
        
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[]{.3f,.3f,.3f},0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[]{-1000,1000,1000},0);
        
        gl.glEnable(GL2.GL_LIGHT2);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, new float[]{.1f,.1f,.1f},0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, new float[]{0,-1000,10},0);
        
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, new float[]{0,0,1},0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{1,1,1},0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 100f);
        */
        gl.glPushMatrix();
        CurvePoint p = getPosition();
        gl.glTranslated(p.x, p.y, p.z);
        
        gl.glRotated(getOrientation(), 0, 0, 1);
        
        //glut.glutSolidTeapot(getRadius(),false);
        glut.glutSolidTorus(1, 3, 60, 60);

        // Max steps area.
/*        int col = gl.glGetUniformLocation(prog, "col");
        if (showMaxStepCircle) {
            gl.glUniform4f(col, color[0], color[1], color[2], color[3]);
            glut.glutWireCylinder(getRadius() + getMaxStep(), 0, 16, 1);
        }
*/
        gl.glPopMatrix();

    }
}
