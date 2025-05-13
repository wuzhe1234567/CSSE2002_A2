package game;

import game.core.SpaceObject;
import game.core.PowerUp;
import game.core.Asteroid;
import game.core.Enemy;
import game.core.Bullet;
import game.core.Ship;
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

    private final Random random = new Random();
    private final List<SpaceObject> spaceObjects;
    private final Ship ship;
    private int level;
    private int spawnRate;
    private final Logger logger;
    private final PlayerStatsTracker statsTracker;
    private boolean verbose = false;

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
    
    public void addObject(SpaceObject object) {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null");
        }
        this.spaceObjects.add(object);
    }
    // Exposed for controller
    public void updateState(int tick) {
        updateGame(tick);
    }

    public void processInput(String key) {
        // Example: handle fire command
        if ("fire".equalsIgnoreCase(key)) {
            fireBullet();
            statsTracker.recordShotFired();
        }
        // TODO: other input handling
        logger.log("Input: " + key);
    }

    public Object getCurrentFrame() {
        // Use spaceObjects, ship, etc to build frame data
        return new ArrayList<>(spaceObjects);
    }

    public int getScore() {
        return ship.getScore();
    }

    public PlayerStatsTracker getStats() {
        return statsTracker;
    }

    // Original methods below
    public void updateGame(int tick) {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            obj.tick(tick);
            if (!isInBounds(obj)) toRemove.add(obj);
        }
        spaceObjects.removeAll(toRemove);
    }

    public void spawnObjects() {
        // spawn logic
    }

    public void levelUp() { /* ... */ }
    public void fireBullet() { /* ... */ }
    public void checkCollisions() { /* ... */ }
    public void setRandomSeed(int seed) { random.setSeed(seed); }
    public boolean checkGameOver() { return ship.getHealth() <= 0; }
    public static boolean isInBounds(SpaceObject spaceObject) { /* ... */ return true; }
    public void setVerbose(boolean verbose) { this.verbose = verbose; }
    public Ship getShip() { return ship; }
    public List<SpaceObject> getSpaceObjects() { return new ArrayList<>(spaceObjects); }
    public int getLevel() { return level; }
}
