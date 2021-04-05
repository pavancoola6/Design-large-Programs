package ScrabbleGame.Project3.controller;

import ScrabbleGame.Project3.model.Location;
import ScrabbleGame.Project3.model.Square;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;


public class SquareInputState implements EventHandler<MouseEvent> {


    private MainGameLoop controller;
    private Square square;

    public SquareInputState(MainGameLoop controller, Square square){
        this.controller = controller;
        this.square = square;
    }

    @Override
    public void handle(MouseEvent event) {
        controller.doClick(square);
    }
}

