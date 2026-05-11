package front.rendering;

import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRANSPARENT_FRAMEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import front.rendering.gui.Canvas;

public class Window {

	private int width;
	private int height;
	
	private String title;
	private final long windowId;
	GLCapabilities windowCapabilities;	
	public ArrayList<Canvas> windowCanvases;
	
 	public Window(int width, int height, String title) {
 		 this.width = width;
 	    this.height = height;
 		
 		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW_RESIZABLE,GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW_DECORATED,GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER,GLFW_TRUE);
		windowId = glfwCreateWindow(width,height,title,0,0);
		GLFW.glfwMakeContextCurrent(windowId);
		windowCapabilities = GL.createCapabilities();
		GLFW.glfwSetWindowPos(windowId,560,240);
		GLFW.glfwShowWindow(windowId);
		windowCanvases = new ArrayList<>();
	}
	
	public int getWindowWidth() {
		return width;
	}
	
	public int getWindowHeight() {
		return height;
	}
	
	public String getWindowTitle() {
		return title;
	}
	
	public long getWindowId() {
		return windowId;
	}

	public GLCapabilities getWindowCapabilities() {
		return windowCapabilities;
	}
	
}
