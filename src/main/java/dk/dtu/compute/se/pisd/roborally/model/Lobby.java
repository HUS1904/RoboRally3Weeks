package dk.dtu.compute.se.pisd.roborally.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lobby {
    private Long id;
    private int playerCount;
    private int maxPlayers;
    private String course;
    private List<String> cards;
    private List<String> cardField; // Added field

    private int playerIndex;

    private String currentPlayer;


}
