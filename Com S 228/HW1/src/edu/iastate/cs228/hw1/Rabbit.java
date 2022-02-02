package edu.iastate.cs228.hw1;

/**
 * A rabbit eats grass and lives no more than three years.
 * 
 * @author Alex Huynh
 */
public class Rabbit extends Animal {

	/**
	 * Constructs a Rabbit in the given plain, in the given row and position, and
	 * with a given age
	 * 
	 * @param p: plain
	 * @param r: row position
	 * @param c: column position
	 * @param a: age
	 */
	public Rabbit(Plain p, int r, int c, int a) {
		super.plain = p;
		super.row = r;
		super.column = c;
		super.age = a;
	}

	// Rabbit occupies the square.
	public State who() {
		return State.RABBIT;
	}

	/**
	 * A rabbit dies of old age or hunger. It may also be eaten by a badger or a
	 * fox.
	 * 
	 * @param pNew plain of the next cycle
	 * @return Living new life form occupying the same square
	 */
	public Living next(Plain pNew) {
		int[] population = new int[NUM_LIFE_FORMS];
		census(population);

		if (super.age == RABBIT_MAX_AGE) {
			pNew.grid[super.row][super.column] = new Empty(pNew, super.row, super.column);
		} else if (population[GRASS] == 0) {
			pNew.grid[super.row][super.column] = new Empty(pNew, super.row, super.column);
		} else if ((population[FOX] + population[BADGER] >= population[RABBIT])
				&& (population[FOX] > population[BADGER])) {
			pNew.grid[super.row][super.column] = new Fox(pNew, super.row, super.column, 0);
		} else if (population[BADGER] > population[RABBIT]) {
			pNew.grid[super.row][super.column] = new Badger(pNew, super.row, super.column, 0);
		} else {
			pNew.grid[super.row][super.column] = new Rabbit(pNew, super.row, super.column, super.age + 1);
		}
		super.plain = pNew;
		return pNew.grid[super.row][super.column];
	}
}
