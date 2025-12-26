package minesweeper;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class is responsible for creating and organizing visual elements and delegating logical updates to MSGame.
 */
public class PaneOrganizer {
    private BorderPane root;
    private VBox topBox;
    private HBox topPanel;
    private GridPane gridPane;
    private Label mineCounter;
    private Label timerLabel;
    private Button faceButton;
    private Label sessionBestLabel;
    private Label allTimeBestLabel;
    /**
     * This method is the constructor and it initializes instance variables and delegates to other methods to set up
     * the menu and modes.
     */
    public PaneOrganizer(MSGame gameManager) {
        this.root = new BorderPane();
        this.makeTopMenu(gameManager);
        this.setupSinglePlayer();
        this.topBox.getChildren().add(this.topPanel);
        this.root.setCenter(this.gridPane);
        this.startSinglePlayer(gameManager, Constants.EASY_ROWS, Constants.EASY_COLS, Constants.EASY_MINES,
                false);
    }

    /**
     * This method sets up the menu at the top by styling it and adding it to the top of the root node.
     */
    private void makeTopMenu(MSGame gameManager) {
        this.topBox = new VBox();
        this.topBox.setStyle("-fx-background-color: #C0C0C0;");
        MenuBar menuBar = this.makeMenu(gameManager);
        this.topBox.getChildren().add(menuBar);
        this.root.setTop(this.topBox);
    }

