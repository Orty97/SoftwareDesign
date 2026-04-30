package front;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import front.rendering.Window;
import front.rendering.fonts.Font;
import front.rendering.fonts.FontParser;
import front.util.FileReadHelper;

public class Launch {
	private static Window runningWindow;

	public static int MONITOR_WIDTH;
	public static int MONITOR_HEIGHT;

	// TODO: TEMPORARY -> TO BE MOVED TO RENDERER WHEN CLEANING UP
	private static int glyphFlatteningProgram;
	private static int glyphFlatteningComputeShader;

	private static int glyphEdgeSSBO;
	private static int tesselationLevel = 10;
	
	public static int DONUT_GLYPH_ID = 0;
	
	public static void main(String args[]) throws IOException {

		if (!glfwInit()) {
			System.err.println("Failed to initialize GLFW, app will abort!");
			System.exit(1);
		}

		long primaryMonitorPointer = GLFW.glfwGetPrimaryMonitor();
		GLFWVidMode primaryMonitor = GLFWVidMode.create(primaryMonitorPointer);

		MONITOR_WIDTH = primaryMonitor.width();
		MONITOR_HEIGHT = primaryMonitor.height();

		runningWindow = new LandingPage(500, 500, "GrowFlow-Unauthenticated");
		
		GLFW.glfwMakeContextCurrent(runningWindow.getWindowId());
		GL.createCapabilities();
		
		Font font = FontParser.parseFontFile("C:\\Windows\\Fonts\\segoeui.ttf");
				
		GLFW.glfwSetKeyCallback(runningWindow.getWindowId(),(window,key,scan,action,mods)->{
						
			if(key == GLFW.GLFW_KEY_RIGHT && action == GLFW.GLFW_PRESS) {
				DONUT_GLYPH_ID++;
				if(DONUT_GLYPH_ID > 2409)
					DONUT_GLYPH_ID = 2409;
			}
			
			if(key == GLFW.GLFW_KEY_LEFT && action == GLFW.GLFW_PRESS) {
				DONUT_GLYPH_ID--;
				if(DONUT_GLYPH_ID < 0)
					DONUT_GLYPH_ID = 0;
			}
			
			if(key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z && action == GLFW.GLFW_PRESS) {
				if(mods == GLFW.GLFW_MOD_SHIFT)
					DONUT_GLYPH_ID = font.unicodeToGlyphMap.get(key - GLFW.GLFW_KEY_A + 'A');
				else
					DONUT_GLYPH_ID = font.unicodeToGlyphMap.get(key - GLFW.GLFW_KEY_A + 'a');
			}
			
			if(key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9 && action == GLFW.GLFW_PRESS) {
				DONUT_GLYPH_ID = font.unicodeToGlyphMap.get(key - GLFW.GLFW_KEY_0 + '0');
			}
		});

		int shaderProgram = GL20.glCreateProgram();
		
		int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		GL20.glShaderSource(vertexShader, FileReadHelper.loadShaderCode("res\\Text.vert"));
		GL20.glCompileShader(vertexShader);

		GL20.glShaderSource(fragmentShader, FileReadHelper.loadShaderCode("res\\Text.frag"));
		GL20.glCompileShader(fragmentShader);
		
		GL20.glAttachShader(shaderProgram, vertexShader);
		GL20.glAttachShader(shaderProgram, fragmentShader);

		GL20.glLinkProgram(shaderProgram);
		GL20.glValidateProgram(shaderProgram);
		
		int geometryUniformLocation = GL30.glGetUniformLocation(shaderProgram,"u_target_glyph");
		
		long currentTime = System.currentTimeMillis();
		long measuredTime;
		long deltaTime = 0;;
		
		while (runningWindow != null) {
			measuredTime = System.currentTimeMillis();
			deltaTime += measuredTime - currentTime;
			currentTime = measuredTime;
			
//			if(deltaTime >= 50) {
//				DONUT_GLYPH_ID +=1;
//				deltaTime -= 50;
//				if(DONUT_GLYPH_ID >= 5499)
//					DONUT_GLYPH_ID = 0;
//			}
			
			
			GLFW.glfwPollEvents();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			GL20.glUseProgram(shaderProgram);
			
			glBindVertexArray(0);
			
			
			
			GL30.glUniform1ui(geometryUniformLocation,DONUT_GLYPH_ID);
			glDrawArrays(GL_LINE_STRIP, 0, 3000);
			
			GLFW.glfwSwapBuffers(runningWindow.getWindowId());
		}
	}
}
