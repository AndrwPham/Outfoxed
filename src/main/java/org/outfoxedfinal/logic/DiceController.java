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
    private String[] validDice = {"dice1.jpg", "dice2.jpg", "dice5.jpg"}; // Valid dice for findingClues

    private int rollCount = 0; // Counter to track the number of rolls
    private static final int MAX_ROLLS = 3; // Maximum allowed rolls

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
                if (isValidRoll()) {
                    totalMoves = calculateMoves();
                    System.out.println("Valid roll! Total moves: " + totalMoves);
                    gameController.handleDiceRollResult(totalMoves);
                } else {
                    System.out.println("Invalid roll. Roll failed.");
                }
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

    private boolean isDiceValid(ImageView diceView) {
        String imageName = new File(diceView.getImage().getUrl()).getName();
        for (String valid : validDice) {
            if (imageName.equals(valid)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidRoll() {
        return isDiceValid(diceImage) && isDiceValid(diceImage1) && isDiceValid(diceImage2);
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
        if ("dice1.jpg".equals(imageName)) return 100;
        if ("dice2.jpg".equals(imageName)) return 200;
        if ("dice5.jpg".equals(imageName)) return 100;
        return 0;
    }

    private void rollDie(ImageView diceView) {
        int diceValue = random.nextInt(6) + 1;
        File file = new File(DICE_PATH + "dice" + diceValue + ".jpg");
        diceView.setImage(new Image(file.toURI().toString()));
    }
}