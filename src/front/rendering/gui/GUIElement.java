package front.rendering.gui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;

public abstract class GUIElement {
	
	private int layer;
	
	private Vector2f position;
	private Quaternionf rotation;
	
	private Matrix4f transform;
	
	private final int VAO;
	private final int VBO;
	private final int IBO;
	private final int TCBO;
	
	public GUIElement(double x_pos, double y_pos, double width, double height) {
		
		int layer = 0;
		
		position.x = (float)x_pos;
		position.y = (float)y_pos;		
		
		rotation = new Quaternionf();
		transform = new Matrix4f();
		
		VAO = glGenVertexArrays();
		VBO = glGenBuffers();
		IBO = glGenBuffers();
		TCBO = glGenBuffers();
		
		genMesh();
	}
	
	private final void genMesh() {
		glBindVertexArray(VAO);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER,generateVertices(),GL_STATIC_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		
		glBindBuffer(GL_ARRAY_BUFFER,TCBO);
		glBufferData(GL_ARRAY_BUFFER,generateTextCoords(),GL_STATIC_DRAW);
		glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,IBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,generateIndices(),GL_STATIC_DRAW);
	}
	
	private final FloatBuffer generateVertices() {
		//WIP make vertices
		return null;
	}
	
	private final FloatBuffer generateTextCoords() {
		//WIP make texture coords
		return null;
	}
	
	private final IntBuffer generateIndices() {
		//WIP make indices
		return null;
	}
	
	public abstract void render();
	
	public abstract void update();
	
	public abstract void interact();	
}
