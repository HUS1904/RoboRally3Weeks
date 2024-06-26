package com.example.cum;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "lobbies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Lobby {

    @Id
    private Long id;

    private String currentPlayer;

    private int playerCount;
    private int playerIndex;
    private int maxPlayers;

    private String course;

    @ElementCollection
    @CollectionTable(name = "lobby_cards", joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "card")
    private List<String> cards;

    @ElementCollection
    @CollectionTable(name = "lobby_cardField", joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "cardField")
    private List<String> cardField;


    @ElementCollection
    @CollectionTable(name = "lobby_player_positions", joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "playerPositions")
    private List<Integer> playersPosition ;

    @ElementCollection
    @CollectionTable(name = "lobby_player_heading", joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "playersheadings")
    private List<String> playersHeadings ;

    //@OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, orphanRemoval = true)
   // private Set<LobbyPlayer> lobbyPlayers = new HashSet<>();

    // Getters and Setters
}