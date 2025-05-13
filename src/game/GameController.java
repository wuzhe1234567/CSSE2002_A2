package game;

import game.achievements.PlayerStatsTracker;
import game.achievements.AchievementManager;
import game.achievements.Achievement;
import java.util.List;

public class GameController {
    private final UI ui;
    private final GameModel model;
    private final AchievementManager aManager;
    private boolean isPaused = false;

    public GameController(UI ui, AchievementManager aManager) {
        this.ui = ui;
        this.model = new GameModel(ui::log, new PlayerStatsTracker());
        this.aManager = aManager;
        ui.onKey(this::handlePlayerInput);
        ui.start();
        startGameLoop();
    }

    private void startGameLoop() {
        int tick = 0;
        while (!isPaused) {
            tick++;
            // 按 Javadoc 顺序调用
            model.updateGame(tick);
            model.checkCollisions();
            model.spawnObjects();
            model.levelUp();
            aManager.updateAll(model, tick);

            // 渲染整个模型
            ui.render(model);

            if (model.checkGameOver()) {
                isPaused = true;
                ui.showGameOver(model.getShip().getScore());
            }
        }
    }

    private void handlePlayerInput(String key) {
        switch (key) {
            case "LEFT":  model.getShip().move(-1); break;
            case "RIGHT": model.getShip().move(1);  break;
            case "SPACE": model.fireBullet();       break;
            case "P":     isPaused = !isPaused;      break;
            default:      ui.log("Invalid input. Use A, D, SPACE or P."); break;
        }
    }

    public GameModel getModel() {
        return model;
    }

    public PlayerStatsTracker getStatsTracker() {
        return model.getStats(); // 或者 model.getStatsTracker()
    }
}
