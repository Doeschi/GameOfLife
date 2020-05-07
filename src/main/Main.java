package main;

import components.*;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main Klasse des Programms. Diese Klasse enthält die Logik des GoL.
 * Die Klasse erbt von PApplet, einer Klasse des Processing Frameworks.
 * Diese Klasse erstellt ein Fenster, welches einmal pro Frame neu gezeichnet wird (in der draw() Methode).
 * Die Funktionen zum Zeichnen bietet auch die PApplet Klasse an.
 */
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

    // Prozentsatz, wie viele Zellen zu Beginn leben sollen
    public static final int firstGenProbability = 60;

    private Cell[][] currentGen;
    // Arrayliste mit allen vergangen Genertationen
    private ArrayList<Cell[][]> previousGens;

    private PButton buttonStartStop;
    private PButton buttonClear;
    private PButton buttonRandom;
    private PButton buttonPrevious;
    private PButton buttonNext;
    private PButton buttonSave;

    private PLabel lblCellCounter;
    private PLabel lblInfo;
    private PLabel lblGensPerSecond;
    private PLabel lblGenerationJumps;

    private PTextbox txtGenerations;
    private PTextbox txtGensPerSecond;

    // Boolean, ob das Spiel läuft oder nicht
    private boolean running;
    // Zeitpunkt, wann die letze Generation erstellt wurde
    private int timeOfLastGen;

    /**
     * Diese Methode wird beim Programmstart als erstes aufgerufen.
     * Die Methode legt die Fenstergrösse fest.
     */
    @Override
    public void settings() {
        size(windowWidth, windowHeight);
    }

    /**
     * Diese Methode wird beim Programmstart als zweites aufgerufen.
     * Die Framerate und die Textgrösse wird festgelegt. Danach wird die erste
     * Generation des GoL vorbereitet und alle GUI Komponenten initialisiert und beschriftet.
     */
    @Override
    public void setup() {
        previousGens = new ArrayList<>();
        frameRate(100);
        textSize(14);
        prepareFirstGen();
        initComponents();
        updateUI();
        running = false;
    }

    /**
     * Diese Mehthode wird einmal pro Frame aufgerufen (bei 30 FPS, 30 mal in der Sekunde)
     * Das aktuelle Fenster wird mit background() komplett übermalt. Wenn das GoL läuft, wird alle
     * 0.5 Sekunden eine neue Generation vorbereitet.
     * Danach wird das Komplette GoL Spielfeld, sowie die GUI Komponenten gezeichnet.
     */
    @Override
    public void draw() {
        // Komplettes Fenster Weiss übermalen
        background(255);

        // Wenn das Spiel läuft und mindestens 0.5s seit der letzten Generation vergangen sind,
        // wird eine neue Generation vorbereitet.
        if (running && millis() - timeOfLastGen > 1000 / Integer.parseInt(txtGensPerSecond.getText())) {
            prepareNextGen();
        }
        // Spielfeld und GUI zeichnen
        drawWindow();
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Taste auf der Tastatur gedrückt wird.
     * Ist eines der Textfelder fokusiert, wird die gedrückte Taste verarbeitet.
     */
    @Override
    public void keyPressed() {
        if (txtGenerations.isFocused()) {
            if (keyCode == BACKSPACE) {
                txtGenerations.removeChar();
            } else {
                txtGenerations.addChar(key);
            }
        } else if (txtGensPerSecond.isFocused()){
            if (keyCode == BACKSPACE){
                txtGensPerSecond.removeChar();
            } else{
                txtGensPerSecond.addChar(key);
            }
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Maustaste gedrückt wurde.
     * Wenn das Spiel nicht läuft, wird die gedrückte Zelle verarbeitet (falls auf eine Zelle gedrückt wurde)
     * Wird die Linke Maustaste gedrückt, wird dies den Buttons und Textfeldern mitgeteilt.
     */
    @Override
    public void mousePressed() {
        if (!running) {
            processClickedCell();
        }

        if (mouseButton == LEFT) {
            // Teilt den Buttons mit, das die Linke Maus gedrückt wurde
            buttonStartStop.mousePressed(mouseX, mouseY);
            buttonClear.mousePressed(mouseX, mouseY);
            buttonRandom.mousePressed(mouseX, mouseY);
            buttonPrevious.mousePressed(mouseX, mouseY);
            buttonNext.mousePressed(mouseX, mouseY);
            buttonSave.mousePressed(mouseX, mouseY);

            // Teil der Textbox mit, das die Maus gedrückt wurde
            txtGenerations.mousePressed(mouseX, mouseY);
            txtGensPerSecond.mousePressed(mouseX, mouseY);
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Maustaste gedrückt ist und die Maus bewegt wird.
     * Wenn das Spiel nicht läuft, wird die geklickte Zelle verarbeitet. (Falls sich die Maus über einer Zelle befindet)
     */
    @Override
    public void mouseDragged() {
        if (!running) {
            processClickedCell();
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn eine Maustaste losgelassen wird.
     * Allen Buttons wird mitgeteilt, dass die Maus nicht mehr gedrückt wird.
     */
    @Override
    public void mouseReleased() {
        buttonStartStop.mouseReleased();
        buttonClear.mouseReleased();
        buttonRandom.mouseReleased();
        buttonPrevious.mouseReleased();
        buttonNext.mouseReleased();
        buttonSave.mouseReleased();
    }

    /**
     * Diese Methode holt sich die aktuell gedrückte Zelle.
     * Ist diese Null, wurde keine Zelle gedrückt.
     * Ansonsten wird die Zelle belebt, wenn die linke Maustaste gedrückt
     * wird und getötet, wenn die rechte Maustaste gedrückt wird.
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
        timeOfLastGen = millis();
    }

    /**
     * Hier wird die nächste Generation des GoL vorbereitet.
     */
    private void prepareNextGen() {
        // Es wird eine Kopie des Spielfeldes gemacht, damit die neue Generation erstellt werden kann
        Cell[][] nextGen = getDeepCopy();

        // Die aktuelle Generation wird gespeichert, damit bei Bedarf auf diese Generation zurück gegangen werden kann
        previousGens.add(currentGen);

        // Loop durch jede Zelle des Spielfeldes der nächsten Generation und bereitet die Zellen vor
        for (Cell[] cellRow : nextGen) {
            for (Cell cell : cellRow) {
                cell.prepareNextGen(currentGen);
            }
        }

        // Die vorbereitete Generation wird als aktuelle Generation gesetzt
        currentGen = nextGen;
        timeOfLastGen = millis();
    }

    /**
     * Erstellt eine Kopie des Spielfeldes, inklusiver einer Kopie der Zellen.
     * Dies ist nötig, weil der Zustand einer Zelle in der nächsten Generation von
     * den Zuständen seiner Nachbarzellen in der aktuellen Generation abhängt.
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
        updateUI();

        // Loop durch alle Zellen des Spielfeldes
        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                // Zeichnen der Zelle
                cell.draw(this);
            }
        }

        // Zeichnen der Button
        buttonStartStop.draw(this);
        buttonClear.draw(this);
        buttonRandom.draw(this);
        buttonPrevious.draw(this);
        buttonNext.draw(this);
        buttonSave.draw(this);

        // Zeichnen der Labels
        lblCellCounter.draw(this);
        lblInfo.draw(this);
        lblGensPerSecond.draw(this);
        lblGenerationJumps.draw(this);

        txtGenerations.draw(this);
        txtGensPerSecond.draw(this);
    }

    /**
     * Diese Methode updated den Text der Labels und Buttons.
     */
    private void updateUI() {
        // Framerate auf 3 Stellen genau runden
        float currentFps = Math.round(frameRate * 1000);

        // Neue FPS Zahl und Nummer der aktuellen Generation setzten.
        lblInfo.setText("FPS: " + (currentFps / 1000) + "\nGen: " + previousGens.size());

        // sketchHeight/cellSize ergibt die Anzahl Zeilen, sketchWidth/cellSize ergibt die Anzahl Spalten
        // Anzahl aller Zeller und Anzahl lebender Zellen setzen
        lblCellCounter.setText("Cells: " + (sketchHeight / cellSize * sketchWidth / cellSize) + "\nLiving: " + (countLivingCells()));

        // Wenn kein Text in der Textbox steht, wird "0" geschrieben
        // Ansonsten wird die Zahl aus der Textbox verwendet
        if (txtGenerations.getText().equals("")){
            buttonPrevious.setText("Previous 0");
            buttonNext.setText("Skip 0");
        } else{
            buttonPrevious.setText("Previous " + txtGenerations.getText());
            buttonNext.setText("Skip " + txtGenerations.getText());
        }
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
     * Die Nummer der Generation entspricht ihrem Index im Array der vergangen Generationen.
     */
    private void setPreviousGen() {
        // Wenn überhaupt vorherige Generation existieren
        if (previousGens.size() != 0) {
            // Wenn die Anzahl Generation die gesprungen werden sollen, nicht leer ist
            if (!txtGenerations.getText().equals("")) {
                // Wenn mehr Generationen zurück gesprungen werden sollen, als überhaupt existieren, wird die erste Generation angezeigt
                if (Integer.parseInt(txtGenerations.getText()) > previousGens.size()) {
                    // Erste Generation holen
                    currentGen = previousGens.get(0);
                    // Alle Zwischengespeicherten Generationen löschen
                    previousGens.clear();
                } else {
                    // die gewünschte Generation holen
                    currentGen = previousGens.get(previousGens.size() - Integer.parseInt(txtGenerations.getText()));
                    // die zurückgesprungenen Generationen aus dem Zwischenspeicher löschen
                    for (int i = 0; i < Integer.parseInt(txtGenerations.getText()); i++) {
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
        // Start/Stop Button
        buttonStartStop = new PButton(0, 500, componentWidth, componentHeight, "Start / Stop") {
            @Override
            public void buttonEvent() {
                // Wenn das Spiel läuft, wird es angehalten und die deaktivierten Buttons wieder aktiviert
                if (running) {
                    running = false;
                    enableComponents();
                } else {
                    // Wenn Spiel pausiert war, wird es wieder gestartet und die Buttons deaktiviert
                    running = true;
                    disableComponents();

                    // Falls die Textbox für die Anzahl Generationen pro Sekunden
                    // leer ist, wird sie auf 1 gesetzt.
                    if (txtGensPerSecond.getText().equals("")){
                        txtGensPerSecond.setText("1");
                    }
                }
            }
        };

        // Clear Button
        buttonClear = new PButton(150, 500, componentWidth, componentHeight, "Clear") {
            @Override
            public void buttonEvent() {
                // Töten aller Zellen
                killAll();
                // Alle Zwischengespeicherten Generationen löschen (Clear setzt die Generation auf 0)
                previousGens.clear();
            }
        };

        // Random Gen Button
        buttonRandom = new PButton(300, 500, componentWidth, componentHeight, "Random Generation") {
            @Override
            public void buttonEvent() {
                // Neue erste Generation setzten
                prepareFirstGen();
            }
        };

        // Save Button
        buttonSave = new PButton(450, 500, componentWidth, componentHeight, "Safe Frame") {
            @Override
            public void buttonEvent() {
                // Diese Funktion speichert den gesamten Inhalt des aktuellen Programmfensters
                save("/src/images/save_image" + System.currentTimeMillis() + ".png");
            }
        };

        // Label CellCounter
        lblCellCounter = new PLabel(600, 500, componentWidth, componentHeight, "");

        // Label für FPS und Generationnummer
        lblInfo = new PLabel(750, 500, componentWidth, componentHeight, "");

        // Label Gen Jumps
        lblGenerationJumps = new PLabel(0, 550, componentWidth, componentHeight,"Generation Jumps:");

        // Textbox; Dient der Eingabe einer Zahl; die dort eingegebene Zahl wird zum springen zwischen Generationen genutzt
        // Beispiel: Zahl 5 -> 5 Generationen nach vorne oder hinten springen
        txtGenerations = new PTextbox(150, 550, 50, componentHeight, "1");

        // Previous Gen Button
        buttonPrevious = new PButton(300, 550, componentWidth, componentHeight, "") {
            @Override
            public void buttonEvent() {
                // Setzt eine vorherige Generation als aktuelle
                // Die Anzahl Generation, welche zurückgesprungen wird, wird aus der Textbox ausgelesen
                setPreviousGen();
            }
        };

        // Next Gen Button
        buttonNext = new PButton(450, 550, componentWidth, componentHeight, "") {
            @Override
            public void buttonEvent() {
                // Die Anzahl Generationen wird aus der Textbox ausgelesen
                if (!txtGenerations.getText().equals("")) {
                    // Die nächsten Generationen vorbereiten
                    for (int i = 0; i < Integer.parseInt(txtGenerations.getText()); i++) {
                        prepareNextGen();
                    }
                }
            }
        };

        // Label für Generation pro Sekunde
        lblGensPerSecond = new PLabel(600, 550, componentWidth, componentHeight, "Generations per\nsecond:");

        // Textbox zur Eingabe, wie viel Generationen pro Sekunde berechnet werden sollen.
        txtGensPerSecond = new PTextbox(750, 550, 50, componentHeight, "2");
    }

    /**
     * Diese Methode deaktiviert Buttons und Textboxen.
     */
    private void disableComponents() {
        buttonClear.setEnabled(false);
        buttonRandom.setEnabled(false);
        buttonPrevious.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonSave.setEnabled(false);

        txtGensPerSecond.setEnable(false);
        txtGenerations.setEnable(false);
    }

    /**
     * Diese Methode aktiviert Buttons und Textboxen.
     */
    private void enableComponents() {
        buttonClear.setEnabled(true);
        buttonRandom.setEnabled(true);
        buttonPrevious.setEnabled(true);
        buttonNext.setEnabled(true);
        buttonSave.setEnabled(true);

        txtGensPerSecond.setEnable(true);
        txtGenerations.setEnable(true);
    }

    /**
     * @return Die Anzahl lebender Zellen
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
