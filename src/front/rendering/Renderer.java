package front.rendering;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL.setCapabilities;

import org.joml.Matrix4f;

import org.lwjgl.opengl.GL30;

import front.rendering.gui.Canvas;
import front.rendering.gui.GUIElement;

public class Renderer {
	public void renderWindow(Window target_window) {
		glfwMakeContextCurrent(target_window.getWindowId());
		setCapabilities(target_window.getWindowCapabilities());
		GL30.glClear(GL30.GL_COLOR_BUFFER_BIT|GL30.GL_DEPTH_BUFFER_BIT);
		
		for(Canvas canvas : target_window.windowCanvases)
			for(GUIElement element : canvas.getAllElements())
				renderElement(element,canvas.getCanvasTransform());

		glfwSwapBuffers(target_window.getWindowId());
	}
	
	private void renderElement(GUIElement element,Matrix4f canvas_transform) {
		element.render(canvas_transform);
	}
}
