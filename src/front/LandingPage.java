package front;

import static org.lwjgl.opengl.GL11.glClearColor;

import front.rendering.Window;

public class LandingPage extends Window{

	public LandingPage(int width, int height, String title) {
		super(width,height,title);
		glClearColor(207f/255f,255/255f,102f/255f,1.0f);	
	}
}
