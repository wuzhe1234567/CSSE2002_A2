package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import game.core.SpaceObject;
import game.core.Ship;
import game.core.Asteroid;
import game.core.Bullet;
import game.core.DescendingEnemy;
import game.core.Enemy;
import game.core.HealthPowerUp;
import game.core.ShieldPowerUp;

/**
 * Represents the game information and state. Stores and manipulates the game state.
 */
public class GameModel {
    public static final int GAME_WIDTH = 20;
    public static final int GAME_HEIGHT = 15;
    public static final int START_LEVEL = 1;
    public static final int START_SPAWN_RATE = 5;
    public static final int SPAWN_RATE_INCREASE = 2;
    public static final int SCORE_THRESHOLD = 100;
    public static final double ENEMY_SPAWN_RATE = 0.5;
    public static final double POWER_UP_SPAWN_RATE = 0.2;
    public static final int ASTEROID_DAMAGE = 10;
    public static final int ENEMY_DAMAGE = 20;

    private final Random random = new Random();
    private final List<SpaceObject> spaceObjects = new ArrayList<>();
    private final Ship ship;
    private int level = START_LEVEL;
    private int spawnRate = START_SPAWN_RATE;
    private boolean verbose = false;

    /**
     * Models a game, storing and modifying data relevant to the game.
     */
    public GameModel() {
        this.ship = new Ship();
        spaceObjects.add(ship);
    }

    public Ship getShip() {
        return ship;
    }

    public List<SpaceObject> getSpaceObjects() {
        return new ArrayList<>(spaceObjects);
    }

    public int getLevel() {
        return level;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void addObject(SpaceObject object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        spaceObjects.add(object);
    }

    public void updateGame(int tick) {
        // Move each object
        for (SpaceObject obj : new ArrayList<>(spaceObjects)) {
            obj.tick(tick);
        }
        // Remove out-of-bounds
        Iterator<SpaceObject> it = spaceObjects.iterator();
        while (it.hasNext()) {
            SpaceObject obj = it.next();
            if (!isInBounds(obj)) it.remove();
        }
    }

    public void spawnObjects() {
        // EXACTLY 6 calls to nextInt, 1 nextBoolean
        if (random.nextInt(100) < spawnRate) {
            int x = random.nextInt(GAME_WIDTH);
            addObject(new Asteroid(x, 0));
        }
        if (random.nextInt(100) < spawnRate * ENEMY_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            addObject(new Enemy(x, 0));
        }
        if (random.nextInt(100) < spawnRate * POWER_UP_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            boolean shield = random.nextBoolean();
            if (shield) addObject(new ShieldPowerUp(x, 0));
            else      addObject(new HealthPowerUp(x, 0));
        }
    }

    public void levelUp() {
        if (ship.getScore() >= level * SCORE_THRESHOLD) {
            level++;
            spawnRate += SPAWN_RATE_INCREASE;
            if (verbose) {
                // Logger omitted here; integrate ui::log as needed
                System.out.println("Level Up! Welcome to Level " + level
                        + ". Spawn rate increased to " + spawnRate + "%.");
            }
        }
    }

    public void fireBullet() {
        addObject(new Bullet(ship.getX(), ship.getY()));
    }

    public void checkCollisions() {
        // Ship collisions
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            if (obj != ship && obj.getX() == ship.getX() && obj.getY() == ship.getY()) {
                if (obj instanceof game.core.PowerUp) {
                    ((game.core.PowerUp) obj).applyEffect(ship);
                    if (verbose) System.out.println("PowerUp collected: " + obj.render());
                } else if (obj instanceof Asteroid) {
                    ship.takeDamage(ASTEROID_DAMAGE);
                    if (verbose) System.out.println("Hit by " + obj.render()
                            + "! Health reduced by " + ASTEROID_DAMAGE + ".");
                } else if (obj instanceof Enemy) {
                    ship.takeDamage(ENEMY_DAMAGE);
                    if (verbose) System.out.println("Hit by " + obj.render()
                            + "! Health reduced by " + ENEMY_DAMAGE + ".");
                }
                toRemove.add(obj);
            }
        }
        spaceObjects.removeAll(toRemove);

        // Bullet collisions
        toRemove.clear();
        for (SpaceObject obj : spaceObjects) {
            if (obj instanceof Bullet) {
                for (SpaceObject target : spaceObjects) {
                    if (target instanceof Enemy
                            && target.getX() == obj.getX()
                            && target.getY() == obj.getY()) {
                        toRemove.add(target);
                        toRemove.add(obj);
                        ship.addScore(10);
                        // recordShotHit omitted here; integrate statsTracker as needed
                    } else if (target instanceof Asteroid
                            && target.getX() == obj.getX()
                            && target.getY() == obj.getY()) {
                        toRemove.add(obj);
                    }
                }
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    public static boolean isInBounds(SpaceObject spaceObject) {
        int x = spaceObject.getX(), y = spaceObject.getY();
        return x >= 0 && x < GAME_WIDTH && y >= 0 && y < GAME_HEIGHT;
    }

    public boolean checkGameOver() {
        return ship.getHealth() <= 0;
    }
}
