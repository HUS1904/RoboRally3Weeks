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

/**
 * Enumerates the possible headings (orientations) a robot can have in the RoboRally game.
 * This orientation determines the direction in which a robot will move or interact with
 * board elements. The headings follow cardinal directions, and methods are provided to
 * navigate between these directions, simulating rotation.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public enum Heading {
    SOUTH, WEST, NORTH, EAST;

    /**
     * Returns the next clockwise heading from this one.
     * For example, if the current heading is NORTH, the next heading will be EAST.
     * @return The next clockwise heading.
     */
    public Heading next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * Returns the previous counter-clockwise heading from this one.
     * For example, if the current heading is EAST, the previous heading will be NORTH.
     * @return The previous counter-clockwise heading.
     */
    public Heading prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }
}
