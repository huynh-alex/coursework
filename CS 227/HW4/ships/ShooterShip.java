package ships;

import projectiles.Projectile;
import utils.Position;

/**
 * The implementation of a Shooter Ship.
 * @author Alex Huynh
 */

public class ShooterShip extends InvaderShip {
	/**
	 * Constructs a ShooterShip
	 * 
	 * @param p     The initial position
	 * @param armor The initial armor level
	 */
	public ShooterShip(Position p, int armor) {
		super(p, armor);
	}

	/**
	 * Drops a single projectile
	 * 
	 * @return An array containing a single projectile
	 */
	public Projectile[] fire() {
		if (!canFire()) {
			return null;
		}
			
		lastShotTime = System.currentTimeMillis();
		
		Projectile[] out = new Projectile[1];
		out[0] = new Projectile(pos, 0, -PROJECTILE_SPEED, Projectile.GRAVITY);
		return out;

	}

	@Override
	public String imgPath() {
		return "res/monster.png";

	}

	@Override
	public int getPoints() {
		return 50;
	}
}
