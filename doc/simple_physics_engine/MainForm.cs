#define USE_IMPULSES


using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
//using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;


using System.IO;
using System.Diagnostics;
using System.Windows.Forms.Design;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

using System.Runtime.InteropServices;

namespace code
{




	public partial class MainForm : Form
	{
		[DllImport("User32.dll")]
        public static extern short GetAsyncKeyState(int vKey);
        
	    static public GraphicsDevice                   m_device         = null;
		Timer                                   m_timer;
		bool									m_addRigidBodies	= false;
    
		hires.Stopwatch                         m_stopWatch      = new hires.Stopwatch();
		float                                   m_fps            = 0;
    
		
		//Vector3									m_supportNormal	 = new Vector3(0,1,0);
		//Vector3									m_random		 = new Vector3(0,0,0);
		//Model_c[]								m_model			 = new Model_c[2];
		
		List<RigidBody_c>						m_rigidBodies	= new List<RigidBody_c>();
		
		int										m_curStep		= 0;
		List<Body_c>[]							m_prevSteps		= new List<Body_c>[100];
		
		System.Drawing.Point    m_lastMousePos	= new System.Drawing.Point(0,0);
		Vector3                 m_camRot      	= new Vector3(0,0,0);
		Vector3                 m_lastCamRot  	= new Vector3(0,0,0);
		float                   m_camDist     	= -100.0f;
		float                   m_lastCamDist 	= -100.0f;
    
		public MainForm()
		{
			InitializeComponent();
			
			textBoxStack.Text = "5";
			textBoxWallX.Text = "5";
			textBoxWallY.Text = "3";
		}
		
		
		
		void UpdateCamera()
		{
			Matrix camRot = Matrix.CreateRotationY(m_camRot.Y) * Matrix.CreateRotationX(m_camRot.X);
	        
			float aspectRatio = (float)m_device.Viewport.Width / m_device.Viewport.Height;
			Camera_c.s_view = Matrix.CreateLookAt( Vector3.Transform(new Vector3(0,0,m_camDist), camRot),
												  Vector3.Zero,
												  Vector3.Up);      
	    
			Camera_c.s_projection = Matrix.CreatePerspectiveFieldOfView(MathHelper.ToRadians(45.0f), 
																 aspectRatio, 
																 1.0f, 
																 1000.0f);
	                                               
	        
			System.Drawing.Point mp = System.Windows.Forms.Cursor.Position;
	        
			System.Drawing.Point clientMP = pictureBox.PointToClient( mp );
			bool mouseInWindow = false;
			if (clientMP.X>0 && clientMP.X<pictureBox.Width &&
				clientMP.Y>0 && clientMP.Y<pictureBox.Height)
			{
				mouseInWindow = true;
			}

			if (mouseInWindow && System.Windows.Forms.Control.MouseButtons == MouseButtons.Left)
			{
			   float mouseSpeed = 0.01f;
			   float dx = (mp.X - m_lastMousePos.X)*mouseSpeed;
			   m_camRot.Y = m_lastCamRot.Y + dx;
	           
			   float dy = (mp.Y - m_lastMousePos.Y)*mouseSpeed;
			   m_camRot.X = m_lastCamRot.X + dy;
			}
			else if (mouseInWindow && System.Windows.Forms.Control.MouseButtons == MouseButtons.Right)
			{
				float zoomSpeed = 0.5f;
				float dy = (mp.Y - m_lastMousePos.Y)*zoomSpeed;
				m_camDist = m_lastCamDist - dy;
			}
			else
			{
				m_lastCamDist  = m_camDist;
				m_lastCamRot   = m_camRot;
				m_lastMousePos = mp;
			}
		}
    
