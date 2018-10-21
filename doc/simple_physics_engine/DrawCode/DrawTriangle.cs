




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




public class DrawTriangle_c
{
	static BasicEffect			s_basicEffect		= null;
	static VertexDeclaration	s_vertexDeclaration	= null;
		
		
	public static 
	void DrawTriangle(GraphicsDevice graphicsDevice, Vector3 p0, Vector3 p1, Vector3 p2, Microsoft.Xna.Framework.Graphics.Color c)
	{
		/*
		DrawLine_c.DrawLine(graphicsDevice, p0, p1, c);
		DrawLine_c.DrawLine(graphicsDevice, p1, p2, c);
		DrawLine_c.DrawLine(graphicsDevice, p2, p0, c);
		*/
		if (s_basicEffect==null)
        {  
			s_basicEffect = new BasicEffect(graphicsDevice, null);
        }
        s_basicEffect.VertexColorEnabled = false;
        s_basicEffect.Alpha              = 0.5f;
        s_basicEffect.View               = Camera_c.s_view;
        s_basicEffect.Projection         = Camera_c.s_projection;
        s_basicEffect.World              = Matrix.Identity;
        s_basicEffect.TextureEnabled	 = false;
        s_basicEffect.DiffuseColor		 = new Vector3(c.R/255.0f, c.G/255.0f, c.B/255.0f);
        s_basicEffect.EmissiveColor		 = new Vector3(0.0f, 0.0f, 0.0f);
        
		s_basicEffect.EnableDefaultLighting(); 
		s_basicEffect.DirectionalLight0.Enabled = true; 
    
        
		graphicsDevice.RenderState.AlphaBlendEnable  = true; 
		graphicsDevice.RenderState.SourceBlend		 = Blend.SourceAlpha; 
		graphicsDevice.RenderState.DestinationBlend  = Blend.InverseSourceAlpha; 
		graphicsDevice.RenderState.DepthBufferEnable = true; 
		graphicsDevice.RenderState.CullMode			 = CullMode.CullClockwiseFace; 
		graphicsDevice.RenderState.FillMode			 = FillMode.Solid;
		
		if (s_vertexDeclaration==null)
		{
			s_vertexDeclaration =
			new VertexDeclaration( graphicsDevice, Microsoft.Xna.Framework.Graphics.VertexPositionNormalTexture.VertexElements );
        }
        
        graphicsDevice.VertexDeclaration           = s_vertexDeclaration;
        

		Vector3 n = Vector3.Cross( p0-p1, p1-p2 );
        List<VertexPositionNormalTexture> points = new List<VertexPositionNormalTexture>();
        points.Add( new VertexPositionNormalTexture(p0, n, Vector2.Zero) );
        points.Add( new VertexPositionNormalTexture(p1, n, Vector2.Zero) );
        points.Add( new VertexPositionNormalTexture(p2, n, Vector2.Zero) );
        int numTris = 1;
        s_basicEffect.Begin();
        foreach( EffectPass effectPass in s_basicEffect.CurrentTechnique.Passes )
        {
            effectPass.Begin();
            graphicsDevice.DrawUserPrimitives<VertexPositionNormalTexture>(	PrimitiveType.TriangleList, 
																			points.ToArray(), 
																			0, 
																			numTris);

            effectPass.End();
        }
        s_basicEffect.End();
	}
	

	public static 
	void DrawTriangleList(GraphicsDevice graphicsDevice, 
						  Matrix worldMat, 
						  VertexPositionNormalTexture[] triPoints, 
						  Microsoft.Xna.Framework.Graphics.Color c)
	{
	    if (s_basicEffect==null)
        {  
			s_basicEffect = new BasicEffect(graphicsDevice, null);
        }
        s_basicEffect.VertexColorEnabled = false;
        s_basicEffect.Alpha              = 0.7f;
        s_basicEffect.View               = Camera_c.s_view;
        s_basicEffect.Projection         = Camera_c.s_projection;
        s_basicEffect.World              = worldMat;
		s_basicEffect.TextureEnabled	 = false;
        s_basicEffect.DiffuseColor		 = new Vector3(c.R/255.0f, c.G/255.0f, c.B/255.0f);
        s_basicEffect.EmissiveColor		 = new Vector3(0.0f, 0.0f, 0.0f);
        s_basicEffect.EnableDefaultLighting(); 
		s_basicEffect.DirectionalLight0.Enabled = true; 
        
		graphicsDevice.RenderState.AlphaBlendEnable  = true; 
		graphicsDevice.RenderState.SourceBlend		 = Blend.SourceAlpha; 
		graphicsDevice.RenderState.DestinationBlend  = Blend.InverseSourceAlpha; 
		graphicsDevice.RenderState.DepthBufferEnable = true; 
		graphicsDevice.RenderState.CullMode			 = CullMode.CullClockwiseFace; 
		graphicsDevice.RenderState.FillMode			 = FillMode.Solid;
		
		if (s_vertexDeclaration==null)
		{
			s_vertexDeclaration =
			new VertexDeclaration( graphicsDevice, VertexPositionNormalTexture.VertexElements );
        }
        graphicsDevice.VertexDeclaration           = s_vertexDeclaration;



        int numTris = triPoints.Count() / 3;
        s_basicEffect.Begin();
        foreach( EffectPass effectPass in s_basicEffect.CurrentTechnique.Passes )
        {
            effectPass.Begin();
            graphicsDevice.DrawUserPrimitives<VertexPositionNormalTexture>(	PrimitiveType.TriangleList, 
																			triPoints, 
																			0, 
																			numTris);

            effectPass.End();
        }
        s_basicEffect.End();
	}
};