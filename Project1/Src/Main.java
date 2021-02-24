/*pavan kumar singara
  CS351L
  2/5/21
 */
package sample;

import java.text.DecimalFormat;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {
    public static final double WIDTH = 1800.0D;
    public static final double HEIGHT = 1024.0D;
    public static final double OFFSET_W = 900.0D;
    public static final double OFFSET_H = 512.0D;

    public Main() {
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Times Table Visualization");
        double ttn = 2.0D;
        Circle circle = new Circle(900.0D, 510.0D, 250.0D);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        double spacing = 10.0D;
        VBox controls = new VBox(spacing);
        controls.setLayoutX(10.0D);
        controls.setLayoutY(100.0D);
        Button start = new Button("Run");
        Button stop = new Button("Pause");
        Button jumpTofav=new Button("Jump To Favorite");
        ChoiceBox<String> choiceBox= new ChoiceBox<>();
        choiceBox.getItems().add("15");
        choiceBox.getItems().add("25");
        choiceBox.getItems().add("48");
        choiceBox.getItems().add("320");
        choiceBox.setValue(" My Favorites");
        HBox ttnBox = new HBox(spacing);
        Label ttnLabel = new Label("Times Table Number:");
        final DecimalFormat dfTtn = new DecimalFormat("#.0");
        final Label ttnValueLabel = new Label(Double.toString(ttn));
        ttnBox.getChildren().addAll(new Node[]{ttnLabel, ttnValueLabel});
        HBox stepNumBox = new HBox(spacing);
        Label stepNumLabel = new Label("Increment By:");
        final Slider stepNumSlider = new Slider(0.0D, 5.0D, 1.0D);
        stepNumSlider.setShowTickMarks(true);
        stepNumSlider.setShowTickLabels(true);
        stepNumSlider.setMajorTickUnit(0.25D);
        stepNumSlider.setBlockIncrement(0.10000000149011612D);
        stepNumBox.getChildren().addAll(new Node[]{stepNumLabel, stepNumSlider});
        HBox delayBox = new HBox(spacing);
        Label delayLabel = new Label("Delay By(seconds):");
        final Slider delaySlider = new Slider(0.0D, 5.0D, 0.5D);
        delaySlider.setShowTickMarks(true);
        delaySlider.setShowTickLabels(true);
        delaySlider.setMajorTickUnit(0.25D);
        delaySlider.setBlockIncrement(0.10000000149011612D);
        delayBox.getChildren().addAll(new Node[]{delayLabel, delaySlider});
        Label jumpToLabel = new Label("Jump To Parameters Section");
        HBox timesTableNumBox = new HBox(spacing);
        Label timesTableNumLabel = new Label("Times Table Number:");
        TextField timesTableNumTF = new TextField("2");
        timesTableNumBox.getChildren().addAll(new Node[]{timesTableNumLabel, timesTableNumTF});
        HBox numPointsBox = new HBox(spacing);
        Label numPointsLabel = new Label("Number Of Points:");
        final TextField numPointsTF = new TextField("360");
        numPointsBox.getChildren().addAll(new Node[]{numPointsLabel, numPointsTF});
        Button jumpToButton = new Button("Jump To");
        controls.getChildren().addAll(new Node[]{ttnBox, stepNumBox, delayBox, jumpToLabel, timesTableNumBox, numPointsBox, jumpToButton, start, stop, choiceBox, jumpTofav});
        Canvas canvas = new Canvas(1800.0D, 1024.0D);
        final Pane root = new Pane(new Node[]{canvas, circle, controls});
        Scene scene = new Scene(root, 1800.0D, 1024.0D);
        primaryStage.setScene(scene);
        primaryStage.show();
        final sample.Visualization visualization = new sample.Visualization(ttn,250.0D);

        class MyAnimationTimer extends AnimationTimer {
            private long lastUpdate = 0L;

            MyAnimationTimer() {
            }

            public void run(boolean jumpTo) {
                root.getChildren().removeIf((node) -> {
                    return node instanceof Group;
                });
                Group lines = visualization.generateLines(Double.parseDouble(numPointsTF.getText()));
                root.getChildren().add(lines);
                ttnValueLabel.setText(dfTtn.format(visualization.getTimesTableNum()));
                if (!jumpTo) {
                    visualization.incrementTimesTableNum(stepNumSlider.getValue());
                }

            }

            public void handle(long now) {
                if ((double)(now - this.lastUpdate) >= delaySlider.getValue() * 1.0E9D && !numPointsTF.getText().equals("")) {
                    this.run(false);
                    this.lastUpdate = now;
                }

            }
        }

        MyAnimationTimer timer = new MyAnimationTimer();
        start.setOnAction((actionEvent) -> {
            timer.start();
        });
        stop.setOnAction((actionEvent) -> {
            timer.stop();
        });
        jumpTofav.setOnAction((event) -> {
            timer.stop();
            visualization.setTimesTableNum(Double.parseDouble(choiceBox.getValue()));
            timer.run(true);
        });
        jumpToButton.setOnAction((event) -> {
            timer.stop();
            visualization.setTimesTableNum(Double.parseDouble(timesTableNumTF.getText()));
            timer.run(true);
        });
        choiceBox.setOnAction((event) ->{
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
