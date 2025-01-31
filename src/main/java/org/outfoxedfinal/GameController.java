package org.outfoxedfinal;


import javafx.application.Platform;
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


public class GameController {
    private final OverlayManager overlayManager;
    private final GameMap gameMap;
    private final KeyHandler keyHandler;
    private int selectedSuspectsCount = 0;
    private final DiceController diceController;
    private Thief thief;
    private int move = -1;
    private boolean movingDone = false;
    private boolean rollingDone = false;
    private boolean actionDone = false;
    private boolean actionPrompt = false;

    public GameController(GameMap gameMap, KeyHandler keyHandler,DiceController diceController,OverlayManager overlayManager) {
        this.gameMap = gameMap;
        this.keyHandler = keyHandler;
        this.diceController = diceController;
        this.overlayManager = overlayManager;
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
                overlayManager.removeOverlay(); // Remove overlay when action is selected
            });

            // Find Clue button
            Button clueButton = new Button("Find Clue");
            clueButton.setFont(new Font(18));
            clueButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
            clueButton.setOnMouseEntered(e -> clueButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;"));
            clueButton.setOnMouseExited(e -> clueButton.setStyle("-fx-background-color: white; -fx-text-fill: black;"));
            clueButton.setOnAction(e -> {
                showDiceGUI("find clue");
                overlayManager.removeOverlay();
            });

            // Cancel button
            Button cancelButton = new Button("Cancel");
            cancelButton.setFont(new Font(18));
            cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;"));
            cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"));
            cancelButton.setOnAction(e -> overlayManager.removeOverlay());

            // Arrange buttons in a vertical box
            VBox vbox = new VBox(20, title, revealButton, clueButton, cancelButton);
            vbox.setAlignment(Pos.CENTER);

            // Create an overlay using overlayManager
            overlayManager.createOverlay(vbox);
        });
    }

    public void resetSelection() {
        selectedSuspectsCount = 0; // Reset the count
        System.out.println("Selection reset. Prompting for next action...");
        actionPrompt = false;
    }

    public void onSuspectSelected(Text card, Suspect suspect) {
        if (suspect.isRevealed()) {
            System.out.println("This suspect is already revealed.");
            return;
        }

        if (selectedSuspectsCount <= 2) {
            selectedSuspectsCount++;
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
        Platform.runLater(() -> {
            try {
                // Load the Dice.fxml layout
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dice.fxml"));
                Parent diceRoot = loader.load(); // Load FXML content

                // Get the DiceController and set necessary values
                DiceController diceController = loader.getController();

                diceController.setAction(action);
                diceController.setGameController(this);
                // ✅ Properly use overlayManager to create the overlay
                overlayManager.createOverlay(diceRoot);
                new Thread(() -> {
                    while (!rollingDone) {
                        try {
                            Thread.sleep(100); // Small delay to avoid CPU overload
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // ✅ Ensure UI changes run on JavaFX Application Thread
                    Platform.runLater(() -> {
                        overlayManager.removeOverlay(); // Remove dice overlay
                        rollingDone = false; // Reset rolling state
                        System.out.println("Dice overlay removed.");
                    });

                }).start();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to load Dice.fxml.");
            }
        });
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
            Parent decoderRoot = loader.load();

            Decoder decoder = loader.getController();
            decoder.setClueItems(gameMap.getClueItemsAtLocation(row, col));
            decoder.setThiefItems(thief.getThiefItems());

            overlayManager.createOverlay(decoderRoot);
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
    public boolean actionDone(boolean action){
        System.out.println("actionDone: " + action);
        actionDone = action;
        return actionDone;
    }
    public boolean rollingDone(boolean rolling){
        System.out.println("rollingDone: " + rolling);
        rollingDone = rolling;
        return rollingDone;
    }

    //GameLoop
    public void updateGameLogic() {
        if (!actionPrompt && selectedSuspectsCount == 2) {
            actionPrompt = true;
            showActionPrompt();
        }
        if (actionDone) {
            renderUpdates();
            actionPrompt = false;
            actionDone = false;
            keyHandler.switchTurn();
        }
        if (movingDone){
            renderUpdates();
            actionPrompt = false;
            movingDone = false;
            keyHandler.switchTurn();

        }
//        if (getLocation(14,17)){
//            System.out.println("You've lost");
//
//        }

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
        // Game result message
        Text message = new Text(isWinner
                ? "🎉 YOU WIN! " + suspectName + " was the thief! 🎉"
                : "❌ WRONG ACCUSATION! " + suspectName + " is NOT the thief.");
        message.setFont(new Font(30));
        message.setFill(Color.WHITE);

        // Load an image based on result
        String imagePath = isWinner ? "win.jpg" : "lose.jpg";
        ImageView resultImage = new ImageView();

        try {
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            resultImage.setImage(image);
            resultImage.setFitWidth(300); // Set desired image width
            resultImage.setFitHeight(300); // Set desired image height
        } catch (Exception e) {
            System.out.println("Error loading image: " + imagePath);
            e.printStackTrace();
        }

        // Restart button
        Button restartButton = new Button("Restart Game");
        restartButton.setFont(new Font(18));
        restartButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        restartButton.setOnMouseEntered(e -> restartButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;"));
        restartButton.setOnMouseExited(e -> restartButton.setStyle("-fx-background-color: white; -fx-text-fill: black;"));
        restartButton.setOnAction(event -> {
            restartGame();
            overlayManager.removeOverlay(); // Remove overlay after restarting
        });

        // Layout for message, image & button
        VBox vbox = new VBox(20, resultImage, message, restartButton);
        vbox.setAlignment(Pos.CENTER);

        // **Use overlayManager to create the overlay**
        overlayManager.createOverlay(vbox);
    }


    private void restartGame() {

    }


}

