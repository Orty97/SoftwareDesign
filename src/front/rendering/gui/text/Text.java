package front.rendering.gui.text;

public class Text {

	public String content;
	
	public Text() {
		content = new String();
	}
	
	public Text(String content) {
		this.content = content;
	}
	
	public Text(char[] content) {
		this.content = new String(content);
	}	
}
