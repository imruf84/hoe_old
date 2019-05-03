#version 120

varying vec3 N;
varying vec3 v;

vec3 lightPos = vec3(10, 10, 40);
vec4 ambientColor = vec4(vec3(1,0,0)*.1, 1);
vec4 diffuseColor = vec4(vec3(1,0,0)*.6, 1);
vec4 specColor = vec4(vec3(1)*1, 1);
float shininess = 10;

void main (void)
{
  vec3 L = normalize(lightPos - v);
  vec3 E = normalize(-v);
  vec3 R = normalize(-reflect(L,N));

  vec4 spec = vec4(0);
  float intensity = max(dot(N,L), 0.0);
  if (intensity > 0.0) {
    vec3 H = normalize(L + E);
    float intSpec = max(dot(H,N), 0.0);
    spec = specColor * pow(intSpec, shininess);
  }

  gl_FragColor = max(intensity * diffuseColor + spec, ambientColor);
}