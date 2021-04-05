package ScrabbleGame.Project3.utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.util.Scanner;
import java.util.List;
import java.io.FileNotFoundException;
import ScrabbleGame.Project3.model.Square;
import ScrabbleGame.Project3.model.Tile;

public class GameLoader {
    public HashMap<String, String> loadDictionary(String filename) {

        // list of words from file
        HashMap<String, String> words = new HashMap<>();

        try {
            Scanner sc = new Scanner(new File(filename));

            while (sc.hasNextLine()) {
                String word = sc.nextLine();
                words.put(word, word);
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return words;
    }

    // load squares
    public Square[][] loadSquares(Scanner sc) {

        // squares of board
        // Square[][] squares = null;
        int rows = 0;
        int cols = 0;

        Square[][] squares = null;

        rows = cols = sc.nextInt();

        //create matrix
        squares = new Square[rows][cols];

        //for each row
        for (int r = 0; r < rows; r++) {

            //for each column
            for (int c = 0; c < cols; c++) {

                String data = sc.next(); // ..  or 3.  or .3

                if (data.length() == 1) { //a character

                    squares[r][c] = new Square(data, 0, c, r);
                    squares[r][c].setTile(new Tile(data.charAt(0), 0));

                }else if (data.equals("..")) {
                    squares[r][c] = new Square(data, 0, c, r);
                }else if (data.startsWith(".")) { // .3
                    squares[r][c] = new Square(data, Integer.parseInt(data.substring(1)), c, r);
                }else { // 3.
                    squares[r][c] = new Square(data, Integer.parseInt(data.substring(0, 1)), c, r);
                }
            }
        }


        return squares;
    }

    // load frequencies
    public List<Tile> loadTiles(String filename) {

        // list of tiles
        List<Tile> tiles = new ArrayList<Tile>();

        try {
            Scanner sc = new Scanner(new File(filename));

            while (sc.hasNext()) {

                //read letter, score and times
                char letter = sc.next().charAt(0);
                int score = sc.nextInt();
                int times = sc.nextInt();

                for (int t = 0; t < times; t++) {
                    tiles.add(new Tile(letter, score));
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return tiles;
    }

//	// testing only
//	public static void main(String[] args) {
//
//		GameLoader loader = new GameLoader();
//
//		Set<String> words = loader.loadDictionary("animals.txt");
//
//		List<Tile> tiles = loader.loadTiles("scrabble_tiles.txt");
//
//		System.out.println(words);
//		System.out.println(tiles);
//	}
}
