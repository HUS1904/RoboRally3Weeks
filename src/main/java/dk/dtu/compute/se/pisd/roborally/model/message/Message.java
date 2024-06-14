package dk.dtu.compute.se.pisd.roborally.model.message;

public interface Message {
    // Default method added just as an example if you want to add
    // methods to messages that don't depend on individual class properties.
    default String getWindowsHostName() {
        return System.getenv("COMPUTERNAME");
    }
}
