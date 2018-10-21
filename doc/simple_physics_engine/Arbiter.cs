// Arbiter.cs

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
	Each Arbiter maintains a list of contacts between two RigidBody objects.

	As new points of contact between the pair are discovered, they are added
	to the Arbiter with a call to AddContact().
*/


public class ArbiterContainer_c
{
	public class ArbiterItem_c
	{
		public ArbiterItem_c(ArbiterKey_c key, Arbiter_c arbiter)
		{
			this.key		= key;
			this.arbiter	= arbiter;
		}
		public ArbiterKey_c		key;
		public Arbiter_c		arbiter;
	};
	public static List<ArbiterItem_c> arbiterArray = new List<ArbiterItem_c>();
	
	public static 
	void Clear()
	{
		arbiterArray = new List<ArbiterItem_c>();
	}
	
	public static 
	Arbiter_c FindArbiter( ref RigidBody_c rb1, ref RigidBody_c rb2 )
	{
		ArbiterKey_c key = new ArbiterKey_c(ref rb1, ref rb2);
		for (int i=0; i<arbiterArray.Count; i++)
		{
			if (arbiterArray[i].key.b1.id==key.b1.id && 
				arbiterArray[i].key.b2.id==key.b2.id)
			{
				return arbiterArray[i].arbiter;
			}
		}
		
		arbiterArray.Add( new ArbiterItem_c(key, new Arbiter_c(ref rb1, ref rb2)) );
		return arbiterArray[ arbiterArray.Count-1 ].arbiter;
	}
	
	static public 
	void DebugDraw(GraphicsDevice graphicsDevice)
	{
		for (int i=0; i<arbiterArray.Count; i++)
		{
			for (int k=0; k<arbiterArray[i].arbiter.contacts.Count; k++)
			{
				RigidBody_c b1 = arbiterArray[i].arbiter.contacts[k].b1;
				RigidBody_c b2 = arbiterArray[i].arbiter.contacts[k].b2;
				
				Vector3 n = arbiterArray[i].arbiter.contacts[k].normal;
				Vector3 local1 = arbiterArray[i].arbiter.contacts[k].local1;
				Vector3 local2 = arbiterArray[i].arbiter.contacts[k].local2;
				
				Vector3 p1 = MyMath.Rotate( b1.body.q, local1 ) + b1.body.x;
				Vector3 p2 = MyMath.Rotate( b2.body.q, local2 ) + b2.body.x;
		

				DrawLine_c.DrawLine( graphicsDevice, p1, p1 + n*10.0f, Microsoft.Xna.Framework.Graphics.Color.Yellow);
				DrawLine_c.DrawCross( graphicsDevice, p1, 2.0f );
				
				DrawLine_c.DrawLine( graphicsDevice, p2, p2 + n*10.0f, Microsoft.Xna.Framework.Graphics.Color.Aqua);
				DrawLine_c.DrawCross( graphicsDevice, p2, 2.0f );
			}
		}
	}
	
	static public 
	void SortInYDirection()
	{
		for (int i=0; i<arbiterArray.Count; i++)
		{
			for (int k=i+1; k<arbiterArray.Count; k++)
			{
				float yiLowest = Math.Min( arbiterArray[i].arbiter.b1.body.x.Y, arbiterArray[i].arbiter.b2.body.x.Y );
				float ykLowest = Math.Min( arbiterArray[k].arbiter.b1.body.x.Y, arbiterArray[k].arbiter.b2.body.x.Y );
				
				if (yiLowest > ykLowest)
				{
					ArbiterItem_c tmp = arbiterArray[i];
					arbiterArray[i] = arbiterArray[k];
					arbiterArray[k] = tmp;
				}
			}
		}
	}
};


public class Arbiter_c
{
	public	Arbiter_c(ref RigidBody_c in1, ref RigidBody_c in2)
	{
		b1 = in1;
		b2 = in2;
	}
	
	~Arbiter_c()
	{
		contacts.Clear();
	}

