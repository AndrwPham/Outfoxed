package org.outfoxedfinal;


import javafx.application.Platform;
import org.outfoxedfinal.entity.Suspect;
import org.outfoxedfinal.entity.Thief;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.outfoxedfinal.logic.DiceController;

public class GameController {
    private final GameMap gameMap;
    private final KeyHandler keyHandler;
    private int selectedSuspectsCount = 0;
    private boolean movementMode = false;
    private final DiceController diceController;

    public GameController(GameMap gameMap, KeyHandler keyHandler,DiceController diceController) {
        this.gameMap = gameMap;
        this.keyHandler = keyHandler;
        this.diceController = diceController;
        keyHandler.setGameController(this);
        diceController.setGameController(this);
        Thief thief = new Thief();
        thief.selectThief(gameMap.getSuspects());
        gameMap.setThief(thief.getThief()); // Pass the selected thief to the GameMap
    }

    public void handleKeyPress(Scene scene) {
        scene.setOnKeyPressed(event -> {
            System.out.println("Key event detected: " + event.getCode());
            if (movementMode) {
                keyHandler.handleMovement(event); // Delegate to KeyHandler
            }
        });
    }


    public void showActionPrompt() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choose an Action");
        alert.setHeaderText("What would you like to do?");
        alert.setContentText("Choose your next action:");
        alert.getButtonTypes().setAll(
                new ButtonType("Reveal Suspect"),
                new ButtonType("Find Clue"),
                ButtonType.CANCEL
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            String choice = result.get().getText();
            if (choice.equals("Reveal Suspect")) {
                movementMode = false; // Disable movement
                keyHandler.setMovementEnabled(false); // Sync with KeyHandler
                gameMap.setSuspectCardsEnabled(true); // Enable suspect cards
                showDiceGUI("reveal suspect");
            } else if (choice.equals("Find Clue")) {
                movementMode = true; // Enable movement
                showDiceGUI("find clue");

            }
        }
    }

    public void resetSelection() {
        selectedSuspectsCount = 0; // Reset the count
        System.out.println("Selection reset. Prompting for next action...");
        showActionPrompt(); // Show the action prompt again
    }

    public void onSuspectSelected(Text card, Suspect suspect) {
        if (suspect.isRevealed()) {
            System.out.println("This suspect is already revealed.");
            return;
        }

        if (selectedSuspectsCount < 2 && !movementMode) {
            card.setText(suspect.getName()); // Reveal suspect name
            suspect.setRevealed(true); // Mark suspect as revealed
            selectedSuspectsCount++;

            if (selectedSuspectsCount == 2) {
                resetSelection(); // Reset after two selections
            }
        }
    }

    public void returnToGameScene() {
        Platform.runLater(() -> {
            try {
                if (gameMap.getMapGrid() == null) {
                    System.err.println("GridPane from gameMap is null. Cannot return to game scene.");
                    return;
                }

                Scene gameScene = gameMap.getMapGrid().getScene();
                if (gameScene == null) {
                    System.err.println("Game scene is not attached to the GridPane.");
                    return;
                }

                Stage primaryStage = (Stage) gameScene.getWindow();
                if (primaryStage == null) {
                    System.err.println("Primary stage is not set for the game scene.");
                    return;
                }

                primaryStage.setScene(gameScene);
                // handleKeyPress(gameScene); // Reattach key handler
                primaryStage.requestFocus(); // Ensure window focus
            } catch (NullPointerException e) {
                System.err.println("Error in returnToGameScene: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


//    protected void checkForClueEncounter(int currentRow, int currentCol) {
//        for (int[] clueLocation : gameMap.getClueLocations()) {
//            if (clueLocation[0] == currentRow && clueLocation[1] == currentCol) {
//                System.out.println("Clue found at (" + currentRow + ", " + currentCol + ")");
//                showClueSelectionScene();
//                break;
//            }
//        }
//    }

    public void accuseSuspect(Suspect suspect) {
        if (!suspect.isRevealed()) {
            System.out.println("You can only accuse revealed suspects!");
            return;
        }

        if (gameMap.getThief().equals(suspect)) {
            System.out.println("Correct! " + suspect.getName() + " is the thief!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, suspect.getName() + " is the thief!", ButtonType.OK);
            alert.showAndWait();
        } else {
            System.out.println("Incorrect accusation! " + suspect.getName() + " is not the thief.");
            Alert alert = new Alert(Alert.AlertType.ERROR, suspect.getName() + " is not the thief.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void showDiceGUI(String action) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dice.fxml"));
            Parent root = loader.load();

            DiceController diceController = loader.getController();
            diceController.setAction(action);
            diceController.setGameController(this);
            Stage stage = new Stage();
            stage.setTitle("Roll the Dice");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleDiceRollResult(int moves) {
        System.out.println("Dice roll result: " + moves);
        if (moves > 0) {
            System.out.println("Dice roll successful. Enabling movement with " + moves + " moves.");
            movementMode = true;
            keyHandler.setMovementEnabled(true);
            keyHandler.setRemainingMoves(moves);
        } else {
            System.out.println("Dice roll failed. No moves available.");
        }
    }
}

