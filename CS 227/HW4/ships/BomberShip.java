package ships;

import projectiles.Bomb;
import projectiles.Projectile;
import utils.Position;

/**
 * The implementation of a Bomber Ship.
 * 
 * @author Alex Huynh
 */

public class BomberShip extends InvaderShip {
	public static final double EXPLOSION_RADIUS = 10;

	/**
	 * Constructs a BomberShip
	 * 
	 * @param p     The initial position
	 * @param armor The initial armor level
	 */
	public BomberShip(Position p, int armor) {
		super(p, armor);
	}

	/**
	 * Drops a single bomb
	 * 
	 * @return An array containing a single bomb
	 */
	public Projectile[] fire() {

		if (!canFire()) {
			return null;
		}

		lastShotTime = System.currentTimeMillis();
		
		Projectile[] out = new Projectile[1];
		out[0] = new Bomb(pos, 0, -PROJECTILE_SPEED, Projectile.GRAVITY, EXPLOSION_RADIUS);
		return out;

	}

	@Override
	public String imgPath() {
		return "res/monster2.png";
	}

	@Override
	public int getPoints() {
		return 100;
	}
}
