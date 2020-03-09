import processing.core.PApplet;

import java.util.Random;

public class Main extends PApplet {

    public static int windowWidth = 800;
    public static int windowHeight = 800;
    public static int cellSize = 20;

    private Cell[][] currentGen;
    private Cell[][] nextGen;

    @Override
    public void settings() {
        size(windowWidth, windowHeight);
    }

    @Override
    public void setup() {
        frameRate(30);
        if (cellSize == 1){
            noStroke();
        }
        prepareFirstGen();
        drawCells();
    }

    @Override
    public void draw() {
        prepareNextGen();
        drawCells();
    }

    @Override
    public void keyPressed() {
        if (key == ' '){
            prepareNextGen();
            drawCells();
            redraw();
        } else if(key == 'p' || key == 'P'){
            if (looping){
                noLoop();
            } else{
                loop();
            }
        }
    }

    private void prepareFirstGen(){
        Random random = new Random();
        int randomInt;

        currentGen = new Cell[windowHeight / cellSize][windowWidth / cellSize];

        for(int y = 0; y < windowHeight / cellSize; y++){
            for(int x = 0; x < windowWidth / cellSize; x++){
                currentGen[y][x] = new Cell(x * cellSize, y * cellSize);
                randomInt = random.nextInt(5);
                if (randomInt < 2){
                    currentGen[y][x].changeState();
                }
            }
        }
    }

    private void prepareNextGen(){
        nextGen = currentGen.clone();
        for (Cell[] cellRow : nextGen) {
            for (Cell cell : cellRow) {
                cell.prepareNextGen(currentGen);
            }
        }
        currentGen = nextGen;
    }

    private void drawCells(){
        for (Cell[] cellRow : currentGen) {
            for (Cell cell : cellRow) {
                cell.draw(this);
            }
        }
    }

    public static void main(String[] args) {
        String[] processingArgs = {"Game of Life"};
        Main main = new Main();
        PApplet.runSketch(processingArgs, main);
    }
}
