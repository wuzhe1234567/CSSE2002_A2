package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents a shield PowerUp in the game.
 */
public class ShieldPowerUp extends PowerUp {
    /**
     * Creates a new ShieldPowerUp with the given coordinates.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public ShieldPowerUp(int x, int y) {
        super(x, y);
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.
     * The text representation is "💠".
     * The image path is "assets/shield.png".
     *
     * @return the appropriate new ObjectGraphic.
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("💠", "assets/shield.png");
    }

    /**
     * Applies the shield effect to the ship, increasing the score by 50.
     *
     * @param ship the ship to apply the effect to
     */
    @Override
    public void applyEffect(Ship ship) {
        ship.addScore(50);
    }
}


