package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import hoe.servlets.RenderServlet;
import java.awt.Color;

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

    public void setTextures(Texture texture, Texture texture2) {
        GL2 gl = getGl();
        
        getGl().glActiveTexture(GL2.GL_TEXTURE1);
        
        gl.glUniform1i(gl.glGetUniformLocation(getProgram(), "tex"), 0);
        gl.glActiveTexture(GL2.GL_TEXTURE0);
        texture.bind(gl);

        gl.glUniform1i(gl.glGetUniformLocation(getProgram(), "tex2"), 1);
        gl.glActiveTexture(GL2.GL_TEXTURE1);
        texture2.bind(gl);
        
        getGl().glActiveTexture(GL2.GL_TEXTURE0);
    }
}
