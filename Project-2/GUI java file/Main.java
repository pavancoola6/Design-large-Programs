package domino;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Display display = new Display();
        MainGameLoop controller = new MainGameLoop(display);

        primaryStage.setTitle("Project 2 - Domino Game");
        primaryStage.setScene(new Scene(display, 1000, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
