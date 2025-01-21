package org.outfoxedfinal;
//This is for the GameGUI and the game loop

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
import org.outfoxedfinal.logic.DiceController;

import java.util.List;
import java.util.Optional;

public class GamePanel {
    public Scene createScene() {
        BorderPane root = new BorderPane();

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

        Image foxIcon = new Image(getClass().getResource("fox/thief.jpg").toString()); // Ensure the file path is correct
        ImageView foxImageView = new ImageView(foxIcon);

        // Optional: Adjust icon size
        foxImageView.setFitWidth(30);
        foxImageView.setFitHeight(30);
        foxImageView.setTranslateX(xOffset - imgMap.getFitWidth() / 2); // Adjust relative to center
        foxImageView.setTranslateY(yOffset - imgMap.getFitHeight() / 2); // Adjust relative to center

        StackPane mapContainer = new StackPane(imgMap);
        mapContainer.setPrefSize(550, 550);
        mapContainer.getChildren().add(character);
        StackPane.setAlignment(character, Pos.CENTER);
        mapContainer.getChildren().add(foxImageView);

        GameMap gameMap = new GameMap(18,18);
        //mapContainer.getChildren().addAll(gameMap.getMapGrid());
        HBox topSuspects = gameMap.getTopSuspectCards(true);
        VBox leftSuspects = gameMap.getLeftSuspectCards(true);
        VBox rightSuspects = gameMap.getRightSuspectCards(true);

       // StackPane.setAlignment(gameMap.getMapGrid(),Pos.CENTER);
        // Align suspect cards
        topSuspects.setAlignment(Pos.CENTER);
        leftSuspects.setAlignment(Pos.CENTER);
        rightSuspects.setAlignment(Pos.CENTER);

        // Set spacing between cards
        topSuspects.setSpacing(20);
        leftSuspects.setSpacing(20);
        rightSuspects.setSpacing(20);

        // Set padding to ensure equal spacing
        BorderPane.setMargin(topSuspects, new Insets(20));
        BorderPane.setMargin(leftSuspects, new Insets(20));
        BorderPane.setMargin(rightSuspects, new Insets(20));

        // Add components to the BorderPane
        root.setCenter(mapContainer);
        root.setTop(topSuspects);
        root.setLeft(leftSuspects);
        root.setRight(rightSuspects);

        KeyHandler movementHandler = new KeyHandler(character, gameMap);
        DiceController diceController = new DiceController();
        GameController gameController = new GameController(gameMap, movementHandler,diceController);
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
        buttonContainer.setPadding(new Insets(10));
        bottomSection.getChildren().add(buttonContainer);

        // Set the bottom section in the BorderPane
        root.setBottom(bottomSection);
        BorderPane.setAlignment(bottomSection, Pos.CENTER);

        Scene scene = new Scene(root, 900, 750);
        gameController.handleKeyPress(scene);

        return scene;
    }

}
