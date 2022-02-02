package hw3;

import java.util.ArrayList;

import maze.MazeObserver;
import maze.Status;

/**
 * A Maze consists of MazeCells arranged in a 2D grid.
 * 
 * @author Alex Huynh
 */
public class Maze {

	/**
	 * Observer to be notified in case of changes to cells in this maze.
	 */
	private MazeObserver observer;

	/**
	 * Number of columns in the maze
	 */
	private int columns;

	/**
	 * Number of rows in the maze
	 */
	private int rows;

	/**
	 * The array of strings that holds the array and describes what it looks like
	 */
	private String[] mazeImage;

	/**
	 * A MazeCell object that corresponds to the starting cell in the maze
	 */
	private MazeCell start;

	/**
	 * A MazeCell object that corresponds to the goal cell in the maze
	 */
	private MazeCell goal;

	/**
	 * An ArrayList that holds the individual cells of the maze as MazeCell objects
	 */
	private ArrayList<MazeCell> listOfCells;

	/**
	 * Constructs a maze based on a 2D grid.
	 * 
	 * @param rows the maze
	 */
	public Maze(String[] rows) {
		this.observer = null;
		this.mazeImage = rows;
		this.columns = rows[0].length();
		this.rows = rows.length;

		// check if rectangular or not
		boolean rectangular = true;

		// first row length
		int rowLength = rows[0].length();
		// go through every row and check if equal
		for (int i = 1; i < rows.length; i++) {
			if (rowLength != rows[i].length()) {
				rectangular = false;
			}
		}

		// check for one start and one end
		boolean oneStart = false;
		boolean oneGoal = false;
		boolean goodStart = true;
		boolean goodGoal = true;

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.columns; j++) {
				if (mazeImage[i].charAt(j) == 'S' && oneStart == false) {
					oneStart = true;
				} else if (mazeImage[i].charAt(j) == 'S' && oneStart == true) {
					goodStart = false;
				} else if (mazeImage[i].charAt(j) == '$' && oneGoal == false) {
					oneGoal = true;
				} else if (mazeImage[i].charAt(j) == '$' && oneGoal == true) {
					goodGoal = false;
				}
			}
		}

		if (!rectangular) {
			System.out.println("Not rectangular");
		} else if (!goodStart || !goodGoal) {
			System.out.println("Bad cells");
		} else {
			listOfCells = new ArrayList<>();

			// places all the cells into the ArrayList
			// also notes where the start and end are
			for (int i = 0; i < this.rows; i++) {
				for (int j = 0; j < this.columns; j++) {
					// default status is WALL
					MazeCell currentCell = new MazeCell(i, j);
					currentCell.setOwner(this);

					if (mazeImage[i].charAt(j) == 'S') {
						currentCell.setStatus(Status.UNVISITED);
						start = currentCell;
					} else if (mazeImage[i].charAt(j) == '$') {
						currentCell.setStatus(Status.GOAL);
						goal = currentCell;
					}
					// If it is not a wall, make sure it is unvisited
					else if (mazeImage[i].charAt(j) != '#') {
						currentCell.setStatus(Status.UNVISITED);
					}
					listOfCells.add(currentCell);
				}
			}

			// we need to find the neighbors of all non-walls in our ArrayList
			for (int cellNumber = 0; cellNumber < listOfCells.size(); cellNumber++) {
				if (listOfCells.get(cellNumber).getStatus() != Status.WALL) {
					// check from top, left, bottom, right (4 checks)
					for (int neighborsChecked = 1; neighborsChecked < 5; neighborsChecked++) {
						int indexToCheck = 0;

						if (neighborsChecked == 1) {
							indexToCheck = cellNumber - rows[0].length();
						} else if (neighborsChecked == 2) {
							indexToCheck = cellNumber - 1;
						} else if (neighborsChecked == 3) {
							indexToCheck = cellNumber + rows[0].length();
						} else {
							indexToCheck = cellNumber + 1;
						}

						// cannot be outside boundaries of the maze
						if (!(indexToCheck < 0 || indexToCheck > listOfCells.size() - 1)) {
							MazeCell potentialNeighbor = listOfCells.get(indexToCheck);

							int distance = 0;

							// neighbors cannot be walls
							if (potentialNeighbor.getStatus() != Status.WALL) {
								// distance has to be 1 to be a neighbor
								distance = listOfCells.get(cellNumber).distance(potentialNeighbor);

								if (distance == 1) {
									listOfCells.get(cellNumber).addNeighbor(potentialNeighbor);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the observer for the cells of this maze.
	 * 
	 * @param obs the observer
	 */
	public void setObserver(MazeObserver obs) {
		observer = obs;
	}

	/**
	 * Notifies the observer that the given cell's status has changed.
	 * 
	 * @param cell a given cell in the maze
	 */
	public void updateStatus(MazeCell cell) {
		if (observer != null) {
			observer.updateStatus(cell);
		}
	}

	/**
	 * Returns the cell at the given position.
	 * 
	 * @param row the row
	 * @param col the column
	 * @return the cell at the given row and column
	 */
	public MazeCell getCell(int row, int col) {
		MazeCell cell = listOfCells.get(row * this.rows + col);
		return cell;
	}

	/**
	 * Returns the number of columns in this maze.
	 * 
	 * @return col number
	 */
	public int getColumns() {
		return this.columns;
	}

	/**
	 * Returns the number of rows in this maze.
	 * 
	 * @return row number
	 */
	public int getRows() {
		return this.rows;
	}

	/**
	 * Returns the start cell for this maze.
	 * 
	 * @return the start cell
	 */
	public MazeCell getStart() {
		return start;
	}

	/**
	 * Returns the goal cell for this maze.
	 * 
	 * @return the goal cell
	 */
	public MazeCell getGoal() {
		return goal;
	}

}
