
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
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides the graphical representation of the RoboRally game board, including all spaces and
 * any players or items present on them. This view is dynamically updated to reflect the current
 * state of the game, such as player positions, phase changes, and any special conditions affecting
 * the board.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class BoardView extends VBox implements ViewObserver {
    private Board board;
    private GridPane mainBoardPane;
    private SpaceView[][] spaces;
    private PlayersView playersView;
    private Label statusLabel;
    private SpaceEventHandler spaceEventHandler;
    private final RoboRally roborally;
    private Shop shop;

    /**
     * Constructs a BoardView associated with a given game controller, initializing
     * the visual components for each space on the board and setting up event handlers
     * for player interaction.
     * @param gameController The GameController associated with this board view, providing
     * access to the game logic and state.
     */
    public BoardView(@NotNull GameController gameController, RoboRally roborally1) {
        board = gameController.board;
        this.roborally = roborally1;
        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");
        shop = new Shop(gameController, roborally);


        spaces = new SpaceView[board.width][board.height];

        spaceEventHandler = new SpaceEventHandler(gameController);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space,board);
                spaces[x][y] = spaceView;
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }

        board.attach(this);
        update(board);
    }

    /**
     * Updates the view based on changes to the observed board object. This includes
     * updating the display of each space to reflect any new state, such as the presence
     * of players or the result of game actions.
     * @param subject The subject (board) being observed for changes.
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            this.getChildren().clear();
            Phase phase = board.getPhase();

            for (int x = 0; x < board.width; x++) {
                for(int y = 0; y < board.height; y++) {
                    SpaceView s = spaces[x][y];
                    Space s1 = board.getSpace(x, y);
                    s.updateView(s1);
                }
            }

            statusLabel.setText(board.getStatusMessage());
            if(phase == Phase.INITIALISATION){
                this.getChildren().addAll(shop,playersView,statusLabel);
            } else{
                this.getChildren().addAll(mainBoardPane,playersView,statusLabel);
            }
        }
    }

    public SpaceView[][] getSpaceViews(){
        return spaces;
    }



    // XXX this handler and its uses should eventually be deleted! This is just to help test the
    //     behaviour of the game by being able to explicitly move the players on the board!
    private class SpaceEventHandler implements EventHandler<MouseEvent> {

        final public GameController gameController;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        /**
         * Handles mouse click events on the game board's spaces. When a space is clicked,
         * this method determines the corresponding action, such as moving the current player
         * to the clicked space, based on the current state of the game. This event handler
         * is primarily for testing and demonstration purposes and should be adapted or replaced
         * for actual game mechanics.
         * @param event the mouse event that triggered this handler
         */
        @Override
        public void handle(MouseEvent event) {
            Object source = event.getSource();
            if (source instanceof SpaceView) {
                SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    gameController.movePlayerToSpace(gameController.board.getCurrentPlayer(), space);
                    event.consume();
                }
            }
        }
    }
}
