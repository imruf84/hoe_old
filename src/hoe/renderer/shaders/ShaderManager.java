package hoe.renderer.shaders;

import com.jogamp.opengl.GL2;
import java.util.HashMap;

public class ShaderManager extends HashMap<String, ShaderBase> {
    public static final String CONSTANT_COLOR_SHADER = "color";
    public static final String PHONG_SHADER = "phong";
    
    public void destroyShaders(GL2 gl) {
        for (String key : keySet()) {
            ShaderBase shader = get(key);
            shader.delete();
        }
        clear();
    }
}
