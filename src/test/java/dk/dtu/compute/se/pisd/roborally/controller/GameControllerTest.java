package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private GameController gameController;
    private Board board;
    private Player player;


    @BeforeEach
    public void setUp() {
        board = new Board(8, 8);
        gameController = new GameController(board);
        player = new Player(board, "Blue", "TestPlayer");
        board.addPlayer(player);
        Space startingSpace = board.getSpace(0, 0); // Initial position for testing purposes
        player.setSpace(startingSpace);
        board.setCurrentPlayer(player);
    }

    @Test
    public void testMoveCurrentPlayerToSpace() {
        Space originalSpace = board.getCurrentPlayer().getSpace();
        Space targetSpace = board.getSpace(1, 1);
        gameController.moveCurrentPlayerToSpace(targetSpace);

        assertEquals(targetSpace, board.getCurrentPlayer().getSpace(), "Current player should move to the target space.");
        assertNull(originalSpace.getPlayer(), "Original space should be empty after the move.");
    }

    @Test
    public void testStartProgrammingPhase() {
        gameController.startProgrammingPhase();
        assertEquals(Phase.PROGRAMMING, board.getPhase(), "Board phase should be PROGRAMMING.");
    }

    @Test
    public void testFinishProgrammingPhase() {
        gameController.startProgrammingPhase();
        gameController.finishProgrammingPhase();
        assertEquals(Phase.ACTIVATION, board.getPhase(), "Board phase should transition to ACTIVATION.");
    }

    @Test
    public void testExecuteCommandForward() {
        player.setSpace(board.getSpace(4, 4));
        player.setHeading(Heading.NORTH);
        CommandCard forwardCard = new CommandCard(Command.FORWARD);
        gameController.executeCommand(player, forwardCard.command);

        assertEquals(board.getSpace(4, 3), player.getSpace(), "Player should have moved north by one space.");
    }



    @Test
    public void testExecuteCommandTurnRight() {
        player.setHeading(Heading.NORTH);
        CommandCard turnRightCard = new CommandCard(Command.RIGHT);
        gameController.executeCommand(player, turnRightCard.command);

        assertEquals(Heading.EAST, player.getHeading(), "Player should be facing East after turning right.");
    }

    @Test
    public void testExecuteCommandTurnLeft() {
        player.setHeading(Heading.NORTH);
        CommandCard turnLeftCard = new CommandCard(Command.LEFT);
        gameController.executeCommand(player, turnLeftCard.command);

        assertEquals(Heading.WEST, player.getHeading(), "Player should be facing West after turning left.");
    }

    @Test
    public void testMoveCards() {
        CommandCardField source = new CommandCardField(player);
        CommandCardField target = new CommandCardField(player);
        CommandCard card = new CommandCard(Command.FORWARD);
        source.setCard(card);
        boolean result = gameController.moveCards(source, target);
        assertTrue(result, "Card should be moved successfully.");
        assertNull(source.getCard(), "Source field should be empty after move.");
        assertEquals(card, target.getCard(), "Target field should contain the moved card.");
    }

    @Test
    public void testExecuteCommandTurnRightFromDifferentHeadings() {
        for (Heading heading : Heading.values()) {
            player.setHeading(heading);
            gameController.turnRight(player);
            assertEquals(heading.next(), player.getHeading(), "Player should be facing " + heading.next() + " after turning right from " + heading);
        }
    }

    @Test
    public void testExecuteCommandTurnLeftFromDifferentHeadings() {
        for (Heading heading : Heading.values()) {
            player.setHeading(heading);
            gameController.turnLeft(player);
            assertEquals(heading.prev(), player.getHeading(), "Player should be facing " + heading.prev() + " after turning left from " + heading);
        }
    }

    @Test
    public void testMoveOffBoard() {
        player.setSpace(board.getSpace(0, 0));
        player.setHeading(Heading.WEST); // Facing off the board
        gameController.moveForward(player);

        // Player should remain in the same place
        assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not move off the board.");
    }

    @Test
    public void testMoveOntoOccupiedSpace() {
        Player otherPlayer = new Player(board, "Green", "Player2");
        board.addPlayer(otherPlayer);
        Space startingSpace = board.getSpace(4, 4);
        Space occupiedSpace = board.getSpace(4, 3);
        player.setSpace(startingSpace);
        player.setHeading(Heading.NORTH);
        otherPlayer.setSpace(occupiedSpace);

        gameController.moveForward(player);

        // Player should remain in the starting space
        assertEquals(startingSpace, player.getSpace(), "Player should not move onto an occupied space.");
        // The other player should still be in the occupied space
        assertEquals(otherPlayer, occupiedSpace.getPlayer(), "The other player should remain on the occupied space.");
    }
}
