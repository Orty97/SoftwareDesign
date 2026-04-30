#version 430

struct Instance_r{
    int instance_start_index;
    int instance_count;
};

struct Instance_s{
    int geometry_index;
    int transform_index;
};

struct Geometry_s{
    int contour_start_index;
    int contour_count;
    int point_count;
};

struct Contour_s{
    int point_start_index;
    int point_count;
};

struct Segment_s{
    ivec2 on_1;
    ivec2 off;
    ivec2 on_2;
};

layout(std430, binding = 5) buffer Transforms {
    mat4 transform_array[];
}transforms;

layout(std430, binding = 4) buffer Glyphs{
    ivec2 glyph_instance_indices[];
}glyphs;

layout(std430, binding = 3) buffer Instances{
    Instance_s instance_structs[];
}instances;

layout(std430, binding = 2) buffer Geometries{
    Geometry_s geometry_structs[];
}geometries;

layout(std430, binding = 1) buffer Contours{
    Contour_s contour_structs[];
}contours;

layout(std430, binding = 0) buffer Points {
    ivec2 points_array[];
} pts;


uniform uint u_target_glyph;

void main() {
    ivec2 glyph_instance_meta_data = glyphs.glyph_instance_indices[u_target_glyph];
    Instance_s targetInstance = instances.instance_structs[glyph_instance_meta_data.x];

    Geometry_s target_geometry = geometries.geometry_structs[targetInstance.geometry_index];
    mat4 transform = transforms.transform_array[0];

    uint punctAccumulator = 0;
    int targetContour = 0;

    if(gl_VertexID > target_geometry.point_count){
        Contour_s last_contour = contours.contour_structs[target_geometry.contour_start_index + target_geometry.contour_count - 1];
        uint point_offset = last_contour.point_start_index + last_contour.point_count -1;
        gl_Position = transform * vec4(vec2(pts.points_array[point_offset]),0.0,1.0);
    }else{
        Contour_s target_contour;
        int pointsAccumulated = 0;

        for(int i = 0; i < target_geometry.contour_count;i++){
            Contour_s current_contour = contours.contour_structs[target_geometry.contour_start_index + i];
            pointsAccumulated += current_contour.point_count;
            if(gl_VertexID < pointsAccumulated){
                target_contour = current_contour;
                pointsAccumulated -= current_contour.point_count;
                break;
            }
        }
        ivec2 targetPoint = pts.points_array[target_contour.point_start_index + (gl_VertexID - pointsAccumulated)];
        vec2 finaltargetPoint = (transform * vec4(vec2(targetPoint),0.0,1.0)).xy;
        gl_Position = vec4(finaltargetPoint.xy, 0.0, 1.0);
    }
}