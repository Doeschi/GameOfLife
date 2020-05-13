package components;

import main.Main;
import processing.core.PApplet;

/**
 * Klasse für die Zellen des Game of Life.
 * Jede Zelle im Spiel wird durch eine Instanz dieser Klasse repräsentiert.
 */
public class Cell extends BaseComponent {
    private boolean alive;

    private int red;
    private int green;
    private int blue;

    /**
     * Konstruktor der Zelle. Die Zelle wird als tote Zelle instanziert.
     *
     * @param x X Koordinate der Zelle
     * @param y Y Koordiante der Zelle
     */
    public Cell(int x, int y) {
        super(x, y);
        kill();
    }

    /**
     * Konstruktor der Zelle. Ob die Zelle lebt oder tot ist, kann im Konstruktor mitgegeben werden.
     *
     * @param x     X Koordinate der Zelle
     * @param y     Y Koordiante der Zelle
     * @param alive Boolean, ob die Zelle lebt oder nicht
     */
    public Cell(int x, int y, boolean alive) {
        super(x, y);
        this.alive = alive;
    }

    public Cell(int x, int y, boolean alive, int red, int green, int blue) {
        super(x, y);
        this.alive = alive;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Zeichnet die Zelle als Quadrat. Eine tote Zelle wird weiss gezeichnet (255),
     * eine lebende scharz (0).
     *
     * @param pApplet Processing Objekt, welches Funktionen zum Zeichnen anbietet
     */
    @Override
    public void draw(PApplet pApplet) {
        pApplet.noStroke();
        if (alive) {
            pApplet.fill(255);
        } else {
            pApplet.fill(red, green, blue);
        }
        pApplet.square(x, y, Main.cellSize);
        pApplet.g.stroke = true;
    }

    /**
     * Diese Mehthode bereitet den Status der Zelle (Variable alive) der nächsten Generation vor.
     * Es wird durch die Nachbaren der Zelle geloopt und gezählt, wie viele leben.
     * Danach wird der Status der Zelle anhand den Regeln von GoL gesetzt.
     *
     * @param cells
     */
    public void prepareNextGen(Cell[][] cells) {
        if(Main.counter % 2 == 0) {
            if (red > 0) {
                red--;
            }
            else if (blue > 65) {
                blue--;
            }
        }

        int xPos = x / Main.cellSize;
        int yPos = y / Main.cellSize;

        int neighbors = 0;

        // Loop duch die Nachbaren
        for (int y = yPos - 1; y < yPos + 2; y++) {
            for (int x = xPos - 1; x < xPos + 2; x++) {

                // Die Zelle selbst soll nicht gezählt werden
                if (x == xPos && y == yPos) {
                    continue;
                }
                try {
                    if (cells[y][x].isAlive()) {
                        neighbors++;
                    }
                } catch (IndexOutOfBoundsException ex) {
                    // Do Nothing
                }
            }
        }

        // Zelle Tot und genau 3 Nachbaren
        if (!alive && neighbors == 3) {
            alive = true;
        }
        // Zelle lebt
        else if (alive) {
            // Weniger als 2 Nachbaren (0 oder 1)
            if (neighbors < 2) {
                kill();
            }
            // Mehr als 3 Nachbaren (4, 5, 6, 7, 8)
            else if (neighbors > 3) {
                kill();
            }
        }
    }

    /**
     * Setzt die Variable alive auf true.
     */
    public void revive() {
        alive = true;
    }

    /**
     * Setzt die Variable alive auf false.
     */
    public void kill() {
        alive = false;
        red = 255;
        green = 0;
        blue = 255;
    }

    /**
     * @return Gibt den Status der Zelle zurück.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Erstellt eine Kopie von sich selbst und gibt diese zurück.
     *
     * @return Kopie von sich selbst
     */
    public Cell getCopy() {
        return new Cell(x, y, alive, red, green, blue);
    }
}
