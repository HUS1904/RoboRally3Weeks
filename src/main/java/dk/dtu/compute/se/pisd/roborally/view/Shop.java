package dk.dtu.compute.se.pisd.roborally.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
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

    public Shop(GameController gameController){
        this.deck = gameController.board.getShop();
        //this.deck = deck.generateUpgradeDeck(gameController);
        this.gameController = gameController;
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Facedown.png")));


        /// button to change phase
        Button phaseChanger = new Button("Start programming phase");
        phaseChanger.setOnAction(e -> handleClick());

        phaseChanger.setAlignment(Pos.CENTER);


        // the cardfield view

        GridPane upgradshop = new GridPane();
        upgradshop.setAlignment(Pos.CENTER);
        upgradshop.setHgap(10);


        Pane deck1 = new Pane();
        ImageView deckImage = new ImageView(image);
        deckImage.setFitHeight(70);
        deckImage.setFitWidth(60);
        deck1.getChildren().add(deckImage);


        for(int i = 0; i< gameController.board.getPlayerAmount();i++) {


                this.deck.generateUpgradeDeck(gameController);
                CommandCard commandCard = this.deck.deal();
                CommandCardField cardfield = new CommandCardField(gameController.board.getCurrentPlayer(), "upgrade");

                cardfield.setCard(commandCard);
                CardFieldView fieldView = new CardFieldView(gameController, cardfield);
                gameController.board.getShopFields().add(cardfield);
                upgradshop.add(fieldView, i, 0);


            }

        upgradshop.add(deck1,gameController.board.getPlayerAmount(),0);





        this.getChildren().addAll(phaseChanger,upgradshop);


    }


    public Lobby getLobby(long id) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpGet request
            HttpGet getRequest = new HttpGet("http://localhost:8080/api/lobby/" + id);
            getRequest.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                // Print the response status code
                System.out.println("Status code: " + response.getStatusLine().getStatusCode());

                // Parse the JSON response into a list of Lobby objects
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getEntity().getContent(), new TypeReference<Lobby>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void putRequest(Lobby lobby) {
        String url = "http://localhost:8080/api/lobby/shop/" + lobby.getId(); // Example URL with ID

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpPut request with the URL
            HttpPut putRequest = new HttpPut(url);

            // Convert the Lobby object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(lobby);

            // Set the JSON as the entity of the put request
            StringEntity entity = new StringEntity(json);
            putRequest.setEntity(entity);
            putRequest.setHeader("Accept", "application/json");
            putRequest.setHeader("Content-type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(putRequest)) {
                // Print the response status code
                System.out.println("Status code: " + response.getStatusLine().getStatusCode());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleClick() {

        if (gameController.board.getCurrentPlayer() == gameController.board.getCurrentTurn()) {
            List<String> cardFields = new ArrayList<>();

            Lobby lobby = gameController.getLobby();
            gameController.board.getCurrentTurn().phase = Phase.PROGRAMMING;

            if(gameController.board.getPlayers().indexOf(gameController.board.getCurrentPlayer()) == gameController.board.getPlayerAmount() -1 &&
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
            putRequest(lobby);


            if(gameController.board.getTurnIndex() != gameController.board.getPlayerAmount()) {
                timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                    Lobby updatedLobby = getLobby(lobby.getId());
                    if (updatedLobby.getPlayerIndex() >= gameController.board.getPlayerAmount()) {
                        // Logic when player index is as big as the amount of players
                        gameController.startProgrammingPhase();
                        timeline.stop(); // Stop the timeline when the condition is met
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely until stopped
                timeline.play();

            }






        }







        }


    }


