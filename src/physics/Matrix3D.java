package physics;

public class Matrix3D {
    protected final double m[];

    public Matrix3D()
    {
        m = new double[16];
    }
    
    public Matrix3D(boolean identity)
    {
        m = new double[16];
        if (identity) {
            loadIdentity();
        }
    }


    public Matrix3D(Matrix3D copy)    // makes a copy of the matrix
    {
        m = new double[16];
        System.arraycopy(copy.getArray(), 0, m, 0, 16);
    }


    /*
        ... Methods for setting and getting matrix elements ...
    */
    protected double [] getArray() {
        return m;
    }

    public void set(int j, int i, double val)
    {
        m[4*j+i] = val;
    }

    public double get(int j, int i)
    {
        return m[4*j+i];
    }

    protected void set(int i, double val)
    {
        m[i] = val;
    }
    
    protected double get(int i)
    {
        return m[i];
    }


    public final void copy(Matrix3D src)
    {
        System.arraycopy(src.getArray(), 0, m, 0, 16);
    }

    public void transform(Vector3D in[], Vector3D out[], int vertices) {
        for (int i = 0; i < vertices; i++) {
            out[i].x = m[0]*in[i].x + m[1]*in[i].y + m[2]*in[i].z + m[3]*1;
            out[i].y = m[4]*in[i].x + m[5]*in[i].y + m[6]*in[i].z + m[7]*1;
            out[i].z = m[8]*in[i].x + m[9]*in[i].y + m[10]*in[i].z + m[11]*1;
            //out[i].w = m[12]*in[i].x + m[13]*in[i].y + m[14]*in[i].z + m[15]*in[i].w;
        }
    }

    public Vector3D transform(Vector3D v)
    {
        double x, y, z, w;
        x = m[0]*v.x + m[1]*v.y + m[2]*v.z + m[3]*1;
        y = m[4]*v.x + m[5]*v.y + m[6]*v.z + m[7]*1;
        z = m[8]*v.x + m[9]*v.y + m[10]*v.z + m[11]*1;
        w = m[12]*v.x + m[13]*v.y + m[14]*v.z + m[15]*1;

        w = 1 / w;
        Vector3D result = new Vector3D(x*w, y*w, z*w);
        
        return result;
    }

    public final void compose(Matrix3D s)
    {
        double t0, t1, t2, t3;
        for (int i = 0; i < 16; i += 4) {
            t0 = m[i  ];
            t1 = m[i+1];
            t2 = m[i+2];
            t3 = m[i+3];
            m[i  ] = t0*s.get(0) + t1*s.get(4) + t2*s.get( 8) + t3*s.get(12);
            m[i+1] = t0*s.get(1) + t1*s.get(5) + t2*s.get( 9) + t3*s.get(13);
            m[i+2] = t0*s.get(2) + t1*s.get(6) + t2*s.get(10) + t3*s.get(14);
            m[i+3] = t0*s.get(3) + t1*s.get(7) + t2*s.get(11) + t3*s.get(15);
        }
    }

    public final void loadIdentity()
    {
        for (int i = 0; i < 16; i++)
            if ((i >> 2) == (i & 3))
                m[i] = 1;
            else
                m[i] = 0;
    }
    
    

    @Override
    public String toString()
    {
        return ("[ ["+m[ 0]+", "+m[ 1]+", "+m[ 2]+", "+m[ 3]+" ],\n  ["+
                      m[ 4]+", "+m[ 5]+", "+m[ 6]+", "+m[ 7]+" ],\n  ["+
                      m[ 8]+", "+m[ 9]+", "+m[10]+", "+m[11]+" ],\n  ["+
                      m[12]+", "+m[13]+", "+m[14]+", "+m[15]+" ] ]\n");
    }
}