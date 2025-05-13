package game;

import game.achievements.AchievementManager;
import game.achievements.PlayerStatsTracker;
import game.ui.UI;
import game.utility.Direction;
import game.exceptions.BoundaryExceededException;

import java.util.List;

/**
 * The Controller handling the game flow and interactions.
 *
 * Holds references to the UI and the Model, so it can pass information and references
 * back and forth as necessary.
 * Manages changes to the game, which are stored in the Model and displayed by the UI.
 */
public class GameController {
    private final UI ui;
    private final GameModel model;
    private final AchievementManager achievementManager;
    private final long startTime;
    private boolean isVerbose = false;

    /**
     * Initializes the game controller with the given UI and AchievementManager.
     * Stores the ui, creates a new GameModel, and records the start time.
     * The start time System.currentTimeMillis() should be stored as a long.
     * Starts the UI using UI.start().
     *
     * @param ui the UI used to draw the Game
     * @param achievementManager the manager used to maintain achievement information
     * @requires ui is not null, achievementManager is not null
     */
    public GameController(UI ui, AchievementManager achievementManager) {
        if (ui == null || achievementManager == null) {
            throw new IllegalArgumentException("ui and achievementManager must not be null");
        }
        this.ui = ui;
        this.model = new GameModel(ui);
        this.achievementManager = achievementManager;
        this.startTime = System.currentTimeMillis();
        ui.start();
    }

    /**
     * Initializes the game controller with the given UI, GameModel, and AchievementManager.
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
        if (ui == null || model == null || achievementManager == null) {
            throw new IllegalArgumentException("ui, model, and achievementManager must not be null");
        }
        this.ui = ui;
        this.model = model;
        this.achievementManager = achievementManager;
        this.startTime = System.currentTimeMillis();
        ui.start();
    }

    /**
     * Returns the current GameModel.
     *
     * @return the current GameModel.
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Returns the current PlayerStatsTracker.
     *
     * @return the current PlayerStatsTracker.
     */
    public PlayerStatsTracker getStatsTracker() {
        return model.getPlayerStatsTracker();
    }

    /**
     * Handles player input and performs actions such as moving the ship or firing Bullets.
     *
     * @param input the player's input command.
     */
    public void handlePlayerInput(String input) {
        if (input == null || input.length() != 1) {
            ui.log("Invalid input. Use W, A, S, D, F, or P.");
            return;
        }
        char c = Character.toUpperCase(input.charAt(0));
        switch (c) {
            case 'W', 'A', 'S', 'D' -> {
                Direction dir = switch (c) {
                    case 'W' -> Direction.UP;
                    case 'S' -> Direction.DOWN;
                    case 'A' -> Direction.LEFT;
                    default -> Direction.RIGHT;
                };
                try {
                    model.getShip().move(dir);
                    if (isVerbose) {
                        ui.log(String.format("Ship moved to (%d, %d)",
                            model.getShip().getX(), model.getShip().getY()));
                    }
                } catch (BoundaryExceededException e) {
                    // ignore or log
                }
            }
            case 'F' -> {
                model.fireBullet();
                getStatsTracker().recordShotFired();
            }
            case 'P' -> pauseGame();
            default -> ui.log("Invalid input. Use W, A, S, D, F, or P.");
        }
    }

    /**
     * Uses the provided tick to call and advance the following:
     * - A call to model.updateGame(tick) to advance the game by the given tick.
     * - A call to model.checkCollisions() to handle game interactions.
     * - A call to model.spawnObjects() to handle object creation.
     * - A call to model.levelUp() to check and handle leveling.
     * - A call to refreshAchievements(tick) to handle achievement updating.
     * - A call to renderGame() to draw the current state of the game.
     * - Checks if the game is over using model.checkGameOver().
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
        if (model.isGameOver()) {
            pauseGame();
        }
    }

    /**
     * Calls ui.pause() to pause the game until the method is called again.
     */
    public void pauseGame() {
        ui.pause();
        ui.log(isVerbose ? "Game paused." : "Game unpaused.");
        isVerbose = !isVerbose;
    }

    /**
     * Updates the player's progress towards achievements on every game tick,
     * and uses the achievementManager to track and update the player's achievements.
     *
     * @param tick the provided tick
     */
    public void refreshAchievements(int tick) {
        achievementManager.refreshProgress(getStatsTracker(), tick);
    }

    /**
     * Renders the current game state, including score, health, level, and survival time.
     */
    public void renderGame() {
        ui.setStat("Score", String.valueOf(model.getShip().getScore()));
        ui.setStat("Health", String.valueOf(model.getShip().getHealth()));
        ui.setStat("Level", String.valueOf(model.getLevel()));
        ui.setStat("Time Survived", (System.currentTimeMillis() - startTime) / 1000 + " seconds");
        ui.render(model.getSpaceObjects());
    }

    /**
     * Sets verbose state to the provided input.
     * Also sets the model's verbose state to the provided input.
     *
     * @param verbose whether to set verbose state to true or false.
     */
    public void setVerbose(boolean verbose) {
        this.isVerbose = verbose;
        model.setVerbose(verbose);
    }

    /**
     * Starts the main game loop.
     */
    public void startGame() {
        ui.onStep(this::onTick);
        ui.onKey(this::handlePlayerInput);
    }
}

