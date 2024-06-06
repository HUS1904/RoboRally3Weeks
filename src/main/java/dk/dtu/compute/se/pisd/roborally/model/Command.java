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

    FORWARD("Fwd"),
    FORWARD1("Fwd x2"),
    FORWARD2("Fwd x3"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    UTURN("U-Turn"),
    Back("Back up"),
    Again("Again"),
    Power("Power Up");

    final public String displayName;

    Command(String displayName) {
        this.displayName = displayName;
    }

}
