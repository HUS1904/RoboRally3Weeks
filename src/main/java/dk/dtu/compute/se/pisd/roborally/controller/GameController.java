
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * The GameController class is responsible for managing the game logic and state transitions
 * within the RoboRally game. It coordinates the execution of game phases, handling player
 * commands, and updating the game board. The GameController interacts closely with model
 * classes such as Board, Player, and CommandCard to reflect the game's current state.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {
    private boolean gearPhase = true;

    public int turnCounter = 0;

    public Board board;
    private BoardView boardView;

    private Timeline timeline;

    private Timeline timeline2;

    private AppController appController;

    @Setter
    @Getter
    private Lobby lobby;

    /**
     * Constructs a GameController with the specified game board.
     *
     * @param board the game board that this controller will manage
     */
    public GameController(@NotNull Board board, Lobby lobby, AppController appController) {
        this.board = board;
        this.lobby = lobby;
        this.appController = appController;
    }

    public GameController(@NotNull Board board) {
        this.board = board;
    }


    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void movePlayerToSpace(Player player,@NotNull Space space)  {
        List<Integer> cords = new ArrayList<>();
        if (player != null) {
            // Check if the target space is occupied
            if (space.getPlayer() == null) {
                // Move the current player to the target space
                player.setSpace(space);
                for (Player player1 : board.getPlayers()) {
                    cords.add(player1.getSpace().x);
                    cords.add(player1.getSpace().y);

                }
                try {
                    lobby.setPlayersPosition(cords);
                    LobbyUtil.httpPutLobby(lobby.getId(), lobby);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
    }


    public void stringToHeading(String string,Player player){
        switch (string) {
            case "south":
                player.setHeading(Heading.SOUTH);
                break;
            case "west":
                player.setHeading(Heading.WEST);
                break;
            case "north":
                player.setHeading(Heading.NORTH);
                break;
            case "east":
                player.setHeading(Heading.EAST);
                break;
        }


    }

    public void movePlayer(Player player, @NotNull Space space){
        List<Integer> cords = new ArrayList<>();
        if (player != null) {
            // Check if the target space is occupied
            if (space.getPlayer() == null && board.getCurrentPlayer().getName().equals(lobby.getCurrentPlayer())) {
                // Move the current player to the target space
                player.setSpace(space);
                for (Player player1 : board.getPlayers()) {
                    cords.add(player1.getSpace().x);
                    cords.add(player1.getSpace().y);

                }
            }

            lobby.setPlayersHeadings(board.headingsToString());

            if (gearPhase && board.getCurrentPlayer().getSpace().getType() == ActionField.STARTING_GEAR) {
                try {
                    lobby.setPlayersPosition(cords);
                    gearPhase = !gearPhase;
                    board.moveCurrentTurn();
                    lobby.setCurrentPlayer(board.getCurrentTurn().getName());

                    LobbyUtil.httpPutLobby(lobby.getId(), lobby);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }



    /**
     * Initiates the programming phase of the game where players program the movement of their robots
     * for the round. This method sets up the game board and players for the programming phase.
     */
    public void startProgrammingPhase() {

        board.setPhase(Phase.PROGRAMMING);
        board.setStep(0);


        board.getPlayers().forEach(player -> {
                    player.phase = Phase.PROGRAMMING;
                    });

        board.getPlayers().forEach(player -> {
            player.getProgramFields().forEach(field -> {
                field.setCard(null);
                field.setVisible(true);
            });
            player.getCardFields().forEach(field -> {
                field.setCard(player.getDeck().deal());
                field.setVisible(true);
            });
        });
        board.getPlayers().forEach(player -> {
           player.incrementEnergy(1);
        });
        startLobbyPolling();
    }

    // XXX: implemented in the current version
    public CommandCard generateRandomCommandCard() {
        ArrayList<Command> damageCards = new ArrayList<>(List.of(
                Command.SPAM,
                Command.RAMMINGGEAR,
                Command.RECHARGE
        ));
        ArrayList<Command> commands = new ArrayList<>(List.of(Command.values()));
        commands.removeAll(damageCards);

        int random = (int) (Math.random() * commands.size());
        return new CommandCard(commands.get(random), "program");
    }


    // Helper-method to generate a damage-card
    public CommandCard generateDamageCard() {
        return new CommandCard(Command.SPAM, "damage");
    }

    public CommandCard generateUpgradeCard() {
        List<Command> upgrades = List.of(Command.RECHARGE, Command.RAMMINGGEAR);

        int random = (int) (Math.random() * upgrades.size());
        return new CommandCard(upgrades.get(random), "upgrade");
    }


    /**
     * Completes the programming phase and prepares for the activation phase. This method
     * transitions the game from programming to activation, making the program fields of
     * the players visible for the current register.
     */
    public void finishProgrammingPhase() {
        if(turnCounter == 0){
        gearPhase = false;

        for(Player player: board.getPlayers()){
            if(player.getSpace().getType() != ActionField.STARTING_GEAR){
                gearPhase = true;
            }
        }
        }

        if(!gearPhase ) {
            Space antenna = board.getSpacesList()
                    .stream()
                    .filter(s -> s.getType() == ActionField.PRIORITY_ANTENNA)
                    .findAny()
                    .orElseThrow(NoSuchElementException::new);
            board.determineTurn(antenna.x, antenna.y);
            lobby.setCurrentPlayer(board.getCurrentTurn().getName());
            try {
                LobbyUtil.httpPutLobby(lobby.getId(), lobby);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            makeProgramFieldsInvisible();
            makeProgramFieldsVisible(0);
            board.setPhase(Phase.ACTIVATION);
            board.setStep(0);
            turnCounter++;
        }

    }

    /**
     * Executes the next register in the sequence for each player's robot. This method progresses the game by
     * one step in the activation phase, executing the command in the current register for each player.
     */
    public void executeRegister() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            try {
                lobby = LobbyUtil.getLobby(lobby.getId());
                List<Integer> cords = lobby.getPlayersPosition();
                Player currentPlayer = board.getCurrentPlayer();

                if(lobby.getCurrentPlayer().equals(board.getCurrentPlayer().getName())) {
                    makeProgramFieldsVisible(board.getStep() + 1);

                    if (board.getPhase() == Phase.ACTIVATION) {
                        int step = board.getStep();
                        if (step >= 0 && step < Player.NO_REGISTERS) {
                            currentPlayer.getProgramField(step).getCard().ifPresent(card -> {
                                if (card.command.isInteractive()) {
                                    board.setPhase(Phase.PLAYER_INTERACTION);
                                } else {
                                    executeCommand(currentPlayer, card.command);
                                }
                            });
                        }
                    }

                    advanceStep();
                    discardCards();

                    cords.clear();
                    for (Player player : board.getPlayers()) {
                        cords.add(player.getSpace().x);
                        cords.add(player.getSpace().y);
                    }



                    lobby.setPlayersHeadings(board.headingsToString());
                    lobby.setPlayersPosition(cords);

                    board.moveCurrentTurn();
                    lobby.setCurrentPlayer(board.getCurrentTurn().getName());

                    System.out.println("After moveCurrentTurn:");
                    System.out.println("Next Player: " + board.getCurrentTurn().getName());
                    System.out.println("Turn Index: " + board.getTurnIndex());

                    LobbyUtil.httpPutLobby(lobby.getId(), lobby);

                    timeline.stop();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void startLobbyPolling() {
        timeline2 = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

                lobby = LobbyUtil.getLobby(lobby.getId());
                List<Integer> coords = lobby.getPlayersPosition();
                List<String> headings = lobby.getPlayersHeadings();
                int maxPlayers = lobby.getMaxPlayers();

                // Check if we have the correct amount of data
                if (coords.size() == maxPlayers * 2 && headings.size() == maxPlayers) {
                    for (int i = 0; i < maxPlayers; i++) {
                        Player player = board.getPlayer(i).orElseThrow(NoSuchElementException::new);

                        // Update position
                        int x = coords.get(i * 2);
                        int y = coords.get(i * 2 + 1);
                        movePlayerToSpace(player, board.getSpace(x, y));

                        // Update heading
                        String headingString = headings.get(i);
                        stringToHeading(headingString, player);


                        // Debug output to verify the heading
                    }
                } else {
                    System.err.println("Mismatch between players and positions/headings data sizes.");
                }

        }));
        timeline2.setCycleCount(Timeline.INDEFINITE); // Run indefinitely until stopped
        timeline2.play();
    }



    private void discardCards() {
        board.getPlayers().forEach(player -> {
            player.getProgramFields().forEach(field -> {
                field.getCard().ifPresent(player.getDeck()::sendToDiscardPile);
            });
            player.getCardFields().forEach(field -> {
                field.getCard().ifPresent(player.getDeck()::sendToDiscardPile);
            });
        });
    }

//    private void activateRobotLasers() {
//        Set<ActionField> invalidValues = new HashSet<>();
//        invalidValues.add(ActionField.WALL);
//        invalidValues.add(ActionField.BOARD_LASER_START);
//        invalidValues.add(ActionField.BOARD_LASER_END);
//        invalidValues.add(ActionField.PRIORITY_ANTENNA);
//
//        for (int i = 0; i < board.getPlayerAmount(); i++) {
//            Player p = board.getPlayer(i);
//            Space s = p.getSpace();
//            SpaceView[][] spaces = boardView.getSpaceViews();
//            int x = p.getSpace().x;
//            int y = p.getSpace().y;
//
//            while (board.getSpace(x,y).getType() != null && !invalidValues.contains(board.getSpace(x,y).getType())){
//                if ((x >= 0 && x < board.width) && (y >= 0 && y < board.height)) {
////                    //PHASE = PROGRAMMING | LASERS = OFF
////                    if(board.getPhase() == Phase.PROGRAMMING){
////                        spaces[x][y].setAltImage("/" + board.getSpace(x,y).getType() + ".png");
////                        spaces[x][y].setChangeImage(true);
////                    }
//
//                    //PHASE = ACTIVATION | LASERS = ON
//                    if(board.getPhase() == Phase.ACTIVATION && !p.getSpace().equals(s)){
//                        spaces[x][y].setAltImage("/" + "BOARD_LASER" + ".png");
//                        spaces[x][y].setChangeImage(true);
//                    }
//                }
//
//                switch (p.getHeading()) {
//                    case NORTH:
//                        y--;
//                        break;
//                    case EAST:
//                        x++;
//                        break;
//                    case SOUTH:
//                        y++;
//                        break;
//                    case WEST:
//                        x--;
//                        break;
//                }
//            }
//        }
//    }

    /**
     * Advances the game to the next step in the activation phase.
     * If the current step is the last register, the game will switch to the programming phase.
     */
    private void advanceStep() {
        int currentStep = board.getStep();
        int nextStep = currentStep + 1;
        if (nextStep < Player.NO_REGISTERS) {
            board.setStep(nextStep);
        } else {
            // Wrap up activation phase or reset for a new cycle
            startProgrammingPhase();  // Assuming this method resets for a new cycle
        }
    }

    public Optional<Player> getWinner() {
        long checkpoints = board.getSpacesList().stream().map(Space::getType).filter(ActionField.CHECKPOINT::equals).count();

        return board.getPlayers().stream().filter(player -> player.getIndex() == checkpoints).findAny();
    }

    public void activateSpaces() {
        board.getSpacesList().forEach(Space::activate);
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS)
            board.getPlayers().forEach(player -> player.getProgramField(register).setVisible(true));
    }

    private void makeProgramFieldsInvisible() {
        board.getPlayers().stream().flatMap(player -> player.getProgramFields().stream()).forEach(field -> field.setVisible(false));
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
            if (board.getStep() >= Player.NO_REGISTERS) {
                this.startProgrammingPhase();
            } else {
                executeNextStep();
            }
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    private void executeNextStep() {
        while (true) {
            Player currentPlayer = board.getCurrentPlayer();
            if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
                int step = board.getStep();
                if (step >= 0 && step < Player.NO_REGISTERS) {
                    currentPlayer.getProgramField(step).getCard().ifPresent(card -> {
                        Command command = card.command;
                        if (command.isInteractive()) {
                            // Switch to interactive mode and break loop
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            return;  // Stop further execution to wait for user input
                        } else {
                            // Execute non-interactive command
                            executeCommand(currentPlayer, command);
                        }
                    });

                    // Move to the next player
                    int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                    if (nextPlayerNumber < board.getPlayersNumber()) {
                        board.setCurrentPlayer(board.getPlayer(nextPlayerNumber).orElse(null));
                    } else {
                        // Last player, move to next step or end of the cycle
                        step++;
                        if (step < Player.NO_REGISTERS) {
                            makeProgramFieldsVisible(step);
                            board.setStep(step);
                            board.setCurrentPlayer(board.getPlayer(0).orElseThrow(NoSuchElementException::new));  // Reset to first player
                        } else {
                            startProgrammingPhase();
                            return;  // Exit if all steps are done
                        }
                    }
                } else {
                    // This should not happen
                    assert false;
                }
            } else {
                // This should not happen
                assert false;
            }
            // This loop will continue until a break condition like an interactive command occurs
        }
    }

    public void executeCommandOptionAndContinue(@NotNull Player player, Command command) {
        // Execute the selected command
        executeCommand(player, command);

        // After executing the command, check if we need to progress the step or end the interactive phase
        board.setPhase(Phase.ACTIVATION);
        nextStepOrFinish();
    }

    private void nextStepOrFinish() {
        int currentStep = board.getStep() + 1;
        if (currentStep < Player.NO_REGISTERS) {
            board.setStep(currentStep);
            // Setup next command execution
        } else {
            // All commands executed, move to next phase or wrap up the current phase
            startProgrammingPhase(); // Assuming this method resets or prepares for a new cycle
        }
    }

    void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // Handle different commands
            switch (command) {
                case FORWARD:
                    this.moveForward(player, 1, true);
                    break;
                case FORWARD1:
                    this.moveForward(player, 2, true);
                    break;
                case FORWARD2:
                    this.moveForward(player, 3, true);
                    break;
                case Back:
                    this.moveForward(player, 1, false);
                    break;
                case RIGHT:
                case UTURN:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case Again:
                    // Execute the previous command again
                    if (board.getStep() - 1 >= 0) {
                        player.getProgramField(board.getStep() - 1).getCard().ifPresent(card -> executeCommand(player, card.command));
                    }
                default:
                    // DO NOTHING (for now)
            }
        }
        // Activate all spaces
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                board.getSpace(i, j).activate();
            }
        }
       for(Player player1 : board.getPlayers()){
           if((player1.getSpace().getType() == ActionField.CHECKPOINT) && (player1.getSpace().getIndex() == (player1.getIndex() + 1)) ){
               player1.incrementIndex();
           }
       }

        getWinner().ifPresent(appController::announceWinner);
    }

    /**
     * Moves the player's robot forward by one space, if possible, based on the robot's current heading and position.
     *
     * @param player the player whose robot should move forward
     */
    public void moveForward(Player player, int numSpaces, boolean forward) {
        if (player == null) return;

        Space currentSpace = player.getSpace();
        int newX = currentSpace.x;
        int newY = currentSpace.y;
        int directionMultiplier = forward ? 1 : -1;  // Positive for forward, negative for backward

        for (int i = 0; i < Math.abs(numSpaces); i++) {
            // Calculate the new position based on the player's heading and the direction multiplier
            switch (player.getHeading()) {
                case NORTH:
                    newY -= directionMultiplier;
                    break;
                case EAST:
                    newX += directionMultiplier;
                    break;
                case SOUTH:
                    newY += directionMultiplier;
                    break;
                case WEST:
                    newX -= directionMultiplier;
                    break;
            }

            // Check if the new position is within board limits
            if (newX < 0 || newX >= board.width || newY < 0 || newY >= board.height) {
                System.out.println("Move stopped: Reached board limits.");
                return;
            }

            Space nextSpace = board.getSpace(newX, newY);

            // Check if the next space is a wall
            if (nextSpace.getType() == ActionField.WALL) {
                System.out.println("Move stopped: Wall ahead.");
                return;
            }

            if (nextSpace.getPlayer() != null) {
                // Attempt to push the player at nextPosition
                Player otherPlayer = nextSpace.getPlayer();
                if (canPush(otherPlayer, player.getHeading(), forward)) {
                    // Move the current player to the next space after pushing
                    currentSpace.setPlayer(null);
                    nextSpace.setPlayer(player);
                    player.setSpace(nextSpace);
                } else {
                    System.out.println("Move stopped: Cannot push player.");
                    return;
                }
            } else {
                // Move the current player to the next space
                currentSpace.setPlayer(null);
                nextSpace.setPlayer(player);
                player.setSpace(nextSpace);
            }
        }
    }

    private boolean canPush (Player player, Heading heading,boolean forward){
        Space currentSpace = player.getSpace();
        int newX = currentSpace.x;
        int newY = currentSpace.y;
        int directionMultiplier = forward ? 1 : -1;  // Positive for forward, negative for backward

        // Calculate the position to which the player would be pushed
        switch (heading) {
            case NORTH:
                newY -= directionMultiplier;
                break;
            case EAST:
                newX += directionMultiplier;
                break;
            case SOUTH:
                newY += directionMultiplier;
                break;
            case WEST:
                newX -= directionMultiplier;
                break;
        }

        if (newX < 0 || newX >= board.width || newY < 0 || newY >= board.height) {
            return false;  // Return false if out of bounds
        }

        Space nextSpace = board.getSpace(newX, newY);
        if (nextSpace.isOccupiable() && nextSpace.getPlayer() == null && nextSpace.getType() != ActionField.WALL) {
            // Push the player to the new space if it is empty and not a wall
            currentSpace.setPlayer(null);
            nextSpace.setPlayer(player);
            player.setSpace(nextSpace);
            return true;
        }
        return false;
    }


    public void moveTo (Player player,int x, int y){
        Space nextSpace = board.getSpace(x, y);
        player.setSpace(nextSpace);
    }

    public void reInitialize (Board board){
        switch (board.getPhase()) {
            case PROGRAMMING:
                this.board.setPhase(Phase.PROGRAMMING);
                this.board.setCurrentPlayer(board.getPlayer(0).orElseThrow(NoSuchElementException::new));
                this.board.setStep(board.getStep());
                break;
            case ACTIVATION:
                this.finishProgrammingPhase();
                break;
            default:
        }
        this.board.setStep(board.getStep());
        this.board.setStepMode(board.isStepMode());

        for (int i = 0; i < this.board.getPlayerAmount(); i++) {
            Player player = this.board.getPlayer(i).orElseThrow(NoSuchElementException::new);
            Player otherPlayer = board.getPlayer(i).orElseThrow(NoSuchElementException::new);

            for (int j = 0; j < Player.NO_CARDS; j++) {
                CommandCardField field = player.getCardField(j);
                CommandCardField otherField = otherPlayer.getCardField(j);
                otherField.getCard().ifPresent(card -> {
                    field.setCard(new CommandCard(card.command, "program"));
                    field.setVisible(true);
                });
            }
            for (int k = 0; k < Player.NO_REGISTERS; k++) {
                CommandCardField field = player.getProgramField(k);
                CommandCardField otherField = otherPlayer.getProgramField(k);
                otherField.getCard().ifPresent(card -> {
                    field.setCard(new CommandCard(card.command, "program"));
                    field.setVisible(true);
                });
            }

            for (int k = 0; k < Player.NO_UPGRADES; k++) {
                CommandCardField field = player.getUpgradeField(k);
                CommandCardField otherField = otherPlayer.getUpgradeField(k);
                otherField.getCard().ifPresent(card -> {
                    field.setCard(new CommandCard(card.command, "upgrade"));
                    field.setVisible(true);
                });
            }

            for (int k = 0; k < Player.NO_UPGRADE_INV; k++) {
                CommandCardField field = player.getUpgradeInv(k);
                CommandCardField otherField = otherPlayer.getUpgradeInv(k);
                otherField.getCard().ifPresent(card -> {
                    field.setCard(new CommandCard(card.command, "upgrade"));
                    field.setVisible(true);
                });
            }
        }
    }

    /**
     * Rotates the player's robot 90 degrees to the right.
     * @param player the player whose robot should turn right
     */
    public void turnRight (Player player){
        board.getCurrentPlayer().getProgramField(board.getStep()).getCard().ifPresent(card -> {
            Heading heading = card.getName().equals("U-Turn")
                    ? player.getHeading().next().next()
                    : player.getHeading().next();
            player.setHeading(heading);
        });
    }

    /**
     * Rotates the player's robot 90 degrees to the left.
     * @param player the player whose robot should turn left
     */
    public void turnLeft (Player player){
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
    public boolean moveCards (@NotNull CommandCardField source, @NotNull CommandCardField target){
        return source.getCard().map(card -> {
            if(target.getCard().isEmpty()) {
                target.setCard(card);
                source.setCard(null);
                return true;
            } else return false;
        }).orElse(false);
    }
}
