// RigidBody.cs


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

public class Body_c
{
	public Body_c()
	{
		com		= Vector3.Zero;
		x		= Vector3.Zero;
		v		= Vector3.Zero;
		m		= 1.0f;
		inv_m	= 1.0f;
		q		= new Quaternion(0,0,0,1);
		omega	= Vector3.Zero;
		I		= Matrix.Identity;
		inv_I	= Matrix.Identity;
	}
	
	public Body_c(Body_c copy)
	{
		com			= copy.com;
		x			= copy.x;	
		v			= copy.v;	
		m			= copy.m;	
		inv_m		= copy.inv_m;
		inv_m_back	= copy.inv_m_back;
		q			= copy.q;	
		omega		= copy.omega;
		I			= copy.I;	
		inv_I		= copy.inv_I;
	}

	public Vector3		com;		// center of mass
	public Vector3		x;			// position
	public Vector3		v;			// velocity
	public float		m;			// mass
	public float		inv_m;		// inverse mass (1 / mass)
	public float		inv_m_back;

	public Quaternion	q;			// rotation
	public Vector3		omega;		// angular velocity
	public Matrix		I;			// inertia tensor
	public Matrix		inv_I;		// inverse intertia tensor

	public void UpdateVel(float dt)
	{
		if (m>0.0f)
		{
			v += new Vector3(0, -109.8f, 0) * dt;
			Debug_c.Valid(v);
		}
	}
	
	public void UpdatePos(float dt)
	{
		if (m>0.0f)
		{
			x += v * dt;
			Debug_c.Valid(x);
			
			Quaternion temp = MyMath.Mult(new Quaternion(omega.X, omega.Y, omega.Z, 0), q) * 0.5f;
			q = q + temp * dt;
			q.Normalize();
		}
	}
	

	Body_c store = null;
	public void StoreState()
	{
		store = new Body_c();
		store.com			= com;
		store.x				= x;			
		store.v				= v;			
		store.m				= m;			
		store.inv_m			= inv_m;		
		store.inv_m_back	= inv_m_back;

		store.q				= q;			
		store.omega			= omega;		
		store.I				= I;			
		store.inv_I			= inv_I;		
	}
	public void RestoreState()
	{
		com				= store.com;
		x				= store.x;			
		v				= store.v;			
		m				= store.m;			
		inv_m			= store.inv_m;		
		inv_m_back		= store.inv_m_back;
						 
		q				= store.q;			
		omega			= store.omega;		
		I				= store.I;			
		inv_I			= store.inv_I;
	}
};

        

/*
	RigidBody

	These are the objects you see in the physics simulation within the demo.
	Each RigidBody object contains a physics model (Body), a collision model
	(CollideGeometry), and render model (RenderPolytope) and a unique color.

	The physics and render models are created directly from the collision
	model, which itself is a support mapping that represents the shape.
*/
 
public class MyMatrix
{
	public MyMatrix(Matrix m)
	{
		mat = m;
	}
	
	public float this[int r, int c]  
	{  
	    get 
	    { 
			if (r==0 && c==0) return mat.M11;
			if (r==0 && c==1) return mat.M12;
			if (r==0 && c==2) return mat.M13;
			if (r==0 && c==3) return mat.M14;
			
			if (r==1 && c==0) return mat.M21;
			if (r==1 && c==1) return mat.M22;
			if (r==1 && c==2) return mat.M23;
			if (r==1 && c==3) return mat.M24;
			
			if (r==2 && c==0) return mat.M31;
			if (r==2 && c==1) return mat.M32;
			if (r==2 && c==2) return mat.M33;
			if (r==2 && c==3) return mat.M34;
			
			if (r==3 && c==0) return mat.M41;
			if (r==3 && c==1) return mat.M42;
			if (r==3 && c==2) return mat.M43;
			if (r==3 && c==3) return mat.M44;
			return 0.0f;
	    }  
	    set 
	    { 
			if (r==0 && c==0) mat.M11=value;
			if (r==0 && c==1) mat.M12=value;
			if (r==0 && c==2) mat.M13=value;
			if (r==0 && c==3) mat.M14=value;
			
			if (r==1 && c==0) mat.M21=value;
			if (r==1 && c==1) mat.M22=value;
			if (r==1 && c==2) mat.M23=value;
			if (r==1 && c==3) mat.M24=value;
			
			if (r==2 && c==0) mat.M31=value;
			if (r==2 && c==1) mat.M32=value;
			if (r==2 && c==2) mat.M33=value;
			if (r==2 && c==3) mat.M34=value;
			
			if (r==3 && c==0) mat.M41=value;
			if (r==3 && c==1) mat.M42=value;
			if (r==3 && c==2) mat.M43=value;
			if (r==3 && c==3) mat.M44=value;
		}  
	} 
	
