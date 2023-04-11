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

    int color = int(u_diffuseColor.r + u_diffuseColor.g);
    int palette_index =
        int(0 > color) + int(72 > color) + int(135 > color) + int(163 > color) +
        int(182 > color) + int(214 > color) + int(228 > color) + int(249 > color) +
        int(253 > color) + int(255 > color) + int(374 > color) + int(389 > color) +
        int(418 > color) + int(459 > color) + int(491 > color) + int(496 > color);

    gl_FragColor.rgb = diffuse.rgb * (v_lightDiffuse);
    gl_FragColor.a = 1.0;
}