package osogl;

// https://github.com/processing/processing/wiki/Running-without-a-Display
// http://jogamp.org/deployment/jogamp-current/jar/
/*
apt-get install xvfb libxrender1 libxtst6 libxi6 
armbian-on: apt-get install libgl1-mesa-dri
pkill Xvfb
Xvfb :1 -screen 0 1024x768x24 </dev/null &
export DISPLAY=":1"
java -Xms512m -jar aaa.jar
 */
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class osr3_shaders {

    static int width = 5000;
    static int height = 5000;
    static int numPoints = 100;
    static Random r = new Random();

    public static void main(String[] args) throws IOException {
        GLProfile glp = GLProfile.get(GLProfile.GL2);
        //GLProfile glp = GLProfile.get(GLProfile.GLES1);
        //GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setOnscreen(false);

        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
        GLOffscreenAutoDrawable drawable = factory.createOffscreenAutoDrawable(null, caps, null, width, height);
        drawable.display();
        drawable.getContext().makeCurrent();

        GL2 gl = drawable.getGL().getGL2();

        String fc[] = new String[]{""
            + "uniform vec4 col;"
            + "void main()"
            + "{"
            + "  gl_FragColor = col;"
            + "}"
        };

        int fs2 = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fs2, 1, fc, null);
        gl.glCompileShader(fs2);
        int prog = gl.glCreateProgram();
        gl.glAttachShader(prog, fs2);
        gl.glLinkProgram(prog);
        gl.glValidateProgram(prog);

        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrtho(0d, width, height, 0d, -1d, 1d);

        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        gl.glUseProgram(prog);
        int col = gl.glGetUniformLocation(prog, "col");
        gl.glUniform4f(col, 0, 0, 1, 1);

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex2d(width / 4, height / 4);
        gl.glVertex2d(width / 2, height / 4);
        gl.glVertex2d(width / 4, height / 2);
        gl.glEnd();

        BufferedImage im = new AWTGLReadBufferUtil(drawable.getGLProfile(), false).readPixelsToBufferedImage(drawable.getGL(), 0, 0, width, height, true);
        ImageIO.write(im, "png", new File("im.png"));
    }
}
