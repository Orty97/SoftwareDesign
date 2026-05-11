package front.rendering.gui.text;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

import org.joml.Matrix4f;
import org.joml.Vector2i;

import front.rendering.fonts.Font;
import front.rendering.gui.GUIElement;

public class TextBox extends GUIElement{
	
	public Vector2i dimensions;
	public Text text;
	public Font font;
	public int textSize;
	
	public TextBox(Font font,Vector2i position, Vector2i dimensions, int layer) {
		super(position.x,position.y,layer);
		this.dimensions = dimensions;		
		this.font = font;
		this.text = new Text();
		textSize = 1;
	}

	public void changeFont(Font new_font) {
		this.font = new_font;
	}
	
	@Override
	public void render(Matrix4f canvas_transform) {
		float cursorAdvance = 0;
		
		font.useFontProgram();
		glBindVertexArray(0);
		
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0 , font.pointsSSBO);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1 , font.contoursSSBO);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2 , font.geometriesSSBO);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3 , font.instancesSSBO);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 4, font.glyphsSSBO);
	    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 5 , font.transformsSSBO);

		for(char c:text.content.toCharArray()) {
			font.setRenderTargetGlyph((int)c);
			font.setTextTransform(canvas_transform);
			font.setCursorRenderPosition(cursorAdvance);
			glDrawArrays(GL_LINE_STRIP,0,100);
			cursorAdvance += font.glyphAdvanceWidth[font.unicodeToGlyphIdMap.get((int)c)];
		}
		font.stopUsingFontProgram();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void interact() {
		// TODO Auto-generated method stub
	}
}
