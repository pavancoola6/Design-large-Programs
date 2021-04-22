/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.util.Pair;

public class PrimGenerator implements MazeGenerator{
	private List<Pair<Cell, Cell>> wallPairs;
	private Random rand;
	
	@Override
	public void generateMaze(Board board, int delay, GraphicsContext gc, Stage stage, int rendSize) {
		wallPairs = new ArrayList<Pair<Cell, Cell>>();
		rand = new Random();
		Cell cell = board.getRandCell();
		cell.setVisited(true);
		wallPairs.addAll(board.getWallPairs(cell));
		
		generateLoop(board, delay, gc, stage, rendSize);
		
	}
	
	@Override
	public boolean generateStep(Board board) {
		if (wallPairs.size() == 0)
			return false;
		Pair<Cell, Cell> randWall = wallPairs.get(rand.nextInt(wallPairs.size()));
		if (randWall.getKey().getVisited() && !randWall.getValue().getVisited())
			breakWall(board, randWall.getKey(), randWall.getValue());
		else if (!randWall.getKey().getVisited() && randWall.getValue().getVisited())
			breakWall(board, randWall.getValue(), randWall.getKey());
		wallPairs.remove(randWall);
		//Keep generating? Yes
		return true;
	}
	
	public void breakWall(Board board, Cell visited, Cell unvisited) {
		visited.removeWall(unvisited);
		unvisited.setVisited(true);
		wallPairs.addAll(board.getWallPairs(unvisited));
	}
}
