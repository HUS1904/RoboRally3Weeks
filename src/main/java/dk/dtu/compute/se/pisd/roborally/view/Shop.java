package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Deck;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Shop extends VBox {

    GameController gameController;
    Image image;
    Deck deck;

    public Shop(GameController gameController){

        this.deck = new Deck("upgrade",gameController);
        this.gameController = gameController;
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Facedown.png")));


        /// button to change phase
        Button phaseChanger = new Button("Start programming phase");
        phaseChanger.setOnAction(e -> handleClick());

        phaseChanger.setAlignment(Pos.CENTER);


        // the cardfield view

        GridPane upgradshop = new GridPane();
        upgradshop.setAlignment(Pos.CENTER);
        upgradshop.setHgap(10);


        Pane deck = new Pane();
        ImageView deckImage = new ImageView(image);
        deckImage.setFitHeight(70);
        deckImage.setFitWidth(60);
        deck.getChildren().add(deckImage);


        for(int i = 0; i< gameController.board.getPlayerAmount();i++) {
            CommandCard commandCard = this.deck.deal();
            CommandCardField cardfield = new CommandCardField(gameController.board.getCurrentPlayer(),"upgrade");
            gameController.board.getShopFields().add(cardfield);
            cardfield.setCard(commandCard);
            upgradshop.add(new CardFieldView(gameController,cardfield),i,0);

            }

        upgradshop.add(deck,gameController.board.getPlayerAmount(),0);





        this.getChildren().addAll(phaseChanger,upgradshop);


    }

    public void handleClick(){

      if (gameController.board.getCurrentPlayer() == gameController.board.getCurrentTurn()) {
            gameController.board.getCurrentTurn().phase = Phase.PROGRAMMING;
            gameController.board.moveCurrentTurn();
        }

        if(gameController.board.getPlayers().indexOf(gameController.board.getCurrentPlayer()) == gameController.board.getPlayerAmount() -1 &&
                gameController.board.getCurrentTurn().phase == Phase.PROGRAMMING) {

            gameController.startProgrammingPhase();
        }

        }


    }


