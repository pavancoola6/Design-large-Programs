/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
 
public class Driver extends Application {
	public static int winSize;
	public static int cellSize;
	public static MazeGenerator generator;
	public static MazeSolver solver;
	
    public static void main(String[] args) { 
		try {
			File file = new File(args[0]);
			Scanner scanner = new Scanner(file);
			String[] input = scanner.nextLine().split(" ");
			scanner.close();
			winSize = Integer.parseInt(input[0]); 
			cellSize = Integer.parseInt(input[1]);
			generator = MazeGenerator.retrieve(input[2]);
			solver = MazeSolver.retrieve(input[3]);
		} 
		catch (FileNotFoundException e) {
			System.exit(0);
		}    
        launch(args);

    }

    @Override
    public void start(Stage stage) {
    	stage.setTitle("Maze Generation and Traversal");
        Canvas canvas = new Canvas(winSize, winSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Group group = new Group(canvas);
        Scene scene = new Scene(group, winSize, winSize);
        stage.setScene(scene);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, winSize, winSize);
        stage.show();
         
        // separate non-FX thread
        new Thread() {
            public void run() {
                
            	//Create board and set up randomized start end values
            	Board board = new Board(winSize, cellSize);
            	Pair<Cell, Cell> startEnd = board.getStartEnd();
            	Cell start = startEnd.getKey();
            	Cell end = startEnd.getValue();
            	board.setStartEnd(start, end);     
            	
            	//Generate and animate changes to the board
            	generator.generateMaze(board, 10, gc, stage, cellSize / 2);
            	
            	//Solve and animate changes to the board
				solver.solveMaze(board, start, end, 100, gc, stage, cellSize, winSize, cellSize / 2, true);
				


            }
        }.start();  
        
    }
}