package org.outfoxedfinal;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.outfoxedfinal.entity.Suspect;
import org.outfoxedfinal.entity.Thief;
import javafx.scene.Scene;


import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.outfoxedfinal.logic.Decoder;
import org.outfoxedfinal.logic.DiceController;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;


public class GameController {
    private final StackPane stackPane;
    private final GameMap gameMap;
    private final KeyHandler keyHandler;
    private int selectedSuspectsCount = 0;
    private boolean selectMode = false;
    private final DiceController diceController;
    private Thief thief;
    private int move = -1;
    public boolean rollingDone = false;
    private boolean movingDone = false;

    public GameController(GameMap gameMap, KeyHandler keyHandler,DiceController diceController,StackPane stackPane) {
        this.gameMap = gameMap;
        this.keyHandler = keyHandler;
        this.diceController = diceController;
        this.stackPane = stackPane;
        keyHandler.setGameController(this);
        diceController.setGameController(this);
        this.thief = new Thief();
        thief.selectThief(gameMap.getSuspects());
        gameMap.setThief(thief.getThief()); // Pass the selected thief to the GameMap
    }

    public void handleKeyPress(Scene scene) {
        scene.setOnKeyPressed(event -> {
            System.out.println("Key event detected: " + event.getCode());
            keyHandler.handleMovement(event); // Delegate to KeyHandler

        });
    }

