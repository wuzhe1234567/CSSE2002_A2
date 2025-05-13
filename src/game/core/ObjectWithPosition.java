package game.core;

/**
 * Represents a movable and interactive object in the space game.
 */
public abstract class ObjectWithPosition implements SpaceObject {
    /**
     * The x coordinate of the Object.
     */
    protected int x;
    /**
     * The y coordinate of the Object.
     */
    protected int y;

    /**
     * Creates a movable and interactive object at the given coordinates.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public ObjectWithPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate of the SpaceObject, where 0 represents the left-most space with positive numbers extending to the right.
     *
     * @return x coordinate of the SpaceObject
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the SpaceObject, where 0 represents the top-most space with positive numbers extending downwards.
     *
     * @return y coordinate of the SpaceObject
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Returns a string representation of the Object.
     *
     * @return a string identifying the object's name and current position, eg. Bullet(2, 4)
     */
    @Override
    public String toString() {
        return String.format("%s(%d, %d)", getClass().getSimpleName(), x, y);
    }
}
