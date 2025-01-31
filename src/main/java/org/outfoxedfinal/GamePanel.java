package org.outfoxedfinal;
//This is for the GameGUI and the game loop

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.outfoxedfinal.entity.Suspect;
import org.outfoxedfinal.entity.SuspectInitializer;
import org.outfoxedfinal.logic.DiceController;

import java.util.*;

public class GamePanel {

    public Scene createScene() {
        BorderPane root = new BorderPane();
        StackPane stackPane = new StackPane();
        stackPane.setPickOnBounds(false);
        GameMap gameMap = new GameMap(18,18);

        Image img = new Image(getClass().getResource("map/map.png").toString());
        ImageView imgMap = new ImageView(img);
        imgMap.setFitHeight(650);
        imgMap.setFitWidth(650);
        double cellWidth = imgMap.getFitWidth() / 18;  // Assuming 18 columns
        double cellHeight = imgMap.getFitHeight() / 18; // Assuming 18 rows
        double xOffset =  cellWidth * 1 + cellWidth / 2; // Column index 0
        double yOffset = cellHeight * 0 + cellHeight / 2; // Row index 1 // Row index 1
        imgMap.setPreserveRatio(true);

        Text character = new Text("🎩");
        character.setFont(new Font(15));

        Image foxIcon = new Image(getClass().getResource("fox/thief.png").toString()); // Ensure the file path is correct
        ImageView foxImageView = new ImageView(foxIcon);

        // Optional: Adjust icon size
        foxImageView.setFitWidth(80);
        foxImageView.setFitHeight(80);
        gameMap.setFoxImageView(foxImageView);
        foxImageView.setTranslateX(xOffset - imgMap.getFitWidth() / 2); // Adjust relative to center
        foxImageView.setTranslateY(yOffset - imgMap.getFitHeight() / 2); // Adjust relative to center

        StackPane mapContainer = new StackPane(imgMap);
        mapContainer.setPrefSize(550, 550);
        mapContainer.getChildren().add(character);
        StackPane.setAlignment(character, Pos.CENTER);
        mapContainer.getChildren().add(foxImageView);

        VBox leftSuspects = gameMap.getLeftSuspectCards(true);
        VBox rightSuspects = gameMap.getRightSuspectCards(true);

        // Align suspect cards
        leftSuspects.setAlignment(Pos.CENTER);
        rightSuspects.setAlignment(Pos.CENTER);

        // Set spacing between cards
        leftSuspects.setSpacing(20);
        rightSuspects.setSpacing(20);

        // Set padding to ensure equal spacing
        BorderPane.setMargin(leftSuspects, new Insets(20));
        BorderPane.setMargin(rightSuspects, new Insets(20));

        // Add components to the BorderPane
        root.setCenter(mapContainer);
      //  root.setTop(topSuspects);
        root.setLeft(leftSuspects);
        root.setRight(rightSuspects);

        KeyHandler movementHandler = new KeyHandler(character, gameMap);
        DiceController diceController = new DiceController();
        GameController gameController = new GameController(gameMap, movementHandler,diceController,stackPane);
        diceController.setGameController(gameController);
        gameMap.setGameController(gameController);
        movementHandler.setGameController(gameController);

        // Create the "Accuse" button
        Button accuseButton = new Button("Accuse");
        accuseButton.setOnAction(event -> {
            List<Suspect> revealedSuspects = gameMap.getSuspects().stream()
                    .filter(Suspect::isRevealed)
                    .toList();

            if (revealedSuspects.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No suspects have been revealed yet!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            ChoiceDialog<Suspect> dialog = new ChoiceDialog<>(revealedSuspects.get(0), revealedSuspects);
            dialog.setTitle("Accuse a Suspect");
            dialog.setHeaderText("Select a revealed suspect to accuse.");
            dialog.setContentText("Choose a suspect:");

            Optional<Suspect> result = dialog.showAndWait();
            result.ifPresent(gameController::accuseSuspect);
        });

        // Combine the bottom suspects and the "Accuse" button
        StackPane bottomSection = new StackPane();
        VBox buttonContainer = new VBox(accuseButton);
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
        buttonContainer.setTranslateY(-30); // Move up by 30 pixels

        bottomSection.getChildren().add(buttonContainer);

        // Set the bottom section in the BorderPane
        root.setBottom(bottomSection);
        BorderPane.setAlignment(bottomSection, Pos.CENTER);
        // Wrap everything inside another StackPane to allow overlays
        StackPane mainLayout = new StackPane(root, stackPane);

        Scene scene = new Scene(mainLayout, 900, 750);
        gameController.handleKeyPress(scene);
        scene.getStylesheets().add(getClass().getResource("UIStyle.css").toExternalForm());

        // The GameLoop
        new AnimationTimer() {
            long lastTick = 0;
            public void handle(long now) {
                // Check if sufficient time has passed since the last update
                if (lastTick == 0) {
                    lastTick = now;
                    return;
                }

                // Time elapsed in nanoseconds
                long elapsedTime = now - lastTick;

                // Update interval (e.g., 16ms for ~60FPS)
                if (elapsedTime > 16_000_000) {
                    lastTick = now;
                    gameController.updateGameLogic();
                }
            }
        }.start();
        return scene;
    }
}
