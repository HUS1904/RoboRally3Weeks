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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumerates the different commands that can be issued to robots in the RoboRally game.
 * Each command represents an action that a robot can perform on the game board, such as
 * moving forward or turning. These commands are fundamental to the gameplay, dictating
 * how robots navigate the board and interact with various elements and each other.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.

    FORWARD("Fwd", "Move your robot in the direction it is facing by 1 space."),
    FORWARD1("Fwd x2", "Move your robot in the direction it is facing by 2 spaces."),
    FORWARD2("Fwd x3", "Move your robot in the direction it is facing by 3 spaces."),
    RIGHT("Turn Right", "Turn your robot 90 degrees to the right."),
    LEFT("Turn Left", "Turn your robot 90 degrees to the left."),
    UTURN("U-Turn", "Turn your robot 180 degrees so it faces the opposite direction."),
    Back("Back up", "Move your robot one space back."),
    Again("Again", "Repeat the programming in your previous register."),
    Power("Power Up", "Take one energy cube, and place it on your player mat."),
    SPAM("Spam", "MANGLER DESCRIPTION"),
    RECHARGE("Recha", "Gain three energy"),
    RAMMINGGEAR("Ram", "Deal one SPAM damage card when you push a robot."),
    VIRUSMODULE("Virus", "Deal one VIRUS damage card when you push a robot."),
    BOINK("Boink", "Perform an additional move either forward, backward, left or right without changing heading"),
    OPTION_LEFT_RIGHT("Option Left or Right", "MANGLER DESCRIPTION", LEFT, RIGHT),
    OPTION_LEFT_FORWARD("Option Left or Forward", "MANGLER DESCRIPTION", LEFT, FORWARD);

    final public String displayName;
    final public String description;
    final private List<Command> options;

    Command(String displayName, String description, Command... options) {
        this.displayName = displayName;
        this.description = description;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }

    public boolean isInteractive() {return !options.isEmpty(); }
    public List<Command> getOptions() {return options; }

    public String getName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
