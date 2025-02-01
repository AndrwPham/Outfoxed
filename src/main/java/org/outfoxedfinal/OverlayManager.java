package org.outfoxedfinal;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.geometry.Pos;

public class OverlayManager {
    private final StackPane overlayContainer; // Container for overlays
    private StackPane currentOverlay; // Stores the active overlay

    public OverlayManager(StackPane overlayContainer) {
        this.overlayContainer = overlayContainer;
    }

    /**
     * Creates and adds an overlay with the given content.
     * @param content The UI component to display inside the overlay.
     * @return The created overlay StackPane.
     */
    public StackPane createOverlay(Parent content) {
        // Remove any existing overlay before adding a new one
        removeOverlay();

        // Create overlay with a semi-transparent background
        currentOverlay = new StackPane();
        currentOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        currentOverlay.setPrefSize(900, 750);

        // Center the content inside the overlay
        currentOverlay.getChildren().add(content);
        StackPane.setAlignment(content, Pos.CENTER);

        // Add overlay to the main container
        overlayContainer.getChildren().add(currentOverlay);
        content.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                removeOverlay();
                System.out.println("Overlay removed using ESC key.");
            }
        });

        // Ensure the overlay can receive key events
        content.requestFocus();
        return currentOverlay; // ✅ Now returning the overlay so it can be stored in variables
    }

    /**
     * Removes the current overlay from the screen.
     */
    public void removeOverlay() {
        if (overlayContainer != null && currentOverlay != null) {
            overlayContainer.getChildren().remove(currentOverlay);
            currentOverlay = null;
        }
    }
    public StackPane getOverlayContainer() {
        return overlayContainer; // Assuming overlayContainer is the StackPane managing overlays
    }
}


