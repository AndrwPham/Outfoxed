package org.outfoxedfinal;

import javafx.animation.FillTransition;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(900, 750);

        Image bgImage = new Image(getClass().getResource("bg.jpg").toExternalForm());
        ImageView img = new ImageView(bgImage);
        VBox box = new VBox(5,new MenuItem("PLAY",this::showGamePanel),
                            new MenuItem("QUIT", Platform::exit)
                            );
        box.setBackground(new Background(new BackgroundFill(Color.web("white",0.5),null,null)));
        box.setTranslateX(375);
        box.setTranslateY(650);

        root.getChildren().addAll(img,box);
        return root;
    }
    private static class MenuItem extends StackPane {
        MenuItem(String name, Runnable action) {
            LinearGradient gradient = new LinearGradient(
                    0,0.5,1,0.5,true, CycleMethod.NO_CYCLE,
                    new Stop(0.1, Color.web("white", 0.75)),
                    new Stop(1.0, Color.web("white", 0.15))
                    );
            Rectangle bg = new Rectangle(150,30, gradient);
            Rectangle bg1 = new Rectangle(150,30, Color.web("white", 0.2));

            FillTransition ft = new FillTransition(Duration.seconds(0.5),
                    bg1, Color.web("black", 0.2),Color.web("white", 0.2));
            ft.setAutoReverse(true);
            ft.setCycleCount(Integer.MAX_VALUE);

            hoverProperty().addListener((observable, oldValue, isHovering) -> {
                if (isHovering){
                    ft.playFromStart();
                }else{
                    ft.stop();
                    bg1.setFill(Color.web("black", 0.2));
                }
            });
            Rectangle line = new Rectangle(5,30);
            line.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.RED).otherwise(Color.GRAY));

            Text text = new Text(name);
            text.setFont(Font.font("System", FontWeight.BOLD, 22)); // Bold font style

            setOnMouseClicked(e->action.run());
            setOnMousePressed(e-> bg.setFill(Color.LIGHTBLUE));

            HBox box = new HBox(40,line,text);
            box.setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(bg,bg1,box);
        }
    }

    private void showGamePanel() {
        GamePanel gamePanel = new GamePanel();
        Scene gameScene = gamePanel.createScene(); // Get the scene from GamePanel
        primaryStage.setScene(gameScene); // Switch to the new scene
    }

    public static void main(String[] args) {
        launch(args);
    }
}