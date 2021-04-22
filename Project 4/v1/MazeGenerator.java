/* Authors - Pavan Kumar Singara and Anmol Singh Gill
 * CS351L - Project 4
 *
 */

package v1;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public interface MazeGenerator {
	
	public abstract boolean generateStep(Board board);
	
	public abstract void generateMaze(Board board, int delay, GraphicsContext gc, Stage stage, int rendSize);
	
	public default void generateLoop(Board board, int delay, GraphicsContext gc, Stage stage, int rendSize) {
		boolean generating = true;               
		while(generating) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {}
			generating = generateStep(board);
		 	Platform.runLater(new Runnable() {
		        public void run() {
		        	board.render(gc, stage, rendSize);
		        }
		    });	
		}
	}
	
	public static MazeGenerator retrieve(String name) {
		switch(name) {
			case "dfs":
				return (new DepthFirstGenerator());
			case "kruskal":
				return (new KruskalGenerator());
			case "prim":
				return (new PrimGenerator());
		}
		return null;
	}
}
