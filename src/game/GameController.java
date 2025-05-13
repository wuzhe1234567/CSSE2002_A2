package game;

import game.achievements.Achievement;
import game.achievements.AchievementManager;
import game.achievements.PlayerStatsTracker;
import game.GameModel;
import game.core.SpaceObject;
import java.util.List;
import java.util.Objects;

/**
 * The Controller handling the game flow and interactions.
 * Holds references to the UI and the Model, so it can pass information
 * back and forth as necessary. Manages changes to the game, stored in the Model,
 * and displayed by the UI.
 */
public class GameController {
    private final UI ui;
    private final GameModel model;
    private final AchievementManager achievementManager;
    private final long startTime;
    private boolean verbose = false;
    private boolean isPaused = false;

    /**
     * Initializes the game controller with the given UI and GameModel.
     * Stores UI, Model, AchievementManager and start time. Starts the UI.
     * @param ui the UI used to draw the Game
     * @param model the model used to maintain game information
     * @param achievementManager the manager used to maintain achievement information
     */
    public GameController(UI ui, GameModel model, AchievementManager achievementManager) {
        Objects.requireNonNull(ui, "ui must not be null");
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(achievementManager, "achievementManager must not be null");
        this.ui = ui;
        this.model = model;
        this.achievementManager = achievementManager;
        this.startTime = System.currentTimeMillis();
        ui.start();
    }

    /**
     * Initializes the game controller with the given UI and AchievementManager.
     * Creates a new GameModel internally. Stores UI, Model, and start time.
     * @param ui the UI used to draw the Game
     * @param achievementManager the manager used to maintain achievement information
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
     * Sets verbose state on both controller and model.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
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
     * Handles a single game tick, advancing game state and rendering.
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
     * Pauses or unpauses the game, logging state and invoking UI.pause().
     */
    public void pauseGame() {
        ui.pause();
        isPaused = !isPaused;
        ui.log(isPaused ? "Game paused." : "Game unpaused.");
    }

    /**
     * Updates achievements on every game tick.
     */
    public void refreshAchievements(int tick) {
        achievementManager.updateAll(model, tick);
        if (verbose && tick % 100 == 0) {
            ui.log("Achievements refreshed at tick " + tick);
        }
    }

    /**
     * Renders the current game state.
     */
    public void renderGame() {
        // update stats
        ui.setStat("Score", String.valueOf(model.getShip().getScore()));
        ui.setStat("Health", String.valueOf(model.getShip().getHealth()));
        ui.setStat("Level", String.valueOf(model.getLevel()));
        long elapsedSec = (System.currentTimeMillis() - startTime) / 1000;
        ui.setStat("Time Survived", elapsedSec + " seconds");
        // render objects
        List<SpaceObject> objs = model.getSpaceObjects();
        ui.render(objs);
    }

    /**
     * Handles player input commands.
     */
    public void handlePlayerInput(String input) {
        if (input == null || input.length() != 1) {
            ui.log("Invalid input. Use W, A, S, D, F, or P.");
            return;
        }
        char c = Character.toUpperCase(input.charAt(0));
        if (isPaused) {
            if (c == 'P') {
                pauseGame();
            }
            return;
        }
        switch (c) {
            case 'W': model.getShip().tick(-1); // if vertical supported
                     if (verbose) ui.log("Ship moved to (" + model.getShip().getX() + "," + model.getShip().getY() + ")");
                     break;
            case 'A': model.getShip().move(-1);
                     if (verbose) ui.log("Ship moved to (" + model.getShip().getX() + "," + model.getShip().getY() + ")");
                     break;
            case 'S': model.getShip().tick(1); // if vertical supported
                     if (verbose) ui.log("Ship moved to (" + model.getShip().getX() + "," + model.getShip().getY() + ")");
                     break;
            case 'D': model.getShip().move(1);
                     if (verbose) ui.log("Ship moved to (" + model.getShip().getX() + "," + model.getShip().getY() + ")");
                     break;
            case 'F': model.fireBullet(); break;
            case 'P': pauseGame();             break;
            default:  ui.log("Invalid input. Use W, A, S, D, F, or P.");
        }
    }
}
