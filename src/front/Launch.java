package front;

import java.io.IOException;

import front.rendering.Window;
import front.rendering.fonts.FontParser;

public class Launch {
	private static Window runningWindow;

	public static int MONITOR_WIDTH;
	public static int MONITOR_HEIGHT;

	// TODO: TEMPORARY -> TO BE MOVED TO RENDERER WHEN CLEANING UP
	private static int glyphFlatteningProgram;
	private static int glyphFlatteningComputeShader;

	private static int glyphEdgeSSBO;
	private static int tesselationLevel = 10;

	private static int GLYPH_UNICODE = 0x43;
	
	public static void main(String args[]) throws IOException {

		FontParser.parseFontFile("C:\\Windows\\Fonts\\seguiemj.ttf");
		System.in.read();
		FontParser.parseFontFile("C:\\Windows\\Fonts\\segoeui.ttf");
		
//		if (!glfwInit()) {
//			System.err.println("Failed to initialize GLFW, app will abort!");
//			System.exit(1);
//		}
//
//		long primaryMonitorPointer = GLFW.glfwGetPrimaryMonitor();
//		GLFWVidMode primaryMonitor = GLFWVidMode.create(primaryMonitorPointer);
//
//		MONITOR_WIDTH = primaryMonitor.width();
//		MONITOR_HEIGHT = primaryMonitor.height();
//
//		runningWindow = new LandingPage(500, 500, "GrowFlow-Unauthenticated");
//		
//		GLFW.glfwMakeContextCurrent(runningWindow.getWindowId());
//		GL.createCapabilities();
//		
//		GLFW.glfwSetKeyCallback(runningWindow.getWindowId(),(window,key,scan,action,mods)->{
//			if(key == GLFW.GLFW_KEY_D && action == GLFW.GLFW_PRESS) {
//				GLYPH_UNICODE += 1;
//				if(GLYPH_UNICODE > 0x7E)
//					GLYPH_UNICODE = 0x7E;
//			}
//			
//			if(key == GLFW.GLFW_KEY_A && action == GLFW.GLFW_PRESS) {
//				GLYPH_UNICODE -= 1;
//				if(GLYPH_UNICODE < 0x20)
//					GLYPH_UNICODE = 0x20;
//			}
//		});
//		
//		Font testFont = new Font();
//		
//		Glyph testGlyph = testFont.getGlyph(GLYPH_UNICODE);
//		
//		float scale = testGlyph.getGlyphScale() * 0.5f;
//		Vector2f offset = testGlyph.getGlyphOffset();
//		
//		int glyphContours = testGlyph.getContoursCount();
//		int glyphTriplets = testGlyph.getPointsCount()/2;
//		
//				
//		while (runningWindow != null) {
//			
//			glyphEdgeSSBO = glGenBuffers();
//			glBindBuffer(GL_SHADER_STORAGE_BUFFER, glyphEdgeSSBO);
//			glBufferData(GL_SHADER_STORAGE_BUFFER, tesselationLevel * (2 * Float.BYTES) * glyphTriplets * 2,GL_DYNAMIC_DRAW);
//			glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, glyphEdgeSSBO);
//			
//			glyphFlatteningProgram = glCreateProgram();
//			
//			glyphFlatteningComputeShader = glCreateShader(GL_COMPUTE_SHADER);
//			glShaderSource(glyphFlatteningComputeShader, FileReadHelper.loadShaderCode("res\\GlyphFlatten.comp"));
//			glCompileShader(glyphFlatteningComputeShader);
//
//			glAttachShader(glyphFlatteningProgram, glyphFlatteningComputeShader);
//			glLinkProgram(glyphFlatteningProgram);
//			glValidateProgram(glyphFlatteningProgram);
//
//			int tesselationLevelLocation = glGetUniformLocation(glyphFlatteningProgram, "tesselationLevel");
//			int glyphIndexLocation = glGetUniformLocation(glyphFlatteningProgram, "glyphID");
//			int tripletCountLocation = glGetUniformLocation(glyphFlatteningProgram, "tripletCount");
//
//			int numGroups = (glyphTriplets / 2 + 255) / 256;
//			
//			glUseProgram(glyphFlatteningProgram);
//			glUniform1i(tesselationLevelLocation, tesselationLevel);
//			glUniform1i(glyphIndexLocation, testFont.getGlyphIndex(GLYPH_UNICODE));
//			glUniform1i(tripletCountLocation, glyphTriplets);
//			glDispatchCompute(numGroups, 1, 1);
//
//			glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
//			
//			int shaderProgram = GL20.glCreateProgram();
//
//			int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
//			int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
//
//			GL20.glShaderSource(vertexShader, FileReadHelper.loadShaderCode("res\\Text.vert"));
//			GL20.glCompileShader(vertexShader);
//
//			GL20.glShaderSource(fragmentShader, FileReadHelper.loadShaderCode("res\\Text.frag"));
//			GL20.glCompileShader(fragmentShader);
//
//			GL20.glAttachShader(shaderProgram, vertexShader);
//			GL20.glAttachShader(shaderProgram, fragmentShader);
//
//			GL20.glLinkProgram(shaderProgram);
//			GL20.glValidateProgram(shaderProgram);
//
//			int scaleUniformLocation = GL20.glGetUniformLocation(shaderProgram, "scale");
//			int offsetUniformLocation = GL20.glGetUniformLocation(shaderProgram, "offset");
//			
//			GLFW.glfwPollEvents();
//			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
//
//			GL20.glUseProgram(shaderProgram);
//			glUniform2f(offsetUniformLocation, offset.x, offset.y);
//			glUniform1f(scaleUniformLocation, scale);
//			
//			glDrawArrays(GL_LINES, 0, tesselationLevel * glyphTriplets * 2);
//			
//			GLFW.glfwSwapBuffers(runningWindow.getWindowId());
//		}
	}
}
