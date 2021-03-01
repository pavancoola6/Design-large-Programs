package domino;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;


public class InputState implements EventHandler<MouseEvent> {


    private MainGameLoop controller;
    private DominoDisplay dominoDisplay;


    public InputState(MainGameLoop controller, DominoDisplay dominoDisplay){
        this.controller = controller;
        this.dominoDisplay = dominoDisplay;
    }


    @Override
    public void handle(MouseEvent event) {
        controller.doClick(dominoDisplay);
    }
}
