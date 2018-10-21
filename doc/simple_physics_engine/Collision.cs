// Collision.cs

/*

	Simple True/False collision detection to see if two of our shapes have 
	collided.

*/


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


public class MyMath
{
	public static
	Vector3 Rotate(Quaternion q, Vector3 v)
	{		
		Quaternion qv = new Quaternion(v, 0.0f);
		
		Quaternion qr = MyMath.Mult(q, MyMath.Mult(qv, Conjugate(q)));
		return new Vector3(qr.X, qr.Y, qr.Z);
	}
	
	public static 
	Quaternion Conjugate(Quaternion q)
	{
		Quaternion qr = new Quaternion(-q.X,-q.Y,-q.Z, q.W);
		return qr;
	}
	
	public static 
	Quaternion Mult(Quaternion a, Quaternion b)
	{
		
		#if true
		Quaternion c;
		float aW = a.W, aX = a.X, aY = a.Y, aZ = a.Z;
		float bW = b.W, bX = b.X, bY = b.Y, bZ = b.Z;

		c.X = (aW * bX) + (aX * bW) + (aY * bZ) - (aZ * bY);
		c.Y = (aW * bY) + (aY * bW) + (aZ * bX) - (aX * bZ);
		c.Z = (aW * bZ) + (aZ * bW) + (aX * bY) - (aY * bX);
		c.W = (aW * bW) - (aX * bX) - (aY * bY) - (aZ * bZ);
		
		// Debug Speedup Checks - slight error difference between our
		// custom Quaternion calculation and the API one - possibly due
		// to internal xna speedups?
		Quaternion check = Quaternion.Multiply( a, b );
		float err = 0.01f;
		Debug_c.Assert(	(Math.Abs(check.X-c.X)<err) &&
						(Math.Abs(check.Y-c.Y)<err) &&
						(Math.Abs(check.Z-c.Z)<err) &&
						(Math.Abs(check.W-c.W)<err));
		c = check;
		
		return c;
		
		#else
		
		return Quaternion.Multiply( a, b );
		
		#endif
	}
	
	public static
	float Sign( float a )
	{
		if (a>=0) return 1.0f;
		return -1.0f;
	}
}

