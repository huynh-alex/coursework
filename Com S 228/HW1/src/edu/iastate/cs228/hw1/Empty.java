package edu.iastate.cs228.hw1;

/**
 * Empty squares are competed by various forms of life.
 * 
 * @author Alex Huynh
 */
public class Empty extends Living {

	/**
	 * Constructs an Empty in the given plain, and in the given row and position
	 * 
	 * @param p: plain
	 * @param r: row position
	 * @param c: column position
	 */
	public Empty(Plain p, int r, int c) {
		super.plain = p;
		super.row = r;
		super.column = c;
	}

	/**
	 * An Empty occupies the square.
	 */
	public State who() {
		return State.EMPTY;
	}

	/**
	 * An empty square will be occupied by a neighboring Badger, Fox, Rabbit, or
	 * Grass, or remain empty.
	 * 
	 * @param pNew plain of the next life cycle.
	 * @return Living life form in the next cycle.
	 */
	public Living next(Plain pNew) {
		int[] population = new int[NUM_LIFE_FORMS];
		census(population);

		if (population[RABBIT] > 1) {
			pNew.grid[super.row][super.column] = new Rabbit(pNew, super.row, super.column, 0);
		} else if (population[FOX] > 1) {
			pNew.grid[super.row][super.column] = new Fox(pNew, super.row, super.column, 0);
		} else if (population[BADGER] > 1) {
			pNew.grid[super.row][super.column] = new Badger(pNew, super.row, super.column, 0);
		} else if (population[GRASS] >= 1) {
			pNew.grid[super.row][super.column] = new Grass(pNew, super.row, super.column);
		} else {
			pNew.grid[super.row][super.column] = new Empty(pNew, super.row, super.column);
		}
		super.plain = pNew;
		return pNew.grid[super.row][super.column];
	}
}
