package com.example.cum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LobbyService{

    @Autowired
    private lobbyRepo lobby;

    public List<Lobby> getAllLobbies() {
        return lobby.findAll();
    }

    public Lobby getlobbyById(Long id) {
        return lobby.findById(id).orElse(null);
    }

    public Lobby saveLobby(Lobby nib) {
        return lobby.save(nib);
    }

    public void deletelobby(Long id) {
        lobby.deleteById(id);
    }
}
