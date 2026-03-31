package front;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;

import java.io.BufferedReader;
import java.io.FileReader;

import org.joml.Vector2f;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

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
		
		Glyph testGlyph = testFont.getGlyph(0x42);
				
		float scale = testGlyph.getGlyphScale() * 0.5f;
		Vector2f offset = testGlyph.getGlyphOffset();
		
		int shaderProgram = GL20.glCreateProgram();
		
		int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		GL20.glShaderSource(vertexShader,loadShaderCode("res\\Text.vert"));
		GL20.glCompileShader(vertexShader);
		
		GL20.glShaderSource(fragmentShader,loadShaderCode("res\\Text.frag"));
		GL20.glCompileShader(fragmentShader);
				
		GL20.glAttachShader(shaderProgram,vertexShader);
		GL20.glAttachShader(shaderProgram,fragmentShader);
		
		
		GL20.glLinkProgram(shaderProgram);
		GL20.glValidateProgram(shaderProgram);
				
		int scaleUniformLocation = GL20.glGetUniformLocation(shaderProgram,"scale");
		int offsetUniformLocation = GL20.glGetUniformLocation(shaderProgram,"offset");
		int startPointUniformLocation = GL20.glGetUniformLocation(shaderProgram,"startIndex");
					

		
		while(runningWindow != null) {
			GLFW.glfwPollEvents();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			GL20.glUseProgram(shaderProgram);
			glUniform1i(startPointUniformLocation, testFont.getGlyphIndex(0x42));
			glUniform2f(offsetUniformLocation, offset.x, offset.y);
			glUniform1f(scaleUniformLocation, scale);
			
			glDrawArrays(GL_LINE_STRIP, 0, testGlyph.getPointsCount());
			
			GLFW.glfwSwapBuffers(runningWindow.getWindowId());			
		}
	}	
	
	public static String loadShaderCode(String shader_code_path) {
		StringBuilder sourceBuilder = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(shader_code_path));
			String line;
			
			while((line = reader.readLine())!=null)
				sourceBuilder.append(line).append("\n");
			
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return sourceBuilder.toString();
	}
}
