package org.outfoxedfinal;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.outfoxedfinal.entity.Suspect;
import org.outfoxedfinal.entity.SuspectInitializer;

import java.util.ArrayList;
import java.util.List;

//This is the logic behind everything in the map

public class GameMap {
    private final List<int[]> clueLocations;
    private final int rows;
    private final int cols;
    private final GridPane mapGrid;
    private final List<Suspect> suspects;
    private GameController gameController;
    private Suspect thief;



    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.mapGrid = new GridPane();
        this.suspects = SuspectInitializer.initializeSuspects();
        this.clueLocations = new ArrayList<>();
        initializeMap();
        initializeClues();
    }


    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        System.out.println("GameController assigned to GameMap.");
        //recreateSuspectCards();
    }


    public void setThief(Suspect thief) {
        this.thief = thief;
    }

    public Suspect getThief() {
        return thief;
    }

    public void recreateSuspectCards() {
       // setSuspectCardsEnabled(false); // Reset all cards to be non-clickable
    }

    public List<Suspect> getSuspects() {
        return this.suspects;
    }

    private void initializeClues() {
        // Example: Add clues to specific locations
        clueLocations.add(new int[]{2, 15});
        clueLocations.add(new int[]{11, 11});
        clueLocations.add(new int[]{7, 13});
        clueLocations.add(new int[]{12, 7});
        clueLocations.add(new int[]{11, 3});
        clueLocations.add(new int[]{15, 3});
        clueLocations.add(new int[]{15, 10});
        clueLocations.add(new int[]{13, 14});
        clueLocations.add(new int[]{3, 11});
        clueLocations.add(new int[]{6, 6});
        clueLocations.add(new int[]{5, 2});
        clueLocations.add(new int[]{2, 4});
    }

    public List<int[]> getClueLocations() {
        return clueLocations;
    }

    public boolean isClueLocation(int row, int col) {
        return clueLocations.stream().anyMatch(loc -> loc[0] == row && loc[1] == col);
    }

    public GridPane getMapGrid() {
        return mapGrid;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public HBox getTopSuspectCards(boolean isEnabled) {
        HBox top = new HBox(20);
        for (int i = 0; i < 4; i++) {
            top.getChildren().add(createSuspectCard(suspects.get(i), isEnabled));
        }
        return top;
    }

    public VBox getLeftSuspectCards(boolean isEnabled) {
        VBox left = new VBox(20);
        for (int i = 4; i < 8; i++) {
            left.getChildren().add(createSuspectCard(suspects.get(i), isEnabled));
        }
        return left;
    }

    public VBox getRightSuspectCards(boolean isEnabled) {
        VBox right = new VBox(20);
        for (int i = 8; i < 12; i++) {
            right.getChildren().add(createSuspectCard(suspects.get(i), isEnabled));
        }
        return right;
    }

    public Text createSuspectCard(Suspect suspect, boolean isEnabled) {
        Text card = new Text(suspect.isRevealed() ? suspect.getName() : "Suspect");
        card.setFont(new Font(20));

        // Set appearance and functionality based on the isEnabled state
        if (isEnabled && !suspect.isRevealed()) {
            card.setStyle("-fx-fill: black; -fx-opacity: 1;"); // Normal appearance
            card.setOnMouseClicked(event -> {
                gameController.onSuspectSelected(card, suspect); // Use onSuspectSelected
            });
        } else {
            card.setStyle("-fx-fill: gray; -fx-opacity: 0.5;"); // Disabled appearance
            card.setOnMouseClicked(null); // Remove click listener for disabled cards
        }

        return card;
    }



    private void initializeMap() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Rectangle cell = new Rectangle(30, 30);
                cell.setFill(Color.LIGHTGRAY);
                cell.setStroke(Color.BLACK);
                mapGrid.add(cell, col, row);
            }
        }
    }

    public List<String> getClueItemsAtLocation(int row, int col) {
        // Example logic: return all items at the location
        // Replace with your own logic to get the clue items for the specified location
        return List.of("umbrella", "gloves", "hat","glasses","1 eye glasses",
                "scarf","clock","stick","jewelry","bag","flower","cloak"); // Example items
    }

}
