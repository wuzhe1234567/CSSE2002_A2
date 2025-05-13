package game.achievements;

public class PlayerStatsTracker {
    private int shotsFired;
    private int shotsHit;
    private int enemiesDestroyed;
    private final long startTime;

    public PlayerStatsTracker() {
        this.shotsFired = 0;
        this.shotsHit = 0;
        this.enemiesDestroyed = 0;
        this.startTime = System.currentTimeMillis();
    }

    /** 记录一次开火 */
    public void recordShotFired() {
        shotsFired++;
    }

    /** 记录一次命中并计为消灭一个敌人 */
    public void recordShotHit() {
        shotsHit++;
        enemiesDestroyed++;
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

    /** 返回从游戏开始到现在经过的秒数 */
    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}
