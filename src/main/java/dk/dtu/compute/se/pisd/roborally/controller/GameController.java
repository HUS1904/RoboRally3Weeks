
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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The GameController class is responsible for managing the game logic and state transitions
 * within the RoboRally game. It coordinates the execution of game phases, handling player
 * commands, and updating the game board. The GameController interacts closely with model
 * classes such as Board, Player, and CommandCard to reflect the game's current state.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

   public Board board;

    /**
     * Constructs a GameController with the specified game board.
     *
     * @param board the game board that this controller will manage
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer != null) {
            // Check if the target space is occupied
            if (space.getPlayer() == null) {
                // Move the current player to the target space
                currentPlayer.setSpace(space);

            }
        }
    }



    /**
     * Initiates the programming phase of the game where players program the movement of their robots
     * for the round. This method sets up the game board and players for the programming phase.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    CommandCard card = player.getDeck().deal();
                    field.setCard(card);
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: implemented in the current version
   public CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();

        // Make sure no damage-cards are generated
       for (int i = 0; i < commands.length; i++) {
           if (commands[i] == Command.valueOf("SPAM") || commands[i] == Command.valueOf("RAMMINGGEAR")
           || commands[i] == Command.valueOf("RECHARGE")) {
               commands[i] = commands[i-1];
           }
       }

        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random],"program");
    }


    // Helper-method to generate a damage-card
    public CommandCard generateDamageCard() {
        Command[] commands = Command.values();
        int index = 0;

        for (int i = 0; i < commands.length; i++) {
            if (commands[i] == Command.valueOf("SPAM")) {
                index = i;
            }
        }

        return new CommandCard(commands[index], "damage");
    }

    public CommandCard generateUpgradeCard() {
        Command[] commands = Command.values();
        List<Command> upgrades = new ArrayList<>();

        for (Command command : commands) {
            if (command == Command.valueOf("RECHARGE") || command == Command.valueOf("RAMMINGGEAR")) {
                upgrades.add(command);
            }
        }

        int random = (int) (Math.random() * upgrades.size());
        return new CommandCard(upgrades.get(random),"upgrade");
    }




    /**
     * Completes the programming phase and prepares for the activation phase. This method
     * transitions the game from programming to activation, making the program fields of
     * the players visible for the current register.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

    }

    /**
     * Executes the next register in the sequence for each player's robot. This method progresses the game by
     * one step in the activation phase, executing the command in the current register for each player.
     */
    public void executeRegister(){
        this.board.getCurrentPlayer().incrementEnergy(1);
        makeProgramFieldsVisible(board.getStep() + 1);
        for (int i = 0; i < board.getPlayerAmount(); i++) {
            Player currentPlayer = board.getPlayer(i);
            if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
                int step = board.getStep();
                if (step >= 0 && step < Player.NO_REGISTERS) {
                    CommandCard card = currentPlayer.getProgramField(step).getCard();
                    if (card != null) {
                        Command command = card.command;
                        executeCommand(currentPlayer, command);
                    }
                }

            }
        }
        if(board.getStep() != 5) {
            board.setStep(board.getStep() + 1);
        } else {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                if (player != null) {
                    for (int j = 0; j < Player.NO_REGISTERS; j++) {
                        CommandCardField field = player.getProgramField(j);
                        if(field.getCard() != null){
                            CommandCard card = field.getCard();
                            player.getDeck().sendToDiscardPile(card);
                        }
                    }
                    for (int j = 0; j < Player.NO_CARDS; j++) {
                        CommandCardField field = player.getCardField(j);
                        if(field.getCard() != null){
                            CommandCard card = field.getCard();
                            player.getDeck().sendToDiscardPile(card);
                        }
                    }
                }
            }

            startProgrammingPhase();
        }
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Executes the programs of all players in sequence, according to the command cards
     * placed in their program registers. This method handles the activation phase of the
     * game, executing each step until the phase is complete.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes a single step of the current player's program. This method is used when
     * the game is in step mode, allowing for step-by-step execution of commands.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            if(board.getStep() >= board.getCurrentPlayer().NO_REGISTERS){
                this.startProgrammingPhase();
            } else {
                executeNextStep();
            }
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    private void executeNextStep() {
        board.setCurrentPlayer(board.getPlayer(0));
        for (int i = 0; i < board.getPlayerAmount(); i++) {
            Player currentPlayer = board.getPlayer(i);
            if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
                int step = board.getStep();
                if (step >= 0 && step < Player.NO_REGISTERS) {
                    CommandCard card = currentPlayer.getProgramField(step).getCard();
                    if (card != null) {
                        Command command = card.command;
                        executeCommand(currentPlayer, command);
                    }
                    int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                    if (nextPlayerNumber < board.getPlayersNumber()) {
                        board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                    } else {
                        step++;
                        if (step < Player.NO_REGISTERS) {
                            makeProgramFieldsVisible(step);
                            board.setStep(step);
                            board.setCurrentPlayer(board.getPlayer(0));
                        } else {
                            startProgrammingPhase();
                        }
                    }
                } else {
                    // this should not happen
                    assert false;
                }
            } else {
                // this should not happen
                assert false;
            }
        }
    }

    void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player,1);
                    break;
                case FORWARD1:
                    this.moveForward(player,2);
                    break;
                case FORWARD2:
                    this.moveForward(player,3);
                    break;
                case Back:
                    this.moveForward(player,1);
                    break;
                case RIGHT:
                case UTURN:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case Again:
                    if(board.getStep() - 1 >= 0){
                        executeCommand(player,player.getProgramField(board.getStep() - 1).getCard().command);
                    }


                default:
                    // DO NOTHING (for now)
            }
        }
        for(int i = 0; i < board.width; i++) {
            for(int j = 0; j < board.height; j++) {
                board.getSpace(i, j).activate();
            }
        }
        /*if(player.getIndex() == board.maxIndex) {
            end();
        }*/
    }

    /**
     * Moves the player's robot forward by one space, if possible, based on the robot's current heading and position.
     *
     * @param player the player whose robot should move forward
     */
    public void moveForward(Player player,int num) {
        Space currentSpace = player.getSpace();


        Heading heading = player.getHeading();
        if(board.getCurrentPlayer().getProgramField(board.getStep()).getCard().getName() != null){
            if(board.getCurrentPlayer().getProgramField(board.getStep()).getCard().getName().equals("Back up")) {
                heading = heading.next().next();
                ;
            }
        }


        int currentX = currentSpace.x;
        int currentY = currentSpace.y;

        int nextX = currentX;
        int nextY = currentY;

        for(int i = 0; i <num; i++) {
            switch (heading) {
                case NORTH:
                    nextY--;
                    break;
                case EAST:
                    nextX++;
                    break;
                case SOUTH:
                    nextY++;
                    break;
                case WEST:
                    nextX--;
                    break;
            }
        }

        // Check if the next space is within the board boundaries
        if (nextX >= 0 && nextX < board.width && nextY >= 0 && nextY < board.height) {
            Space nextSpace = board.getSpace(nextX, nextY);

            // If the next space is empty, move the player to that space
            if (nextSpace.getPlayer() == null) {
                player.setSpace(nextSpace);
                board.setCurrentPlayer(player);
            }
        }
    }

    public void moveTo(Player player,int x ,int y) {
        Space nextSpace = board.getSpace(x, y);
        player.setSpace(nextSpace);
    }

    public void reInitialize(Board board) {
        Phase phase = board.getPhase();
        switch (phase){
            case PROGRAMMING:
                this.board.setPhase(Phase.PROGRAMMING);
                this.board.setCurrentPlayer(board.getPlayer(0));
                this.board.setStep(board.getStep());
                break;
            case ACTIVATION:
                this.finishProgrammingPhase();
                break;
            default:
        }
        this.board.setStep(board.getStep());
        this.board.setStepMode(board.isStepMode());

        for(int i = 0; i < this.board.getPlayerAmount(); i++){
            Player player = this.board.getPlayer(i);
            Player otherPlayer = board.getPlayer(i);

            for(int j = 0; j < Player.NO_CARDS; j++){
                CommandCardField field = player.getCardField(j);
                CommandCardField otherField = otherPlayer.getCardField(j);
                CommandCard card = otherField.getCard();
                if(card != null){
                    field.setCard(new CommandCard(card.command,"program"));
                    field.setVisible(true);

                }
            }
            for(int k = 0; k < Player.NO_REGISTERS; k++){
                CommandCardField field = player.getProgramField(k);
                CommandCardField otherField = otherPlayer.getProgramField(k);
                CommandCard card = otherField.getCard();
                if(card != null){
                    field.setCard(new CommandCard(card.command,"program"));
                    field.setVisible(true);


                }
            }
        }
    }

    /**
     * Moves the player's robot forward by two spaces, if possible, to simulate the Fast Forward command.
     * @param player the player whose robot should move forward quickly
     */
    public void fastForward(Player player) {
        Space currentSpace = player.getSpace();
        Heading heading= player.getHeading();


        int currentX = currentSpace.x;
        int currentY = currentSpace.y;

        int secondNextX = currentX;
        int secondNextY = currentY;

        // Calculate the second next space coordinates based on the player's heading
        switch (heading) {
            case NORTH:
                secondNextY -= 2;
                break;
            case EAST:
                secondNextX += 2;
                break;
            case SOUTH:
                secondNextY += 2;
                break;
            case WEST:
                secondNextX -= 2;
                break;
        }

        // Check if the second next space is within the board boundaries
        if (secondNextX >= 0 && secondNextX < board.width && secondNextY >= 0 && secondNextY < board.height) {
            Space secondNextSpace = board.getSpace(secondNextX, secondNextY);

            // If the second next space is empty, move the player to that space
            if (secondNextSpace.getPlayer() == null) {
                currentSpace.setPlayer(null);
                secondNextSpace.setPlayer(player);
                board.setCurrentPlayer(player);
            }
        }
    }

    /**
     * Rotates the player's robot 90 degrees to the right.
     * @param player the player whose robot should turn right
     */
    public void turnRight(Player player) {
        Heading heading = player.getHeading();
        if(board.getCurrentPlayer().getProgramField(board.getStep()).getCard().getName().equals("U-Turn")) {
            player.setHeading(heading.next().next());
            ;
        } else {
            player.setHeading(heading.next());
        }
    }

    /**
     * Rotates the player's robot 90 degrees to the left.
     * @param player the player whose robot should turn left
     */
    public void turnLeft(Player player) {
        Heading heading = player.getHeading();
        player.setHeading(heading.prev());
    }

    /**
     * Moves a command card from a source field to a target field. This method is used to
     * simulate the player's action of organizing their command cards during the programming phase.
     *
     * @param source the source CommandCardField from which the card will be moved
     * @param target the target CommandCardField to which the card will be moved
     * @return true if the card was successfully moved, false otherwise
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}