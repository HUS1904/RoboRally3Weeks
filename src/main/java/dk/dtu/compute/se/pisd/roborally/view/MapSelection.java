package dk.dtu.compute.se.pisd.roborally.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class MapSelection extends VBox {

    GameController controller;
    Image image = null ;
    ImageView imageView;
    StackPane stackPane;

    private LobbySelecter lobbySelecter;

    public MapSelection(LobbySelecter lobbySelecter) {

        this.lobbySelecter = lobbySelecter;
        // Create a ChoiceBox with selectable options
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setItems(FXCollections.observableArrayList(
                "Course 1", "Course 2", "Course 3", "Course 4"
        ));

        // Set default selection (optional)
        choiceBox.setValue("Course 1");
        choiceBox.setPadding(new Insets(10, 10, 10, 10));
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + choiceBox.getValue() +".png")), 400, 300, false, false));
            stackPane.getChildren().clear();
            stackPane.getChildren().add(imageView);
        });

        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + choiceBox.getValue() +".png")), 400, 300, false, false);
        imageView = new ImageView(image);
        imageView.prefHeight(300);
        imageView.prefWidth(400);

        stackPane = new StackPane();
        stackPane.getChildren().add(imageView);

        // Create a button to get the selected option
        Button selectButton = new Button("Select");
        selectButton.setOnAction(e -> {

           lobbySelecter.setCourse(choiceBox.getValue());
            try {
                lobbySelecter.sendRequest();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });

        this.getChildren().addAll(choiceBox,stackPane, selectButton);
    }
}