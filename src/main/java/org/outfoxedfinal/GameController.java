package org.outfoxedfinal;


import javafx.application.Platform;
import javafx.stage.Modality;
import org.outfoxedfinal.entity.Suspect;
import org.outfoxedfinal.entity.Thief;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.outfoxedfinal.logic.Decoder;
import org.outfoxedfinal.logic.DiceController;

public class GameController {
    private final GameMap gameMap;
    private final KeyHandler keyHandler;
    private int selectedSuspectsCount = 0;
    private boolean selectMode = false;
    private final DiceController diceController;
    private Thief thief;
    private int move = -1;
    public boolean rollingDone = false;
    private boolean movingDone = false;

    public GameController(GameMap gameMap, KeyHandler keyHandler,DiceController diceController) {
        this.gameMap = gameMap;
        this.keyHandler = keyHandler;
        this.diceController = diceController;
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Choose an Action");
            alert.setHeaderText("What would you like to do?");
            alert.setContentText("Choose your next action:");
            alert.getButtonTypes().setAll(
                    new ButtonType("Reveal Suspect"),
                    new ButtonType("Find Clue"),
                    ButtonType.CANCEL
            );

            alert.showAndWait().ifPresent(result -> {
                String choice = result.getText();
                if (choice.equals("Reveal Suspect")) {
                    showDiceGUI("reveal suspect");
                } else if (choice.equals("Find Clue")) {
                    showDiceGUI("find clue");
                }
            });
        });
    }

    public void resetSelection() {
        selectedSuspectsCount = 2; // Reset the count
        System.out.println("Selection reset. Prompting for next action...");
        selectMode = true;
        //showActionPrompt(); // Show the action prompt again
    }

    public void onSuspectSelected(Text card, Suspect suspect) {
        if (suspect.isRevealed()) {
            System.out.println("This suspect is already revealed.");
            return;
        }

        if (selectedSuspectsCount < 4 && !selectMode) {
            card.setText(suspect.getName()); // Reveal suspect name
            suspect.setRevealed(true); // Mark suspect as revealed
            selectedSuspectsCount++;

        }
    }


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
    public void updateGameLogic() {
        if (!actionPrompt && selectedSuspectsCount == 2) {
            actionPrompt = true;
            showActionPrompt();
        }
        if (selectedSuspectsCount == 4){
           // gameMap.setSuspectCardsEnabled(false);
            actionPrompt = false;
            resetSelection();
        }
        if (rollingDone || movingDone) {
            actionPrompt = false;
            rollingDone = false;
            movingDone = false;
        }
    }
}

