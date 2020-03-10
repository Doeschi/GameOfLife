import processing.core.PApplet;

import java.util.concurrent.ConcurrentNavigableMap;

public class Cell {
    private int x;
    private int y;
    private boolean alive;

    public Cell(int x, int y){
        this.x = x;
        this.y = y;
        alive = false;
    }

    public Cell(int x, int y, boolean alive){
        this.x = x;
        this.y = y;
        this.alive = alive;
    }

    public void draw(PApplet pApplet){
        if (alive){
            pApplet.fill(0);
        } else{
            pApplet.fill(255);
        }
        pApplet.square(x, y, Main.cellSize);
    }

    public void prepareNextGen(Cell[][] cells){
        int xPos = x / Main.cellSize;
        int yPos = y / Main.cellSize;

        int neighbors = 0;

        for(int y = yPos - 1; y < yPos + 2; y++){
            for(int x = xPos - 1; x < xPos + 2; x++){
                if (x == xPos && y == yPos){
                    continue;
                }
                try{
                    if (cells[y][x].isAlive()){
                        neighbors++;
                    }
                } catch (IndexOutOfBoundsException ex){
                    // Do Nothing
                }
            }
        }

        if (!alive && neighbors == 3){
            alive = true;
        }
        else if(alive){
            if (neighbors < 2){
                alive = false;
            }
            else if(neighbors > 3){
                alive = false;
            }
        }
    }

    public void revive(){
        alive = true;
    }

    public void kill(){
        alive = false;
    }

    public boolean isAlive(){
        return alive;
    }

    public Cell getCopy() {
        return new Cell(x, y, alive);
    }
}
