package game.achievements;

public class Achievement {
    private final String name;
    private final String description;
    private int currentTier;
    private double progress;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.currentTier = 0;
        this.progress = 0.0;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCurrentTier() {
        return currentTier;
    }

    public double getProgress() {
        return progress;
    }

    public void updateProgress(double delta) {
        this.progress = Math.min(1.0, this.progress + delta);
        if (progress >= 1.0) {
            currentTier++;
            progress = 0.0;
        }
    }
}
