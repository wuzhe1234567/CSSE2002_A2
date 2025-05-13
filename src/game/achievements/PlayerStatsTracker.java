package game.achievements;

public class PlayerStatsTracker {
    private int shotsFired = 0;
    private int shotsHit = 0;
    private int enemiesDestroyed = 0;
    private long startTime = System.currentTimeMillis();

    public void recordAction(String action) {
        // TODO: 根据 action 类型更新统计
        if ("fire".equals(action)) shotsFired++;
        if ("hit".equals(action)) shotsHit++;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public int getShotsHit() {
        return shotsHit;
    }

    public int getEnemiesDestroyed() {
        return enemiesDestroyed;
    }

    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}
