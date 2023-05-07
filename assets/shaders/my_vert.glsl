#define numDirectionalLights 2

attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_projViewTrans;
uniform mat3 u_normalMatrix;
uniform mat4 u_worldTrans;
struct DirectionalLight
{
    vec3 color;
    vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];
uniform mat4 u_shadowMapProjViewTrans;

varying vec3 v_normal;
varying vec3 v_lightDiffuse;
varying vec4 v_color;
varying vec3 v_shadowMapUv;
varying vec3 v_ambientLight;

void main()
{
    vec3 ambientLight = vec3(0.3);
    v_ambientLight = ambientLight;
    v_lightDiffuse = ambientLight;
    vec3 normal = normalize(u_normalMatrix * a_normal);
    for (int i = 0; i < numDirectionalLights; i++) {
        vec3 lightDir = -u_dirLights[i].direction;
        float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
        vec3 value = u_dirLights[i].color * NdotL;
        v_lightDiffuse += value;
    }

    vec4 pos = u_worldTrans * vec4(a_position, 1.0);
    vec4 spos = u_shadowMapProjViewTrans * pos;
    v_shadowMapUv.xyz = (spos.xyz / spos.w) * 0.5 + 0.5;
    v_shadowMapUv.z = min(v_shadowMapUv.z, 0.998);


    gl_Position = u_projViewTrans * pos;
}