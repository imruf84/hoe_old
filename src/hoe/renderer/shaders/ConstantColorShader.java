package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;

public class ConstantColorShader extends ShaderBase {

    public static final String COLOR_PARAMETER_NAME = "color";
    
    public ConstantColorShader(GL2 gl) {
        super(gl);
    }

    @Override
    protected String[] getParameterNames() {
        return new String[]{COLOR_PARAMETER_NAME};
    }

    @Override
    protected String getSourceCode() {
        return ""
                + "uniform vec4 color;"
                + "void main()"
                + "{"
                + "  gl_FragColor = color;"
                + "}";
    }

    public void setColor(float r, float g, float b, float a) {
        int color = getShaderParamteter(COLOR_PARAMETER_NAME);
        getGl().glUniform4f(color, r, g, b, a);
    }

    public void apply(float r, float g, float b, float a) {
        apply();
        setColor(r, g, b, a);
    }

}
