// File: src/game/GameModel.java
package game;

import game.core.Asteroid;
import game.core.Bullet;
import game.core.Enemy;
import game.core.HealthPowerUp;
import game.core.PowerUp;
import game.core.ShieldPowerUp;
import game.core.Ship;
import game.core.SpaceObject;
import game.achievements.PlayerStatsTracker;
import game.utility.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game information and state. Stores and manipulates the game state.
 */
public class GameModel {
    public static final int GAME_HEIGHT = 20;
    public static final int GAME_WIDTH = 10;
    public static final int START_SPAWN_RATE = 2;
    public static final int SPAWN_RATE_INCREASE = 5;
    public static final int START_LEVEL = 1;
    public static final int SCORE_THRESHOLD = 100;
    public static final int ASTEROID_DAMAGE = 10;
    public static final int ENEMY_DAMAGE = 20;
    public static final double ENEMY_SPAWN_RATE = 0.5;
    public static final double POWER_UP_SPAWN_RATE = 0.25;

    public final Random random = new Random();
    private final List<SpaceObject> spaceObjects;
    private final Ship ship;
    private int level;
    private int spawnRate;
    private final Logger logger;
    private final PlayerStatsTracker statsTracker;
    private boolean verbose = false;

    /**
     * Models a game, storing and modifying data relevant to the game.
     *
     * @param logger       a functional interface for passing information between classes.
     * @param statsTracker a PlayerStatsTracker instance to record stats.
     * @requires logger != null, statsTracker != null
     */
    public GameModel(Logger logger, PlayerStatsTracker statsTracker) {
        if (logger == null || statsTracker == null) {
            throw new IllegalArgumentException("logger and statsTracker must not be null");
        }
        this.logger = logger;
        this.statsTracker = statsTracker;
        this.spaceObjects = new ArrayList<>();
        this.level = START_LEVEL;
        this.spawnRate = START_SPAWN_RATE;
        this.ship = new Ship();
    }

    /** Exposed for controller: updates state by delegating to updateGame */
    public void updateState(int tick) {
        updateGame(tick);
    }

    /** Exposed for controller: processes player input */
    public void processInput(String key) {
        if ("fire".equalsIgnoreCase(key)) {
            fireBullet();
            statsTracker.recordShotFired();
        }
        logger.log("Input: " + key);
    }

    /** Exposed for controller: returns current frame data */
    public Object getCurrentFrame() {
        return new ArrayList<>(spaceObjects);
    }

    /** Exposed for controller: returns current score */
    public int getScore() {
        return ship.getScore();
    }

    /** Exposed for controller: returns stats tracker */
    public PlayerStatsTracker getStats() {
        return statsTracker;
    }

    /** Returns the ship instance in the game. */
    public Ship getShip() {
        return ship;
    }

    /** Returns a list of all SpaceObjects in the game. */
    public List<SpaceObject> getSpaceObjects() {
        return new ArrayList<>(spaceObjects);
    }

    /** Returns the current level. */
    public int getLevel() {
        return level;
    }

    /** Returns the current player stats tracker. */
    public PlayerStatsTracker getStatsTracker() {
        return statsTracker;
    }

