#version 430 core

struct GlyphPoint {
    vec2 pos;
    uint flags;
    uint pad0;
};

struct Glyph {
    uint pointStart;
    uint pointCount;
    uint contourStart;
    uint contourCount;
};

layout(std430, binding = 2) buffer Glyphs { Glyph glyphs[]; };
layout(std430, binding = 0) buffer Points { GlyphPoint points[]; };

uniform int startIndex; 
uniform vec2 offset;
uniform float scale;

void main() {
    Glyph g = glyphs[startIndex];
    GlyphPoint p = points[g.pointStart + gl_VertexID];
    gl_Position = vec4(p.pos * scale + offset, 0.0, 1.0);
}