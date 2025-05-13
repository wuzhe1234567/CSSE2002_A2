package game;


import game.core.*;
import game.utility.Logger;
import game.core.SpaceObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game information and state. Stores and manipulates the game state.
 */
public class GameModel {
    public static final int GAME_HEIGHT = 20;
    public static final int GAME_WIDTH = 10;
    public static final int START_SPAWN_RATE = 2; // spawn rate (percentage chance per tick)
    public static final int SPAWN_RATE_INCREASE = 5; // Increase spawn rate by 5% per level
    public static final int START_LEVEL = 1; // Starting level value
    public static final int SCORE_THRESHOLD = 100; // Score threshold for leveling
    public static final int ASTEROID_DAMAGE = 10; // The amount of damage an asteroid deals
    public static final int ENEMY_DAMAGE = 20; // The amount of damage an enemy deals
    public static final double ENEMY_SPAWN_RATE = 0.5; // Percentage of asteroid spawn chance
    public static final double POWER_UP_SPAWN_RATE = 0.25; // Percentage of asteroid spawn chance

    private final Random random = new Random(); // ONLY USED IN this.spawnObjects()
    private final List<SpaceObject> spaceObjects; // List of all objects
    private Ship boat; // Core.Ship starts at (5, 10) with 100 health
    private int lvl; // The current game level
    private int spawnRate; // The current game spawn rate
    private Logger wrter; // The Logger reference used for logging.

    /**
     * Models a game, storing and modifying data relevant to the game.<br>
     * <p>
     * Logger argument should be a method reference to a .log method such as the UI.log method.<br>
     * Example: Model gameModel = new GameModel(ui::log)<br>
     * <p>
     * - Instantiates an empty list for storing all SpaceObjects (except the ship) that the model needs to track.<br>
     * - Instantiates the game level with the starting level value.<br>
     * - Instantiates the game spawn rate with the starting spawn rate.<br>
     * - Instantiates a new ship. (The ship should not be stored in the SpaceObjects list)<br>
     * - Stores reference to the given logger.<br>
     *
     * @param wrter a functional interface for passing information between classes.
     */
    public GameModel(Logger wrter) {
        spaceObjects = new ArrayList<>();
        lvl = START_LEVEL;
        spawnRate = START_SPAWN_RATE;
        boat = new Ship();
        this.wrter = wrter;
    }

    /**
     * Returns the ship instance in the game.
     *
     * @return the current ship instance.
     */
    public Ship getShip() {
        return boat;
    }

    /**
     * Returns a list of all SpaceObjects in the game.
     *
     * @return a list of all spaceObjects.
     */
    public List<SpaceObject> getSpaceObjects() {
        return spaceObjects;
    }

    /**
     * Returns the current level.
     *
     * @return the current level.
     */
    public int getLevel() {
        return lvl;
    }

    /**
     * Adds a SpaceObject to the game.<br>
     * <p>
     * Objects are considered part of the game only when they are tracked by the model.<br>
     *
     * @param object the SpaceObject to be added to the game.
     * @requires object != null.
     */
    public void addObject(SpaceObject object) {
        this.spaceObjects.add(object);
    }

