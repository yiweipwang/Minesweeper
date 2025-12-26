package minesweeper;

/**
 * This class is an empty risk node that is used for the hintAI and implements the IRiskNode.java interface.
 */
class EmptyRiskNode implements IRiskNode {
    /**
     * This getter method returns that there is no cell reference and the current node is empty.
     */
    @Override
    public Cell getCell() {
        return null;
    }
    /**
     * This getter method returns that there is no left node by returning itself.
     */
    @Override
    public IRiskNode getLeft() {
        return this;
    }
    /**
     * This getter method returns that there is no right node by returning itself.
     */
    @Override
    public IRiskNode getRight() {
        return this;
    }
    /**
     * This getter method returns a boolean that says it is empty.
     */
    @Override
    public boolean isEmpty() {
        return true;
    }
    /**
     * This method inserts a new cell and risk into this node passed in the parameters and returns a new RiskNode.java
     * in place of this empty node.
     */
    @Override
    public IRiskNode insert(Cell newCell, double newRisk) {
        return new RiskNode(newCell, newRisk);
    }
    /**
     * This method returns that there is no left most node by returning null since the tree is empty.
     */
    @Override
    public IRiskNode leftMost() {
        return null;
    }
}
