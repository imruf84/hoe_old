package hoe.renderer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public abstract class RenderCallback implements Runnable {
    private GL2 gl;
    private GLU glu;
    private GLUT glut;

    public RenderCallback() {
    }

    public GL2 getGL() {
        return gl;
    }

    public GLU getGLU() {
        return glu;
    }

    public GLUT getGLUT() {
        return glut;
    }

    @Override
    public void run() {
        render();
    }
    
    public void init(GL2 gl, GLU glu, GLUT glut) {
        this.gl = gl;
        this.glu = glu;
        this.glut = glut;
    }
    
    public abstract void render();
}
