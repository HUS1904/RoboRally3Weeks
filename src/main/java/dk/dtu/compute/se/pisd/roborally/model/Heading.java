package dk.dtu.compute.se.pisd.roborally.model;

/**
 * Enumerates the possible headings (orientations) a robot can have in the RoboRally game.
 * This orientation determines the direction in which a robot will move or interact with
 * board elements. The headings follow cardinal directions, and methods are provided to
 * navigate between these directions, simulating rotation.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public enum Heading {
    SOUTH, WEST, NORTH, EAST;

    /**
     * Returns the next clockwise heading from this one.
     * For example, if the current heading is NORTH, the next heading will be EAST.
     * @return The next clockwise heading.
     */
    public Heading next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * Returns the previous counter-clockwise heading from this one.
     * For example, if the current heading is EAST, the previous heading will be NORTH.
     * @return The previous counter-clockwise heading.
     */
    public Heading prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }

    /**
     * Returns the opposite heading from this one.
     * For example, if the current heading is NORTH, the opposite heading will be SOUTH.
     * @return The opposite heading.
     */
    public Heading opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
