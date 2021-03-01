package domino;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class DominoDisplay  extends Canvas {


    private Domino domino;


    private InputState inputState;


    public DominoDisplay(Domino domino){

        setWidth(60);
        setHeight(120);

        this.domino = domino;
    }



    public void draw() {

        // graphics context of the canvas
        GraphicsContext gc = getGraphicsContext2D();

        //clear
        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.strokeText(domino.toString(), 10, 50);


    }


    public void setController(MainGameLoop controller) {

        inputState = new InputState(controller, this);

        setOnMouseClicked(event -> {

            inputState.handle(event);

        });
    }

    public Domino getDomino() {
        return domino;
    }
}
