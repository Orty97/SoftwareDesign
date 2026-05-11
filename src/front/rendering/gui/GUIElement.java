package front.rendering.gui;

import org.joml.Matrix4f;
import org.joml.Vector2i;

public abstract class GUIElement {
	
	private int layer;
	private Vector2i position;
	
	public GUIElement(int x_pos, int y_pos, int layer) {
		this.layer = layer;
		position = new Vector2i();
		position.x = x_pos;
		position.y = y_pos;		
	}

	public int getLayer() {
		return layer;
	}
	
	public Vector2i getPosition() {
		return position;
	}
	
	public abstract void render(Matrix4f canvas_transform);
	
	public abstract void update();
	
	public abstract void interact();	
}
