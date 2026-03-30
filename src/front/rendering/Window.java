package front.rendering;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public class Window {

	private int width;
	private int height;
	
	private String title;
	private final long windowId;
	GLCapabilities windowCapabilities;	
	
	public Window(int width, int height, String title) {
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW_RESIZABLE,GLFW_FALSE);
				
		windowId = glfwCreateWindow(width,height,title,0,0);
		GLFW.glfwMakeContextCurrent(windowId);
		windowCapabilities = GL.createCapabilities();
	}
	
	protected int getWindowWidth() {
		return width;
	}
	
	protected int getWindowHeight() {
		return height;
	}
	
	protected String getWindowTitle() {
		return title;
	}
	
	public long getWindowId() {
		return windowId;
	}
}
