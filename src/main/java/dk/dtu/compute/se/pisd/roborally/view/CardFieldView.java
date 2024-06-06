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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The CardFieldView class provides the visual representation of a command card
 * slot in the RoboRally game. It supports interactive drag-and-drop features to
 * allow players to program their robot's actions by moving command cards between
 * their hand and their robot's program registers. It also updates its appearance
 * based on the state of the associated command card slot, such as highlighting
 * when a card is placed or removed.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class CardFieldView extends GridPane implements ViewObserver {

    // This data format helps avoiding transfers of e.g. Strings from other
    // programs which can copy/paste Strings.
    public static  DataFormat ROBO_RALLY_CARD_UPGRADE = new DataFormat("/games/roborally/upgrade");;


    final public static int CARDFIELD_WIDTH = 60;
    final public static int CARDFIELD_HEIGHT = 70;

    final public static Border BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));

    final public static Background BG_DEFAULT = new Background(new BackgroundFill(Color.WHITE, null, null));
    final public static Background BG_DRAG = new Background(new BackgroundFill(Color.GRAY, null, null));
    final public static Background BG_DROP = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));

    final public static Background BG_ACTIVE = new Background(new BackgroundFill(Color.YELLOW, null, null));
    final public static Background BG_DONE = new Background(new BackgroundFill(Color.GREENYELLOW,  null, null));

    private CommandCardField field;


    private ImageView imageView;

    private Label label;

    private GameController gameController;



    /**
     * Constructor for creating a view for a command card field.
     * @param gameController The game controller managing game logic and state.
     * @param field The command card field model associated with this view.
     */
    public CardFieldView(@NotNull GameController gameController, @NotNull CommandCardField field) {







        this.gameController = gameController;
        this.field = field;

        if(field.getCard() != null) {
            imageView = field.getCard().getCardImage();
            imageView.setFitWidth(45);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);
            this.add(imageView, 0, 0);
        }
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(5, 5, 5, 5));

        this.setBorder(BORDER);
        this.setBackground(BG_DEFAULT);

        this.setPrefWidth(CARDFIELD_WIDTH);
        this.setMinWidth(CARDFIELD_WIDTH);
        this.setMaxWidth(CARDFIELD_WIDTH);
        this.setPrefHeight(CARDFIELD_HEIGHT);
        this.setMinHeight(CARDFIELD_HEIGHT);
        this.setMaxHeight(CARDFIELD_HEIGHT);

        label = new Label("This is a slightly longer text");
        label.setWrapText(true);
        label.setMouseTransparent(true);


        this.setOnDragDetected(new OnDragDetectedHandler());
        this.setOnDragOver(new OnDragOverHandler());
        this.setOnDragEntered(new OnDragEnteredHandler());
        this.setOnDragExited(new OnDragExitedHandler());
        this.setOnDragDropped(new OnDragDroppedHandler());
        this.setOnDragDone(new OnDragDoneHandler());


        field.attach(this);
        update(field);
    }






    private String cardFieldRepresentation(CommandCardField cardField) {
        if (cardField.player != null) {

            for (int i = 0; i < Player.NO_REGISTERS; i++) {
                CommandCardField other = cardField.player.getProgramField(i);
                if (other == cardField) {
                    return "P," + i;
                }
            }

            for (int i = 0; i < Player.NO_CARDS; i++) {
                CommandCardField other = cardField.player.getCardField(i);
                if (other == cardField) {
                    return "C," + i;
                }
            }

                for (int i = 0; i < gameController.board.getShopFields().size(); i++) {
                    CommandCardField other = gameController.board.getShopFields().get(i);
                    if (other == cardField) {
                        return "S," + i;
                    }
                }
            for (int i = 0; i < Player.NO_UPGRADES ; i++) {
                CommandCardField other = cardField.player.getUpgradeField(i);;
                if (other == cardField) {
                    return "U," + i;
                }
            }
            for (int i = 0; i < gameController.board.getShopFields().size(); i++) {
                CommandCardField other = cardField.player.getUpgradeInv(i);;
                if (other == cardField) {
                    return "I," + i;
                }
            }
        }
        return null;

    }

    private CommandCardField cardFieldFromRepresentation(String rep) {
        if (rep != null && field.player != null) {
            String[] strings = rep.split(",");
            if (strings.length == 2) {
                int i = Integer.parseInt(strings[1]);
                if ("P".equals(strings[0])) {
                    if (i < Player.NO_REGISTERS) {
                        return field.player.getProgramField(i);
                    }
                } else if ("C".equals(strings[0])) {
                    if (i < Player.NO_CARDS + 2) {
                        return field.player.getCardField(i);
                    }
                } else if ("S".equals(strings[0])) {
                    if (i < gameController.board.getShopFields().size()) {
                        return gameController.board.getShopFields().get(i);
                    } else if ("U".equals(strings[0])) {
                        if (i < Player.NO_UPGRADES) {
                            return field.player.getUpgradeField(i);
                        }
                    } else if ("I".equals(strings[0])) {
                        if (i < Player.NO_UPGRADE_INV) {
                            return field.player.getUpgradeInv(i);
                        }
                    }
                }

            }

        }
        return null;
    }

    /**
     * Updates the view based on changes to the observed command card field.
     * @param subject The subject (command card field) being observed for changes.
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == field && subject != null) {
            CommandCard card = field.getCard();
            if (card != null && field.isVisible()) {
                // If the field has a card, set the ImageView to display the card image
                imageView = card.getCardImage();
                imageView.setFitWidth(45);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                this.getChildren().clear(); // Clear any existing content
                this.add(imageView, 0, 0);
                label.setText(card.getName());
            } else {
                // If the field doesn't have a card, clear the ImageView and label
                this.getChildren().clear();
            }
        }
    }

    private class OnDragDetectedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Object t = event.getTarget();
            CardFieldView source = null;
            CommandCardField cardField = null;
            if (t instanceof CardFieldView) {
                source = (CardFieldView) t;
                cardField = source.field;
            } else if (t instanceof ImageView) {
                source = (CardFieldView)((ImageView) t).getParent();
                cardField = source.field;
            }
                if (cardField != null &&
                        cardField.getCard() != null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
                    Image image = source.snapshot(null, null);
                    db.setDragView(image);

                    ClipboardContent content = new ClipboardContent();
                    content.put(ROBO_RALLY_CARD_UPGRADE, cardFieldRepresentation(cardField));

                    db.setContent(content);
                    source.setBackground(BG_DRAG);
                }

            event.consume();
            }

        }



    private class OnDragOverHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {

            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;

                // checking the phase becasue the turn nature differs from phase to phase
                if(cardField.player.board.getPhase() == Phase.INITIALISATION) {
                    if (cardField != null &&
                            (cardField.getCard() == null || event.getGestureSource() == target) &&
                            cardField.player != null &&
                            cardField.player.board != null && target.field.player == gameController.board.getCurrentTurn()) {
                        CardFieldView source = (CardFieldView) event.getGestureSource();
                        CommandCardField field = source.field;
                        if (event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE)  && field.getType().equals(cardField.getType())) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                    }
                } else {
                    if (cardField != null &&
                            (cardField.getCard() == null || event.getGestureSource() == target) &&
                            cardField.player != null &&
                            cardField.player.board != null) {
                        CardFieldView source = (CardFieldView) event.getGestureSource();
                        CommandCardField field = source.field;
                        if (event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE) && field.getType().equals(cardField.getType())) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                    }

                }
            }
            event.consume();
        }

    }

    private class OnDragEnteredHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                // checking the phase becasue the turn nature differs from phase to phase
                if(cardField.player.board.getPhase() == Phase.INITIALISATION) {



                    if (cardField != null &&
                            cardField.getCard() == null &&
                            cardField.player != null &&
                            cardField.player.board != null) {
                        CardFieldView source = (CardFieldView) event.getGestureSource();

                        if (event.getGestureSource() != target &&
                                event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE)   && target.field.player == gameController.board.getCurrentTurn()) {
                            target.setBackground(BG_DROP);
                        }
                    }


                } else{
                    if (cardField != null &&
                            cardField.getCard() == null &&
                            cardField.player != null &&
                            cardField.player.board != null) {
                        CardFieldView source = (CardFieldView) event.getGestureSource();
                        if (event.getGestureSource() != target &&
                                event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE)) {
                            target.setBackground(BG_DROP);
                        }
                    }

                }
            }
            event.consume();
        }

    }

    private class OnDragExitedHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                if (cardField != null &&
                        cardField.getCard() == null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getGestureSource() != target &&
                            event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE)) {
                        target.setBackground(BG_DEFAULT);
                    }
                }
            }
            event.consume();
        }

    }

    private class OnDragDroppedHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;

                Dragboard db = event.getDragboard();
                boolean success = false;
                if(cardField.player.board.getPhase() == Phase.INITIALISATION) {


                    if (cardField != null &&
                            cardField.getCard() == null &&
                            cardField.player != null &&
                            cardField.player.board != null && target.field.player == gameController.board.getCurrentTurn()) {
                        if (event.getGestureSource() != target &&
                                db.hasContent(ROBO_RALLY_CARD_UPGRADE)) {
                            Object object = db.getContent(ROBO_RALLY_CARD_UPGRADE);
                            if (object instanceof String) {
                                CommandCardField source = cardFieldFromRepresentation((String) object);
                                if (source != null && gameController.moveCards(source, cardField)) {
                                    // CommandCard card = source.getCard();
                                    // if (card != null) {
                                    // if (gameController.moveCards(source, cardField)) {
                                    // cardField.setCard(card);
                                    success = true;
                                    // }
                                }
                            }
                        }
                    }



                } else {
                    if (cardField != null &&
                            cardField.getCard() == null &&
                            cardField.player != null &&
                            cardField.player.board != null) {
                        if (event.getGestureSource() != target &&
                                db.hasContent(ROBO_RALLY_CARD_UPGRADE)) {
                            Object object = db.getContent(ROBO_RALLY_CARD_UPGRADE);
                            if (object instanceof String) {
                                CommandCardField source = cardFieldFromRepresentation((String) object);
                                if (source != null && gameController.moveCards(source, cardField)) {
                                    // CommandCard card = source.getCard();
                                    // if (card != null) {
                                    // if (gameController.moveCards(source, cardField)) {
                                    // cardField.setCard(card);
                                    success = true;
                                    // }
                                }
                            }
                        }
                    }


                }
                event.setDropCompleted(success);
                target.setBackground(BG_DEFAULT);
            }
            event.consume();
        }

    }

    private class OnDragDoneHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView source = (CardFieldView) t;
                source.setBackground(BG_DEFAULT);
            }
            event.consume();
        }

    }

}




