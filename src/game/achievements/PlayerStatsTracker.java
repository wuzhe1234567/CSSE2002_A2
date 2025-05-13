package game.achievements;

/**
 * Represents a tracker for player statistics.
 * Monitors shots fired and hits, and computes accuracy & elapsed time.
 */
public class PlayerStatsTracker {
    private final long startTime;
    private int shotsFired;
    private int shotsHit;

    /**
     * Constructs with current system time (ms) as the start.
     */
    public PlayerStatsTracker() {
        this(System.currentTimeMillis());
    }

    /**
     * Constructs with a custom start time.
     *
     * @param startTime the time when the tracking began
     */
    public PlayerStatsTracker(long startTime) {
        this.startTime = startTime;
        this.shotsFired = 0;
        this.shotsHit = 0;
    }

    /**
     * Records the player firing one shot.
     */
    public void recordShotFired() {
        shotsFired++;
    }

    /**
     * Records the player hitting one target.
     */
    public void recordShotHit() {
        shotsHit++;
    }

    /**
     * Returns the total number of shots the player has fired.
     */
    public int getShotsFired() {
        return shotsFired;
    }

    /**
     * Returns the total number of shots the player has successfully hit.
     */
    public int getShotsHit() {
        return shotsHit;
    }

    /**
     * Returns the shooting accuracy percentage as a decimal.
     * Accuracy = shotsHit / shotsFired, or 0.0 if no shots fired.
     */
    public double getAccuracy() {
        return shotsFired == 0 ? 0.0 : (double) shotsHit / shotsFired;
    }

    /**
     * Returns the number of seconds elapsed since the tracker started.
     */
    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}
