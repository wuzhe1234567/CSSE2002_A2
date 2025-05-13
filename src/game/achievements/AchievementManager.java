package game.achievements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GameAchievementManager coordinates achievement updates, file persistence management.
 */
public class AchievementManager {
    private final AchievementFile file;
    private final List<Achievement> achievements = new ArrayList<>();
    private final Set<String> masteredLogged = new HashSet<>();

    /**
     * Constructs a GameAchievementManager with the specified AchievementFile.
     *
     * @param achievementFile the AchievementFile instance to use (non-null)
     * @throws IllegalArgumentException if achievementFile is null.
     */
    public AchievementManager(AchievementFile achievementFile) {
        if (achievementFile == null) {
            throw new IllegalArgumentException("AchievementFile cannot be null");
        }
        this.file = achievementFile;
    }

    /**
     * Registers a new achievement.
     *
     * @param achievement the Achievement to register.
     * @throws IllegalArgumentException if achievement is null or already registered.
     */
    public void addAchievement(Achievement achievement) {
        if (achievement == null) {
            throw new IllegalArgumentException("Achievement cannot be null");
        }
        if (achievements.stream().anyMatch(a -> a.getName().equals(achievement.getName()))) {
            throw new IllegalArgumentException("Achievement already registered: " + achievement.getName());
        }
        achievements.add(achievement);
    }

    /**
     * Sets the progress of the specified achievement to a given amount.
     *
     * @param achievementName       the name of the achievement.
     * @param absoluteProgressValue the value the achievement's progress will be set to.
     * @throws IllegalArgumentException if no achievement is registered under the provided name.
     */
    public void updateAchievement(String achievementName, double absoluteProgressValue) {
        Achievement a = achievements.stream()
                .filter(x -> x.getName().equals(achievementName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such achievement: " + achievementName));
        a.setProgress(absoluteProgressValue);
    }

    /**
     * Checks all registered achievements. For any achievement that is mastered and has not yet been logged,
     * this method logs the event via AchievementFile, and marks the achievement as logged.
     */
    public void logAchievementMastered() {
        for (Achievement a : achievements) {
            if (a.getProgress() >= 1.0 && !masteredLogged.contains(a.getName())) {
                file.save(a.getName());
                masteredLogged.add(a.getName());
            }
        }
    }

    /**
     * Returns a list of all registered achievements.
     */
    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }
}
