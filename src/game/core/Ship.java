package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents the player's ship.
 */
public class Ship extends Controllable {
    private static final int STARTING_HEALTH = 100;
    private static final int STARTING_SCORE = 0;
    private static final int STARTING_X = 5;
    private static final int STARTING_Y = 10;

    private int health;
    private int score;

    public Ship(int x, int y, int health) {
        super(x, y);
        this.health = health;
        this.score = STARTING_SCORE;
    }

    public Ship() {
        this(STARTING_X, STARTING_Y, STARTING_HEALTH);
    }

    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("🚀", "assets/ship.png");
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
        health += amount;
        if (health > STARTING_HEALTH) health = STARTING_HEALTH;
    }

    public void addScore(int points) {
        score += points;
    }

    public int getHealth() {
        return health;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void tick(int tick) {
        // No tick-dependent behaviour
    }
}