    /**
     * Adds a SpaceObject to the game.
     *
     * @param object the SpaceObject to be added to the game.
     * @requires object != null
     */
    public void addObject(SpaceObject object) {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null");
        }
        spaceObjects.add(object);
    }

    /**
     * Moves all objects and updates the game state.
     * Objects should be moved by calling .tick(tick) on each object.
     * The game state is updated by removing out-of-bound objects during the tick.
     *
     * @param tick the tick value passed through to the objects tick() method.
     */
    public void updateGame(int tick) {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            obj.tick(tick);
            if (!isInBounds(obj)) {
                toRemove.add(obj);
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Spawns new objects (Asteroids, Enemies, and PowerUps) at random positions.
     * Uses this.random to make EXACTLY 6 calls to random.nextInt() and 1 random.nextBoolean().
     */
    public void spawnObjects() {
        // 1. Asteroid spawn check
        int roll1 = random.nextInt(100);
        // 2. Asteroid x position
        int x1 = random.nextInt(GAME_WIDTH);
        if (roll1 < spawnRate && !collidesWithShipOrObject(x1, 0)) {
            spaceObjects.add(new Asteroid(x1, 0));
        }
        // 3. Enemy spawn check
        int roll2 = random.nextInt(100);
        // 4. Enemy x position
        int x2 = random.nextInt(GAME_WIDTH);
        if (roll2 < spawnRate * ENEMY_SPAWN_RATE && !collidesWithShipOrObject(x2, 0)) {
            spaceObjects.add(new Enemy(x2, 0));
        }
        // 5. PowerUp spawn check
        int roll3 = random.nextInt(100);
        // 6. PowerUp x position
        int x3 = random.nextInt(GAME_WIDTH);
        boolean spawnPU = roll3 < spawnRate * POWER_UP_SPAWN_RATE;
        // 7. PowerUp type
        boolean kind = random.nextBoolean();
        if (spawnPU && !collidesWithShipOrObject(x3, 0)) {
            spaceObjects.add(kind
                ? new ShieldPowerUp(x3, 0)
                : new HealthPowerUp(x3, 0));
        }
    }

    // Helper to prevent spawning on ship or existing objects
    private boolean collidesWithShipOrObject(int x, int y) {
        if (ship.getX() == x && ship.getY() == y) return true;
        for (SpaceObject obj : spaceObjects) {
            if (obj.getX() == x && obj.getY() == y) return true;
        }
        return false;
    }

    /** Detects and handles collisions between spaceObjects. */
    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        // Ship collisions
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (!(obj instanceof Bullet)
                && ship.getX() == obj.getX()
                && ship.getY() == obj.getY()) {

                if (obj instanceof PowerUp) {
                    ((PowerUp) obj).applyEffect(ship);
                    if (verbose) logger.log("PowerUp collected: " + obj.render());
                } else if (obj instanceof Asteroid) {
                    ship.takeDamage(ASTEROID_DAMAGE);
                    if (verbose) logger.log("Hit by asteroid! Health reduced by " + ASTEROID_DAMAGE + ".");
                } else if (obj instanceof Enemy) {
                    ship.takeDamage(ENEMY_DAMAGE);
                    if (verbose) logger.log("Hit by enemy! Health reduced by " + ENEMY_DAMAGE + ".");
                }
                toRemove.add(obj);
            }
        }
        // Bullet collisions
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (obj instanceof Bullet) {
                for (SpaceObject other : spaceObjects) {
                    if (other instanceof Enemy
                        && obj.getX() == other.getX()
                        && obj.getY() == other.getY()) {
                        toRemove.add(obj);
                        toRemove.add(other);
                        statsTracker.recordShotHit();
                        break;
                    } else if (other instanceof Asteroid
                        && obj.getX() == other.getX()
                        && obj.getY() == other.getY()) {
                        toRemove.add(obj);
                        break;
                    }
                }
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * If level progression requirements are satisfied, levels up the game by increasing
     * the spawn rate and level number.
     */
    public void levelUp() {
        if (ship.getScore() >= level * SCORE_THRESHOLD) {
            level++;
            spawnRate += SPAWN_RATE_INCREASE;
            if (verbose) {
                logger.log("Level Up! Welcome to Level " + level +
                           ". Spawn rate increased to " + spawnRate + "%.");
            }
        }
    }

    /** Fires a Bullet from the ship's current position. */
    public void fireBullet() {
        spaceObjects.add(new Bullet(ship.getX(), ship.getY()));
    }

    /** Sets the seed of the Random instance. */
    public void setRandomSeed(int seed) {
        random.setSeed(seed);
    }

    /**
     * Checks if the game is over. The game is considered over if the ship health is <= 0.
     *
     * @return true if the ship health is <= 0, false otherwise.
     */
    public boolean checkGameOver() {
        return ship.getHealth() <= 0;
    }

    /**
     * Checks if the given SpaceObject is inside the game bounds.
     *
     * @param spaceObject the SpaceObject to check
     * @return true if the SpaceObject is in bounds, false otherwise
     * @requires spaceObject != null
     */
    public static boolean isInBounds(SpaceObject spaceObject) {
        if (spaceObject == null) {
            throw new IllegalArgumentException("spaceObject must not be null");
        }
        int x = spaceObject.getX();
        int y = spaceObject.getY();
        return x >= 0 && x < GAME_WIDTH && y >= 0 && y < GAME_HEIGHT;
    }

    /** Sets verbose state to the provided input. */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}

