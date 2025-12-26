package minesweeper;

/**
 * This class is a number cell that inherits from the parent Cell class, and defines its methods in the context of
 * the current cell being a number cell that displays a number corresponding to how many mines are in its neighbor
 * cells.
 */
public class NumberCell extends Cell {
    private int adjacentMines;
    /**
     * This method is the constructor which uses the parent constructor through the super keyword and stores the number
     * of adjacent mines.
     */
    public NumberCell(int row, int col, int adjacentMines) {
        super(row, col);
        this.adjacentMines = adjacentMines;
    }
    /**
     * This method reveals the cell only if it is not yet revealed or not flagged, updates it visually, and checks
     * if the game is won.
     */
    @Override
    public void reveal(Board board) {
        if (this.isFlagged() || this.isRevealed()) {
            return;
        }
        this.setRevealed(true);
        board.updateVisual(this.getRow(), this.getCol());
        board.checkWin();
    }
    /**
     * This getter method returns false since it is not an empty cell.
     */
    @Override
    public boolean isZero() {
        return false;
    }
    /**
     * This getter method returns the clue value which corresponds to the number of mines in its neighboring
     * cells.
     */
    @Override
    public int getClueValue() {
        return this.adjacentMines;
    }
    /**
     * This getter method returns the number of mines in its neighboring cells in a string format to be displayed
     * if there are any.
     */
    @Override
    public String getDisplayText() {
        if (this.adjacentMines > 0) {
            return String.valueOf(this.adjacentMines);
        }
        else {
            return "";
        }
    }
    /**
     * This getter method returns the text color needed for the number cell by getting the number of mines and its
     * corresponding index in the constants array of colors.
     */
    @Override
    public String getTextColor() {
        if (this.adjacentMines >= 0 && this.adjacentMines < Constants.NUMBER_COLORS.length) {
            return Constants.NUMBER_COLORS[this.adjacentMines];
        }
        return "#000000";
    }
    /**
     * This getter method returns the background color of the cell.
     */
    @Override
    public String getBackgroundColor() {
        return "#BEBEBE";
    }
}
