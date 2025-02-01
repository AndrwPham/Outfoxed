package org.outfoxedfinal;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.util.List;

class KeyHandler {
    private final List<Text> players;
    private final GameMap gameMap;
    private int currentPlayerIndex = 0; // Tracks which player is active
    private int[] playerRows;
    private int[] playerCols;
    private boolean movementEnabled = false;
    private int remainingMoves = 0;
    private GameController gameController;

    public KeyHandler(List<Text> players, GameMap gameMap) {
        this.players = players;
        this.gameMap = gameMap;
        this.playerRows = new int[players.size()];
        this.playerCols = new int[players.size()];
        int[][] playerPositions = {{9, 9}, {8, 9}, {8, 8}, {9, 8}}; // Starting positions
        for (int i = 0; i < players.size(); i++) {
            playerRows[i] = playerPositions[i][0];
            playerCols[i] = playerPositions[i][1];
        }
        updateCharacterPosition();
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setMovementEnabled(boolean enabled) {
        this.movementEnabled = enabled;
        System.out.println("Movement enabled: " + enabled);
    }

    public void setRemainingMoves(int moves) {
        this.remainingMoves = moves;
        System.out.println("Remaining moves set to: " + moves);
    }

    public void handleMovement(KeyEvent event) {
        if (!movementEnabled) {
            System.out.println("Movement disabled.");
            return;
        }

        if (remainingMoves <= 0) {
            System.out.println("No remaining moves.");
            return;
        }

        boolean moved = false;
        switch (event.getCode()) {
            case W -> {
                if (playerRows[currentPlayerIndex] > 0) {
                    playerRows[currentPlayerIndex]--;
                    moved = true;
                }
            }
            case S -> {
                if (playerRows[currentPlayerIndex] < gameMap.getRows() - 1) {
                    playerRows[currentPlayerIndex]++;
                    moved = true;
                }
            }
            case A -> {
                if (playerCols[currentPlayerIndex] > 0) {
                    playerCols[currentPlayerIndex]--;
                    moved = true;
                }
            }
            case D -> {
                if (playerCols[currentPlayerIndex] < gameMap.getCols() - 1) {
                    playerCols[currentPlayerIndex]++;
                    moved = true;
                }
            }
            default -> System.out.println("Unhandled key: " + event.getCode());
        }


        if (moved) {
            remainingMoves--; // Decrease the remaining moves
            System.out.println("Moved. Remaining moves: " + remainingMoves);
            updateCharacterPosition();
            checkForClueEncounter();

            if (remainingMoves <= 0) {
                System.out.println("No more moves left. Movement disabled.");
                setMovementEnabled(false);// Disable movement if no moves remain
                gameController.movingDone(remainingMoves);
            }
        }
    }

    private void updateCharacterPosition() {
        double cellSize = 35; // Grid cell size
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setTranslateX((playerCols[i] * cellSize) - ((gameMap.getCols() * cellSize) / 2) + (cellSize / 2));
            players.get(i).setTranslateY((playerRows[i] * cellSize) - ((gameMap.getRows() * cellSize) / 2) + (cellSize / 2));
        }
        System.out.println("Player " + (currentPlayerIndex + 1) + " moved to row " + playerRows[currentPlayerIndex] + ", column " + playerCols[currentPlayerIndex]);
    }

    private void checkForClueEncounter() {
        int row = playerRows[currentPlayerIndex];
        int col = playerCols[currentPlayerIndex];

        if (gameMap.isClueLocation(row, col)) {
            System.out.println("Player " + (currentPlayerIndex + 1) + " found a clue at (" + row + ", " + col + ")");
            gameController.onClueEncounter(row, col);
            // Deactivate the clue so it disappears
            gameMap.deactivateClue(row, col);
        }
    }


    public void switchTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); // Cycle to the next player
        System.out.println("It's now Player " + (currentPlayerIndex + 1) + "'s turn!");
        //gameController.notifyPlayerTurn(currentPlayerIndex);
    }

}


