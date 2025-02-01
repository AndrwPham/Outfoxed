package org.outfoxedfinal;

import javafx.animation.FillTransition;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;


public class Main extends Application {
    private static Stage primaryStage;
    private Pane root;
    private StackPane overlayPane; // Overlay for instructions
    private VBox instructionOverlay;
    private VBox menuBox;
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    Parent createContent() {
        root = new Pane();
        root.setPrefSize(900, 750);

        Image bgImage = new Image(getClass().getResource("bg.gif").toExternalForm());
        ImageView img = new ImageView(bgImage);
        img.setFitWidth(900);
        img.setFitHeight(750);

        menuBox = new VBox(5, new MenuItem("   PLAY", this::showPlayerSelection),
                new MenuItem("   QUIT", Platform::exit));
        menuBox.setBackground(new Background(new BackgroundFill(Color.web("white", 0.5), null, null)));
        menuBox.setTranslateX(375);
        menuBox.setTranslateY(650);

        // **QUESTION MARK BUTTON**
        Label questionMark = new Label("?");
        questionMark.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 50));

        questionMark.setTextFill(Color.BLACK);
        questionMark.setStyle("-fx-cursor: hand;");
        questionMark.setOnMouseClicked(event -> toggleInstructions());

        // **Question Mark Positioning**
        questionMark.setTranslateX(820); // Adjust right alignment
        questionMark.setTranslateY(20);  // Adjust top alignment

        // **Overlay Pane (Initially Hidden)**
        overlayPane = new StackPane();
        overlayPane.setPrefSize(900, 750);
        overlayPane.setVisible(false);

        // **Instruction Overlay**
        instructionOverlay = createInstructionOverlay();
        overlayPane.getChildren().add(instructionOverlay);

        root.getChildren().addAll(img, menuBox, questionMark, overlayPane);
        return root;
    }

    private VBox createInstructionOverlay() {
        VBox overlay = new VBox(10);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(20));
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-border-color: white; -fx-border-width: 2px;");
        overlay.setPrefSize(300, 250); // Smaller overlay

        Text instructionTitle = new Text("📜 How to Play");
        instructionTitle.setFont(Font.font("Palatino Linotype", FontWeight.BOLD, 18));
        instructionTitle.setFill(Color.WHITE);

        Label instructionText = new Label(
                "🐾 The player who last ate a piece of pie goes first, and turns move left.\n\n" +

                        "🎲 **On Your Turn:**\n" +
                        " 1️. Declare 'Search for Clues' 🐾 or 'Reveal Suspects' 👀.\n" +
                        " 2️. Roll three dice to match your action.\n" +
                        " 3️. You have up to **three attempts** to roll all matching symbols.\n" +
                        " 4️. If successful, perform your action. Otherwise, the thief moves!\n\n" +

                        "🔎 **Search for Clues**:\n" +
                        " - Move based on footprints rolled.\n" +
                        " - If you land on a paw print, draw a clue.\n" +
                        " - Use the **Clue Decoder** to check the color:\n" +
                        "     ✅ Green → The thief **has** this item.\n" +
                        "     ❌ White → The thief **does not** have it.\n" +
                        " - Compare clues to revealed suspects and eliminate those proven innocent.\n\n" +

                        "🕵️ **Reveal Suspects**:\n" +
                        " - Flip two suspect cards.\n" +
                        " - Check if suspects wear **clues marked red**.\n" +
                        " - Eliminate suspects who **do not** match.\n\n" +

                        "🚨 **Failed Dice Rolls**:\n" +
                        " If your dice don’t match after three rolls, the **thief moves 3 spaces forward!**\n\n" +

                        "🎯 **Win Condition**:\n" +
                        " Work together to **find the thief before they escape!** 🦊"
        );
        instructionText.setWrapText(true);
        instructionText.setTextFill(Color.WHITE);
        instructionText.setFont(Font.font("Palatino Linotype", 14));

        Button closeButton = new Button("CLOSE");
        closeButton.setOnAction(e -> overlayPane.setVisible(false)); // Hide the overlay when clicked

        overlay.getChildren().addAll(instructionTitle, instructionText, closeButton);
        return overlay;
    }

    // **Toggle Instructions Overlay**
    private void toggleInstructions() {
        overlayPane.setVisible(!overlayPane.isVisible());
    }

    private static class MenuItem extends StackPane {
        MenuItem(String name, Runnable action) {
            LinearGradient gradient = new LinearGradient(
                    0,0.5,1,0.5,true, CycleMethod.NO_CYCLE,
                    new Stop(0.1, Color.web("white", 0.75)),
                    new Stop(1.0, Color.web("white", 0.15))
                    );
            Rectangle bg = new Rectangle(200,30, gradient);
            Rectangle bg1 = new Rectangle(200,30, Color.web("white", 0.2));

            FillTransition ft = new FillTransition(Duration.seconds(0.5),
                    bg1, Color.web("black", 0.2),Color.web("white", 0.2));
            ft.setAutoReverse(true);
            ft.setCycleCount(Integer.MAX_VALUE);

            hoverProperty().addListener((observable, oldValue, isHovering) -> {
                if (isHovering){
                    ft.playFromStart();
                }else{
                    ft.stop();
                    bg1.setFill(Color.web("black", 0.2));
                }
            });
            Rectangle line = new Rectangle(5,30);
            line.fillProperty().bind(Bindings.when(hoverProperty()).then(Color.RED).otherwise(Color.GRAY));

            Text text = new Text(name);
            text.setFont(Font.font("System", FontWeight.BOLD, 22)); // Bold font style

            setOnMouseClicked(e->action.run());
            setOnMousePressed(e-> bg.setFill(Color.LIGHTBLUE));

            HBox box = new HBox(40,line,text);
            box.setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(bg,bg1,box);
        }
    }

    private void showPlayerSelection() {
        // Remove existing PLAY button and replace it with player selection buttons
        menuBox.getChildren().clear(); // Clear existing menu items



        // Create new menu items for player selection
        menuBox = new VBox(5,new MenuItem("2 PLAYERS", () -> startGameWithPlayers(2)),
                                new MenuItem("3 PLAYERS", () -> startGameWithPlayers(3)),
                                new MenuItem("4 PLAYERS", () -> startGameWithPlayers(4)),
                                new MenuItem("BACK", this::showMainMenu));

        menuBox.setTranslateX(375);
        menuBox.setTranslateY(550);


        // Add new options to the menu
        menuBox.getChildren().addAll();
        root.getChildren().addAll(menuBox);
    }


    private void startGameWithPlayers(int numPlayers) {
        System.out.println("Starting game with " + numPlayers + " players...");
        overlayPane.setVisible(false); // Hide overlay
        Loading gameLoad = new Loading(primaryStage, numPlayers); // Pass player count to Loading
        Scene gameScene = new Scene(gameLoad.showLoadingScene());
        primaryStage.setScene(gameScene);
    }

    private void showMainMenu() {
        menuBox.getChildren().clear(); // Remove player selection options

        // Restore original buttons

        menuBox.getChildren().addAll(
                new MenuItem("   PLAY", this::showPlayerSelection),
                new MenuItem("   QUIT", Platform::exit)
        );
        menuBox.setTranslateX(375);
        menuBox.setTranslateY(650);
    }

    public static void returnToMainMenu() {
        Platform.runLater(() -> { // Ensures UI updates are done on the JavaFX Application Thread.
            try {
                Scene mainScene = new Scene(new Main().createContent()); // Reload Main Menu
                primaryStage.setScene(mainScene); // Switch to main menu
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}