	public Matrix Get()
	{
		return mat;
	}
	
	Matrix mat;
}


public class VecMat
{
	public static 
	void Set(ref Matrix mat, int r, int c, float v)
	{
		if (r==0 && c==0) mat.M11=v;
		if (r==0 && c==1) mat.M12=v;
		if (r==0 && c==2) mat.M13=v;
		if (r==0 && c==3) mat.M14=v;
		
		if (r==1 && c==0) mat.M21=v;
		if (r==1 && c==1) mat.M22=v;
		if (r==1 && c==2) mat.M23=v;
		if (r==1 && c==3) mat.M24=v;
		
		if (r==2 && c==0) mat.M31=v;
		if (r==2 && c==1) mat.M32=v;
		if (r==2 && c==2) mat.M33=v;
		if (r==2 && c==3) mat.M34=v;
		
		if (r==3 && c==0) mat.M41=v;
		if (r==3 && c==1) mat.M42=v;
		if (r==3 && c==2) mat.M43=v;
		if (r==3 && c==3) mat.M44=v;
	}
	
	public static 
	void Set(ref Vector3 vec, int r, float v)
	{
		if (r==0) vec.X=v;
		if (r==1) vec.Y=v;
		if (r==2) vec.Z=v;
	}
	public static 
	float Get(ref Vector3 vec, int r)
	{
		if (r==0) return vec.X;
		if (r==1) return vec.Y;
		if (r==2) return vec.Z;
		return 0.0f;
	}
};


public class MyVector3
{
	public MyVector3( Vector3 v3 )
	{	
		m_vector3 = v3;
	}
	
	public float this[int i]  
	{  
	    get 
	    { 
			if (i==0) return this.m_vector3.X;
			if (i==1) return this.m_vector3.Y;
			if (i==2) return this.m_vector3.Z;
			return 0.0f;
	    }  
	    set 
	    { 
			if (i==0) this.m_vector3.X=value;
			if (i==1) this.m_vector3.Y=value;
			if (i==2) this.m_vector3.Z=value;
		}  
	} 
	
	public static 
	MyVector3 operator+(MyVector3 a, MyVector3 b)
	{
		MyVector3 res = new MyVector3( Vector3.Zero );
		res.m_vector3.X = a.m_vector3.X + b.m_vector3.X;
		res.m_vector3.Y = a.m_vector3.Y + b.m_vector3.Y;
		res.m_vector3.Z = a.m_vector3.Z + b.m_vector3.Z;
		return res;
	}
	
	public static 
	MyVector3 operator/(MyVector3 a, float b)
	{
		MyVector3 res = new MyVector3( Vector3.Zero );
		res.m_vector3.X = a.m_vector3.X / b;
		res.m_vector3.Y = a.m_vector3.Y / b;
		res.m_vector3.Z = a.m_vector3.Z / b;
		return res;
		
	}
	
	public static 
	MyVector3 operator * (MyVector3 a, float b)
	{
		MyVector3 res = new MyVector3( a.m_vector3*=b );
		return res;
		
	}
	
	public Vector3 V3()
	{
		return m_vector3;
	}
	
	Vector3 m_vector3;
};


public class RigidBody_c
{
	static int idcounter = 0;
	public RigidBody_c(Body_c b, Shape cg, HullMaker hull, float radius, Microsoft.Xna.Framework.Graphics.Color c)
	{
		maxRadius		= radius;
		body			= b;
		collideModel	= cg;
		renderModel		= hull;
		id				= idcounter;
		colour			= c;
		idcounter++;
	}

	public int				id;
	public Body_c			body;
	public Shape			collideModel;
	public HullMaker		renderModel;
	public float			maxRadius;
	public Microsoft.Xna.Framework.Graphics.Color colour;

};


public class HelperRigidBody_c
{
	public static int randColourIndx = 0;
	
