package ScrabbleGame.Project3.view;


import ScrabbleGame.Project3.controller.MainGameLoop;
import ScrabbleGame.Project3.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class Display extends BorderPane{

    private Board board;
    private Human humanPlayer;
    private GridPane gridPane;

    // pane for player
    private HBox playerPane;

    private TextArea txtMessage = new TextArea();
    private TextArea txtStatus = new TextArea();

    //place OK
    private Button btnOK;

    //Undo button
    private Button btnUndo;

    //Game reset button
//    private Button btnResetGame;

    public Display(Board board, Human humanPlayer){

        this.board = board;
        this.humanPlayer = humanPlayer;

        VBox centerBox = new VBox();

        //create tiles pane
        gridPane = new GridPane();

        //set gap
        gridPane.setHgap(2);
        gridPane.setVgap(2);

        //add tiles to board
        for (int i = 0; i < board.getSize(); i++){
            for (int j = 0; j < board.getSize(); j++){
                gridPane.add(board.getSquare(i, j), j, i, 1, 1);
            }
        }

        gridPane.setPadding(new Insets(10, 10, 10, 10));

        playerPane = new HBox();
        playerPane.setSpacing(2);
        playerPane.setPadding(new Insets(10, 10, 10, 10));

        for (int i = 0; i < humanPlayer.getTileCanvasList().size(); i++){ //7 tiles of human player
            playerPane.getChildren().add(humanPlayer.getTileCanvasList().get(i));
        }

        btnOK = new Button("OK");
        btnOK.setPrefWidth(200);
        btnUndo = new Button("Undo");
        btnUndo.setPrefWidth(200);

        centerBox.getChildren().add(gridPane);
        centerBox.getChildren().add(playerPane);

        //button box
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(btnOK);
        buttonBox.getChildren().add(btnUndo);

        buttonBox.setSpacing(2);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));

        centerBox.getChildren().add(buttonBox);
        centerBox.getChildren().add(new Label("Click on the letter then click on the board to place that letter. Then click OK button"));

        //put at center
        setCenter(centerBox);

        //right box to display messages
        VBox rightBox = new VBox();

        rightBox.setSpacing(10);
        rightBox.setPadding(new Insets(10, 10, 10, 10));

        rightBox.getChildren().add(new Label("Game Information:"));
        rightBox.getChildren().add(txtMessage);

        rightBox.getChildren().add(new Label("Game Status:"));
        rightBox.getChildren().add(txtStatus);

//        btnResetGame = new Button("Reset Game");
//        rightBox.getChildren().add(btnResetGame);

        //put at right
        setRight(rightBox);

        txtMessage.setEditable(false);
        txtStatus.setEditable(false);


    }

    //set controller for buttons
    public void setController(MainGameLoop controller){
        //handle undo event
        btnUndo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                controller.undo();
            }
        });

        //handle OK
        btnOK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                controller.humanPlay();
            }
        });
    }


    public Board getBoard(){
        return board;
    }


    public Human getHumanPlayer() {
        return humanPlayer;
    }

    //show message
    public void showMessage(String message) {
        txtMessage.appendText(message + System.lineSeparator());

        txtMessage.selectPositionCaret(txtMessage.getLength());
        txtMessage.deselect(); //removes the highlighting
    }

    //show status
    public void showStatus(String status) {
        txtStatus.setText(status);
    }
}

