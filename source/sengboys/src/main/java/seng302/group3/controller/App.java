package seng302.group3.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * The main application, this loads the FXML, the controller and shows the JavaFX.
 * 
 *
 */
public class App extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println("Java Version: " + System.getProperty("java.version"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();

        // here we get access to the controller if at any later stage we want to access it
        MainController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("SCRUMPY");
        primaryStage.setScene(new Scene(root, 600, 275));
        primaryStage.show();


    }


    public static void main( String[] args )
    {
        launch(args);
    }
}