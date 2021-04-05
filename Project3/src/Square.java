package ScrabbleGame.Project3.model;


import ScrabbleGame.Project3.controller.SquareInputState;
import ScrabbleGame.Project3.controller.MainGameLoop;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Square extends Canvas{

    private Tile tile;
    private int score;
    private int row;
    private int column;
    private String display;
    private SquareInputState inputState;
    public Square(String display, int score, int col, int row) {

        setWidth(40);
        setHeight(40);

        this.display = display;
        this.score = score;

        this.row = row;
        this.column = col;
    }


    public Tile getTile() {
        return tile;
    }
    public void setTile(Tile tile) {
        this.tile = tile;
    }
    public int getScore() {
        if (score == 0){
            return 1;
        }
        return score;
    }


    public String getDisplay() {
        return display;
    }
    public void setController(MainGameLoop controller) {

        inputState = new SquareInputState(controller, this);

        setOnMouseClicked(event -> {

            inputState.handle(event);

        });

    }


    public void draw() {

        // graphics context of the canvas
        GraphicsContext gc = getGraphicsContext2D();
        gc.setLineWidth(0.3);
        gc.setStroke(Color.GREY);
        //clear
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.strokeRect(1, 1, getWidth() - 2, getHeight() - 2);

        gc.setLineWidth(1);
        if (score == 2) {
            gc.setStroke(Color.LIGHTBLUE);
            gc.strokeText("" + score, 3, 35);
        }else if (score == 3) {
            gc.setStroke(Color.DARKBLUE);
            gc.strokeText("" + score, 3, 35);
        }else if (score > 0) { //NOT HERE
            gc.setStroke(Color.RED);
            gc.strokeText("" + score, 3, 35);
        }

        //draw letter
        if (tile != null){
            gc.setStroke(Color.BLACK);
            gc.strokeText("" + tile.getLetter(), 10, 15);
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}

