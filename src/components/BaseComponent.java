package components;

import processing.core.PApplet;

public abstract class BaseComponent {
    protected int x;
    protected int y;

    public BaseComponent(int x, int y){
        this.x = x;
        this.y = y;
    }

    public abstract void draw(PApplet pApplet);
}
