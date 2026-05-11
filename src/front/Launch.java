package front;

import static org.lwjgl.glfw.GLFW.glfwInit;

import java.io.IOException;

import org.joml.Vector2i;

import org.lwjgl.glfw.GLFW;

import front.rendering.Renderer;
import front.rendering.Window;
import front.rendering.fonts.Font;
import front.rendering.fonts.FontParser;
import front.rendering.gui.Canvas;
import front.rendering.gui.text.Text;
import front.rendering.gui.text.TextBox;

public class Launch {
	
	private static Window runningWindow;
	private static Renderer testRenderer;
	
	public static Font font_TimesNewRoman;
	public static Font font_Arial;
	
	private static Text targetText;
	
	static {
		if (!glfwInit()) {
			System.err.println("Failed to initialize GLFW, app will abort!");
			System.exit(1);
		}
		runningWindow = new LandingPage(800,600, "GrowFlow-Unauthenticated");
		testRenderer = new Renderer();
		
		font_TimesNewRoman = FontParser.parseFontFile("C:\\Windows\\Fonts\\segoeui.ttf");
		font_Arial = FontParser.parseFontFile("C:\\Windows\\Fonts\\arialbd.ttf");
	}
	
	public static void main(String args[]) throws IOException {
			
		Canvas titleCanvas =  new Canvas(runningWindow,new Vector2i(0,550),75,75,0);
		TextBox titleBox = new TextBox(Launch.font_Arial,new Vector2i(0,0),new Vector2i(0,0),0);
		titleBox.text.content = "GrowFlow-Unauthenticated";
		titleCanvas.addElement(titleBox);		
		runningWindow.windowCanvases.add(titleCanvas);
		
		Canvas UsernameLabelCanvas =  new Canvas(runningWindow,new Vector2i(0,350),75,75,0);
		TextBox UsernameLabelText = new TextBox(Launch.font_Arial,new Vector2i(0,0),new Vector2i(0,0),0);
		UsernameLabelText.text.content = "Username:";
		UsernameLabelCanvas.addElement(UsernameLabelText);		
		runningWindow.windowCanvases.add(UsernameLabelCanvas);
		
		Canvas PasswordLabelCanvas =  new Canvas(runningWindow,new Vector2i(0,200),75,75,0);
		TextBox PasswordLabelText = new TextBox(Launch.font_Arial,new Vector2i(0,0),new Vector2i(0,0),0);
		PasswordLabelText.text.content = "Password:";
		PasswordLabelCanvas.addElement(PasswordLabelText);		
		runningWindow.windowCanvases.add(PasswordLabelCanvas);
		
		Canvas UsernameCanvas =  new Canvas(runningWindow,new Vector2i(0,300),75,75,0);
		TextBox UsernameText = new TextBox(Launch.font_TimesNewRoman,new Vector2i(0,0),new Vector2i(0,0),0);
		UsernameText.text.content = "";
		UsernameCanvas.addElement(UsernameText);		
		runningWindow.windowCanvases.add(UsernameCanvas);
		
		Canvas PasswordCanvas =  new Canvas(runningWindow,new Vector2i(0,125),75,75,0);
		TextBox PasswordText = new TextBox(Launch.font_TimesNewRoman,new Vector2i(0,0),new Vector2i(0,0),0);
		PasswordText.text.content = "";
		PasswordCanvas.addElement(PasswordText);
		runningWindow.windowCanvases.add(PasswordCanvas);
		
		targetText = UsernameText.text;
		
		GLFW.glfwSetKeyCallback(runningWindow.getWindowId(),(window,key,scan,action,mod)->{
			if(key == GLFW.GLFW_KEY_ENTER && action == GLFW.GLFW_PRESS)
				targetText = PasswordText.text;
			if(key == GLFW.GLFW_KEY_BACKSPACE && action == GLFW.GLFW_PRESS)
				if(targetText.content.length()!=0)
					targetText.content = targetText.content.substring(0,targetText.content.length()-1);
		});
		
		GLFW.glfwSetCharCallback(runningWindow.getWindowId(),(window,code_point)->{
			if(code_point != ' ')
				targetText.content += (char)code_point;
		});
		
		
		
		while (runningWindow != null) {
			GLFW.glfwPollEvents();
			testRenderer.renderWindow(runningWindow);
		}
	}
}
