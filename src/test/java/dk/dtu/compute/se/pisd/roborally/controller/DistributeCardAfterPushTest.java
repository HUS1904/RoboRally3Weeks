package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DistributeCardAfterPushTest {
    private GameController gameController;
    private AppController appController;
    private Board board;
    private RoboRally roboRally;

    @Mock
    private Player player;

    @Mock
    private Player otherPlayer;

    @Mock
    private Space currentSpace;

    @Mock
    private Space nextSpace;

    @Mock
    private Deck otherPlayerDeck;

    @BeforeEach
    public void setUp() {
        roboRally = new RoboRally();
        appController = new AppController(roboRally);
        board = new Board(100, 100);

        player = mock(Player.class);
        otherPlayer = mock(Player.class);
        currentSpace = mock(Space.class);
        nextSpace = mock(Space.class);
        otherPlayerDeck = mock(Deck.class);

        gameController = new GameController(board, appController);

        when(otherPlayer.getDeck()).thenReturn(otherPlayerDeck);

        doNothing().when(otherPlayerDeck).addToDeck(any(CommandCard.class));
        doNothing().when(otherPlayerDeck).sendToDiscardPile(any(CommandCard.class));
    }

    @Test
    void testCardDistributionAfterPush() {
        when(nextSpace.getPlayer()).thenReturn(otherPlayer);
        when(player.getHeading()).thenReturn(Heading.NORTH);
        when(player.containsUpgradeCardWithCommand(any(), any())).thenReturn(true);

        int currentX = 1;
        int currentY = 1;
        int nextX = 2;
        int nextY = 2;

        when(currentSpace.getX()).thenReturn(currentX);
        when(currentSpace.getY()).thenReturn(currentY);
        when(nextSpace.getX()).thenReturn(nextX);
        when(nextSpace.getY()).thenReturn(nextY);

        gameController.moveTo(player, nextSpace.getX(), nextSpace.getY());

        otherPlayerDeck.addToDeck(gameController.generateDamageCard());
        otherPlayerDeck.sendToDiscardPile(gameController.generateDamageCard());

        ArgumentCaptor<CommandCard> cardCaptor = ArgumentCaptor.forClass(CommandCard.class);

        verify(otherPlayerDeck, times(1)).addToDeck(cardCaptor.capture());
        CommandCard addedCard = cardCaptor.getValue();
        assertNotNull(addedCard, "Card added is not null");

        verify(otherPlayerDeck, times(1)).sendToDiscardPile(cardCaptor.capture());
        CommandCard discardedCard = cardCaptor.getValue();
        assertNotNull(discardedCard, "Card discarded is not null");
    }
}