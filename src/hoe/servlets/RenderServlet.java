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
import com.jogamp.opengl.util.glsl.ShaderUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import hoe.Log;
import hoe.SceneManager;
import hoe.editor.TimeElapseMeter;
import hoe.renderer.shaders.ConstantColorShader;
import hoe.renderer.shaders.ShaderManager;
import hoe.renderer.shaders.TextureShader;
import hoe.servers.AbstractServer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
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
    public static final double TILE_SIZE_IN_WORLD = 5d;
    public static final boolean RENDER_TILE_INFORMATION = true;
    public static final boolean RENDER_TILE_TURN_CENTER = !true;
    public static final boolean RENDER_TILE_BORDER = true;

    public RenderServlet(AbstractServer server) throws ServletException {
        super(server);
    }

    public static ShaderManager createShaders(GL2 gl) {
        ShaderManager shaders = new ShaderManager();

        shaders.put("color", new ConstantColorShader(gl));

        return shaders;
    }

    public static GL2 initGL(int tileSize, int multisample) {
        int size = tileSize * multisample;
        GLProfile glp = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setDepthBits(16);
        caps.setOnscreen(false);

        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
        GLOffscreenAutoDrawable drawable = factory.createOffscreenAutoDrawable(null, caps, null, size, size);
        drawable.display();
        drawable.getContext().makeCurrent();

        GL2 gl = drawable.getGL().getGL2();

        gl.glViewport(0, 0, size, size);

        return gl;
    }

    @Override
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, String apiKey, int requestType) throws IOException {

        TimeElapseMeter timer = new TimeElapseMeter(true);

        Thread t = new Thread(() -> {
            TileRequest tile;

            Log.debug("Rendering tiles...");

            GL2 gl = initGL(TILE_SIZE, TILE_MULTISAMPLE);
            GLU glu = new GLU();
            GLUT glut = new GLUT();
            ShaderManager shaders = createShaders(gl);

            try {
                while ((tile = SceneManager.markTileToRender()) != null) {

                    TimeElapseMeter meter = new TimeElapseMeter();

                    int x = tile.getX();
                    int y = tile.getY();
                    long turn = tile.getTurn();
                    long frame = tile.getFrame();

                    // Rendering the tile
                    BufferedImage image = renderTile(gl, glu, glut, shaders, null, x, y, turn, frame, SAMPLE_SIZE, TILE_SIZE_IN_WORLD);

                    // Multisampling
                    image = multisampleImage(image, TILE_SIZE);

                    if (RENDER_TILE_INFORMATION || RENDER_TILE_BORDER || RENDER_TILE_TURN_CENTER) {
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
                            Font font = new Font("Courier New", Font.PLAIN, fontSize);

                            if (RENDER_TILE_TURN_CENTER) {
                                drawCenteredString(g, "" + turn, new Rectangle(TILE_SIZE, TILE_SIZE), font);
                            } else {

                                g.setFont(font);

                                g.drawString("x=" + x, 10, (int) (fontSize * 1.1));
                                g.drawString("y=" + y, 10, (int) (fontSize * 2.2));
                                g.drawString("t=" + turn, 10, (int) (fontSize * 3.4));
                                g.drawString("f=" + frame, 10, (int) (fontSize * 4.6));
                            }
                        }
                        g.dispose();
                    }
                    try {
                        // Update tile in database.
                        long renderTime = meter.stopAndGet();
                        SceneManager.updateTile(turn, frame, x, y, image, renderTime);
                    } catch (SQLException | IOException ex) {
                        Log.error(ex);
                    }
                }
            } catch (SQLException ex) {
                Log.error(ex);

                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            Log.debug("Rendering tiles [" + getServer().getIp() + ":" + getServer().getPort() + "] has been finished [it took " + timer.stopAndGetFormat() + "].");
        });
        t.start();

        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static BufferedImage multisampleImage(BufferedImage image, int size) {

        if (image.getWidth() == size && image.getHeight() == size) {
            return image;
        }

        BufferedImage im2 = new BufferedImage(size, size, image.getType());
        im2.getGraphics().drawImage(((Image) image).getScaledInstance(size, size, Image.SCALE_AREA_AVERAGING), 0, 0, null);

        return im2;
    }

    public static void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public static int createDepthShader(GL2 gl) {

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

    public static BufferedImage renderTile(GL2 gl, GLU glu, GLUT glut, ShaderManager shaders, Runnable scene, int row, int column, long turn, long frame, int tileSizeInPixels, double tileSizeInOrtho) {

        int x = row;
        int y = -column;

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        double h = tileSizeInOrtho / 2d;
        double ox = (double) x * tileSizeInOrtho;
        double oy = (double) y * tileSizeInOrtho;

        gl.glOrtho(ox - h, ox + h, oy - h, oy + h, -100d, 100d);
        glu.gluLookAt(0, 1, 1, 0, 0, 0, 0, 0, 1);

        gl.glClearColor(.15f, .15f, .15f, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        //gl.glEnable(GL2.GL_CULL_FACE);
        //gl.glCullFace(GL2.GL_BACK);

        if (scene == null) {
            renderScene(gl, glu, glut, shaders, turn, frame);
        } else {
            scene.run();
        }

        BufferedImage image = new AWTGLReadBufferUtil(gl.getGLProfile(), false).readPixelsToBufferedImage(gl, 0, 0, tileSizeInPixels, tileSizeInPixels, true);

        return image;
    }

    public static void renderScene(GL2 gl, GLU glu, GLUT glut, ShaderManager shaders, long turn, long frame) {

        // Rendering.
        // Rendering.
                    /*ConstantColorShader colorShader = new ConstantColorShader(gl);
         colorShader.apply(0, 0, 1, 1);*/
        String vc[] = new String[]{""
            + "varying vec3 N;"
            + "varying vec3 v;"
            + "void main(void)"
            + "{"
            + "   v = vec3(gl_ModelViewMatrix * gl_Vertex);"
            + "   N = normalize(gl_NormalMatrix * gl_Normal);"
            + "   gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;"
            + "}"};

        String fc[] = new String[]{""
            + "varying vec3 N;"
            + "varying vec3 v;"
            + ""
            + "const vec3 lightPos = vec3(10, 10, 40);"
            + "const vec4 ambientColor = vec4(vec3(1,0,0)*.1, 1);"
            + "const vec4 diffuseColor = vec4(vec3(1,0,0)*.6, 1);"
            + "const vec4 specColor = vec4(vec3(1)*1, 1);"
            + "const float shininess = 10;"
            + ""
            + "void main (void)"
            + "{"
            + "   vec3 L = normalize(lightPos - v);"
            + "   vec3 E = normalize(-v);"
            + "   vec3 R = normalize(-reflect(L,N));"
            + ""
            + "    vec4 spec = vec4(0);"
            + "    float intensity = max(dot(N,L), 0.0);"
            + "    if (intensity > 0.0) {"
            + "        vec3 H = normalize(L + E);"
            + "        float intSpec = max(dot(H,N), 0.0);"
            + "        spec = specColor * pow(intSpec, shininess);"
            + "    }"
            + " "
            + "    gl_FragColor = max(intensity * diffuseColor + spec, ambientColor);"
            + "}"};

        int vs = gl.glCreateShader(GL2.GL_VERTEX_SHADER);

        gl.glShaderSource(vs, 1, vc, null);
        gl.glCompileShader(vs);

        int fs = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fs, 1, fc, null);
        gl.glCompileShader(fs);
        
        int progID = gl.glCreateProgram();
        gl.glAttachShader(progID, fs);
        gl.glAttachShader(progID, vs);
        gl.glLinkProgram(progID);
        gl.glValidateProgram(progID);
        gl.glUseProgram(progID);
        gl.glPushMatrix();

        gl.glTranslated(0, 0, 1.5);
        gl.glRotated(turn*15, 0, 0, 1);
        glut.glutSolidTeapot(3, false);
                    //glut.glutSolidSphere(3, 30, 30);
        //glut.glutSolidTorus(1, 3, 40, 40);
        gl.glPopMatrix();

        TextureShader textureShader = new TextureShader(gl);

        textureShader.apply();

        Texture texture = RenderServlet.createCheckerTexture(gl, 512, 4, Color.red, Color.green);
        Texture texture2 = RenderServlet.createCircleTexture(gl, 1024, Color.lightGray);

        textureShader.setTextures(texture, texture2);

        gl.glPushMatrix();
        gl.glRotated(15, 0, 0, 1);
        gl.glBegin(GL2.GL_QUADS);
        double s = 6;
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(-s, -s, 0);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(s, -s, 0);
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(s, s, 0);
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(-s, s, 0);
        gl.glEnd();
        gl.glPopMatrix();

    }

    public static Texture createCheckerTexture(GL2 gl, int textureSize, int squareCount, Color color1, Color color2) {
        int squareSize = textureSize / squareCount;
        BufferedImage img = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color1);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(color2);
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

    public static Texture createCircleTexture(GL2 gl, int textureSize, Color color) {
        BufferedImage img = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillOval(0, 0, img.getWidth(), img.getHeight());
        g.dispose();
        Texture t = AWTTextureIO.newTexture(GLProfile.getDefault(), img, true);
        t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        return t;
    }

}
