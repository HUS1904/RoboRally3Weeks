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
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the graphical view of a single space on the RoboRally game board. This class
 * extends StackPane and is responsible for displaying the space's state, including any
 * player present on the space. It listens for updates to its associated space and updates
 * the display accordingly.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 60; // 75;
    final public static int SPACE_WIDTH = 60;  // 60; // 75;

    public final Space space;

    public ImageView image;

    /**
     * Constructs a SpaceView for the specified Space.
     * @param space The space model to be represented by this view.
     */
    public SpaceView(@NotNull Space space, Board board) {
        this.space = space;


        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        image = new ImageView(this.space.image);
        image.setFitWidth(60); // Set the width to 200 pixels
        image.setFitHeight(60);
        this.getChildren().add(image);

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();
        Player player = space.getPlayer();
        this.getChildren().add(image);
        if (player != null) {
            String color = player.getColor().toLowerCase();
            String playerImageFile = "/robot-" + color + ".png";
            InputStream imageStream = getClass().getResourceAsStream(playerImageFile);
            if (imageStream == null) {
                System.out.println("Image file not found: " + playerImageFile);
                return;
            }
            ImageView playerImage = new ImageView(new Image(imageStream));
            playerImage.setFitWidth(55);
            playerImage.setFitHeight(55);

            playerImage.setRotate(switch (player.getHeading()) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
            });

            this.getChildren().add(playerImage);
        }
    }

    /**
     * Responds to updates from the observed Space model. Updates the view to reflect any
     * changes in the space's state, such as a player moving onto or off of the space.
     * @param subject The subject (Space) that has been updated.
     */
    @Override
    public void updateView(Subject subject) {
        Space s = this.space;
        Board board = s.getBoard();
        Phase phase = s.getPhase();

        Set<ActionField> invalidValues = new HashSet<>();
        invalidValues.add(ActionField.WALL);
        invalidValues.add(ActionField.BOARD_LASER_START);
        invalidValues.add(ActionField.BOARD_LASER_END);
        invalidValues.add(ActionField.PRIORITY_ANTENNA);

        if (subject == s) {
            switch (s.getHeading()) {
                case EAST -> image.setRotate(90);
                case WEST -> image.setRotate(-90);
                case SOUTH -> image.setRotate(180);
            }

            //LASERS OFF
            if (phase == Phase.PROGRAMMING) {
                switch (s.getType()) {
                    case BOARD_LASER_START -> image.setImage(new Image(getClass().getResourceAsStream("/" + "BOARD_LASER_START_OFF" + ".png")));
                    case BOARD_LASER -> image.setImage(new Image(getClass().getResourceAsStream("/" + "NORMAL" + ".png")));
                    case BOARD_LASER_END -> image.setImage(new Image(getClass().getResourceAsStream("/" + "WALL" + ".png")));
                }
            }
            //LASERS ON
            if (phase == Phase.ACTIVATION) {
                switch (s.getType()) {
                    case BOARD_LASER_START -> image.setImage(new Image(getClass().getResourceAsStream("/" + "BOARD_LASER_START" + ".png")));
                    case BOARD_LASER -> image.setImage(new Image(getClass().getResourceAsStream("/" + "BOARD_LASER" + ".png")));
                    case BOARD_LASER_END -> image.setImage(new Image(getClass().getResourceAsStream("/" + "BOARD_LASER_END" + ".png")));
                }
            }

//            //ROBOTLASERS
//            for(int i = 0; i < board.getPlayerAmount(); i++){
//                Player p = board.getPlayer(i);
//                int x = p.getSpace().x;
//                int y = p.getSpace().y;
//
//                while (board.getSpace(x,y).getType() != null && !invalidValues.contains(board.getSpace(x,y).getType())){
//                    if ((x >= 0 && x < board.width) && (y >= 0 && y < board.height)) {
//                        //PHASE = PROGRAMMING | LASERS = OFF
//                        if(phase == Phase.PROGRAMMING){
//                            insertHere.image.setImage(new Image(getClass().getResourceAsStream("/" + board.getSpace(x,y).getType() + ".png" )));
//                        }
//
//                        //PHASE = ACTIVATION | LASERS = ON
//                        if(phase == Phase.ACTIVATION && !p.getSpace().equals(s)){
//                            insertHere.setImage(new Image(getClass().getResourceAsStream("/" + "BOARD_LASER" + ".png" )));
//                        }
//                    }
//
//                    switch (p.getHeading()) {
//                        case NORTH:
//                            y--;
//                            break;
//                        case EAST:
//                            x++;
//                            break;
//                        case SOUTH:
//                            y++;
//                            break;
//                        case WEST:
//                            x--;
//                            break;
//                    }
//                }
//            }
            updatePlayer();
        }
    }

    public Phase getPhase(){
        return space.getPhase();
    }

}