	public void AddContact(Vector3 p1, Vector3 p2, Vector3 normal)
	{
		Vector3 point = (p1 + p2) * 0.5f;
		
		int foundContact = -1;
		for (int i=0; i<contacts.Count; i++)
		{
			if ((contacts[i].point - point).LengthSquared() < 1.0f )
			{
				foundContact = i;
				break;
			}
		}
		
		if (foundContact<0)
		{
			contacts.Add( new Contact_c(b1, b2, p1, p2, normal) );
		}
		else 
		{
			contacts[foundContact].Update(  p1,  p2,  normal );
		}
	}

	public RigidBody_c		b1;
	public RigidBody_c		b2;
	public List<Contact_c>	contacts	= new List<Contact_c>();
};


/*
	To find the Arbiter for two RigidBodies, build an ArbiterKey for the two 
	bodies and use it as the lookup key.
*/
  
public class ArbiterKey_c
{
	public ArbiterKey_c(ref RigidBody_c b1, ref RigidBody_c b2)
	{
		if ( b1.id < b2.id )
		{
			this.b1 = b1;
			this.b2 = b2;
		}
		else
		{
			this.b1 = b2;
			this.b2 = b1;
		}
	}

	public RigidBody_c	b1;
	public RigidBody_c	b2;
};


public class Contact_c
{
	public static int   gTimeStamp = 0;
	public static float gTimeStep  = 0.01f;
	public static float gTimeRatio = 1.00f;
	
	
	public ContactConstraint_c	constraint;
	public Vector3				point;
	public float				timeStamp;
	public RigidBody_c			b1;
	public RigidBody_c			b2;
	public Vector3				local1;
	public Vector3				local2;
	public float				distStart;
	public Vector3				normal;

	public Contact_c(RigidBody_c b1, RigidBody_c b2, Vector3 p1, Vector3 p2, Vector3 n)
	{
		this.b1		= b1;
		this.b2		= b2;
		this.normal = n;

		this.local1 = MyMath.Rotate( MyMath.Conjugate(b1.body.q), p1 - b1.body.x );
		this.local2 = MyMath.Rotate( MyMath.Conjugate(b2.body.q), p2 - b2.body.x );
		this.distStart = 0.0f;
		this.distStart = this.Distance();

		this.constraint = new ContactConstraint_c(ref b1.body, ref b2.body, ref local1, ref local2, ref n);
		this.point = 0.5f * (p1 + p2);
		this.timeStamp = Contact_c.gTimeStamp;
	}
	~Contact_c()
	{
		constraint = null;
	}


	public void Update(Vector3 p1, Vector3 p2, Vector3 n)
	{
		this.point  = (p1 + p2) * 0.5f;
		this.normal = n;

		this.local1 = MyMath.Rotate( MyMath.Conjugate(b1.body.q), ( p1 - b1.body.x ));
		this.local2 = MyMath.Rotate( MyMath.Conjugate(b2.body.q), ( p2 - b2.body.x ));
		this.distStart = 0.0f;
		this.distStart = this.Distance();

		this.constraint.Update(ref local1, ref local2, ref n);
		this.timeStamp = gTimeStamp;
	}
	
	public
	float Distance()
	{
		Vector3 p1 = MyMath.Rotate(b1.body.q, local1 ) + b1.body.x;		
		Vector3 p2 = MyMath.Rotate(b2.body.q, local2 ) + b2.body.x;	
		//return (p1 - p2).Length();

		float val = Math.Abs((p1-p2).Length() - distStart);
		return val;
	}
};



/*
	Constraint

	A Constraint represents one or more constraints on the physical motion of
	one or more Body objects.

	Common examples of Constraints include joints and physical contact.
*/

public abstract class Constraint_c
{
	public abstract void PrepareForIteration		();
	public abstract void Iterate					();
	public abstract void Draw						();
};


/*
	ContactConstraint

	This Constraint represents contact between two Body objects.  The two
	bodies are allowed to move away from each other along their contact
	normal, but they cannot move toward each other.

	Movement perpendicular to the normal results in frictional resistance.
*/

