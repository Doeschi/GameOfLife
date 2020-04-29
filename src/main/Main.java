package main;

import components.*;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

public class Main extends PApplet {


    public static final int cellSize = 5;
    public static final int componentWidth = 150;
    public static final int componentHeight = 50;

    public static final int windowWidth = 900;
    public static final int windowHeight = 600;
    // Breite des GoL Feldes
    public static final int sketchWidth = windowWidth;
    // Höhe des GoL Feldes
    public static final int sketchHeight = windowHeight - (2 * componentHeight);

    // Prozentsatz, wie viel bei Beginn leben sollen
    public static final int firstGenProbability = 60;

    private Cell[][] currentGen;
    // Arrayliste mit allen vergangen Genertationen
    private ArrayList<Cell[][]> previousGens;

    // Liste aller Buttons
    private ArrayList<PButton> buttons;

    private PButton buttonStartStop;
    private PButton buttonClear;
    private PButton buttonRandom;
    private PButton buttonPrevious;
    private PButton buttonNext;
    private PButton buttonSave;

    private PLabel cellCounter;
    private PLabel info;
    private PTextbox textbox;

    // Boolean, ob das Spiel läuft oder nicht
    private boolean running;

    /**
     * Diese Methode wird beim Programmstart als erstes aufgerufen.
     * Die Methode legt die Fenstergrösse fest.
     */
    @Override
    public void settings() {
        size(windowWidth, windowHeight);
    }

    /**
     * Diese Methode wird beim Programmstart als zweites aufgerufen
     * Es werden Attribute auf die gewünschten Werte gesetzt, die erste
     * Generation des GoL vorbereitet und alle GUI Komponenten initialisiert.
     */
    @Override
    public void setup() {
        previousGens = new ArrayList<>();
        frameRate(30);
        textSize(14);
        if (cellSize == 1) {
            noStroke();
        }
        prepareFirstGen();
        initComponents();
        updateLabels();
        running = false;
    }

