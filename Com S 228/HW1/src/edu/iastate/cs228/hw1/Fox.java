package edu.iastate.cs228.hw1;

/**
 * A fox eats rabbits and competes against a badger.
 * 
 * @author Alex Huynh
 */
public class Fox extends Animal {
	/**
	 * Constructs a Badger in the given plain, in the given row and position, and
	 * with a given age
	 * 
	 * @param p: plain
	 * @param r: row position
	 * @param c: column position
	 * @param a: age
	 */
	public Fox(Plain p, int r, int c, int a) {
		super.plain = p;
		super.row = r;
		super.column = c;
		super.age = a;
	}

	/**
	 * A fox occupies the square.
	 */
	public State who() {
		return State.FOX;
	}

	/**
	 * A fox dies of old age or hunger, or from attack by numerically superior
	 * badgers.
	 * 
	 * @param pNew plain of the next cycle
	 * @return Living life form occupying the square in the next cycle.
	 */
	public Living next(Plain pNew) {
		int population[] = new int[NUM_LIFE_FORMS];
		census(population);

		if (super.age == FOX_MAX_AGE) {
			pNew.grid[super.row][super.column] = new Empty(pNew, super.row, super.column);
		} else if (population[BADGER] > population[FOX]) {
			pNew.grid[super.row][super.column] = new Badger(pNew, super.row, super.column, 0);
		} else if (population[FOX] + population[BADGER] > population[RABBIT]) {
			pNew.grid[super.row][super.column] = new Empty(pNew, super.row, super.column);
		} else {
			pNew.grid[super.row][super.column] = new Fox(pNew, super.row, super.column, super.age + 1);
		}
		super.plain = pNew;
		return pNew.grid[super.row][super.column];
	}
}
