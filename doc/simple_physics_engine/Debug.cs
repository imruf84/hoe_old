
// Debug.cs

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


public class Debug_c
{
	public static
	void Assert(bool valid)
	{
		if (!valid)
		{
			System.Diagnostics.Debugger.Break();
		}
	}
	
	public static
	void Valid(Vector3 v)
	{
		Debug_c.Assert( float.IsNaN( v.X )==false );
		Debug_c.Assert( float.IsNaN( v.Y )==false );
		Debug_c.Assert( float.IsNaN( v.Z )==false );
	}
	
	public static
	void Valid(float v)
	{
		Debug_c.Assert( float.IsNaN( v )==false );
	}
	
	public static
	void Valid(Matrix m)
	{
		MyMatrix temp = new MyMatrix(m);
		for (int i=0; i<4; i++)
			for (int k=0; k<4; k++)
				Debug_c.Assert( float.IsNaN( temp[i,k] )==false );
	}
};