package game;

import game.achievements.PlayerStatsTracker;
import game.core.*;
import game.utility.Logger;
import game.core.SpaceObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game information and state.
 * Stores and manipulates the game state.
 */
public class GameModel {
    public static final int GAME_HEIGHT = 20;
    public static final int GAME_WIDTH = 10;
    public static final int START_SPAWN_RATE = 2; // Initial spawn rate (percentage chance per tick)
    public static final int SPAWN_RATE_INCREASE = 5; // Spawn rate increases by 5% per level
    public static final int START_LEVEL = 1;         // Initial level value
    public static final int SCORE_THRESHOLD = 100;     // Score threshold needed for leveling up
    public static final int ASTEROID_DAMAGE = 10;      // Damage dealt by an asteroid
    public static final int ENEMY_DAMAGE = 20;         // Damage dealt by an enemy
    public static final double ENEMY_SPAWN_RATE = 0.5;
    // Enemy spawn rate relative to asteroid spawn chance
    public static final double POWER_UP_SPAWN_RATE = 0.25;
    // Power-up spawn rate relative to asteroid spawn chance

    private final Random random = new Random();
    // Used exclusively in spawnObjects() for randomness
    private List<SpaceObject> spaceObjects;
    // List of all space objects in the game (not including the ship)
    private Ship boat; // The ship instance, starting at (5, 10) with 100 health
    private int lvl; // Current game level
    private int spawnRate; // Current spawn rate
    private Logger wrter; // Logger used for outputting information
    private boolean verbose = false; // Controls whether to log additional information
    // Unused instance variables for specific object types:
    private Asteroid asteroid;
    private Enemy enemy;
    private PowerUp powerUp;
    private PlayerStatsTracker playerStatsTracker;

    /**
     * Constructs a game model that stores and modifies game data.
     * <p>
     * The Logger parameter should be a method reference to a logging method (e.g., UI.log).
     * For example: new GameModel(ui::log)
     * <p>
     * The following are initialized:
     * - An empty list for all SpaceObjects (except the ship).
     * - Starting game level.
     * - Initial spawn rate.
     * - A new ship instance (this ship is not part of the space objects list).
     * - The Logger reference.
     *
     * @param wrter              a functional interface for passing information (logging).
     * @param playerStatsTracker tracker for player statistics (may be null).
     */
    public GameModel(Logger wrter, PlayerStatsTracker playerStatsTracker) {
        spaceObjects = new ArrayList<>();
        lvl = START_LEVEL;
        spawnRate = START_SPAWN_RATE;
        boat = new Ship();
        this.wrter = wrter;
        this.playerStatsTracker = playerStatsTracker;
    }

    /**
     * Sets the verbose flag to enable or disable detailed logging.
     *
     * @param verbose if true, additional logging is enabled.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Determines if a given space object's coordinates are within game bounds.
     *
     * @param spaceObject the SpaceObject to check.
     * @return true if the object's coordinates are within bounds, false otherwise.
     * @throws IllegalArgumentException if the spaceObject is null.
     */
    public static boolean isInBounds(SpaceObject spaceObject) {
        if (spaceObject == null) {
            throw new IllegalArgumentException("spaceObject cannot be null");
        }
        int x = spaceObject.getX();
        int y = spaceObject.getY();
        return x >= 0 && x < GAME_WIDTH && y >= 0 && y < GAME_HEIGHT;
    }

