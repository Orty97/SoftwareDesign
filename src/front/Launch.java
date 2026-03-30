package front;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.FloatBuffer;

import org.joml.Vector2f;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import front.rendering.Window;
import front.rendering.fonts.Font;
import front.rendering.fonts.Glyph;

public class Launch {
	private static Window runningWindow;
	
	public static int MONITOR_WIDTH;
	public static int MONITOR_HEIGHT;
	
	public static void main(String args[]) {
				
		if(!glfwInit()) {
			System.err.println("Failed to initialize GLFW, app will abort!");
			System.exit(1);
		}
		
		long primaryMonitorPointer = GLFW.glfwGetPrimaryMonitor();
		GLFWVidMode primaryMonitor = GLFWVidMode.create(primaryMonitorPointer);
		
		MONITOR_WIDTH = primaryMonitor.width();
		MONITOR_HEIGHT = primaryMonitor.height();
		
		runningWindow = new LandingPage(500,500, "GrowFlow-Unauthenticated");
		
		GLFW.glfwMakeContextCurrent(runningWindow.getWindowId());
		GL.createCapabilities();
		
		
		Font testFont = new Font();
		
		Glyph testGlyph = testFont.getGlyph(0x41);
		
		int contourCount = testGlyph.getContoursCount();
		int pointsCount = testGlyph.getPointsCount();
		
		float scale = testGlyph.getGlyphScale() * 0.75f;
		Vector2f offset = testGlyph.getGlyphOffset();
		offset.x += 0.25f;
		offset.y += 0.25f;
			
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer((pointsCount+contourCount) * 4);
		
		for(int contour = 0; contour < contourCount; contour++) {
			int contourStart = (contour == 0) ? 0 : testGlyph.getContourEnd(contour - 1) + 1;
			int contourEnd = testGlyph.getContourEnd(contour);
			
			for(int point = contourStart; point <= contourEnd; point++) {
				int next_point = (point == contourEnd) ? contourStart : point + 1;
				
				vertexBuffer.put(testGlyph.getPointX(point)*scale + offset.x).put(testGlyph.getPointY(point)*scale + offset.y);
				vertexBuffer.put(testGlyph.getPointX(next_point)*scale + offset.x).put(testGlyph.getPointY(next_point)*scale + offset.y);
			}
		}
		
		vertexBuffer.flip();
		
		int vao = GL30.glGenVertexArrays();
		int vbo = GL15.glGenBuffers();
		
		GL30.glBindVertexArray(vao);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER,vertexBuffer,GL15.GL_STATIC_DRAW);
		
		glVertexAttribPointer(0,2,GL11.GL_FLOAT,false, 2 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		
		int shaderProgram = GL20.glCreateProgram();
		
		int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		GL20.glShaderSource(vertexShader,"#version 330 core\nlayout(location = 0) in vec2 aPos;\nvoid main() {\ngl_Position = vec4(aPos, 0.0, 1.0);\n}\n");
		GL20.glCompileShader(vertexShader);
		
		GL20.glShaderSource(fragmentShader,"#version 330 core\nout vec4 FragColor;\nvoid main() {\nFragColor = vec4(0.0, 0.0, 0.0, 0.0);\n}");
		GL20.glCompileShader(fragmentShader);
				
		GL20.glAttachShader(shaderProgram,vertexShader);
		GL20.glAttachShader(shaderProgram,fragmentShader);
		
		
		GL20.glLinkProgram(shaderProgram);
		GL20.glValidateProgram(shaderProgram);
				
		while(runningWindow != null) {
			GLFW.glfwPollEvents();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			GL20.glUseProgram(shaderProgram);
			glBindVertexArray(vao);
			glDrawArrays(GL30.GL_LINES, 0,(pointsCount + contourCount)*2);
			
			GLFW.glfwSwapBuffers(runningWindow.getWindowId());			
		}
	}	
}
