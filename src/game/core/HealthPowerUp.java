package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents a health PowerUp in the game.
 */
public class HealthPowerUp extends PowerUp {

    public HealthPowerUp(int x, int y) {
        super(x, y);
    }

    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("❤️", "assets/health.png");
    }

    @Override
    public void applyEffect(Ship ship) {
        ship.heal(20);
    }
}
