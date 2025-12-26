package minesweeper;

/**
 * This class Cell tracks the game state like position, revealed, and flagged of each cell on the board, and updates
 * the panes and texts through parameters passed to each method.
 */
public abstract class Cell {
    private int row;
    private int col;
    private boolean revealed;
    private boolean flagged;
    /**
     * This method is the constructor which initializes the four instance variables.
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.revealed = false;
        this.flagged = false;
    }

    /**
     * This method is an abstract method that is different for each child class and reveals the content of the cell.
     */
    public abstract void reveal(Board board);
    /**
     * This method reverses the current flagged state of the cell if it isn't already a revealed cell.
     */
    public void toggleFlag() {
        if (this.revealed) {
            return;
        }
        this.flagged = !this.flagged;
    }
    /**
     * This method is an abstract method that tells whether the current cell has no surrounding mines.
     */
    public abstract boolean isZero();
    /**
     * This method returns whether the current cell is a mine.
     */
    public boolean isMine() {
        return false;
    }
    /**
     * This method is a getter method for if the current cell is revealed.
     */
    public boolean isRevealed() { return this.revealed; }
    /**
     * This method is a getter method for whether the current cell is flagged.
     */
    public boolean isFlagged() { return this.flagged; }
    /**
     * This method is a getter method for the current cell's row.
     */
    public int getRow() { return this.row; }
    /**
     * This method is a getter method for whether the current cell's column.
     */
    public int getCol() { return this.col; }
    /**
     * This method is a getter method for the current cell's clue value.
     */
    public int getClueValue() {
        return -1;
    }
    /**
     * This method is a getter method for whether the current cell is a mine.
     */
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
    /**
     * This method is an abstract getter method for the text/emoji to be displayed at the cell.
     */
    public abstract String getDisplayText();
    /**
     * This method is an abstract getter method for the text/emoji color to be displayed at the cell.
     */
    public abstract String getTextColor();
    /**
     * This method is an abstract getter method for the background color to be displayed at the cell.
     */
    public abstract String getBackgroundColor();
}
