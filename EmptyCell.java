package minesweeper;

import java.util.LinkedList;
import java.util.Queue;
/**
 * This class is an empty cell that inherits from the parent Cell class, and defines its methods in the context of
 * the current cell being an empty cell with no surrounding mines and nothing to display.
 */
public class EmptyCell extends Cell {
    /**
     * This method is the constructor which uses the parent constructor through the super keyword.
     */
    public EmptyCell(int row, int col) {
        super(row, col);
    }
    /**
     * This method is a BFS reveal method that stops upon reaching a border of number cells using a queue, adding
     * all neighboring empty/zero cells to the queue and revealing them logically and graphically if they are
     * not yet flagged/revealed.
     */
    @Override
    public void reveal(Board board) {
        if (this.isFlagged() || this.isRevealed()) {
            return;
        }
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{this.getRow(), this.getCol()});
        while (!queue.isEmpty()) {
            int[] coords = queue.poll();
            int i = coords[0];
            int j = coords[1];
            Cell current = board.getCell(i, j);
            if (current == null || current.isFlagged() || current.isRevealed()) {
                continue;
            }
            current.setRevealed(true);
            board.updateVisual(i, j);
            if (current.isZero()) {
                int[][] neighbors = board.getNeighborCoords(i, j);
                for (int[] n : neighbors) {
                    Cell neighbor = board.getCell(n[0], n[1]);
                    if (neighbor != null && !neighbor.isRevealed() && !neighbor.isFlagged()) {
                        queue.add(new int[]{n[0], n[1]});
                    }
                }
            }
        }
        board.checkWin();
    }
    /**
     * This method overrides the parent isZero method by returning true since the empty cell has zero mines surrounding
     * it.
     */
    @Override
    public boolean isZero() {
        return true;
    }
    /**
     * This getter method overrides the parent getDisplayText method by returning an empty string to represent an empty cell.
     */
    @Override
    public String getDisplayText() {
        return "";
    }
    /**
     * This getter method overrides the parent getTextColor method and returns a black color to be used for the text.
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
        return "#BEBEBE";
    }
}