    /**
     * Updates the game state by moving all objects and then removing off-screen objects.<br>
     * <p>
     * Objects should be moved by calling .tick(tick) on each object.<br>
     * Objects are considered off-screen if they are at y-coordinate &gt; GAME_HEIGHT.<br>
     *
     * @param tick the tick value passed through to the objects tick() method.
     */
    public void updateGame(int tick) {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            obj.tick(tick); // Move objects downward
            if (obj.getY() > GAME_HEIGHT) { // Remove objects that move off-screen
                toRemove.add(obj);
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Spawns new objects (asteroids, enemies, and power-ups) at random positions.
     * Uses this.random to make EXACTLY 6 calls to random.nextInt() and 1 random.nextBoolean.
     * <p>
     * Random calls should be in the following order:<br>
     * 1. Check if an asteroid should spawn (random.nextInt(100) &lt; spawnRate)<br>
     * 2. If spawning an asteroid, spawn at x-coordinate = random.nextInt(GAME_WIDTH)<br>
     * 3. Check if an enemy should spawn (random.nextInt(100) &lt; spawnRate * ENEMY_SPAWN_RATE)<br>
     * 4. If spawning an enemy, spawn at x-coordinate = random.nextInt(GAME_WIDTH)<br>
     * 5. Check if a power-up should spawn (random.nextInt(100) &lt; spawnRate * POWER_UP_SPAWN_RATE)<br>
     * 6. If spawning a power-up, spawn at x-coordinate = random.nextInt(GAME_WIDTH)<br>
     * 7. If spawning a power-up, spawn a ShieldPowerUp if random.nextBoolean(), else a HealthPowerUp.<br>
     * <p>
     * Failure to match random calls correctly will result in failed tests.<br>
     * <p>
     * Objects spawn at y = 0 (top of the screen).<br>
     * Objects may not spawn if there is a ship at the intended spawn location.<br>
     * This should NOT impact calls to random.<br>
     */
    public void spawnObjects() {
        // Spawn asteroids with a chance determined by spawnRate
        if (random.nextInt(100) < spawnRate) {
            int x = random.nextInt(GAME_WIDTH); // Random x-coordinate
            int y = 0; // Spawn at the top of the screen
            if (!isCollidingWithShip(x, y)) {
                spaceObjects.add(new Asteroid(x, y));
            }
        }

        // Spawn enemies with a lower chance
        // Half the rate of asteroids
        if (random.nextInt(100) < spawnRate * ENEMY_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            if (!isCollidingWithShip(x, y)) {
                spaceObjects.add(new Enemy(x, y));
            }
        }

        // Spawn power-ups with an even lower chance
        // One-fourth the spawn rate of asteroids
        if (random.nextInt(100) < spawnRate * POWER_UP_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            PowerUp powerUp = random.nextBoolean() ? new ShieldPowerUp(x, y) :
                    new HealthPowerUp(x, y);
            if (!isCollidingWithShip(x, y)) {
                spaceObjects.add(powerUp);
            }
        }
    }

    /**
     * Checks if a given position would collide with the ship.
     *
     * @param x the x-coordinate to check.
     * @param y the y-coordinate to check.
     * @return true if the position collides with the ship, false otherwise.
     */
    private boolean isCollidingWithShip(int x, int y) {
        return (boat.getX() == x) && (boat.getY() == y);
    }

    /**
     * If level progression requirements are satisfied, levels up the game by
     * increasing the spawn rate and level number.<br>
     * <p>
     * To level up, the score must not be less than the current level multiplied by the score threshold.<br>
     * To increase the level the spawn rate should increase by SPAWN_RATE_INCREASE, and the level number should increase by 1.<br>
     * If the level is increased, log the following:
     * "Level Up! Welcome to Level {new level}. Spawn rate increased to {new spawn rate}%."<br>
     * @hint score is not stored in the GameModel.
     */
    public void levelUp() {
        if (boat.getScore() < lvl * SCORE_THRESHOLD) {
            return;
        }
        lvl++;
        spawnRate += SPAWN_RATE_INCREASE;
        wrter.log("Level Up! Welcome to Level " + lvl + ". Spawn rate increased to "
                + spawnRate + "%.");
    }

    /**
     * Fires a bullet from the ship's current position.<br>
     * <p>
     * Creates a new bullet at the coordinates the ship occupies.<br>
     * Logs "Core.Bullet fired!"<br>
     */
    public void fireBullet() {
        int bulletX = boat.getX();
        int bulletY = boat.getY(); // Core.Bullet starts just above the ship
        spaceObjects.add(new Bullet(bulletX, bulletY));
        wrter.log("Core.Bullet fired!");
    }

    /**
     * Detects and handles collisions between spaceObjects (Ship and Bullet collisions).<br>
     * Objects are considered to be colliding if they share x and y coordinates.<br>
     * <p>
     * First checks ship collision:
     * - If the ship is colliding with a powerup, apply the effect, and
     * .log("Power-up collected: " + obj.render())<br>
     * - If the ship is colliding with an asteroid, take the appropriate damage, and
     * .log("Hit by asteroid! Health reduced by " + ASTEROID_DAMAGE + ".")<br>
     * - If the ship is colliding with an enemy, take the appropriate damage, and
     * .log("Hit by enemy! Health reduced by " + ENEMY_DAMAGE + ".")<br>
     * For any collisions with the ship, the colliding object should be removed.<br>
     * <p>
     * Then check bullet collision:<br>
     * If a bullet collides with an enemy, remove both the enemy and the bullet. No logging required.<br>
     */
    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            // Skip checking Ships (No ships should be in this list)
            if (obj instanceof Ship) {
                continue;
            }
            // Check Ship collision (except Bullets)
            if (isCollidingWithShip(obj.getX(), obj.getY()) && !(obj instanceof Bullet)) {
                // Handle collision effects
                switch (obj) {
                    case PowerUp powerUp -> {
                        powerUp.applyEffect(boat);
                        wrter.log("Power-up collected: " + obj.render());
                    }
                    case Asteroid asteroid -> {
                        boat.takeDamage(ASTEROID_DAMAGE);
                        wrter.log("Hit by asteroid! Health reduced by " + ASTEROID_DAMAGE + ".");
                    }
                    case Enemy enemy -> {
                        boat.takeDamage(ENEMY_DAMAGE);
                        wrter.log("Hit by enemy! Health reduced by " + ENEMY_DAMAGE + ".");
                    }
                    default -> {
                    }
                }
                wrter.log("Collision with: " + obj);
                toRemove.add(obj);
                continue;
            }
        }

        for (SpaceObject obj : spaceObjects) {
            // Check only Bullets
            if (!(obj instanceof Bullet)) {
                continue;
            }
            // Check Bullet collision
            for (SpaceObject other : spaceObjects) {
                // Check only Enemies
                if (!(other instanceof Enemy)) {
                    continue;
                }
                if ((obj.getX() == other.getX()) && (obj.getY() == other.getY())) {
                    toRemove.add(obj);  // Remove bullet
                    toRemove.add(other); // Remove enemy
                    break;
                }
            }
        }

        spaceObjects.removeAll(toRemove); // Remove all collided objects
    }

    /**
     * Sets the seed of the Random instance created in the constructor using .setSeed().<br>
     * <p>
     * This method should NEVER be called.
     *
     * @param seed to be set for the Random instance
     * @provided
     */
    public void setRandomSeed(int seed) {
        this.random.setSeed(seed);
    }
}
