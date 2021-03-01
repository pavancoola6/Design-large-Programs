package domino;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GUIHumanPlayer extends Player{

    private HBox box;

    private List<DominoDisplay> dominoDisplayList = new ArrayList<>();
    public GUIHumanPlayer(){
        super("Human");
    }
    public Domino chooseDomino(Boneyard boneyard, DominoDisplay dominoDisplay){

        Domino domino = dominoDisplay.getDomino();

        //choose value
        int value = readInt("Enter value: [" + domino.getFirst()+ "/" + domino.getSecond() + "] ");
        while (value != domino.getFirst() && value != domino.getSecond()){
            value = readInt("Enter value: [" + domino.getFirst()+ "/" + domino.getSecond() + "] ");
        }

        chosenValue = value;

        //choose row
        int row = readInt("Enter row: [" + boneyard.getFirstRow() + "/" + boneyard.getSecondRow() + "] ");
        while (row !=  boneyard.getFirstRow() && row != boneyard.getSecondRow()){
            row = readInt("Enter row: [" + boneyard.getFirstRow() + "/" + boneyard.getSecondRow() + "] ");
        }

        chosenRow = row;

        return domino;
    }


    public void createDislay(){

        box = new HBox();
        box.setPadding(new Insets(15, 12, 15, 12));
        box.setSpacing(10);

        //set grap
        box.setAlignment(Pos.CENTER);

        //add domino to board
        for (int i = 0; i < numDominoes(); i++){
            DominoDisplay dominoDisplay = new DominoDisplay(dominoes.get(i));
            dominoDisplayList.add(dominoDisplay);
            box.getChildren().add(dominoDisplay);
        }

        draw();
    }


    public void addDominoGUI(Domino domino, MainGameLoop controller){

        dominoes.add(domino);

        DominoDisplay dominoDisplay = new DominoDisplay(domino);
        dominoDisplayList.add(dominoDisplay);

        box.getChildren().add(dominoDisplay);

        dominoDisplay.setController(controller);
    }


    public void draw(){
        for (int i = 0; i < dominoDisplayList.size(); i++){
            dominoDisplayList.get(i).draw();
        }
    }


    public HBox getBox() {
        return box;
    }


    public void setController(MainGameLoop controller){
        for (int i = 0; i < dominoDisplayList.size(); i++){
            dominoDisplayList.get(i).setController(controller);
        }
    }


    public List<DominoDisplay> getDominoDisplayList() {
        return dominoDisplayList;
    }


    public void removeDomino(DominoDisplay dominoDisplay){
        dominoes.remove(dominoDisplay.getDomino());
        box.getChildren().remove(dominoDisplay);
    }


    public int readInt(String prompt){

        int value = -1;
        //read until user enters valid integer
        do{

            TextInputDialog dialog = new TextInputDialog("");

            dialog.setTitle("Domino game");
            dialog.setHeaderText("Input");
            dialog.setContentText(prompt);

            Optional<String> result = dialog.showAndWait();

            try{
                //convert line to integer
                value = Integer.parseInt(result.get());
                break;
            }catch(Exception e){
                //ignore
            }


        }while(true);

        return value;
    }
}
