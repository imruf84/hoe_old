package nlopt;

import prototype.CurvePoint;

public class PackerData {
    
    public double radius;
    public double maxStep;
    public CurvePoint previousPosition;
    public CurvePoint nextPosition;
    public double orientation;
    public boolean immovable = false;    
    public double step;
}
