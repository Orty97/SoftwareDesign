package front;

import static org.lwjgl.glfw.GLFW.glfwInit;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import front.rendering.Renderer;
import front.rendering.Window;

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
		
		while(runningWindow != null) {
			GLFW.glfwPollEvents();
			//TODO process inputs / pending updates
			Renderer.renderWindow(runningWindow);
		}
	}	
}
