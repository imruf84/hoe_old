package hoe.renderer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import hoe.renderer.shaders.ConstantColorShader;
import hoe.renderer.shaders.PhongShader;
import hoe.renderer.shaders.ShaderBase;
import hoe.renderer.shaders.ShaderManager;

public abstract class RenderCallback implements Runnable {

    private GL2 gl;
    private GLU glu;
    private GLUT glut;
    private ShaderManager shaders;

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

    public ShaderManager getShaders() {
        return shaders;
    }

    public ShaderBase getShader(String name) {
        return shaders.get(name);
    }
    
    public ConstantColorShader getConstantColorShader() {
        return (ConstantColorShader) getShader(ShaderManager.CONSTANT_COLOR_SHADER);
    }
    
    public PhongShader getPhongShader() {
        return (PhongShader) getShader(ShaderManager.PHONG_SHADER);
    }

    @Override
    public void run() {
        render();
    }

    public void init(GL2 gl, GLU glu, GLUT glut, ShaderManager shaders) {
        this.gl = gl;
        this.glu = glu;
        this.glut = glut;
        this.shaders = shaders;
    }

    public abstract void render();
}