    /**
     * Diese Mehthode wird einmal pro Frame aufgerufen (bei 30 FPS, 30 mal in der Sekunde)
     * Das aktuelle Fenster wird mit background() komplett übermalt. Wenn das GoL läuft, wird alle
     * 15 Frames (ca. alle 0.5 Sekunden) eine neue Generation vorbereitet.
     * Danach wird das Komplette GoL Spielfeld sowie die GUI Komponenten gezeichnet
     */
    @Override
    public void draw() {
        background(255);
        if (running && frameCount % ((int) (frameRate / 2)) == 0) {
            prepareNextGen();
        }
        drawWindow();
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Taste auf der Tastatur gedrückt wird.
     * Ist das Textfeld fokusiert, wird die gedrückte Taste verarbeitet.
     */
    @Override
    public void keyPressed() {
        if (textbox.isFocused()) {
            if (keyCode == BACKSPACE) {
                textbox.removeChar();
            } else {
                textbox.addChar(key);
            }
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Maustaste gedrückt wurde.
     */
    @Override
    public void mousePressed() {
        if (!running) {
            processClickedCell();
        }

        // Teilt allen Buttons mit, dass die Maus gedrückt wurde
        for (PButton button : buttons) {
            button.mousePressed(mouseX, mouseY);
        }

        // Teil der Textbox mit, das die Maus gedrückt wurde
        textbox.mousePressed(mouseX, mouseY);
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Maustaste gedrückt ist und die Maus bewegt wird.
     */
    @Override
    public void mouseDragged() {
        if (!running) {
            processClickedCell();
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Maustaste losgelassen wird.
     */
    @Override
    public void mouseReleased() {
        for (PButton button : buttons) {
            button.mouseReleased();
        }
    }

    /**
     * Diese Methode holt sich die aktuell gedrückte Zelle.
     * Ist diese Null, wurde keine gedrückt. Ansonsten wird die Zelle
     * belebt, wenn die linke Maustaste gedrückt wird und getötet, wenn
     * die rechte Maustaste gedrückt wird.
     */
    private void processClickedCell() {
        Cell pressedCell = getCell();
        if (pressedCell != null) {
            if (mouseButton == LEFT) {
                pressedCell.revive();
            } else if (mouseButton == RIGHT) {
                pressedCell.kill();
            }
        }
    }

    /**
     * Diese Methode bereitet die erste Generation des GoL vor.
     * Die Anzahl Zellen wird durch SketchHeight/CellSize und SketchWidth/CellSize bestimmt.
     * Jede Zelle hat eine Wahrscheinlichkeit von firstGenProbability, bei der ersten Generation am Leben zu sein.
     */
    private void prepareFirstGen() {
        Random random = new Random();
        int randomInt;

        // Initialisierung des Zweidimensionalen Arrays zum Speichern der Zellen
        currentGen = new Cell[sketchHeight / cellSize][sketchWidth / cellSize];

        // Loop über jede Zellen Position im currentGen Array
        for (int y = 0; y < sketchHeight / cellSize; y++) {
            for (int x = 0; x < sketchWidth / cellSize; x++) {
                // An jeder Position wird ein neues Zellen Objekt instanziert
                // Die X und Y Koordinate entsprechen ihrer Position im Array * CellSize
                currentGen[y][x] = new Cell(x * cellSize, y * cellSize);

                // Zufalls Zahl zwischen 0 und 99 (inklusive 0 und 99)
                randomInt = random.nextInt(100);
                if (randomInt < firstGenProbability) {
                    currentGen[y][x].revive();
                }
            }
        }

        // Zwischengespeicherte Generation löschen
        previousGens.clear();
    }

    /**
     * Hier wird die nächste Generation des GoL vorbereitet.
     */
    private void prepareNextGen() {
        // Es wird eine Kopie des Spielfeldes gemacht, damit die neue Generation erstellt werden kann
        Cell[][] nextGen = getDeepCopy();

        // Die aktuelle Generation wird gespeichert, damit bei Bedarf auf diese Generation zurück gegangen werden kann
        previousGens.add(currentGen);

        // Loop durch jede Zelle des Spielfeldes der nächsten Generation
        for (Cell[] cellRow : nextGen) {
            for (Cell cell : cellRow) {
                cell.prepareNextGen(currentGen);
            }
        }

        // Die vorbereitete Generation wird als aktuelle Generation gesetzt
        currentGen = nextGen;
    }

    /**
     * Erstellt eine Kopie des Spielfeldes, inklusiver einer Kopie der Zellen.
     * Dies ist nötig, damit die nächste Generation auf basis der aktuellen Generation verändert werden kann.
     *
     * @return Gibt eine tiefe Kopie des Spielfeldes zurück (Kopie des Feldes und der Zellen)
     */
    private Cell[][] getDeepCopy() {
        // Neues Array erstellen
        Cell[][] copy = new Cell[sketchHeight / cellSize][sketchWidth / cellSize];

        // Loop durch alle Zellen
        for (int i = 0; i < copy.length; i++) {
            for (int j = 0; j < copy[i].length; j++) {
                // Kopie der Zelle erstellen
                copy[i][j] = currentGen[i][j].getCopy();
            }
        }
        return copy;
    }

    /**
     * In dieser Methode wird GoL Spielfeld und die GUI Komponenten gezeichnet.
     */
    private void drawWindow() {
        // Updated den Text der Labels
        updateLabels();

        // Loop durch alle Zellen des Spielfeldes
        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                // Zeichnen der Zelle
                cell.draw(this);
            }
        }

        // Zeichnen der Buttons
        for (PButton button : buttons) {
            button.draw(this);
        }

        // Zeichnen der Labels
        cellCounter.draw(this);
        info.draw(this);
        textbox.draw(this);
    }

    /**
     * Diese Methode updated den Text der Labels.
     */
    private void updateLabels() {
        // Framerate auf 3 Stellen genau runden
        float currentFps = Math.round(frameRate * 1000);
        info.setText("FPS: " + (currentFps / 1000) + "\nGen: " + previousGens.size());

        // sketchHeight/cellSize ergibt die Anzahl Zeilen, sketchWidth/cellSize ergibt die Anzahl Spalten
        cellCounter.setText("Cells: " + (sketchHeight / cellSize * sketchWidth / cellSize) + "\nLiving: " + (countLivingCells()));
    }

    /**
     * Diese Methode töte alle Zellen auf dem Spielfeld.
     */
    private void killAll() {
        // Loop durch alle Zellen
        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                // Zelle töten
                cell.kill();
            }
        }
    }

    /**
     * Diese Methode setzt eine frühere Generation als die aktuelle.
     * Die Anzahl Generationen, die zurück gesprungen wird, wird aus dem Textfeld ausgelesen.
     */
    private void setPreviousGen() {
        // Wenn überhaupt vorherige Generation existieren
        if (previousGens.size() != 0) {
            // Wenn der Inhalt nicht leer ist
            if (!textbox.getText().equals("")) {
                // Wenn mehr Generationen zurück gesprungen werden soll, als überhaupt existieren, wird die erste Generation angezeigt
                if (Integer.parseInt(textbox.getText()) > previousGens.size()) {
                    // Erste Generation holen
                    currentGen = previousGens.get(0);
                    // Alle Zwischengespeicherten Generationen löschen
                    previousGens.clear();
                } else {
                    // die gewünschte Generation holen
                    currentGen = previousGens.get(previousGens.size() - Integer.parseInt(textbox.getText()));
                    // die zurückgesprungenen Generationen aus dem Zwischenspeicher löschen
                    for (int i = 0; i < Integer.parseInt(textbox.getText()); i++) {
                        previousGens.remove(previousGens.size() - 1);
                    }
                }
            }
        }
    }

    /**
     * Diese Methode gibt die Zelle zurück, über welcher sich die Maus befindet.
     * Befindet sich die Maus über keiner Zelle, wird Null zurück gegeben.
     *
     * @return Die Zelle, über der sich die Maus befindet oder Null
     */
    private Cell getCell() {
        try {
            // Integer division -> gibt keine Kommastellen
            return currentGen[mouseY / cellSize][mouseX / cellSize];
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * In dieser Methode werden alle GUI Komponenten initialisiert.
     * Zudem wird hier das ButtonEvent der verschiedenen Buttons ausprogrammiert.
     */
    private void initComponents() {
        // Button Array initialisieren
        buttons = new ArrayList<>();

        // Diese Variablen enthalten die Position der nächsten Komponente
        int xOffset = 0;
        int yOffset = sketchHeight;

        // Start/Stop Button
        buttonStartStop = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Start / Stop") {
            @Override
            public void buttonEvent() {
                // Wenn das Spiel läuft, wird es angehalten und die deaktivierten Buttons wieder aktiviert
                if (running) {
                    running = false;
                    enableButtons();
                } else {
                    // Wenn Spiel pausiert war, wird er wieder gestartet und die Buttons deaktiviert
                    running = true;
                    disableButtons();
                }
            }
        };
        buttons.add(buttonStartStop);
        xOffset += componentWidth;

        // Clear Button
        buttonClear = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Clear") {
            @Override
            public void buttonEvent() {
                // Töten aller Zellen
                killAll();
                // Alle Zwischengespeicherten Generationen löschen (Clear setzt die Generation auf 0)
                previousGens.clear();
            }
        };
        buttons.add(buttonClear);
        xOffset += componentWidth;

        // Random Gen Button
        buttonRandom = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Random Generation") {
            @Override
            public void buttonEvent() {
                // Neue erste Generation setzten
                prepareFirstGen();
            }
        };
        buttons.add(buttonRandom);
        xOffset += componentWidth;

        // Save Button
        buttonSave = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Safe Frame") {
            @Override
            public void buttonEvent() {
                // Diese Funktion speichert den gesamten Inhalt des aktuellen Programmfensters
                save("/src/images/save_image" + System.currentTimeMillis() + ".png");
            }
        };
        buttons.add(buttonSave);
        xOffset += componentWidth;

        // Label CellCounter
        cellCounter = new PLabel(xOffset, yOffset, componentWidth, componentHeight, "");
        xOffset += componentWidth;

        // Label für FPS und Generation
        info = new PLabel(xOffset, yOffset, componentWidth, componentHeight, "");
        xOffset = 0;
        yOffset += componentHeight;

        // Previous Gen Button
        buttonPrevious = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Previous Generation") {
            @Override
            public void buttonEvent() {
                // Setzt eine vorherige Generation als aktuelle
                // Die Anzahl Generation, welche zurückgesprungen wird, wird aus der Textbox ausgelesen
                setPreviousGen();
            }
        };
        buttons.add(buttonPrevious);
        xOffset += componentWidth;

