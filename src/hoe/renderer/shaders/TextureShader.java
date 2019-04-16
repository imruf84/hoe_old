package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

public class TextureShader extends ShaderBase {

    public static final String TEXTURE_PARAMETER_NAME = "tex";

    public TextureShader(GL2 gl) {
        super(gl);
    }

    @Override
    protected String[] getParameterNames() {
        return new String[]{TEXTURE_PARAMETER_NAME};
    }

    @Override
    protected String getSourceCode() {
        return "" 
                + "uniform sampler2D tex;"
                + "uniform sampler2D tex2;"
                + "void main()"
                + "{"
                + "    vec4 color = texture2D(tex,gl_TexCoord[0].st);"
                + "    vec4 color2 = texture2D(tex2,gl_TexCoord[0].st);"
                + "    gl_FragColor = color*color2;"
                + "}";
    }

    public void setTexture(Texture texture) {
        int col = getShaderParamteter(TEXTURE_PARAMETER_NAME);
        //getGl().glUniform
    }
}
