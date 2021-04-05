package ScrabbleGame.Project3.model;


import ScrabbleGame.Project3.WordSolverAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Computer extends Player {

    //constructor
    public Computer() {
        super("Computer");
    }

    //locations of letters
    private ArrayList<Location> positionSolution;

    //letter solution
    private String letterSolution;

    //computer plays
    public void play(HashMap<String, String> dictionary, List<Tile> tiles, Board board,
                     HashMap<Character, Integer> scores){

        WordSolverAlgorithm solver = new WordSolverAlgorithm(dictionary, tiles, board, scores);

        //tray
        String tray = "";
        for (Tile tile: getTiles()){
            tray += tile.getLetter();
        }
        solver.solve(tray);

        //print result
//		System.out.println("Tray: " + tray);
//		System.out.println("Solution has " + solver.getScore() + " points");
//		System.out.println("Solution Board:");
//		System.out.println(board);

        positionSolution = solver.getPositionSolution();
        letterSolution = solver.getLetterSolution();

        setScore(getScore() + solver.getScore());
    }

    public ArrayList<Location> getPositionSolution() {
        return positionSolution;
    }

    //get tile index
    public int getTileIndex(char letter){
        for (int i = 0; i < getTiles().size(); i++){
            if (tiles.get(i).getLetter() == letter){
                return i;
            }
        }
        return -1;
    }

    public String getLetterSolution() {
        return letterSolution;
    }
}
