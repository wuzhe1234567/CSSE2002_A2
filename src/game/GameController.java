package game;

import game.GameModel;

/**
 * 可被控制的空间物体基类（例如飞船）。
 */
public abstract class Controllable implements SpaceObject {
    protected int x, y;

    public Controllable(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 横向移动 dx 个格子：dx=+1 向右，dx=-1 向左。
     * 超出边界时夹紧到 [0, GAME_WIDTH-1]。
     */
    public void move(int dx) {
        int nx = x + dx;
        nx = Math.max(0, Math.min(GameModel.GAME_WIDTH - 1, nx));
        this.x = nx;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public abstract void tick(int tick);
}


// src/game/GameController.java
package game;

import game.achievements.AchievementManager;
import game.achievements.PlayerStatsTracker;
import game.ui.UI;
import game.GameModel;
import game.core.SpaceObject;

import java.util.List;
import java.util.Objects;

/**
 * The Controller handling the game flow and interactions.
 * Holds references to the UI and the Model, so it can pass information
 * and references back and forth as necessary.
 * Manages changes to the game, which are stored in the Model, and displayed by the UI.
 */
public class GameController {
    private final UI ui;
    private final GameModel model;
    private final AchievementManager achievementManager;
    private final long startTime;
    private boolean isVerbose = false;

    /**
     * Initializes the game controller with the given UI, GameModel and AchievementManager.
     * Stores the UI, GameModel, AchievementManager and start time.
     * The start time System.currentTimeMillis() should be stored as a long.
     * Starts the UI using UI.start().
     *
     * @param ui the UI used to draw the Game
     * @param model the model used to maintain game information
     * @param achievementManager the manager used to maintain achievement information
     * @requires ui is not null, model is not null, achievementManager is not null
     */
    public GameController(UI ui, GameModel model, AchievementManager achievementManager) {
        this.ui = Objects.requireNonNull(ui, "ui must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.achievementManager = Objects.requireNonNull(achievementManager, "achievementManager must not be null");
        this.startTime = System.currentTimeMillis();
        ui.start();
    }

    /**
     * Initializes the game controller with the given UI and AchievementManager.
     * Creates a new GameModel internally. Stores the UI, Model and start time.
     * The start time System.currentTimeMillis() should be stored as a long.
     * Starts the UI using UI.start().
     *
     * @param ui the UI used to draw the Game
     * @param achievementManager the manager used to maintain achievement information
     * @requires ui is not null, achievementManager is not null
     */
    public GameController(UI ui, AchievementManager achievementManager) {
        this(ui, new GameModel(ui::log, new PlayerStatsTracker()), achievementManager);
    }

    /**
     * Returns the current GameModel.
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Returns the current PlayerStatsTracker.
     */
    public PlayerStatsTracker getStatsTracker() {
        return model.getStatsTracker();
    }

    /**
     * Sets verbose state to the provided input.
     * Also sets the model's verbose state.
     */
    public void setVerbose(boolean verbose) {
        this.isVerbose = verbose;
        model.setVerbose(verbose);
    }

    /**
     * Starts the main game loop by registering tick and input handlers.
     */
    public void startGame() {
        ui.onStep(this::onTick);
        ui.onKey(this::handlePlayerInput);
    }

    /**
     * Uses the provided tick to advance game state and render.
     *
     * @param tick the provided tick
     */
    public void onTick(int tick) {
        model.updateGame(tick);
        model.checkCollisions();
        model.spawnObjects();
        model.levelUp();
        refreshAchievements(tick);
        renderGame();
        if (model.checkGameOver()) {
            pauseGame();
            ui.showGameOver(model.getShip().getScore());
        }
    }

    /**
     * Pauses or unpauses the game, calling ui.pause() and logging state.
     */
    public void pauseGame() {
        ui.pause();
        boolean paused = model.checkGameOver() || false; // placeholder, or track separately
        ui.log(paused ? "Game paused." : "Game unpaused.");
    }

    /**
     * Updates the player's progress towards achievements.
     *
     * @param tick the provided tick
     */
    public void refreshAchievements(int tick) {
        achievementManager.updateAll(model, tick);
        if (isVerbose && tick % 100 == 0) {
            ui.log("Achievements refreshed at tick " + tick);
        }
    }

    /**
     * Renders the current game state including stats and objects.
     */
    public void renderGame() {
        ui.setStat("Score", String.valueOf(model.getShip().getScore()));
        ui.setStat("Health", String.valueOf(model.getShip().getHealth()));
        ui.setStat("Level", String.valueOf(model.getLevel()));
        long elapsedSec = (System.currentTimeMillis() - startTime) / 1000;
        ui.setStat("Time Survived", elapsedSec + " seconds");
        List<SpaceObject> objects = model.getSpaceObjects();
        ui.render(objects);
    }

    /**
     * Handles player input commands.
     *
     * @param input the player's input command
     */
    public void handlePlayerInput(String input) {
        if (input == null || input.length() != 1) {
            ui.log("Invalid input. Use W, A, S, D, F, or P.");
            return;
        }
        char c = Character.toUpperCase(input.charAt(0));
        if (c == 'P') {
            pauseGame();
            return;
        }
        switch (c) {
            case 'W': // up not implemented
                break;
            case 'A': model.getShip().move(-1); break;
            case 'S': // down not implemented
                break;
            case 'D': model.getShip().move(1); break;
            case 'F': model.fireBullet(); break;
            default: ui.log("Invalid input. Use W, A, S, D, F, or P."); return;
        }
        if (isVerbose) {
            ui.log("Ship moved to (" + model.getShip().getX() + "," + model.getShip().getY() + ")");
        }
    }
}
