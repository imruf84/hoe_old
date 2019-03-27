package hoe.servlets;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import hoe.Log;
import hoe.SceneManager;
import hoe.editor.TimeElapseMeter;
import hoe.servers.AbstractServer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderServlet extends HttpServletWithApiKeyValidator {

    public static final int TILE_MULTISAMPLE = 1;
    public static final int TILE_SIZE = 500;
    public static final int SAMPLE_SIZE = TILE_SIZE * TILE_MULTISAMPLE;
    public static final double TILE_SIZE_IN_WORLD = 2.5d;
    public static final boolean RENDER_TILE_INFORMATION = true;
    public static final boolean RENDER_TILE_BORDER = !true;

    private GLUT glut = null;
    private GLU glu = null;
    private GL2 gl = null;
    private Texture texture;
    private int colorShader;
    private int depthShader;

    public RenderServlet(AbstractServer server) throws ServletException {
        super(server);
    }

    private void initGL(int size) {
        GLProfile glp = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setDepthBits(16);
        caps.setOnscreen(false);

        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
        GLOffscreenAutoDrawable drawable = factory.createOffscreenAutoDrawable(null, caps, null, size, size);
        drawable.display();
        drawable.getContext().makeCurrent();

        gl = drawable.getGL().getGL2();
        glut = new GLUT();
        glu = new GLU();

        gl.glViewport(0, 0, SAMPLE_SIZE, SAMPLE_SIZE);
        depthShader = createDepthShader(gl);
        colorShader = createConstantColorShader(gl);
        texture = createCheckerTexture(gl, 200, 2);
    }

    @Override
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, String apiKey, int requestType) throws IOException {

        TimeElapseMeter timer = new TimeElapseMeter(true);

        Thread t = new Thread(() -> {
            TileRequest tile;

            Log.debug("Rendering tiles...");

            initGL(SAMPLE_SIZE);

            try {
                while ((tile = SceneManager.markTileToRender()) != null) {

                    int x = tile.getX();
                    int y = tile.getY();
                    long turn = tile.getTurn();
                    long frame = tile.getFrame();

                    // Rendering the tile
                    BufferedImage image = renderTile(gl, -y, x, turn, frame, SAMPLE_SIZE, TILE_SIZE_IN_WORLD);

                    // Multisampling
                    if (TILE_MULTISAMPLE != 1) {
                        BufferedImage im2 = new BufferedImage(TILE_SIZE, TILE_SIZE, image.getType());
                        im2.getGraphics().drawImage(((Image) image).getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_AREA_AVERAGING), 0, 0, null);
                        image = im2;
                    }

                    if (RENDER_TILE_INFORMATION || RENDER_TILE_BORDER) {
                        Graphics2D g = (Graphics2D) image.getGraphics();
                        if (RENDER_TILE_BORDER) {
                            g.setColor(Color.red);
                            g.drawRect(0, 0, TILE_SIZE - 1, TILE_SIZE - 1);
                        }
                        if (RENDER_TILE_INFORMATION) {
                            g.setColor(Color.white);
                            try (InputStream mainFontIn = getClass().getClassLoader().getResourceAsStream("fonts/cour.ttf")) {
                                Font mainFont = Font.createFont(Font.TRUETYPE_FONT, mainFontIn);
                                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                                ge.registerFont(mainFont);
                            } catch (IOException | FontFormatException e) {
                                Log.error(e);
                            }
                            int fontSize = 100;
                            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setFont(new Font("Courier New", Font.PLAIN, fontSize));

                            //g.drawString("x=" + x, 10, (int) (fontSize * 1.1));
                            //g.drawString("y=" + y, 10, (int) (fontSize * 2.2));
                            //g.drawString("t=" + turn, 10, (int) (fontSize * 3.4));
                            g.drawString(""+turn, 10, (int) (fontSize * 3.4));
                            //g.drawString("f=" + frame, 10, (int) (fontSize * 4.6));
                        }
                        g.dispose();
                    }
                    try {
                        // Update tile in database.
                        SceneManager.updateTile(turn, frame, x, y, image);
                    } catch (SQLException | IOException ex) {
                        Log.error(ex);
                    }
                }
            } catch (SQLException ex) {
                Log.error(ex);

                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            Log.debug("Rendering tiles [" + getServer().getIp() + ":" + getServer().getPort() + "] has been finished [it took " + timer.stopAndGet() + "].");
        });
        t.start();

        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private int createConstantColorShader(GL2 gl) {
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

    private int createDepthShader(GL2 gl) {

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

    private BufferedImage renderTile(GL2 gl, int row, int column, long turn, long frame, int tileSizeInPixels, double tileSizeInOrtho) {

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        double h = tileSizeInOrtho / 2d;
        double ox = (double) column * tileSizeInOrtho;
        double oy = (double) row * tileSizeInOrtho;

        gl.glOrtho(ox - h, ox + h, oy - h, oy + h, -100d, 100d);
        glu.gluLookAt(0, 1, 1, 0, 0, 0, 0, 0, 1);

        gl.glClearColor(.15f, .15f, .15f, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        renderScene(gl, turn, frame);

        BufferedImage image = new AWTGLReadBufferUtil(gl.getGLProfile(), false).readPixelsToBufferedImage(gl, 0, 0, tileSizeInPixels, tileSizeInPixels, true);

        return image;
    }

    private void renderScene(GL2 gl, long turn, long frame) {

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

        gl.glPushMatrix();
        gl.glRotated(turn * 4, 0, 0, 1);
        gl.glUseProgram(depthShader);
        glut.glutSolidTeapot(4, false);
        gl.glPopMatrix();

        gl.glUseProgram(0);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        texture.enable(gl);
        gl.glPushMatrix();
        gl.glTranslated(-5, 5, 0);
        gl.glScaled(.5, .5, .5);
        gl.glRotated(-turn * 2, 0, 0, 1);
        glut.glutSolidTeapot(4, false);
        gl.glPopMatrix();
    }

    private Texture createCheckerTexture(GL2 gl, int textureSize, int squareCount) {
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
