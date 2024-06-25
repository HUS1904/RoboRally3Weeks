package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandCardGeneratorTest {
    private RoboRally roboRally;
    private AppController appController;
    private Course course;
    private Board board;
    private GameController gameController;

    @BeforeEach
    public void setUp() {
        roboRally = new RoboRally();
        appController = new AppController(roboRally);
        board = new Board(100, 100);
        course = new Course(100, 100);

        ArrayList<ArrayList<Space>> spaces = new ArrayList<>();
        for (int y = 0; y < course.height; y++) {
            ArrayList<Space> row = new ArrayList<>();
            for (int x = 0; x < course.width; x++) {
                row.add(new Space(board, x, y));
            }
            spaces.add(row);
        }
        course.setSpaces(spaces);

        gameController = new GameController(board, appController);
    }


    @Test
    void testGenerateDamageCard() {
        CommandCard card = gameController.generateDamageCard();
        assertEquals(Command.SPAM, card.getCommand());
        assertEquals("damage", card.getType());
    }

    @Test
    void testGenerateVirusCard() {
        CommandCard card = gameController.generateVirusCard();
        assertEquals(Command.VIRUSMODULE, card.getCommand());
        assertEquals("virus", card.getType());
    }

    @Test
    void testGenerateUpgradeCard() {
        CommandCard card = gameController.generateUpgradeCard();
        assertEquals("upgrade", card.getType());
        assertTrue(List.of(Command.RECHARGE, Command.RAMMINGGEAR, Command.VIRUSMODULE, Command.BOINK).contains(card.getCommand()));
    }
}
