package main;

import components.*;
import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.ArrayList;
import java.util.Random;

public class Main extends PApplet {

    public static final int cellSize = 10;
    public static final int componentWidth = 150;
    public static final int componentHeight = 50;

    public static final int windowWidth = 900;
    public static final int windowHeight = 600;
    public static final int sketchWidth = windowWidth;
    public static final int sketchHeight = windowHeight - (2 * componentHeight);

    public static final int firstGenProbability = 60;

    private Cell[][] currentGen;
    private ArrayList<Cell[][]> previousGens;
    private Cell[][] previousGen;

    private ArrayList<PButton> buttons;
    private ArrayList<PButton> buttonsToDisable;

    private PLabel cellCounter;
    private PLabel fps;
    private PSlider slider;
    private PTextbox textbox;

    private boolean running;

    @Override
    public void settings() {
        size(windowWidth, windowHeight);
    }

    @Override
    public void setup() {
        frameRate(30);
        textSize(14);
        if (cellSize == 1) {
            noStroke();
        }
        initComponents();
        prepareFirstGen();
        cellCounter.setText("Cells: " + (sketchHeight / cellSize * sketchWidth / cellSize) + "\nLiving: " + (countLivingCells()));
        running = false;
        previousGens = new ArrayList<>();
    }

    @Override
    public void draw() {
        background(255);
        if (running  && frameCount % 15 == 0){
            prepareNextGen();
        }
        drawWindow();
    }

    @Override
    public void keyPressed() {
        if (key == 'p' || key == 'P') {
            if (running) {
                running = false;
                enableButtons();
            } else {
                running = true;
                disableButtons();
            }
        }
        if (!running){
            switch (key){
                case ' ':
                    prepareNextGen();
                    break;

                case 'c':
                case 'C':
                    killAll();
                    break;

                case 'r':
                case 'R':
                    prepareFirstGen();
                    break;

                case 'z':
                case 'Z':
                    drawPreviousGen();
                    break;
            }
        }
    }

    @Override
    public void mousePressed() {
        if (!running){
            processClickedCell();
            slider.mousePressed(mouseX, mouseY);
        }

        for (PButton button : buttons) {
            button.mousePressed(mouseX, mouseY);
        }

        textbox.keyPressed(mouseX, mouseY);
    }

    @Override
    public void mouseDragged() {
        if (!running){
            processClickedCell();
            slider.mouseDragged(mouseX);
        }
    }

    @Override
    public void mouseReleased() {
        for (PButton button : buttons) {
            button.mouseReleased();
        }
        slider.mouseReleased();
    }

    private void processClickedCell(){
        Cell pressedCell = getPressedCell(mouseX, mouseY);
        if (pressedCell != null){
            if (mouseButton == LEFT){
                pressedCell.revive();
            } else if (mouseButton == RIGHT){
                pressedCell.kill();
            }
        }
    }

    private void prepareFirstGen() {
        Random random = new Random();
        int randomInt;

        currentGen = new Cell[sketchHeight / cellSize][sketchWidth / cellSize];

        for (int y = 0; y < sketchHeight / cellSize; y++) {
            for (int x = 0; x < sketchWidth / cellSize; x++) {
                currentGen[y][x] = new Cell(x * cellSize, y * cellSize);
                randomInt = random.nextInt(100);
                if (randomInt < firstGenProbability) {
                    currentGen[y][x].revive();
                }
            }
        }

        previousGen = getDeepCopy(currentGen);
    }

    private void prepareNextGen() {
        Cell[][] nextGen = getDeepCopy(currentGen);
        previousGen = getDeepCopy(currentGen);
        previousGens.add(previousGen);
        for (Cell[] cellRow : nextGen) {
            for (Cell cell : cellRow) {
                cell.prepareNextGen(currentGen);
            }
        }

        currentGen = nextGen;
    }

    private Cell[][] getDeepCopy(Cell[][] currentGen){
        Cell[][] copy = new Cell[sketchHeight / cellSize][sketchWidth / cellSize];
        for (int i = 0; i < copy.length; i++) {
            for (int j = 0; j < copy[i].length; j++) {
                Cell currentCell = currentGen[i][j];
                copy[i][j] = currentCell.getCopy();
            }
        }
        return copy;
    }

