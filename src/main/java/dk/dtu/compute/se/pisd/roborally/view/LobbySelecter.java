package dk.dtu.compute.se.pisd.roborally.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LobbySelecter extends VBox {

    private static final Background BG_ACTIVE = new Background(new BackgroundFill(Color.LIGHTBLUE, null, null));
    private static final Background BG_INACTIVE = new Background(new BackgroundFill(Color.WHITE, null, null));
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);

    long ID;

    private HBox titles;
    private HBox buttons;
    private VBox lobbyBox;
    private HBox chosenLobbyView;

    private final RoboRally roborally;

    private AppController appController;

    private String course = null;


    public LobbySelecter(RoboRally roborally,AppController appController) {

        this.ID = 5;

        this.roborally = roborally;

        this.appController = appController;
        // Create a new HBox for titles with spacing between the elements
        titles = new HBox(50); // 50 pixels of spacing
        titles.setPadding(new Insets(10, 10, 10, 10)); // Padding around the HBox

        // Create labels with larger font size
        Label lobbyName = new Label("LobbyID");
        lobbyName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Set font size and weight

        Label playerCount = new Label("Players");
        playerCount.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Set font size and weight

        // Add labels to the HBox
        titles.getChildren().addAll(lobbyName, playerCount);

        // Add the HBox to the VBox
        this.getChildren().add(titles);

        // Optional: Set some padding around the VBox itself
        this.setPadding(new Insets(20, 20, 20, 20));

        // Create a new HBox for buttons with spacing between the elements
        buttons = new HBox(50); // 50 pixels of spacing
        buttons.setPadding(new Insets(10, 10, 10, 10)); // Padding around the HBox

        // Create buttons with larger font size
        Button refresh = new Button("Refresh");
        refresh.setStyle("-fx-font-size: 24px; -fx-padding: 10px 20px;"); // Set font size and padding

        Button create = new Button("Create");
        create.setStyle("-fx-font-size: 24px; -fx-padding: 10px 20px;"); // Set font size and padding

        // Add buttons to the HBox
        buttons.getChildren().addAll(refresh, create);

        // Add the HBox to the VBox
        this.getChildren().add(buttons);

        // Initialize the lobbyBox and add it to the VBox
        lobbyBox = new VBox(10); // 10 pixels of spacing between elements
        lobbyBox.setPadding(new Insets(10, 10, 10, 10)); // Padding around the VBox
        this.getChildren().add(lobbyBox);

        refresh.setOnAction(event -> updateView());

        create.setOnAction(event -> {
            try {
                handleCreation();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setCourse(String course){
        this.course = course;
    }

    public void handleCreation() throws JsonProcessingException {
        roborally.createMapSlectionView(this);

    }

    public List<Lobby> getLobbies() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpGet request
            HttpGet getRequest = new HttpGet("http://localhost:8080/api/lobby");
            getRequest.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                // Print the response status code
                System.out.println("Status code: " + response.getStatusLine().getStatusCode());

                // Parse the JSON response into a list of Lobby objects
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getEntity().getContent(), new TypeReference<List<Lobby>>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


    public void sendRequest() throws JsonProcessingException {

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        List<String> cards = new ArrayList<>();




        Lobby lobby = new Lobby();
        lobby.setId(5L);
        lobby.setPlayerCount(1);
        lobby.setMaxPlayers(result.orElse(1));
        lobby.setCourse(course);


       appController.startGame(lobby);
    }


    public void joinLobby(long id) throws InterruptedException {
        httpPut(id);
        Lobby lobby = getLobby(id);
        while (lobby.getPlayerCount() != lobby.getMaxPlayers()){
            Thread.sleep(3000);
            lobby = getLobby(lobby.getId());
        }
        appController.startGameFromJoinLobby(lobby);



    }


    public void httpPut(long id) {
        String url = "http://localhost:8080/api/lobby/" + id; // Example URL with ID 1

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpPut request with the URL
            HttpPut putRequest = new HttpPut(url);

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



    public void updateView() {
        lobbyBox.getChildren().clear(); // Clear the existing lobby views

        List<Lobby> lobbies = getLobbies();

        // Print the lobbies
        for (Lobby lobby : lobbies) {
            HBox lobbyView = new HBox(50); // 50 pixels of spacing between elements
            lobbyView.setPadding(new Insets(10, 10, 10, 10)); // Padding around the HBox

            Label lobbyIdLabel = new Label("Lobby " + lobby.getId());
            lobbyIdLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Set font size and weight

            Label playerCountLabel = new Label(lobby.getPlayerCount() + "/8");
            playerCountLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Set font size and weight

            lobbyView.getChildren().addAll(lobbyIdLabel, playerCountLabel);
            lobbyView.setBackground(BG_INACTIVE); // Set the initial background to inactive

            // Add click event handling to the HBox
            lobbyView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getClickCount() == 2) {
                    try {
                        joinLobby(lobby.getId());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (chosenLobbyView != null) {
                        chosenLobbyView.setBackground(BG_INACTIVE); // Reset the background of the previously selected HBox
                    }
                    lobbyView.setBackground(BG_ACTIVE); // Set the background of the currently selected HBox
                    chosenLobbyView = lobbyView; // Update the reference to the currently selected HBox
                }
            });

            lobbyBox.getChildren().add(lobbyView);
        }

        // Re-add the children to the VBox
        this.getChildren().clear();
        this.getChildren().addAll(titles, lobbyBox, buttons);
    }
}
