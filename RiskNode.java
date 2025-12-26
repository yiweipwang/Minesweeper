package minesweeper;

/**
 * This class is a risk node that is used for the hintAI and implements the IRiskNode interface.
 */
class RiskNode implements IRiskNode {

    private Cell cell;
    private double risk;
    private IRiskNode left;
    private IRiskNode right;

    public RiskNode(Cell cell, double risk) {
        this.cell = cell;
        this.risk = risk;
        this.left = new EmptyRiskNode();
        this.right = new EmptyRiskNode();
    }
    /**
     * This getter method returns the cell that this node stores.
     */
    @Override
    public Cell getCell() {
        return this.cell;
    }
    /**
     * This getter method returns the node stored as the left branch in the subtree of this node.
     */
    @Override
    public IRiskNode getLeft() {
        return this.left;
    }
    /**
     * This getter method returns the node stored as the right branch in the subtree of this node.
     */
    @Override
    public IRiskNode getRight() {
        return this.right;
    }
    /**
     * This getter method returns that this node is not empty.
     */
    @Override
    public boolean isEmpty() {
        return false;
    }
    /**
     * This method inserts and returns a new node with the cell and risk given in the parameters, storing it in
     * accordance to the rules of the tree where the leftmost node is the lowest risk.
     */
    @Override
    public IRiskNode insert(Cell newCell, double newRisk) {
        if (newRisk < this.risk) {
            this.left = this.left.insert(newCell, newRisk);
        }
        else if (newRisk > this.risk) {
            this.right = this.right.insert(newCell, newRisk);
        }
        else {
            this.right = this.right.insert(newCell, newRisk);
        }
        return this;
    }
    /**
     * This getter method returns the leftmost node or the smallest risk node in this node's subtree if it exists,
     * otherwise return this node.
     */
    @Override
    public IRiskNode leftMost() {
        if (this.left.isEmpty()) {
            return this;
        }
        return this.left.leftMost();
    }
}
