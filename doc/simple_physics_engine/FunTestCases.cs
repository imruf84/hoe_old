
// FunTestCases.cs

/*
  Well physics should be fun, so I thought I'd think up some test cases
  of different configurations of rigid bodies to test out things and find
  hidden bugs
*/

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;


using System.IO;
using System.Diagnostics;
using System.Windows.Forms.Design;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

using System.Runtime.InteropServices;



public class FunTestCases_c
{
	static public 
	void BuildTestScene(ref List<RigidBody_c> rigidBodies, int i)
	{
		switch (i)
		{
			case 0:
			{
				//FunTestCases_c.BuildSeeSaw(ref rigidBodies);
				//FunTestCases_c.BuildDominos(ref rigidBodies);
				//FunTestCases_c.BuildBowling(ref rigidBodies);
				FunTestCases_c.BuildMarbles(ref rigidBodies);
			}
			break;
			
			default:
			{
			}
			break;
		}
	}
	
	static public 
	void BuildSeeSaw(ref List<RigidBody_c> rigidBodies)
	{
		rigidBodies.Clear();
			
		// Add ground rectangle			
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 5, 100)), Quaternion.Identity, new Vector3(0,-10,0), 0.0f) );


		Vector3 cubeSize = new Vector3(5, 5, 5);
		Vector3 cubePos  = new Vector3(0, 0, 0);
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, cubePos, 1.0f) );
		
		cubeSize = new Vector3(30, 2, 5);
		cubePos  = new Vector3(0, 7.0f, 0);
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, cubePos, 1.0f) );
		
		for (int i=0; i<5; i++)
		{
			cubeSize = new Vector3(5, 5, 5);
			cubePos  = new Vector3(25, 50.0f+i*20, 0);
			rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, cubePos, 1.0f) );
		}
		
		cubeSize = new Vector3(5, 5, 5);
		cubePos  = new Vector3(-25.0f, 15.0f, 0);
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, cubePos, 1.0f) );
	}

	static public 
	void BuildDominos(ref List<RigidBody_c> rigidBodies)
	{
		rigidBodies.Clear();
			
		// Add ground rectangle			
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 5, 100)), Quaternion.Identity, new Vector3(0,-10,0), 0.0f) );

		
		int num = 20;
		float r = 50.0f;
		
		for (int i=0; i<num; i++)
		{
			float inc = i/(float)(num); // 0 to 1.0f
			
			float x = r * (float)Math.Sin(inc * Math.PI * 2.0f);
			float z = r * (float)Math.Cos(inc * Math.PI * 2.0f);
			
			Quaternion yQRot = Quaternion.CreateFromAxisAngle(new Vector3(0,1,0), inc * (float)Math.PI * 2.0f);
			
			Vector3 cubeSize = new Vector3(1, 10, 5);
			Vector3 cubePos  = new Vector3(x, 5.0f, z);
			rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), yQRot, cubePos, 1.0f) );		
		}
		
		// Trigger the domino effect
		{
		Vector3 cubeSize = new Vector3(5, 5, 5);
		Vector3 cubePos  = new Vector3(5, 45.0f, r);
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, cubePos, 1.0f) );
		}
	}

	static public 
	void BuildBowling(ref List<RigidBody_c> rigidBodies)
	{
		rigidBodies.Clear();
			
		// Add ground rectangle			
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 5, 100)), Quaternion.Identity, new Vector3(0,-10,0), 0.0f) );

		
	
		Vector3 firstPinPos		= new Vector3(0, 5, 30);
		Vector3 cubeSize		= new Vector3(2, 10, 2);
		
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, firstPinPos, 1.0f) );		
		
		for (int i=0; i<4; i++)
		{	
			for (int k=0; k<2; k++)
			{	
				float side = 1.0f;
				if (k==1) side = -1.0f;
					
				float gap = 5.0f;
				Vector3 pinPos  = firstPinPos;
				pinPos.Z = firstPinPos.Z + (i+1)*gap;
				pinPos.X = (i+1)*side*gap;
				rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, pinPos, 1.0f) );		
			}
		}
		
		// Fire bowling ball
		{
		Vector3 spherePos  = new Vector3(0, 3, -70);
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeSphere(5.0f), Quaternion.Identity, spherePos, 1.0f) );
		
		rigidBodies[ rigidBodies.Count()-1 ].body.v.Z = 400.0f;
		}
	}
	
	static public 
	void BuildMarbles(ref List<RigidBody_c> rigidBodies)
	{
		rigidBodies.Clear();
			
		// Add ground rectangle			
		rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 5, 100)), Quaternion.Identity, new Vector3(0,-10,0), 0.0f) );

		
		for (int i=0; i<4; i++)
		{
			float side = 1.0f;
			if (i%2==0) side = -1.0f;
			
			Vector3 cubeSize	= new Vector3(30, 2, 5);
			Vector3 cubePos		= new Vector3(side*5.0f, 7.0f + i*25.0f, 0);
			Quaternion cubeRot  = Quaternion.CreateFromAxisAngle(new Vector3(0,0,1), side*0.2f);
			rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), cubeRot, cubePos, 0.0f) );
		}
		
		for (int i=0; i<2; i++)
		{
			float side = 1.0f;
			if (i==0) side=-1.0f;
			
			Vector3 cubeSize	= new Vector3(2, 50, 5);
			Vector3 cubePos		= new Vector3(35*side, 50, 0);
			rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, cubePos, 0.0f) );
		}
		
		// Add some falling marbles
		for (int i=0; i<1; i++)
		{
			Vector3 spherePos  = new Vector3(25-7*i, 95+i*0.0f, 0);
			rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeSphere(3.0f), Quaternion.Identity, spherePos, 1.0f) );
		}

	}
	
};




