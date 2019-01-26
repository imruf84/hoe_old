package osogl;

// https://github.com/processing/processing/wiki/Running-without-a-Display
// http://jogamp.org/deployment/jogamp-current/jar/
/*
 apt-get install xvfb libxrender1 libxtst6 libxi6 
 armbian-on: apt-get install libgl1-mesa-dri
 pkill Xvfb
 Xvfb :1 -screen 0 1024x768x24 </dev/null &
 export DISPLAY=":1"
 java -Xms512m -jar osogl.jar imageSize samples
 */
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class osogl {

    static int samples = 4;
    static int size = 1000;
    static GLUT glut = new GLUT();
    static GLU glu = new GLU();

    public static void main(String[] args) throws IOException {

        size = Integer.parseInt(args[0]);
        samples = Integer.parseInt(args[1]);

        int sampleSize = size * samples;

        GLProfile glp = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setOnscreen(false);

        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
        GLOffscreenAutoDrawable drawable = factory.createOffscreenAutoDrawable(null, caps, null, sampleSize, sampleSize);
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

        gl.glViewport(0, 0, sampleSize, sampleSize);

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrtho(-10, 10, -10, 10, -100d, 100d);
        glu.gluLookAt(10, 10, 10, 0, 0, 0, 0, 0, 1);

        gl.glClearColor(.15f, .15f, .15f, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        gl.glUseProgram(prog);
        int col = gl.glGetUniformLocation(prog, "col");
        gl.glUniform4f(col, 0, 0, 1, 1);

        gl.glBegin(GL2.GL_QUADS);
        double s = 7;
        gl.glVertex3d(-s, -s, 0);
        gl.glVertex3d(s, -s, 0);
        gl.glVertex3d(s, s, 0);
        gl.glVertex3d(-s, s, 0);
        gl.glEnd();

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[]{.1f, .1f, .1f, 0}, 0);

        gl.glEnable(GL2.GL_LIGHT0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{.4f, .4f, .4f}, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{1000, 1000, 1000}, 0);

        gl.glEnable(GL2.GL_LIGHT1);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[]{.3f, .3f, .3f}, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[]{-1000, 1000, 1000}, 0);

        gl.glEnable(GL2.GL_LIGHT2);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, new float[]{.1f, .1f, .1f}, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, new float[]{0, -1000, 10}, 0);

        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, new float[]{1, 1, 1}, 0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{1, 1, 1}, 0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 100f);

        gl.glUseProgram(0);

        gl.glEnable(GL2.GL_TEXTURE_2D);

        int textureSize = 200;
        int squareCount = 2;
        int squareSize = textureSize/squareCount;
        BufferedImage img = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.red);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.green);
        for (int i = 0; i < squareCount; i++) {
            for (int j = i % 2; j < squareCount; j += 2) {
                g.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
            }
        }
        g.dispose();
        Texture texture = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        texture.enable(gl);

        glut.glutSolidTeapot(4, false);

        BufferedImage im = new AWTGLReadBufferUtil(drawable.getGLProfile(), false).readPixelsToBufferedImage(drawable.getGL(), 0, 0, sampleSize, sampleSize, true);

        if (samples != 1) {
            BufferedImage im2 = new BufferedImage(size, size, im.getType());
            im2.getGraphics().drawImage(((Image) im).getScaledInstance(size, size, Image.SCALE_AREA_AVERAGING), 0, 0, null);
            im = im2;
        }

        ImageIO.write(im, "png", new File("im.png"));
    }
}
