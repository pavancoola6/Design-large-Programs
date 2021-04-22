/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public interface MazeSolver {
	
    public static Set<Cell> cellsToRender = new HashSet<Cell>();
	
	public abstract boolean generateStep(Board board);
	
	public abstract void render(GraphicsContext gc, int cellSize, int winSize, int i, Color purple, Color white);
	
	public abstract void solveMaze(Board board, Cell start, Cell end, int delay, GraphicsContext gc, Stage stage, int cellSize, int winSize, int rendSize, boolean rendSelf);
	
	public default void solveLoop(Board board, int delay, GraphicsContext gc, Stage stage, int cellSize, int winSize, int rendSize, boolean rendSelf) {
		boolean solving = true;
		while(solving) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {}
			solving = generateStep(board);
		 	Platform.runLater(new Runnable() {
		        public void run() {
		        	if(rendSelf)
		        		board.render(gc, stage, rendSize);
		        	render(gc, cellSize, winSize, rendSize, Color.PURPLE, Color.WHITE);
		        }
		    });	
		}
	}

	
	public static MazeSolver retrieve(String name) {
		switch(name) {
			case "mouse":
				return (new RandomMouseSolver());
			case "mouse_thread":
				return (new RandomMouseThreadSolver());
			case "wall":
				return (new WallFollowerSolver(true));
			case "wall_thread":
				return (new WallFollowerThreadSolver());
			case "pledge":
				return (new PledgeSolver());
		}
		return null;
	}
}
