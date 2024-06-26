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
import lombok.Getter;
import java.io.InputStream;

import java.util.EnumSet;
import java.util.Objects;


/**
 * Represents a single space or tile on the RoboRally game board. Each space is
 * defined by its position on the board (x and y coordinates) and can hold a
 * single player (robot) at any given time.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Space extends Subject {
    @Getter
    public transient Board board;
    @Expose
    public int x;
    @Expose
    public int y;
    @Getter
    private Player player;
    @Getter
    @Expose
    private ActionField type;
    @Getter
    @Expose
    private final Heading heading;
    @Getter
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
        this.board = board;
        this.x = x;
        this.y = y;
        this.type = ActionField.NORMAL;
        this.heading = Heading.NORTH;
        this.index = 0;
        EnumSet.noneOf(Heading.class);
        player = null;
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/NORMAL.png")));
    }

    public Space(Board board, int x, int y, int index,EnumSet<Heading> walls) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.type = ActionField.NORMAL;
        this.heading = Heading.NORTH;
        this.index = index;
        this.walls = walls;
    }

    public Space(Board board, int x, int y, EnumSet<Heading> walls) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.type = ActionField.NORMAL;
        this.heading = Heading.NORTH;
        this.index = 0;
        this.walls = walls;
    }


    public Space(Board board, int x, int y, ActionField type, Heading heading, EnumSet<Heading> walls) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.type = type;
        this.heading = heading;
        this.index = 0;
        this.walls = walls;
    }


    /**
     * Constructs a new Space with the specified board, coordinates, type and heading.
     *
     * @param board   The board to which this space belongs.
     * @param x       The x coordinate of this space on the board.
     * @param y       The y coordinate of this space on the board.
     * @param type    The type of action field located at this space
     * @param heading The heading the field is facing in
     */
    public Space(Board board, int x, int y, ActionField type, Heading heading) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.type = type;
        this.heading = heading;
        this.index = 0;
        player = null;
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + this.type + ".png")));
    }

    /**
     * Constructs a new CHECKPOINT type space
     *
     * @param board The board to which this space belongs.
     * @param x     The x coordinate of this space on the board.
     * @param y     The y coordinate of this space on the board.
     * @param index The index corresponding to this checkpoint
     */
    public Space(Board board, int x, int y, int index) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.type = ActionField.CHECKPOINT;
        this.heading = Heading.NORTH;
        this.index = index;
        player = null;
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/NORMAL.png")));
    }

    public void setType(ActionField t) {
        this.type = t;
        notifyChange();
    }

    public Space(Board board, int x, int y, Heading heading, ActionField type, int index, EnumSet<Heading> walls) {
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

    private void initializeImage() {
        String imagePath = "/" + this.type + ".png";
        InputStream inputStream = getClass().getResourceAsStream(imagePath);
        if (inputStream != null) {
            this.image = new Image(inputStream);
        } else {
            System.err.println("Failed to load image from path: " + imagePath);
        }
    }

    private void logSpaceCreation() {
        if (!this.walls.isEmpty()) {
            System.out.println("Space created at (" + x + "," + y + ") with walls: " + this.walls);
        } else {
            System.out.println("Space create at (" + x + "," + y + ") with no walls");
        }
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
            Player p = this.getPlayer();
            switch (type) {
                case LEFT_CONVEYOR_BELT:
                    p.setHeading(heading.prev());
                    p.move(1);
                    p.setHeading(oldHeading);
                    break;
                case RIGHT_CONVEYOR_BELT:
                    p.setHeading(heading.next());
                    p.move(1);
                    p.setHeading(oldHeading);
                    break;
                case CONVEYOR_BELT, PUSH_PANEL:
                    p.setHeading(heading);
                    p.move(1);
                    p.setHeading(oldHeading);
                    break;
                case DOUBLE_CONVEYOR_BELT,
                     DOUBLE_RIGHTTREE_CONVEYOR_BELT,
                     DOUBLE_LEFTTREE_CONVEYOR_BELT:
                    p.setHeading(heading);
                    p.move(2);
                    p.setHeading(oldHeading);
                    break;
                case LEFT_GEAR:
                    p.setHeading(p.getHeading().prev());
                    break;
                case RIGHT_GEAR:
                    p.setHeading(p.getHeading().next());
                    break;
                case BOARD_LASER_START,
                     BOARD_LASER,
                     BOARD_LASER_END:
                    // TODO: Implement BOARD_LASER
                    return;
                case PIT:
                    // TODO: Implement PIT
                    return;
                case ENERGY_SPACE:
                    // TODO: Implement ENERGY_SPACE
                    return;
                case CHECKPOINT:
                    // Checkpoint logic handled in move
                    return;
                case WALL:
                    // This doesn't do anything here but any action having to do with movement will eventually need to check for its presence
                    return;
                case PRIORITY_ANTENNA:
                    board.determineTurn(x, y);
                    return;
                case RESPAWN:
                    // TODO: Implement RESPAWN
                default:
                    return;
            }
        }
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    public Phase getPhase() {
        return board.getPhase();
    }

    public boolean isOccupiable() {
        // Here, you can add any logic that determines if the space is occupiable.
        // For now, let's assume a space is occupiable if there is no player on it.
        return this.player == null;
    }

    public void addWall(Heading direction) {
        this.walls.add(direction);
    }

    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    public EnumSet<Heading> getWalls() {
        return walls;
    }
}
