/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.Random;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PledgeSolver implements MazeSolver{
	private String direction;
	private String initialDirection;
	private Random rand;
	private Cell end;
	private Cell current;
	private int sumOfTurns;
	private boolean wallFollowing;
	
	public void solveMaze(Board board, Cell start, Cell end, int delay, GraphicsContext gc, Stage stage, int cellSize, int winSize, int rendSize, boolean rendSelf) {
		current = start;
		this.end = end;
		wallFollowing = false;
		sumOfTurns = 0;
		rand = new Random();
		//Pick random direction
		direction = board.getAllDirections().get(rand.nextInt(4));
		initialDirection = direction;
		
		solveLoop(board, delay, gc, stage, cellSize, winSize, rendSize, rendSelf);
	}
	

	@Override
	public boolean generateStep(Board board) {
		if (current == end)
			return false;
		
		if (wallFollowing) {
			if (board.pathOpen(current, direction) && board.onlyPathOpen(current, direction)) {
				current = board.getRelative(current, direction);
			}
			else {
				direction = board.getTurnedDirection(direction, -1);
				sumOfTurns += -1;
				for (int i = 0; i < 3; i ++) {
					if (!board.pathOpen(current, direction)) {
						direction = board.getTurnedDirection(direction, 1);
						sumOfTurns += 1;
					}
				}
				current = board.getRelative(current, direction);
			}
			
			if(sumOfTurns == 0 && direction.equals(initialDirection)) {
				wallFollowing = false;
			}
		}
		else {
			if (board.pathOpen(current, direction)) {
				current = board.getRelative(current, direction);
			}
			else {
				wallFollowing = true;
			}
		}
		//Still solving? Yes
		return true;
	}



	@Override
	public void render(GraphicsContext gc, int cellSize, int winSize, int rendSize, Color cellCol, Color wallCol) {
		current.render(gc, cellSize, winSize, rendSize, cellCol, wallCol);
	}


}
