package components;

import processing.core.PApplet;
import processing.core.PConstants;

public class PTextbox extends BaseComponent {

    private int width;
    private int height;
    private String text;
    private boolean focus;
    private boolean drawBlinky;

    public PTextbox(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
        text = "";
        focus = false;
        drawBlinky = false;
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.fill(255);
        pApplet.rect(x, y, width, height);

        pApplet.fill(0);
        pApplet.textAlign(PConstants.CENTER, PConstants.CENTER);

        if(focus && drawBlinky){
            pApplet.text(text + "_", x + width / 2, y + height / 2);
        } else {
            pApplet.text(text, x + width / 2, y + height / 2);
        }

        if (pApplet.frameCount % 15 == 0){
            drawBlinky = !drawBlinky;
        }
    }

    public void addChar(char c) {
        if(Character.isDigit(c)){
            text += c;
        }
    }

    public void removeChar() {
        if (!text.equals("")) {
            text = text.substring(0, text.length() - 1);
        }
    }

    public boolean isFocused() {
        return focus;
    }

    public void keyPressed(int mouseX, int mouseY) {
        if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) {
            focus = true;
            drawBlinky = true;
        } else {
            focus = false;
            drawBlinky = false;
        }
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }
}
