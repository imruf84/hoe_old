package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import hoe.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Fine examples (bumpnotan...):
 * http://web.engr.oregonstate.edu/~mjb/glman/Examples/
 *
 * Frankó pbr shader processinghez (vannak fények, de normal map pl nincs):
 * https://github.com/kosowski/SimplePBR/blob/master/data/shaders/pbr/simplepbr.frag
 *
 * https://github.com/Nadrin/PBR/blob/master/data/shaders/glsl/pbr_fs.glsl Ez
 * tűnik a legjobbnak (van benne minden):
 * https://github.com/Nadrin/PBR/blob/master/data/shaders/glsl/pbr_fs.glsl
 * https://github.com/mattdesl/lwjgl-basics/wiki/GLSL-Versions
 */
abstract public class ShaderBase {

    private int programId;
    private int fragmentShaderId = -1;
    private int vertexShaderId = -1;
    private final GL2 gl;
    private final HashMap<String, Integer> parameters = new HashMap<>();

    public ShaderBase(GL2 gl) {
        this.gl = gl;
        vertexShaderId = createVertexShader();
        fragmentShaderId = createFragmentShader();
        programId = createProgram(vertexShaderId, fragmentShaderId);
        createParameters();
    }

    public static String readSourceCodeAsResource(String path) {
        InputStream in = ShaderBase.class.getResourceAsStream(path);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder result = new StringBuilder();
            String s;
            while (null != (s = input.readLine())) {
                result.append(s).append('\n');
            }
            
            return result.toString();
        } catch (IOException ex) {
            Log.error(ex);
        }
        
        return "";
    }
    
    private void createParameters() {
        for (String paramName : getParameterNames()) {
            int param = gl.glGetUniformLocation(getProgramId(), paramName);
            parameters.put(paramName, param);
        }
    }

    protected void printShaderInfoLog(int sh) {
        String shaderInfoLog = ShaderUtil.getShaderInfoLog(gl, sh);
        for (String e : shaderInfoLog.split("\n")) {
            if (e.startsWith("ERROR")) {
                Log.error(new Exception(e));
            }
            if (e.startsWith("WARNING")) {
                Log.warning(e);
                Log.warning(Log.stackTraceToString(new Exception("Shader compile warning")));
            }
        }
    }

    public GL2 getGl() {
        return gl;
    }

    public int getProgramId() {
        return programId;
    }

    public int getFragmentShaderId() {
        return fragmentShaderId;
    }

    public int getVertexShaderId() {
        return vertexShaderId;
    }

    public HashMap<String, Integer> getParameters() {
        return parameters;
    }

    public int getShaderParamteter(String name) {
        return getParameters().get(name);
    }

    private int createShader(int shaderType, int sourceCodeIndex) {
        GL2 lgl = getGl();

        String sc = getSourceCodes()[sourceCodeIndex];
        int sh = -1;
        if (sc != null) {
            String fc[] = new String[]{sc};
            sh = lgl.glCreateShader(shaderType);
            lgl.glShaderSource(sh, 1, fc, null);
            lgl.glCompileShader(sh);

            printShaderInfoLog(sh);
        }

        return sh;
    }

    private int createVertexShader() {
        return createShader(GL2.GL_VERTEX_SHADER, 0);
    }

    private int createFragmentShader() {
        return createShader(GL2.GL_FRAGMENT_SHADER, 1);
    }

    private int createProgram(int vs, int fs) {

        if (vs == -1 && fs == -1) {
            return -1;
        }

        GL2 lgl = getGl();

        int pId = lgl.glCreateProgram();

        if (vs != -1) {
            lgl.glAttachShader(pId, vs);
            lgl.glLinkProgram(pId);
        }

        if (fs != -1) {
            lgl.glAttachShader(pId, fs);
            lgl.glLinkProgram(pId);
        }

        lgl.glValidateProgram(pId);

        return pId;
    }

    public void apply() {
        if (getProgramId() == -1) {
            return;
        }

        getGl().glUseProgram(getProgramId());
    }

    public void delete() {

        if (getVertexShaderId() != -1) {
            getGl().glDeleteShader(getVertexShaderId());
            return;
        }

        if (getFragmentShaderId() != -1) {
            getGl().glDeleteShader(getFragmentShaderId());
            return;
        }

        if (getProgramId() != -1) {
            getGl().glDeleteProgram(getProgramId());
            programId = -1;
        }
    }

    protected String[] getSourceCodes() {
        return new String[]{getVertexShaderSourceCode(), getFragmentShaderSourceCode()};
    }

    abstract protected String[] getParameterNames();

    abstract protected String getVertexShaderSourceCode();

    abstract protected String getFragmentShaderSourceCode();
}
