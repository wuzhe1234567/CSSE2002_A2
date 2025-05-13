package game.achievements;

import game.GameModel;
import java.util.ArrayList;
import java.util.List;

public class AchievementManager {
    private final List<Achievement> achievements = new ArrayList<>();
    private final PlayerStatsTracker stats;

    public AchievementManager(PlayerStatsTracker stats) {
        this.stats = stats;
        // TODO: 初始化成就列表
    }

    public void updateAll(GameModel model, int tick) {
        for (Achievement ach : achievements) {
            // TODO: 根据 model 或 stats 更新成就进度
            ach.updateProgress(0.01);
        }
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }

    public PlayerStatsTracker getStatsTracker() {
        return stats;
    }
}
