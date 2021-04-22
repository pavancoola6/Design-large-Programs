/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

public class RandomMouseSolver implements MazeSolver {
	private Cell end;
	private Cell current;
	private String direction;
	
	public void solveMaze(Board board, Cell start, Cell end, int delay, GraphicsContext gc, Stage stage, int cellSize, int winSize, int rendSize, boolean rendSelf) {
		this.end = end;
		current = start;
		direction = board.getRandPossDirection(current);
		
		solveLoop(board, delay, gc, stage, cellSize, winSize, rendSize, rendSelf);
	}

	@Override
	public boolean generateStep(Board board) {
		if (current == end)
			return false;
		if (board.pathOpen(current, board.getTurnedDirection(direction, 2)) && board.getPossDirections(current).size() > 1) {
			String newDir = board.getRandPossDirection(current);
			while(newDir.equals(board.getTurnedDirection(direction, 2))) {
				newDir = board.getRandPossDirection(current);
			}
			direction = newDir;
			current = board.getRelative(current, direction);
		}
		else {
			direction = board.getRandPossDirection(current);
			current = board.getRelative(current, direction);
		}
		//Still solving? Yes
		return true;
	}

	public void render(GraphicsContext gc, int cellSize, int winSize, int rendSize, Color cellCol, Color wallCol) {
		current.render(gc, cellSize, winSize, rendSize, cellCol, wallCol);	
	}
}
