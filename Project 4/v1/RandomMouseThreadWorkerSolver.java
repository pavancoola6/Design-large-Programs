/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RandomMouseThreadWorkerSolver implements MazeSolver{
	private Cell end;
	private Cell current;
	private String direction;
	
	private GraphicsContext gc;
	private Stage stage;
	private int cellSize;
	private int winSize;
	
	public RandomMouseThreadWorkerSolver(String direction) {
		this.direction = direction;
	}
	
	public void solveMaze(Board board, Cell start, Cell end, int delay, GraphicsContext gc, Stage stage, int cellSize, int winSize, int rendSize, boolean rendSelf) {
		this.gc = gc;
		this.stage = stage;
		this.cellSize = cellSize;
		this.winSize = winSize;
		this.end = end;
		current = start;
		direction = board.getRandPossDirection(current);
		cellsToRender.add(current);
		
		
		solveLoop(board, delay, gc, stage, cellSize, winSize, rendSize, rendSelf);
	}

	@Override
	public boolean generateStep(Board board) {
		if (current == end)
			return false;
		if (board.pathOpen(current, board.getTurnedDirection(direction, 2)) && board.getPossDirections(current).size() == 2) {
			String newDir = board.getRandPossDirection(current);
			while(newDir.equals(board.getTurnedDirection(direction, 2))) {
				newDir = board.getRandPossDirection(current);
			}
			direction = newDir;
			current = board.getRelative(current, direction);
		}
		else if(board.getPossDirections(current).size() == 1) {
			direction = board.getRandPossDirection(current);
			current = board.getRelative(current, direction);
		}
		else {
			for(String newDirection : board.getPossDirections(current)) {
				direction = newDirection;
				if(!newDirection.equals(board.getTurnedDirection(direction, 2))) {
					current = board.getRelative(current, newDirection);
					Thread startThread = 
					new Thread() {
			            public void run() {
			            	MazeSolver mouseWorkerSolver = new RandomMouseThreadWorkerSolver(direction);
			            	mouseWorkerSolver.solveMaze(board, current, end, 100, gc, stage, cellSize, winSize, cellSize / 2, false);
			            }
			        };        
			        startThread.start();
				}
			}
	        return false;
		}
		//Still solving? Yes
		return true;
	}

	public void render(GraphicsContext gc, int cellSize, int winSize, int rendSize, Color cellCol, Color wallCol) {
		current.render(gc, cellSize, winSize, rendSize, cellCol, wallCol);	
	}

}
