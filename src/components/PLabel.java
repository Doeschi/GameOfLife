package components;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Klasse für GUI Labels.
 */
public class PLabel extends BaseTextComponent {

    /**
     * Konstruktor des Labels.
     *
     * @param x      X Koordinate des Labels
     * @param y      Y Koordinate des Labels
     * @param width  Breite des Labels
     * @param height Höhe des Labels
     * @param text   der Text der angezeigt werden soll
     */
    public PLabel(int x, int y, int width, int height, String text) {
        super(x, y, width, height, text);
    }

    /**
     * Zeichnet das Label als Rechteck mit zentriertem Text.
     *
     * @param pApplet Processing Objekt, welches Funktionen zum Zeichnen anbietet
     */
    @Override
    public void draw(PApplet pApplet) {
        // Setzt die dicke der Aussenlinien auf 1px
        pApplet.strokeWeight(1);
        // Setz die Füllfarbe
        pApplet.fill(235);
        pApplet.rect(x, y, width, height);
        // Setz die Füllfarbe
        pApplet.fill(0);
        // Setzt das Verhalten der Methode pApplet.text()
        pApplet.textAlign(PConstants.CENTER, PConstants.CENTER);
        pApplet.text(text, x + width / 2, y + height / 2);
    }

    /**
     * Setzt den Text, der angezeigt werden soll.
     *
     * @param text Text zum anzeigen
     */
    public void setText(String text) {
        this.text = text;
    }
}
