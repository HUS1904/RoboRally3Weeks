package dk.dtu.compute.se.pisd.roborally.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GameMove(
        @JsonProperty("player") String player,
        @JsonProperty("move") String move
) implements Message {}
