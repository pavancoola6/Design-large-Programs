package ScrabbleGame.Project3.view;


import ScrabbleGame.Project3.controller.MainGameLoop;
import ScrabbleGame.Project3.model.*;
import ScrabbleGame.Project3.utility.GameLoader;
import ScrabbleGame.Project3.utility.Permutate;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        // loader
        GameLoader loader = new GameLoader();

        // dictionary
        HashMap<String, String>  dictionary = loader.loadDictionary("sowpods.txt");

        //load default board
        Square[][] squares = loader.loadSquares(new Scanner(new File("scrabble_board.txt")));

        //available tiles
        List<Tile> tiles = loader.loadTiles("scrabble_tiles.txt");

        // board
        Board board = new Board(squares);

        //two players
        Player[] players = new Player[2];
        players[0] = new Human();
        players[1] = new Computer();


        //shuffle tiles
        Collections.shuffle(tiles);

        //score by character
        HashMap<Character, Integer> scores = new HashMap<Character, Integer>();
        //put as the score in hash
        for (Tile t: tiles) {
            scores.put(Character.toLowerCase(t.getLetter()), t.getScore());
            scores.put(Character.toUpperCase(t.getLetter()), t.getScore());
        }

        //deliver card
        for (Player player: players){

            List<Tile> playerTiles = new ArrayList<>();
            for (int i = 0; i < 7; i++){
                playerTiles.add(tiles.remove(0));
            }

            //deliver
            player.deliverTiles(playerTiles);
        }

        //create display
        Display display = new Display(board, (Human)players[0]);
        MainGameLoop controller = new MainGameLoop(display, dictionary, tiles, players, scores);
        display.setController(controller);

        primaryStage.setTitle("Project 3 - Scrabble Game");
        primaryStage.setScene(new Scene(display, 1150, 850));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
