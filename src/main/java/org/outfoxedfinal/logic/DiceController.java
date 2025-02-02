package org.outfoxedfinal.logic;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.outfoxedfinal.GameController;

import java.io.File;
import java.util.Random;

public class DiceController {

    private static final String DICE_PATH = "src/main/resources/org/outfoxedfinal/dice/";
    private Random random = new Random();
    private String currentAction; // Current action set by GameController
    private String[] keepDice; // Keep dice for the current action
    private static int totalMoves;
    private GameController gameController;

    @FXML
    private ImageView diceImage;
    @FXML
    private ImageView diceImage1;
    @FXML
    private ImageView diceImage2;

    @FXML
    private Button rollButton;

    private boolean keepDice1 = false;
    private boolean keepDice2 = false;
    private boolean keepDice3 = false;
    private boolean actionDone = false;
    private boolean rollingDone = false;
    private ActionType rolling = null;


    private int rollCount = 0; // Counter to track the number of rolls
    private static final int MAX_ROLLS = 3; // Maximum allowed rolls

    public enum ActionType {
        ROLL
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setAction(String action) {
        this.currentAction = action;

        // Determine which dice to keep based on the action
        if ("find clue".equals(action)) {
            keepDice = new String[]{"dice1.jpg", "dice2.jpg", "dice5.jpg"};
        } else if ("reveal suspect".equals(action)) {
            keepDice = new String[]{"dice3.jpg", "dice4.jpg", "dice6.jpg"};
        } else {
            keepDice = new String[]{}; // Default: no dice to keep
        }

        // Reset the roll count for a new action
        rollCount = 0;
        rollButton.setDisable(false);
    }

    @FXML
    void roll() {
        rolling = ActionType.ROLL;
        if (rollCount >= MAX_ROLLS) {
            rollButton.setDisable(true); // Disable the button if max rolls are reached
            return;
        }

        rollButton.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < 15; i++) {
                    // Roll dice only if they are not flagged to be kept
                    if (!keepDice1) rollDie(diceImage);
                    if (!keepDice2) rollDie(diceImage1);
                    if (!keepDice3) rollDie(diceImage2);

                    Thread.sleep(50);

                    // After the last iteration, check if dice should be kept
                    if (i == 14) {
                        keepDice1 = shouldKeep(diceImage);
                        keepDice2 = shouldKeep(diceImage1);
                        keepDice3 = shouldKeep(diceImage2);
                    }
                }

                rollCount++; // Increment the roll count
                if (rollCount < MAX_ROLLS) {
                    rollButton.setDisable(false); // Re-enable the button if rolls remain
                } else {
                    rollButton.setDisable(true); // Disable button if max rolls reached
                }
                if (currentAction.equals("find clue")) {
                    if (isValidRoll()) {
                        rollCount = MAX_ROLLS;
                        totalMoves = calculateMoves();
                        System.out.println("Valid roll! Total moves: " + totalMoves);
                        gameController.handleDiceRollResult(totalMoves);
                        rollingDone = true;
                    } else if (rollCount == MAX_ROLLS) {
                        System.out.println("No more rolls");
                        actionDone = true;
                        rollingDone = true;
                    }
                }
                else if (currentAction.equals("reveal suspect")) {
                    if (isValidRoll()) {
                        rollCount = MAX_ROLLS;
                        gameController.resetSelection();
                       // gameController.isSelectMode(false);
                        rollingDone = true;
                        gameController.movingDone(0);   // Use movingDone to track if reveal suspect is done
                    }
                    else if (rollCount == MAX_ROLLS) {
                        actionDone = true;
                        rollingDone = true;
                    }
                }
                gameController.actionDone(actionDone);
                gameController.rollingDone(rollingDone);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    private boolean shouldKeep(ImageView diceView) {
        // Check if the dice image matches any in the keep list
        String imageName = new File(diceView.getImage().getUrl()).getName();
        for (String keep : keepDice) {
            if (imageName.equals(keep)) {
                return true; // Keep this dice
            }
        }
        return false; // Re-roll this dice
    }


    private boolean isValidRoll() {
        return shouldKeep(diceImage) && shouldKeep(diceImage1) && shouldKeep(diceImage2);
    }

    public int calculateMoves() {
        int moves = 0;
        moves += getMoveValue(diceImage);
        moves += getMoveValue(diceImage1);
        moves += getMoveValue(diceImage2);
        return moves;
    }

    private int getMoveValue(ImageView diceView) {
        String imageName = new File(diceView.getImage().getUrl()).getName();
        if ("dice1.jpg".equals(imageName)) return 1;
        if ("dice2.jpg".equals(imageName)) return 2;
        if ("dice5.jpg".equals(imageName)) return 1;
        return 0;
    }

    private void rollDie(ImageView diceView) {
        int diceValue = random.nextInt(6) + 1;
        File file = new File(DICE_PATH + "dice" + diceValue + ".jpg");
        diceView.setImage(new Image(file.toURI().toString()));
    }
}