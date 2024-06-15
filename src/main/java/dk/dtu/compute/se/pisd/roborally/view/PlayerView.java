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
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the graphical user interface for a player in the RoboRally game,
 * including the programming board and command cards. It allows the player to
 * view and interact with their program and command cards during the game.
 * It observes changes to the player's state and updates the display accordingly.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class PlayerView extends Tab implements ViewObserver {

    private Player player;

    private VBox top;

    private Label programLabel;
    private GridPane programPane;
    private Label cardsLabel;
    private GridPane cardsPane;
    private GridPane upgradesPane;
    private GridPane upgradesInvPane;

    private CardFieldView[] programCardViews;
    private CardFieldView[] cardViews;
    private CardFieldView[] upgradesViews;
    private CardFieldView[] upgradesInvViews;

    private VBox buttonPanel;

    private Button finishButton;
    private Button executeButton;
    private Button stepButton;

    private VBox playerInteractionPanel;

    private GridPane energyCubes;

    private GameController gameController;

    /**
     * Constructs a PlayerView for the specified player and game controller.
     * @param gameController The game controller handling game logic.
     * @param player The player this view represents.
     */
    public PlayerView(@NotNull GameController gameController, @NotNull Player player) {
        super(player.getName());
        this.setStyle("-fx-text-base-color: " + player.getColor() + ";");

        top = new VBox();
        this.setContent(top);

        this.gameController = gameController;
        this.player = player;

        programLabel = new Label("Program");

        programPane = new GridPane();
        programPane.setVgap(2.0);
        programPane.setHgap(2.0);
        programCardViews = new CardFieldView[Player.NO_REGISTERS];
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CommandCardField cardField = player.getProgramField(i);
            if (cardField != null) {
                programCardViews[i] = new CardFieldView(gameController, cardField);
                programPane.add(programCardViews[i], i, 0);
            }
        }

        // XXX  the following buttons should actually not be on the tabs of the individual
        //      players, but on the PlayersView (view for all players). This should be
        //      refactored.

        finishButton = new Button("Finish Programming");
        finishButton.setOnAction( e -> gameController.finishProgrammingPhase());

        executeButton = new Button("Execute Program");
        executeButton.setOnAction( e-> gameController.executePrograms());

        stepButton = new Button("Execute Current Register");
        stepButton.setOnAction( e-> gameController.executeRegister());

        buttonPanel = new VBox(finishButton, executeButton, stepButton);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.setSpacing(3.0);
        // programPane.add(buttonPanel, Player.NO_REGISTERS, 0); done in update now

        playerInteractionPanel = new VBox();
        playerInteractionPanel.setAlignment(Pos.CENTER_LEFT);
        playerInteractionPanel.setSpacing(3.0);

        cardsLabel = new Label("Command Cards");
        cardsPane = new GridPane();
        cardsPane.setVgap(2.0);
        cardsPane.setHgap(2.0);
        cardViews = new CardFieldView[Player.NO_CARDS];
        for (int i = 0; i < Player.NO_CARDS; i++) {
            CommandCardField cardField = player.getCardField(i);
            if (cardField != null) {
                cardViews[i] = new CardFieldView(gameController, cardField);
                cardsPane.add(cardViews[i], i, 0);
            }
        }


        Label currentUpgrades = new Label("Current Upgrades");
        upgradesPane = new GridPane();
        upgradesPane.setVgap(2.0);
        upgradesPane.setHgap(2.0);
        upgradesViews = new CardFieldView[Player.NO_UPGRADES];
        for (int i = 0; i < Player.NO_UPGRADES; i++) {
            CommandCardField cardField = player.getUpgradeField(i);
            if (cardField != null) {
                upgradesViews[i] = new CardFieldView(gameController, cardField);
                upgradesPane.add(upgradesViews[i], i, 0);
            }
        }

        Label upgradeCards = new Label("Available upgrades");
        upgradesInvPane = new GridPane();
        upgradesInvPane.setVgap(2.0);
        upgradesInvPane.setHgap(2.0);
        upgradesInvViews = new CardFieldView[Player.NO_UPGRADE_INV];
        for (int i = 0; i < Player.NO_UPGRADE_INV; i++) {
            CommandCardField cardField = player.getUpgradeInv(i);
            if (cardField != null) {
                upgradesInvViews[i] = new CardFieldView(gameController, cardField);
                upgradesInvPane.add(upgradesInvViews[i], i, 0);
            }
        }

        energyCubes = new GridPane();
        energyCubes.setPadding(new Insets(4.0, 4.0, 4.0, 4.0));
        energyCubes.setHgap(2.0);


        for (int i = 0; i < player.getEnergy(); i++) {
            Rectangle rectangle = new Rectangle(27, 27); // Set width and height to 200

            // Create a radial gradient for the shine effect
            RadialGradient gradient = new RadialGradient(
                    0,
                    0,
                    0.5,
                    0.5,
                    0.5,
                    true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.WHITE),
                    new Stop(1, Color.GREEN)
            );
            rectangle.setFill(gradient);

            energyCubes.add(rectangle,i,0);
        }


        // Set the fill of the rectangle to the gradient

        top.getChildren().add(energyCubes);
        top.getChildren().add(playerInteractionPanel);
        top.getChildren().add(currentUpgrades);
        top.getChildren().add(upgradesPane);
        top.getChildren().add(upgradeCards);
        top.getChildren().add(upgradesInvPane);
        top.getChildren().add(programLabel);
        top.getChildren().add(programPane);
        top.getChildren().add(cardsLabel);
        top.getChildren().add(cardsPane);

        if (player.board != null) {
            player.board.attach(this);
            player.attach(this);
            update(player.board);
        }



        this.setOnSelectionChanged(event -> {
            if (isSelected()) {
                this.player.board.setCurrentPlayer(this.player);
                this.player.board.getStatusMessage();
            }
        });

    }



    /**
     * Updates the view based on changes to the observed player's state.
     * This method is invoked in response to notifications from the observed subject.
     * @param subject The subject (player) whose state has changed.
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == player.board) {
            for (int i = 0; i < Player.NO_REGISTERS; i++) {
                CardFieldView cardFieldView = programCardViews[i];
                if (cardFieldView != null) {
                    if (player.board.getPhase() == Phase.PROGRAMMING) {
                        cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                    } else {
                        if (i < player.board.getStep()) {
                            cardFieldView.setBackground(CardFieldView.BG_DONE);
                        } else if (i == player.board.getStep()) {
                            if (player.board.getCurrentPlayer() == player) {
                                cardFieldView.setBackground(CardFieldView.BG_ACTIVE);
                            } else if (player.board.getPlayerNumber(player.board.getCurrentPlayer()) > player.board.getPlayerNumber(player)) {
                                cardFieldView.setBackground(CardFieldView.BG_DONE);
                            } else {
                                cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                            }
                        } else {
                            cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                        }
                    }
                }
            }

            // Handling non-interaction phases
            if (player.board.getPhase() != Phase.PLAYER_INTERACTION) {
                if (!programPane.getChildren().contains(buttonPanel)) {
                    programPane.getChildren().remove(playerInteractionPanel);
                    programPane.add(buttonPanel, Player.NO_REGISTERS + 2, 0);
                }
                switch (player.board.getPhase()) {
                    case INITIALISATION:
                        finishButton.setDisable(true);
                        executeButton.setDisable(false);
                        stepButton.setDisable(true);
                        break;

                    case PROGRAMMING:
                        finishButton.setDisable(false);
                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                        break;

                    case ACTIVATION:
                        finishButton.setDisable(true);
                        executeButton.setDisable(false);
                        stepButton.setDisable(false);
                        break;

                    default:
                        finishButton.setDisable(true);
                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                }

            } else {
                // Handle player interaction phase
                handlePlayerInteraction();
            }
        } else{
            energyCubes.getChildren().clear();
            for (int i = 0; i < player.getEnergy(); i++) {
                Rectangle rectangle = new Rectangle(27, 27); // Set width and height to 200

                // Create a radial gradient for the shine effect
                RadialGradient gradient = new RadialGradient(
                        0,
                        0,
                        0.5,
                        0.5,
                        0.5,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.WHITE),
                        new Stop(1, Color.GREEN)
                );
                rectangle.setFill(gradient);

                energyCubes.add(rectangle,i,0);
            }

        }
    }

    /**
     * Updates the view based on changes to the observed player's state.
     * This method is invoked in response to notifications from the observed subject.
     */
    private void handlePlayerInteraction() {
        if (player.board.getCurrentPlayer() == player) {
            int currentStep = player.board.getStep();
            CommandCard card = player.getProgramField(currentStep).getCard();
            if (card != null && card.command.isInteractive()) {
                // Create a dialog for the command options
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Choose an Option");
                dialog.setHeaderText("Select your command:");

                // Set up a custom pane for button layout
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                int row = 0;
                for (Command option : card.command.getOptions()) {
                    Button optionButton = new Button(option.displayName);
                    GridPane.setConstraints(optionButton, 0, row++);
                    optionButton.setMaxWidth(Double.MAX_VALUE);
                    grid.getChildren().add(optionButton);

                    // Set action on button to execute command option and continue with the game
                    optionButton.setOnAction(e -> {
                        dialog.close();
                        gameController.executeCommandOptionAndContinue(player, option);
                    });
                }

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL); // Add a cancel button to the dialog

                dialog.showAndWait(); // Show dialog and wait for user response
            }
        }
    }
}
