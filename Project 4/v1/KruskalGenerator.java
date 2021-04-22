/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.util.Pair;

public class KruskalGenerator implements MazeGenerator{
	private List<Set<Cell>> cellSets;
	private List<Pair<Cell, Cell>> wallPairs;
	private int wallIdx;
	
	@Override
	public void generateMaze(Board board, int delay, GraphicsContext gc, Stage stage, int rendSize) {
		cellSets = new ArrayList<Set<Cell>>();
		//Create cell sets
		for (Cell newCell : board.getAllCells()) {
			cellSets.add(new HashSet<Cell>(List.of(newCell)));
		}
		wallPairs = board.getAllWalls();
		//Randomize order of walls
		Collections.shuffle(wallPairs);
		wallIdx = 0;
		
		generateLoop(board, delay, gc, stage, rendSize);
	}

	@Override
	public boolean generateStep(Board board) {
		if (wallIdx == wallPairs.size())
			return false;
		Pair<Cell, Cell> currentWall = wallPairs.get(wallIdx);
		if (!(getSet(currentWall.getKey()) == getSet(currentWall.getValue()))) {
			currentWall.getKey().removeWall(currentWall.getValue());
			getSet(currentWall.getKey()).addAll(getSet(currentWall.getValue()));
			Collections.replaceAll(cellSets, getSet(currentWall.getValue()), getSet(currentWall.getKey()));
			currentWall.getKey().setVisited(true);
			currentWall.getValue().setVisited(true);
		}
		wallIdx ++;
		//Still generating
		return true;
	}

	public Set<Cell> getSet(Cell cell){
		for (Set<Cell> set : cellSets) {
			if (set.contains(cell))
				return set;
		}
		return null;
	}
}
