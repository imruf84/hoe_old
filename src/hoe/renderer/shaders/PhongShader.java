package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
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
        return ShaderBase.readSourceCodeAsResource("/hoe/renderer/shaders/phong.vert");
    }

    @Override
    protected String getFragmentShaderSourceCode() {
        return ShaderBase.readSourceCodeAsResource("/hoe/renderer/shaders/phong.frag");
    }

    public void apply(Vector3d dir) {
        super.apply();
        getGl().glUniform3f(getShaderParamteter("eye"), (float) dir.x, (float) dir.y, (float) dir.z);
    }

}