    /**
     * Sets the player statistics tracker.
     *
     * @param tracker the PlayerStatsTracker instance.
     */
    public void setPlayerStatsTracker(PlayerStatsTracker tracker) {
        this.playerStatsTracker = tracker;
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
     * Returns the list of all space objects currently in the game.
     *
     * @return list of SpaceObject instances.
     */
    public List<SpaceObject> getSpaceObjects() {
        return spaceObjects;
    }

    /**
     * Returns the current game level.
     *
     * @return the current level.
     */
    public int getLevel() {
        return lvl;
    }

    /**
     * Adds a new space object to the game.
     * The object becomes tracked in the game state.
     *
     * @param object the SpaceObject to add.
     * @requires object != null.
     */
    public void addObject(SpaceObject object) {
        this.spaceObjects.add(object);
    }

    /**
     * Updates the game state by moving each space object and removing those that are off-screen.
     * Each object's tick() method is called with the current tick value.
     *
     * @param tick the tick value passed to each object's tick() method.
     */
    public void updateGame(int tick) {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            obj.tick(tick); // Move object downward
            if (!isInBounds(obj)) {
                toRemove.add(obj);
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Spawns new objects (asteroids, enemies, and power-ups) at random positions.
     * Exactly 6 calls are made to random.nextInt() and 1 call to random.nextBoolean() in a set order.
     * <p>
     * The order of random calls is as follows:
     * 1. Check if an asteroid should spawn (random.nextInt(100) < spawnRate).
     * 2. If so, spawn an asteroid at a random x-coordinate (random.nextInt(GAME_WIDTH)).
     * 3. Check if an enemy should spawn (random.nextInt(100) < spawnRate * ENEMY_SPAWN_RATE).
     * 4. If so, spawn an enemy at a random x-coordinate.
     * 5. Check if a power-up should spawn (random.nextInt(100) < spawnRate * POWER_UP_SPAWN_RATE).
     * 6. If so, spawn a power-up at a random x-coordinate.
     * 7. For the power-up, use random.nextBoolean() to decide between spawning a ShieldPowerUp or a HealthPowerUp.
     * <p>
     * All objects are spawned at y = 0 (top of the screen), and spawning is skipped if the position is already occupied.
     */
    public void spawnObjects() {
        // Spawn asteroid if the random condition is met
        if (random.nextInt(100) < spawnRate) {
            int x = random.nextInt(GAME_WIDTH); // Random x-coordinate
            int y = 0; // Spawn at the top
            if (!isused(x, y)) {
                spaceObjects.add(new Asteroid(x, y));
            }
        }

        // Spawn enemy with a chance relative to asteroid spawn rate (half rate)
        if (random.nextInt(100) < spawnRate * ENEMY_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            if (!isused(x, y)) {
                spaceObjects.add(new Enemy(x, y));
            }
        }

        // Spawn power-up with a chance relative to asteroid spawn rate (one-fourth rate)
        if (random.nextInt(100) < spawnRate * POWER_UP_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            // Use random.nextBoolean() to choose between Shield or Health power-up
            if (!isused(x, y) && random.nextBoolean()) {
                spaceObjects.add(new ShieldPowerUp(x, y));
            } else if (!isused(x, y)) {
                spaceObjects.add(new HealthPowerUp(x, y));
            }
        }
    }

    /**
     * Helper method to check if a given coordinate is already used by an existing space object.
     *
     * @param x the x-coordinate to check.
     * @param y the y-coordinate to check.
     * @return true if there is already an object at the given coordinates, false otherwise.
     */
    private boolean isused(int x, int y) {
        // Assuming 'boat' is the current ship object.
        // Check each space object that has already been generated.
        for (SpaceObject obj : spaceObjects) {
            if (obj.getX() == x && obj.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given coordinates would result in a collision with the ship.
     *
     * @param x the x-coordinate to check.
     * @param y the y-coordinate to check.
     * @return true if the ship occupies the given coordinates, false otherwise.
     */
    private boolean isCollidingWithShip(int x, int y) {
        return (boat.getX() == x) && (boat.getY() == y);
    }

    /**
     * Increases the game level if the ship's score meets or exceeds the required threshold.
     * If leveled up, the spawn rate increases and a log message is output.
     * <p>
     * The score threshold for leveling is (current level * SCORE_THRESHOLD).
     */
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

    /**
     * Fires a bullet from the ship's current position.
     * A new bullet object is created at the ship's location.
     */
    public void fireBullet() {
        int bulletX = boat.getX();
        int bulletY = boat.getY(); // Bullet starts just above the ship
        spaceObjects.add(new Bullet(bulletX, bulletY));
    }

    /**
     * Detects and processes collisions between space objects (collisions involving the ship and bullets).
     * <p>
     * For collisions with the ship (excluding bullets):
     * - If colliding with a power-up, applies its effect and logs a message.
     * - If colliding with an asteroid, reduces the ship's health and logs damage.
     * - If colliding with an enemy, reduces the ship's health and logs damage.
     * Collided objects with the ship are then removed.
     * <p>
     * For bullet collisions:
     * - If a bullet collides with an enemy, both the bullet and the enemy are removed.
     *   Additionally, the player's hit statistic is recorded.
     * - If a bullet collides with an asteroid, only the bullet is removed.
     */
    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        // Check collisions between ship and other objects (excluding bullets)
        for (SpaceObject obj : spaceObjects) {
            if (obj instanceof Ship) {
                continue; // Skip any Ship instances (should not be in the list)
            }
            if (isCollidingWithShip(obj.getX(), obj.getY()) && !(obj instanceof Bullet)) {
                if (obj instanceof PowerUp powerUp) {
                    powerUp.applyEffect(boat);
                    if (verbose) {
                        wrter.log("PowerUp collected: " + obj.render());
                    }
                } else if (obj instanceof Asteroid asteroid) {
                    boat.takeDamage(ASTEROID_DAMAGE);
                    if (verbose) {
                        wrter.log("Hit by " + obj.render()
                                + "! Health reduced by " + ASTEROID_DAMAGE + ".");
                    }
                } else if (obj instanceof Enemy enemy) {
                    boat.takeDamage(ENEMY_DAMAGE);
                    if (verbose) {
                        wrter.log("Hit by " + obj.render()
                                + "! Health reduced by " + ENEMY_DAMAGE + ".");
                    }
                }
                // Remove any object that collides with the ship
                toRemove.add(obj);
            }
        }

        // Create a copy of spaceObjects for bullet collision checks
        // to avoid concurrent modification issues
        List<SpaceObject> objectsCopy = new ArrayList<>(spaceObjects);
        for (SpaceObject bullet : objectsCopy) {
            if (!(bullet instanceof Bullet)) {
                continue;
            }
            // Check collision for each bullet with possible targets (Enemy or Asteroid)
            for (SpaceObject target : objectsCopy) {
                if (target instanceof Enemy || target instanceof Asteroid) {
                    if (bullet.getX() == target.getX() && bullet.getY() == target.getY()) {
                        if (target instanceof Enemy) {
                            // Bullet hits enemy: remove both bullet and enemy, record hit
                            toRemove.add(bullet);
                            toRemove.add(target);
                            if (playerStatsTracker != null) {
                                playerStatsTracker.recordShotHit();
                            }
                            break; // Process one collision per bullet
                        } else if (target instanceof Asteroid) {
                            // Bullet hits asteroid: remove only the bullet
                            toRemove.add(bullet);
                            break;
                        }
                    }
                }
            }
        }

        // Remove all collided objects from the game state
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Sets the seed of the internal Random instance.
     * <p>
     * This method is not intended for normal use.
     *
     * @param seed the seed value used by the Random instance.
     */
    public void setRandomSeed(int seed) {
        this.random.setSeed(seed);
    }

    /**
     * Checks if the game is over, defined as the ship's health being zero or less.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean checkGameOver() {
        return this.getShip().getHealth() <= 0;
    }
}
