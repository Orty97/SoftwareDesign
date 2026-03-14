package front.rendering;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Renderer {
	public static void renderWindow(Window target_window) {
		glfwMakeContextCurrent(target_window.getWindowId());
		glClear(GL_DEPTH_BUFFER_BIT|GL_COLOR_BUFFER_BIT);
		//WIP  = add window elements rendering;
		glfwSwapBuffers(target_window.getWindowId());
	}
	
	private static void renderElement() {
		
	}
}
