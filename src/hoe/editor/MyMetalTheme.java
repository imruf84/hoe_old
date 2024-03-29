package hoe.editor;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class MyMetalTheme extends DefaultMetalTheme {

    @Override
    protected ColorUIResource getPrimary1() {
        return new ColorUIResource(50, 50, 50);
    }

    @Override
    protected ColorUIResource getPrimary2() {
        return new ColorUIResource(150, 150, 150);
    }

    @Override
    public ColorUIResource getTextHighlightColor() {
        return new ColorUIResource(150, 150, 150);
    }
}
