/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.Random;
import java.util.Stack;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DepthFirstGenerator implements MazeGenerator{
	
	private Stack<Cell> stack;
	
	@Override
	public void generateMaze(Board board, int delay, GraphicsContext gc, Stage stage, int rendSize) {
		stack = new Stack<Cell>();
		stack.push(board.getRandCell());
		
		generateLoop(board, delay, gc, stage, rendSize);
		
	}

	@Override
	public boolean generateStep(Board board) {
		if (stack.size() == 0)
			return false;
		Cell currentCell = stack.pop();
		if (board.getUnvisNeighbors(currentCell).size() > 0) {
			stack.push(currentCell);
			Cell newCell = board.getRandUnvisNeighbor(currentCell);
			currentCell.removeWall(newCell);
			newCell.setVisited(true);
			stack.push(newCell);
		}
		//Still generating? Yes
		return true;
		
	}



	
}
