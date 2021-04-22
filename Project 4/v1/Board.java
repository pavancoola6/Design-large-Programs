/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;


public class Board {
	private int size;
	private int cellSize;
	private int numCells;
	private List<List<Cell>> cells;
	private Random rand = new Random();
	private Cell start;
	private Cell end;
	
	public Board(int size, int cellSize) {
		this.size = size;
		this.cellSize = cellSize;
		numCells = size / cellSize;
		
		//Create an empty board of cells
		cells = new ArrayList<List<Cell>>();
		for (int i = 0; i < numCells; i++) {
			cells.add(new ArrayList<Cell>(numCells));
			for (int j = 0; j < numCells; j++) {
				cells.get(i).add(new Cell(false, j, i));
			}
		}
	}
	
	public Cell getCellAt(int x, int y) {
		if (x < 0 || y < 0 || x >= numCells || y >= numCells)
			return null;
		return cells.get(y).get(x);
	}
	
	public Cell getRelative(Cell cell, int xDisplacement, int yDisplacement) {
		return getCellAt(cell.getX() + xDisplacement, cell.getY() + yDisplacement);
	}
	
	public Cell getRelative(Cell cell, String direction) {
		switch(direction.toLowerCase()) {
			case "left":
				return getCellAt(cell.getX() - 1, cell.getY());
			case "up":
				return getCellAt(cell.getX(), cell.getY() + 1);
			case "right":
				return getCellAt(cell.getX() + 1, cell.getY());
		}
		//down is  default if string is any unreadable value or "down" itself
		return getCellAt(cell.getX(), cell.getY() - 1);
	}
	
	public boolean pathOpen(Cell cell, String direction) {
		boolean pathOp = false;
		switch(direction.toLowerCase()) {
			case "left":
				pathOp = !((cell.getWalls() | 0b0111) == 0b1111);
				break;
			case "up":
				pathOp = !((cell.getWalls() | 0b1011) == 0b1111);
				break;
			case "right":
				pathOp = !((cell.getWalls() | 0b1101) == 0b1111);
				break;
			case "down":
				pathOp = !((cell.getWalls() | 0b1110) == 0b1111);
				break;
		}
		pathOp = pathOp && (getRelative(cell, direction) != null);
		return pathOp;
	}
	
	public boolean onlyPathOpen(Cell cell, String direction) {
		boolean pathOp = false;
		switch(direction.toLowerCase()) {
			case "left":
				pathOp = (cell.getWalls() == 0b0111);
				break;
			case "up":
				pathOp = (cell.getWalls() == 0b1011);
				break;
			case "right":
				pathOp = (cell.getWalls() == 0b1101);
				break;
			case "down":
				pathOp = (cell.getWalls() == 0b1110);
				break;
		}
		return pathOp;
	}
	
	public List<Cell> getNeighbors(Cell cell) {
		List<Cell> neighbors = new ArrayList<Cell>();
		neighbors.add(getRelative(cell, -1, 0));
		neighbors.add(getRelative(cell, 0, 1));
		neighbors.add(getRelative(cell, 1, 0));
		neighbors.add(getRelative(cell, 0, -1));
		//Remove all null values
		while (neighbors.remove(null));
		return neighbors;
	}
	
	public List<Cell> getUnvisNeighbors(Cell cell) {
		List<Cell> unvisNeighbors = new ArrayList<Cell>();
		for (Cell neighbor : getNeighbors(cell)) {
			if (!neighbor.getVisited())
				unvisNeighbors.add(neighbor);
		}
		return unvisNeighbors;	
	}
	
	public Cell getRandUnvisNeighbor(Cell cell) {
		List<Cell> unvisited = getUnvisNeighbors(cell);
		if (unvisited.size() == 0)
			return null;
		
		//Return a random value from the unvisited neighbors
		return unvisited.get(rand.nextInt(unvisited.size()));
	}
	
	public void render(GraphicsContext gc, Stage stage, int rendSize) {
        gc.setFill(Color.WHITE);
        for (int i = 0; i < numCells; i++) {
        	for (int j = 0; j < numCells; j++) {
        		if (getCellAt(i, j).getVisited()) {
        			getCellAt(i, j).render(gc, cellSize, size, rendSize, Color.WHITE, Color.WHITE);
        		}
        	}
        }
        //Render Start and end locations
    	start.render(gc, cellSize, size, rendSize, Color.GREEN, Color.WHITE);
    	end.render(gc, cellSize, size, rendSize, Color.RED, Color.WHITE);
    	
	}
	
