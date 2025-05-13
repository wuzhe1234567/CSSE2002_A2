package game;

import game.achievements.Achievement;
import game.achievements.AchievementManager;
import game.achievements.PlayerStatsTracker;
import game.core.Healable;
import game.core.HealthPowerUp;
import java.util.List;

public class GameController {
    private final UI ui;
    private final GameModel model;
    private final AchievementManager aManager;
    private boolean isPaused = false;

    public GameController(UI ui, GameModel model, AchievementManager aManager) {
        this.ui = ui;
        this.model = model;
        this.aManager = aManager;
        initialize();
    }

    public GameController(UI ui, AchievementManager aManager) {
        this(ui, new GameModel(ui::log, new PlayerStatsTracker()), aManager);
    }

    private void initialize() {
        ui.onKey(this::handlePlayerInput);
        ui.start();
    }

    public void startGameLoop() {
        int tick = 0;
        while (!isPaused) {
            tick++;
            model.updateState(tick);
            refreshAchievements(tick);
            renderGame();
            if (model.checkGameOver()) {
                pauseGame();
            }
        }
    }

    private void handlePlayerInput(String key) {
        model.processInput(key);
    }

    private void refreshAchievements(int tick) {
        aManager.updateAll(model, tick);
    }

    private void renderGame() {
        ui.render(model.getCurrentFrame());
    }

    private void pauseGame() {
        isPaused = true;
        ui.showGameOver(model.getScore());
    }

    public PlayerStatsTracker getStatsTracker() {
        return model.getStats();
    }

    public void showStats() {
        PlayerStatsTracker stats = getStatsTracker();
        StringBuilder sb = new StringBuilder();
        sb.append("Shots Fired: ").append(stats.getShotsFired()).append("\n");
        sb.append("Shots Hit: ").append(stats.getShotsHit()).append("\n");
        sb.append("Enemies Destroyed: ").append(stats.getEnemiesDestroyed()).append("\n");
        sb.append("Survival Time: ").append(stats.getElapsedSeconds()).append(" seconds\n");
        ui.showText(sb.toString());
        List<Achievement> achievements = aManager.getAchievements();
        for (Achievement ach : achievements) {
            double progressPercent = ach.getProgress() * 100;
            ui.showText(String.format("%s: %s (Tier %d) - %.1f%% complete",
                    ach.getName(), ach.getDescription(), ach.getCurrentTier(), progressPercent));
        }
    }
}
