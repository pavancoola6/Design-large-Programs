package ScrabbleGame.Project3.controller;

import ScrabbleGame.Project3.model.*;
import ScrabbleGame.Project3.view.Display;
import ScrabbleGame.Project3.view.TileCanvas;
import javafx.scene.control.Alert;

import java.util.*;

public class MainGameLoop {


    private Display display;

    // dictionary
    private HashMap<String, String> dictionary;

    //available tiles
    private List<Tile> tiles;

    //two players
    private Player[] players;

    //current turn
    private int currentTurn;

    //score by character
    private HashMap<Character, Integer> scores = new HashMap<Character, Integer>();


    private HashMap<Integer, Square> turnHashMap = new HashMap<>();

    public MainGameLoop(Display display, HashMap<String, String> dictionary,
                        List<Tile> tiles, Player[] players, HashMap<Character, Integer> scores){

        this.display = display;
        this.dictionary = dictionary;
        this.tiles = tiles;
        this.players = players;
        this.scores = scores;

        display.getBoard().setController(this);
        display.getHumanPlayer().setController(this);

        display.getBoard().draw();
        display.getHumanPlayer().draw();
    }

    public void doClick(Square square) {
        if (square.getTile() != null)
        {//square must be not empty
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scrabble Game");
            alert.setHeaderText("Error");
            alert.setContentText("This location is not empty");
            alert.showAndWait();
        }else if ( display.getHumanPlayer().getChosenTileCanvas() == null
                || display.getHumanPlayer().getChosenTileCanvas().getTile() == null)
        {//player must choose the tile

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scrabble Game");
            alert.setHeaderText("Error");
            alert.setContentText("Please choose the tile to place");
            alert.showAndWait();

        }else{

            //put into map
            turnHashMap.put(display.getHumanPlayer().getChosenPosition(), square);

            square.setTile(display.getHumanPlayer().getChosenTileCanvas().getTile());
            square.draw();

            display.getHumanPlayer().getChosenTileCanvas().setTile(null);
            display.getHumanPlayer().getChosenTileCanvas().setHighlight(false);
            display.getHumanPlayer().setChosenTileCanvas(null);
        }
    }

    //undo play
    public void undo() {

        // Using for-each loop
        for (Map.Entry<Integer, Square> mapElement : turnHashMap.entrySet()) {

            int position = mapElement.getKey();
            Square square = mapElement.getValue();

            display.getHumanPlayer().getTileCanvasList().get(position).setTile(square.getTile());
            square.setTile(null);

        }

        display.getHumanPlayer().clearHighlight(); //clear other highlight

        //redraw
        display.getBoard().draw();
        display.getHumanPlayer().draw();

        turnHashMap.clear(); //clear hash map
    }


    public void doClick(TileCanvas tileCanvas) {

        display.getHumanPlayer().clearHighlight(); //clear other highlight

        //highlight this canvas
        tileCanvas.setHighlight(true);

        //chosen this tile
        display.getHumanPlayer().setChosenTileCanvas(tileCanvas);
        display.getHumanPlayer().setChosenPosition(tileCanvas.getPosition());
    }


    public void humanPlay() {

        //validate
        if (turnHashMap.size() == 0){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scrabble Game");
            alert.setHeaderText("Error");
            alert.setContentText("Please choose the tile to place");
            alert.showAndWait();

            return;
        }

        Human human = (Human)players[0];

        //invalid place
        if (!human.isValidPlaces(display.getBoard(), turnHashMap)){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scrabble Game");
            alert.setHeaderText("Error");
            alert.setContentText("Invalid places. Please click undo button and place letters again");
            alert.showAndWait();

            return;

        }

        //validate
        String message = "Player played: ";

        int playerScore = 0;

        // Using for-each loop
        for (Map.Entry<Integer, Square> mapElement : turnHashMap.entrySet()) {

            int position = mapElement.getKey();
            Square square = mapElement.getValue();

            message += square.getTile().getLetter() + " ";

            playerScore += scores.get(square.getTile().getLetter()) * display.getBoard().getScore(square.getRow(), square.getColumn());
        }

        display.showMessage(message);

        display.getHumanPlayer().clearHighlight(); //clear other highlight

        //deliver tiles for player

        for (int i = 0; i < human.getTileCanvasList().size() && tiles.size() > 0; i++){

            if (human.getTileCanvasList().get(i).getTile() == null){
                human.getTileCanvasList().get(i).setTile(tiles.remove(0));
            }
        }

        human.setScore(human.getScore() + playerScore);

        //redraw
        display.getBoard().draw();
        display.getHumanPlayer().draw();

        turnHashMap.clear(); //clear hash map

        display.showMessage("Computer's turn");
        display.showMessage("Computer is thinking....");

        //show scores
        display.showStatus("Human's score: " + human.getScore() + System.lineSeparator() +
                "Computer's score: " + players[1].getScore() + System.lineSeparator() );
        //swap turn
        currentTurn = (currentTurn + 1) % 2;

        Computer computer = (Computer)players[1];
        computer.play(dictionary, tiles, display.getBoard(), scores);

        if (computer.getLetterSolution().equals("")){ //cannot place, replace

            int idx = (int)(Math.random() * 7);

            //change tile
            if (tiles.size() > 0) {
                Tile tile = computer.getTiles().get(idx);
                computer.getTiles().set(idx, tiles.remove(0));
                tiles.add(tile);

                display.showMessage("Compute replaces " + tile.getLetter() + " by " +
                        computer.getTiles().get(idx).getLetter());
            }

        }else {

            //locations of letters
            ArrayList<Location> positionSolution = computer.getPositionSolution();
            String letterSolution = computer.getLetterSolution();

            ArrayList<Square> squares = new ArrayList<>();
            for (Location loc : positionSolution) {
                squares.add(display.getBoard().getSquare(loc.x, loc.y));
            }

            //print result
            message = "Computer played: ";

            //place on board
            for (int i = 0; i < letterSolution.length(); i++) {

                int index = computer.getTileIndex(letterSolution.charAt(i));

                message += computer.getTiles().get(index).getLetter();

                squares.get(i).setTile(computer.getTiles().get(index));

                //deliver tile
                if (tiles.size() > 0) {
                    computer.getTiles().set(index, tiles.remove(0));
                }
            }

            display.showMessage(message);

        }

        //show scores
        display.showStatus("Human's score: " + human.getScore() + System.lineSeparator() +
                "Computer's score: " + players[1].getScore() + System.lineSeparator() );

        //redraw
        display.getBoard().draw();
        display.getHumanPlayer().draw();
    }
}
