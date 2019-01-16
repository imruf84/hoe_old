package osogl;

// https://github.com/processing/processing/wiki/Running-without-a-Display
/*
sudo apt-get install xvfb libxrender1 libxtst6 libxi6 
sudo Xvfb :1 -screen 0 1024x768x24 </dev/null &
export DISPLAY=":1"
java -jar aaa.jar
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

public class osr2 {

    static int width = 5000; 
    static int height = 5000; 
    static int numPoints = 100; 
    static Random r = new Random(); 
    
    public static void main3(String[] args) throws IOException {
        //GLProfile glp = GLProfile.getDefault();
        GLProfile glp = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setOnscreen(false);
        //caps.setFBO(true);
        //caps.setPBuffer(true);
        //caps.setDoubleBuffered(false);

// create the offscreen drawable
        GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
        GLOffscreenAutoDrawable drawable = factory.createOffscreenAutoDrawable(null, caps, null, width, height);
        /*
        drawable.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable glad) {
                drawable.getContext().makeCurrent();
                System.out.println("init");
            }

            @Override
            public void dispose(GLAutoDrawable glad) {
            }

            @Override
            public void display(GLAutoDrawable glad) {
            }

            @Override
            public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
            }
        });*/
        
        drawable.display();
        drawable.getContext().makeCurrent();

        GL2 gl = drawable.getGL().getGL2(); 

        gl.glViewport(0, 0, width, height); 

        // use pixel coordinates 
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); 
        gl.glLoadIdentity(); 

        gl.glOrtho(0d, width, height, 0d, -1d, 1d); 
        
        gl.glClearColor(1,1,1,1); 
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        
        gl.glColor3f(1f, 0f, 1f);
        
        /*gl.glEnableClientState(GL2.GL_VERTEX_ARRAY); 
        gl.glVertexPointer(2, GL2.GL_FLOAT, 0, buffer); 
        gl.glDrawArrays(GL2.GL_POINTS, 0, numPoints); 
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY); */
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex2d(width/4, height/4);
        gl.glVertex2d(width/2, height/4);
        gl.glVertex2d(width/4, height/2);
        gl.glEnd();

        BufferedImage im = new AWTGLReadBufferUtil(drawable.getGLProfile(), false).readPixelsToBufferedImage(drawable.getGL(), 0, 0, width, height, true /* awtOrientation */);
        ImageIO.write(im, "png", new File("im.png"));
    }
}
