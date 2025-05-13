package game;

import game.utility.Direction;
import game.core.SpaceObject;
import game.core.Ship;
import game.ui.UI;  // 根据你项目中 UI 的实际包名调整

/**
 * The Controller handling the game flow and interactions.
 */
public class GameController {
    private final UI ui;
    private final GameModel model;
    private long startTime;
    private boolean paused = false;

    public GameController(UI ui) {
        if (ui == null) throw new IllegalArgumentException("ui cannot be null");
        this.ui = ui;
        this.model = new GameModel();
        this.startTime = System.currentTimeMillis();
        ui.start();
    }

    public GameModel getModel() {
        return model;
    }

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
        if (paused) return;

        switch (c) {
            case 'W': move(Direction.UP);    break;
            case 'A': move(Direction.LEFT);  break;
            case 'S': move(Direction.DOWN);  break;
            case 'D': move(Direction.RIGHT); break;
            case 'F':
                model.fireBullet();
                break;
            default:
                ui.log("Invalid input. Use W, A, S, D, F, or P.");
        }
    }

    private void move(Direction dir) {
        try {
            model.getShip().move(dir);
            ui.log("Ship moved to ("
                    + model.getShip().getX() + ", "
                    + model.getShip().getY() + ")");
        } catch (Exception e) {
            ui.log(e.getMessage());
        }
    }

    public void onTick(int tick) {
        model.updateGame(tick);
        model.spawnObjects();
        model.checkCollisions();
        model.levelUp();
        renderGame();
        if (model.checkGameOver()) {
            pauseGame();
            // 如果你的 UI 有“游戏结束”窗口方法，请在这里调用：
            // ui.showGameOverWindow();
        }
    }

    public void pauseGame() {
        paused = !paused;
        ui.pause();
        ui.log(paused ? "Game paused." : "Game unpaused.");
    }

    public void renderGame() {
        ui.setStat("Score", String.valueOf(model.getShip().getScore()));
        ui.setStat("Health", String.valueOf(model.getShip().getHealth()));
        ui.setStat("Level", String.valueOf(model.getLevel()));
        ui.setStat("Time Survived",
                ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
        // 一次性渲染所有空间对象
        ui.render(model.getSpaceObjects());
    }

    public void setVerbose(boolean verbose) {
        model.setVerbose(verbose);
    }

    public void startGame() {
        ui.onStep(this::onTick);
        ui.onKey(this::handlePlayerInput);
    }
}
