// Intersection.cs

/*

	Expanding on Collision.cs - we use this to create a function which can also gather
	collision information, such as the collision normal, penetration depth, 
	collisionp point etc.

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




public class Intersection_c
{
	static
	void Swap(ref Vector3 a, ref Vector3 b)
	{
		Vector3 tmp = a;
		a = b;
		b = tmp;
	}

	public static
	bool HasIntersection(Shape				p1, 
						 Quaternion			q1, 
						 Vector3			t1, 
						 Shape				p2, 
						 Quaternion			q2, 
						 Vector3			t2, 
						 out Vector3		returnNormal, 
						 out Vector3		point1, 
						 out Vector3		point2)
	{
		returnNormal = Vector3.Zero;
		point1		 = Vector3.Zero;
		point2		 = Vector3.Zero;

		const float kCollideEpsilon = 1e-3f;
		
		// v0 = center of Minkowski sum
		Vector3 v01 = MyMath.Rotate(q1, p1.GetCenter()) + t1;
		Vector3 v02 = MyMath.Rotate(q2, p2.GetCenter()) + t2; 
		Vector3 v0 = v02 - v01;
		Debug_c.Valid(v02);
		Debug_c.Valid(v01);
		Debug_c.Valid(v0);
		
		// Avoid case where centers overlap -- any direction is fine in this case
		if (v0.LengthSquared()<0.0001f) v0 = new Vector3(0.00001f, 0, 0);
		
		
		// v1 = support in direction of origin
		Vector3 n = -v0;
		Vector3 v11 = Collision.TransformSupportVert(p1, q1, t1, -n);
		Vector3 v12 = Collision.TransformSupportVert(p2, q2, t2,  n);
		Debug_c.Valid(v11);
		Debug_c.Valid(v12);
		Vector3 v1  = v12 - v11;
		
		if (Vector3.Dot(v1,n) <= 0.0f)
		{
			return false;
		}
		Debug_c.Valid(v0);
		Debug_c.Valid(v1);
	
		// v2 - support perpendicular to v1,v0
		n = Vector3.Cross(v1, v0);
		Debug_c.Valid(n);
		if (n.LengthSquared()<0.0001f)
		{
			n = v1 - v0;
			n.Normalize();
			returnNormal = n;
			point1		 = v11;
			point2		 = v12;
			return true;
		}
	
		Vector3 v21 = Collision.TransformSupportVert(p1, q1, t1, -n);
		Vector3 v22 = Collision.TransformSupportVert(p2, q2, t2,  n);
		Vector3 v2 = v22 - v21;
		if (Vector3.Dot(v2, n) <= 0.0f)
		{
			return false;
		}
		Debug_c.Valid(v21);
		Debug_c.Valid(v22);
	
		// Determine whether origin is on + or - side of plane (v1,v0,v2)
		n = Vector3.Cross(v1 - v0,v2 - v0);
		Debug_c.Valid(n);
		float dist = Vector3.Dot(n, v0);

		Debug_c.Assert( n.LengthSquared()>0.0001f );

		// If the origin is on the - side of the plane, reverse the direction of the plane
		if (dist > 0)
		{
			Swap(ref v1,  ref v2);
			Swap(ref v11, ref v21);
			Swap(ref v12, ref v22);
			n = -n;
		}

		
		bool hit = false;
		
		///
		// Phase One: Identify a portal
		//
		while (true)
		{
			// Obtain the support point in a direction perpendicular to the existing plane
			// Note: This point is guaranteed to lie off the plane

			Vector3 v31 = Collision.TransformSupportVert(p1, q1, t1, -n);
			Vector3 v32 = Collision.TransformSupportVert(p2, q2, t2,  n); 
			Vector3 v3 = v32 - v31;
			if (Vector3.Dot(v3, n) <= 0)
			{
				return false;
			}
			
			// If origin is outside (v1,v0,v3), then eliminate v2 and loop
			if (Vector3.Dot( Vector3.Cross(v1,v3), v0) < 0)
			{
				v2 = v3;
				v21 = v31;
				v22 = v32;
				n = Vector3.Cross(v1 - v0, v3 - v0);
				continue;
			}

			// If origin is outside (v3,v0,v2), then eliminate v1 and loop
			if (Vector3.Dot( Vector3.Cross(v3, v2), v0) < 0)
			{
				v1 = v3;
				v11 = v31;
				v12 = v32;
				n = Vector3.Cross(v3 - v0, v2 - v0);
				continue;
			}
			
			///
			// Phase Two: Refine the portal

			// We are now inside of a wedge...
			while (true)
			{
				// Compute normal of the wedge face
				n = Vector3.Cross(v2 - v1, v3 - v1);
				
				// Can this happen???  Can it be handled more cleanly?
				if (n.LengthSquared()<0.00001f)
				{
					Debug_c.Assert(false);
					return false;
				}
				
				n.Normalize();
				
				
				// Compute distance from origin to wedge face
				float d = Vector3.Dot(n, v1);
				
				
				// If the origin is inside the wedge, we have a hit
				if (d >= 0 && !hit)
				{

					returnNormal = n;

					// Compute the barycentric coordinates of the origin

					float b0 = Vector3.Dot( Vector3.Cross(v1, v2), v3);
					float b1 = Vector3.Dot( Vector3.Cross(v3, v2), v0);
					float b2 = Vector3.Dot( Vector3.Cross(v0, v1), v3);
					float b3 = Vector3.Dot( Vector3.Cross(v2, v1), v0);

					float sum = b0 + b1 + b2 + b3;

					if (sum <= 0)
					{


						b0 = 0;
						b1 = Vector3.Dot( Vector3.Cross(v2, v3), n);
						b2 = Vector3.Dot( Vector3.Cross(v3, v1), n);
						b3 = Vector3.Dot( Vector3.Cross(v1, v2), n);

						sum = b1 + b2 + b3;
					}

					float inv = 1.0f / sum;

					
					point1 = (b0 * v01 + b1 * v11 + b2 * v21 + b3 * v31) * inv;

					point2 = (b0 * v02 + b1 * v12 + b2 * v22 + b3 * v32) * inv;

					// HIT!!!
					hit = true;
				}
				
				// Find the support point in the direction of the wedge face
				Vector3 v41 = Collision.TransformSupportVert(p1, q1, t1, -n);
				Vector3 v42 = Collision.TransformSupportVert(p2, q2, t2, n); 
				Vector3 v4 = v42 - v41;

				float delta = Vector3.Dot((v4 - v3), n);
				float separation = -Vector3.Dot(v4, n);
			
				// If the boundary is thin enough or the origin is outside the support plane for the 
				// newly discovered vertex, then we can terminate
				if ( delta <= kCollideEpsilon || separation >= 0 )
				{
					returnNormal = n;
					return hit;
				}
				
				// Compute the tetrahedron dividing face (v4,v0,v1)
				float d1 = Vector3.Dot( Vector3.Cross(v4, v1), v0);

				// Compute the tetrahedron dividing face (v4,v0,v2)
				float d2 = Vector3.Dot( Vector3.Cross(v4, v2), v0);

				// Compute the tetrahedron dividing face (v4,v0,v3)
				float d3 = Vector3.Dot( Vector3.Cross(v4, v3), v0);

				if (d1 < 0)
				{
					if (d2 < 0)
					{
						// Inside d1 & inside d2 ==> eliminate v1
						v1 = v4;
						v11 = v41;
						v12 = v42;
					}
					else
					{
						// Inside d1 & outside d2 ==> eliminate v3
						v3 = v4;
						v31 = v41;
						v32 = v42;
					}
				}
				else
				{
					if (d3 < 0)
					{
						// Outside d1 & inside d3 ==> eliminate v2
						v2 = v4;
						v21 = v41;
						v22 = v42;
					}
					else
					{
						// Outside d1 & outside d3 ==> eliminate v1
						v1 = v4;
						v11 = v41;
						v12 = v42;
					}
				}
			
			
			}
		}
	}
};