        // Next Gen Button
        buttonNext = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Next Generation") {
            @Override
            public void buttonEvent() {
                // Die Anzahl Generationen wird aus der Textbox ausgelesen
                if (!textbox.getText().equals("")) {
                    // Die nächsten Generationen vorbereiten
                    for (int i = 0; i < Integer.parseInt(textbox.getText()); i++) {
                        prepareNextGen();
                    }
                }
            }
        };
        buttons.add(buttonNext);
        xOffset += componentWidth;

        // Textbox; Dient der Eingabe einer Zahl; die dort eingegebene Zahl wird zum springen zwischen Generationen genutzt
        // Beispiel: Zahl 5 -> 5 Generationen nach vorne oder hinten springen
        textbox = new PTextbox(xOffset, yOffset, 50, componentHeight, "1");
        xOffset += 50;
    }

    /**
     * Diese Methode deaktiviert Buttons.
     */
    private void disableButtons() {
        buttonClear.setEnabled(false);
        buttonRandom.setEnabled(false);
        buttonPrevious.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonSave.setEnabled(false);
    }

    /**
     * Diese Methode aktiviert Buttons.
     */
    private void enableButtons() {
        buttonClear.setEnabled(true);
        buttonRandom.setEnabled(true);
        buttonPrevious.setEnabled(true);
        buttonNext.setEnabled(true);
        buttonSave.setEnabled(true);
    }

    /**
     * @return Die Anzahl lebendes Zellen
     */
    private int countLivingCells() {
        int livingCells = 0;

        // Loop durch alle Zellen
        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                if (cell.isAlive()) {
                    // Wenn die Zelle lebt, den Counter erhöhen
                    livingCells++;
                }
            }
        }

        return livingCells;
    }

    /**
     * Dies ist die Main Methode und somit der Einstieg in das Programm.
     * Hier wird eine neue Instanz dieser Klasse erzeugt und mit der Methode
     * PApplet.runSketch() ausgeführt.
     */
    public static void main(String[] args) {
        String[] processingArgs = {"Game of Life"};
        Main main = new Main();
        PApplet.runSketch(processingArgs, main);
    }
}
