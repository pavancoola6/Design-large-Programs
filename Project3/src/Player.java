package ScrabbleGame.Project3.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Player {


    private int score;
    private String name;
    protected List<Tile> tiles = new ArrayList<>();
    public Player(String name) {
        this.name = name;
    }

    // 7 tiles
    public void deliverTiles(List<Tile> tiles){
        this.tiles = tiles;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }
    public List<Tile> getTiles() {
        return tiles;
    }

    //check if user placed valid letter
    public boolean isValidPlaces(Board board, HashMap<Integer, Square> turnHashMap){


        return true;
    }

}
