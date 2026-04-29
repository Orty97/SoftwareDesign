package front.rendering.fonts;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import org.lwjgl.BufferUtils;

import front.rendering.fonts.FontParser.GlyphInstance;
import front.rendering.fonts.FontParser.GlyphInstanceRange;

public class Font {
	
	public HashMap<Integer,Integer> unicodeToGlyphMap = new HashMap<Integer,Integer>();
	
	public Font(Matrix3f font_ndc_proj,HashMap<Integer,GlyphInstanceRange> ranges, ArrayList<GlyphInstance> instances) {
		
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
		
		for(int i = 0; i < 10; i ++) {
			System.out.println(fontTransforms.get(i));
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
		
		for(int unicode : ranges.keySet()) {
			GlyphInstanceRange unicodeRange = ranges.get(unicode);
			
			for(int i = 0; i < unicodeRange.length;i++) {
				GlyphInstance unicodeInstance = instances.get(unicodeRange.startIndex+i);
				fontInstances.add(geometries.indexOf(unicodeInstance.geometry));
				fontInstances.add(fontTransforms.indexOf(unicodeInstance.transform));
			}
			
			fontGlyphs.add(processedUnicodeRangeCount,unicodeRange.length);			
			unicodeToGlyphMap.put(unicode,processedUnicodeRangeCount++);
		}
		
		ByteBuffer pointsBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontPoints.size());
		for(int coordinate : fontPoints)
			pointsBuffer.putInt(coordinate);
		pointsBuffer.flip();
		
		int pointSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, pointSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, pointsBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, pointSSBO);
		
		ByteBuffer contoursBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontContours.size());
		for(int meta_data : fontContours)
			contoursBuffer.putInt(meta_data);
		contoursBuffer.flip();
		int contourSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, contourSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, contoursBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, contourSSBO);
				
		ByteBuffer geometryBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontGeometries.size());
		for(int meta_data : fontGeometries)
			geometryBuffer.putInt(meta_data);
		geometryBuffer.flip();
		int geometrySSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, geometrySSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, geometryBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, geometrySSBO);
				
		ByteBuffer instancesBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontInstances.size());
		for(int data : fontInstances)
			instancesBuffer.putInt(data);
		instancesBuffer.flip();
		int instanceSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, instanceSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, instancesBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, instanceSSBO);
		
		ByteBuffer glyphsBuffer = BufferUtils.createByteBuffer(Integer.BYTES * fontGlyphs.size());
		for(int data : fontGlyphs)
			glyphsBuffer.putInt(data);
		glyphsBuffer.flip();
		int glyphSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, glyphSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, glyphsBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 4, glyphSSBO);
		
		ByteBuffer transformsBuffer = BufferUtils.createByteBuffer(Float.BYTES * 16 * fontTransforms.size());
		ByteBuffer matrixBuffer = BufferUtils.createByteBuffer(Float.BYTES * 16);
		
		for(int i = 0; i < fontTransforms.size(); i++) {
			Matrix4f test = new Matrix4f().set(fontTransforms.get(i));
			test.get(matrixBuffer);
			transformsBuffer.put(matrixBuffer);
		}
		
		transformsBuffer.flip();
		int transformSSBO = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, transformSSBO);
		glBufferData(GL_SHADER_STORAGE_BUFFER, transformsBuffer, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 5, transformSSBO);	
	}
}