public class Collision
{
	public static
	Vector3 TransformSupportVert( Shape			s, 
								  Quaternion	q, 
								  Vector3		t, 
								  Vector3		n )
	{
		Vector3 localNormal  = MyMath.Rotate( MyMath.Conjugate( q ), n );
		Vector3 localSupport = s.GetSupportPoint( localNormal );
		Vector3 worldSupport = MyMath.Rotate( q, localSupport ) + t;
		return worldSupport;
	}
/*	
	static public List<DebugDrawInfo> s_debugDrawInfo = new List<DebugDrawInfo>();
	
	public class DebugDrawInfo
	{
		public DebugType	dtype;
		public string		desc;
		public Vector3		p0;
		public Vector3		p1;
		public Microsoft.Xna.Framework.Graphics.Color c;
		
		public enum DebugType
		{
			DT_POINT,
			DT_LINE
		};
		
		public DebugDrawInfo(DebugDrawInfo.DebugType dtype, string desc, Vector3 p0, Microsoft.Xna.Framework.Graphics.Color c)
		{
			this.dtype  = dtype;
			this.desc	= desc;
			this.p0		= p0;
			this.c		= c;
		}
		public DebugDrawInfo(DebugDrawInfo.DebugType dtype, string desc, Vector3 p0, Vector3 p1)
		{
			this.dtype  = dtype;
			this.desc	= desc;
			this.p0		= p0;
			this.p1		= p1;
		}
		
		public void Draw(GraphicsDevice graphicsDevice)
		{
			if (dtype==DebugType.DT_POINT)
				DrawLine_c.DrawCross( graphicsDevice, p0, 2.0f, c);
				
			if (dtype==DebugType.DT_LINE)
				DrawLine_c.DrawLine( graphicsDevice, p0, p1, Microsoft.Xna.Framework.Graphics.Color.White );
		}
	};
	
	
	public static void DrawDebugInfo( GraphicsDevice graphicsDevice )
	{
		DrawLine_c.DrawCross( graphicsDevice, Vector3.Zero, 3.0f);
		
		for (int i=0; i<s_debugDrawInfo.Count; i++)
		{
			s_debugDrawInfo[i].Draw( graphicsDevice );
		}
	}
	
	

	public static
	bool HasCollision(Shape			shape0,
					  Quaternion	q0,
					  Vector3		t0,
					  Shape			shape1,
					  Quaternion	q1,
					  Vector3		t1)
	{
		s_debugDrawInfo.Clear();
		
		// First we need a centre reference point between our two shapes,
		// we'll use the average centres - aka. the Minkowski difference
		Vector3 va = MyMath.Rotate( q0, shape0.GetCenter() ) + t0;
		Vector3 vb = MyMath.Rotate( q1, shape0.GetCenter() ) + t1;
		Vector3 v0 = vb - va;

		Microsoft.Xna.Framework.Graphics.Color c = Microsoft.Xna.Framework.Graphics.Color.White;

		//s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
		//                                       "O",
		//                                       Vector3.Zero) );
								   
		//s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
		//                                       "va",
		//                                       va) );
											   
		//s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
		//                                       "vb",
		//                                       vb) );
		

		
								   
		//s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_LINE,
		//                                       "v0",
		//                                       vb,
		//                                       va) );
											   
		//s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
		//                                       "v0",
		//                                       v0,
		//                                       c) );

											   
										   
											   
		
		if (v0.LengthSquared()<0.001f) return true; // Both shape centres overlap
		
		
		// Support Point 0 - Lets pick a first support point in the direction of the origin
		Vector3 n = -v0;
		Vector3 v1 = TransformSupportVert(shape1, q1, t1, n) - TransformSupportVert(shape0, q0, t0, -n);
		
		s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
											   "v1",
											   v1,
											   Microsoft.Xna.Framework.Graphics.Color.Red) );	
											   
		//s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_LINE,
		//								   "v1",
		//								    Vector3.Zero,
		//								    v1) );
										   
		Vector3 ndb = Vector3.Cross(v1, v0);
		s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
										   "v2",
										    ndb,
										    Microsoft.Xna.Framework.Graphics.Color.Green) );
		
										   
		if ( Vector3.Dot(v1, n) <= 0 ) return false; // Origin is on the wrong side of vs0 support plane -> miss
		

											   
		
		// Support Point 1 - Perpenduclar to the plane containing the origin, vs0, and v
		n = Vector3.Cross(v1, v0);
		if (n.LengthSquared()<0.001f) return true; // v0, v1 and origin colinear -> hit
												   
		Vector3 v2 = TransformSupportVert(shape1, q1, t1, n) - TransformSupportVert(shape0, q0, t0, -n);
		
		
										    
										    
		if (Vector3.Dot(v2, n) <= 0 ) return false; // Origin outside v2 support plane -> miss
		

										   
										   
		
		// v3 = support perpendicular to plane containing v0, v1 and v2
		n = Vector3.Cross( (v1 - v0), (v2 - v0) );
	
	
		// If origin is on the -ve side of the plane, reverse direction of the plane
		if (Vector3.Dot(n, v0) > 0)
		{
			Vector3 tmp = v2;
			v2 = v1;
			v1 = v2;
			n = -n;
		}
	
		// Recurse around the shape to find any intersections
		while (true)
		{
			Vector3 v3 = TransformSupportVert(shape1, q1, t1, n) - TransformSupportVert(shape0, q0, t0, -n);
			if (Vector3.Dot(v3, n) <= 0) return false; // Origin outside v3 plane -> miss
			
			s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
										   "v3",
										    v3,
										    c) );
										    
			// Check if origin is outsie(v1,v0,v3) - if so elimiate v2 and find new support face
			if ( Vector3.Dot( Vector3.Cross( v1, v3 ), v0 ) < 0 )
			{
				v2 = v3;
				n = Vector3.Cross( (v1-v0), (v3-v0) );
				continue;
			}
			
			// Check if origin is ouside (v3,v0,v2) - if so elimiate v1 and find new support face
			if ( Vector3.Dot( Vector3.Cross( v3, v2 ), v0 ) < 0 )
			{
				v1 = v3;
				n = Vector3.Cross( (v3-v0), (v2-v0) );
				continue;
			}
			
			// Refine check
			while (true)
			{
				// Outward facing normal
				n = Vector3.Cross( (v2-v1), (v3-v1) );
				
				// Check if the origin is inside, -> we have a hit
				if ( Vector3.Dot(n, v1) >= 0 ) return true;
				
				// Find support point in the direction of the portal
				Vector3 v4 = TransformSupportVert(shape1, q1, t1, n) - TransformSupportVert(shape0, q0, t0, -n);
				
				s_debugDrawInfo.Add( new DebugDrawInfo(DebugDrawInfo.DebugType.DT_POINT,
										   "v4",
										    v4,
										    Microsoft.Xna.Framework.Graphics.Color.Tomato) );
				
				const float boundaryTolerance = 0.0001f;
				
				// If the origin is outside the support plane we have a miss
				n.Normalize();
				if ( -Vector3.Dot(v4, n) >= 0 || Vector3.Dot( (v4-v3), n ) <= boundaryTolerance ) return false;
				
				
				
				// Test origin against the three planes that separate the new portal candidates: (v1,v4,v0) (v2,v4,v0) (v3,v4,v0)
				// Note:  We're taking advantage of the triple product identities here as an optimization
				//        (v1 % v4) * v0 == v1 * (v4 % v0)    > 0 if origin inside (v1, v4, v0)
				//        (v2 % v4) * v0 == v2 * (v4 % v0)    > 0 if origin inside (v2, v4, v0)
				//        (v3 % v4) * v0 == v3 * (v4 % v0)    > 0 if origin inside (v3, v4, v0)
				Vector3 cross = Vector3.Cross(v4, v0);
				if ( Vector3.Dot(v1, cross) > 0)
				{
					if (Vector3.Dot(v2,cross) > 0) v1 = v4;		// Inside v1 & inside v2 ==> eliminate v1
					else v3 = v4;								// Inside v1 & outside v2 ==> eliminate v3
				}
				else
				{
					if (Vector3.Dot(v3, cross) > 0) v2 = v4;	// Outside v1 & inside v3 ==> eliminate v2
					else v1 = v4;								// Outside v1 & outside v3 ==> eliminate v1
				}
			}
		}
	}
 */
};