

using System;
using System.Collections.Generic;
using System.Collections;
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

public class Vector
{
	public static bool Find(List<Vector3> pointSet, Vector3 find)
	{
		for (int i=0; i<pointSet.Count; i++)
		{
			if (pointSet[i].X==find.X &&
				pointSet[i].Y==find.Y &&
				pointSet[i].Z==find.Z)
				{
					return true;
				}
		}
		return false;
	}
};

public class HullMaker
{
	public
	struct ClipTri
	{
		public Vector3 n1;
		public Vector3 n2;
		public Vector3 n3;
		public int     generation;
	};
	
	//List<Vector3> m_points = new List<Vector3>();
	
	public
	List<ClipTri> surfaceTriList = new List<ClipTri>();

	public HullMaker(Shape shape, int generationThreshold)
	{
		float distanceThreshold = 0.0f;

		if (generationThreshold < 0) generationThreshold = 4;

		Stack activeTriList = new Stack();

		Vector3[] v = new Vector3[] // 6 Array
		{
			new Vector3( -1,  0,  0 ),
			new Vector3(  1,  0,  0 ),

			new Vector3(  0, -1,  0 ),
			new Vector3(  0,  1,  0 ),

			new Vector3(  0,  0, -1 ),
			new Vector3(  0,  0,  1 ),
		};

		int[,] kTriangleVerts = new int[8,3] // 8 x 3 Array
		{
			{ 5, 1, 3 },
			{ 4, 3, 1 },
			{ 3, 4, 0 },
			{ 0, 5, 3 },

			{ 5, 2, 1 },
			{ 4, 1, 2 },
			{ 2, 0, 4 },
			{ 0, 2, 5 }
		};

		for (int i=0; i < 8; i++)
		{
			ClipTri tri = new ClipTri();
			tri.n1			= v[ kTriangleVerts[i,0] ];
			tri.n2			= v[ kTriangleVerts[i,1] ];
			tri.n3			= v[ kTriangleVerts[i,2] ];
			tri.generation	= 0;
			activeTriList.Push(tri);
		}

		//List<Vector3> pointSet = new List<Vector3>();

		
		// surfaceTriList
		while (activeTriList.Count > 0)
		{
			ClipTri tri = (ClipTri)activeTriList.Pop();

			Vector3 p1 = shape.GetSupportPoint( tri.n1 );
			Vector3 p2 = shape.GetSupportPoint( tri.n2 );
			Vector3 p3 = shape.GetSupportPoint( tri.n3 );
		
			//tri.n1 = p1;
			//tri.n2 = p2;
			//tri.n3 = p3;
		
			float d1 = (p2 - p1).LengthSquared();
			float d2 = (p3 - p2).LengthSquared();
			float d3 = (p1 - p3).LengthSquared();

			if ( Math.Max( Math.Max(d1, d2), d3 ) > distanceThreshold && tri.generation < generationThreshold )
			{	
				ClipTri tri1 = new ClipTri();
				ClipTri tri2 = new ClipTri();
				ClipTri tri3 = new ClipTri();
				ClipTri tri4 = new ClipTri();

				tri1.generation = tri.generation+1;
				tri2.generation = tri.generation+1;
				tri3.generation = tri.generation+1;
				tri4.generation = tri.generation+1;

				tri1.n1 = tri.n1;
				tri2.n2 = tri.n2;
				tri3.n3 = tri.n3;

				Vector3 n = 0.5f * (tri.n1 + tri.n2);
				n.Normalize();

				tri1.n2 = n;
				tri2.n1 = n;
				tri4.n3 = n;

				n = 0.5f * (tri.n2 + tri.n3);
				n.Normalize();

				tri2.n3 = n;
				tri3.n2 = n;
				tri4.n1 = n;

				n = 0.5f * (tri.n3 + tri.n1);
				n.Normalize();

				tri1.n3 = n;
				tri3.n1 = n;
				tri4.n2 = n;

				activeTriList.Push(tri1);
				activeTriList.Push(tri2);
				activeTriList.Push(tri3);
				activeTriList.Push(tri4);
				
				
			}
			else 
			{
				ClipTri triKeep = new ClipTri();
				triKeep.n1 = p1;
				triKeep.n2 = p2;
				triKeep.n3 = p3;
				surfaceTriList.Add( triKeep );
				
				//m_points.Add(p1);
				//m_points.Add(p2);
				//m_points.Add(p3);
			}
		}


		/*
		while (activeTriList.Count > 0)
		{
			ClipTri tri = (ClipTri)activeTriList.Pop();

			Vector3 p1 = shape.GetSupportPoint( tri.n1 );
			Vector3 p2 = shape.GetSupportPoint( tri.n2 );
			Vector3 p3 = shape.GetSupportPoint( tri.n3 );

			if (!Vector.Find(pointSet,p1))
			{
				pointSet.Add(p1);
				m_points.Add(p1);
			}
			if (!Vector.Find(pointSet,p2))
			{
				pointSet.Add(p2);
				m_points.Add(p2);
			}
			if (!Vector.Find(pointSet,p3))
			{
				pointSet.Add(p3);
				m_points.Add(p3);
			}

			float d1 = (p2 - p1).LengthSquared();
			float d2 = (p3 - p2).LengthSquared();
			float d3 = (p1 - p3).LengthSquared();

			if ( Math.Max( Math.Max(d1, d2), d3 ) > distanceThreshold && tri.generation < generationThreshold )
			{
				ClipTri tri1 = new ClipTri();
				ClipTri tri2 = new ClipTri();
				ClipTri tri3 = new ClipTri();
				ClipTri tri4 = new ClipTri();

				tri1.generation = tri.generation+1;
				tri2.generation = tri.generation+1;
				tri3.generation = tri.generation+1;
				tri4.generation = tri.generation+1;

				tri1.n1 = tri.n1;
				tri2.n2 = tri.n2;
				tri3.n3 = tri.n3;

				Vector3 n = 0.5f * (tri.n1 + tri.n2);
				n.Normalize();

				tri1.n2 = n;
				tri2.n1 = n;
				tri4.n3 = n;

				n = 0.5f * (tri.n2 + tri.n3);
				n.Normalize();

				tri2.n3 = n;
				tri3.n2 = n;
				tri4.n1 = n;

				n = 0.5f * (tri.n3 + tri.n1);
				n.Normalize();

				tri1.n3 = n;
				tri3.n1 = n;
				tri4.n2 = n;

				activeTriList.Push(tri1);
				activeTriList.Push(tri2);
				activeTriList.Push(tri3);
				activeTriList.Push(tri4);
			}
			
			
		}//While
		*/
		
	}
	
	
	VertexPositionNormalTexture[] renderTris = null;
	
