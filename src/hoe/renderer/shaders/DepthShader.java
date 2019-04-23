package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;

public class DepthShader extends ShaderBase {

    public DepthShader(GL2 gl) {
        super(gl);
    }

    @Override
    protected String[] getParameterNames() {
        return new String[]{};
    }
    
    @Override
    protected String getVertexShaderSourceCode() {
        return null;
    }

    @Override
    protected String getFragmentShaderSourceCode() {
        return ""
            + "uniform vec4 col;"
            + "void main()"
            + "{"
            + " float ndcDepth = (2.0 * gl_FragCoord.z - gl_DepthRange.near - gl_DepthRange.far) / (gl_DepthRange.far - gl_DepthRange.near); "
            + " float clipDepth = ndcDepth / gl_FragCoord.w; "
            //+ " gl_FragColor = vec4((clipDepth * 0.5) + 0.5); "
            + " gl_FragColor = vec4(gl_FragCoord.z);"
            + "}";
    }

}