public class ContactConstraint_c : Constraint_c
{
	public ContactConstraint_c(ref Body_c b1, ref Body_c b2, ref Vector3 r1, ref Vector3 r2, ref Vector3 normal)
	{
		m_body1							= b1;
		m_body2							= b2;
		m_r1							= r1;
		m_r2							= r2;
		m_velocityConstraintDirection	= normal;
		m_beta							= 1.0f;
		m_cachedMomentum				= 0;
		m_cachedTangentMomentum			= Vector3.Zero;
	}
	
	public override void PrepareForIteration()
	{
		if (m_body1.inv_m==0.0f && m_body2.inv_m==0.0f)
		{
			return;
		}
		
		Vector3 r1 = MyMath.Rotate(m_body1.q, m_r1);
		Vector3 r2 = MyMath.Rotate(m_body2.q, m_r2);

		Vector3 x1 = m_body1.x + r1;
		Vector3 x2 = m_body2.x + r2;

		// Compute the positional constraint error (scaled by the Baumgarte coefficient 'm_beta')
	//	m_beta = 0.98f / gTimeStep;
		//m_beta = 0.5f / Contact_c.gTimeStep;
		m_beta = 0.5f / Contact_c.gTimeStep;
	
		m_positionError = m_beta * Vector3.Dot((x2 - x1), m_velocityConstraintDirection);

		// Add a boundary layer to the position error -- this will ensure the objects remain in contact
		//m_positionError -= 1.0f;
		m_positionError -= 1.0f;
			
		// The velocity constraint direction is aligned with the contact normal
		// (This represents how much angular velocity we get for every unit of momentum transferred along the constraint direction)
		m_invMoment1 = Vector3.Transform(Vector3.Cross(r1, m_velocityConstraintDirection), m_body1.inv_I);
		m_invMoment2 = Vector3.Transform(Vector3.Cross(r2, m_velocityConstraintDirection), m_body2.inv_I);
		
		Debug_c.Valid(m_invMoment1);
		Debug_c.Valid(m_invMoment2);

		// Compute effective mass of the constraint system -- this is a measure of how easy it
		// is to accelerate the contact points apart along the constraint direction -- it's analogous
		// to effective resistance in an electric circuit [i.e., 1 / (1/R1 + 1/R2)]
		m_effectiveMass =
			1.0f /
			(
				m_body1.inv_m +
				m_body2.inv_m +
				Vector3.Dot(m_velocityConstraintDirection,
				(
					Vector3.Cross(m_invMoment1, r1) +
					Vector3.Cross(m_invMoment2, r2)
				))
			);

		// Convert last frame's momentum to momentum for the new time step
		float timeRatio = Contact_c.gTimeRatio;
		m_cachedMomentum *= Contact_c.gTimeRatio;
		m_cachedTangentMomentum *= Contact_c.gTimeRatio;
		
		// Apply last frame's momentum
		m_body1.v -= m_cachedMomentum * m_velocityConstraintDirection * m_body1.inv_m;
		m_body2.v += m_cachedMomentum * m_velocityConstraintDirection * m_body2.inv_m;
		m_body1.omega -= m_cachedMomentum * m_invMoment1;
		m_body2.omega += m_cachedMomentum * m_invMoment2;
		
		Debug_c.Valid(m_body1.v);
		Debug_c.Valid(m_body2.v);
		Debug_c.Valid(m_body1.omega );
		Debug_c.Valid(m_body2.omega );

		m_cachedTangentMomentum = Vector3.Zero;
	}
	
