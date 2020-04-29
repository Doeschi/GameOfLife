package components;

import processing.core.PApplet;

/**
 * Basis Klasse für alle GUI Komponenten
 */
public abstract class BaseComponent {
    protected int x;
    protected int y;

    /**
     * Konstruktor für die Basisklasse
     *
     * @param x X Koordinate der Komponente
     * @param y Y Koordinate der Komponente
     */
    public BaseComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Methode zum Zeichnen der Komponente
     *
     * @param pApplet Processing Objekt, welches Funktionen zum Zeichnen anbietet
     */
    public abstract void draw(PApplet pApplet);
}
