package org.outfoxedfinal;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Loading {
    private Stage primaryStage;
    private GamePanel preloadedGamePanel;
    private Scene preloadedGameScene;
    private boolean assetsLoaded = false; // Track loading status
    private ImageView bg; // GIF Background
    private int numPlayers;

    public Loading(Stage primaryStage,int numPlayers) {
        this.primaryStage = primaryStage;
        this.numPlayers = numPlayers;
    }

    public Parent showLoadingScene() {
        Pane pane = new Pane();
        pane.setPrefSize(900, 750);

        // Load GIF Image
        bg = new ImageView(new Image(getClass().getResource("loading1.gif").toExternalForm()));
        bg.setFitWidth(900);
        bg.setFitHeight(750);

        pane.getChildren().addAll(bg);

        System.out.println("Loading scene displayed...");

        // Start preloading assets asynchronously
        preloadGameAssets();

        return pane;
    }

    private void preloadGameAssets() {
        Task<Void> loadingTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Preloading game assets...");

                // Simulate heavy loading process (replace with actual game loading logic)
                for (int i = 0; i < 200; i++) {
                    Thread.sleep(50); // Simulate asset loading
                    updateProgress(i + 1, 200);
                }

                // **Create GamePanel in the Background**
                preloadedGamePanel = new GamePanel(numPlayers);

                // **Create Scene BEFORE switching** (this prevents delay when switching)
                preloadedGameScene = preloadedGamePanel.createScene();

                assetsLoaded = true; // Mark assets as loaded
                System.out.println("GamePanel and Scene preloaded successfully!");

                return null;
            }
        };

        // Once loading is complete, switch to the game scene
        loadingTask.setOnSucceeded(event -> Platform.runLater(this::showGamePanel));

        // Run the task in a background thread
        Thread thread = new Thread(loadingTask);
        thread.setDaemon(true); // Ensure it exits when the application closes
        thread.start();
    }


    private void showGamePanel() {
        if (assetsLoaded && preloadedGameScene != null) {
            System.out.println("Switching to GamePanel...");
            Platform.runLater(() -> primaryStage.setScene(preloadedGameScene)); // Switch scene instantly
        } else {
            System.out.println("Error: GamePanel or Scene was not preloaded correctly.");
        }
    }
}
