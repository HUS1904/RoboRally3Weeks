package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    private List<CommandCard> deck = new ArrayList<>();
    private GameController controller;


    public Deck(String type, GameController gameController) {
        this.controller = gameController;

        for (int i = 0; i < 52; i++) {

            deck.add(controller.generateRandomCommandCard());
        }
    }


    public CommandCard deal() {
        return deck.remove(deck.size() - 1);
    }
}

