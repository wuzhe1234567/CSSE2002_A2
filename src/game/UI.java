package game;

import java.util.function.Consumer;

public interface UI {
    void log(String message);
    void onKey(Consumer<String> handler);
    void start();
    void render(Object frame);
    void showGameOver(int score);
    void showText(String text);
}
