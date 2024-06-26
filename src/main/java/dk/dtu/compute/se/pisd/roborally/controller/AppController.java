





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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.model.*;


import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.google.gson.Gson;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The AppController class manages the application-level operations for the RoboRally game,
 * including starting new games, saving and loading games, and exiting the application.
 * It interacts with both the model and view components of the MVC architecture to
 * facilitate the game's overall functionality and flow.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class AppController implements Observer {

    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;
    public String course = null;
    private boolean isLightMode = true;

    public Lobby lobby  = new Lobby();

    /**
     * Initializes a new AppController instance with the specified RoboRally game instance.
     *
     * @param roboRally the main game instance that this controller will manage
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Starts a new game by allowing the user to select the number of players and
     * initializing the game board and players accordingly.
     */
    public void newGame() throws JsonProcessingException {

        roboRally.createLobbySelectionView();
    }

    public void startGame(Lobby lobby) {


        this.lobby = lobby;
        List<Integer> playerPositions = new ArrayList<>();
        String directoryPath = "src/main/resources/courses/" + lobby.getCourse() + ".json";

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        File courseFile = new File(directoryPath);

        try {
            String jsonContent = new String(Files.readAllBytes(courseFile.toPath()));
            Course course = gson.fromJson(jsonContent, Course.class);

            System.out.print(lobby.getId());

            gameController = new GameController(new Board(course, "e"),lobby, this);

            Board board = gameController.board;

            Deck shop = new Deck("upgrade", gameController);
            shop.generateUpgradeDeck(gameController);
            board.setShop(shop);
            for (int i = 0; i < lobby.getMaxPlayers(); i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1), gameController);
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));

                playerPositions.add(player.getSpace().y);
                playerPositions.add(player.getSpace().x);
            }
            lobby.setPlayersPosition(playerPositions);
            Space antenna = board.getSpacesList().stream().filter(s -> s.getType() == ActionField.PRIORITY_ANTENNA).findAny().orElseThrow(NoSuchElementException::new);
            board.determineTurn(antenna.x, antenna.y);
            board.setCurrentPlayer(board.getPlayer(0).orElseThrow(NoSuchElementException::new));
            lobby.setCards(board.getShop().deckIntoString(board.getShop()));
            lobby.setCurrentPlayer(gameController.board.getPlayer(0).orElseThrow(NoSuchElementException::new).getName());

            LobbyUtil.httpPost(lobby);

            gameController.board.setCurrentPlayer( gameController.board.findCorrespondingPlayer("Player " + lobby.getPlayerCount()));

            gameController.board.setPhase(Phase.INITIALISATION);

            roboRally.createBoardView(gameController);

        } catch (IOException ignored) {

        }


}

        public void startGameFromJoinLobby (Lobby lobby) {

            this.lobby = lobby;

            List<Integer> playerPositions = new ArrayList<>();

            String directoryPath = "src/main/resources/courses/" + lobby.getCourse() + ".json";

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            File courseFile = new File(directoryPath);

            try {
                String jsonContent = null;
                try {
                    jsonContent = new String(Files.readAllBytes(courseFile.toPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Course course = gson.fromJson(jsonContent, Course.class);


                gameController = new GameController(new Board(course, "e"),lobby, this);

                Board board = gameController.board;

                Deck shop = new Deck("upgrade", gameController);
                board.setShop(shop.turnStringToDeck(lobby.getCards()));

                for (int i = 0; i < lobby.getMaxPlayers(); i++) {
                    Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1), gameController);
                    board.addPlayer(player);
                    player.setSpace(board.getSpace(i % board.width, i));
                }
                board.determineTurn(2, 2);
                gameController.board.setCurrentPlayer( gameController.board.findCorrespondingPlayer("Player " + lobby.getPlayerCount()));
                gameController.getLobby().setCards(board.getShop().deckIntoString(board.getShop()));


                gameController.board.setPhase(Phase.INITIALISATION);

                roboRally.createBoardView(gameController);

            } catch(IllegalArgumentException e){

            }
        }

    /**
     * Saves the current game state. This method is intended for future implementation.
     */
    public void saveGame() {
        // making the board into a Json String
        Board board = gameController.board;
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String gsonString = gson.toJson(board);

        // making a directory path by combining the static driectoryoath and the filename provided by the user through the dialogue box
        String directoryPath = "src/main/resources/saves/";


        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save game");
        dialog.setHeaderText("Enter the name of the file");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();

        String fileName = result.orElse("default") + ".json";

        // Create directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Directory created: " + directoryPath);
        }
        Path filePath = Path.of(directoryPath, fileName);

        //

        // Create an empty JSON file
        try {
            Files.createFile(filePath);
            System.out.println("Empty JSON file created: " + filePath);
        } catch (IOException e) {
            // Handle file creation exception
            e.printStackTrace();
        }


        // Write the Json String into the Json file that was created
        try (FileWriter fileWriter = new FileWriter(directoryPath + fileName)) {
            // Write JSON content to the file
            fileWriter.write(gsonString);
            System.out.println("JSON content has been written to the file.");
        } catch (IOException e) {
            // Handle file I/O exception
            e.printStackTrace();
        }

    }

    /**
     * Loads a previously saved game state. Currently, this method starts a new game
     * as a placeholder for future functionality.
     */
    public void loadGame() throws JsonProcessingException {
        JFileChooser fileChooser = new JFileChooser();
        File selectedFile = null;

        int result = fileChooser.showOpenDialog(null); // You can pass a parent component here if needed

        // Check if a file was selected
        if (result == JFileChooser.APPROVE_OPTION) {

            selectedFile = fileChooser.getSelectedFile();
        }
        try {
            // Read the content of the JSON file
            assert selectedFile != null;
            String jsonContent = new String(Files.readAllBytes(selectedFile.toPath()));

            // Create a Gson object
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();


            Board board = gson.fromJson(jsonContent,Board.class);

            Course course = board.getCourse();





            Board newBoard = new Board(course,"game1");
            gameController = new GameController(newBoard);


            int no = board.getPlayerAmount();
            for (int i = 0; i < no; i++) {
                Player player = new Player(newBoard, PLAYER_COLORS.get(i), "Player " + (i + 1),gameController);
                newBoard.addPlayer(player);
                player.setSpace(newBoard.getSpace(i % board.width, i));
                gameController.moveTo(player,board.findCorrespondingPlayer(player.getName()).getSpace().x,board.findCorrespondingPlayer(player.getName()).getSpace().y);
                player.setHeading(board.getPlayer(i).orElseThrow(NoSuchElementException::new).getHeading());
            }

            newBoard.setCurrentPlayer(board.getPlayer(0).orElseThrow(NoSuchElementException::new));

            gameController.reInitialize(board);
            roboRally.createBoardView(gameController);






        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }


        if (gameController == null) {
            newGame();
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {
            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Exits the RoboRally application, with a confirmation dialog to the user.
     * If a game is currently running, offers the user the option to save before exiting.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * Checks if a game is currently running.
     *
     * @return true if a game is currently running, false otherwise
     */
    public boolean isGameRunning() {
        return gameController != null;
    }

    public void changeMode() {
        System.out.println("Attempting to change mode.");
        isLightMode = !isLightMode;

        try {
            if (isLightMode) {
                System.out.println("Setting light mode.");
                setLightMode();
            } else {
                System.out.println("Setting dark mode.");
                setDarkMode();
            }
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setDarkMode() {
        try {
            URL url = getClass().getResource("/stylesheets/DarkMode.css");
            if (url == null) {
                throw new Exception("Resource not found: DarkMode.css");
            }
            final Image image = new Image(getClass().getResourceAsStream("/light.png"));
            Platform.runLater(() -> {
                roboRally.getPrimaryScene().getStylesheets().clear();
                roboRally.getPrimaryScene().getStylesheets().add(url.toExternalForm());
                roboRally.setImgMode(image);
            });
        } catch (Exception e) {
            System.out.println("Failed to set dark mode: " + e.getMessage());
        }
    }

    private void setLightMode() {
        try {
            URL url = getClass().getResource("/stylesheets/LightMode.css");
            if (url == null) {
                throw new Exception("Resource not found: LightMode.css");
            }
            final Image image = new Image(getClass().getResourceAsStream("/dark.png"));
            Platform.runLater(() -> {
                roboRally.getPrimaryScene().getStylesheets().clear();
                roboRally.getPrimaryScene().getStylesheets().add(url.toExternalForm());
                roboRally.setImgMode(image);
            });
        } catch (Exception e) {
            System.out.println("Failed to set light mode: " + e.getMessage());
        }
    }

    public void announceWinner(Player winner) {
        roboRally.displayWinner(winner);  // Delegate to RoboRally to update UI
    }

    public boolean isLightMode() {
        return isLightMode;
    }


    /**
     * Responds to updates from subjects this observer is observing. Currently does nothing.
     *
     * @param subject the subject that has been updated
     */
    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
