package ScrabbleGame.Project3.controller;

import ScrabbleGame.Project3.model.Square;
import ScrabbleGame.Project3.view.TileCanvas;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;


public class HumanTileInputState implements EventHandler<MouseEvent> {


    private MainGameLoop controller;
    private TileCanvas tileCanvas;
    public HumanTileInputState(MainGameLoop controller, TileCanvas tileCanvas){
        this.controller = controller;
        this.tileCanvas = tileCanvas;
    }

    @Override
    public void handle(MouseEvent event) {
        controller.doClick(tileCanvas);
    }
}
