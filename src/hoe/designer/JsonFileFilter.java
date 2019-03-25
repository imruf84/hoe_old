package hoe.designer;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class JsonFileFilter extends FileFilter {

    public static final String JSON_FILE_EXTENSION = ".json";
    
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getPath().toLowerCase().endsWith(JSON_FILE_EXTENSION);
    }

    @Override
    public String getDescription() {
        return "JSON Object File";
    }
}
