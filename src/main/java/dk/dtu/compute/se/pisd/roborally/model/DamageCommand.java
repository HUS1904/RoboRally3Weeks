package dk.dtu.compute.se.pisd.roborally.model;

public enum DamageCommand {
    SPAM("Spam");

    final public String displayName;

    DamageCommand(String displayName) {
        this.displayName = displayName;
    }
}
