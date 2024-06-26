/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.EnumSet;

/**
 * Represents a single space or tile on the RoboRally game board. Each space is
 * defined by its position on the board (x and y coordinates) and can hold a
 * single player (robot) at any given time.
 * Author: Ekkart Kindler, ekki@dtu.dk
 */
public class Space extends Subject {
    public transient Board board;
    @Expose
    public int x;
    @Expose
    public int y;
    private Player player;
    @Expose
    private ActionField type;
    @Expose
    private final Heading heading;
    @Expose
    private final int index;
    @Expose
    private EnumSet<Heading> walls;

    public Image image;

    /**
     * Constructs a new Space with the specified board and coordinates.
     *
     * @param board The board to which this space belongs.
     * @param x     The x coordinate of this space on the board.
     * @param y     The y coordinate of this space on the board.
     */
    public Space(Board board, int x, int y) {
        this(board, x, y, ActionField.NORMAL, Heading.NORTH, 0, EnumSet.noneOf(Heading.class));
    }

    /**
     * Constructs a new Space with walls specified.
     *
     * @param board The board to which this space belongs.
     * @param x     The x coordinate of this space on the board.
     * @param y     The y coordinate of this space on the board.
     * @param walls The set of walls for this space.
     */
    public Space(Board board, int x, int y, EnumSet<Heading> walls) {
        this(board, x, y, ActionField.NORMAL, Heading.NORTH, 0, walls);
    }

    /**
     * Constructs a new Space with the specified board, coordinates, type, and walls.
     *
     * @param board The board to which this space belongs.
     * @param x     The x coordinate of this space on the board.
     * @param y     The y coordinate of this space on the board.
     * @param type  The type of action field located at this space.
     * @param walls The set of walls for this space.
     */
    public Space(Board board, int x, int y, ActionField type, EnumSet<Heading> walls) {
        this(board, x, y, type, Heading.NORTH, 0, walls);
    }

    /**
     * Constructs a new Space with the specified board, coordinates, type, heading, and walls.
     *
     * @param board   The board to which this space belongs.
     * @param x       The x coordinate of this space on the board.
     * @param y       The y coordinate of this space on the board.
     * @param type    The type of action field located at this space.
     * @param heading The heading the field is facing in.
     * @param walls   The set of walls for this space.
     */
    public Space(Board board, int x, int y, ActionField type, Heading heading, EnumSet<Heading> walls) {
        this(board, x, y, type, heading, 0, walls);
    }

    /**
     * Constructs a new Space with the specified board, coordinates, type, heading, index, and walls.
     *
     * @param board   The board to which this space belongs.
     * @param x       The x coordinate of this space on the board.
     * @param y       The y coordinate of this space on the board.
     * @param type    The type of action field located at this space.
     * @param heading The heading the field is facing in.
     * @param index   The index corresponding to this checkpoint.
     * @param walls   The set of walls for this space.
     */
    public Space(Board board, int x, int y, ActionField type, Heading heading, int index, EnumSet<Heading> walls) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.type = type;
        this.heading = heading;
        this.index = index;
        this.walls = walls != null ? EnumSet.copyOf(walls) : EnumSet.noneOf(Heading.class);
        this.player = null;

