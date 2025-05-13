package game.core;

import game.exceptions.BoundaryExceededException;
import game.utility.Direction;
import static game.GameModel.*;

/**
 * Represents a controllable object in the space game.
 */
public abstract class Controllable extends ObjectWithPosition {

    /**
     * Creates a controllable object at the given coordinates.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public Controllable(int x, int y) {
        super(x, y);
    }

    /**
     * Moves the Controllable by one in the direction given.
     *
     * Throws BoundaryExceededException if the Controllable is attempting to move out-of-bounds.
     * A controllable is considered out-of-bounds if they are at:
     * x-coordinate >= GAME_WIDTH,
     * y-coordinate >= GAME_HEIGHT,
     * x-coordinate < 0, or
     * y-coordinate < 0.
     * Argument given to the exception is "Cannot move {up/down/left/right}. Out of bounds!" depending on the direction.
     * Hint: game dimensions are stored in the model.
     *
     * @param direction the given direction
     * @throws BoundaryExceededException if attempting to move out-of-bounds
     */
    public void move(Direction direction) throws BoundaryExceededException {
        int newX = x;
        int newY = y;
        switch (direction) {
            case UP ->    newY--;
            case DOWN ->  newY++;
            case LEFT ->  newX--;
            case RIGHT -> newX++;
        }
        if (newX < 0 || newY < 0 || newX >= GAME_WIDTH || newY >= GAME_HEIGHT) {
            throw new BoundaryExceededException(
                    String.format("Cannot move %s. Out of bounds!", direction.name().toLowerCase())
            );
        }
        x = newX;
        y = newY;
    }
}
