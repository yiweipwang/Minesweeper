package minesweeper;

/**
 * This interface defines methods that will be used in the HintAI tree and its nodes.
 */
public interface IRiskNode {
    /**
     * This method returns the cell the node is storing.
     */
    Cell getCell();
    /**
     * This method returns the node on the left of the current node.
     */
    IRiskNode getLeft();
    /**
     * This method returns the node on the right of the current node.
     */
    IRiskNode getRight();
    /**
     * This method returns if the current node is an empty node with no reference.
     */
    boolean isEmpty();
    /**
     * This method inserts a cell with given risk into the tree and returns the root of this subtree.
     */
    IRiskNode insert(Cell cell, double risk);
    /**
     * This method returns the left-most or the lowest risk node in this subtree, or null if this subtree is empty.
     */
    IRiskNode leftMost();
}
