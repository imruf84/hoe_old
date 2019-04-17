package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import java.util.HashMap;

abstract public class ShaderBase {

    private final int program;
    private final int fragmentShader;
    private final GL2 gl;
    private final HashMap<String, Integer> parameters = new HashMap<>();

    public ShaderBase(GL2 gl) {
        this.gl = gl;
        fragmentShader = createFragmentShader();
        program = createProgram(fragmentShader);
        createParameters();
    }

    private void createParameters() {
        for (String paramName : getParameterNames()) {
            int param = gl.glGetUniformLocation(getProgram(), paramName);
            parameters.put(paramName, param);
        }
    }

    public GL2 getGl() {
        return gl;
    }

    public int getProgram() {
        return program;
    }

    public HashMap<String, Integer> getParameters() {
        return parameters;
    }

    public int getShaderParamteter(String name) {
        return getParameters().get(name);
    }

    private int createFragmentShader() {
        GL2 lgl = getGl();
        
        String fc[] = new String[]{getSourceCode()};
        int fs = lgl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        lgl.glShaderSource(fs, 1, fc, null);
        lgl.glCompileShader(fs);
        
        //System.out.println(ShaderUtil.getShaderInfoLog(gl, fs));
        
        return fs;
    }
    
    private int createProgram(int fs) {

        GL2 lgl = getGl();

        int lsh = lgl.glCreateProgram();
        lgl.glAttachShader(lsh, fs);
        lgl.glLinkProgram(lsh);
        
        //System.out.println(ShaderUtil.getShaderInfoLog(gl, lsh));
        
        lgl.glValidateProgram(lsh);

        return lsh;
    }

    public void apply() {
        getGl().glUseProgram(getProgram());
    }
    
    public void dispose() {
        getGl().glDeleteProgram(getProgram());
    }

    abstract protected String[] getParameterNames();

    abstract protected String getSourceCode();
}
