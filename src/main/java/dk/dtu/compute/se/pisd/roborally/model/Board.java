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
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * Represents the game board for the RoboRally game, including all spaces,
 * players, and the current phase of the game. It serves as the central model
 * for the game's state, with methods to manipulate and query the board and
 * its contents.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Board extends Subject {
    @Expose

    public final int width;
    @Expose
    public final int height;

    @Expose
    private Course course;

    @Expose
    public final String boardName;
    @Expose
    private Integer gameId;
    @Expose
    private  Space[][] spaces;
    @Expose
    private  List<Player> players = new ArrayList<>();
    @Expose
    private Player current;
    @Expose
    private Phase phase = INITIALISATION;

    private String Course;

    @Expose
    private int step = 0;
    @Expose
    private boolean stepMode;

    private static final int MAX_PLAYERS = 6; // Example value to debug the code

    /**
     * Constructs a new Board with the given dimensions and name.
     * Initializes the spaces within the board.
     *
     * @param boardName the name of the board
     */
    public Board(Course course, @NotNull String boardName) {
        this.boardName = boardName;
        this.course = course;
        this.width = course.width;
        this.height = course.height;
        spaces = new Space[width][height];
        ArrayList<ArrayList<Space>> courseSpaces = course.getSpaces();
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++)  {
                Space courseSpace = courseSpaces.get(y).get(x);
                spaces[x][y] = createSpaceFromType(x, y, courseSpace);
            }
        }
        this.stepMode = false;
    }

    /**
     * Constructs a new Board with the given Course and then retrives the Width and Height
     *
     */


    public Board(int width, int height) {
        this.boardName = "default";
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;

    }

    /**
     * Creates a {@link Space} object based on the type specified in the {@link Space} object from the course.
     * This method chooses the appropriate constructor for the {@link Space} class depending on the type of
     * space described in the courseSpace object. It facilitates the initialization of spaces with specific
     * attributes such as types, headings, and indexes, which are critical for setting up the game board.
     *
     * @param x The x-coordinate on the game board where the space is to be located.
     * @param y The y-coordinate on the game board where the space is to be located.
     * @param courseSpace The template {@link Space} object that contains the type and possibly additional
     *                    properties needed to properly initialize a new {@link Space}.
     * @return A new {@link Space} object initialized with properties specified in courseSpace and located
     *         at the specified coordinates on the game board.
     * @author Hussein Jarrah
     */
    private Space createSpaceFromType(int x, int y, Space courseSpace) {
        if(courseSpace.getType() != null){
            switch (courseSpace.getType()) {
                case ENERGY_SPACE,
                     CONVEYOR_BELT,
                     DOUBLE_CONVEYOR_BELT,
                     DOUBLE_LEFTTREE_CONVEYOR_BELT,
                     DOUBLE_RIGHTTREE_CONVEYOR_BELT,
                     RIGHT_CONVEYOR_BELT,
                     LEFT_CONVEYOR_BELT,
                     LEFT_GEAR,
                     RIGHT_GEAR,
                     STARTING_GEAR,
                     RESPAWN,
                     WALL,
                     BOARD_LASER_START,
                     BOARD_LASER,
                     BOARD_LASER_END,
                     PRIORITY_ANTENNA:
                    return new Space(this, x, y, courseSpace.getType(), courseSpace.getHeading());
                case CHECKPOINT:
                    return new Space(this, x, y, courseSpace.getIndex());
                default:
                    return new Space(this, x, y);
            }
        }
        return new Space(this, x, y);
    }

    /**
     * Gets the unique game identifier for this board.
     *
     * @return the game ID, or null if not set
     */
    public void determineTurn(int x, int y) {
         int counter = 0;
        double[] distances = new double[players.size()];
        List<Player> temp = new ArrayList<>();

        for(Player player: players) {
            int dx = player.getSpace().x - x;
            int dy = player.getSpace().y - y;

            double distance = Math.sqrt((dx*dx)+(dy*dy));

            player.distance = distance;
            distances[counter] = distance;
           }

        double[] distancesSorted = Arrays.stream(distances).sorted().toArray();

        for (int i = 0; i < distances.length; i++){
            for (Player player: players){
                if (player.distance == distancesSorted[0]){
                    temp.add(player);
                }
            }
        }

        players.clear();
        players.addAll(temp);


        }

        // Sort distances array to determine order of players based on proximity


    /**
     * Sets the unique game identifier for this board. Throws an exception if
     * attempting to change an already set ID.
     *
     * @param gameId the new game ID
     * @throws IllegalStateException if the ID is already set and a different ID is given
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Returns the space at the specified coordinates on the board.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the space at the given coordinates, or null if out of bounds
     */
    public Space getSpace(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            // Error handling
            throw new IllegalArgumentException("Coordinates are out of board bounds.");
        }
        return spaces[x][y];
    }


    /**
     * Returns the number of players currently on the board.
     *
     * @return the number of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * Adds a player to the board if not already present.
     * @param player the player to add
     */
    public void addPlayer(@NotNull Player player) {
        if (players.size() >= MAX_PLAYERS) {
            // Handle the error when too many players are added.
            throw new IllegalStateException("Maximum number of players reached.");
        }
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        } else {
            throw new IllegalArgumentException("Player is either already on this board or associated with a different board.");
        }
    }

    /**
     * Returns the player at the specified index in the player list.
     *
     * @param i the index of the player
     * @return the player at the specified index, or null if index is out of bounds
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Gets the current player whose turn it is.
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Sets the current player to the given player.
     * @param player the player to set as the current player
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Gets the current phase of the game.
     *
     * @return the current game phase
     */
    public Phase getPhase() {
        return phase;
    }

    public Space getSpaceView(int x, int y){
        return spaces[x][y];
    }

    /**
     * Sets the current phase of the game to the given phase.
     *
     * @param phase the new game phase
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Gets the current step of the game within the current phase.
     * @return the current step
     */
    public int getStep() {
        return step;
    }

    /**
     * Sets the current step of the game within the current phase.
     * @param step the new step
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Checks if the game is in step mode, which means actions are executed step by step rather than all at once.
     * @return true if the game is in step mode, false otherwise
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Sets the game to step mode or continuous mode based on the parameter.
     * @param stepMode if true, the game will proceed step by step; if false, actions are executed all at once
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Gets the player's number/index based on the player object.
     * @param player the player whose number is queried
     * @return the index of the player in the player list, or -1 if the player is not on the board
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    /**
     * Retrieves the total number of players currently participating in the game.
     * @return the number of players in the game.
     */
    public int getPlayerAmount(){
        return this.players.size();
    }

    /**
     * Provides a status message about the current state of the game, including the phase, current player, and step.
     * This method is primarily for debugging and providing game status updates.
     * @return a string representation of the current game status
     */
    public String getStatusMessage() {
        // This is actually a view aspect, but for making the first task easy for
        // the students, this method gives a string representation of the current
        // status of the game (specifically, it shows the phase, the player and the step)

        // TODO Task1: this string could eventually be refined
        //      The status line should show more information based on
        //      situation; for now, introduce a counter to the Board,
        //      which is counted up every time a player makes a move; the
        //      status line should show the current player and the number
        //      of the current move!
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep();

        // TODO Task1: add a counter along with a getter and a setter, so the
        //      state of the board (game) contains the number of moves, which then can
        //      be used to extend the status message 
    }
}
