package dk.dtu.compute.se.pisd.roborally.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JoinRequest(
        @JsonProperty("player") String player,
        @JsonProperty("lobby") String lobby
) implements Message {}
