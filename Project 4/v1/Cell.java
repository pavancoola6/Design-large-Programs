/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class Cell {
	private boolean visited;
	private int x;
	private int y;
	private int walls;
	
	public Cell(boolean visited, int x, int y) {
		this.visited = visited;
		this.x = x;
		this.y = y;
		//left top right bottom
		walls = 0b1111;
		
	}

	public Vector<Integer> getDisplacement(Cell end){
		int xDis = end.getX() - getX();
		int yDis = end.getY() - getY();
		Vector<Integer> displacement = new Vector<Integer>(2);
		displacement.add(xDis);
		displacement.add(yDis);
		return displacement;
	}
	
	public int leftX(int cellSize, int winSize) {
		return x * cellSize;
		
	}
	public int leftY(int cellSize, int winSize) {
		return winSize - (y + 1) * cellSize;
	}
	
	public void render(GraphicsContext gc, int cellSize, int winSize, int rendSize, Color cellCol, Color pathCol) {
		gc.setFill(pathCol);
		renderPaths(gc, cellSize, winSize, rendSize);
		gc.setFill(cellCol);
		gc.fillRect(x * cellSize + (cellSize - rendSize) / 2, winSize - (y + 1) * cellSize + (cellSize - rendSize) / 2, rendSize, rendSize);
		
	}
	
	public void renderPaths(GraphicsContext gc, int cellSize, int winSize, int rendSize) {
		int wallTh = (cellSize - rendSize) / 2;
		//Left path open
		if (!((walls | 0b0111) == 0b1111)) {
			gc.fillRect(leftX(cellSize, winSize) - wallTh, leftY(cellSize, winSize) + wallTh, wallTh * 3, rendSize);
		}
		//Top path open
		if (!((walls | 0b1011) == 0b1111)) {
			gc.fillRect(leftX(cellSize, winSize) + wallTh, leftY(cellSize, winSize) - wallTh, rendSize, wallTh * 3);
		}
		//Right path open
		if (!((walls | 0b1101) == 0b1111)) {
			gc.fillRect(leftX(cellSize, winSize) + rendSize, leftY(cellSize, winSize) + wallTh, wallTh * 3, rendSize);
		}
		//Bottom path open
		if (!((walls | 0b1110) == 0b1111)) {
			gc.fillRect(leftX(cellSize, winSize) + wallTh, leftY(cellSize, winSize) + rendSize, rendSize, wallTh * 3);
		}
	}
	
	//Assume cells are adjacent - Either next to or on top of one another
	public void removeWall(Cell end) {
		Vector<Integer> dis = getDisplacement(end);
		int startMask;
		int endMask;
		//Left
		if(dis.get(0) == -1 && dis.get(1) == 0) {
			startMask = 0b0111;
			endMask = 0b1101;
		}
		//Top
		else if(dis.get(0) == 0 && dis.get(1) == 1) {
			startMask = 0b1011;
			endMask = 0b1110;
		}
		//Right
		else if(dis.get(0) == 1 && dis.get(1) == 0) {
			startMask = 0b1101;
			endMask = 0b0111;
		}
		//Bottom
		else {
			startMask = 0b1110;
			endMask = 0b1011;
		}
		setWalls(getWalls() & startMask);
		end.setWalls(end.getWalls() & endMask);		
	}
	
	
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWalls() {
		return walls;
	}
	public void setWalls(int walls) {
		this.walls = walls;
	}
	public boolean getVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
}


