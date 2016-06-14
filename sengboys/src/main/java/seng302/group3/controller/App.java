package seng302.group3.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seng302.group3.model.io.Dialogs;

/**
 * The main application, this loads the FXML, the controller and shows the JavaFX.
 */
public class App extends Application {
    public static MainController controller;

    public static final int VERSION = 6;

    @Override public void start(Stage primaryStage) throws Exception {
        System.out.println("Java Version: " + System.getProperty("java.version"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();

        // here we get access to the controller if at any later stage we want to access it
        controller = loader.getController();
        controller.setStage(primaryStage);
        controller.initLoad();
        Rectangle2D windowBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setScene(new Scene(root, windowBounds.getWidth() * 0.8, windowBounds.getHeight() * 0.8));
        primaryStage.setMaximized(true);
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(700);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/imgs/SCRUMPY.png")));
        primaryStage.show();


        primaryStage.setOnCloseRequest(event -> {
            Dialogs d = new Dialogs();
            if (controller.currentOrganisation.getUnsaved()) {
                d.SaveQuitProgram();
            } else {
                d.QuitProgram();
            }
            event.consume();
        });


    }

    public static MainController getMainController() {
        return controller;
    }


    public static void main(String[] args) {
        launch(args);
    }

}
