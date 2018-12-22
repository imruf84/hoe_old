package hoe.editor;

public class TimeUtils {

    public static double elapsedTime = 0;
    public static double timeUnit = 1;
    public static double timeUnitLeft = 0;
    public static double timeSteps = 10;

    public static double getDeltaTime() {
        return timeUnit / timeSteps;
    }
}
