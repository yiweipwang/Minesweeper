package minesweeper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class makes risk estimates for cells based on revealed number clues using a tree and is used as the base
 * of the AI system in the hint, versus, and tutorial options of the menu.
 */
public class HintAI {
    private IRiskNode root;
    /**
     * This method is the constructor that creates an empty root risk tree.
     */
    public HintAI() {
        this.root = new EmptyRiskNode();
    }
    /**
     * This method returns the single safest cell based on risk or null if no move is available by inserting
     * unknown cells and their risks into the tree and getting the leftmost node.
     */
    public Cell getHint(Board board) {
        if (board == null || !board.isInitialized() || board.isGameOver()) {
            return null;
        }
        this.root = new EmptyRiskNode();
        int rows = board.getRows();
        int cols = board.getCols();
        Cell[][] grid = board.getGrid();
        double defaultRisk = this.setRisk(board, rows, cols, grid);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = grid[i][j];
                if (cell.isRevealed() || cell.isFlagged()) {
                    continue;
                }
                double risk = this.getRisk(board, i, j, defaultRisk);
                this.root = this.root.insert(cell, risk);
            }
        }
        IRiskNode bestNode = this.root.leftMost();
        if (bestNode == null || bestNode.getCell() == null) {
            return null;
        }
        return bestNode.getCell();
    }
    /**
     * This method computes the general default risk by dividing remaining mines by unknown cells.
     */
    public double setRisk(Board board, int rows, int cols, Cell[][] grid) {
        int totalMines = board.getNumMines();
        int flagsPlaced = board.getFlags();
        int unknownCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = grid[i][j];
                if (!cell.isRevealed() && !cell.isFlagged()) {
                    unknownCount++;
                }
            }
        }
        int remainingMines = totalMines - flagsPlaced;
        if (unknownCount > 0) {
            return (double) remainingMines / unknownCount;
        } else {
            return 1.0;
        }
    }

    /**
     * This method returns all cells whose estimated risk is greater than the minRisk parameter and is used to
     * highlight dangerous cells in red in MSGame.java
     */
    public List<Cell> getDangerousCells(Board board, double minRisk) {
        List<Cell> result = new ArrayList<>();
        if (board == null || !board.isInitialized() || board.isGameOver()) {
            return result;
        }
        int rows = board.getRows();
        int cols = board.getCols();
        Cell[][] grid = board.getGrid();
        double defaultRisk = this.setRisk(board, rows, cols, grid);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = grid[i][j];
                if (cell.isRevealed() || cell.isFlagged()) {
                    continue;
                }
                double risk = this.getRisk(board, i, j, defaultRisk);
                if (risk >= minRisk) {
                    result.add(cell);
                }
            }
        }

        return result;
    }

    /**
     * This method estimates how likely it is that this cell is a mine by looking at nearby revealed number cells. it
     * checks how many mines are still unaccounted for and how many neighboring cells are still unknown for each
     * revealed number around the cell, then calculates a risk value. The final risk is the highest risk found from all
     * nearby numbers. If no numbers give a risk value, then it uses the defaultRisk instead.
     */
    private double getRisk(Board board, int row, int col, double defaultRisk) {
        // track the highest risk from any neighboring cell, start at -1 meaning no useful hint
        Cell[][] grid = board.getGrid();
        int[][] neighbours = board.getNeighborCoords(row, col);
        double maxLocalRisk = -1.0;
        for (int[] coord : neighbours) {
            int i = coord[0];
            int j = coord[1];
            Cell neighborCell = grid[i][j];
            if (!neighborCell.isRevealed()) {
                continue;
            }
            int clue = neighborCell.getClueValue();
            if (clue < 0) {
                continue;
            }
            // look at neighbor cells and count number flagged and number unknown
            int[][] numberNeighbors = board.getNeighborCoords(i, j);
            int flagged = 0;
            int unknown = 0;
            for (int[] around : numberNeighbors) {
                int ii = around[0];
                int jj = around[1];
                Cell n = grid[ii][jj];
                if (n.isFlagged()) {
                    flagged++;
                }
                else if (!n.isRevealed()) {
                    unknown++;
                }
            }
            if (unknown == 0) {
                continue;
            }
            // if all mines around this number have already been flagged the remaining unknown neighbors must be safe
            int minesRemaining = clue - flagged;
            if (minesRemaining <= 0) {
                return 0.0;
            }
            // Keep track of the highest risk value, if one is found return the highest, if none then return default
            // risk from before
            double localRisk = (double) minesRemaining / unknown;
            if (localRisk > maxLocalRisk) {
                maxLocalRisk = localRisk;
            }
        }
        if (maxLocalRisk >= 0.0) {
            return maxLocalRisk;
        }
        return defaultRisk;
    }
}
