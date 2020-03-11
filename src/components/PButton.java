package components;

import processing.core.PApplet;
import processing.core.PConstants;

public abstract class PButton extends BaseTextComponent{
    private boolean clicked;
    private boolean enabled;

    public PButton(int x, int y, int width, int height, String text){
        super(x, y, width, height, text);
        this.clicked = false;
        this.enabled = true;
    }

    public void draw(PApplet pApplet){
        if (clicked && enabled){
            drawButton(pApplet, 225, 0);
        }else if(!enabled){
            drawButton(pApplet, 255, 200);
        }
        else{
            drawButton(pApplet, 255, 0);
        }
    }

    private void drawButton(PApplet pApplet, int background, int textColor){
        pApplet.fill(background);
        pApplet.rect(x, y, width, height);
        pApplet.fill(textColor);
        pApplet.textAlign(PConstants.CENTER, PConstants.CENTER);
        pApplet.text(text, x + width / 2, y + height / 2);
    }

    public void mousePressed(int mouseX, int mouseY){
        if (mouseX > x &&
            mouseX < x + width &&
            mouseY > y &&
            mouseY < y + height &&
            enabled){
            clicked = true;
        }
    }

    public void mouseReleased(){
        if (clicked){
            buttonEvent();
            clicked = false;
        }
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public abstract void buttonEvent();
}
