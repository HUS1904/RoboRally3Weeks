package dk.dtu.compute.se.pisd.roborally.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Shop extends VBox {

    GameController gameController;
    Image image;
    Deck deck = null;

    Timeline timeline;

    Timeline timeline2;

    public Shop(GameController gameController) {
        this.deck = gameController.board.getShop();
        this.gameController = gameController;
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Facedown.png")));

        /// button to change phase
        Button phaseChanger = new Button("Start programming phase");
        phaseChanger.setOnAction(e -> {
            try {
                handleClick();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        phaseChanger.setAlignment(Pos.CENTER);

        // the cardfield view
        GridPane upgradeShop = new GridPane();
        upgradeShop.setAlignment(Pos.CENTER);
        upgradeShop.setHgap(10);

        Pane deck1 = new Pane();
        ImageView deckImage = new ImageView(image);
        deckImage.setFitHeight(70);
        deckImage.setFitWidth(60);
        deck1.getChildren().add(deckImage);

        for (int i = 0; i < gameController.board.getPlayerAmount(); i++) {
            this.deck.generateUpgradeDeck(gameController);
            CommandCard commandCard = this.deck.deal();
            CommandCardField cardField = new CommandCardField(gameController.board.getCurrentPlayer(), "upgrade");
            cardField.setCard(commandCard);
            CardFieldView fieldView = new CardFieldView(gameController, cardField);
            gameController.board.getShopFields().add(cardField);
            upgradeShop.add(fieldView, i, 0);
        }

        upgradeShop.add(deck1, gameController.board.getPlayerAmount(), 0);

        this.getChildren().addAll(phaseChanger, upgradeShop);

        startLobbyPolling();
    }

    private void startLobbyPolling() {
        timeline2 = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            try {
                gameController.setLobby(LobbyUtil.getLobby(gameController.getLobby().getId()));
                Lobby lobby = gameController.getLobby();
                gameController.board.setTurnIndex(lobby.getPlayerIndex());
                gameController.board.setCurrentTurn(gameController.board.findCorrespondingPlayer(lobby.getCurrentPlayer()));
                gameController.board.readjustShop(lobby.getCardField());
            } catch (Exception ex) {
                ex.printStackTrace(); // Handle the exception appropriately
            }
        }));
        timeline2.setCycleCount(Timeline.INDEFINITE); // Run indefinitely until stopped
        timeline2.play();
    }

    public void handleClick() throws IOException {
        if (gameController.board.getCurrentPlayer() == gameController.board.getCurrentTurn()) {
            List<String> cardFields = new ArrayList<>();

            Lobby lobby = gameController.getLobby();
            gameController.board.getCurrentTurn().phase = Phase.PROGRAMMING;

            if (gameController.board.getPlayers().indexOf(gameController.board.getCurrentPlayer()) == gameController.board.getPlayerAmount() - 1 &&
                    gameController.board.getCurrentTurn().phase == Phase.PROGRAMMING) {
                gameController.startProgrammingPhase();
            }

            gameController.board.moveCurrentTurn();
            lobby.setCurrentPlayer(gameController.board.getCurrentTurn().getName());

            for (CommandCardField commandField : gameController.board.getShopFields()) {
                if (commandField.getCard().isEmpty()) {
                    cardFields.add("null");
                } else {
                    cardFields.add("filled");
                }
            }
            lobby.setCardField(cardFields);
            lobby.setPlayerIndex(gameController.board.getTurnIndex());
            LobbyUtil.httpPutLobby(lobby.getId(), lobby);

            if (gameController.board.getTurnIndex() != gameController.board.getPlayerAmount()) {
                startProgrammingPhasePolling(lobby);
            }
        }
    }

    private void startProgrammingPhasePolling(Lobby lobby) {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            try {
                Lobby updatedLobby = LobbyUtil.getLobby(lobby.getId());
                if (updatedLobby.getPlayerIndex() >= gameController.board.getPlayerAmount()) {
                    gameController.startProgrammingPhase();
                    timeline.stop();
                    timeline2.stop();
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // Handle the exception appropriately
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely until stopped
        timeline.play();
    }
}
