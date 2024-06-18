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
package dk.dtu.compute.se.pisd.roborally;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.LobbySelecter;
import dk.dtu.compute.se.pisd.roborally.view.MapSelection;
import dk.dtu.compute.se.pisd.roborally.view.RoboRallyMenuBar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The main class of the RoboRally game application. This class sets up the main
 * game window, initializes the game's user interface, and serves as the central
 * point for starting and managing the game. It creates the game's menu bar,
 * board view, and controllers, and handles the application lifecycle.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class RoboRally extends Application {
    private static final int MIN_APP_WIDTH = 600;
    private Stage stage;
    private BorderPane boardRoot;
    // private RoboRallyMenuBar menuBar;

    public AppController appController;
    private URL url;
    private ImageView imgMode = new ImageView();;
    private Scene primaryScene;

    /**
     * Initializes the application before the start method is called. This is where
     * any necessary setup before creating the UI should be done. Overrides the init
     * method from the Application class.
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void init() throws Exception {
        super.init();
    }

    /**
     * Launches the RoboRally game application, setting up the main stage
     * and scene, and initializing the game's components.
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        Stage loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.TRANSPARENT);

        // Creating Icon in the setup-fase
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Rally-Master.png")));
        primaryStage.getIcons().add(appIcon);

        Scene loadingScene = createLoadingScene();
        loadingStage.setScene(loadingScene);
        loadingStage.setResizable(false);
        loadingStage.show();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Logger.getLogger(RoboRally.class.getName()).log(Level.SEVERE, null, e);
            }
            Platform.runLater(() -> {
                loadingStage.close();
                appController = new AppController(this);
                setupMainScene(primaryStage);
            });
        }).start();
    }

    /**
     * Creates and displays the board view based on the current game controller.
     * This method is responsible for updating the game's main display to show
     * the current state of the game board.
     * @param gameController The game controller that manages the state of the game.
     */
    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

        if (gameController != null) {
            // create and add view for new board
            BoardView boardView = new BoardView(gameController);
            //gameController.setBoardView(boardView);
            boardView.setId("board");
            boardRoot.setCenter(boardView);
        }
        // Waiting witch calling sizeToScene and shows, until everything is fully updated
        if (!stage.isShowing()) {
            stage.sizeToScene();
            stage.show();
        } else {
            // if stage shows, then its gonna maximize
            stage.setMaximized(true);
        }


    }

    public void createMapSlectionView(){
        boardRoot.getChildren().clear();

            // create and add view for new board
            MapSelection mapselection = new MapSelection(appController);
            mapselection.setId("mapselect");
            boardRoot.setCenter(mapselection);

        // Waiting witch calling sizeToScene and shows, until everything is fully updated
        stage.sizeToScene();
        stage.show();
    }


    public void createLobbySelectionView(){
        boardRoot.getChildren().clear();

        LobbySelecter lobby = new LobbySelecter();

        boardRoot.setCenter(lobby);

        // Waiting witch calling sizeToScene and shows, until everything is fully updated
        stage.sizeToScene();
        stage.show();

    }


    private Scene createLoadingScene() {
        VBox loadingRoot = new VBox(5);
        loadingRoot.setAlignment(Pos.CENTER);
        loadingRoot.setStyle("-fx-background-color: transparent;");

        Image logoimg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Rally-Master.png")));
        ImageView logo = new ImageView(logoimg);

        logo.setFitHeight(200);
        logo.setFitWidth(520);

        Image loadingimg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/loading.gif")));
        ImageView loading = new ImageView(loadingimg);

        loading.setFitHeight(150);
        loading.setFitWidth(150);

        VBox.setMargin(logo, new Insets(0, 0, 0, 0));
        VBox.setMargin(loading, new Insets(-50, 0, 0, 0));
        loadingRoot.getChildren().addAll(logo, loading);
        Scene scene = new Scene(loadingRoot, 600, 400);
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }

    private void setupMainScene(Stage stage) {
        // create the primary scene with a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        menuBar.setId("menu");

        boardRoot = new BorderPane();
        boardRoot.setId("root");

        Image image = new Image(getClass().getResourceAsStream("/dark.png" ));
        imgMode.setImage(image);

        imgMode.setFitHeight(25);  // Set the height of the image
        imgMode.setFitWidth(25);   // Set the width of the image

        Button mode = new Button();
        mode.setId("circular-button");
        mode.setGraphic(imgMode);  // Set the ImageView as the button's graphic
        mode.setOnAction(actionEvent -> appController.changeMode());

        HBox menuAndButton = new HBox();
        menuAndButton.setId("menu-and-button");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setMargin(mode, new Insets(0, 10, 0, 0));
        menuAndButton.getChildren().addAll(menuBar, spacer, mode);


        VBox vbox = new VBox(menuAndButton, boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);
        primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(true);
        stage.show();

        URL url = getClass().getResource("/stylesheets/LightMode.css");
        if (url == null) {
            System.out.println("Resource not found. Error!");
        } else {
            primaryScene.getStylesheets().add(url.toExternalForm());
        }
    }

    /**
     * Called when the application should stop, and provides a convenient place
     * to prepare for application exit and destroy resources. Overrides the stop
     * method from the Application class.
     * @throws Exception if an error occurs during termination.
     */
    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    /**
     * The main entry point for the application. This method is called when
     * the application is started.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setImgMode(Image img) {
        imgMode.setImage(img);
    }

    public Scene getPrimaryScene() {
        return primaryScene;
    }
}