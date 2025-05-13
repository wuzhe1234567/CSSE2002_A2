package game;

import game.core.SpaceObject;
import game.core.PowerUp;
import game.core.Asteroid;
import game.core.Enemy;
import game.core.Bullet;
import game.core.Ship;
import game.core.ShieldPowerUp;
import game.core.HealthPowerUp;
import game.achievements.PlayerStatsTracker;
import game.utility.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game information and state. Stores and manipulates the game state.
 */
public class GameModel {
    // ... existing fields and constructor ...

    /**
     * Exposed for controller: updates state by delegating to updateGame
     */
    public void updateState(int tick) {
        updateGame(tick);
    }

    /**
     * Exposed for controller: processes player input
     */
    public void processInput(String key) {
        // Example: handle fire command
        if ("fire".equalsIgnoreCase(key)) {
            fireBullet();
            statsTracker.recordShotFired();
        }
        logger.log("Input: " + key);
    }

    /**
     * Exposed for controller: returns current frame data
     */
    public Object getCurrentFrame() {
        return new ArrayList<>(spaceObjects);
    }

    /**
     * Exposed for controller: returns current score
     */
    public int getScore() {
        return ship.getScore();
    }

    /**
     * Exposed for controller: returns stats tracker
     */
    public PlayerStatsTracker getStats() {
        return statsTracker;
    }
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
     * Adds a SpaceObject to the game.
     * @param object the SpaceObject to be added. Requires object is not null.
     */
    public void addObject(SpaceObject object) {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null");
        }
        spaceObjects.add(object);
    }

    /**
     * Moves all objects and removes out-of-bounds objects.
     * @param tick the tick value passed to objects.
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
     * Uses this.random to make EXACTLY 6 nextInt calls and 1 nextBoolean.
     */
    public void spawnObjects() {
        // 1. Asteroid spawn check
        int roll1 = random.nextInt(100);
        // 2. Asteroid spawn position
        int x1 = random.nextInt(GAME_WIDTH);
        if (roll1 < spawnRate && !collidesWithShipOrObject(x1, 0)) {
            spaceObjects.add(new Asteroid(x1, 0));
        }
        // 3. Enemy spawn check
        int roll2 = random.nextInt(100);
        // 4. Enemy spawn position
        int x2 = random.nextInt(GAME_WIDTH);
        if (roll2 < spawnRate * ENEMY_SPAWN_RATE && !collidesWithShipOrObject(x2, 0)) {
            spaceObjects.add(new Enemy(x2, 0));
        }
        // 5. PowerUp spawn check
        int roll3 = random.nextInt(100);
        // 6. PowerUp spawn position
        int x3 = random.nextInt(GAME_WIDTH);
        boolean spawnPU = roll3 < spawnRate * POWER_UP_SPAWN_RATE;
        // 7. PowerUp type
        boolean kind = random.nextBoolean();
        if (spawnPU && !collidesWithShipOrObject(x3, 0)) {
            spaceObjects.add(kind ? new ShieldPowerUp(x3, 0) : new HealthPowerUp(x3, 0));
        }
    }

    private boolean collidesWithShipOrObject(int x, int y) {
        if (ship.getX() == x && ship.getY() == y) return true;
        for (SpaceObject obj : spaceObjects) {
            if (obj.getX() == x && obj.getY() == y) return true;
        }
        return false;
    }

    /**
     * Detects and handles collisions: Ship vs Objects, then Bullet vs Enemies/Asteroids.
     */
    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        // Ship collisions
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (!(obj instanceof Bullet) && ship.getX() == obj.getX() && ship.getY() == obj.getY()) {
                if (obj instanceof PowerUp) {
                    ((PowerUp) obj).applyEffect(ship);
                    if (verbose) logger.log("PowerUp collected: " + obj.render());
                } else if (obj instanceof Asteroid) {
                    ship.takeDamage(ASTEROID_DAMAGE);
                    if (verbose) logger.log("Hit by " + obj.render() + "! Health reduced by " + ASTEROID_DAMAGE + ".");
                } else if (obj instanceof Enemy) {
                    ship.takeDamage(ENEMY_DAMAGE);
                    if (verbose) logger.log("Hit by " + obj.render() + "! Health reduced by " + ENEMY_DAMAGE + ".");
                }
                toRemove.add(obj);
            }
        }
        // Bullet collisions
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (obj instanceof Bullet) {
                for (SpaceObject other : spaceObjects) {
                    if (other instanceof Enemy && obj.getX() == other.getX() && obj.getY() == other.getY()) {
                        toRemove.add(obj);
                        toRemove.add(other);
                        statsTracker.recordShotHit();
                        break;
                    } else if (other instanceof Asteroid && obj.getX() == other.getX() && obj.getY() == other.getY()) {
                        toRemove.add(obj);
                        break;
                    }
                }
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Levels up game if threshold met. Logs if verbose.
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
     * Fires a Bullet from the ship's position.
     */
    public void fireBullet() {
        spaceObjects.add(new Bullet(ship.getX(), ship.getY()));
    }

    /**
     * Sets random seed. Should never be called in production.
     */
    public void setRandomSeed(int seed) {
        random.setSeed(seed);
    }

    /**
     * Checks if the game is over (ship health <= 0).
     */
    public boolean checkGameOver() {
        return ship.getHealth() <= 0;
    }

    /**
     * Checks if given SpaceObject is in bounds.
     */
    public static boolean isInBounds(SpaceObject spaceObject) {
        if (spaceObject == null) throw new IllegalArgumentException("spaceObject must not be null");
        int x = spaceObject.getX();
        int y = spaceObject.getY();
        return x >= 0 && x < GAME_WIDTH && y >= 0 && y < GAME_HEIGHT;
    }

    /**
     * Sets verbose logging.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Returns the current ship instance.
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns a copy of the list of space objects.
     */
    public List<SpaceObject> getSpaceObjects() {
        return new ArrayList<>(spaceObjects);
    }

    /**
     * Returns the current level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the player stats tracker.
     */
    public PlayerStatsTracker getStatsTracker() {
        return statsTracker;
    }
}
