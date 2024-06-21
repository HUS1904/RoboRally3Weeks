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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import javafx.scene.image.Image;

import java.awt.*;
import java.io.InputStream;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
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

    private String altImage;

    private boolean changeImage;

    public final Space space;

    public ImageView image;

    public void setAltImage(String altImage) {
        this.altImage = altImage;
    }

    public void setChangeImage(boolean b) {
        this.changeImage = b;
    }

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

    // Ændring af polygon/trekant figur til en avatar karakter
    private void updatePlayer() {
        this.getChildren().clear();
        Player player = space.getPlayer();
        this.getChildren().add(image);

        VBox playerBox = new VBox();

        if (player != null) {
            String color = player.getColor().toLowerCase();
            String playerImageFile = "/robot-" + color + ".png";
            InputStream imageStream = getClass().getResourceAsStream(playerImageFile);
            if (imageStream == null) {
                System.out.println("Image file not found: " + playerImageFile);
                return;
            }
            ImageView playerImage = new ImageView(new Image(imageStream));
            playerImage.setFitWidth(45);
            playerImage.setFitHeight(45);

            playerImage.setRotate(switch (player.getHeading()) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
            });

            Label playerName = new Label(player.getName().toUpperCase());
            playerName.setId("player-name");
            playerBox.getChildren().addAll(playerName, playerImage);
            playerBox.setAlignment(Pos.CENTER);

            this.getChildren().add(playerBox);
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

        Set<ActionField> laserValues = new HashSet<>();
        laserValues.add(ActionField.BOARD_LASER_START);
        laserValues.add(ActionField.BOARD_LASER);
        laserValues.add(ActionField.BOARD_LASER_END);

        if (subject == s) {
            switch (s.getHeading()) {
                case EAST -> image.setRotate(90);
                case WEST -> image.setRotate(-90);
                case SOUTH -> image.setRotate(180);
            }

            if (laserValues.contains(s.getType())) {
                String img = "/" + s.getType() + ".png";

                //LASERS OFF
                if (phase == Phase.PROGRAMMING) {
                    img = switch (s.getType()) {
                        case BOARD_LASER_START -> "/" + "BOARD_LASER_START_OFF" + ".png";
                        case BOARD_LASER -> "/" + "NORMAL" + ".png";
                        case BOARD_LASER_END -> "/" + "WALL" + ".png";
                        default -> img;
                    };
                }
                //LASERS ON
                if (phase == Phase.ACTIVATION) {
                    img = switch (s.getType()) {
                        case BOARD_LASER_START -> "/" + "BOARD_LASER_START" + ".png";
                        case BOARD_LASER -> "/" + "BOARD_LASER" + ".png";
                        case BOARD_LASER_END -> "/" + "BOARD_LASER_END" + ".png";
                        default -> img;
                    };
                }
                //img = changeImage ? altImage : img;
                image.setImage(new Image(getClass().getResourceAsStream(img)));
                //changeImage = false;
            }

            if (s.getType() == ActionField.CHECKPOINT)
                image.setImage(new Image(getClass().getResourceAsStream("/" + s.getIndex() + ".png")));
        }
        updatePlayer();
    }
}