		protected override void OnLoad(EventArgs e)
		{
			base.OnLoad(e);

			PresentationParameters pp = new PresentationParameters();
			pp.BackBufferCount          = 1;
			pp.IsFullScreen             = false;
			pp.SwapEffect               = SwapEffect.Discard;
			pp.BackBufferWidth          = pictureBox.Width;
			pp.BackBufferHeight         = pictureBox.Height;
			pp.AutoDepthStencilFormat   = DepthFormat.Depth24Stencil8;
			pp.EnableAutoDepthStencil   = true;
			pp.PresentationInterval     = PresentInterval.Default;
			pp.BackBufferFormat         = SurfaceFormat.Unknown;
			pp.MultiSampleType          = MultiSampleType.None;

			m_device = new GraphicsDevice(  GraphicsAdapter.DefaultAdapter,
											DeviceType.Hardware, 
											this.pictureBox.Handle,
											pp);

			m_device.PresentationParameters.BackBufferWidth   = pictureBox.Width;
			m_device.PresentationParameters.BackBufferHeight  = pictureBox.Height;
			m_device.Reset();

			m_device.RenderState.AlphaBlendEnable	= true;
			m_device.RenderState.DestinationBlend	= Blend.InverseSourceAlpha;
			m_device.RenderState.SourceBlend		= Blend.SourceAlpha;


			//float aspectRatio = (float)m_device.Viewport.Width / m_device.Viewport.Height;
			//Camera_c.s_view = Matrix.CreateLookAt( new Vector3(0,0,20.0f),
			//                                       Vector3.Zero,
			//                                       Vector3.Up);      
	    
			//Camera_c.s_projection = Matrix.CreatePerspectiveFieldOfView(MathHelper.ToRadians(45.0f), 
			//                                                            aspectRatio, 
			//                                                            1.0f, 
			//                                                            1000.0f);
                                                             
			m_timer = new Timer();
			m_timer.Interval = 1;
			m_timer.Tick += new EventHandler(Draw);
			m_timer.Start();

			// Add ground rectangle		
				
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 5, 100)), Quaternion.Identity, new Vector3(0,-10,0), 0.0f) );

			/*
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 100, 5)), Quaternion.Identity, new Vector3(0,70, 100), 0.0f) );
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 100, 5)), Quaternion.Identity, new Vector3(0,70,-100), 0.0f) );
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(5, 100, 100)), Quaternion.Identity, new Vector3(-100, 70,0), 0.0f) );
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(5, 100, 100)), Quaternion.Identity, new Vector3( 100, 70,0), 0.0f) );



			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeSphere( 5.0f ), Quaternion.Identity, Vector3.Zero, 1.0f) );
			
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(new Vector3(5, 10, 15) ), Quaternion.Identity, new Vector3(-20,0,0), 1.0f) );
			*/

			//m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeSphere( 5.0f ), Quaternion.Identity, new Vector3(0,3,0), 1.0f) );
			
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(new Vector3(10, 5, 5) ), Quaternion.Normalize(new Quaternion(1,0,1,1)), new Vector3(0,7,0), 1.0f) );
			
	
			// So we can step back and check previous values
			m_curStep		= 0;
			for (int i=0; i<m_prevSteps.Count(); i++)
			{
				m_prevSteps[i] = new List<Body_c>();
			}
		
			//m_model[0] = new Model_c();
			//m_model[1] = new Model_c();
			
			//m_model[0].m_shape			 = new ShapeSphere( 5.0f );
			//m_model[0].m_hullMaker		 = new HullMaker(m_model[0].m_shape, -1);
			//m_model[0].m_position		 = new Vector3(8,8,0);
			
			//m_model[1].m_shape			 = new ShapeCube( new Vector3(5, 10, 15) );
			//m_model[1].m_hullMaker		 = new HullMaker(m_model[1].m_shape, -1);
			//m_model[1].m_position		 = new Vector3(-8,8,0);
		}
		
		void CheckCollisions()
		{
			// Check for any collisions
			for (int i=0; i < m_rigidBodies.Count; i++)
			{
				RigidBody_c b1 = m_rigidBodies[i];

				for (int j=i+1; j < m_rigidBodies.Count; j++)
				{
					RigidBody_c b2 = m_rigidBodies[j];
				
					float cullingRadius = b1.maxRadius + b2.maxRadius;

					if ((b1.body.x - b2.body.x).LengthSquared() > cullingRadius * cullingRadius)
					{
						continue;
					}
				
					//if (b1.body.inv_m==0.0f && b1.body.inv_m==0.0f) continue;
				
					Vector3 n  = Vector3.Zero;
					Vector3 p1 = Vector3.Zero;
					Vector3 p2 = Vector3.Zero;
					bool haveHit = 
					Intersection_c.HasIntersection(b1.collideModel, b1.body.q, b1.body.x - MyMath.Rotate(b1.body.q, b1.body.com), 
												   b2.collideModel, b2.body.q, b2.body.x - MyMath.Rotate(b2.body.q, b2.body.com), 
												   out n, 
												   out p1, 
												   out p2);	
					if (haveHit)
					{
						Arbiter_c arb = ArbiterContainer_c.FindArbiter(ref b1, ref b2);
						
						// Find the support points
						Vector3 s1 = Collision.TransformSupportVert(b1.collideModel, b1.body.q, b1.body.x - MyMath.Rotate(b1.body.q, b1.body.com), -n);
						Vector3 s2 = Collision.TransformSupportVert(b2.collideModel, b2.body.q, b2.body.x - MyMath.Rotate(b2.body.q, b2.body.com),  n);

						Vector3 pp1 = (s1 - p1) * Vector3.Dot(n, n) + p1;
						Vector3 pp2 = (s2 - p2) * Vector3.Dot(n, n) + p2;
			
						arb.AddContact(pp1, pp2, n);
						
					}												   
				}
			}
		}

		void Draw(object sender, EventArgs e)
		{	        
			m_stopWatch.Start();
			
	        UpdateCamera();
	        
			m_device.Clear(ClearOptions.Target | ClearOptions.DepthBuffer,
						   Microsoft.Xna.Framework.Graphics.Color.Blue,
						   1.0f, 0);
			
			
			this.Text = String.Format("RigidBodies: {0} FPS({1})", m_rigidBodies.Count, (int)m_fps);
			

			// Render our rigidbodies
			for (int i=0; i<m_rigidBodies.Count; i++)
			{
				// Loop around from the back so it fixes our z draw issues
				int indx = m_rigidBodies.Count - i - 1;
				m_rigidBodies[indx].renderModel.DebugDraw( m_device, m_rigidBodies[indx] );
			}
			
			float dt = 0.01f; // 100hz
			
			if (this.checkBoxRun.Checked)
			{
				Update(dt);
			}
			
			if (checkBoxDebugDraw.Checked)
			{
				ArbiterContainer_c.DebugDraw( m_device );
			}
			
			/*
			float speed = 1.0f;
			if (GetAsyncKeyState((int)System.Windows.Forms.Keys.A) != 0)	m_rigidBodies[1].body.v.X += speed;
			if (GetAsyncKeyState((int)System.Windows.Forms.Keys.D) != 0)	m_rigidBodies[1].body.v.X -= speed;
			if (GetAsyncKeyState((int)System.Windows.Forms.Keys.W) != 0)	m_rigidBodies[1].body.v.Y += speed;
			if (GetAsyncKeyState((int)System.Windows.Forms.Keys.S) != 0)	m_rigidBodies[1].body.v.Y -= speed;
			*/

			m_device.Present();
			
						
			m_stopWatch.Stop();
			float newFps = 1.0f / ((float)m_stopWatch.Elapsed);
			m_fps = m_fps + (newFps-m_fps)*0.05f;
		}
		
				
		void Update(float dt)
		{
			Contact_c.gTimeStamp++;
			
			
			//float linDrag = 0.99f;
			//float angDrag = 0.98f;
			
			/*
			//*****Integrate******
			for (int i=0; i < m_rigidBodies.Count; i++)
			{
				Body_c b = m_rigidBodies[i].body;

				b.x += b.v * dt;
				Debug_c.Valid(b.x);
				
				b.v += new Vector3(0, -400.8f, 0) * dt * b.m;
				Debug_c.Valid(b.v);

				Quaternion temp = MyMath.Mult(new Quaternion(b.omega.X, b.omega.Y, b.omega.Z, 0), b.q) * 0.5f;
				b.q = b.q + temp * dt;
				b.q.Normalize();

				b.v			*= linDrag;
				b.omega		*= angDrag;
				Debug_c.Valid(b.omega);
			}
			*/
	
			
			#if USE_IMPULSES

			m_prevSteps[ m_curStep ].Clear();
			for (int i=0; i < m_rigidBodies.Count; i++)
			{
				m_prevSteps[ m_curStep ].Add( new Body_c(m_rigidBodies[i].body) );
			}
			m_curStep = (m_curStep+1) % m_prevSteps.Count();
				
				
			// Process all collisions
			//if (false)
			{
				
				
				for (int i=0; i < m_rigidBodies.Count; i++)
				{
					Body_c b = m_rigidBodies[i].body;
					b.StoreState();
				}
				for (int i=0; i < m_rigidBodies.Count; i++)
				{
					Body_c b = m_rigidBodies[i].body;
					b.UpdateVel( dt );
					b.UpdatePos( dt );
				}
				
				CheckCollisions();
				ArbiterContainer_c.SortInYDirection();
				
				for (int i=0; i < m_rigidBodies.Count; i++)
				{
					Body_c b = m_rigidBodies[i].body;
					b.RestoreState();
				}
				
				for (int iteration=0; iteration < 4; iteration++)
				{
					for (int k=0; k<ArbiterContainer_c.arbiterArray.Count; k++)
					{
						for (int a=0; a<ArbiterContainer_c.arbiterArray[k].arbiter.contacts.Count; a++)
						{
							Contact_c contact = ArbiterContainer_c.arbiterArray[k].arbiter.contacts[a];


							bool zapIt = false;

							if ( contact.Distance() > 0.01f && Contact_c.gTimeStamp != contact.timeStamp )
							{
								zapIt = true;
							}

							if ( zapIt )
							{
								ArbiterContainer_c.arbiterArray[k].arbiter.contacts.RemoveAt( a );
								a--;
								continue;
							}
							
							contact.constraint.GenerateImpulse( 0.9f );					
						}
					}
				}
			}
			
			// Update Velocity
			for (int i=0; i < m_rigidBodies.Count; i++)
			{
				Body_c b = m_rigidBodies[i].body;
				b.UpdateVel( dt );
			}
			
			
			
			// Process Contacts
			//if (false)
			{
				
				for (int i=0; i < m_rigidBodies.Count; i++)
				{
					Body_c b = m_rigidBodies[i].body;
					b.StoreState();
				}
				
				for (int i=0; i < m_rigidBodies.Count; i++)
				{
					Body_c b = m_rigidBodies[i].body;
					b.UpdatePos(dt);
				}
				
				CheckCollisions();
				ArbiterContainer_c.SortInYDirection();
				
				for (int i=0; i < m_rigidBodies.Count; i++)
				{
					Body_c b = m_rigidBodies[i].body;
					b.RestoreState();
				}
				
				
				// For the shock propogation - should do a sort on order
				// but since this is our test code, I know I've added the rigidbodies
				// to the list in the order from bottom to top
				
				//if (false)
				for (int iteration=0; iteration < 90; iteration++)
				{
					for (int i=0; i < m_rigidBodies.Count; i++)
					{
						RigidBody_c b1 = m_rigidBodies[i];

						b1.body.inv_m = 0.0f;
						
						//int j = i+1;
						//if (j>m_rigidBodies.Count-1) continue;
						
						for (int j=i+1; j < m_rigidBodies.Count; j++)
						{
							RigidBody_c b2 = m_rigidBodies[j];
						
							float cullingRadius = b1.maxRadius + b2.maxRadius;

							if ((b1.body.x - b2.body.x).LengthSquared() > cullingRadius * cullingRadius)
							{
								continue;
							}

							b1.body.StoreState();
							b2.body.StoreState();
							
							b1.body.UpdatePos(dt);
							b2.body.UpdatePos(dt);
							
							/*
							for (int mm=0; mm < m_rigidBodies.Count; mm++)
							{
								Body_c b = m_rigidBodies[mm].body;
								b.StoreState();
							}
							for (int mm=0; mm < m_rigidBodies.Count; mm++)
							{
								Body_c b = m_rigidBodies[mm].body;
								b.UpdatePos(dt);
							}
							*/
						
							Vector3 n  = Vector3.Zero;
							Vector3 p1 = Vector3.Zero;
							Vector3 p2 = Vector3.Zero;
							bool haveHit = 
							Intersection_c.HasIntersection(b1.collideModel, b1.body.q, b1.body.x - MyMath.Rotate(b1.body.q, b1.body.com), 
														   b2.collideModel, b2.body.q, b2.body.x - MyMath.Rotate(b2.body.q, b2.body.com), 
														   out n, 
														   out p1, 
														   out p2);	
							/*							   
							for (int mm=0; mm < m_rigidBodies.Count; mm++)
							{
								Body_c b = m_rigidBodies[mm].body;
								b.RestoreState();
							}
							*/
							
							b1.body.RestoreState();
							b2.body.RestoreState();
							
							if (haveHit)
							{
								Contact_c c = new Contact_c(b1, b2, p1,  p2, n );
								c.constraint.GenerateImpulse( 0.0f  );
							}
						}
					}
					
					for (int i=0; i < m_rigidBodies.Count; i++)
					{
						m_rigidBodies[i].body.inv_m = m_rigidBodies[i].body.inv_m_back;
					}
				}
				
				
				
				int numContactSteps = 2;
				if (false)
				for (int step=0; step<numContactSteps; step++)
				{	
					for (int iteration=0; iteration < 5; iteration++)
					{
						for (int k=0; k<ArbiterContainer_c.arbiterArray.Count; k++)
						{
							for (int a=0; a<ArbiterContainer_c.arbiterArray[k].arbiter.contacts.Count; a++)
							{
								/*
								for (int i=0; i < m_rigidBodies.Count; i++)
								{
									Body_c b = m_rigidBodies[i].body;
									b.StoreState();
								}
								for (int i=0; i < m_rigidBodies.Count; i++)
								{
									Body_c b = m_rigidBodies[i].body;
									b.UpdatePos(dt);
								}
								CheckCollisions();
								for (int i=0; i < m_rigidBodies.Count; i++)
								{
									Body_c b = m_rigidBodies[i].body;
									b.RestoreState();
								}
								*/
					
								Contact_c contact = ArbiterContainer_c.arbiterArray[k].arbiter.contacts[a];
								
								bool zapIt = false;

								if ( contact.Distance() > 0.01f && Contact_c.gTimeStamp != contact.timeStamp )
								{
									zapIt = true;
								}

								if ( zapIt )
								{
									ArbiterContainer_c.arbiterArray[k].arbiter.contacts.RemoveAt( a );
									a--;
									continue;
								}
								
								float ee = (numContactSteps - step - 1)* -1.0f / (float)numContactSteps;
	
								//if ( Math.Abs(contact.timeStamp - Contact_c.gTimeStamp) > 2 ) ee = -0.8f;
								
								//ee = 0.0f;
								
								contact.constraint.GenerateImpulse( ee  );
							}
						}
					}
				}
				
				
				// Shock propogation
				if (false)
				{
					for (int iteration=0; iteration < 5; iteration++)
					{
						for (int k=0; k<ArbiterContainer_c.arbiterArray.Count; k++)
						{
							for (int a=0; a<ArbiterContainer_c.arbiterArray[k].arbiter.contacts.Count; a++)
							{
								Contact_c contact = ArbiterContainer_c.arbiterArray[k].arbiter.contacts[a];
								
								if (ArbiterContainer_c.arbiterArray[k].arbiter.contacts.Count>0)
								{
									ArbiterContainer_c.arbiterArray[k].arbiter.contacts[0].b1.body.inv_m = 0.0f;
								}
								
							
								bool zapIt = false;

								if ( contact.Distance() > 0.1f && Contact_c.gTimeStamp != contact.timeStamp )
								{
									zapIt = true;
								}
								
								//if ( Math.Abs(contact.timeStamp - Contact_c.gTimeStamp) > 30 ) zapIt = true;

								if ( zapIt )
								{
									ArbiterContainer_c.arbiterArray[k].arbiter.contacts.RemoveAt( a );
									a--;
									continue;
								}
								
								contact.constraint.GenerateImpulse( 0.0f );
							}
							
							
						}
						
						for (int i=0; i < m_rigidBodies.Count; i++)
						{
							m_rigidBodies[i].body.inv_m = m_rigidBodies[i].body.inv_m_back;
						}
					}				
				}
			}
			
			// Update Positions
			for (int i=0; i < m_rigidBodies.Count; i++)
			{				
				Body_c b = m_rigidBodies[i].body;
				b.UpdatePos( dt );
				
				//b.v			*= linDrag;
				b.omega		*= 0.95f; //angDrag;
				Debug_c.Valid(b.omega);
			}	
			#endif //USE_IMPULSES
			
			

			
			
			// Resolve momentum exchanges
			#if !USE_IMPULSES
			
			
			for (int i=0; i < m_rigidBodies.Count; i++)
			{
				Body_c b = m_rigidBodies[i].body;
				b.UpdateVel( dt );
				b.UpdatePos( dt );
			}
			
			CheckCollisions();
			ArbiterContainer_c.SortInYDirection();
			
			for (int iteration=0; iteration < 10; iteration++)
			{
				for (int k=0; k<ArbiterContainer_c.arbiterArray.Count; k++)
				{
					for (int a=0; a<ArbiterContainer_c.arbiterArray[k].arbiter.contacts.Count; a++)
					{
						Contact_c contact = ArbiterContainer_c.arbiterArray[k].arbiter.contacts[a];


						bool zapIt = false;

						if ( contact.Distance() > 0.1f && Contact_c.gTimeStamp != contact.timeStamp )
						{
							zapIt = true;
						}

						if ( zapIt )
						{
							ArbiterContainer_c.arbiterArray[k].arbiter.contacts.RemoveAt( a );
							a--;
							continue;
						}

						if (iteration == 0)
						{
							contact.constraint.PrepareForIteration();
						}
						else
						{
							contact.constraint.Iterate();
						}
					
					}
				}
			}
			#endif

		}
		
		

		private void buttonEllipsoid_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeEllipsoid( new Vector3(5, 10, 5) );
			ModifyRigidBody(shape);
		}

		private void buttonCube_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeCube( new Vector3(5, 10, 15) );
			ModifyRigidBody(shape);
		}

		private void buttonFootball_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeFootball( 5, 10 );
			ModifyRigidBody(shape);
		}

		private void buttonSphere_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeSphere( 5 );
			ModifyRigidBody(shape);
		}

		private void buttonPoint_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapePoint( Vector3.Zero );
			ModifyRigidBody(shape);
		}

		private void buttonRectPlane_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeRectanglePlane( 5, 5 );
			ModifyRigidBody(shape);
		}

		private void buttonLine_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeSegment( 5 );
			ModifyRigidBody(shape);
		}

		private void buttonCircle_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeCirclePlane( 10 );
			ModifyRigidBody(shape);
		}

		private void buttonCyclinder_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeCyclinder(5, 5);
			ModifyRigidBody(shape);
			
		}
		
		public void ModifyRigidBody(Shape shape)
		{
			ArbiterContainer_c.Clear();
			
			if (m_addRigidBodies)
			{
				//for (int i=1; i<10; i++)
				int i = 1;
				m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( shape, Quaternion.Identity, new Vector3(20,30*i,10), 1.0f) );
			}
			else 
			{
				m_rigidBodies[m_rigidBodies.Count-1] = HelperRigidBody_c.CreateRigidBody( shape, Quaternion.Identity, new Vector3(0,10,0), 1.0f) ;
			}	
		}

		private void checkAddShape_CheckedChanged(object sender, EventArgs e)
		{
			if (checkAddShape.Checked)
			{
				m_addRigidBodies = true;
			}
			else 
			{
				m_addRigidBodies = false;
			}
		}

		private void buttonStack_Click(object sender, EventArgs e)
		{
			int num = 5;
			if (int.TryParse(textBoxStack.Text, out num)==false)
			{
				return;
			}
			
			ArbiterContainer_c.Clear();
			m_rigidBodies.Clear();
			
			// Add ground rectangle			
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 5, 100)), Quaternion.Identity, new Vector3(0,-10,0), 0.0f) );

			// Stack cubes
			for (int i=0; i<num; i++)
			{
				m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(new Vector3(5, 5, 5) ), Quaternion.Identity, new Vector3(0,10*i,0), 1.0f) );
			}
		}

		private void buttonWall_Click(object sender, EventArgs e)
		{
			int numX = 5;
			int numY = 5;
			if (!int.TryParse(textBoxWallX.Text, out numX)) return;
			if (!int.TryParse(textBoxWallY.Text, out numY)) return;
			
			ArbiterContainer_c.Clear();
			m_rigidBodies.Clear();
			
			// Add ground rectangle			
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube( new Vector3(100, 5, 100)), Quaternion.Identity, new Vector3(0,-10,0), 0.0f) );


			// Wall cubes
			for (int y=0; y<numY; y++)
			{
				for (int x=0; x<numX; x++)
				{
					float xx = 0;
					if (y%2==0) xx = 5.0f;
					
					Vector3 cubeSize = new Vector3(4.5f, 5, 5);
					Vector3 cubePos  = new Vector3(x*10+xx, 10*y,0);
					m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( new ShapeCube(cubeSize), Quaternion.Identity, cubePos, 1.0f) );
				}
			}
		}

		private void buttonStep_Click(object sender, EventArgs e)
		{
			Update( 0.01f );

		}

		private void buttonPrevStep_Click(object sender, EventArgs e)
		{
			m_curStep = m_curStep-1;
			if (m_curStep<0) m_curStep = m_prevSteps.Count()-1;
			
			for (int i=0; i < m_rigidBodies.Count && i<m_prevSteps.Count(); i++)
			{
				if (i > m_prevSteps[ m_curStep ].Count()-1) continue;
				
				m_rigidBodies[i].body = new Body_c(m_prevSteps[ m_curStep ][i]);
			}
			
			//ArbiterContainer_c.Clear();
			CheckCollisions();
		}

		private void buttonShootBall_Click(object sender, EventArgs e)
		{
			Shape shape = new ShapeSphere( 5 );
			m_rigidBodies.Add( HelperRigidBody_c.CreateRigidBody( shape, Quaternion.Identity, new Vector3(0,30,-100), 1.0f) );
			
			m_rigidBodies[ m_rigidBodies.Count-1 ].body.v += new Vector3(0,0,200);

		}

		private void buttonSeaSaw_Click(object sender, EventArgs e)
		{
			ArbiterContainer_c.Clear();
			FunTestCases_c.BuildSeeSaw( ref m_rigidBodies );
		}

		private void buttonBowling_Click(object sender, EventArgs e)
		{
			ArbiterContainer_c.Clear();
			FunTestCases_c.BuildBowling( ref m_rigidBodies );

		}

		private void buttonMarbles_Click(object sender, EventArgs e)
		{
			ArbiterContainer_c.Clear();
			FunTestCases_c.BuildMarbles( ref m_rigidBodies );

		}

		private void buttonDominos_Click(object sender, EventArgs e)
		{
			ArbiterContainer_c.Clear();
			FunTestCases_c.BuildDominos( ref m_rigidBodies );
		}
	}
}
