package hw3;

import java.awt.Point;
import java.util.ArrayList;

import maze.Status;

/**
 * Implementation of MazeCell that has a location in a 2D plane.
 * 
 * @author Alex Huynh
 */
public class MazeCell {
	
	/**
	 * the maze to which this MazeCell belongs.
	 */
	private Maze owner;

	/**
	 * Status of this cell
	 */
	private Status status;

	/**
	 * The parent of this cell
	 */
	private MazeCell parent;

	/**
	 * Holds the time stamp of this cell
	 */
	private int timeStamp;

	/**
	 * The row that this cell is in
	 */
	private int row;

	/**
	 * The column that this cell is in
	 */
	private int col;
	/**
	 * The location of this cell
	 */

	/**
	 * The x and y coordinates of the MazeCell
	 */
	private Point point;

	/**
	 * An ArrayList that holds the neighbors of a cell in top, left, bottom, right order
	 */
	private ArrayList<MazeCell> neighbors;

	/**
	 * Constructs a maze cell whose location is specified by the given row and column, 
	 * whose status is WALL by default, and whose parent is null. The cell initially 
	 * has no neighbors. Its initial time stamp is 0.
	 * 
	 * @param row
	 * 	the row
	 * @param col
	 * 	the column
	 */
	public MazeCell(int row, int col) {
		this.row = row;
		this.col = col;
		point = new Point(row, col);
		status = Status.WALL;
		parent = null;
		timeStamp = 0;
		owner = null;
		neighbors = new ArrayList<>();
	}

	/**
	 * Adds an observer for changes in this cell's status.
	 * 
	 * @param maze
	 * 	the maze to which this cell belong
	 */
	public void setOwner(Maze maze) {
		owner = maze;
	}

	/**
	 * Update the status of this cell and notifies the owner that this current
	 * cell's status has changed
	 * 
	 * @param s
	 * 	the updated status
	 */
	public void setStatus(Status s) {
		status = s;
		if (owner != null) {
			owner.updateStatus(this);
		}
	}

	/**
	 * return the status of the current the status
	 * 
	 * @return 
	 * 	status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Adds a neighbor to this cell.
	 * 
	 * @param m
	 * 	the maze cell
	 */
	public void addNeighbor(MazeCell m) {
		neighbors.add(m);
	}

	/**
	 * Computes the Manhattan distance between this cell and other cell.
	 * 
	 * @param other
	 * 	the other cell
	 * @return
	 * 	the Manhattan distance
	 */
	public int distance(MazeCell other) {
		int distance = Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
		return distance;
	}

	/**
	 * Returns this cell's location as a point, which contains its row and column.
	 * 
	 * @return
	 * 	location
	 */
	public Point getLocation() {
		return point;
	}

	/**
	 * Returns the neighbors of the current cell.
	 * 
	 * @return
	 * 	neighbors
	 */
	public ArrayList<MazeCell> getNeighbors() {
		return neighbors;
	}

	/**
	 * Gets the parent of this cell.
	 * 
	 * @return
	 * 	parent cell
	 */
	public MazeCell getParent() {
		if (parent != null) {
			return parent;
		}
		return null;
	}

	/**
	 * Sets the parent of this cell with the given parent.
	 * 
	 * @param parent
	 * 	the parent cell
	 */
	public void setParent(MazeCell parent) {
		this.parent = parent;
	}

	/**
	 * Returns the time stamp of this cell
	 * 
	 * @return
	 * 	time stamp
	 */
	public int getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Sets the time stamp of this cell
	 * 
	 * @param time
	 * 	time stamp
	 */
	public void setTimeStamp(int time) {
		timeStamp = time;
	}

	/**
	 * Returns a string representation of this cell's row and column numbers
	 * enclosed by a pair of parenthesis, e.g., (3, 4), or (10, 0).
	 */
	@Override
	public String toString() {
		return "(" + row + ", " + col + ")";
	}

}
