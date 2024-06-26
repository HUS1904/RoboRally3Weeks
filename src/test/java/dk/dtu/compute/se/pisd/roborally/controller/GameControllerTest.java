package dk.dtu.compute.se.pisd.roborally.controller;

import com.beust.ah.A;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private AppController appController;

    @Test
    public void getWinnerTest() {
        ArrayList<Space> spaceLine = new ArrayList<>(List.of(
                new Space(null, 1, 0, 1),
                new Space(null, 2, 0, 2),
                new Space(null, 3, 0, 3)
        ));
        ArrayList<ArrayList<Space>> spaces = new ArrayList<>();
        spaces.add(spaceLine);
        Course course = new Course(3, 1, spaces);
        course.getSpaces().add(spaceLine);
        Board board = new Board(course, "TestBoard");
        GameController gameController = new GameController(board);
        Player player = new Player(board, "Blue", "TestPlayer", gameController);
        board.addPlayer(player);

        player.incrementIndex();
        player.incrementIndex();
        player.incrementIndex();

        assert gameController.getWinner().isPresent();
    }
}