	public static 
	RigidBody_c CreateRigidBody(Shape collideModel, Quaternion q, Vector3 x, float inv_m )
	{
		//RenderPolytope	renderModel	 = rb.renderModel;
		HullMaker		renderModel	 = new HullMaker( collideModel, -1 );
		
		//if (!renderModel)
		//{
		//	renderModel = CreateRenderModel(collideModel);
		//}

		Body_c body = new Body_c();
		body.q = q;
		body.x = x;
		body.inv_I = Matrix.Identity;
		body.inv_I *= inv_m / 25.0f;
		//body.inv_I.M33 = 1;
		body.inv_I.M44 = 1;
		body.inv_m = inv_m;
		
		if (inv_m==0.0f)
		{
			body.m = 0.0f;
		}

		if (inv_m > 0.0f)
		{
			HelperRigidBody_c.ComputeMassProperties(body, renderModel, 1.0f);
		}

		float radiusNegX = Math.Abs(collideModel.GetSupportPoint( new Vector3(-1, 0, 0) ).X);
		float radiusPosX = Math.Abs(collideModel.GetSupportPoint( new Vector3( 1, 0, 0) ).X);
		float radiusNegY = Math.Abs(collideModel.GetSupportPoint( new Vector3(0, -1, 0) ).Y);
		float radiusPosY = Math.Abs(collideModel.GetSupportPoint( new Vector3(0,  1, 0) ).Y);
		float radiusNegZ = Math.Abs(collideModel.GetSupportPoint( new Vector3(0, 0, -1) ).Z);
		float radiusPosZ = Math.Abs(collideModel.GetSupportPoint( new Vector3(0, 0,  1) ).Z);

		Vector3 maxRadiusVector = new Vector3
		(
			Math.Max(radiusNegX, radiusPosX),
			Math.Max(radiusNegY, radiusPosY),
			Math.Max(radiusNegZ, radiusPosZ)
		);

		float maxRadius = maxRadiusVector.Length();

		Microsoft.Xna.Framework.Graphics.Color[] randColour = { Microsoft.Xna.Framework.Graphics.Color.Red,
																Microsoft.Xna.Framework.Graphics.Color.Green,
																Microsoft.Xna.Framework.Graphics.Color.PaleGoldenrod,
																Microsoft.Xna.Framework.Graphics.Color.OliveDrab,
																Microsoft.Xna.Framework.Graphics.Color.OrangeRed,
																Microsoft.Xna.Framework.Graphics.Color.Wheat,
																Microsoft.Xna.Framework.Graphics.Color.YellowGreen };

		randColourIndx++;
		if (randColourIndx>(randColour.Count()-1)) randColourIndx=0;
		
		body.inv_m_back = body.inv_m;
		
		return new RigidBody_c(body, collideModel, renderModel, maxRadius, randColour[ randColourIndx ]);
		
	}
	
	public static
	float Det(Vector3 v0,Vector3 v1, Vector3 v2)
	{
		float det =
			v0.X * ( v1.Y * v2.Z - v1.Z * v2.Y ) +
			v0.Y * ( v1.Z * v2.X - v1.X * v2.Z ) +
			v0.Z * ( v1.X * v2.Y - v1.Y * v2.X );
		return det;
	}

