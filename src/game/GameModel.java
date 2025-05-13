ackage game;

import game.core.*;
import game.utility.Logger;
import game.achievements.PlayerStatsTracker;
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
     * @param logger a functional interface for passing information between classes.
     * @param statsTracker a PlayerStatsTracker instance to record stats.
     * @requires logger is not null, statsTracker is not null
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

    /**
     * Returns the ship instance in the game.
     *
     * @return the current ship instance.
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns a list of all SpaceObjects in the game.
     *
     * @return a list of all spaceObjects.
     */
    public List<SpaceObject> getSpaceObjects() {
        return new ArrayList<>(spaceObjects);
    }

    /**
     * Returns the current level.
     *
     * @return the current level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the current player stats tracker.
     *
     * @return the current player stats tracker.
     */
    public PlayerStatsTracker getStatsTracker() {
        return statsTracker;
    }

    /**
     * Adds a SpaceObject to the game.
     *
     * @param object the SpaceObject to be added to the game.
     * @requires object is not null.
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
     * Spawns new objects (Asteroids, Enemies, and PowerUp) at random positions.
     * Uses this.random to make EXACTLY 6 calls to random.nextInt() and 1 random.nextBoolean.
     *
     * Random calls order:
     * 1. random.nextInt(100)
     * 2. random.nextInt(GAME_WIDTH)
     * 3. random.nextInt(100)
     * 4. random.nextInt(GAME_WIDTH)
     * 5. random.nextInt(100)
     * 6. random.nextInt(GAME_WIDTH)
     * 7. random.nextBoolean()
     *
     * @requires random and ship initialized
     */
    public void spawnObjects() {
        // 1
        int roll1 = random.nextInt(100);
        // 2
        int x1 = random.nextInt(GAME_WIDTH);
        if (roll1 < spawnRate && !collidesWithShip(x1, 0)) {
            spaceObjects.add(new Asteroid(x1, 0));
        }
        // 3
        int roll2 = random.nextInt(100);
        // 4
        int x2 = random.nextInt(GAME_WIDTH);
        if (roll2 < spawnRate * ENEMY_SPAWN_RATE && !collidesWithShip(x2, 0)) {
            spaceObjects.add(new Enemy(x2, 0));
        }
        // 5
        int roll3 = random.nextInt(100);
        // 6
        int x3 = random.nextInt(GAME_WIDTH);
        boolean spawnPU = roll3 < spawnRate * POWER_UP_SPAWN_RATE;
        // 7
        boolean kind = random.nextBoolean();
        if (spawnPU && !collidesWithShip(x3, 0)) {
            spaceObjects.add(kind ? new ShieldPowerUp(x3, 0) : new HealthPowerUp(x3, 0));
        }
    }

    private boolean collidesWithShip(int x, int y) {
        return ship.getX() == x && ship.getY() == y;
    }

    /**
     * If level progression requirements are satisfied, levels up the game by increasing the spawn rate and level number.
     * To level up, the score must not be less than the current level multiplied by the score threshold.
     * To increase the level the spawn rate should increase by SPAWN_RATE_INCREASE, and the level number should increase by 1.
     * If the level is increased, and verbose is set to true, log the following:
     * "Level Up! Welcome to Level {new level}. Spawn rate increased to {new spawn rate}%."
     */
    public void levelUp() {
        if (ship.getScore() >= level * SCORE_THRESHOLD) {
            level++;
            spawnRate += SPAWN_RATE_INCREASE;
            if (verbose) {
                logger.log("Level Up! Welcome to Level " + level + ". Spawn rate increased to " + spawnRate + "%.");
            }
        }
    }

    /**
     * Fires a Bullet from the ship's current position.
     * Creates a new Bullet at the coordinates the ship occupies.
     */
    public void fireBullet() {
        int x = ship.getX();
        int y = ship.getY();
        addObject(new Bullet(x, y));
    }

    /**
     * Detects and handles collisions between spaceObjects (Ship and Bullet collisions).
     * Objects are considered to collide if they share x and y coordinates.
     * First checks ship collisions, then bullet collisions.
     */
    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (!(obj instanceof Bullet) && collidesWithShip(obj.getX(), obj.getY())) {
                if (obj instanceof PowerUp pu) {
                    pu.applyEffect(ship);
                } else if (obj instanceof Asteroid) {
                    ship.takeDamage(ASTEROID_DAMAGE);
                } else if (obj instanceof Enemy) {
                    ship.takeDamage(ENEMY_DAMAGE);
                }
                toRemove.add(obj);
            }
        }
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (obj instanceof Bullet b) {
                for (SpaceObject other : spaceObjects) {
                    if (other instanceof Enemy e && b.getX() == e.getX() && b.getY() == e.getY()) {
                        toRemove.add(b);
                        toRemove.add(e);
                        statsTracker.recordShotHit();
                        break;
                    }
                }
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Sets the seed of the Random instance created in the constructor using .setSeed().
     * This method should NEVER be called.
     *
     * @param seed to be set for the Random instance
     */
    public void setRandomSeed(int seed) {
        random.setSeed(seed);
    }

    /**
     * Checks if the game is over.
     * The game is considered over if the Ship health is <= 0.
     *
     * @return true if the Ship health is <= 0, false otherwise
     */
    public boolean checkGameOver() {
        return ship.getHealth() <= 0;
    }

    /**
     * Checks if the given SpaceObject is inside the game bounds.
     *
     * @param spaceObject the SpaceObject to check
     * @return true if the SpaceObject is in bounds, false otherwise
     * @requires spaceObject is not Null
     */
    public static boolean isInBounds(SpaceObject spaceObject) {
        if (spaceObject == null) {
            throw new IllegalArgumentException("spaceObject must not be null");
        }
        int x = spaceObject.getX();
        int y = spaceObject.getY();
        return x >= 0 && x < GAME_WIDTH && y >= 0 && y < GAME_HEIGHT;
    }

    /**
     * Sets verbose state to the provided input.
     *
     * @param verbose whether to set verbose state to true or false.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
