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
package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a command card in the RoboRally game, which is used to issue
 * commands to the robots on the game board. Each card corresponds to a specific
 * command that can be executed, such as moving forward or turning. These cards
 * are central to the game's mechanics, allowing players to control their robots
 * and interact with the game board.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class CommandCard extends Subject {
    @Expose
    final public Command command;
    @Expose
    private ImageView cardImage;

    /**
     * Constructs a new CommandCard with the specified command.
     * @param command The command that this card represents.
     */
    public CommandCard(@NotNull Command command) {
        this.command = command;
        if(command.displayName != null) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + command.displayName + ".png")));
            cardImage = new ImageView(image);
        }
    }

    /**
     * Gets the name of the command associated with this card, which is useful
     * for displaying the command in the UI or debugging.
     * @return The display name of the command.
     */
    public String getName() {
        return command.displayName;
    }


    public ImageView getCardImage() {
        return cardImage;
    }


}
