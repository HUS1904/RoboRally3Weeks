package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Deck {

    private List<CommandCard> deck = new ArrayList<>();
    private List<CommandCard> discardPile = new ArrayList<>();
    private List<CommandCard> upgradeDeck = new ArrayList<>();

   private GameController gameController;


    public Deck(String type,GameController gameController) {
        this.gameController = gameController;

        if (!Objects.equals(type, "upgrade")) {
            for (int i = 0; i < 52; i++) {

                deck.add(gameController.generateRandomCommandCard());
            }
        } else {
            for (int j = 0; j < 33; j++) {
                deck.add(gameController.generateUpgradeCard());
            }
        }
    }

    //private Deck upgradeDeck = new Deck("upgrade", gameController);

    public void generateUpgradeDeck(GameController gameController) {
        this.gameController = gameController;

        for (int i = 0; i < 33; i++) {
            upgradeDeck.add(gameController.generateUpgradeCard());
        }
        //return upgradeDeck;
    }

    public void shuffleDeck(){
        for(int i = 0; i < discardPile.size();i++){
            int random = (int) (Math.random() * discardPile.size());
            deck.add(discardPile.remove(random));

        }
    }

    public void sendToDiscardPile(CommandCard card){
        discardPile.add(card);
    }

    public void addToDeck(CommandCard card){

        int random = (int) (Math.random() * deck.size());
        deck.add(random,card);

    }


    public CommandCard deal() {

        if(deck.size() != 8){
            shuffleDeck();
        }
        CommandCard card = deck.remove(deck.size() - 1);
        return card;
    }
}

