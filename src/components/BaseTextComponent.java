package components;

import processing.core.PApplet;

public abstract class BaseTextComponent extends BaseComponent {
    protected int width;
    protected int height;
    protected String text;

    public BaseTextComponent(int x, int y, int width, int height, String text){
        super(x, y);
        this.width = width;
        this.height = height;
        this.text = text;
    }
}
