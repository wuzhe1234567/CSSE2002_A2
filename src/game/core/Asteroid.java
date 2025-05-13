package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents an Asteroid in the game.
 */
public class Asteroid extends DescendingEnemy {

    /**
     * Creates a new Asteroid instance with coordinates x and y.
     * @param x the x Coordinate
     * @param y the y Coordinate
     */
    public Asteroid(int x, int y) {
        super(x, y);
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.
     *
     * The text representation is "🌑".
     * The image path is "assets/asteroid.png".
     *
     * @return the appropriate new ObjectGraphic
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("🌑", "assets/asteroid.png");
    }
}