	public Cell getCellOnWall(String wall) {
		switch(wall.toLowerCase()) {
			case "left":
				return getCellAt(0, rand.nextInt(numCells));
			case "top":
				return getCellAt(rand.nextInt(numCells), numCells - 1);
			case "right":
				return getCellAt(numCells - 1, rand.nextInt(numCells));
		}
		//Default to bottom
		return getCellAt(rand.nextInt(numCells), 0);
	}
	
	public Pair<Cell, Cell> getStartEnd() {
		double value = Math.random();
		Cell start;
		Cell end;
		if (value <= 0.25) {
			start = getCellOnWall("left");
			end = getCellOnWall("right");
		}
		else if(value <= 0.5) {
			start = getCellOnWall("top");
			end = getCellOnWall("bottom");
		}
		else if(value <= 0.75) {
			start = getCellOnWall("right");
			end = getCellOnWall("left");
		}
		else {
			start = getCellOnWall("bottom");
			end = getCellOnWall("top");
		}
		return (new Pair<Cell, Cell>(start, end));
	}
	
	public void setStartEnd(Cell start, Cell end) {
		this.start = start;
		this.end = end;
	}
	
	public List<String> getAllDirections(){
		List<String> directions = new ArrayList<String>(4);
		directions.add("left");
		directions.add("up");
		directions.add("right");
		directions.add("down");
		return directions;
	}
	
	public String getRandPossDirection(Cell cell) {
		Random rand = new Random();
		List<String> poss = getPossDirections(cell);
        return poss.get(rand.nextInt(poss.size()));
	}
	
	public String getDirection(int ang) {
		switch(ang) {
			case 0:
				return "left";
			case 1:
				return "up";
			case 2:
				return "right";
			case 3:
				return "down";
		}
		return "down";
	}
	
	public String getTurnedDirection(String direction, int turnAmount) {
		int current = 0;
		switch(direction) {
			case "left":
				current = 0;
				break;
			case "up":
				current = 1;
				break;
			case "right":
				current = 2;
				break;
			case "down":
				current = 3;
				break;
		}
		current += turnAmount;
		while(current > 3 || current < 0) {
			if(current > 3)
				current = (current % 3) - 1;
			else if(current < 0)
				current = -current + 2;
		}
		return getDirection(current);
		
	}
	
	public List<String> getPossDirections(Cell cell){
		List<String> directions = new ArrayList<String>(4);
		if (pathOpen(cell, "left"))
			directions.add("left");
		if (pathOpen(cell, "up"))
			directions.add("up");
		if (pathOpen(cell, "right"))
			directions.add("right");
		if (pathOpen(cell, "down"))
			directions.add("down");
		return directions;
	}
	
	public List<Cell> getAllCells(){
		List<Cell> allCells = new ArrayList<Cell>(numCells * numCells);
		for (List<Cell> list : cells) {
			allCells.addAll(list);
		}
		return allCells;
	}
	
	public List<Pair<Cell, Cell>> getWallPairs(Cell cell){
		List<Pair<Cell, Cell>> wallPairs = new ArrayList<Pair<Cell, Cell>>(4);
		for (String direction : getAllDirections()) {
			Cell other = getRelative(cell, direction);
			if (other != null) {
				wallPairs.add(new Pair<Cell, Cell>(cell, other));
			}
		}
		return wallPairs;
	}
	
	public List<Pair<Cell, Cell>> getAllWalls(){
		List<Pair<Cell, Cell>> allWalls = new ArrayList<Pair<Cell, Cell>>();
		
		for (Cell cell : getAllCells()) {
			for (Pair<Cell, Cell> newPair : getWallPairs(cell)) {
				boolean duplicate = false;
				for (Pair<Cell, Cell> cellPair : allWalls) {
					//Only add if pair doesn't already exist
					if ((cellPair.getKey() == newPair.getKey() && cellPair.getValue() == newPair.getValue()) || 
						(cellPair.getKey() == newPair.getValue() && cellPair.getValue() == newPair.getKey())) {
						duplicate = true;
						break;
					}
				}
				if(!duplicate)
					allWalls.add(newPair);
			}
		}
		return allWalls;
	}
	
	public Cell getRandCell() {
		return getAllCells().get(rand.nextInt(getAllCells().size()));
	}

}
