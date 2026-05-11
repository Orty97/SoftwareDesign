package front.rendering.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector2i;

import front.rendering.Window;

public class Canvas {

	public Vector2i position;
	
	public int width;
	public int height;
	
	public int layer;
	
	HashMap<Integer,ArrayList<GUIElement>> elements;
	
	private Window parent;
	
	public Canvas(Window parent, Vector2i position, int width, int height, int layer) {
		this.width = width;
		this.height = height;
		this.layer = layer;
		this.position = position;
		this.parent = parent;
		elements = new HashMap<>();
	}
	
	public void addElement(GUIElement element) {
		if(elements.get(element.getLayer()) == null)
			elements.put(element.getLayer(),new ArrayList<GUIElement>());
		elements.get(element.getLayer()).add(element);
	}
	
	public Collection<GUIElement> getAllLayerElements(int layer){
		return elements.get(layer);
	}
	
	public Collection<GUIElement> getAllElements(){
		ArrayList<GUIElement> elementsCollection = new ArrayList<GUIElement>();
		for(Collection<GUIElement> collection : elements.values())
			elementsCollection.addAll(collection);
		return elementsCollection;
	}
	
	public Set<Integer> getAllLayers(){
		return elements.keySet();
	}
	
	public Matrix4f getCanvasTransform() {
	    float sx = 2.0f * width / parent.getWindowWidth();
	    float sy = 2.0f * height / parent.getWindowHeight();
	    float tx = 2.0f * position.x / parent.getWindowWidth() - 1.0f;
	    float ty = 2.0f * position.y / parent.getWindowHeight() - 1.0f;

	    return new Matrix4f()
	        .translate(tx, ty, 0)
	        .scale(sx, sy, 1);
	}
}
