// https://github.com/mattdesl/lwjgl-basics/wiki/GLSL-Versions

varying vec3 V;
varying vec3 N;

uniform vec3 lightPosition;
uniform vec4 lightAmbientColor;
uniform vec4 lightDiffuseColor;
uniform vec4 lightSpecularColor;
uniform vec4 materialAmbientColor;
uniform vec4 materialDiffuseColor;
uniform vec4 materialSpecularColor;
uniform float materialShininess;

void main()
{
  vec3 L = normalize(lightPosition - V);
  vec3 E = normalize(-V);
  vec3 R = normalize(-reflect(L,N));

  float intSpec = 0;
  float intDiff = max(dot(N,L), 0.0);
  if (intDiff > 0.0) {
    vec3 H = normalize(L + E);
    intSpec = pow(max(dot(H,N), 0.0), materialShininess);
  }

  vec3 mdc = vec3(materialDiffuseColor)*materialDiffuseColor.w;
  vec3 msc = vec3(materialSpecularColor)*materialSpecularColor.w;
  vec3 mac = vec3(materialAmbientColor)*materialAmbientColor.w;

  vec3 ldc = vec3(lightDiffuseColor)*lightDiffuseColor.w;
  vec3 lsc = vec3(lightSpecularColor)*lightSpecularColor.w;
  vec3 lac = vec3(lightAmbientColor)*lightAmbientColor.w;

  gl_FragColor = vec4(intDiff * ldc*mdc+intSpec*lsc*msc+mac*lac,1);

}
