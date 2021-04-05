package ScrabbleGame.Project3.view;

import ScrabbleGame.Project3.controller.HumanTileInputState;
import ScrabbleGame.Project3.controller.MainGameLoop;
import ScrabbleGame.Project3.model.Tile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TileCanvas extends Canvas {

    private Tile tile;
    private HumanTileInputState inputState;

    //has highlight
    private boolean highlight;

    //position
    private int position;

    public TileCanvas(Tile tile, int position) {

        setWidth(80);
        setHeight(80);

        this.tile = tile;
        this.position = position;
    }


    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setController(MainGameLoop controller) {

        inputState = new HumanTileInputState(controller, this);

        setOnMouseClicked(event -> {

            inputState.handle(event);

        });

    }

    //getter of highlight
    public boolean isHighlight() {
        return highlight;
    }

    //setter of highlight
    public void setHighlight(boolean highlight) {
        this.highlight = highlight;

        //repaint
        draw();
    }


    public void draw() {

        // graphics context of the canvas
        GraphicsContext gc = getGraphicsContext2D();


        //clear
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (highlight) {

            gc.setLineWidth(1);
            gc.setStroke(Color.RED);

        }else{
            gc.setLineWidth(0.3);
            gc.setStroke(Color.BLACK);
        }

        gc.strokeRect(1, 1, getWidth() - 2, getHeight() - 2);

        if (tile != null) {

            gc.setLineWidth(1);
            gc.setStroke(Color.BLUE);
            gc.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 25));
            gc.strokeText("" + tile.getLetter(), 20, 30);

            gc.setLineWidth(0.5);
            gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
            gc.strokeText("" + tile.getScore(), 5, 70);
        }
    }

    public int getPosition() {
        return position;
    }
}
