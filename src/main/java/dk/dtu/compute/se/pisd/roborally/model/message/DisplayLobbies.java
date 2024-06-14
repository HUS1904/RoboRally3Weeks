package dk.dtu.compute.se.pisd.roborally.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DisplayLobbies(
        @JsonProperty("player") String player
) implements Message {}
