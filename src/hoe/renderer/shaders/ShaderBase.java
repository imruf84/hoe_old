package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import java.util.HashMap;

abstract public class ShaderBase {

    private final int shader;
    private final GL2 gl;
    private final HashMap<String, Integer> parameters = new HashMap<>();

    public ShaderBase(GL2 gl) {
        this.gl = gl;
        shader = createShader();
        createParameters();
    }

    private void createParameters() {
        for (String paramName : getParameterNames()) {
            int param = gl.glGetUniformLocation(getShader(), paramName);
            parameters.put(paramName, param);
        }
    }

    public GL2 getGl() {
        return gl;
    }

    public int getShader() {
        return shader;
    }

    public HashMap<String, Integer> getParameters() {
        return parameters;
    }

    public int getShaderParamteter(String name) {
        return getParameters().get(name);
    }

    private int createShader() {

        GL2 lgl = getGl();

        String fc[] = new String[]{getSourceCode()};

        int fs2 = lgl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        lgl.glShaderSource(fs2, 1, fc, null);
        lgl.glCompileShader(fs2);
        int lsh = lgl.glCreateProgram();
        lgl.glAttachShader(lsh, fs2);
        lgl.glLinkProgram(lsh);
        lgl.glValidateProgram(lsh);

        return lsh;
    }

    public void apply() {
        getGl().glUseProgram(getShader());
    }

    abstract protected String[] getParameterNames();

    abstract protected String getSourceCode();
}
