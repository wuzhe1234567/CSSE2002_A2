package game.achievements;

/**
 * Represents a single achievement with progress tracking and tier information.
 * The progress value of an achievement is always maintained between 0.0 (0%)
 * and 1.0 (100%). When updating progress, the increment must be non-negative
 * and the cumulative progress is capped at 1.0.
 */
public interface Achievement {
    /**
     * Returns the unique name of the achievement.
     */
    String getName();

    /**
     * Returns a description of the achievement.
     */
    String getDescription();

    /**
     * Returns the current progress as a double between 0.0 (0%) and 1.0 (100%).
     * Ensures: 0.0 <= getProgress() <= 1.0
     */
    double getProgress();

    /**
     * Sets the progress to the specified value.
     *
     * @param newProgress the updated progress.
     * @throws IllegalArgumentException if newProgress is not between 0.0 and 1.0 inclusive.
     * Ensures: getProgress() == newProgress (capped at 1.0 and floored at 0.0).
     */
    void setProgress(double newProgress);

    /**
     * Returns "Novice" if getProgress() < 0.5,
     * "Expert" if 0.5 <= getProgress() < 0.999,
     * and "Master" if getProgress() >= 0.999.
     */
    String getCurrentTier();
}

