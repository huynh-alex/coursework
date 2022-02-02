package edu.iastate.cs228.hw1;

/**
 * Grass remains if more than rabbits in the neighborhood; otherwise, it is
 * eaten.
 * 
 * @author Alex Huynh
 */
public class Grass extends Living {
	/**
	 * Constructs a Grass in the given plain, and in the given row and position
	 * 
	 * @param p: plain
	 * @param r: row position
	 * @param c: column position
	 */
	public Grass(Plain p, int r, int c) {
		super.plain = p;
		super.row = r;
		super.column = c;
	}

	/**
	 * A Grass occupies the square.
	 */
	public State who() {
		return State.GRASS;
	}

	/**
	 * Grass can be eaten out by too many rabbits. Rabbits may also multiply fast
	 * enough to take over Grass.
	 */
	public Living next(Plain pNew) {
		int[] population = new int[NUM_LIFE_FORMS];
		census(population);

		if ((population[RABBIT]) >= (population[GRASS] * 3)) {
			pNew.grid[super.row][super.column] = new Empty(pNew, super.row, super.column);
		} else if (population[RABBIT] >= 3) {
			pNew.grid[super.row][super.column] = new Rabbit(pNew, super.row, super.column, 0);
		} else {
			pNew.grid[super.row][super.column] = new Grass(pNew, super.row, super.column);
		}
		super.plain = pNew;
		return pNew.grid[super.row][super.column];
	}
}