        initializeImage();
        logSpaceCreation();
    }

    /**
     * Initializes the image associated with this space based on its type.
     */
    private void initializeImage() {
        String imagePath = "/" + this.type + ".png";
        InputStream inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream != null) {
            this.image = new Image(inputStream);
        } else {
            System.err.println("Failed to load image from path: " + imagePath);
        }
    }

    /**
     * Logs the creation of this space, indicating walls if present.
     */
    private void logSpaceCreation() {
        if (!this.walls.isEmpty()) {
            System.out.println("Space created at (" + x + "," + y + ") with walls: " + this.walls);
        } else {
            System.out.println("Space created at (" + x + ", " + y + ") with no walls.");
        }
    }

    /**
     * Constructs a new CHECKPOINT type space with walls specified.
     *
     * @param board The board to which this space belongs.
     * @param x     The x coordinate of this space on the board.
     * @param y     The y coordinate of this space on the board.
     * @param index The index corresponding to this checkpoint.
     * @param walls The set of walls for this space.
     */
    public Space(Board board, int x, int y, int index, EnumSet<Heading> walls) {
        this(board, x, y, ActionField.CHECKPOINT, Heading.NORTH, index, walls);
    }

    public int getIndex() {
        return index;
    }

    public ActionField getType() {
        return type;
    }

    public void setType(ActionField t) {
        this.type = t;
        notifyChange();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Phase getPhase() {
        return board.getPhase();
    }

    /**
     * Gets the player (robot) currently occupying this space, if any.
     *
     * @return The player occupying this space, or null if the space is empty.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retrieves the current heading of this object.
     *
     * @return The current heading of the object, represented as an {@link Heading} enum.
     */
    public Heading getHeading() {
        return heading;
    }

    public Board getBoard() {
        return board;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    /**
     * Sets or clears the player occupying this space. This method also ensures
     * consistency by updating the player's space reference accordingly.
     *
     * @param player The new player to occupy this space, or null to clear the space.
     */
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer && (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
                notifyChange();
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    /**
     * Activates effects of the space. This method will need to be called from
     * the controller every turn.
     */
    public void activate() {
        if (player != null) {
            Heading oldHeading = player.getHeading();
            switch (type) {
                case LEFT_CONVEYOR_BELT:
                    player.setHeading(heading.prev());
                    player.move(1);
                    player.setHeading(oldHeading);
                    break;
                case RIGHT_CONVEYOR_BELT:
                    player.setHeading(heading.next());
                    player.move(1);
                    player.setHeading(oldHeading);
                    break;
                case CONVEYOR_BELT:
                case PUSH_PANEL:
                    player.setHeading(heading);
                    player.move(1);
                    player.setHeading(oldHeading);
                    break;
                case DOUBLE_CONVEYOR_BELT:
                case DOUBLE_RIGHTTREE_CONVEYOR_BELT:
                case DOUBLE_LEFTTREE_CONVEYOR_BELT:
                    player.setHeading(heading);
                    player.move(2);
                    player.setHeading(oldHeading);
                    break;
                case LEFT_GEAR:
                    player.setHeading(player.getHeading().prev());
                    break;
                case RIGHT_GEAR:
                    player.setHeading(player.getHeading().next());
                    break;
                case BOARD_LASER_START:
                case BOARD_LASER:
                case BOARD_LASER_END:
                    // TODO: Implement BOARD_LASER logic
                    break;
                case PIT:
                    // TODO: Implement PIT logic
                    break;
                case ENERGY_SPACE:
                    player.incrementEnergy(1);
                    break;
                case CHECKPOINT:
                    player.incrementIndex();
                    System.out.println("Checkpoint reached:" + player.getIndex());
                    break;
                case WALL:
                    // No action for WALL type in activation
                    break;
                case PRIORITY_ANTENNA:
                    board.determineTurn(x, y);
                    break;
                case RESPAWN:
                    // TODO: Implement RESPAWN logic
                    break;
                default:
                    break;
            }
        }
    }



    public void addWall(Heading direction) {
        walls.add(direction);
    }

    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    public EnumSet<Heading> getWalls() {
        return walls;
    }

    public boolean isOccupiable() {
        // Here, you can add any logic that determines if the space is occupiable.
        // For now, let's assume a space is occupiable if there is no player on it.
        return this.player == null;
    }

    @Override
    public String toString() {
        return "Space{" +
                "x=" + x +
                ", y=" + y +
                ", player=" + player +
                ", type=" + type +
                ", heading=" + heading +
                ", walls=" + walls +
                '}';
    }
}


