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
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dk.dtu.compute.se.pisd.roborally.model.Command.SPAM;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * Represents a player in the RoboRally game. Each player has a set of programming
 * registers and command cards, a current position on the board, and a direction
 * they are facing. This class manages the player's state, including their name,
 * color, and the cards they have been dealt.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Player extends Subject {
    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;
    final public static int TEMPORARY_UPGRADES = 3;
    final public static int PERMANENT_UPGRADES = 3;


    final transient public Board board;
    @Getter
    @Expose
    private String name;
    @Getter
    @Expose
    private String color;
    @Getter
    @Expose
    private Space space;
    @Getter
    @Expose
    private Heading heading = SOUTH;
    @Expose
    private ArrayList<CommandCardField> program = new ArrayList<>();
    @Expose
    private ArrayList<CommandCardField> cards = new ArrayList<>();
    @Expose
    private ArrayList<CommandCardField> permUpgrades = new ArrayList<>();
    @Expose
    private ArrayList<CommandCardField> tempUpgrades = new ArrayList<>();
    @Getter
    private int index = 0;
    @Expose
    public double distance;
    @Expose
    public Phase phase;

    private int energyCubes;

    @Getter
    private final Deck deck;

    private final GameController gameController;

    /**
     * Constructs a new Player with the specified board, color, and name.
     * Initializes the player's program and command card fields.
     * @param board the board on which the player is playing
     * @param color the color representing the player
     * @param name the name of the player
     */
    public Player(@NotNull Board board, String color, @NotNull String name, GameController gameController) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.energyCubes = 5;

        this.space = null;
        this.phase = Phase.INITIALISATION;
        this.gameController = gameController;

        this.deck = new Deck("program",gameController);

        for (int i = 0; i < NO_REGISTERS; i++) {
            program.add(new CommandCardField(this,"program"));
        }

        for (int i = 0; i < NO_CARDS; i++) {
            cards.add(new CommandCardField(this,"program"));
        }

        for (int i = 0; i < 3; i++) {
             permUpgrades.add(new CommandCardField(this,"upgrade"));
        }

        for (int i = 0; i < 3; i++) {
            tempUpgrades.add(new CommandCardField(this,"upgrade"));
        }
    }

    /**
     * Sets the name of the player and notifies observers of the change.
     * @param name the new name of the player
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Sets the color representing the player and notifies observers of the change.
     * @param color the new color of the player
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Sets the player's current space on the board and notifies observers of the change.
     * @param space the new space for the player
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }

        }
    }

    /**
     * Sets the heading (direction) of the player and notifies observers of the change.
     * @param heading the new heading for the player
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Gets the programming field at the specified index.
     * @param i the index of the programming field
     * @return the programming field at the specified index
     */
    public CommandCardField getProgramField(int i) {
        return program.get(i);
    }

    /**
     * Gets all the programming fields
     * @return an ArrayList of programming fields
     */
    public ArrayList<CommandCardField> getProgramFields() {
        return program;
    }

    /**
     * Gets the command card field at the specified index.
     * @param i the index of the command card field
     * @return the command card field at the specified index
     */
    public CommandCardField getCardField(int i) {
        return cards.get(i);
    }

    /**
     * Gets all the card fields
     * @return an ArrayList of card fields
     */
    public ArrayList<CommandCardField> getCardFields() {
        return cards;
    }

    public CommandCardField getPermUpgradeField(int i) {
        return permUpgrades.get(i);
    }
    public CommandCardField getTempUpgradeInv(int i) {
        return tempUpgrades.get(i);
    }

    public ArrayList<CommandCardField> getUpgradeFields() {
        return tempUpgrades;
    }

    public void incrementIndex() {
        index++;
    }

    public void move(int n) {
        if(n == 0) {
            return;
        } else {
            move(n - 1);
        }

        Space nextSpace = switch (heading) {
            case SOUTH -> board.getSpace(space.x, space.y + 1);
            case NORTH -> board.getSpace(space.x, space.y - 1);
            case WEST  -> board.getSpace(space.x - 1, space.y);
            case EAST  -> board.getSpace(space.x + 1, space.y);
        };

        switch (nextSpace.getType()) {
            case WALL: return;
            case CHECKPOINT:
                incrementIndex();
                break;
            case BOARD_LASER:
                deck.sendToDiscardPile(gameController.generateDamageCard());
                System.out.println("1 card sent to discard-pile");
        }

        setSpace(nextSpace);
    }

    /**
     * Gets the amount of energy cubes the player has
     * @return returns the amount of energy cubes
     */
    public int getEnergy(){
        return this.energyCubes;
    }

    /**
     * increments or decrements the players energy cubes
     * @param amount can be either negative or positive
     */
    public void incrementEnergy(int amount){
        this.energyCubes += amount;

        notifyChange();
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean containsUpgradeCardWithCommand(CommandCardField field, Command command) {
        for (CommandCardField permField : permUpgrades) {
            Optional<CommandCard> card = permField.getCard();
            if (card.isPresent() && card.get().command == command) {
                return true;
            }
        }

        for (CommandCardField tempField : tempUpgrades) {
            Optional<CommandCard> card = tempField.getCard();
            if (card.isPresent() && card.get().command == command) {
                return true;
            }
        }
        return false;
    }
}