    /**
     * This method sets up the menu with all options of modes and quit button, linking them up to what should
     * happen when each button is clicked on, as well as the hint button.
     */
    private MenuBar makeMenu(MSGame gameManager) {
        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Game");
        MenuItem easyItem = new MenuItem("Easy (9x9)");
        MenuItem mediumItem = new MenuItem("Medium (16x16)");
        MenuItem hardItem = new MenuItem("Hard (16x30)");
        MenuItem tutorialItem = new MenuItem("Tutorial (Autoplay)");
        MenuItem versusItem = new MenuItem("Versus (Player vs AI)");
        MenuItem quitItem = new MenuItem("Quit");
        easyItem.setOnAction((ActionEvent e) -> {
            this.switchToSinglePlayer();
            this.startSinglePlayer(gameManager, Constants.EASY_ROWS, Constants.EASY_COLS, Constants.EASY_MINES,
                    false);
        });
        mediumItem.setOnAction((ActionEvent e) -> {
            this.switchToSinglePlayer();
            this.startSinglePlayer(gameManager, Constants.MEDIUM_ROWS, Constants.MEDIUM_COLS, Constants.MEDIUM_MINES,
                    false);
        });
        hardItem.setOnAction((ActionEvent e) -> {
            this.switchToSinglePlayer();
            this.startSinglePlayer(gameManager, Constants.HARD_ROWS, Constants.HARD_COLS, Constants.HARD_MINES,
                    false);
        });
        tutorialItem.setOnAction((ActionEvent e) -> {
            this.switchToSinglePlayer();
            this.startSinglePlayer(gameManager, Constants.EASY_ROWS, Constants.EASY_COLS, Constants.EASY_MINES,
                    true);
        });
        versusItem.setOnAction((ActionEvent e) -> this.startVersus(gameManager));
        quitItem.setOnAction((ActionEvent e) -> System.exit(0));
        gameMenu.getItems().addAll(easyItem, mediumItem, hardItem, tutorialItem, versusItem, quitItem);
        Menu optionsMenu = new Menu("Options");
        CheckMenuItem aiHintsItem = new CheckMenuItem("Enable AI Hints");
        aiHintsItem.setOnAction((ActionEvent e) -> gameManager.enableHints(aiHintsItem.isSelected()));
        optionsMenu.getItems().add(aiHintsItem);
        menuBar.getMenus().addAll(gameMenu, optionsMenu);
        return menuBar;
    }
    /**
     * This method sets up the single player interface with the mines and timer displays, grid for the minesweeper
     * game, and displays for the best time of the session/all time.
     */
    private void setupSinglePlayer() {
        this.topPanel = new HBox(20);
        this.topPanel.setAlignment(Pos.CENTER);
        this.topPanel.setStyle("-fx-background-color: #C0C0C0; -fx-padding: 10;");
        this.mineCounter = new Label("000");
        this.mineCounter.setStyle(
                "-fx-background-color: black; -fx-text-fill: red; " +
                        "-fx-font-family: 'Courier New'; -fx-font-size: 24px; " +
                        "-fx-padding: 5; -fx-font-weight: bold;");
        this.faceButton = new Button("🙂");
        this.faceButton.setStyle("-fx-font-size: 24px;");
        this.faceButton.setFocusTraversable(false);
        this.timerLabel = new Label("000");
        this.timerLabel.setStyle(
                "-fx-background-color: black; -fx-text-fill: red; " +
                        "-fx-font-family: 'Courier New'; -fx-font-size: 24px; " +
                        "-fx-padding: 5; -fx-font-weight: bold;");
        this.topPanel.getChildren().addAll(this.mineCounter, this.faceButton, this.timerLabel);
        HBox bottomPanel = new HBox(30);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #C0C0C0; -fx-padding: 5;");
        VBox sessionBox = new VBox(2);
        sessionBox.setAlignment(Pos.CENTER);
        Label sessionTitle = new Label("Session Best");
        sessionTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #404040;");
        this.sessionBestLabel = new Label("---");
        this.sessionBestLabel.setStyle(
                "-fx-background-color: black; -fx-text-fill: #00FF00; " +
                        "-fx-font-family: 'Courier New'; -fx-font-size: 18px; " +
                        "-fx-padding: 3; -fx-font-weight: bold;");
        sessionBox.getChildren().addAll(sessionTitle, this.sessionBestLabel);
        VBox allTimeBox = new VBox(2);
        allTimeBox.setAlignment(Pos.CENTER);
        Label allTimeTitle = new Label("All-Time Best");
        allTimeTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #404040;");
        this.allTimeBestLabel = new Label("---");
        this.allTimeBestLabel.setStyle(
                "-fx-background-color: black; -fx-text-fill: #FFD700; " +
                        "-fx-font-family: 'Courier New'; -fx-font-size: 18px; " +
                        "-fx-padding: 3; -fx-font-weight: bold;");
        allTimeBox.getChildren().addAll(allTimeTitle, this.allTimeBestLabel);
        bottomPanel.getChildren().addAll(sessionBox, allTimeBox);
        VBox mainPanel = new VBox(5);
        mainPanel.getChildren().addAll(this.topPanel, bottomPanel);
        this.topPanel = (HBox) mainPanel.getChildren().get(0);
        this.gridPane = new GridPane();
        this.gridPane.setStyle("-fx-background-color: #C0C0C0; -fx-padding: 5;");
        HBox combinedPanel = new HBox();
        combinedPanel.getChildren().addAll(this.topPanel, bottomPanel);
        this.topPanel = combinedPanel;
    }
    /**
     * This method starts a single–player board or tutorial with the given size and mine count in the parameters. If
     * isTutorial is true, it starts an autoplay tutorial.
     */
    private void startSinglePlayer(MSGame gameManager,
                                   int rows, int cols, int mines,
                                   boolean isTutorial) {
        this.gridPane.getChildren().clear();
        Pane[][] cellPanes = new Pane[rows][cols];
        Text[][] cellTexts = new Text[rows][cols];
        this.makeCellGrid(this.gridPane, rows, cols, cellPanes, cellTexts);
        if (isTutorial) {
            gameManager.startTutorial(rows, cols, mines, cellPanes, cellTexts,
                    this.mineCounter, this.timerLabel, this.faceButton);
        }
        else {
            MSGame activeGame = gameManager.startGame(rows, cols, mines, cellPanes, cellTexts,
                    this.mineCounter, this.timerLabel, this.faceButton);
            this.handleClicks(cellPanes, rows, cols, activeGame, gameManager);
            this.updateBestScoreDisplay(gameManager);
        }
    }

