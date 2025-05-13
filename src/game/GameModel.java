package game;

import game.core.*;
import game.utility.Logger;
import game.core.SpaceObject;
import game.core.PlayerStatsTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private final Random random = new Random(); // ONLY USED IN spawnObjects()
    private final List<SpaceObject> spaceObjects;
    private final Logger wrter;
    private final PlayerStatsTracker statsTracker;
    private Ship boat;
    private int lvl;
    private int spawnRate;
    private boolean verbose = false;

    /**
     * Models a game, storing and modifying data relevant to the game.
     * @param logger       a functional interface for passing information between classes.
     *                     Should not be null.
     * @param statsTracker a PlayerStatsTracker instance to record stats. Should not be null.
     */
    public GameModel(Logger logger, PlayerStatsTracker statsTracker) {
        Objects.requireNonNull(logger, "logger must not be null");
        Objects.requireNonNull(statsTracker, "statsTracker must not be null");
        this.wrter = logger;
        this.statsTracker = statsTracker;
        this.spaceObjects = new ArrayList<>();
        this.lvl = START_LEVEL;
        this.spawnRate = START_SPAWN_RATE;
        this.boat = new Ship();
    }

    public void addObject(SpaceObject object) {
        Objects.requireNonNull(object, "object must not be null");
        spaceObjects.add(object);
    }

    public void updateGame(int tick) {
        // Move all objects
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            obj.tick(tick);
        }
        // Remove out-of-bounds objects
        spaceObjects.removeIf(obj -> !isInBounds(obj));
    }

    public void spawnObjects() {
        // 1. Asteroid chance + coord
        int chanceA = random.nextInt(100);
        int xA = random.nextInt(GAME_WIDTH);
        if (chanceA < spawnRate && isInBounds(new Asteroid(xA, 0))) {
            spaceObjects.add(new Asteroid(xA, 0));
        }
        // 2. Enemy chance + coord
        int chanceE = random.nextInt(100);
        int xE = random.nextInt(GAME_WIDTH);
        if (chanceE < spawnRate * ENEMY_SPAWN_RATE && isInBounds(new Enemy(xE, 0))) {
            spaceObjects.add(new Enemy(xE, 0));
        }
        // 3. PowerUp chance + coord + type
        int chanceP = random.nextInt(100);
        int xP = random.nextInt(GAME_WIDTH);
        boolean shield = random.nextBoolean(); // 7th random call
        if (chanceP < spawnRate * POWER_UP_SPAWN_RATE && isInBounds(new HealthPowerUp(xP, 0))) {
            PowerUp pu = shield
                       ? new ShieldPowerUp(xP, 0)
                       : new HealthPowerUp(xP, 0);
            spaceObjects.add(pu);
        }
    }

    public void fireBullet() {
        Bullet b = new Bullet(boat.getX(), boat.getY());
        spaceObjects.add(b);
        statsTracker.recordShotFired();
    }

    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        // Ship collisions
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (obj.getX() == boat.getX() && obj.getY() == boat.getY()) {
                if (obj instanceof PowerUp) {
                    PowerUp pu = (PowerUp) obj;
                    pu.apply(boat);
                    if (verbose) {
                        wrter.log("PowerUp collected: " + pu.render());
                    }
                    toRemove.add(obj);
                } else if (obj instanceof Asteroid) {
                    boat.takeDamage(ASTEROID_DAMAGE);
                    if (verbose) {
                        wrter.log("Hit by " + obj.render() + "! Health reduced by " + ASTEROID_DAMAGE + ".");
                    }
                    toRemove.add(obj);
                } else if (obj instanceof Enemy) {
                    boat.takeDamage(ENEMY_DAMAGE);
                    if (verbose) {
                        wrter.log("Hit by " + obj.render() + "! Health reduced by " + ENEMY_DAMAGE + ".");
                    }
                    toRemove.add(obj);
                }
            }
        }
        // Bullet collisions
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            if (obj instanceof Bullet) {
                Bullet b = (Bullet) obj;
                for (SpaceObject other : new ArrayList<>(spaceObjects)) {
                    if (other instanceof Enemy
                            && b.getX() == other.getX()
                            && b.getY() == other.getY()) {
                        toRemove.add(b);
                        toRemove.add(other);
                        statsTracker.recordShotHit();
                    } else if (other instanceof Asteroid
                            && b.getX() == other.getX()
                            && b.getY() == other.getY()) {
                        toRemove.add(b);
                    }
                }
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    public boolean checkGameOver() {
        return boat.getHealth() <= 0;
    }

    public Ship getShip() {
        return boat;
    }

    public List<SpaceObject> getSpaceObjects() {
        return new ArrayList<>(spaceObjects);
    }

    public int getLevel() {
        return lvl;
    }

    public PlayerStatsTracker getStatsTracker() {
        return statsTracker;
    }

    public void levelUp() {
        if (boat.getScore() < lvl * SCORE_THRESHOLD) {
            return;
        }
        lvl++;
        spawnRate += SPAWN_RATE_INCREASE;
        if (verbose) {
            wrter.log("Level Up! Welcome to Level " + lvl
                      + ". Spawn rate increased to " + spawnRate + "%.");
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public static boolean isInBounds(SpaceObject spaceObject) {
        Objects.requireNonNull(spaceObject, "spaceObject cannot be null");
        int x = spaceObject.getX();
        int y = spaceObject.getY();
        return x >= 0 && x < GAME_WIDTH && y >= 0 && y < GAME_HEIGHT;
    }

    /**
     * Sets the seed of the Random instance created in the constructor using .setSeed().
     * <p>
     * This method should NEVER be called.
     *
     * @param seed to be set for the Random instance
     */
    public void setRandomSeed(int seed) {
        this.random.setSeed(seed);
    }
}

