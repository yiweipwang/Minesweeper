package minesweeper;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class is the top level logic class and handles game instances, best score tracking and persistence, and game
 * mode switching logic.
 */
public class MSGame {
    private Board board;
    private Pane[][] cellPanes;
    private Text[][] cellTexts;
    private boolean[][] wasFlagged;
    private Label mineCounter;
    private Label timerLabel;
    private Button faceButton;
    private Timeline timer;
    private Timeline aiMoveTimeline;
    private int seconds;
    private int currentRows;
    private int currentCols;
    private int currentMines;
    private HintAI hintAI;
    private boolean aiHintsEnabled;
    private boolean autoPlay;
    private boolean gameWon;
    private MSGame playerGame;
    private MSGame aiGame;
    private Stage stage;
    private int sessionBestTime;
    private int allTimeBestTime;

    /**
     * This method is a constructor that sets certain default values when called in PaneOrganizer.java.
     */
    public MSGame(Stage stage) {
        this.stage = stage;
        this.aiHintsEnabled = false;
        this.sessionBestTime = 999;
        this.allTimeBestTime = this.loadAllTimeBest();
    }

    /**
     * This method is a constructor that initializes all necessary values when a game instance begins and is
     * controlled internally in this class.
     */
    private MSGame(int rows, int cols, int mines,
                   Pane[][] cellPanes,
                   Text[][] cellTexts,
                   Label mineCounter,
                   Label timerLabel,
                   Button faceButton,
                   boolean autoPlay) {
        this.currentRows = rows;
        this.currentCols = cols;
        this.currentMines = mines;
        this.cellPanes = cellPanes;
        this.cellTexts = cellTexts;
        this.mineCounter = mineCounter;
        this.timerLabel = timerLabel;
        this.faceButton = faceButton;
        this.autoPlay = autoPlay;
        this.seconds = 0;
        this.hintAI = new HintAI();
        this.aiHintsEnabled = false;
        this.wasFlagged = new boolean[rows][cols];
        this.gameWon = false;
        this.setupGame();
    }
    /**
     * This method starts a game by resetting all current games and then passing in necessary values for size and
     * number of mines, as well as panes and labels for visual updates.
     */
    public MSGame startGame(int rows, int cols, int mines,
                            Pane[][] cellPanes, Text[][] cellTexts,
                            Label mineCounter, Label timerLabel, Button faceButton) {
        this.stopAllGames();
        this.playerGame = new MSGame(rows, cols, mines, cellPanes, cellTexts,
                mineCounter, timerLabel, faceButton, false);
        this.playerGame.setHints(this.aiHintsEnabled);
        if (this.stage != null) {
            this.stage.sizeToScene();
        }
        return this.playerGame;
    }
    /**
     * This method starts a tutorial game by resetting all current games, passing in necessary values, and turning
     * on autoplay.
     */
    public void startTutorial(int rows, int cols, int mines,
                                Pane[][] cellPanes, Text[][] cellTexts,
                                Label mineCounter, Label timerLabel, Button faceButton) {
        this.stopAllGames();
        faceButton.setText("🤖");
        this.playerGame = new MSGame(rows, cols, mines, cellPanes, cellTexts,
                mineCounter, timerLabel, faceButton, true);
        this.playerGame.setHints(this.aiHintsEnabled);
        if (this.stage != null) {
            this.stage.sizeToScene();
        }
    }
    /**
     * This method starts a versus game by resetting all current games and starting two game instances side by side,
     * one allowing player input and the other being on auto play with no user input allowed besides resetting with
     * the face button.
     */
    public MSGame[] startVersus(int rows, int cols, int mines,
                                VBox playerBox, VBox aiBox,
                                Pane[][] playerPanes, Text[][] playerTexts,
                                Pane[][] aiPanes, Text[][] aiTexts) {
        this.stopAllGames();
        HBox playerTop = (HBox) playerBox.getChildren().get(1);
        Label playerMineCounter = (Label) playerTop.getChildren().get(0);
        Button playerFaceButton = (Button) playerTop.getChildren().get(1);
        Label playerTimerLabel = (Label) playerTop.getChildren().get(2);
        HBox aiTop = (HBox) aiBox.getChildren().get(1);
        Label aiMineCounter = (Label) aiTop.getChildren().get(0);
        Button aiFaceButton = (Button) aiTop.getChildren().get(1);
        Label aiTimerLabel = (Label) aiTop.getChildren().get(2);
        this.playerGame = new MSGame(rows, cols, mines,
                playerPanes, playerTexts, playerMineCounter, playerTimerLabel, playerFaceButton,
                false);
        this.aiGame = new MSGame(rows, cols, mines,
                aiPanes, aiTexts, aiMineCounter, aiTimerLabel, aiFaceButton,
                true);
        this.playerGame.setHints(this.aiHintsEnabled);
        this.aiGame.setHints(this.aiHintsEnabled);
        if (this.stage != null) {
            this.stage.sizeToScene();
        }
        return new MSGame[]{this.playerGame, this.aiGame};
    }
    /**
     * This method enables hints and toggles them for existing game instances.
     */
    public void enableHints(boolean enabled) {
        this.aiHintsEnabled = enabled;
        if (this.playerGame != null) {
            this.playerGame.setHints(enabled);
        }
        if (this.aiGame != null) {
            this.aiGame.setHints(enabled);
        }
    }
    /**
     * This method is a constructor that sets hints to the state of the parameter and delegates visual updates for
     * the hints to the refreshHints method.
     */
    private void setHints(boolean enabled) {
        this.aiHintsEnabled = enabled;
        this.refreshHints();
    }
    /**
     * This method stops all current existing games logically.
     */
    private void stopAllGames() {
        if (this.playerGame != null) {
            this.playerGame.stopTimers();
            this.playerGame = null;
        }
        if (this.aiGame != null) {
            this.aiGame.stopTimers();
            this.aiGame = null;
        }
    }
    /**
     * This method sets up the game by initializing all components such as the board, counters, buttons, timer, and
     * autoplay if enabled in tutorial/versus mode.
     */
    private void setupGame() {
        this.board = new Board(this.currentRows, this.currentCols, this.currentMines, this);
        this.mineCounter.setText(String.format("%03d", this.currentMines));
        this.timerLabel.setText("000");
        this.faceButton.setText("🙂");
        this.faceButton.setOnAction((ActionEvent e) -> this.restart());
        for (int i = 0; i < this.currentRows; i++) {
            for (int j = 0; j < this.currentCols; j++) {
                this.updateCell(i, j);
            }
        }
        this.setupTimer();
        this.refreshHints();
        if (this.autoPlay) {
            this.aiMove();
        }
    }
    /**
     * This method handles left clicks, ensuring no interaction if game is already over, and otherwise revealing
     * the cell on the board.
     */
    public void handleLeftClick(int row, int col) {
        if (this.board.isGameOver()) {
            return;
        }
        if (!this.board.isInitialized()) {
            this.board.initialize(row, col);
            for (int i = 0; i < this.currentRows; i++) {
                for (int j = 0; j < this.currentCols; j++) {
                    this.updateCell(i, j);
                }
            }
            if (this.timer != null) {
                this.timer.play();
            }
            Cell cell = this.board.getCell(row, col);
            if (cell != null && !cell.isFlagged()) {
                cell.reveal(this.board);
            }
            this.refreshHints();
            return;
        }
        Cell cell = this.board.getCell(row, col);
        if (cell != null && !cell.isFlagged()) {
            cell.reveal(this.board);
        }
        this.refreshHints();
    }
    /**
     * This method handles right clicks, ensuring no interaction if game is already over, and otherwise toggling a
     * flag in the corresponding cell on the board.
     */
    public void handleRightClick(int row, int col) {
        if (!this.board.isInitialized() || this.board.isGameOver()) {
            return;
        }
        this.board.toggleFlag(row, col);
    }
    /**
     * This method updates the cell at the given coordinates in the parameters, and sets necessary texts and emojis
     * and styling the cell panes.
     */
    public void updateCell(int row, int col) {
        if (this.cellPanes == null || this.cellTexts == null) {
            return;
        }
        if (row < 0 || row >= this.currentRows || col < 0 || col >= this.currentCols) {
            return;
        }
        Cell cell = this.board.getCell(row, col);
        Pane pane = this.cellPanes[row][col];
        Text text = this.cellTexts[row][col];
        if (!cell.isRevealed()) {
            if (cell.isFlagged()) {
                text.setText("🚩");
                text.setFill(Color.BLACK);
                if (!this.wasFlagged[row][col]) {
                    this.playFlagAnimation(text);
                    this.wasFlagged[row][col] = true;
                }
            }
            else {
                text.setText("");
                this.wasFlagged[row][col] = false;
            }
            pane.setStyle(
                    "-fx-background-color: #C0C0C0;" +
                            "-fx-border-color: #FFFFFF #808080 #808080 #FFFFFF;" +
                            "-fx-border-width: 2;");
        }
        else {
            text.setText(cell.getDisplayText());
            text.setFill(Color.web(cell.getTextColor()));
            pane.setStyle(
                    "-fx-background-color: " + cell.getBackgroundColor() + ";" +
                            "-fx-border-color: #808080;" +
                            "-fx-border-width: 1;");
            this.wasFlagged[row][col] = false;
        }
    }
    /**
     * This method plays a scaling up animation when a flag is placed.
     */
    private void playFlagAnimation(Text text) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), text);
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }
    /**
     * This method updates the mine counter and recalculates hints after doing so.
     */
    public void updateMineCounter() {
        int remaining = this.currentMines - this.board.getFlags();
        this.mineCounter.setText(String.format("%03d", remaining));
        this.refreshHints();
    }
    /**
     * This method sets up the timer that runs when a game is initiated, formatting its text and starting the timeline.
     */
    private void setupTimer() {
        if (this.timer != null) {
            this.timer.stop();
        }
        this.seconds = 0;
        this.timerLabel.setText("000");
        this.timer = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent e) -> {
            this.seconds++;
            if (this.seconds > 999) {
                this.seconds = 999;
            }
            this.timerLabel.setText(String.format("%03d", this.seconds));
        }));
        this.timer.setCycleCount(Animation.INDEFINITE);
    }
    /**
     * This method updates different components like the timer, timeline, and facebutton when a game is over and
     * resets the calculations for hints.
     */
    public void onGameOver(boolean won) {
        if (this.timer != null) {
            this.timer.stop();
        }
        if (this.aiMoveTimeline != null) {
            this.aiMoveTimeline.stop();
        }
        if (won){
            this.faceButton.setText("😎");
        }
        else{
            this.faceButton.setText("😵");
        }
        if (won && !this.autoPlay) {
            this.gameWon = true;
        }
       this.refreshHints();
    }
    /**
     * This method returns whether the game has been won and resets the gameWon variable if necessary.
     */
    public boolean resetWin() {
        if (this.gameWon) {
            this.gameWon = false;
            return true;
        }
        return false;
    }
    /**
     * This getter method returns the time elapsed since game started.
     */
    public int getTime() {
        return this.seconds;
    }
    /**
     * This getter method returns if autoplay is on.
     */
    public boolean isAutoplay() {
        return this.autoPlay;
    }
    /**
     * This method restarts the game by resetting all conditions of timer and timeline if they exist and resets
     * texts and values to default settings, then setting up a new game.
     */
    private void restart() {
        this.stopTimers();
        this.seconds = 0;
        this.timerLabel.setText("000");
        this.faceButton.setText("🙂");
        this.setupGame();
    }
    /**
     * This method stops all timers and timelines if they exist.
     */
    private void stopTimers() {
        if (this.timer != null) {
            this.timer.stop();
        }
        if (this.aiMoveTimeline != null) {
            this.aiMoveTimeline.stop();
        }
    }
    /**
     * This method refreshes the hints given to the user through highlighting certain cells green or red depending
     * on if they are the safest or most dangerous option to reveal or flag.
     */
    private void refreshHints() {
        if (this.board == null || this.cellPanes == null) {
            return;
        }
        for (int i = 0; i < this.currentRows; i++) {
            for (int j = 0; j < this.currentCols; j++) {
                this.updateCell(i, j);
            }
        }
        if (!this.aiHintsEnabled || !this.board.isInitialized() || this.board.isGameOver()) {
            return;
        }
        double minRisk = 0.8;
        List<Cell> dangerousCells = this.hintAI.getDangerousCells(this.board, minRisk);
        for (Cell cell : dangerousCells) {
            if (cell.isRevealed() || cell.isFlagged()) {
                continue;
            }
            Pane pane = this.cellPanes[cell.getRow()][cell.getCol()];
            pane.setStyle(
                    "-fx-background-color: #FFCCCC;" +
                            "-fx-border-color: #FF0000;" +
                            "-fx-border-width: 2;");
        }
        Cell safest = this.hintAI.getHint(this.board);
        if (safest != null && !safest.isRevealed() && !safest.isFlagged()) {
            Pane pane = this.cellPanes[safest.getRow()][safest.getCol()];
            pane.setStyle(
                    "-fx-background-color: #CCFFCC;" +
                            "-fx-border-color: #00AA00;" +
                            "-fx-border-width: 2;");
        }
    }
    /**
     * This method handles the AI player's movement by prioritizing first flagging most dangerous cells and then
     * revealing safest cell options, continuing until the game is over.
     */
    private void aiMove() {
        if (this.board == null || this.board.isGameOver()) {
            return;
        }
        double delay = 0.5 + Math.random();
        this.aiMoveTimeline = new Timeline(
                new KeyFrame(Duration.seconds(delay), (ActionEvent e) -> {
                    if (this.board == null || this.board.isGameOver()) {
                        return;
                    }
                    if (this.timer != null && this.seconds == 0 && !this.board.isInitialized()) {
                        this.timer.play();
                    }
                    boolean madeMove = false;
                    // first click center to initialize the board
                    if (!this.board.isInitialized()) {
                        int startRow = this.currentRows / 2;
                        int startCol = this.currentCols / 2;
                        this.handleLeftClick(startRow, startCol);
                        madeMove = true;
                    }
                    else {
                        // then try to flag dangerous cells
                        List<Cell> dangerous = this.hintAI.getDangerousCells(this.board, 0.99);
                        for (Cell cell : dangerous) {
                            if (!cell.isRevealed() && !cell.isFlagged()
                                    && this.board.getFlags() < this.currentMines) {
                                int i = cell.getRow();
                                int j = cell.getCol();
                                this.board.toggleFlag(i, j);
                                this.refreshHints();
                                madeMove = true;
                                break;
                            }
                        }

                        // if nothing to flag, click the safest cell
                        if (!madeMove) {
                            Cell safest = this.hintAI.getHint(this.board);
                            if (safest != null && !safest.isRevealed() && !safest.isFlagged()) {
                                this.handleLeftClick(safest.getRow(), safest.getCol());
                                madeMove = true;
                            }
                        }

                        // if still nothing, pick a random unrevealed/unflagged cell
                        if (!madeMove) {
                            Cell[][] grid = this.board.getGrid();
                            int attempts = 0;
                            while (attempts < 1000) {
                                int i = (int) (Math.random() * this.currentRows);
                                int j = (int) (Math.random() * this.currentCols);
                                Cell cell = grid[i][j];
                                if (!cell.isRevealed() && !cell.isFlagged()) {
                                    this.handleLeftClick(i, j);
                                    madeMove = true;
                                    break;
                                }
                                attempts++;
                            }
                        }
                    }
                    // move again if move is made and the game isn't over
                    if (madeMove && !this.board.isGameOver()) {
                        this.aiMove();
                    }
                })
        );
        this.aiMoveTimeline.setCycleCount(1);
        this.aiMoveTimeline.play();
    }
    /**
     * This method records the best score in a variable and saves the all time best time by creating a file in the
     * user's home file and writing the best time on it, ensuring that the score saves across different runs.
     */
    public void recordWin(int time) {
        if (time < this.sessionBestTime) {
            this.sessionBestTime = time;
        }

        if (time < this.allTimeBestTime) {
            this.allTimeBestTime = time;
            try {
                String userHome = System.getProperty("user.home");
                File file = new File(userHome, ".minesweeper_best.txt");
                PrintWriter writer = new PrintWriter(file);
                writer.println(this.allTimeBestTime);
                writer.close();
            }
            catch (Exception e) {}
        }
    }
    /**
     * This getter method returns the session best time.
     */
    public int getSessionBestTime() {
        return this.sessionBestTime;
    }
    /**
     * This getter method returns the all time best time.
     */
    public int getAllTimeBestTime() {
        return this.allTimeBestTime;
    }
    /**
     * This method loads the all time best time if there already is a text file saved storing the integer value,
     * otherwise return 999 which means there is no best time yet.
     */
    private int loadAllTimeBest() {
        try {
            String userHome = System.getProperty("user.home");
            File file = new File(userHome, ".minesweeper_best.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    int best = scanner.nextInt();
                    scanner.close();
                    return best;
                }
                scanner.close();
            }
        }
        catch (Exception e) {}
        return 999;
    }
}