    private void drawWindow() {
        updateLabels();

        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                cell.draw(this);
            }
        }

        for (PButton button : buttons) {
            button.draw(this);
        }

        cellCounter.draw(this);
        fps.draw(this);
        slider.draw(this);
        textbox.draw(this);
    }

    private void updateLabels(){
        float currentFps = Math.round(frameRate * 1000);
        fps.setText("FPS: " + (currentFps / 1000));

        cellCounter.setText("Cells: " + (sketchHeight / cellSize * sketchWidth / cellSize) + "\nLiving: " + (countLivingCells()));
    }

    private void killAll(){
        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                if (cell.isAlive()){
                    cell.kill();
                }
            }
        }
    }

    private void drawPreviousGen(){
        if (previousGens.size() != 0) {
            currentGen = previousGens.get(previousGens.size() - 1);
            previousGens.remove(previousGens.size() - 1);
        }
        //        currentGen = previousGen;
    }

    private Cell getPressedCell(int x, int y){
        try {
            return currentGen[y / cellSize][x / cellSize];
        } catch (IndexOutOfBoundsException ex){
            return null;
        }
    }

    private void initComponents(){
        buttons = new ArrayList<>();
        buttonsToDisable = new ArrayList<>();
        int xOffset = 0;
        int yOffset = sketchHeight;

        PButton button = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Start / Stop") {
            @Override
            public void buttonEvent() {
                if (running) {
                    running = false;
                    enableButtons();
                } else {
                    running = true;
                    disableButtons();
                }
            }
        };
        buttons.add(button);
        xOffset += componentWidth;

        button = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Clear") {
            @Override
            public void buttonEvent() {
                killAll();
                previousGens = new ArrayList<>();
            }
        };
        buttons.add(button);
        buttonsToDisable.add(button);
        xOffset += componentWidth;

        button = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Random Generation") {
            @Override
            public void buttonEvent() {
                prepareFirstGen();
            }
        };
        buttons.add(button);
        buttonsToDisable.add(button);
        xOffset += componentWidth;

        button = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Previous Generation") {
            @Override
            public void buttonEvent() {
                drawPreviousGen();
            }
        };
        buttons.add(button);
        buttonsToDisable.add(button);
        xOffset += componentWidth;

        button = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Next Generation") {
            @Override
            public void buttonEvent() {
                prepareNextGen();
            }
        };
        buttons.add(button);
        buttonsToDisable.add(button);
        xOffset += componentWidth;

        button = new PButton(xOffset, yOffset, componentWidth, componentHeight, "Safe Frame") {
            @Override
            public void buttonEvent() {
                save("/src/images/save_image" + System.currentTimeMillis() + ".png");
            }
        };

        buttons.add(button);
        buttonsToDisable.add(button);
        xOffset = 0;
        yOffset += componentHeight;

        cellCounter = new PLabel(xOffset, yOffset, componentWidth, componentHeight, "Zellen");
        xOffset += componentWidth;

        fps = new PLabel(xOffset, yOffset, componentWidth, componentHeight, "FPS");
        xOffset += componentWidth;

        slider = new PSlider(xOffset, yOffset + componentHeight / 2, 100);
        xOffset += 100;

        textbox = new PTextbox(xOffset, yOffset, 50, componentHeight);
        xOffset += 50;
    }

    private void disableButtons(){
        for (PButton button : buttonsToDisable) {
            button.setEnabled(false);
        }
    }

    private void enableButtons(){
        for (PButton button : buttonsToDisable) {
            button.setEnabled(true);
        }
    }

    private int countLivingCells(){
        int livingCells = 0;

        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                if (cell.isAlive()) {
                    livingCells++;
                }
            }
        }

        return livingCells;
    }

    public static void main(String[] args) {
        String[] processingArgs = {"Game of Life"};
        Main main = new Main();
        PApplet.runSketch(processingArgs, main);
    }
}
