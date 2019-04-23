#version 120

varying vec3 position;
varying vec3 normal;
varying vec3 tangent;
varying vec3 bitangent;
varying mat3 tangentBasis;
varying vec2 texcoord;


void main()
{
  texcoord = vec2(gl_MultiTexCoord0.x,gl_MultiTexCoord0.y);
  position = vec3(gl_ModelViewMatrix * gl_Vertex);

  normal = normalize(gl_NormalMatrix * gl_Normal);
  /*if( normal.x != 0. )
  {
    tangent.y = tangent.z = 1.;	
    tangent.x = -( normal.y + normal.z ) / normal.x;
  }
  else if( normal.y != 0. )
  {
    tangent.x = tangent.z = 1.;	
    tangent.y = -( normal.x + normal.z ) / normal.y;
  }
  else
  {
    tangent.x = tangent.y = 1.;	
    tangent.z = -( normal.x + normal.y ) / normal.z;
  }*/

  vec3 c1 = cross(normal, vec3(0.0, 0.0, 1.0));
  vec3 c2 = cross(normal, vec3(0.0, 1.0, 0.0));
  tangent = length(c1) > length(c2) ? c1 : c2;

  tangent = normalize(tangent);
  bitangent = normalize(cross(tangent, normal));

  // Pass tangent space basis vectors (for normal mapping).
  tangentBasis = gl_NormalMatrix/*???*/ * mat3(tangent, bitangent, normal);


  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
