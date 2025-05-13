package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents a health PowerUp in the game.
 */
public class HealthPowerUp extends PowerUp {

    /**
     * Creates a health PowerUp at the given coordinates
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public HealthPowerUp(int x, int y) {
        super(x, y);
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.
     *
     * The text representation is "❤️".
     * The image path is "assets/health.png".
     *
     * @return the appropriate new ObjectGraphic
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("❤️", "assets/health.png");
    }

    /**
     * Applies the health effect to the ship, healing it for 20 health.
     *
     * @param ship the ship to apply the effect to
     */
    @Override
    public void applyEffect(Ship ship) {
        // direct call to Ship.heal; no Healable interface needed
        ship.heal(20);
    }
}
