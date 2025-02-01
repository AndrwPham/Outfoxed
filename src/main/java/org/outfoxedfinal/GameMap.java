package org.outfoxedfinal;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.outfoxedfinal.entity.Suspect;
import org.outfoxedfinal.entity.SuspectInitializer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.outfoxedfinal.logic.Clue;

import java.util.*;

//This is the logic behind everything in the map

public class GameMap extends TilePane {
    private ImageView foxImageView;
    private final Clue clue;
    private final int rows;
    private final int cols;
    private final GridPane mapGrid;
    private final List<Suspect> suspects;
    private GameController gameController;
    private Suspect thief;

    private Image cardBack;
    private Map<String, Image> suspectImages = new HashMap<>();
    private Map<String, ImageView> imageViewMap = new HashMap<>();

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.mapGrid = new GridPane();
        this.suspects = SuspectInitializer.initializeSuspects();
        this.clue = new Clue(); // Initialize Clue object
        initializeMap();
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


    public List<Suspect> getSuspects() {
        return this.suspects;
    }

    // Check if a specific position has a clue
    public boolean isClueLocation(int row, int col) {
        return clue.isClueLocation(row, col);
    }

    public List<String> getClueItemsAtLocation(int row, int col) {
        return clue.getClueItemsAtLocation(row, col);
    }

    public void deactivateClue(int row, int col) {
        clue.deactivateClue(row, col);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public VBox getLeftSuspectCards(boolean isEnabled) {
        VBox left = new VBox(20);
        for (int i = 0; i < 6; i++) {
            left.getChildren().add(createSuspectCard(suspects.get(i), isEnabled,true));
        }
        return left;
    }

    public VBox getRightSuspectCards(boolean isEnabled) {
        VBox right = new VBox(20);
        for (int i = 6; i < 12; i++) {
            right.getChildren().add(createSuspectCard(suspects.get(i), isEnabled,false ));
        }
        return right;
    }

    public HBox createSuspectCard(Suspect suspect, boolean isEnabled, boolean isLeftSide) {
        // Create ImageView for suspect card
        ImageView suspectImage = new ImageView();

        // Function to update the suspect image dynamically
        Runnable updateImage = () -> {
            String imagePath = suspect.isRevealed()
                    ? "suspects/" + suspect.getName().toLowerCase() + ".png"
                    : "suspects/suspect.png";

            Image image = new Image(getClass().getResourceAsStream(imagePath));
            suspectImage.setImage(image);
        };

        // Load initial image
        updateImage.run();
        suspectImage.setFitWidth(50); // Adjust width
        suspectImage.setFitHeight(100); // Adjust height

        // Create suspect name text
        Text cardText = new Text(suspect.isRevealed() ? suspect.getName() : "");
        cardText.setFont(new Font(12));

        // Arrange elements (Text on left or right)
        HBox cardLayout = new HBox(10); // Horizontal spacing
        if (isLeftSide) {
            cardLayout.getChildren().addAll(suspectImage, cardText); // Text on the right
            cardLayout.setAlignment(Pos.CENTER_LEFT);
        } else {
            cardLayout.getChildren().addAll(cardText, suspectImage); // Text on the left
            cardLayout.setAlignment(Pos.CENTER_RIGHT);
        }
        suspectImage.setOnMouseClicked(event -> {
            if (!suspect.isRevealed() && isEnabled) {
                if (gameController.getSelectedSuspectsCount() < 2) {
                    gameController.onSuspectSelected(cardText, suspect);
                    suspect.setRevealed(true);
                    updateImage.run(); // Refresh image
                    cardText.setText(suspect.getName());
                } else {
                    System.out.println("Only two suspects can be revealed!");
                }
            } else {
                showZoomedImage(suspectImage); // Zoom in when revealed
            }
        });

        return cardLayout;
    }

    private void showZoomedImage(ImageView suspectImage) {
        Stage zoomStage = new Stage();
        zoomStage.initModality(Modality.APPLICATION_MODAL);
        zoomStage.setTitle("Suspect Zoomed View");

        // Clone image and enlarge
        ImageView zoomedImage = new ImageView(suspectImage.getImage());
        zoomedImage.setFitWidth(300); // Zoomed size
        zoomedImage.setFitHeight(450);

        StackPane root = new StackPane(zoomedImage);
        Scene scene = new Scene(root, 320, 480);

        zoomStage.setScene(scene);
        zoomStage.show();
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

    public void setFoxImageView(ImageView foxImageView) {
        this.foxImageView = foxImageView;
    }

    public ImageView getFoxImageView() {
        return foxImageView;
    }

    public void updateFoxPosition(double xOffset, double yOffset) {
        if (foxImageView != null) {
            foxImageView.setTranslateX(xOffset - 650 / 2);
            foxImageView.setTranslateY(yOffset - 650 / 2);
        }
    }

}