    /**
     * This method initiates the minesweeper game and overall UI according to the constants for dimensions for
     * versus mode.
     */
    private void startVersus(MSGame gameManager) {
        if (this.topBox.getChildren().size() > 1) {
            this.topBox.getChildren().remove(1, this.topBox.getChildren().size());
        }
        HBox boardsBox = new HBox(20);
        boardsBox.setPadding(new Insets(10));
        boardsBox.setAlignment(Pos.CENTER);
        VBox playerBox = this.makeUI("You");
        VBox aiBox = this.makeUI("AI");
        boardsBox.getChildren().addAll(playerBox, aiBox);
        this.root.setCenter(boardsBox);
        int rows = Constants.EASY_ROWS;
        int cols = Constants.EASY_COLS;
        int mines = Constants.EASY_MINES;
        GridPane playerGrid = (GridPane) playerBox.getChildren().get(2);
        GridPane aiGrid = (GridPane) aiBox.getChildren().get(2);
        Pane[][] playerPanes = new Pane[rows][cols];
        Text[][] playerTexts = new Text[rows][cols];
        Pane[][] aiPanes = new Pane[rows][cols];
        Text[][] aiTexts = new Text[rows][cols];
        this.makeCellGrid(playerGrid, rows, cols, playerPanes, playerTexts);
        this.makeCellGrid(aiGrid, rows, cols, aiPanes, aiTexts);
        MSGame[] games = gameManager.startVersus(rows, cols, mines, playerBox, aiBox,
                playerPanes, playerTexts, aiPanes, aiTexts);
        if (games != null && games[0] != null) {
            this.handleClicks(playerPanes, rows, cols, games[0], gameManager);
        }
    }
    /**
     * This method initializes the gridPane of cells that the user clicks and interacts with.
     */
    private void makeCellGrid(GridPane gridPane, int rows, int cols,
                              Pane[][] cellPanes, Text[][] cellTexts) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(Constants.CELL_SIZE, Constants.CELL_SIZE);
                pane.setMinSize(Constants.CELL_SIZE, Constants.CELL_SIZE);
                pane.setMaxSize(Constants.CELL_SIZE, Constants.CELL_SIZE);
                Text text = new Text("");
                text.setFill(Color.BLACK);
                text.setFont(Font.font("Courier New", 14));
                text.setX(4);
                text.setY(Constants.CELL_SIZE - 6);
                pane.getChildren().add(text);
                cellPanes[i][j] = pane;
                cellTexts[i][j] = text;
                gridPane.add(pane, j, i);
            }
        }
    }
    /**
     * This method calls for left and right clicks to be handled and is delegated to MSGame's methods when detected.
     */
    private void handleClicks(Pane[][] cellPanes, int rows, int cols,
                              MSGame activeGame, MSGame gameManager) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int row = i;
                int col = j;
                cellPanes[i][j].setOnMouseClicked((MouseEvent e) -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        activeGame.handleLeftClick(row, col);
                    }
                    else if (e.getButton() == MouseButton.SECONDARY) {
                        activeGame.handleRightClick(row, col);
                    }
                    if (!activeGame.isAutoplay() && activeGame.resetWin()) {
                        gameManager.recordWin(activeGame.getTime());
                        this.updateBestScoreDisplay(gameManager);
                    }
                });
            }
        }
    }
    /**
     * This method initializes the overall UI such as the labels, buttons, and grid.
     */
    private VBox makeUI(String title) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.TOP_CENTER);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-background-color: #C0C0C0; -fx-padding: 10;");
        Label counter = new Label("000");
        counter.setStyle(
                "-fx-background-color: black; -fx-text-fill: red; " +
                        "-fx-font-family: 'Courier New'; -fx-font-size: 24px; " +
                        "-fx-padding: 5; -fx-font-weight: bold;");
        Button face = new Button("🙂");
        face.setStyle("-fx-font-size: 24px;");
        face.setFocusTraversable(false);
        Label timer = new Label("000");
        timer.setStyle(
                "-fx-background-color: black; -fx-text-fill: red; " +
                        "-fx-font-family: 'Courier New'; -fx-font-size: 24px; " +
                        "-fx-padding: 5; -fx-font-weight: bold;");
        top.getChildren().addAll(counter, face, timer);
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #C0C0C0; -fx-padding: 5;");
        box.getChildren().addAll(titleLabel, top, grid);
        return box;
    }
    /**
     * This method switches back to single player by resetting conditions that are in modes like versus but not in
     * single player modes, such as resetting the amount of children in the topBox.
     */
    private void switchToSinglePlayer() {
        if (this.topBox.getChildren().size() > 1) {
            this.topBox.getChildren().remove(1, this.topBox.getChildren().size());
        }
        this.topBox.getChildren().add(this.topPanel);
        this.root.setCenter(this.gridPane);
    }
    /**
     * This method updates the display text that shows the best score of the session or all time, styling it so that
     * it fits into the 3 digit box and filled in with dashes if no score is registered yet.
     */
    public void updateBestScoreDisplay(MSGame gameManager) {
        int sessionBest = gameManager.getSessionBestTime();
        int allTimeBest = gameManager.getAllTimeBestTime();
        if (sessionBest < 999) {
            this.sessionBestLabel.setText(String.format("%03d", sessionBest));
        }
        else {
            this.sessionBestLabel.setText("---");
        }

        if (allTimeBest < 999) {
            this.allTimeBestLabel.setText(String.format("%03d", allTimeBest));
        }
        else {
            this.allTimeBestLabel.setText("---");
        }
    }
    /**
     * This getter method returns the root borderpane.
     */
    public BorderPane getRoot() {
        return this.root;
    }
}
