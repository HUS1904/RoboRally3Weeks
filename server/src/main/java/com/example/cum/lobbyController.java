package com.example.cum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lobby")
public class lobbyController {

    @Autowired
    private LobbyService lobbyService;


    @GetMapping
    public List<Lobby> getAllUsers() {
        return lobbyService.getAllLobbies();
    }

    @GetMapping("/{id}")
    public Lobby getUserById(@PathVariable Long id) {
        return lobbyService.getlobbyById(id);
    }

    @PostMapping
    public Lobby createLobby(@RequestBody Lobby lobby) {

        return lobbyService.saveLobby(lobby);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
   lobbyService.deletelobby(id);
    }

    @PutMapping("/{id}")
    public void updateLobby(@PathVariable Long id) {
        // Fetch the lobby
        Lobby lobby = lobbyService.getlobbyById(id);

        // Perform updates
        if (lobby != null) {
            lobby.setPlayerCount(lobby.getPlayerCount() + 1);
            // Save the updated lobby
            lobbyService.saveLobby(lobby);
        } else {

        }
    }
    @PutMapping("/shop/{id}")
    public void updateShop(@PathVariable Long id, @RequestBody Lobby updatedLobby) {
        // Fetch the lobby
        Lobby existingLobby = lobbyService.getlobbyById(id);

        // Perform updates
        if (existingLobby != null) {
            existingLobby.setCurrentPlayer(updatedLobby.getCurrentPlayer());
            existingLobby.setCardField(updatedLobby.getCardField());
            existingLobby.setCards(updatedLobby.getCards());
            existingLobby.setCourse(updatedLobby.getCourse());
            existingLobby.setPlayerCount(updatedLobby.getPlayerCount());
            existingLobby.setPlayerIndex(updatedLobby.getPlayerIndex());
            existingLobby.setMaxPlayers(updatedLobby.getMaxPlayers());
            existingLobby.setPlayersPosition(updatedLobby.getPlayersPosition());
            existingLobby.setPlayersHeadings(updatedLobby.getPlayersHeadings());

            // Save the updated lobby
            lobbyService.saveLobby(existingLobby);
        }


    }
}



