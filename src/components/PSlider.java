package components;

import processing.core.PApplet;

public class PSlider extends BaseComponent {
    private int width;
    private int previousMouseX;
    private boolean pressed;

    private int sliderX;
    private int sliderRadius;

    public PSlider(int x, int y, int width) {
        super(x, y);
        this.width = width - 10;
        this.sliderX = this.x + this.width / 2;
        this.sliderRadius = 10;
    }

    @Override
    public void draw(PApplet pApplet) {
        pApplet.strokeWeight(3);
        pApplet.line(x + 5, y, x + width - 5, y);
        pApplet.strokeWeight(1);

        pApplet.fill(125);
        pApplet.circle(sliderX, y, sliderRadius * 2);
    }

    public void mousePressed(int mouseX, int mouseY){
        if (PApplet.dist(sliderX, y, mouseX, mouseY) <= sliderRadius){
            pressed = true;
            previousMouseX = mouseX;
        }
    }

    public void mouseDragged(int mouseX){
        if (pressed) {
            if (mouseX > x + sliderRadius && mouseX < x + width - sliderRadius){
                sliderX += mouseX - previousMouseX;
            }
            previousMouseX = mouseX;
        }
    }

    public void mouseReleased(){
        pressed = false;
    }
}
