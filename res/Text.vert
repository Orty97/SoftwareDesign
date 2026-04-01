#version 430 core

layout(std430, binding = 3) buffer GlyphTesselatedPoints{
	 vec2 points[];
};

uniform vec2 offset;
uniform float scale;

void main() {
	vec2 pos = points[gl_VertexID];

	pos = pos * scale * 0.5;

	gl_Position = vec4(pos, 0.0, 1.0);
}