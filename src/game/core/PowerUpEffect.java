package game.core;

/**
 * Represents the effect of a PowerUp in the game.
 */
public interface PowerUpEffect {
    /**
     * Applies the PowerUp's effect to the specified ship.
     *
     * @param ship the ship to apply the effect to
     */
    void applyEffect(Ship ship);
}
