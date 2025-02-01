package org.outfoxedfinal.logic;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.outfoxedfinal.OverlayManager;
import org.outfoxedfinal.GameController;
import java.util.List;

public class Decoder {

    @FXML
    private ImageView clue1, clue2, clue3, clue4, clue5, clue6, clue7, clue8, clue9, clue10, clue11, clue12;

    private List<String> clueItems; // Clue items at the encountered location
    private List<String> thiefItems; // Items that belong to the thief
    private OverlayManager overlayManager; // Overlay manager instance
    private GameController gameController;
    private boolean decodeDone = false;

    @FXML
    public void initialize() {
        // Add event handlers to each ImageView
        ImageView[] clues = {clue1, clue2, clue3, clue4, clue5, clue6, clue7, clue8, clue9, clue10, clue11, clue12};

        for (int i = 0; i < clues.length; i++) {
            final int index = i;
            if (clues[i] != null) {
                clues[i].setOnMouseClicked(event -> handleClueClick(index));
            }
        }
    }

    public void setClueItems(List<String> clueItems) {
        this.clueItems = clueItems;
    }

    public void setThiefItems(List<String> thiefItems) {
        this.thiefItems = thiefItems;
    }

    public void setOverlayManager(OverlayManager overlayManager) {
        this.overlayManager = overlayManager;
    }
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    private void handleClueClick(int index) {
        if (clueItems == null || thiefItems == null || overlayManager == null) {
            System.out.println("❌ Error: Missing required data (clueItems/thiefItems/overlayManager). Ensure they are set before clicking a clue.");
            return;
        }
        if (index >= clueItems.size()) {
            System.out.println("❌ Error: Invalid clue selection index: " + index);
            return;
        }

        String selectedClue = clueItems.get(index);
        boolean thiefHasItem = thiefItems.contains(selectedClue);

        // Load Clue Image
        ImageView clueImageView = new ImageView();
        String imagePath =  selectedClue.toLowerCase().replace(" ", "") + ".png"; // Assuming clue images are stored here

        try {
            Image clueImage = new Image(getClass().getResource(imagePath).toExternalForm());
            clueImageView.setImage(clueImage);
        } catch (Exception e) {
            System.out.println("⚠ Warning: Missing image for clue '" + selectedClue.toLowerCase().replace(" ", "") + "', using default.");
            //clueImageView.setImage(new Image(getClass().getResource("/clues/default.png").toExternalForm())); // Fallback image
        }

        clueImageView.setFitWidth(150);
        clueImageView.setFitHeight(150);

        // Result Text
        Text resultText = new Text(thiefHasItem
                ? "✅ The thief has this item!"
                : "❌ The thief does not have this item.");
        resultText.setFont(new Font(22));
        resultText.setFill(Color.WHITE);

        // Close Button
        Button closeButton = new Button("Close");
        closeButton.setFont(new Font(16));
        closeButton.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: white; -fx-text-fill: black;"));
        closeButton.setOnAction(e -> {
            overlayManager.removeOverlay();
            gameController.isDecodeDone(true);
        }); // Remove overlay when closed

        // Layout for Clue Result
        VBox clueResultBox = new VBox(15, clueImageView, resultText, closeButton);
        clueResultBox.setAlignment(Pos.CENTER);
        clueResultBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20px; -fx-border-color: white; -fx-border-width: 2px;");

        // Show the overlay
        overlayManager.createOverlay(clueResultBox);
    }

}