	public override void Iterate()
	{
		if (m_body1.inv_m==0.0f && m_body2.inv_m==0.0f)
		{
			return;
		}
		
		// Compute the relative velocity between the bodies
		Vector3 relativeVelocity = (m_body2.v + Vector3.Cross(m_body2.omega, MyMath.Rotate(m_body2.q, m_r2))) - 
								   (m_body1.v + Vector3.Cross(m_body1.omega, MyMath.Rotate(m_body1.q, m_r1)));

		// Project the relative velocity onto the constraint direction
		float velocityError = Vector3.Dot(relativeVelocity, m_velocityConstraintDirection);

		// Compute the velocity delta needed to satisfy the constraint
		float deltaVelocity = - velocityError - m_positionError;

		// Compute the momentum to be exchanged to correct velocities
		float momentumPacket = deltaVelocity * m_effectiveMass;

		// Clamp the momentum packet to reflect the fact that the contact can only push the objects apart
		momentumPacket = Math.Min(momentumPacket, -m_cachedMomentum);

		Vector3 momentumPacketWithDir = momentumPacket * m_velocityConstraintDirection;

		// Exchange the correctional momentum between the bodies
		m_body1.v -= momentumPacketWithDir * m_body1.inv_m;
		m_body2.v += momentumPacketWithDir * m_body2.inv_m;
		m_body1.omega -= momentumPacket * m_invMoment1;
		m_body2.omega += momentumPacket * m_invMoment2;
		Debug_c.Valid(m_body1.omega);
		Debug_c.Valid(m_body2.omega);

		// Test code
		//Vector3 newRelativeVelocity = (m_body2.v + Vector3.Cross(m_body2.omega, MyMath.Rotate(m_body2.q, m_r2))) - 
		//							  (m_body1.v + Vector3.Cross(m_body1.omega, MyMath.Rotate(m_body1.q, m_r1)));
		//float newVelocityError = Vector3.Dot(newRelativeVelocity, m_velocityConstraintDirection);

		// Accumulate the momentum for next frame
		m_cachedMomentum += momentumPacket;

		///
		// FRICTION

		//if (gFriction)
		{
			relativeVelocity = (m_body2.v + Vector3.Cross(m_body2.omega, MyMath.Rotate(m_body2.q, m_r2))) - 
							   (m_body1.v + Vector3.Cross(m_body1.omega, MyMath.Rotate(m_body1.q, m_r1)));

			Vector3 tangentVelocityDirection = relativeVelocity - Vector3.Dot(relativeVelocity,m_velocityConstraintDirection) * m_velocityConstraintDirection;
			if (tangentVelocityDirection.LengthSquared()<0.0001f) return;
			Debug_c.Valid(tangentVelocityDirection);
			tangentVelocityDirection.Normalize();

			float tangentVelocityError = Vector3.Dot(relativeVelocity, tangentVelocityDirection);

			Vector3 r1 = MyMath.Rotate(m_body1.q, m_r1);
			Vector3 r2 = MyMath.Rotate(m_body2.q, m_r2);

			Vector3 invMoment1 =  Vector3.Transform(Vector3.Cross(r1, tangentVelocityDirection), m_body1.inv_I);
			Vector3 invMoment2 =  Vector3.Transform(Vector3.Cross(r2, tangentVelocityDirection), m_body2.inv_I);
			Debug_c.Valid(invMoment1);
			Debug_c.Valid(invMoment2);

			// Compute effective mass of the constraint system -- this is a measure of how easy it
			// is to accelerate the contact points apart along the constraint direction -- it's analogous
			// to effective resistance in an electric circuit [i.e., 1 / (1/R1 + 1/R2)]
			float effectiveMass =
				1.0f /
				(
					m_body1.inv_m +
					m_body2.inv_m +
					Vector3.Dot(tangentVelocityDirection,
					(
						Vector3.Cross(invMoment1, r1) +
						Vector3.Cross(invMoment2, r2)
					))
				);

			float tangentDeltaVelocity = - tangentVelocityError;

			float tangentMomentumPacket = tangentDeltaVelocity * effectiveMass;

			tangentMomentumPacket = Math.Max( tangentMomentumPacket, m_cachedMomentum * 0.5f );

			Vector3 tangentMomentumPacketWithDir = tangentMomentumPacket * tangentVelocityDirection;

			// Exchange the correctional momentum between the bodies
			m_body1.v -= tangentMomentumPacketWithDir * m_body1.inv_m;
			m_body2.v += tangentMomentumPacketWithDir * m_body2.inv_m;
			m_body1.omega -= tangentMomentumPacket * invMoment1;
			m_body2.omega += tangentMomentumPacket * invMoment2;
			
			Debug_c.Valid(m_body1.v);
			Debug_c.Valid(m_body2.v);
			Debug_c.Valid(m_body1.omega );
			Debug_c.Valid(m_body2.omega );

			m_cachedTangentMomentum = tangentMomentumPacketWithDir;
		}
	}
	
