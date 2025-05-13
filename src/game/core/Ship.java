// File: src/game/core/Ship.java
package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents the player's ship.
 */
public class Ship extends Controllable implements Healable {
    private static final int STARTING_HEALTH = 100;
    private static final int STARTING_SCORE = 0;
    private static final int STARTING_X = 5;
    private static final int STARTING_Y = 10;

    private int health;
    private int score;

    /**
     * Constructs a Ship with the specified position and health.
     * Also initialises score to be 0.
     *
     * @param x the initial x coordinate
     * @param y the initial y coordinate
     * @param health the initial health of the ship
     */
    public Ship(int x, int y, int health) {
        super(x, y);
        this.health = health;
        this.score = STARTING_SCORE;
    }

    /**
     * Constructs a Ship with default position and health.
     *
     * By default, a ship should be at position x = 5 and y = 10, with 100 points of health.
     */
    public Ship() {
        this(STARTING_X, STARTING_Y, STARTING_HEALTH);
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.
     *
     * The text representation is "🚀".
     * The image path is "assets/ship.png".
     *
     * @return the appropriate new ObjectGraphic
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("🚀", "assets/ship.png");
    }

    /**
     * Reduces the ship's health by the specified damage amount.
     * A ship's health can never fall below 0.
     *
     * @param damage the amount of damage taken
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * Heals the ship by the specified amount.
     * A ship's health can never rise above 100.
     *
     * @param amount the amount of health restored
     */
    @Override
    public void heal(int amount) {
        health += amount;
        if (health > STARTING_HEALTH) {
            health = STARTING_HEALTH;
        }
    }

    /**
     * Adds points to the ship's score.
     *
     * @param points the points to add
     */
    public void addScore(int points) {
        score += points;
    }

    /**
     * Returns the current health of the ship.
     *
     * @return the current health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the current score of the ship.
     *
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * As Ships have no tick-dependent behaviour, this method should be left blank.
     *
     * @param tick the given game tick
     */
    @Override
    public void tick(int tick) {
        // No tick-dependent behaviour
    }
}
