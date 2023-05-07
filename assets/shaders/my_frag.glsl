#version 330 core

#define numDirectionalLights 2

#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

uniform vec4 u_diffuseColor;
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;

in vec3 v_shadowMapUv;
in vec3 v_ambientLight;
in vec3 v_lightDiffuse;

// This should be const, but I have to find how to setup the version without libgdx prepending something. Because version needs to be the first line.
// Investigate if it ever becomes a real project
const vec3[6] WHITE         = vec3[6](vec3(255, 241, 232), vec3(255, 241, 232), vec3(194, 195, 199), vec3(131, 118, 156), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] YELLOW        = vec3[6](vec3(255, 255, 39), vec3(255, 236, 39), vec3(171, 82, 54), vec3(126, 37, 83), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] PINK_SKIN     = vec3[6](vec3(255, 212, 137), vec3(255, 204, 170), vec3(171, 82, 54), vec3(126, 37, 83), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] ORANGE        = vec3[6](vec3(255, 181, 10), vec3(255, 163, 0), vec3(146, 57, 70), vec3(126, 37, 83), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] GREY          = vec3[6](vec3(209, 205, 169), vec3(194, 195, 199), vec3(131, 118, 156), vec3(95, 87, 79), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] PINK          = vec3[6](vec3(255, 148, 136), vec3(255, 119, 168), vec3(171, 82, 54), vec3(126, 37, 83), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] RED           = vec3[6](vec3(255, 59, 68), vec3(255, 0, 77), vec3(126, 37, 83), vec3(126, 37, 83), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] LIGHT_BROWN   = vec3[6](vec3(192, 121, 50), vec3(171, 82, 54), vec3(126, 37, 83), vec3(82, 40, 83), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] LIGHT_PURPLE  = vec3[6](vec3(162, 148, 127), vec3(131, 118, 156), vec3(95, 87, 79), vec3(59, 63, 81), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] LIGHT_GREEN   = vec3[6](vec3(64,  230, 50), vec3(0, 228, 54), vec3(0, 135, 81), vec3(0, 135, 81), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] LIGHT_BLUE    = vec3[6](vec3(41,  220, 255), vec3(41, 173, 255), vec3(131, 118, 156), vec3(126, 37, 83), vec3(29, 43, 83), vec3(0, 0, 0));
const vec3[6] DARK_BROWN    = vec3[6](vec3(135, 124, 69), vec3(95, 87, 79), vec3(62, 65, 81), vec3(29, 43, 83), vec3(6, 9, 18), vec3(0, 0, 0));
const vec3[6] PURPLE        = vec3[6](vec3(158, 87, 72), vec3(126, 37, 83), vec3(63, 41, 83), vec3(20, 30, 58), vec3(6, 9, 18), vec3(0, 0, 0));
const vec3[6] DARK_GREEN    = vec3[6](vec3(64,  180, 71), vec3(0, 135, 81), vec3(20, 71, 82), vec3(29, 43, 83), vec3(6, 9, 18), vec3(0, 0, 0));
const vec3[6] DARK_BLUE     = vec3[6](vec3(39,  58, 111), vec3(29, 43, 83), vec3(20, 30, 58), vec3(9, 13, 24), vec3(3, 5, 9), vec3(0, 0, 0));
const vec3[6] BLACK         = vec3[6](vec3(0,   0, 0), vec3(0, 0, 0), vec3(0, 0, 0), vec3(0, 0, 0), vec3(0, 0, 0), vec3(0, 0, 0));
const vec3[16 * 6] PALETTE = vec3[16 * 6](
    //WHITE, YELLOW, PINK_SKIN, ORANGE, GREY, PINK, RED, LIGHT_BROWN, LIGHT_PURPLE, LIGHT_GREEN, LIGHT_BLUE, DARK_BROWN, PURPLE, DARK_GREEN, DARK_BLUE, BLACK
    vec3(255.0 / 255.0, 241.0 / 255.0, 232.0 / 255.0), vec3(255.0 / 255.0, 241.0 / 255.0, 232.0 / 255.0), vec3(194.0 / 255.0, 195.0 / 255.0, 199.0 / 255.0), vec3(131.0 / 255.0, 118.0 / 255.0, 156.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(255.0 / 255.0, 255.0 / 255.0, 39.0 / 255.0), vec3(255.0 / 255.0, 236.0 / 255.0, 39.0 / 255.0), vec3(171.0 / 255.0, 82.0 / 255.0, 54.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(255.0 / 255.0, 212.0 / 255.0, 137.0 / 255.0), vec3(255.0 / 255.0, 204.0 / 255.0, 170.0 / 255.0), vec3(171.0 / 255.0, 82.0 / 255.0, 54.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(255.0 / 255.0, 181.0 / 255.0, 10.0 / 255.0), vec3(255.0 / 255.0, 163.0 / 255.0, 0.0 / 255.0), vec3(146.0 / 255.0, 57.0 / 255.0, 70.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(209.0 / 255.0, 205.0 / 255.0, 169.0 / 255.0), vec3(194.0 / 255.0, 195.0 / 255.0, 199.0 / 255.0), vec3(131.0 / 255.0, 118.0 / 255.0, 156.0 / 255.0), vec3(95.0 / 255.0, 87.0 / 255.0, 79.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(255.0 / 255.0, 148.0 / 255.0, 136.0 / 255.0), vec3(255.0 / 255.0, 119.0 / 255.0, 168.0 / 255.0), vec3(171.0 / 255.0, 82.0 / 255.0, 54.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(255.0 / 255.0, 59.0 / 255.0, 68.0 / 255.0), vec3(255.0 / 255.0, 0.0 / 255.0, 77.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(192.0 / 255.0, 121.0 / 255.0, 50.0 / 255.0), vec3(171.0 / 255.0, 82.0 / 255.0, 54.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(82.0 / 255.0, 40.0 / 255.0, 83.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(162.0 / 255.0, 148.0 / 255.0, 127.0 / 255.0), vec3(131.0 / 255.0, 118.0 / 255.0, 156.0 / 255.0), vec3(95.0 / 255.0, 87.0 / 255.0, 79.0 / 255.0), vec3(59.0 / 255.0, 63.0 / 255.0, 81.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(64.0 / 255.0,  230.0 / 255.0, 50.0 / 255.0), vec3(0.0 / 255.0, 228.0 / 255.0, 54.0 / 255.0), vec3(0.0 / 255.0, 135.0 / 255.0, 81.0 / 255.0), vec3(0.0 / 255.0, 135.0 / 255.0, 81.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(41.0 / 255.0,  220.0 / 255.0, 255.0 / 255.0), vec3(41.0 / 255.0, 173.0 / 255.0, 255.0 / 255.0), vec3(131.0 / 255.0, 118.0 / 255.0, 156.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(0, 0, 0),
    vec3(135.0 / 255.0, 124.0 / 255.0, 69.0 / 255.0), vec3(95.0 / 255.0, 87.0 / 255.0, 79.0 / 255.0), vec3(62.0 / 255.0, 65.0 / 255.0, 81.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(6.0 / 255.0, 9.0 / 255.0, 18.0 / 255.0), vec3(0, 0, 0),
    vec3(158.0 / 255.0, 87.0 / 255.0, 72.0 / 255.0), vec3(126.0 / 255.0, 37.0 / 255.0, 83.0 / 255.0), vec3(63.0 / 255.0, 41.0 / 255.0, 83.0 / 255.0), vec3(20.0 / 255.0, 30.0 / 255.0, 58.0 / 255.0), vec3(6.0 / 255.0, 9.0 / 255.0, 18.0 / 255.0), vec3(0, 0, 0),
    vec3(64.0 / 255.0,  180.0 / 255.0, 71.0 / 255.0), vec3(0.0 / 255.0, 135.0 / 255.0, 81.0 / 255.0), vec3(20.0 / 255.0, 71.0 / 255.0, 82.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(6.0 / 255.0, 9.0 / 255.0, 18.0 / 255.0), vec3(0, 0, 0),
    vec3(39.0 / 255.0,  58.0 / 255.0, 111.0 / 255.0), vec3(29.0 / 255.0, 43.0 / 255.0, 83.0 / 255.0), vec3(20.0 / 255.0, 30.0 / 255.0, 58.0 / 255.0), vec3(9.0 / 255.0, 13.0 / 255.0, 24.0 / 255.0), vec3(3.0 / 255.0, 5.0 / 255.0, 9.0 / 255.0), vec3(0, 0, 0),
    vec3(0.0 / 255.0,   0.0 / 255.0, 0.0 / 255.0), vec3(0.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0), vec3(0.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0), vec3(0.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0), vec3(0.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0), vec3(0, 0, 0)
);


float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    return step(v_shadowMapUv.z, dot(texture(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
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

    int color = int(u_diffuseColor.r + u_diffuseColor.g) * 255;
    int palette_index =
        int(0 > color) + int(72 > color) + int(135 > color) + int(163 > color) +
        int(182 > color) + int(214 > color) + int(228 > color) + int(249 > color) +
        int(253 > color) + int(255 > color) + int(374 > color) + int(389 > color) +
        int(418 > color) + int(459 > color) + int(491 > color) + int(496 > color);
    // no colored light for the moment, .r is enough
    int shade = int((1.0 - v_lightDiffuse.r) * 6);

    vec3 trans_color = PALETTE[shade];


    gl_FragColor = vec4(trans_color, 1.0);

//    gl_FragColor.rgb = diffuse.rgb * (v_lightDiffuse);
}
