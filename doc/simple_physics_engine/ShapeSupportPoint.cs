using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;


using System.IO;
using System.Diagnostics;
using System.Windows.Forms.Design;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

/*
public class Model_c
{
	public Shape										m_shape			= null;
	public HullMaker									m_hullMaker		= null;
	public Vector3										m_position		= Vector3.Zero;
	public Quaternion									m_rotation		= Quaternion.Identity;
	public Microsoft.Xna.Framework.Graphics.Color		m_colour		= Microsoft.Xna.Framework.Graphics.Color.Yellow;
}
*/
	
	
	
public class Shape
{
	public virtual Vector3		GetSupportPoint		(Vector3 n)		{ return Vector3.Zero;	}
	public virtual Vector3		GetCenter			()				{ return Vector3.Zero;	}
};



public class ShapePoint : Shape
{
	Vector3 m_point;
	
	public ShapePoint(Vector3 p)
	{
		m_point = p;
	}
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{ 
		return m_point;	
	}
};


public class ShapeSegment : Shape
{
	float m_length;
	
	public ShapeSegment(float length)
	{
		m_length = length;
	}
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
		if (n.X<0) return new Vector3(-m_length, 0, 0);
		return new Vector3(m_length, 0, 0); 
	}
};


public class ShapeRectanglePlane : Shape
{
	Vector3 m_radius;
	
	public ShapeRectanglePlane(float radiusX, float radiusZ)
	{
		m_radius = new Vector3(radiusX, 0, radiusZ);
	}
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
		Vector3 res = m_radius;
		if (n.X<0) res.X = -m_radius.X;
		if (n.Z<0) res.Z = -m_radius.Z;
		return res;
	}
};


public class ShapeCirclePlane : Shape
{
	float m_radius;
	
	public ShapeCirclePlane(float radius)
	{
		m_radius = radius;
	}
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
		Vector3 res = n;
		res.Y = 0.0f;
		if (res.LengthSquared() < 0.0001f) 
		{
			return Vector3.Zero;
		}
		
		res.Normalize();
		res = res * m_radius;
		return res;
	}
};

public class ShapeElipsePlane : Shape
{
	Vector3 m_radius;
	
	public ShapeElipsePlane(float radiusX, float radiusZ)
	{
		m_radius = new Vector3(radiusX, 0, radiusZ);
	}
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
		Vector3 res = n;
		res.Y = 0.0f;
		if (res.LengthSquared() < 0.0001f) 
		{
			return Vector3.Zero;
		}
		
		res.Normalize();
		res = new Vector3(res.X*m_radius.X,  0,  res.Z*m_radius.Z);
		return res;
	}
};


public class ShapeCube : Shape
{
	Vector3 m_radius;
	
	public ShapeCube(Vector3 radius)
	{
		m_radius = radius;
	}
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
		
		Vector3 res = m_radius;
		if (n.X<0) res.X = -m_radius.X;
		if (n.Y<0) res.Y = -m_radius.Y;
		if (n.Z<0) res.Z = -m_radius.Z;
		return res;
		
		
		/*
		// Centre is the origin - and we have 6 planes
		// which is 6 normals
		Vector3[] e = new Vector3[3]{ new Vector3(1,0,0), new Vector3(0,1,0), new Vector3(0,0,1) }; 
		float[] s = new float[2]{1.0f, -1.0f};
		Vector3 best = new Vector3(100000.0f, 10000000.0f, 1000000.0f);
		
		for (int k=0; k<2; k++)
		{
			for (int i=0; i<3; i++)
			{
				Vector3 planeN = s[k]*e[i];
				Vector3 planeP = s[k]*e[i]*m_radius;
				
				if (Vector3.Dot(planeN, n)==0.0f) continue;
				
				float t =  Vector3.Dot(planeN, planeP) /  Vector3.Dot(planeN, n*500.0f);
				
				if (t<0)    continue;
				if (t>1.0f) continue;
				
				Vector3 Px = t * 500.0f*n;
				
				if (Px.LengthSquared() < best.LengthSquared() )
				{
					best = Px;
				}
				

				//Px.X = MathHelper.Clamp(Px.X, -m_radius.X, m_radius.X);
				//Px.Y = MathHelper.Clamp(Px.Y, -m_radius.Y, m_radius.Y);
				//Px.Z = MathHelper.Clamp(Px.Z, -m_radius.Z, m_radius.Z);
				
				//return Px;
			}
		}
		
		if (best.LengthSquared()>0.0f)
		{
			return best;
		}
		
		return Vector3.Zero;
		*/
		
	}
};


public class ShapeSphere : Shape 
{
	float m_radius;
	
	public ShapeSphere(float radius)
	{
		m_radius = radius;
	}
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
		Vector3 res = n;
		res.Normalize();
		res *= m_radius;
		return res;
	}	
};


public class ShapeEllipsoid : Shape 
{
	Vector3	m_radius;
	
	public ShapeEllipsoid(Vector3 radius)
	{
		m_radius = radius;
	}
	
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
		Vector3 nn = Vector3.Normalize(n);
		
		//nn = new Vector3(1,1,1);
		//float t = nn.X*nn.X/(m_radius.X*m_radius.X) + nn.Y*nn.Y/(m_radius.Y*m_radius.Y) + nn.Z*nn.Z/(m_radius.Z*m_radius.Z);
		
		return nn * m_radius;
	}	
};


public class ShapeFootball : Shape 
{
	float	m_radius;
	float	m_length;
	
	public ShapeFootball(float radius, float length)
	{
		m_radius = radius;
		m_length = length;
	}
	
	public override Vector3		GetSupportPoint		(Vector3 n)		
	{
	
		// Radius
		float r = m_radius;

		// Half Length
		float h = m_length;

		// Radius of curvature
		float rc = 0.5f * (h*h/r + r);

		Vector3 norm = Vector3.Normalize(n);

		if (norm.X * rc < -h) return new Vector3(-h, 0, 0);
		if (norm.X * rc >  h) return new Vector3( h, 0, 0);

		Vector3 nr = new Vector3(0, norm.Y, norm.Z);
		nr.Normalize();
		
		Vector3 p = -nr*(rc-r) + norm*rc;
		return p;
	}	
};


public class ShapeCyclinder : Shape 
{
	float	m_radius;
	float	m_length;
	
	public ShapeCyclinder(float radius, float length)
	{
		m_radius = radius;
		m_length = length;
	}
	
	public override Vector3		GetSupportPoint		(Vector3 d)		
	{
		// radius r
		// half height n
		
		float r = m_radius;
		
		float n = m_length;
		
		Vector3 u = new Vector3(0,1,0);
		
		Vector3 w = d - Vector3.Dot(u, d)*u;
		
		if (w.LengthSquared()>0.001f)
		{
			return MyMath.Sign( Vector3.Dot(u,d) )*n*u + r* (w/w.Length());
		}
		else 
		{
			return MyMath.Sign( Vector3.Dot(u,d) )*n*u;
		}
	}	
};



