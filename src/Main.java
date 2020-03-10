import processing.core.PApplet;

import java.util.Random;

public class Main extends PApplet {

    public static final int windowWidth = 900;
    public static final int windowHeight = 900;
    public static final int cellSize = 20;
    public static final int firstGenProbability = 60;

    private Cell[][] currentGen;
    private Cell[][] nextGen;

    @Override
    public void settings() {
        size(windowWidth, windowHeight);
    }

    @Override
    public void setup() {
        frameRate(30);
        if (cellSize == 1) {
            noStroke();
        }
        prepareFirstGen();
        drawCells();
        noLoop();
    }

    @Override
    public void draw() {
        if (looping){
            prepareNextGen();
        }
        drawCells();
    }

    @Override
    public void keyPressed() {
        if (key == 'p' || key == 'P') {
            if (looping) {
                noLoop();
            } else {
                loop();
            }
        }
        if (!looping){
            switch (key){
                case ' ':
                    prepareNextGen();
                    redraw();
                    break;

                case 'c':
                case 'C':
                    killAll();
                    redraw();
                    break;

                case 'r':
                case 'R':
                    prepareFirstGen();
                    redraw();
                    break;
            }
        }
    }

    @Override
    public void mousePressed() {
        if (!looping){
            processMouseInput();
        }
    }

    @Override
    public void mouseDragged() {
        if (!looping){
            processMouseInput();
        }
    }

    private void processMouseInput(){
        Cell pressedCell = getPressedCell(mouseX, mouseY);
        if (mouseButton == LEFT){
            pressedCell.revive();
        } else if (mouseButton == RIGHT){
            pressedCell.kill();
        }
        redraw();
    }

    private void prepareFirstGen() {
        Random random = new Random();
        int randomInt;

        currentGen = new Cell[windowHeight / cellSize][windowWidth / cellSize];

        for (int y = 0; y < windowHeight / cellSize; y++) {
            for (int x = 0; x < windowWidth / cellSize; x++) {
                currentGen[y][x] = new Cell(x * cellSize, y * cellSize);
                randomInt = random.nextInt(100);
                if (randomInt < firstGenProbability) {
                    currentGen[y][x].revive();
                }
            }
        }
    }

    private void prepareNextGen() {
        nextGen = getDeepCopy(currentGen);
        for (Cell[] cellRow : nextGen) {
            for (Cell cell : cellRow) {
                cell.prepareNextGen(currentGen);
            }
        }
        currentGen = nextGen;
    }

    private Cell[][] getDeepCopy(Cell[][] currentGen){
        Cell[][] copy = new Cell[windowHeight / cellSize][windowWidth / cellSize];
        for (int i = 0; i < copy.length; i++) {
            for (int j = 0; j < copy[i].length; j++) {
                Cell currentCell = currentGen[i][j];
                copy[i][j] = currentCell.getCopy();
            }
        }
        return copy;
    }

    private void drawCells() {
        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                cell.draw(this);
            }
        }
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

    private Cell getPressedCell(int x, int y){
        return currentGen[y / cellSize][x / cellSize];
    }

    public static void main(String[] args) {
        String[] processingArgs = {"Game of Life"};
        Main main = new Main();
        PApplet.runSketch(processingArgs, main);
    }
}