	public void GenerateImpulse(float e /*Bouncyness (Coefficient of restitution)*/ )
	{
		if (m_body1.inv_m==0.0f && m_body2.inv_m==0.0f)
		{
			return;
		}

		Vector3 v1 = m_body1.v + Vector3.Cross(m_body1.omega, MyMath.Rotate(m_body1.q, m_r1));
		Vector3 v2 = m_body2.v + Vector3.Cross(m_body2.omega, MyMath.Rotate(m_body2.q, m_r2));
		
		
		// Compute the relative velocity between the bodies
		Vector3 relativeVelocity = v2 - v1; 
		
		// If the objects are moving away from each other we dont need to apply an impulse					
		float relativeMovement = Vector3.Dot(relativeVelocity, m_velocityConstraintDirection);
        if (relativeMovement < -0.01f) 
        {
			return;
        }

        
		Vector3 r1 = MyMath.Rotate(m_body1.q, m_r1);
		Vector3 r2 = MyMath.Rotate(m_body2.q, m_r2);

		//Vector3 r1 = m_body1.x + MyMath.Rotate(m_body1.q, m_r1);
		//Vector3 r2 = m_body2.x + MyMath.Rotate(m_body2.q, m_r2);
		
		//Vector3 r1 = m_r1;
		//Vector3 r2 = m_r2;
		
		Matrix orientationMatrix1 = Matrix.CreateFromQuaternion(m_body1.q);
		Matrix inverseOrientationMatrix1 = Matrix.Transpose( orientationMatrix1 );
		Matrix inverseWorldInertiaMatrix1 = orientationMatrix1 * m_body1.inv_I * inverseOrientationMatrix1;
		//Matrix inverseWorldInertiaMatrix1 = inverseOrientationMatrix1 * m_body1.inv_I * orientationMatrix1;
		//Matrix inverseWorldInertiaMatrix1 = m_body1.inv_I;
		
		Matrix orientationMatrix2 = Matrix.CreateFromQuaternion(m_body2.q);
		Matrix inverseOrientationMatrix2 = Matrix.Transpose( orientationMatrix2 );
		Matrix inverseWorldInertiaMatrix2 = orientationMatrix2 * m_body2.inv_I * inverseOrientationMatrix2;
		//Matrix inverseWorldInertiaMatrix2 = inverseOrientationMatrix2 * m_body2.inv_I * orientationMatrix2;
		//Matrix inverseWorldInertiaMatrix2 = m_body2.inv_I;
		
                        
		Vector3 a1 = Vector3.Transform(Vector3.Cross(r1, m_velocityConstraintDirection), inverseWorldInertiaMatrix1);
		Vector3 a2 = Vector3.Transform(Vector3.Cross(r2, m_velocityConstraintDirection), inverseWorldInertiaMatrix2);
			   
		//Vector3 a1 = Vector3.Transform(Vector3.Cross(r1, m_velocityConstraintDirection), m_body1.inv_I);
		//Vector3 a2 = Vector3.Transform(Vector3.Cross(r2, m_velocityConstraintDirection), m_body2.inv_I);
		
		float kn =	m_body1.inv_m + m_body2.inv_m + 
					Vector3.Dot( m_velocityConstraintDirection, Vector3.Cross(a1, r1) ) + 
					Vector3.Dot( m_velocityConstraintDirection, Vector3.Cross(a2, r2) );
		
		float pn = (1 + e) / kn;				
								   								   
		Vector3 J = -m_velocityConstraintDirection*relativeMovement*(1 + e) / kn;
		

		//m_velocityConstraintDirection = Vector3.Normalize( m_velocityConstraintDirection );
		//J = Vector3.Dot( J, m_velocityConstraintDirection ) * m_velocityConstraintDirection;
		

		//J = Vector3.Normalize( J ) * len;
		//float len2 = J.Length();
		
            
		m_body1.v		-= J * m_body1.inv_m;
		m_body2.v		+= J * m_body2.inv_m;
		Vector3 oldOmega1 = m_body1.omega;
		Vector3 oldOmega2 = m_body2.omega;
		m_body1.omega	= oldOmega1 - Vector3.Transform(Vector3.Cross(r1, J), inverseWorldInertiaMatrix1);
		m_body2.omega	= oldOmega2 + Vector3.Transform(Vector3.Cross(r2, J), inverseWorldInertiaMatrix2);
		//m_body1.omega	= oldOmega1 - Vector3.Transform(Vector3.Cross(r1, J), m_body1.inv_I);
		//m_body2.omega	= oldOmega2 + Vector3.Transform(Vector3.Cross(r2, J), m_body2.inv_I);
		
		Debug_c.Valid(m_body1.v);
		Debug_c.Valid(m_body2.v);
		Debug_c.Valid(m_body1.omega);
		Debug_c.Valid(m_body2.omega);
		
		// Tangent Friction
		//if (false)
		{
			// Work out our tangent vector, with is perpendicular
			// to our collision normal
			Vector3 tangent = relativeVelocity - Vector3.Dot(relativeVelocity, m_velocityConstraintDirection) * m_velocityConstraintDirection;
			
			if (tangent.LengthSquared()< 0.00001f)
			{
				return;
			}
			tangent.Normalize();
			
			Vector3 at1 = Vector3.Transform(Vector3.Cross(r1, tangent), inverseWorldInertiaMatrix1);
			Vector3 at2 = Vector3.Transform(Vector3.Cross(r2, tangent), inverseWorldInertiaMatrix2);
						
			float ktn =	m_body1.inv_m + m_body2.inv_m + 
						Vector3.Dot( tangent, Vector3.Cross(at1, r1) ) + 
						Vector3.Dot( tangent, Vector3.Cross(at2, r2) );
			
			//float mu = 0.5f;
						
			float pt = (1 + e) / ktn;
			
			pt = MathHelper.Clamp(pt, -0.3f*pn, 0.3f*pn);
			Vector3 Jt = -tangent * pt;
			
			
			
			m_body1.v		-= Jt * m_body1.inv_m;
			m_body2.v		+= Jt * m_body2.inv_m;
			Vector3 oldOmegat1 = m_body1.omega;
			Vector3 oldOmegat2 = m_body2.omega;
			m_body1.omega	= oldOmegat1 - Vector3.Transform(Vector3.Cross(r1, Jt), inverseWorldInertiaMatrix1);
			m_body2.omega	= oldOmegat2 + Vector3.Transform(Vector3.Cross(r2, Jt), inverseWorldInertiaMatrix2);
			
			Debug_c.Valid(m_body1.v);
			Debug_c.Valid(m_body2.v);
			Debug_c.Valid(m_body1.omega);
			Debug_c.Valid(m_body2.omega);
		}
	}
	
	public override void Draw()
	{
	}

	public void	Update(ref Vector3 p1, ref Vector3 p2, ref Vector3 normal)
	{
		m_r1 = p1;
		m_r2 = p2;
		m_velocityConstraintDirection = normal;
	}
	
	public bool	StillValid(float tolerance)
	{
		return false;
	}



	Body_c	m_body1;
	Body_c	m_body2;

	Vector3	m_r1;
	Vector3	m_r2;

	float	m_positionError;
	Vector3	m_velocityConstraintDirection;

	Vector3	m_invMoment1;
	Vector3	m_invMoment2;

	float	m_effectiveMass;

	float	m_beta;

	float	m_cachedMomentum;

	Vector3	m_cachedTangentMomentum;
};
