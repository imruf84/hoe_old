#version 120

// https://github.com/mattdesl/lwjgl-basics/wiki/GLSL-Versions

varying vec3 position;
varying vec3 normal;
varying vec3 tangent;
varying vec3 bitangent;
varying mat3 tangentBasis;
varying vec2 texcoord;

uniform sampler2D albedoTexture;
uniform sampler2D normalTexture;
uniform sampler2D metalnessTexture;
uniform sampler2D roughnessTexture;
uniform samplerCube specularTexture;
uniform samplerCube irradianceTexture;
uniform sampler2D specularBRDF_LUT;


void main()
{
  // Sample input textures to get shading model params.
  vec3 albedo = texture2D(albedoTexture, texcoord).rgb;
  float metalness = texture2D(metalnessTexture, texcoord).r;
  float roughness = texture2D(roughnessTexture, texcoord).r;

  gl_FragColor = vec4(albedo,1);
  gl_FragColor = vec4(roughness,roughness,roughness,1);
}