	public void DebugDraw( GraphicsDevice graphicsDevice,  RigidBody_c rb)
	{
		Quaternion q = rb.body.q;
		Vector3 t    = rb.body.x;
		Microsoft.Xna.Framework.Graphics.Color c = rb.colour;;
		
		/*
		for (int i=0; i<m_points.Count; i++)
		{
			DrawLine_c.DrawCross( graphicsDevice, m_points[i], 0.5f );
		}
		*/
		
		if (renderTris==null)
		{
			renderTris = new VertexPositionNormalTexture[surfaceTriList.Count * 3];
			for (int i=0; i<surfaceTriList.Count; i++)
			{
				Vector3 v1 = surfaceTriList[i].n1 - surfaceTriList[i].n2;
				Vector3 v2 = surfaceTriList[i].n3 - surfaceTriList[i].n2;
				Vector3 n = Vector3.Normalize( Vector3.Cross(v2, v1) );
				renderTris[i*3+0] = new VertexPositionNormalTexture(surfaceTriList[i].n1, n, Vector2.Zero);
				renderTris[i*3+1] = new VertexPositionNormalTexture(surfaceTriList[i].n2, n, Vector2.Zero);
				renderTris[i*3+2] = new VertexPositionNormalTexture(surfaceTriList[i].n3, n, Vector2.Zero);
			}
		}
		
		Matrix worldMat = Matrix.CreateFromQuaternion(q) * Matrix.CreateTranslation( t );
		DrawTriangle_c.DrawTriangleList(graphicsDevice, 
										worldMat,
										renderTris,
										c);
										
		/*
		for (int i=0; i<surfaceTriList.Count; i++)
		{
			Vector3 aa = Vector3.Transform(surfaceTriList[i].n1, q);
			
			Vector3 bb = MyMath.Rotate(q, surfaceTriList[i].n1);
		
			DrawTriangle_c.DrawTriangle(graphicsDevice, 
									    MyMath.Rotate(q, surfaceTriList[i].n1) + t,
										MyMath.Rotate(q, surfaceTriList[i].n2) + t,
										MyMath.Rotate(q, surfaceTriList[i].n3) + t,
										c);
		}
		*/

		
		
	}

};