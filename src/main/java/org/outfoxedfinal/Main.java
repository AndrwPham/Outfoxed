package org.outfoxedfinal;

import javafx.animation.FillTransition;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;


public class Main extends Application {
    private Stage primaryStage;
    private Pane root;
    private StackPane overlayPane; // New overlay for instructions
    private VBox instructionOverlay; // The actual instructions
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(900, 750);

        Image bgImage = new Image(getClass().getResource("bg.gif").toExternalForm());
        ImageView img = new ImageView(bgImage);
        img.setFitWidth(900);
        img.setFitHeight(750);

        VBox menuBox = new VBox(5, new MenuItem("PLAY", this::showGameLoading),
                new MenuItem("QUIT", Platform::exit));
        menuBox.setBackground(new Background(new BackgroundFill(Color.web("white", 0.5), null, null)));
        menuBox.setTranslateX(375);
        menuBox.setTranslateY(650);

        // **QUESTION MARK BUTTON**
        Label questionMark = new Label("❓");
        questionMark.setFont(Font.font("Arial", FontWeight.BOLD, 50)); // Bigger font for visibility
        questionMark.setTextFill(Color.WHITE);
        questionMark.setStyle("-fx-cursor: hand;");
        questionMark.setOnMouseClicked(event -> toggleInstructions());

        // **Question Mark Positioning**
        questionMark.setTranslateX(820); // Adjust right alignment
        questionMark.setTranslateY(20);  // Adjust top alignment

        // **Overlay Pane (Initially Hidden)**
        overlayPane = new StackPane();
        overlayPane.setPrefSize(900, 750);
        overlayPane.setVisible(false);

        // **Instruction Overlay**
        instructionOverlay = createInstructionOverlay();
        overlayPane.getChildren().add(instructionOverlay);

        root.getChildren().addAll(img, menuBox, questionMark, overlayPane);
        return root;
    }

    private VBox createInstructionOverlay() {
        VBox overlay = new VBox(10);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(20));
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-border-color: white; -fx-border-width: 2px;");
        overlay.setPrefSize(300, 250); // Smaller overlay

        Text instructionTitle = new Text("📜 How to Play");
        instructionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        instructionTitle.setFill(Color.WHITE);

        Label instructionText = new Label(
                "🔹 Roll the dice to move around.\n" +
                        "🔹 Collect clues to find the thief.\n" +
                        "🔹 Work together to solve the mystery!\n"
        );
        instructionText.setWrapText(true);
        instructionText.setTextFill(Color.WHITE);
        instructionText.setFont(Font.font("Arial", 14));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> overlayPane.setVisible(false)); // Hide the overlay when clicked

        overlay.getChildren().addAll(instructionTitle, instructionText, closeButton);
        return overlay;
    }

    // **Toggle Instructions Overlay**
    private void toggleInstructions() {
        overlayPane.setVisible(!overlayPane.isVisible());
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

    private void showGameLoading() {
        Loading gameLoad = new Loading(primaryStage);
        Scene gameScene = new Scene(gameLoad.showLoadingScene()); // Get the scene from GamePanel
        primaryStage.setScene(gameScene); // Switch to the new scene
    }

    public static void main(String[] args) {
        launch(args);
    }
}