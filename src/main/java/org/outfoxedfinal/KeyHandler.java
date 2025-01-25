package org.outfoxedfinal;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

class KeyHandler {
    private final Text character;
    private final GameMap gameMap;
    private int currentRow = 9;
    private int currentCol = 9;
    private GameController gameController;
    private boolean movementEnabled = false;
    private int remainingMoves = 0;

    public KeyHandler(Text character, GameMap gameMap) {
        this.character = character;
        this.gameMap = gameMap;
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
                if (currentRow > 0) {
                    currentRow--;
                    moved = true;
                }
            }
            case S -> {
                if (currentRow < gameMap.getRows() - 1) {
                    currentRow++;
                    moved = true;
                }
            }
            case A -> {
                if (currentCol > 0) {
                    currentCol--;
                    moved = true;
                }
            }
            case D -> {
                if (currentCol < gameMap.getCols() - 1) {
                    currentCol++;
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
        double cellSize = 35; // Size of each grid cell
        character.setTranslateX((currentCol * cellSize) - ((gameMap.getCols() * cellSize) / 2) + (cellSize / 2));
        character.setTranslateY((currentRow * cellSize) - ((gameMap.getRows() * cellSize) / 2) + (cellSize / 2));
        System.out.println("Character moved to row " + currentRow + ", column " + currentCol);
    }
    private void checkForClueEncounter() {
        if (gameMap.isClueLocation(currentRow, currentCol)) {
            System.out.println("Clue encountered at row " + currentRow + ", column " + currentCol);
            if (gameController != null) {
                gameController.onClueEncounter(currentRow, currentCol);
            }
        }
    }

}


