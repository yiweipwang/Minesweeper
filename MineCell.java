package minesweeper;

/**
 * This class is a mine cell that inherits from the parent Cell class, and defines its methods in the context of
 * the current cell being a mine cell that displays a bomb emoji.
 */
public class MineCell extends Cell {
    /**
     * This method is the constructor which uses the parent constructor through the super keyword.
     */
    public MineCell(int row, int col) {
        super(row, col);
    }
    /**
     * This method reveals the cell only if it is not yet revealed or not flagged, updates it visually, and marks
     * the game to be over.
     */
    @Override
    public void reveal(Board board) {
        if (this.isFlagged() || this.isRevealed()) {
            return;
        }
        this.setRevealed(true);
        board.updateVisual(this.getRow(), this.getCol());
        board.gameOver();
    }
    /**
     * This method returns that this cell is a mine.
     */
    @Override
    public boolean isMine() {
        return true;
    }
    /**
     * This method returns that this cell is not empty.
     */
    @Override
    public boolean isZero() {
        return false;
    }
    /**
     * This getter method returns the bomb emoji that is to be displayed on this cell when revealed.
     */
    @Override
    public String getDisplayText() {
        return "💣";
    }
    /**
     * This getter method returns the text color of the cell.
     */
    @Override
    public String getTextColor() {
        return "#000000";
    }
    /**
     * This getter method returns the background color of the cell.
     */
    @Override
    public String getBackgroundColor() {
        return "#FF0000";
    }
}
