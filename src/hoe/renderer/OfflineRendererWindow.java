package hoe.renderer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import hoe.renderer.shaders.ShaderManager;
import hoe.servlets.RenderServlet;
import static hoe.servlets.RenderServlet.renderTile;
import java.awt.image.BufferedImage;

public class OfflineRendererWindow extends ImageViewer {

    private final RenderCallback render;

    public OfflineRendererWindow(int tileSize, int multisample, double tileWorldSize, int rows[], int columns[], RenderCallback render) {
        super(new BufferedImage((columns[1] - columns[0] + 1) * tileSize, (rows[1] - rows[0] + 1) * tileSize, BufferedImage.TYPE_INT_RGB));
        
        this.render = render;

        GL2 gl = RenderServlet.initGL(tileSize, multisample);
        GLU glu = new GLU();
        GLUT glut = new GLUT();
        ShaderManager shaders = RenderServlet.createShaders(gl);

        BufferedImage result = getImage();
        
        setVisible(true);

        int x = 0;
        int y = 0;
        for (int row = rows[1]; row >= rows[0]; row--) {
            for (int column = columns[0]; column <= columns[1]; column++) {

                // Rendering the tile
                BufferedImage im = renderTile(gl, glu, glut, shaders, new RenderCallback() {
                    
                    @Override
                    public void render() {
                        getRender().init(gl, glu, glut);
                        getRender().run();
                    }
                }, column, -row, 0, 0, tileSize * multisample, tileWorldSize);

                // Multisampling
                im = RenderServlet.multisampleImage(im, tileSize);

                // Composing.
                result.getGraphics().drawImage(im, x, y, null);
                repaint();
                x += tileSize;
            }
            x = 0;
            y += tileSize;
        }
    }

    public final RenderCallback getRender() {
        return render;
    }

}
