package org.outfoxedfinal.logic;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import java.util.List;

public class Decoder {

    @FXML
    private ImageView clue1, clue2, clue3, clue4, clue5, clue6, clue7, clue8, clue9, clue10, clue11, clue12;

    private List<String> clueItems; // Clue items at the encountered location
    private List<String> thiefItems; // Items that belong to the thief

    @FXML
    public void initialize() {
        // Add event handlers to each ImageView
        clue1.setOnMouseClicked(event -> handleClueClick(0));
        clue2.setOnMouseClicked(event -> handleClueClick(1));
        clue3.setOnMouseClicked(event -> handleClueClick(2));
        clue4.setOnMouseClicked(event -> handleClueClick(3));
        clue5.setOnMouseClicked(event -> handleClueClick(4));
        clue6.setOnMouseClicked(event -> handleClueClick(5));
        clue7.setOnMouseClicked(event -> handleClueClick(6));
        clue8.setOnMouseClicked(event -> handleClueClick(7));
        clue9.setOnMouseClicked(event -> handleClueClick(8));
        clue10.setOnMouseClicked(event -> handleClueClick(9));
        clue11.setOnMouseClicked(event -> handleClueClick(10));
        clue12.setOnMouseClicked(event -> handleClueClick(11));
    }

    public void setClueItems(List<String> clueItems) {
        this.clueItems = clueItems;
    }

    public void setThiefItems(List<String> thiefItems) {
        this.thiefItems = thiefItems;
    }

    private void handleClueClick(int index) {
        if (index >= clueItems.size()) {
            return; // Ignore invalid clicks
        }

        String selectedClue = clueItems.get(index);
        boolean thiefHasItem = thiefItems.contains(selectedClue);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Clue Result");
        alert.setHeaderText("Clue: " + selectedClue);
        alert.setContentText(thiefHasItem ? "The thief has this item!" : "The thief does not have this item.");
        alert.showAndWait();
    }
}
