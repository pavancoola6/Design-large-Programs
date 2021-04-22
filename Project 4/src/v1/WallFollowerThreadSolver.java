/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WallFollowerThreadSolver implements MazeSolver{
	private Cell end;
	private Cell current;
	private String direction;
	
	private GraphicsContext gc;
	private Stage stage;
	private int rendSize;
	private Board board;
	
	public void solveMaze(Board board, Cell start, Cell end, int delay, GraphicsContext gc, Stage stage, int cellSize, int winSize, int rendSize, boolean rendSelf) {
		this.gc = gc;
		this.stage = stage;
		this.rendSize = rendSize;
		this.board = board;
		
		Thread leftThread = 
		new Thread() {
            public void run() {
            	MazeSolver leftFollower = new WallFollowerSolver(true);
            	leftFollower.solveMaze(board, start, end, 100, gc, stage, cellSize, winSize, cellSize / 2, false);
            }
        };
        
        Thread rightThread = 
        new Thread() {
        	public void run() {
        		MazeSolver rightFollower = new WallFollowerSolver(false);
        		Cell newStart;
        		
        		if(board.getStartEnd().getKey().getX() == 0 || board.getStartEnd().getKey().getX() == (winSize / cellSize) - 1) {
        			newStart = board.getCellOnWall("top");
            	}
        		else {
        			newStart = board.getCellOnWall("left");
        		}
        		rightFollower.solveMaze(board, newStart, end, 100, gc, stage, cellSize, winSize, cellSize / 2, false);
            }
        };
        
        leftThread.start(); 
        rightThread.start();
        
        while(true) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {}
			//generateStep(board);
		 	Platform.runLater(new Runnable() {
		        public void run() {
		        	gc.setFill(Color.BLACK);
		        	gc.fillRect(0, 0, winSize, winSize);
		        	board.render(gc, stage, rendSize);
		        }
		    });	
		}

	}

	@Override
	public boolean generateStep(Board board) {
		//board.render(gc, stage, rendSize);
		return true;
	}
	
	@Override
	public void render(GraphicsContext gc, int cellSize, int winSize, int rendSize, Color cellCol, Color wallCol) {
		for (Cell cell : cellsToRender) {
			cell.render(gc, cellSize, winSize, rendSize, cellCol, wallCol);
		}
		
	}

}
