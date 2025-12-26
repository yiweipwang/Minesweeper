package minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the app class where I initiate a local variable of the top level logic class and the pane organizer,
 * as well as setting up the overall scene and stage of the game!
 */

public class App extends Application {
    /**
     * This method sets up the main stage and scene of the game, as well as initializing a local variable of the
     * top level logic class and graphical organizer class.
     */
    @Override
    public void start(Stage stage) {
        MSGame game = new MSGame(stage);
        PaneOrganizer organizer = new PaneOrganizer(game);
        Scene scene = new Scene(organizer.getRoot());
        stage.setScene(scene);
        stage.setTitle("Minesweeper");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // launch is a method inherited from Application
    }
}
