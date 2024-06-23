package dk.dtu.compute.se.pisd.roborally.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceDialog;
import javafx.util.Duration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LobbyUtil {
    static Timeline timeline;
    public static List<Lobby> getLobbies() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpGet request
            HttpGet getRequest = new HttpGet("http://localhost:8080/api/lobby");
            getRequest.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                // Print the response status code


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

    public static Lobby getLobby(long id) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpGet request
            HttpGet getRequest = new HttpGet("http://localhost:8080/api/lobby/" + id);
            getRequest.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                // Print the response status code


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


    public static void httpPost(Lobby lobby) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(lobby);
        // Create an HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpPost request
            HttpPost postRequest = new HttpPost("http://localhost:8080/api/lobby");
            postRequest.setEntity(new StringEntity(requestBody));
            postRequest.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                // Print the response status code and body

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }









    public static void httpPut(long id) {
        String url = "http://localhost:8080/api/lobby/" + id; // Example URL with ID 1

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpPut request with the URL
            HttpPut putRequest = new HttpPut(url);

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(putRequest)) {
                // Print the response status code



            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void httpPutLobby(long id,Lobby lobby) throws IOException {
        String url = "http://localhost:8080/api/lobby/shop/" + id; // Example URL with ID 1



            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // Create a HttpPut request with the URL
                HttpPut putRequest = new HttpPut(url);

                // Convert the Lobby object to JSON
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(lobby);

                // Set the JSON as the entity of the PUT request
                StringEntity entity = new StringEntity(json);
                putRequest.setEntity(entity);
                putRequest.setHeader("Content-type", "application/json");

                // Execute the request
                try (CloseableHttpResponse response = httpClient.execute(putRequest)) {
                    // Print the response status code

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static boolean joinLobby(long id) {
            Lobby lobby = getLobby(id);

            if (lobby != null && lobby.addPlayer()) {
                try {
                    httpPutLobby(id, lobby);
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        }

}
