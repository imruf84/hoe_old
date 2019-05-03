package osogl;

// https://github.com/processing/processing/wiki/Running-without-a-Display
// http://jogamp.org/deployment/jogamp-current/jar/
/*
 apt-get install xvfb libxrender1 libxtst6 libxi6 
 armbian-on: apt-get install libgl1-mesa-dri
 pkill Xvfb
 Xvfb :1 -screen 0 1024x768x24 </dev/null &
 export DISPLAY=":1"
 java -Xms512m -jar osogl.jar
 */
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class osogl {

    static GLUT glut = new GLUT();
    static GLU glu = new GLU();
    private static Texture texture;
    private static int colorShader;
    private static int depthShader;

    public static void main(String[] args) throws IOException {

        int multiSample = 1;
        int tileSize = 500;
        int sampleSize = tileSize * multiSample;

        GL2 gl = initGL(sampleSize);
        gl.glViewport(0, 0, sampleSize, sampleSize);
        depthShader = createDepthShader(gl);
        colorShader = createConstantColorShader(gl);
        texture = createCheckerTexture(gl, 200, 2);

        int rows[] = {-2, 2};
        int columns[] = {-4, 4};
        double tileSizeInWorld = 2.5d;

        int rowsCount = rows[1] - rows[0] + 1;
        int columnsCount = columns[1] - columns[0] + 1;

        BufferedImage result = new BufferedImage(columnsCount * tileSize, rowsCount * tileSize, BufferedImage.TYPE_INT_RGB);
//        ImageViewer iw = showImageInFrame(result);

        int x = 0;
        int y = 0;
        for (int row = rows[1]; row >= rows[0]; row--) {
            for (int column = columns[0]; column <= columns[1]; column++) {

                // Rendering the tile
                BufferedImage im = renderTile(gl, row, column, sampleSize, tileSizeInWorld);

                // Multisampling
                if (multiSample != 1) {
                    BufferedImage im2 = new BufferedImage(tileSize, tileSize, im.getType());
                    im2.getGraphics().drawImage(((Image) im).getScaledInstance(tileSize, tileSize, Image.SCALE_AREA_AVERAGING), 0, 0, null);
                    im = im2;
                }

                // Saving tile to file
                ImageIO.write(im, "png", new File("images/img_" + column + "_" + row + ".png"));
                // Composing.
                result.getGraphics().drawImage(im, x, y, null);
//                iw.repaint();
                x += tileSize;
            }
            x = 0;
            y += tileSize;
        }

    }

    static BufferedImage normalize(BufferedImage im) {
        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int x = 0; x < im.getWidth(); x++) {
            for (int y = 0; y < im.getHeight(); y++) {
                int c = new Color(im.getRGB(x, y)).getRed();
                if (c > max) {
                    max = c;
                }
                if (c < min) {
                    min = c;
                }
            }
        }

        for (int x = 0; x < im.getWidth(); x++) {
            for (int y = 0; y < im.getHeight(); y++) {
                double c = new Color(im.getRGB(x, y)).getRed();
                double t = (c - min) / (max - min);
                int cc = (int) (t * 255);
                if (c > 0) {
                    im.setRGB(x, y, new Color(cc, cc, cc).getRGB());
                }
            }
        }

        return im;
    }

    static ImageViewer showImageInFrame(BufferedImage image) {
        ImageViewer iw = new ImageViewer(image);
        iw.setTitle(iw.getTitle() + " - size[" + image.getWidth() + "x" + image.getHeight() + "]");
        iw.setVisible(true);
        return iw;
    }

    static GL2 initGL(int size) {
        GLProfile glp = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setDepthBits(16);
        caps.setOnscreen(false);

        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
        GLOffscreenAutoDrawable drawable = factory.createOffscreenAutoDrawable(null, caps, null, size, size);
        drawable.display();
        drawable.getContext().makeCurrent();

        return drawable.getGL().getGL2();
    }

    static int createConstantColorShader(GL2 gl) {
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
        int shader = gl.glCreateProgram();
        gl.glAttachShader(shader, fs2);
        gl.glLinkProgram(shader);
        gl.glValidateProgram(shader);

        return shader;
    }

    static int createDepthShader(GL2 gl) {

        String fc[] = new String[]{""
            + "uniform vec4 col;"
            + "void main()"
            + "{"
            + " float ndcDepth = (2.0 * gl_FragCoord.z - gl_DepthRange.near - gl_DepthRange.far) / (gl_DepthRange.far - gl_DepthRange.near); "
            + " float clipDepth = ndcDepth / gl_FragCoord.w; "
            //+ " gl_FragColor = vec4((clipDepth * 0.5) + 0.5); "
            + " gl_FragColor = vec4(gl_FragCoord.z);"
            + "}"
        };

        int fs2 = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fs2, 1, fc, null);
        gl.glCompileShader(fs2);
        int shader = gl.glCreateProgram();
        gl.glAttachShader(shader, fs2);
        gl.glLinkProgram(shader);
        gl.glValidateProgram(shader);

        return shader;
    }

    static BufferedImage renderTile(GL2 gl, int row, int column, int tileSizeInPixels, double tileSizeInOrtho) {

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        double h = tileSizeInOrtho / 2d;
        double ox = (double) column * tileSizeInOrtho;
        double oy = (double) row * tileSizeInOrtho;

        gl.glOrtho(ox - h, ox + h, oy - h, oy + h, -100d, 100d);
        //glu.gluLookAt(10, 10, 10, 0, 0, 0, 0, 0, 1);
        glu.gluLookAt(0, 1, 1, 0, 0, 0, 0, 0, 1);

        gl.glClearColor(.15f, .15f, .15f, 1);
        //gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        renderScene(gl);

        BufferedImage im = new AWTGLReadBufferUtil(gl.getGLProfile(), false).readPixelsToBufferedImage(gl, 0, 0, tileSizeInPixels, tileSizeInPixels, true);

        return im;
    }

    static void renderScene(GL2 gl) {

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        gl.glUseProgram(colorShader);
        int col = gl.glGetUniformLocation(colorShader, "col");
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

        gl.glUseProgram(depthShader);
        glut.glutSolidTeapot(4, false);

        gl.glUseProgram(0);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        texture.enable(gl);
        gl.glPushMatrix();
        gl.glTranslated(-5, 5, 0);
        gl.glScaled(.5, .5, .5);
        gl.glRotated(-25, 0, 0, 1);
        glut.glutSolidTeapot(4, false);
        gl.glPopMatrix();
    }

    static Texture createCheckerTexture(GL2 gl, int textureSize, int squareCount) {
        int squareSize = textureSize / squareCount;
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
        Texture t = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
        t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        return t;
    }
}
