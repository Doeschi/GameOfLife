package components;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Klasse für GUI Buttons
 */
public abstract class PButton extends BaseTextComponent {

    /**
     * Boolean, ob der Knopf gerade gedrückt ist oder nicht
     */
    private boolean clicked;

    /**
     * Boolean, ob der Knopf aktiviert (klickbar) ist oder nicht
     */
    private boolean enabled;

    /**
     * Konstruktor des Buttons. Der Button wird enabled und clicked auf false gestellt.
     *
     * @param x      X Koordinate des Buttons
     * @param y      Y Koordinate des Buttons
     * @param width  Breite des Buttons
     * @param height Höhe des Buttons
     * @param text   der Text der angezeigt werden soll
     */
    public PButton(int x, int y, int width, int height, String text) {
        super(x, y, width, height, text);
        this.clicked = false;
        this.enabled = true;
    }

    /**
     * Zeichnet ruft die Methode drawButton. Die Parameter unterscheiden sich je Zustand der Buttons.
     *
     * @param pApplet Processing Objekt, welches Funktionen zum Zeichnen anbietet
     */
    public void draw(PApplet pApplet) {
        // Button kann nur geklickt sein, wenn er auch enabled ist
        if (clicked) {
            // Hintergrund fast weiss und Text schwarz
            drawButton(pApplet, 225, 0);
        } else if (!enabled) {
            // Hintergrund weiss und Text hellgrau
            drawButton(pApplet, 255, 200);
        } else {
            // Hintergrund weiss und Text schwarz
            drawButton(pApplet, 255, 0);
        }
    }

    /**
     * Zeichnet den Button als Rechteck mit zentriertem Text.
     *
     * @param pApplet    Processing Objekt, welches Funktionen zum Zeichnen anbietet
     * @param background Hintergrund Farbe
     * @param textColor  Text Farbe
     */
    private void drawButton(PApplet pApplet, int background, int textColor) {
        // Setzt die dicke der Aussenlinien auf 1px
        pApplet.strokeWeight(1);
        // Setzt die Füllfarbe
        pApplet.fill(background);
        pApplet.rect(x, y, width, height);
        // Setzt die Füllfarbe
        pApplet.fill(textColor);
        // Setzt das Verhalten der Methode pApplet.text()
        pApplet.textAlign(PConstants.CENTER, PConstants.CENTER);
        pApplet.text(text, x + width / 2, y + height / 2);
    }

    /**
     * Überprüft, ob mit der Maus auf den Button gedrückt wurde,
     * und ob dieser aktiviert ist. Ist dies der Fall wird
     * clicked auf true gestellt.
     *
     * @param mouseX X Koordinate der Maus
     * @param mouseY Y Koordinate der Maus
     */
    public void mousePressed(int mouseX, int mouseY) {
        if (mouseX > x &&
                mouseX < x + width &&
                mouseY > y &&
                mouseY < y + height &&
                enabled) {
            clicked = true;
        }
    }

    /**
     * Wenn der Knopf gedrückt ist, wird das ButtonEvent
     * ausgeführt und clicked wieder auf false gestellt.
     */
    public void mouseReleased() {
        if (clicked) {
            buttonEvent();
            clicked = false;
        }
    }

    /**
     * Setzt die Variable enabled auf den als Parameter mitgegebenen Wert. (aktivieren/deaktivieren)
     *
     * @param enabled Boolean, ob der Knopf aktiviert oder deaktiviert ist.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Setzt den Text des Buttons.
     * @param text Text der angezeigt werden soll
     */
    public void setText(String text){
        this.text = text;
    }

    /**
     * Diese Methode erst beim Instanzieren eines Buttons ausprogrammiert.
     * Diese Methode wird ausgelöst, wenn die Maus released wurde und der Button
     * zuvor geklickt wurde.
     */
    public abstract void buttonEvent();
}