	public static
	void ComputeMassProperties(Body_c body, HullMaker model, float density)
	{
		MyVector3 diag = new MyVector3( Vector3.Zero );
		MyVector3 offDiag = new MyVector3( Vector3.Zero );
		Vector3 weightedCenterOfMass = Vector3.Zero;
		float volume = 0;
		float mass = 0;

		// Iterate through the faces
		for (int faceIndex = 0; faceIndex < model.surfaceTriList.Count; faceIndex++)
		{
			HullMaker.ClipTri face = model.surfaceTriList[ faceIndex ];

			// Iterate through the tris in the face
			for (int triIndex = 0; triIndex < 3; triIndex++)
			{
				MyVector3 v0 = new MyVector3(face.n1);
				MyVector3 v1 = new MyVector3(face.n2);
				MyVector3 v2 = new MyVector3(face.n3);

				float det = Det(v0.V3(), v1.V3(), v2.V3());

				// Volume
				float tetVolume = det / 6.0f;
				volume += tetVolume;

				// Mass
				float tetMass = tetVolume * density;
				mass += tetMass;
				
				// Center of Mass
				Vector3 tetCenterOfMass = ((v0 + v1 + v2) / 4.0f).V3(); // Note: includes origin (0, 0, 0) as fourth vertex
				weightedCenterOfMass += tetMass * tetCenterOfMass;

				// Inertia Tensor
				for (int i = 0; i < 3; i++)
				{
					int j = (i + 1) % 3;
					int k = (i + 2) % 3;

					diag[i] += det * ( v0[i]*v1[i] + v1[i]*v2[i] + v2[i]*v0[i] + v0[i]*v0[i] + v1[i]*v1[i] + v2[i]*v2[i] ) / 60.0f;

					offDiag[i] += det * (
						v0[j]*v1[k] + v1[j]*v2[k] + v2[j]*v0[k] +
						v0[j]*v2[k] + v1[j]*v0[k] + v2[j]*v1[k] +
						2*v0[j]*v0[k] + 2*v1[j]*v1[k] + 2*v2[j]*v2[k] ) / 120.0f;
				}
			}
		}
			

		Debug_c.Assert(mass>0);
		if (mass==0.0f) mass = 5.0f;
		
		Vector3 centerOfMass = weightedCenterOfMass / mass;

		diag *= density;
		offDiag *= density;

		MyMatrix I = new MyMatrix(Matrix.Identity);

		I[0,0] = diag[1] + diag[2];
		I[1,1] = diag[2] + diag[0];
		I[2,2] = diag[0] + diag[1];
		I[1,2] = I[2,1] = -offDiag[0];
		I[0,2] = I[2,0] = -offDiag[1];
		I[0,1] = I[1,0] = -offDiag[2];

		///
		// Move inertia tensor to be relative to center of mass (rather than origin)

		// Translate intertia to center of mass
		float x = centerOfMass.X;
		float y = centerOfMass.Y;
		float z = centerOfMass.Z;

		//Debug_c.Assert(Math.Abs(x)>0);
		//Debug_c.Assert(Math.Abs(y)>0);
		//Debug_c.Assert(Math.Abs(z)>0);
		//if (x==0.0f) x = 1.0f;
		//if (y==0.0f) y = 1.0f;
		//if (z==0.0f) z = 1.0f;
		
		I[0,0] -= mass*(y*y + z*z);
		I[0,1] -= mass*(-x*y);
		I[0,2] -= mass*(-x*z);
		I[1,1] -= mass*(x*x + z*z);
		I[1,2] -= mass*(-y*z);
		I[2,2] -= mass*(x*x + y*y);

		// Symmetry
		I[1,0] = I[0,1];
		I[2,0] = I[0,2];
		I[2,1] = I[1,2];
		
		float check = 0.0f;
		for (int r=0; r<3; r++)
			for (int c=0; c<3; c++)
				check += I[r,c];
		Debug_c.Assert( Math.Abs(check)>0.0f );
		if (check==0.0f)
		{
			I = new MyMatrix(Matrix.Identity);
		}
		
		body.com = centerOfMass;
		body.inv_m = 1.0f / mass;
		body.inv_m_back = body.inv_m;
		body.I = I.Get();
		GeneralInverse4x4(out body.inv_I, ref I);
		//body.inv_I = Matrix.Invert( I.Get() );
		
		Debug_c.Valid(body.com);
		Debug_c.Valid(body.inv_m);
		Debug_c.Valid(body.inv_I);
		Debug_c.Valid(body.I);
		
		Matrix test = Matrix.Identity;
		test = Matrix.Invert( I.Get() );
	}
	

	public static
	void GeneralInverse4x4(out Matrix outm, ref MyMatrix m)
	{
		// Compute the adjoint of the input matrix
		MyMatrix adj = new MyMatrix(Matrix.Identity);
		for (int i=0; i < 4; i++)
		{
			for (int j=0; j < 4; j++)
			{
				adj[j,i] = Cofactor(m, i, j);
			}
		}

		// Compute the determinant of the input matrix
		float   det = m[0, 0] * adj[0, 0] 
					- m[0, 1] * adj[1, 0]
					+ m[0, 2] * adj[2, 0]
					- m[0, 3] * adj[3, 0];

		float   invDet = 1.0f / det;

		// Convert the adjoint matrix into the inverse matrix
		//adj.v0 *= invDet;
		//adj.v1 *= invDet;
		//adj.v2 *= invDet;
		//adj.v3 *= invDet;
		
		for (int i=0; i<4; i++)
			for (int k=0; k<4; k++)
				adj[i,k] *= invDet;

		// Return the result
		outm = adj.Get();
	}

	static float Cofactor( MyMatrix m, int row, int col)
	{
		int[] r = new int[4];
		int[] c = new int[4];

		int rowCount = 0;
		int colCount = 0;

		for (int t = 0; t < 4; t++)
		{
			if (row != t) r[rowCount++] = t;
			if (col != t) c[colCount++] = t;
		}

		float   det = m[ r[0], c[0] ] * ( m[r[1], c[1]] * m[r[2], c[2]] - m[r[2], c[1]] * m[r[1], c[2]] )
					- m[ r[0], c[1] ] * ( m[r[1], c[2]] * m[r[2], c[0]] - m[r[2], c[2]] * m[r[1], c[0]] )
					+ m[ r[0], c[2] ] * ( m[r[1], c[0]] * m[r[2], c[1]] - m[r[2], c[0]] * m[r[1], c[1]] );

		if ( ((row + col) & 1) != 0 )
		{
			return -det;
		}

		return det;
	}

}