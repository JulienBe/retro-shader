uniform vec4 u_diffuseColor;
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;

varying vec3 v_ambientLight;
varying vec3 v_lightDiffuse;

float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
}

float getShadow()
{
    return (//getShadowness(vec2(0,0)) +
    getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}

void main() {
    vec4 diffuse = u_diffuseColor;
//    gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + getShadow() * v_lightDiffuse));
//    gl_FragColor.rgb = diffuse.rgb * (v_lightDiffuse); // works
    gl_FragColor.rgb = diffuse.rgb * (v_lightDiffuse);
    gl_FragColor.a = 1.0;
}