    public void showActionPrompt() {
        Platform.runLater(() -> {
            // Create an overlay background
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
            overlay.setPrefSize(300, 300);

            // Title text
            Text title = new Text("Choose an Action");
            title.setFont(new Font(30));
            title.setFill(Color.WHITE);

            // Reveal Suspect button
            Button revealButton = new Button("Reveal Suspect");
            revealButton.setFont(new Font(18));
            revealButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
            revealButton.setOnMouseEntered(e -> revealButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;"));
            revealButton.setOnMouseExited(e -> revealButton.setStyle("-fx-background-color: white; -fx-text-fill: black;"));
            revealButton.setOnAction(e -> {
                showDiceGUI("reveal suspect");
                removeOverlay(overlay);
            });

            // Find Clue button
            Button clueButton = new Button("Find Clue");
            clueButton.setFont(new Font(18));
            clueButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
            clueButton.setOnMouseEntered(e -> clueButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;"));
            clueButton.setOnMouseExited(e -> clueButton.setStyle("-fx-background-color: white; -fx-text-fill: black;"));
            clueButton.setOnAction(e -> {
                showDiceGUI("find clue");
                removeOverlay(overlay);
            });

            // Cancel button
            Button cancelButton = new Button("Cancel");
            cancelButton.setFont(new Font(18));
            cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;"));
            cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"));
            cancelButton.setOnAction(e -> removeOverlay(overlay));

            // Arrange buttons in a vertical box
            VBox vbox = new VBox(20, title, revealButton, clueButton, cancelButton);
            vbox.setAlignment(Pos.CENTER);
            //vbox.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, new CornerRadii(10), Insets.EMPTY)));

            // Add components to overlay
            overlay.getChildren().add(vbox);
            overlay.setAlignment(Pos.CENTER);
            if (stackPane != null) {
                stackPane.getChildren().add(overlay);
            } else {
                System.out.println("overlayContainer is null! Ensure GamePanel passes it correctly.");
            }
        });
    }

    // Helper method to remove the overlay
    private void removeOverlay(StackPane overlay) {
        if (stackPane != null) {
            stackPane.getChildren().remove(overlay);
        }
    }


    public void resetSelection() {
        selectedSuspectsCount = 2; // Reset the count
        System.out.println("Selection reset. Prompting for next action...");
        selectMode = true;
    }

    public void onSuspectSelected(Text card, Suspect suspect) {
        if (suspect.isRevealed()) {
            System.out.println("This suspect is already revealed.");
            return;
        }

        if (selectedSuspectsCount <= 2) {

            System.out.println("Revealed suspect: " + suspect.getName());
            // If two suspects have been revealed, enforce the limit
            if (selectedSuspectsCount == 2) {
                System.out.println("Two suspects have been revealed. No more selections allowed.");
            }
        } else {
            System.out.println("You can only reveal two suspects!");
        }
    }

    public int getSelectedSuspectsCount() {
        return selectedSuspectsCount;
    }
    public void incrementSelectedSuspectsCount() {
        selectedSuspectsCount++;
    }
    public void accuseSuspect(Suspect suspect) {
        if (!suspect.isRevealed()) {
            System.out.println("You can only accuse revealed suspects!");
            return;
        }

        if (gameMap.getThief().equals(suspect)) {
            System.out.println("Correct! " + suspect.getName() + " is the thief! YOU WIN!");
            //showWinMessage(suspect.getName()); // Call win message method
            showGameEndOverlay(suspect.getName(),true);
        } else {
            System.out.println("Incorrect accusation! " + suspect.getName() + " is not the thief.");
            //showLoseMessage(suspect.getName());
            showGameEndOverlay(suspect.getName(),false);
        }
    }

    private void showDiceGUI(String action) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dice.fxml"));
            Parent diceRoot = loader.load(); // Load FXML content

            // Get controller and set necessary values
            DiceController diceController = loader.getController();
            diceController.setAction(action);
            diceController.setGameController(this);

            // Wrap diceRoot inside an overlay
            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Semi-transparent dark background
            overlay.setPrefSize(900, 750);

            // Center the loaded FXML content in the overlay
            overlay.getChildren().add(diceRoot);
            StackPane.setAlignment(diceRoot, Pos.CENTER);

            // **Add overlay to the main game scene**
            if (stackPane != null) {
                stackPane.getChildren().add(overlay);
            } else {
                System.out.println("overlayContainer is null! Ensure GamePanel passes it correctly.");
            }

            // Remove overlay when dice rolling is completed
            new Thread(() -> {
                while (!rollingDone) {
                    try {
                        Thread.sleep(100); // Small delay to avoid CPU overload
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (rollingDone) {
                    removeOverlay(overlay); // Remove overlay when rollingDone is true
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleDiceRollResult(int moves) {
        move = moves;
        System.out.println("Dice roll result: " + move);
        if (move > 0) {
            System.out.println("Dice roll successful. Enabling movement with " + moves + " moves.");
            keyHandler.setMovementEnabled(true);
            keyHandler.setRemainingMoves(move);
        } else {
            System.out.println("Dice roll failed. No moves available.");
        }
    }
    public void onClueEncounter(int row, int col) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Decoder.fxml"));
            Scene scene = new Scene(loader.load());

            // Pass clue information to the Decoder controller
            Decoder decoder = loader.getController();
            decoder.setClueItems(gameMap.getClueItemsAtLocation(row, col));
            decoder.setThiefItems(thief.getThiefItems()); // Ensure thiefItems is passed

            // Show the Decoder GUI
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle("Clue Decoder");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveFox(double colIndex, double rowIndex) {
        double cellWidth = 650 / 18;
        double cellHeight = 650 / 18;
        double xOffset = cellWidth * colIndex + cellWidth / 2;
        double yOffset = cellHeight * rowIndex + cellHeight / 2;

        gameMap.updateFoxPosition(xOffset, yOffset);
    }
    public boolean getLocation(double colIndex, double rowIndex) {
        double cellWidth = 650.0 / 18; // Ensure it's a double to avoid integer division
        double cellHeight = 650.0 / 18;

        // Calculate the expected x and y offsets for the given location
        double expectedXOffset = cellWidth * colIndex + cellWidth / 2 - 650.0 / 2;
        double expectedYOffset = cellHeight * rowIndex + cellHeight / 2 - 650.0 / 2;

        // Get the current fox position from the gameMap
        double currentX = gameMap.getFoxImageView().getTranslateX();
        double currentY = gameMap.getFoxImageView().getTranslateY();

        // Check if the current position matches the expected position
        return Math.abs(currentX - expectedXOffset) < 2 && Math.abs(currentY - expectedYOffset) < 2;
    }


    public void movingDone(int moving){
        if (moving ==0){
            movingDone = true;
        }
    }
    public boolean rollingDone(boolean rolling){
        System.out.println("rollingDone: " + rolling);
        rollingDone = rolling;
        return rollingDone;
    }
    public void isSelectMode(boolean select) {
        selectMode = select;
        System.out.println("isSelectMode: " + selectMode);
    }
    private boolean actionPrompt = false;
    //GameLoop
    public void updateGameLogic() {
        if (!actionPrompt && selectedSuspectsCount == 2) {
            actionPrompt = true;
            showActionPrompt();
        }
        if (selectedSuspectsCount == 4){
            actionPrompt = false;
            resetSelection();
        }
        if (rollingDone) {
            renderUpdates();
            actionPrompt = false;
            rollingDone = false;
        }
        if (movingDone){
            renderUpdates();
            actionPrompt = false;
            movingDone = false;

        }
        if (getLocation(14,17)){
            System.out.println("You've lost");

        }

    }
    //Update UI
    public void renderUpdates() {
        // Define the sequence of positions (x,y) that the fox moves through:
        int[][] positions = {
                {1, 0},  // start
                {1, 3},
                {4, 3},
                {7, 3},
                {10, 3},
                {10, 6},
                {10, 9},
                {13, 9},
                {16, 9},
                {16, 12},
                {13, 12},
                {13, 15},
                {14, 17} // end
        };

        // Iterate through positions to see if we’re at a certain spot
        // and then move to the next one:
        for (int i = 0; i < positions.length - 1; i++) {
            int[] current = positions[i];
            int[] next = positions[i + 1];

            // If we’re at current, move to next
            if (getLocation(current[0], current[1])) {
                moveFox(next[0], next[1]);
                break; // Once we’ve moved, exit to avoid multiple moves in one call
            }
        }
    }

    private void showGameEndOverlay(String suspectName, boolean isWinner) {
        // Create a semi-transparent background
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(900, 750);

        // Game result message
        Text message = new Text(isWinner
                ? "🎉 YOU WIN! " + suspectName + " was the thief! 🎉"
                : "❌ WRONG ACCUSATION! " + suspectName + " is NOT the thief.");
        message.setFont(new Font(30));
        message.setFill(Color.WHITE);

        // Load an image
        String imagePath = isWinner ? "win.jpg" : "lose.jpg";
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        ImageView resultImage = new ImageView(image);
        resultImage.setFitWidth(300); // Set desired image width
        resultImage.setFitHeight(300); // Set desired image height

        // Restart button
        Button restartButton = new Button("Restart Game");
        restartButton.setFont(new Font(18));
        restartButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        restartButton.setOnMouseEntered(e -> restartButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;"));
        restartButton.setOnMouseExited(e -> restartButton.setStyle("-fx-background-color: white; -fx-text-fill: black;"));
        restartButton.setOnAction(event -> restartGame(overlay)); // Restart the game

        // Layout for message, image & button
        VBox vbox = new VBox(20, resultImage, message, restartButton);
        vbox.setAlignment(Pos.CENTER);

        overlay.getChildren().add(vbox);
        overlay.setAlignment(Pos.CENTER);

        // **Add the overlay to overlayContainer, which sits inside BorderPane**
        if (stackPane != null) {
            stackPane.getChildren().add(overlay);
        } else {
            System.out.println("Overlay container is null! Ensure GamePanel passes it correctly.");
        }
    }

    private void restartGame(StackPane overlay) {

    }


}

