package components;

/**
 * Basis Klasse für alle Text Komponenten
 */
public abstract class BaseTextComponent extends BaseComponent {
    protected int width;
    protected int height;
    protected String text;

    /**
     * Konstruktor für Text Komponenten
     *
     * @param x      X Koordinate des Elementes
     * @param y      Y Koordinate des Elementes
     * @param width  Breite des Elementes
     * @param height Höhe des Elementes
     * @param text   Text der angezeigt werden soll
     */
    public BaseTextComponent(int x, int y, int width, int height, String text) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.text = text;
    }
}
