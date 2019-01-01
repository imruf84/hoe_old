package hoe.physics;

public class Transform extends Matrix3D {

    public Transform() {
        super(true);
    }
    
    public final Transform translate(double tx, double ty, double tz)
    {
        m[ 3] += m[ 0]*tx + m[ 1]*ty + m[ 2]*tz;
        m[ 7] += m[ 4]*tx + m[ 5]*ty + m[ 6]*tz;
        m[11] += m[ 8]*tx + m[ 9]*ty + m[10]*tz;
        m[15] += m[12]*tx + m[13]*ty + m[14]*tz;
        
        return this;
    }

    public final Transform scale(double sx, double sy, double sz)
    {
        m[ 0] *= sx; m[ 1] *= sy; m[ 2] *= sz;
        m[ 4] *= sx; m[ 5] *= sy; m[ 6] *= sz;
        m[ 8] *= sx; m[ 9] *= sy; m[10] *= sz;
        m[12] *= sx; m[13] *= sy; m[14] *= sz;
        
        return this;
    }

    public final Transform rotate(double ax, double ay, double az) {
        rotate(1, 0, 0, ax);
        rotate(0, 1, 0, ay);
        rotate(0, 0, 1, az);
        
        return this;
    }
    
    public final Transform rotate(double ax, double ay, double az, double angle)
    {
        double t0, t1, t2;

        if (angle == 0) return this;          // return with m unmodified

        t0 = ax*ax + ay*ay + az*az;
        if (t0 == 0) return this;

        double cosx = (double) Math.cos(angle);
        double sinx = (double) Math.sin(angle);
        t0 = 1f / ((double) Math.sqrt(t0));
        ax *= t0;
        ay *= t0;
        az *= t0;
        t0 = 1f - cosx;

        double r11 = ax*ax*t0 + cosx;
        double r22 = ay*ay*t0 + cosx;
        double r33 = az*az*t0 + cosx;

        t1 = ax*ay*t0;
        t2 = az*sinx;
        double r12 = t1 - t2;
        double r21 = t1 + t2;

        t1 = ax*az*t0;
        t2 = ay*sinx;
        double r13 = t1 + t2;
        double r31 = t1 - t2;

        t1 = ay*az*t0;
        t2 = ax*sinx;
        double r23 = t1 - t2;
        double r32 = t1 + t2;

        for (int i = 0; i < 16; i += 4) {
            t0 = m[i];
            t1 = m[i+1];
            t2 = m[i+2];
            m[i  ] = t0*r11 + t1*r21 + t2*r31;
            m[i+1] = t0*r12 + t1*r22 + t2*r32;
            m[i+2] = t0*r13 + t1*r23 + t2*r33;
        }
        
        return this;
    }

    public Transform lookAt(double eyex, double eyey, double eyez,
                       double atx,  double aty,  double atz,
                       double upx,  double upy,  double upz)
    {
        double t0, t1, t2;

        /*
            .... a unit vector along the line of sight ....
        */
        atx -= eyex;
        aty -= eyey;
        atz -= eyez;

        t0 = atx*atx + aty*aty + atz*atz;
        if (t0 == 0) return this;                // at and eye at same point
        t0 = (double) (1 / Math.sqrt(t0));
        atx *= t0;
        aty *= t0;
        atz *= t0;

        /*
            .... a unit vector to the right ....
        */
        double rightx, righty, rightz;
        rightx = aty*upz - atz*upy;
        righty = atz*upx - atx*upz;
        rightz = atx*upy - aty*upx;
        t0 = rightx*rightx + righty*righty + rightz*rightz;
        if (t0 == 0) return this;                // up is the same as at
        t0 = (double) (1 / Math.sqrt(t0));
        rightx *= t0;
        righty *= t0;
        rightz *= t0;


        /*
            .... a unit up vector ....
        */
        upx = righty*atz - rightz*aty;
        upy = rightz*atx - rightx*atz;
        upz = rightx*aty - righty*atx;


        /*
            .... find camera translation ....
        */
        double tx, ty, tz;
        tx = rightx*eyex + righty*eyey + rightz*eyez;
        ty = upx*eyex + upy*eyey + upz*eyez;
        tz = atx*eyex + aty*eyey + atz*eyez;

        /*
            .... do transform ....
        */
        for (int i = 0; i < 16; i += 4) {
            t0 = m[i];
            t1 = m[i+1];
            t2 = m[i+2];
            m[i  ] = t0*rightx + t1*upx - t2*atx;
            m[i+1] = t0*righty + t1*upy - t2*aty;
            m[i+2] = t0*rightz + t1*upz - t2*atz;
            m[i+3] -= t0*tx + t1*ty - t2*tz;
        }
        
        return this;
    }

    public Transform perspective(double left, double right,
                            double bottom, double top,
                            double near, double far)
    {
        double t0, t1, t2, t3;

        t0 = 1f / (right - left);
        t1 = 1f / (bottom - top);
        t2 = 1f / (far - near);

        double m13 = -t0*(right + left);
        double m23 = -t1*(bottom + top);
        double m33 = t2*(far + near);

        near *= 2;
        double m11 = t0*near;
        double m22 = t1*near;
        double m34 = -t2*far*near;

        for (int i = 0; i < 16; i += 4) {
            t0 = m[i];
            t1 = m[i+1];
            t2 = m[i+2];
            m[i  ] = t0*m11;
            m[i+1] = t1*m22;
            m[i+2] = t0*m13 + t1*m23 + t2*m33 + m[i+3];
            m[i+3] = t2*m34;
        }
        
        return this;
    }
    
    public Transform orthographic(double left, double right,
                             double bottom, double top,
                             double near, double far)
    {
        double t0, t1, t2, t3;

        t0 = 1f / (right - left);
        t1 = 1f / (bottom - top);
        t2 = 1f / (far - near);

        double m11 = 2*t0;
        double m22 = 2*t1;
        double m33 = 2*t2;
        double m14 = -t0*(right + left);
        double m24 = -t1*(bottom + top);
        double m34 = -t2*(far + near);

        for (int i = 0; i < 16; i += 4) {
            t0 = m[i];
            t1 = m[i+1];
            t2 = m[i+2];
            m[i  ] = t0*m11;
            m[i+1] = t1*m22;
            m[i+2] = t2*m33;
            m[i+3] = t0*m14 + t1*m24 + t2*m34 + m[i+3];
        }
        
        return this;
    }
    
    public Transform viewport(double xTopLeft, double yTopLeft, double w, double h) {
        return this;
    }
    
}
