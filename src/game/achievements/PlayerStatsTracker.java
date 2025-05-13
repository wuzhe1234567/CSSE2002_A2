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

    /**
     * Records a shot fired by the player.
     */
    public void recordShotFired() {
        shotsFired++;
    }

    /**
     * Records a successful hit on an enemy.
     */
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

    /**
     * Returns elapsed seconds since tracker creation.
     */
    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}
