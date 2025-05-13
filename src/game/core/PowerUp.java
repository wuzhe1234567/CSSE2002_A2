package game.core;

/**
 * Represents a PowerUp in the game.
 */
public abstract class PowerUp extends ObjectWithPosition implements PowerUpEffect {

    /**
     * Creates a new PowerUp with the given coordinate.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public PowerUp(int x, int y) {
        super(x, y);
    }

    /**
     * Moves PowerUp downwards, once every 10 game ticks.
     *
     * @param tick the given game tick.
     */
    @Override
    public void tick(int tick) {
        if (tick % 10 == 0) {
            y++;
        }
    }
}

