package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import hoe.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.joml.Vector3d;

public class PhongShader extends ShaderBase {

    public PhongShader(GL2 gl) {
        super(gl);
    }

    @Override
    protected String[] getParameterNames() {
        return new String[]{"eye"};
    }

    @Override
    protected String getVertexShaderSourceCode() {
        InputStream in = getClass().getResourceAsStream("/hoe/renderer/shaders/phong.vert");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder result = new StringBuilder();
            String s;
            while (null != (s = input.readLine())) {
                result.append(s);
            }
            
            return result.toString();
        } catch (IOException ex) {
            Log.error(ex);
        }
        
        return "";
    }

    @Override
    protected String getFragmentShaderSourceCode() {
        return "";
    }

    public void apply(Vector3d dir) {
        super.apply();
        getGl().glUniform3f(getShaderParamteter("eye"), (float) dir.x, (float) dir.y, (float) dir.z);
    }

}
