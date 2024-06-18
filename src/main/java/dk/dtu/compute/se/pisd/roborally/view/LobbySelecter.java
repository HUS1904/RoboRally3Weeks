package dk.dtu.compute.se.pisd.roborally.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.List;

public class LobbySelecter extends VBox {

    HBox titles;
    HBox buttons;
    VBox lobbyBox;

    public LobbySelecter() {
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
                HandleCreation();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void HandleCreation() throws JsonProcessingException {
       Lobby lobby = new Lobby();
        lobby.setPlayerCount(1);

        // Convert User object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(lobby);
        System.out.println(requestBody);
        // Create an HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpPost request
            HttpPost postRequest = new HttpPost("http://localhost:8080/api/lobby");
            postRequest.setEntity(new StringEntity(requestBody));
            postRequest.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Print the response status code and body
                System.out.println("Status code: " + response.getStatusLine().getStatusCode());
                System.out.println("Response body: " + response.getEntity().getContent().toString());
                updateView();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateView() {

        this.getChildren().clear();
        lobbyBox.getChildren().clear(); // Clear the existing lobby views

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
                List<Lobby> lobbies = objectMapper.readValue(
                        response.getEntity().getContent(),
                        new TypeReference<List<Lobby>>() {}
                );

                // Print the lobbies
                for (Lobby lobby : lobbies) {
                    HBox lobbyView = new HBox(50); // 50 pixels of spacing between elements
                    lobbyView.setPadding(new Insets(10, 10, 10, 10)); // Padding around the HBox

                    Label lobbyIdLabel = new Label("Lobby " + lobby.getId());
                    lobbyIdLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Set font size and weight

                    Label playerCountLabel = new Label(lobby.getPlayerCount() + "/8");
                    playerCountLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Set font size and weight

                    lobbyView.getChildren().addAll(lobbyIdLabel, playerCountLabel);
                    lobbyBox.getChildren().add(lobbyView);
                }
                this.getChildren().addAll(titles,lobbyBox,buttons);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
