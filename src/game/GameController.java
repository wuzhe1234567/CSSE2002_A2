// src/game/core/Controllable.java
package game.core;

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

import java.util.Objects;
import game.achievements.AchievementManager;
import game.achievements.PlayerStatsTracker;

/**
 * 游戏控制器，处理游戏流程和交互。
 */
public class GameController {
    private final UI ui;
    private final GameModel model;
    private final AchievementManager aManager;
    private boolean isPaused = false;

    /**
     * 构造器：初始化 UI 与成就管理器，创建默认 GameModel，并启动主循环。
     */
    public GameController(UI ui, AchievementManager aManager) {
        this.ui = Objects.requireNonNull(ui, "ui must not be null");
        this.model = new GameModel(ui::log, new PlayerStatsTracker());
        this.aManager = Objects.requireNonNull(aManager, "aManager must not be null");
        ui.onKey(this::handlePlayerInput);
        ui.start();
        startGameLoop();
    }

    /**
     * 游戏主循环：按次调用 updateGame、checkCollisions、spawnObjects、levelUp、achievement 更新与渲染。
     */
    private void startGameLoop() {
        int tick = 0;
        while (!isPaused) {
            tick++;
            model.updateGame(tick);
            model.checkCollisions();
            model.spawnObjects();
            model.levelUp();
            aManager.updateAll(model, tick);
            ui.render(model);
            if (model.checkGameOver()) {
                isPaused = true;
                ui.showGameOver(model.getShip().getScore());
            }
        }
    }

    /**
     * 处理玩家输入："LEFT"/"RIGHT" 移动，"SPACE" 射击，"P" 暂停/继续。
     */
    private void handlePlayerInput(String key) {
        switch (key) {
            case "LEFT":  model.getShip().move(-1); break;
            case "RIGHT": model.getShip().move(1);  break;
            case "SPACE": model.fireBullet();       break;
            case "P":     isPaused = !isPaused;     break;
            default:        ui.log("Invalid input. Use LEFT, RIGHT, SPACE, or P.");
        }
    }

    /**
     * 返回当前模型。
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * 返回当前玩家统计。
     */
    public PlayerStatsTracker getStatsTracker() {
        return model.getStatsTracker();
    }
}

