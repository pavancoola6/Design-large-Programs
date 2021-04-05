package ScrabbleGame;

import ScrabbleGame.Project3.WordSolverAlgorithm;

public class WordSolver {
    // testing only
    public static void main(String[] args) throws Exception {

        // word solver
        if (args.length != 1) {
            System.out.println("Please provide name of the dictionary file to load");
        }
        else {

            WordSolverAlgorithm game = new WordSolverAlgorithm(args[0]);

            // solve
            game.solve();

        }
    }
}
