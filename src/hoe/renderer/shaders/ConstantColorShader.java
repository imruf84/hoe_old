package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import java.util.HashMap;
import java.util.LinkedList;

public class ConstantColorShader {

    private final int shader;
    private final GL2 gl;
    private final HashMap<String,Integer> parameters=new HashMap<>();
    private final String sourceCode = ""
            + "uniform vec4 col;"
            + "void main()"
            + "{"
            + "  gl_FragColor = col;"
            + "}";

    public ConstantColorShader(GL2 gl) {
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
    
    private String[] getParameterNames() {
        return new String[]{"col"};
    }

    public GL2 getGl() {
        return gl;
    }

    public int getShader() {
        return shader;
    }
    
    private String getSourceCode() {
        return sourceCode;
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
    
    public void apply(float r, float g, float b, float a) {
        apply();
        setColor(r, g, b, a);
    }
    
    public void setColor(float r, float g, float b, float a) {
        int col = parameters.get("col");
        gl.glUniform4f(col, r, g, b, a);
    }

}
