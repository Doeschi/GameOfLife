package components;

import processing.core.PApplet;
import processing.core.PConstants;

public class PLabel extends BaseTextComponent{

    public PLabel(int x, int y, int width, int height, String text) {
        super(x, y, width, height, text);
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.fill(235);
        pApplet.rect(x, y, width, height);

        pApplet.fill(0);
        pApplet.textAlign(PConstants.CENTER, PConstants.CENTER);
        pApplet.text(text, x + width / 2, y + height / 2);
    }

    public void setText(String text){
        this.text = text;
    }
}
