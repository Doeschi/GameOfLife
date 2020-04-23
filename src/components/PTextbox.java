package components;

import processing.core.PApplet;
import processing.core.PConstants;

public class PTextbox extends BaseComponent{

    private int width;
    private int height;
    private String text;
    private boolean focus;

    public PTextbox(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
        text = "";
        focus = false;
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.fill(255);
        pApplet.rect(x, y, width, height);

        pApplet.fill(0);
        pApplet.textAlign(PConstants.CENTER, PConstants.CENTER);
        pApplet.text(text, x + width / 2, y + height / 2);
    }

    public void addChar(char c){
        text += c;
    }

    public void removeChar(){
        text = text.substring(0, text.length() - 2);
    }

    public boolean isFocused(){
        return focus;
    }

    public void keyPressed(int mouseX, int mouseY){
        if(mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height){
            focus = true;
        }else {
            focus = false;
        }
    }
}
