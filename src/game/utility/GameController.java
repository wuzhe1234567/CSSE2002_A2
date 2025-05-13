package game;

import game.achievements.Achievement;
import game.achievements.AchievementManager;
import game.achievements.PlayerStatsTracker;
import game.ui.UI;

import java.util.List;

/**
 * The Controller handling the game flow and interactions.
 *
 * Holds references to the UI and the Model, so it can pass information and references back and forth as necessary.
 * Manages changes to the game, which are stored in the Model, and displayed by the UI.
 */
public class GameController {
    private final long starTime;
    private final UI ui;
    private final GameModel model;
    private final AchievementManager aManager;

    /**
     * An internal variable indicating whether certain methods should log their actions.
     * Not all methods respect isVerbose.
     */
    private boolean isVerbose = false;


    /**
     * Initializes the game controller with the given UI, GameModel and AchievementManager.
     * Stores the UI, GameModel, AchievementManager and start time.
     * The start time System.currentTimeMillis() should be stored as a long.
     * Starts the UI using UI.start().
     *
     * @param ui the UI used to draw the Game
     * @param model the model used to maintain game information
     * @param aManager the manager used to maintain achievement information
     *
     * @requires ui is not null
     * @requires model is not null
     * @requires achievementManager is not null
     * @provided
     */
    public GameController(UI ui, GameModel model, AchievementManager aManager) {
        this.ui = ui;
        ui.start();
        this.model = model;
        this.starTime = System.currentTimeMillis(); // Current time
        this.aManager = aManager;
    }

    /**
     * Initializes the game controller with the given UI and GameModel.
     * Stores the ui, model and start time.
     * The start time System.currentTimeMillis() should be stored as a long.
     *
     * @param ui    the UI used to draw the Game
     * @param aManager the manager used to maintain achievement information
     *
     * @requires ui is not null
     * @requires achievementManager is not null
     * @provided
     */
    public GameController(UI ui, AchievementManager aManager) {
        this(ui, new GameModel(ui::log, new PlayerStatsTracker()), aManager);
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
        return model.getStatsTracker();
    }

    /**
     * Sets verbose state to the provided input.
     * Sets the models verbose state to the provided input.
     * @param verbose whether to set verbose state to true or false.
     */
    public void setVerbose(boolean verbose) {
      // Implementation goes here
    }

    /**
     * Starts the main game loop.
     *
     * Passes onTick and handlePlayerInput to ui.onStep and ui.onKey respectively.
     * @provided
     */
    public void startGame() {
        ui.onStep(this::onTick);
        ui.onKey(this::handlePlayerInput);
    }

    /**
     * Uses the provided tick to call and advance the following:
     * - A call to model.updateGame(tick) to advance the game by the given tick.
     * - A call to model.checkCollisions() to handle game interactions.
     * - A call to model.spawnObjects() to handle object creation.
     * - A call to model.levelUp() to check and handle leveling.
     * - A call to refreshAchievements(tick) to handle achievement updating.
     * - A call to renderGame() to draw the current state of the game.
     *  - Checks if the game is over using model.checkGameOver().
     *
     * @param tick the provided tick
     * @provided
     */
    public void onTick(int tick) {
        model.updateGame(tick); // Update GameObjects
        model.checkCollisions(); // Check for Collisions
        model.spawnObjects(); // Handles new spawns
        model.levelUp(); // Level up when score threshold is met
        refreshAchievements(tick); // Handle achievement updating.
        renderGame(); // Update Visual

        // Check game over
        if (model.checkGameOver()) {
            pauseGame();
            showGameOverWindow();
        }
    }

    /**
     * Updates the player's progress towards achievements on every game tick,
     * and uses the achievementManager to track and update the player's achievements.
     *
     * @param tick the provided tick
     */
    public void refreshAchievements(int tick) {
        // Implementation goes here
    }

    /**
     * Renders the current game state, including score, health, level, and survival time.
     */
    public void renderGame() {
        // Implementation goes here
    }

    /**
     * Handles player input and performs actions such as moving the ship or firing Bullets.
     *
     * @param input the player's input command.
     */
    public void handlePlayerInput(String input) {
        // Implementation goes here
    }

    /**
     * Calls ui.pause() to pause the game until the method is called again.
     */
    public void pauseGame() {
        // Implementation goes here
    }

    /**
     * Displays a Game Over window containing the player's final statistics and achievement
     * progress.
     *
     * This window includes:
     * - Number of shots fired and shots hit
     * - Number of Enemies destroyed
     * - Survival time in seconds
     * - Progress for each achievement, including name, description, completion percentage
     * and current tier
     * @provided
     */
    private void showGameOverWindow() {

        // Create a new window to display game over stats.
        javax.swing.JFrame gameOverFrame = new javax.swing.JFrame("Game Over - Player Stats");
        gameOverFrame.setSize(400, 300);
        gameOverFrame.setLocationRelativeTo(null); // center on screen
        gameOverFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);


        StringBuilder sb = new StringBuilder();
        sb.append("Shots Fired: ").append(getStatsTracker().getShotsFired()).append("\n");
        sb.append("Shots Hit: ").append(getStatsTracker().getShotsHit()).append("\n");
        sb.append("Enemies Destroyed: ").append(getStatsTracker().getShotsHit()).append("\n");
        sb.append("Survival Time: ").append(getStatsTracker().getElapsedSeconds()).append(" seconds\n");


        List<Achievement> achievements= aManager.getAchievements();
        for (Achievement ach : achievements) {
            double progressPercent = ach.getProgress() * 100;
            sb.append(ach.getName())
                    .append(" - ")
                    .append(ach.getDescription())
                    .append(" (")
                    .append(String.format("%.0f%%", progressPercent))
                    .append(" complete, Tier: ")
                    .append(ach.getCurrentTier())
                    .append(")\n");
        }

        String statsText = sb.toString();

        // Create a text area to show stats.
        javax.swing.JTextArea statsArea = new javax.swing.JTextArea(statsText);
        statsArea.setEditable(false);
        statsArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));

        // Add the text area to a scroll pane (optional) and add it to the frame.
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(statsArea);
        gameOverFrame.add(scrollPane);

        // Make the window visible.
        gameOverFrame.setVisible(true);
    }

}
