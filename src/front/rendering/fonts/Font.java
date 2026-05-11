package front.rendering.fonts;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import front.rendering.fonts.FontParser.GlyphInstance;
import front.rendering.fonts.FontParser.GlyphInstanceRange;
import front.util.FileReadHelper;

public class Font {
	
	private static int FONT_COUNT = 0;
	
	private final int fontIndex;
	private int shaderProgramId;
	
	private int targetGlyphUniformLocation;
	private int glyphPositionUniformLocation;
	private int textTransformUniformLocation;
	
	private FloatBuffer transformBuffer = BufferUtils.createFloatBuffer(16);
	
	public HashMap<Integer,Integer> unicodeToGlyphIdMap = new HashMap<Integer,Integer>();
	public HashMap<Integer,Integer> unicodeToGlyphMap = new HashMap<Integer,Integer>();
	public HashMap<Long,GlyphKernPair> glyphKernData;
	
	public int[] glyphAdvanceWidth;
	
	public final int pointsSSBO;
	public final int contoursSSBO;
	public final int geometriesSSBO;
	public final int instancesSSBO;
	public final int glyphsSSBO;
	public final int transformsSSBO;
	
	public Font(Matrix3f font_ndc_proj,HashMap<Integer,GlyphInstanceRange> ranges, ArrayList<GlyphInstance> instances, HashMap<Long,GlyphKernPair> glyph_kern_data, int[] advance_width, HashMap<Integer,Integer> glyph_id_map) {
		fontIndex = FONT_COUNT ++;
		unicodeToGlyphIdMap = glyph_id_map;
		glyphKernData = glyph_kern_data;
		glyphAdvanceWidth = advance_width;
		//Arrays that will be used in creating the SSBO's
		
		// All points in the font -> aranged in contours		
		ArrayList<Integer> fontPoints = new ArrayList<Integer>();
		// All contours in the font(point start index/ points count)
		ArrayList<Integer> fontContours = new ArrayList<Integer>();
		// All geometries in the font(contour start index / contours count / points per geometry)
		ArrayList<Integer> fontGeometries = new ArrayList<Integer>();
		// All transforms in the font(3x3 encapsulating a 3x2 matrix)
		ArrayList<Matrix3f> fontTransforms = new ArrayList<Matrix3f>();
		// All glyph instances in the font (pairs of geometry-tranform)
		ArrayList<Integer> fontInstances = new ArrayList<Integer>();
		// All glyphs (instance start / instances count)
		ArrayList<Integer> fontGlyphs = new ArrayList<Integer>();
		
		ArrayList<GlyphGeometry> geometries = new ArrayList<>();
		
		for(int unicode : ranges.keySet()) {
			GlyphInstanceRange unicodeGlyphInstanceRange = ranges.get(unicode);
			for(int i = 0; i < unicodeGlyphInstanceRange.length;i++) {
				GlyphInstance instance = instances.get(unicodeGlyphInstanceRange.startIndex + i);
				if(!geometries.contains(instance.geometry))
					geometries.add(instance.geometry);
				
				if(!fontTransforms.contains(instance.transform))
					fontTransforms.add(instance.transform.mulLocal(font_ndc_proj));
			}
		}
		
		int cumulatedPointCount = 0;
		int cumulatedContoursCount = 0;
				
		for(GlyphGeometry geometry : geometries) {
			boolean[] onCurveFlag = geometry.onCurve;
			int[] geometryPoints = geometry.pointData;
			
			int[] contourData = geometry.contourData;
			int contoursCount = contourData.length/2;
						
			int pointsCumulatedSoFar = fontPoints.size();
			
			ArrayList<Integer> explicitPoints;
			
			for(int c = 0; c < contoursCount; c++) {
				
				explicitPoints  = new ArrayList<Integer>();
				
				int contourStart = contourData[c * 2 + 0];
				int contourCount = contourData[c * 2 + 1];
				int contourEndPt = contourStart+contourCount-1;
				
				fontContours.add(cumulatedPointCount);
				
				//Contour start
				if(!onCurveFlag[contourStart]) {
					if(!onCurveFlag[contourEndPt]) {
						explicitPoints.add((geometryPoints[contourStart*2+0]+geometryPoints[contourEndPt*2+0])/2);
						explicitPoints.add((geometryPoints[contourStart*2+1]+geometryPoints[contourEndPt*2+1])/2);
					}else {
						explicitPoints.add(geometryPoints[contourEndPt*2+0]);
						explicitPoints.add(geometryPoints[contourEndPt*2+1]);
					}
				}
				
				//Contour body
				for(int p = 0; p < contourCount - 1; p++) {
					int pointX = geometryPoints[(contourStart+p)*2+0];
					int pointY = geometryPoints[(contourStart+p)*2+1];
					
					explicitPoints.add(pointX);
					explicitPoints.add(pointY);
					
					if(onCurveFlag[contourStart+p] == onCurveFlag[contourStart+p+1]) {
						int nextPointX = geometryPoints[(contourStart+p+1)*2+0];
						int nextPointY = geometryPoints[(contourStart+p+1)*2+1];
						
						explicitPoints.add((pointX + nextPointX)/2);
						explicitPoints.add((pointY + nextPointY)/2);
					}
				}				
				
				//Contour end
				explicitPoints.add(geometryPoints[contourEndPt*2+0]);
				explicitPoints.add(geometryPoints[contourEndPt*2+1]);
				
				if(onCurveFlag[contourEndPt]) {
					if(onCurveFlag[contourStart]) {
						explicitPoints.add((geometryPoints[contourEndPt*2+0]+geometryPoints[contourStart*2+0])/2);
						explicitPoints.add((geometryPoints[contourEndPt*2+1]+geometryPoints[contourStart*2+1])/2);
						
						explicitPoints.add(geometryPoints[contourStart*2+0]);
						explicitPoints.add(geometryPoints[contourStart*2+1]);
					}
				}else {
					if(!onCurveFlag[contourStart]) {
						explicitPoints.add((geometryPoints[contourStart*2+0]+geometryPoints[contourEndPt*2+0])/2);
						explicitPoints.add((geometryPoints[contourStart*2+1]+geometryPoints[contourEndPt*2+1])/2);
					}else {
						explicitPoints.add(geometryPoints[contourStart*2+0]);
						explicitPoints.add(geometryPoints[contourStart*2+1]);
					}
				}
				
				fontPoints.addAll(explicitPoints);
				fontContours.add(explicitPoints.size()/2);
				cumulatedPointCount = fontPoints.size()/2;
			}
			fontGeometries.add(cumulatedContoursCount);
			cumulatedContoursCount += contourData.length/2;
			fontGeometries.add(contourData.length/2);
			fontGeometries.add((fontPoints.size()-pointsCumulatedSoFar)/2);
		}
		
		int processedUnicodeRangeCount = 0;
		int glyphId = 0;
		
		for(int unicode : ranges.keySet()) {
			GlyphInstanceRange unicodeRange = ranges.get(unicode);
			
			fontGlyphs.add(processedUnicodeRangeCount);	
			for(int i = 0; i < unicodeRange.length;i++) {
				GlyphInstance unicodeInstance = instances.get(unicodeRange.startIndex+i);
				fontInstances.add(geometries.indexOf(unicodeInstance.geometry));
				fontInstances.add(fontTransforms.indexOf(unicodeInstance.transform));
				processedUnicodeRangeCount++;
			}					
			fontGlyphs.add(unicodeRange.length);
			unicodeToGlyphMap.put(unicode,glyphId++);
		}
		
		ByteBuffer pointsBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontPoints.size());
		for(int coordinate : fontPoints)
			pointsBuffer.putInt(coordinate);
		pointsBuffer.flip();
		
		pointsSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, pointsSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, pointsBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER,0, pointsSSBO);
		
		ByteBuffer contoursBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontContours.size());
		for(int meta_data : fontContours)
			contoursBuffer.putInt(meta_data);
		contoursBuffer.flip();
		contoursSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, contoursSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, contoursBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, contoursSSBO);
				
		ByteBuffer geometryBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontGeometries.size());
		for(int meta_data : fontGeometries)
			geometryBuffer.putInt(meta_data);
		geometryBuffer.flip();
		geometriesSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, geometriesSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, geometryBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, geometriesSSBO);
				
		ByteBuffer instancesBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontInstances.size());
		for(int data : fontInstances)
			instancesBuffer.putInt(data);
		instancesBuffer.flip();
		instancesSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, instancesSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, instancesBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, instancesSSBO);
				
		ByteBuffer glyphsBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontGlyphs.size());
		for(int data : fontGlyphs)
			glyphsBuffer.putInt(data);
		glyphsBuffer.flip();
		glyphsSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, glyphsSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, glyphsBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 4, glyphsSSBO);
		
		ByteBuffer transformsBuffer = BufferUtils.createByteBuffer(Float.BYTES * 16 * fontTransforms.size());
		ByteBuffer matrixBuffer = BufferUtils.createByteBuffer(Float.BYTES * 16);
		for(int i = 0; i < fontTransforms.size(); i++) {
			Matrix4f test = new Matrix4f().set(fontTransforms.get(i));
			test.get(matrixBuffer);
			transformsBuffer.put(matrixBuffer);
		}
		
		transformsBuffer.flip();
		transformsSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, transformsSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, transformsBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 5, transformsSSBO);	
		
		loadShaderProgram();
	}

	private void loadShaderProgram() {
		shaderProgramId = GL20.glCreateProgram();
		int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		GL20.glShaderSource(vertexShader, FileReadHelper.loadShaderCode("res\\Text.vert"));
		GL20.glCompileShader(vertexShader);

		GL20.glShaderSource(fragmentShader, FileReadHelper.loadShaderCode("res\\Text.frag"));
		GL20.glCompileShader(fragmentShader);
		
		GL20.glAttachShader(shaderProgramId, vertexShader);
		GL20.glAttachShader(shaderProgramId, fragmentShader);

		GL20.glLinkProgram(shaderProgramId);
		GL20.glValidateProgram(shaderProgramId);
				
		targetGlyphUniformLocation = GL30.glGetUniformLocation(shaderProgramId,"u_target_glyph");
		glyphPositionUniformLocation = GL30.glGetUniformLocation(shaderProgramId,"glyph_position");
		textTransformUniformLocation = GL30.glGetUniformLocation(shaderProgramId,"text_transform");
	}

	public void useFontProgram() {
		GL30.glUseProgram(shaderProgramId);
	}
	
	public void stopUsingFontProgram() {
		GL30.glUseProgram(0);
	}
	
	public void setRenderTargetGlyph(int code_point) {
		GL30.glUniform1ui(targetGlyphUniformLocation,unicodeToGlyphMap.get(code_point));
	}
	
	public void setCursorRenderPosition(float cursor_position) {
		GL30.glUniform3f(glyphPositionUniformLocation,cursor_position,0f,0f);
	}
	
	public void setTextTransform(Matrix4f text_transform) {
		transformBuffer.clear();
		text_transform.get(transformBuffer);
		GL30.glUniformMatrix4fv(textTransformUniformLocation,false,transformBuffer);
	}
	
 	public GlyphKernPair getGlyphPairKern(int glyph_1_id, int glyph_2_id) {
		return glyphKernData.get(((long)glyph_1_id << 32) | (glyph_2_id & 0xFFFFFFFFL));
	}

 	public int getFontIndex() {
 		return fontIndex;
 	}
}
