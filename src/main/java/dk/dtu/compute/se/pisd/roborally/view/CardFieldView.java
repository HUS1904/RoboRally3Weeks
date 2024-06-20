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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

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

    final public static Background BG_DEFAULT = new Background(new BackgroundFill(Color.DARKGRAY, null, null));
    final public static Background BG_DRAG = new Background(new BackgroundFill(Color.GRAY, null, null));
    final public static Background BG_DROP = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));

    final public static Background BG_ACTIVE = new Background(new BackgroundFill(Color.STEELBLUE, null, null));
    final public static Background BG_DONE = new Background(new BackgroundFill(Color.BLACK,  null, null));

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

        field.getCard().ifPresent(card -> {
            imageView = card.getCardImage();
            imageView.setFitWidth(45);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);
            this.add(imageView, 0, 0);
        });

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

    private Lobby getLobby(long id) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create a HttpGet request
            HttpGet getRequest = new HttpGet("http://localhost:8080/api/lobby/" + id);
            getRequest.setHeader("Content-Type", "application/json");

            // Execute the request
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                // Print the response status code
                System.out.println("Status code: " + response.getStatusLine().getStatusCode());

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

    /**
     * Updates the view based on changes to the observed command card field.
     * @param subject The subject (command card field) being observed for changes.
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == field && subject != null) {
            field.getCard().ifPresentOrElse(card -> {
                if (field.isVisible()) {
                    // If the field has a card, set the ImageView to display the card image
                    imageView = card.getCardImage();
                    imageView.setFitWidth(45);
                    imageView.setFitHeight(60);
                    imageView.setPreserveRatio(true);
                    this.getChildren().clear(); // Clear any existing content
                    this.add(imageView, 0, 0);
                    label.setText(card.getName());
                } else this.getChildren().clear();
            }, this.getChildren()::clear
            );
        }
    }

    private class OnDragDetectedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            CardFieldView source = getSource(event.getTarget());
            if (source == null) return;

            CommandCardField cardField = source.field;
            if (isValidCardField(cardField)) {
                initiateDragAndDrop(source, cardField);
            }
            event.consume();
        }

        private CardFieldView getSource(Object target) {
            if (target instanceof CardFieldView) {
                return (CardFieldView) target;
            } else if (target instanceof ImageView) {
                return (CardFieldView) ((ImageView) target).getParent();
            }
            return null;
        }

        private boolean isValidCardField(CommandCardField cardField) {
            return cardField != null && cardField.getCard().isPresent() &&
                    cardField.player != null && cardField.player.board != null;
        }

        private void initiateDragAndDrop(CardFieldView source, CommandCardField cardField) {
            Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
            Image image = source.snapshot(null, null);
            db.setDragView(image);

            ClipboardContent content = new ClipboardContent();
            content.put(ROBO_RALLY_CARD_UPGRADE, cardFieldRepresentation(cardField));

            db.setContent(content);
            source.setBackground(BG_DRAG);
        }
    }

    private class OnDragOverHandler implements EventHandler<DragEvent> {
        private Timeline timeline;

        public OnDragOverHandler() {
            // Create a timeline that runs every 3 seconds
            timeline = new Timeline(new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // Perform your action here
                   gameController.setLobby(getLobby(gameController.getLobby().getId()));
                   Lobby lobby = gameController.getLobby();
                   for(String string: lobby.getCardField()){
                       System.out.println(string);
                   }
                    gameController.board.setCurrentTurn(gameController.board.findCorrespondingPlayer(lobby.getCurrentPlayer()));
                    gameController.board.readjustShop(lobby.getCardField());
                    timeline.stop();
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
        }

        @Override
        public void handle(DragEvent event) {
            if (!(event.getTarget() instanceof CardFieldView target)) return;

            CommandCardField cardField = target.field;
            if (isValidForDragOver(cardField, event)) {
                event.acceptTransferModes(TransferMode.MOVE);
                // Stop the timeline if it's running
                if (timeline.getStatus() == Timeline.Status.RUNNING) {
                    timeline.stop();
                }
            } else {
                // Start the timeline if it's not already running
                if (timeline.getStatus() != Timeline.Status.RUNNING) {
                    timeline.play();
                }
            }
            event.consume();
        }

        private boolean isValidForDragOver(CommandCardField cardField, DragEvent event) {
            if (cardField.player.board.getPhase() == Phase.INITIALISATION) {
                return isValidInitializationPhase(cardField, event);
            } else {
                return isValidGeneralPhase(cardField, event);
            }
        }

        private boolean isValidInitializationPhase(CommandCardField cardField, DragEvent event) {
            return (cardField.getCard().isEmpty() || event.getGestureSource() == cardField)
                    && cardField.player == gameController.board.getCurrentTurn()
                    && event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE)
                    && ((CardFieldView) event.getGestureSource()).field.getType().equals(cardField.getType());
        }

        private boolean isValidGeneralPhase(CommandCardField cardField, DragEvent event) {
            return (cardField.getCard().isEmpty() || event.getGestureSource() == cardField)
                    && event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE)
                    && ((CardFieldView) event.getGestureSource()).field.getType().equals(cardField.getType());
        }
    }

    private class OnDragEnteredHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            if (!(event.getTarget() instanceof CardFieldView target)) return;

            CommandCardField cardField = target.field;
            if (shouldSetBackgroundDrop(cardField, event)) {
                target.setBackground(BG_DROP);
            }
            event.consume();
        }

        private boolean shouldSetBackgroundDrop(CommandCardField cardField, DragEvent event) {
            if (cardField.player.board.getPhase() == Phase.INITIALISATION) {
                return cardField.getCard().isEmpty()
                        && event.getGestureSource() != cardField
                        && event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE)
                        && cardField.player == gameController.board.getCurrentTurn();
            } else {
                return cardField.getCard().isEmpty()
                        && event.getGestureSource() != cardField
                        && event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE);
            }
        }
    }

    private class OnDragExitedHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            if (!(event.getTarget() instanceof CardFieldView target)) return;

            CommandCardField cardField = target.field;
            if (shouldResetBackground(cardField, event)) {
                target.setBackground(BG_DEFAULT);
            }
            event.consume();
        }

        private boolean shouldResetBackground(CommandCardField cardField, DragEvent event) {
            return cardField != null && cardField.getCard().isEmpty()
                    && cardField.player != null && cardField.player.board != null
                    && event.getGestureSource() != cardField
                    && event.getDragboard().hasContent(ROBO_RALLY_CARD_UPGRADE);
        }
    }

    private class OnDragDroppedHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            if (!(event.getTarget() instanceof CardFieldView target)) return;

            CommandCardField cardField = target.field;
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (isDropAllowed(cardField, db)) {
                success = handleDrop(cardField, db);
            }

            event.setDropCompleted(success);
            target.setBackground(BG_DEFAULT);
            event.consume();
        }

        private boolean isDropAllowed(CommandCardField cardField, Dragboard db) {
            if (cardField.player.board.getPhase() == Phase.INITIALISATION) {
                return cardField.getCard().isEmpty()
                        && cardField.player == gameController.board.getCurrentTurn()
                        && db.hasContent(ROBO_RALLY_CARD_UPGRADE);
            } else {
                return cardField.getCard().isEmpty()
                        && db.hasContent(ROBO_RALLY_CARD_UPGRADE);
            }
        }

        private boolean handleDrop(CommandCardField cardField, Dragboard db) {
            Object object = db.getContent(ROBO_RALLY_CARD_UPGRADE);
            if (object instanceof String representation) {
                CommandCardField source = cardFieldFromRepresentation(representation);
                if (source != null && gameController.moveCards(source, cardField)) {
                    cardField.player.incrementEnergy(2);
                    return true;
                }
            }
            return false;
        }
    }

    private class OnDragDoneHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            if (event.getTarget() instanceof CardFieldView source) {
                source.setBackground(BG_DEFAULT);
            }
            event.consume();
        }
    }


}




