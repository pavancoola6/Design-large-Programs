package domino;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;


public class Display extends BorderPane{


    private TextArea gameInfoText = new TextArea();

    private GUIHumanPlayer guiHumanPlayer;

    //constructor
    public  Display(){

    }

    public void initialize(GUIHumanPlayer guiHumanPlayer){

        this.guiHumanPlayer = guiHumanPlayer;
        guiHumanPlayer.createDislay();

        //put at bottom
        setBottom(guiHumanPlayer.getBox());

        gameInfoText.setEditable(false);
        gameInfoText.setText("Game Information\n");

        gameInfoText.setMinWidth(400);
        setCenter(gameInfoText);
    }

    public void addInformation(String info) {
        gameInfoText.appendText(info + '\n');
    }
}
