package minesweeper;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * This class is responsible for logic in relation to the minesweeper board, including changes to flags/mines being
 * placed as well as board generation and checking for when the game is over.
 */
public class Board {
    private Cell[][] grid;
    private int rows;
    private int cols;
    private int numMines;
    private boolean initialized;
    private boolean gameOver;
    private int flagCount;
    private MSGame controller;

    /**
     * This method is the constructor, which takes in four parameters that get assigned to instance variables.
     */
    public Board(int rows, int cols, int numMines, MSGame controller) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;
        this.controller = controller;
        this.initialized = false;
        this.gameOver = false;
        this.flagCount = 0;
        this.grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.grid[i][j] = new EmptyCell(i, j);
            }
        }
    }
    /**
     * This method sets up the board by placing mines and assigning the numbers to necessary tiles by delegating
     * them to the methods placeMines and calculateNumbers, and also marks whether the board is already initialized.
     */
    public void initialize(int firstRow, int firstCol) {
        if (this.initialized){
            return;
        }
        this.placeMines(firstRow, firstCol);
        this.calculateNumbers();
        this.initialized = true;
    }
    /**
     * This method places mines on the board according to how many is needed as indicated by the numMines variable,
     * and does so through random number generation while ensuring that the very first click is safe by delegating
     * to the isExcluded method.
     */
    private void placeMines(int excludeRow, int excludeCol) {
        int placed = 0;
        while (placed < this.numMines) {
            int i = (int) (Math.random() * this.rows);
            int j = (int) (Math.random() * this.cols);
            if (this.isExcluded(i, j, excludeRow, excludeCol)) {
                continue;
            }
            if (!this.grid[i][j].isMine()) {
                this.grid[i][j] = new MineCell(i, j);
                placed++;
            }
        }
    }
    /**
     * This method calculates whether the randomly generated coordinate would be violating the rule of
     * the first click being safe by calculating the magnitude of the row and column difference.
     */
    private boolean isExcluded(int i, int j, int excludeRow, int excludeCol) {
        int rowDiff = Math.abs(i - excludeRow);
        int colDiff = Math.abs(j - excludeCol);
        return rowDiff <= 1 && colDiff <= 1;
    }
    /**
     * This method assigns the numbers for each number cell by counting the number of mines in its neighbor
     * coordinates.
     */
    private void calculateNumbers() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                if (!this.grid[i][j].isMine()) {
                    int count = 0;
                    int[][] neighbors = this.getNeighborCoords(i, j);
                    for (int[] coord : neighbors) {
                        if (this.grid[coord[0]][coord[1]].isMine()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        this.grid[i][j] = new NumberCell(i, j, count);
                    }
                    else {
                        this.grid[i][j] = new EmptyCell(i, j);
                    }
                }
            }
        }
    }
    /**
     * This method calculates neighboring coordinates around a given coordinate in the parameters while making sure
     * it is within the valid grid of coordinates.
     */
    public int[][] getNeighborCoords(int row, int col) {
        ArrayList<int[]> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int ii = row + i;
                int jj = col + j;
                if (this.isValid(ii, jj)) {
                    neighbors.add(new int[]{ii, jj});
                }
            }
        }
        return neighbors.toArray(new int[0][]);
    }
    /**
     * This method checks for if the row and column taken as parameters is within the edges.
     */
    private boolean isValid(int row, int col) {
        return row >= 0 && row < this.rows && col >= 0 && col < this.cols;
    }
    /**
     * This method marks the game as over and lost, then revealing all mines.
     */
    public void gameOver() {
        this.gameOver = true;
        this.revealAllMines();
        this.controller.onGameOver(false);
    }
    /**
     * This method checks to see if the game is won by seeing if there are any unrevealed mines left on the board,
     * and if all are revealed then it marks the game as over and won.
     */
    public void checkWin() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                Cell cell = this.grid[i][j];
                if (!cell.isMine() && !cell.isRevealed()) {
                    return;
                }
            }
        }
        this.gameOver = true;
        this.controller.onGameOver(true);
    }
    /**
     * This method reveals all the mines in the board from left to right to make a wave like animation using a
     * timeline.
     */
    private void revealAllMines() {
        for (int col = 0; col < this.cols; col++) {
            int currentCol = col;
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(col * 50),
                    e -> {
                        for (int row = 0; row < this.rows; row++) {
                            if (this.grid[row][currentCol].isMine()) {
                                this.grid[row][currentCol].reveal(this);
                            }
                        }
                    }
            ));
            timeline.play();
        }
    }
    /**
     * This method places/removes a flag from a cell on the board and updates the cell visually and the mine counter
     * logically.
     */
    public void toggleFlag(int row, int col) {
        if (this.gameOver || !this.initialized){
            return;
        }
        Cell cell = this.grid[row][col];
        boolean wasFlagged = cell.isFlagged();
        if (this.flagCount != this.numMines) {
            cell.toggleFlag();
        }
        else if (wasFlagged) {
            cell.toggleFlag();
        }
        if (wasFlagged) {
            this.flagCount--;
        }
        else if (cell.isFlagged()) {
            this.flagCount++;
        }
        this.updateVisual(row, col);
        this.controller.updateMineCounter();
    }

    /**
     * This method tells the game controller that the cell needs updating and delegates the actual process to the
     * MSGame.java class.
     */
    public void updateVisual(int row, int col) {
        this.controller.updateCell(row, col);
    }
    /**
     * This method returns a cell on the grid at the given coordinate in the parameters.
     */
    public Cell getCell(int row, int col) {
        if (this.isValid(row, col)) {
            return this.grid[row][col];
        }
        return null;
    }
    /**
     * This method is a getter method for the 2d array grid.
     */
    public Cell[][] getGrid() { return this.grid; }
    /**
     * This method is a getter method for the number of rows.
     */
    public int getRows() { return this.rows; }
    /**
     * This method is a getter method for the number of columns.
     */
    public int getCols() { return this.cols; }
    /**
     * This method is a getter method for the 2d array grid.
     */
    public int getNumMines() { return this.numMines; }
    /**
     * This method is a getter method for number of flags on the board.
     */
    public int getFlags() { return this.flagCount; }
    /**
     * This method is a getter method for whether the board has been initialized.
     */
    public boolean isInitialized() { return this.initialized; }
    /**
     * This method is a getter method for if the game is still running or not.
     */
    public boolean isGameOver() { return this.gameOver; }
}
