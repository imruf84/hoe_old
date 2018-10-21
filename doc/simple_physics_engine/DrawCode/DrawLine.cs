


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



public class DrawLine_c
{
	/*
    public
    static void DrawLine(GraphicsDevice graphicsDevice, Vector3 from, Vector3 to, Microsoft.Xna.Framework.Graphics.Color col)
    {

        BasicEffect basicEffect     
        = new BasicEffect(graphicsDevice, null);
        basicEffect.VertexColorEnabled = true;
        basicEffect.Alpha              = 1.0f;
        basicEffect.View               = Camera_c.s_view;
        basicEffect.Projection         = Camera_c.s_projection;
        basicEffect.World              = Matrix.Identity;
        


        VertexDeclaration vertexDeclaration = 
        new VertexDeclaration( graphicsDevice, VertexPositionColor.VertexElements );

        graphicsDevice.VertexDeclaration           = vertexDeclaration;
        graphicsDevice.RenderState.FillMode        = FillMode.Solid;
        graphicsDevice.RenderState.CullMode        = CullMode.None;
        graphicsDevice.RenderState.AlphaBlendEnable= false;

        List<VertexPositionColor> points = new List<VertexPositionColor>();
        points.Add( new VertexPositionColor(from, col) );
        points.Add( new VertexPositionColor(to,   col) );
        basicEffect.Begin();
        foreach( EffectPass effectPass in basicEffect.CurrentTechnique.Passes )
        {
            effectPass.Begin();
            int numLines = points.Count;
            graphicsDevice.DrawUserPrimitives<VertexPositionColor>(
                PrimitiveType.LineList, points.ToArray(), 0, numLines / 2);

            effectPass.End();
        }
        basicEffect.End();
	}
	*/
	
	static BasicEffect			s_basicEffect		= null;
	static VertexDeclaration	s_vertexDeclaration	= null;
	
    public
    static void DrawLine(GraphicsDevice graphicsDevice, Vector3 from, Vector3 to, Microsoft.Xna.Framework.Graphics.Color col)
    {

        if (s_basicEffect==null)
        {  
			s_basicEffect = new BasicEffect(graphicsDevice, null);
        }
        s_basicEffect.VertexColorEnabled = true;
        s_basicEffect.Alpha              = 1.0f;
        s_basicEffect.View               = Camera_c.s_view;
        s_basicEffect.Projection         = Camera_c.s_projection;
        s_basicEffect.World              = Matrix.Identity;
        


		if (s_vertexDeclaration==null)
		{
			s_vertexDeclaration =
			new VertexDeclaration( graphicsDevice, VertexPositionColor.VertexElements );
        }

        graphicsDevice.VertexDeclaration           = s_vertexDeclaration;
        graphicsDevice.RenderState.FillMode        = FillMode.Solid;
        graphicsDevice.RenderState.CullMode        = CullMode.None;
        graphicsDevice.RenderState.AlphaBlendEnable= false;

        List<VertexPositionColor> points = new List<VertexPositionColor>();
        points.Add( new VertexPositionColor(from, col) );
        points.Add( new VertexPositionColor(to,   col) );
        s_basicEffect.Begin();
        foreach( EffectPass effectPass in s_basicEffect.CurrentTechnique.Passes )
        {
            effectPass.Begin();
            int numLines = points.Count;
            graphicsDevice.DrawUserPrimitives<VertexPositionColor>(
                PrimitiveType.LineList, points.ToArray(), 0, numLines / 2);

            effectPass.End();
        }
        s_basicEffect.End();
	}

	
	public
	static void DrawCross(GraphicsDevice graphicsDevice, Vector3 p, float s)
	{
		DrawLine(graphicsDevice, p - new Vector3(s,0,0), p + new Vector3(s,0,0), Microsoft.Xna.Framework.Graphics.Color.Red);
		DrawLine(graphicsDevice, p - new Vector3(0,s,0), p + new Vector3(0,s,0), Microsoft.Xna.Framework.Graphics.Color.Green);
		DrawLine(graphicsDevice, p - new Vector3(0,0,s), p + new Vector3(0,0,s), Microsoft.Xna.Framework.Graphics.Color.BlueViolet);
	}
	
	public
	static void DrawCross(GraphicsDevice graphicsDevice, Vector3 p, float s, Microsoft.Xna.Framework.Graphics.Color c)
	{
		DrawLine(graphicsDevice, p - new Vector3(s,0,0), p + new Vector3(s,0,0), c);
		DrawLine(graphicsDevice, p - new Vector3(0,s,0), p + new Vector3(0,s,0), c);
		DrawLine(graphicsDevice, p - new Vector3(0,0,s), p + new Vector3(0,0,s), c);
	}
}


