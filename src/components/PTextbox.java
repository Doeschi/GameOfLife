package components;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Klasse für GUI Textbox
 */
public class PTextbox extends BaseTextComponent {

    /**
     * Boolean, ob die Textbox fokusiert ist oder nicht
     */
    private boolean focus;
    private boolean enable;

    /**
     * Boolean, ob ein '_' gezeichnet werden soll
     * (zum symbolisieren, dass das Feld im Fokus ist)
     */
    private boolean drawBlinky;

    /**
     * Konstruktor für die Textbox. focus und drawBlinky werden beide auf false gesetzt
     *
     * @param x      X Koordinate der Textbox
     * @param y      Y Koordinate der Textbox
     * @param width  Breite der Textbox
     * @param height Höhe der Textbox
     * @param text   Text der angezeigt werden soll
     */
    public PTextbox(int x, int y, int width, int height, String text) {
        super(x, y, width, height, text);
        focus = false;
        drawBlinky = false;
        enable = true;
    }

    /**
     * Zeichnet die Textbox als Rechteck mit zentriertem Text.
     * Alle 15 Frames (ca. alle 0.5 Sekunden) wird die Variable drawBlinky auf true bzw. false gestellt.
     * Ist drawBlicky auf true und das Feld fokusiert, wird der Text mit einem angehängten '_' gezeichnet.
     * Dies sorgt dafür, dass der Benutzer visualisert bekommt, dass das Feld fokusiert ist.
     *
     * @param pApplet Processing Objekt, welches Funktionen zum Zeichnen anbietet
     */
    @Override
    public void draw(PApplet pApplet) {
        // Setzt die dicke der Aussenlinien auf 1px
        pApplet.strokeWeight(1);
        // Setz die Füllfarbe des Hintergrundes
        pApplet.fill(255);
        pApplet.rect(x, y, width, height);
        // Setz die Füllfarbe des Textes
        if (enable) {
            pApplet.fill(0);
        } else {
            pApplet.fill(200);
        }
        // Setzt das Verhalten der Methode pApplet.text()
        pApplet.textAlign(PConstants.CENTER, PConstants.CENTER);

        if (focus && drawBlinky) {
            pApplet.text(text + "_", x + width / 2, y + height / 2);
        } else {
            pApplet.text(text, x + width / 2, y + height / 2);
        }

        if (pApplet.frameCount % 15 == 0) {
            drawBlinky = !drawBlinky;
        }
    }

    /**
     * Prüft, ob das angegebene Symbol eine Zahl ist.
     * Wenn ja, wird es dem angezeigten Text angehängt.
     *
     * @param c Das Symbol, das am Text angehängt werden soll
     */
    public void addChar(char c) {
        if (Character.isDigit(c)) {
            text += c;
        }
    }

    /**
     * Löscht das zuletzt hinzugefügte Zeichen.
     * Wenn der Text leer sein sollte, wird die Zahl 1 als Text gesetzt.
     */
    public void removeChar() {
        if (!text.equals("")) {
            text = text.substring(0, text.length() - 1);
        }
    }

    /**
     * @return Boolean, ob das Feld fokusiert ist oder nicht
     */
    public boolean isFocused() {
        return focus;
    }

    /**
     * Diese Mehthode prüft, ob mit der Maus auf die Textbox gedrückt wurde.
     * Wenn ja, wird der Fokus auf true gestellt.
     * Ansonsten wird der Fokus auf false gestellt.
     *
     * @param mouseX X Koordinate der Maus
     * @param mouseY Y Koordinate der Maus
     */
    public void mousePressed(int mouseX, int mouseY) {
        if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) {
            focus = true;
        } else {
            focus = false;
        }
    }

    /**
     * @return Gibt den Inhalt der Textbox als String zurück
     */
    public String getText() {
        return text;
    }

    /**
     * Setzt den Text, der in der Textbox angezeigt wird
     *
     * @param text text der angezeigt werden soll
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Setzt die Variable enabled auf den als Parameter mitgegebenen Wert. (aktivieren/deaktivieren)
     *
     * @param enable Boolean, ob der Knopf aktiviert oder deaktiviert ist.
     */
    public void setEnable(boolean enable){
        this.enable = enable;

        if (!enable){
            focus = false;
        }
    }
}
