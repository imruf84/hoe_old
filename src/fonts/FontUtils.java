package fonts;

import hoe.Log;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontUtils {

    public static Font registerFont() {
        try (InputStream mainFontIn = FontUtils.class.getClassLoader().getResourceAsStream("fonts/cour.ttf")) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, mainFontIn);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            
            return font;
        } catch (IOException | FontFormatException e) {
            Log.error(e);
        }
        
        return null;
    }

}
