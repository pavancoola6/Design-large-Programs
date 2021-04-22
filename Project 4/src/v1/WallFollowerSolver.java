/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WallFollowerSolver implements MazeSolver{
	private Cell end;
	private Cell current;
	private String direction;
	private boolean leftWall;
	
	public WallFollowerSolver(boolean leftWall) {
		this.leftWall = leftWall;
	}
	
	public void solveMaze(Board board, Cell start, Cell end, int delay, GraphicsContext gc, Stage stage, int cellSize, int winSize, int rendSize, boolean rendSelf) {
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
		
		if (board.pathOpen(current, direction) && board.onlyPathOpen(current, direction)) {
			current = board.getRelative(current, direction);
		}
		else {
			if(leftWall) {
				direction = board.getTurnedDirection(direction, -1);
				for (int i = 0; i < 3; i ++) {
					if (!board.pathOpen(current, direction)) {
						direction = board.getTurnedDirection(direction, 1);
					}
				}
			}
			else {
				direction = board.getTurnedDirection(direction, 1);
				for (int i = 0; i < 3; i ++) {
					if (!board.pathOpen(current, direction)) {
						direction = board.getTurnedDirection(direction, -1);
					}
				}
			}
			current = board.getRelative(current, direction);
			
		}
		//Still solving? Yes
		return true;
	}
	
	@Override
	public void render(GraphicsContext gc, int cellSize, int winSize, int rendSize, Color cellCol, Color wallCol) {
		current.render(gc, cellSize, winSize, rendSize, cellCol, wallCol);	
	}
}
