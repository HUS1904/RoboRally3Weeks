package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DamageCard extends Subject {
    final public DamageCommand command;

    private ImageView cardImage;

    public DamageCard(@NotNull DamageCommand command) {
        this.command = command;
        if (command.displayName != null) {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + command.displayName + ".png")));
            cardImage = new ImageView(image);
        }
    }
    public String getName() {
        return command.displayName;
    }

    public ImageView getCardImage() {
        return cardImage;
    }

}

