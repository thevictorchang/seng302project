package seng302.group3.model.io;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.omg.CORBA.DynAnyPackage.Invalid;
import seng302.group3.controller.App;
import seng302.group3.controller.MainController;
import seng302.group3.model.Project;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by jwc78 on 16/03/15.
 * Loader class to load files from a previously stored location
 */
public class Loader {

    /**
     * Method to deserialize from a given path
     * @param path Path to deserialize from
     */
    public static Project load(String path) {
        Project project;
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            project = (Project) in.readObject();
            in.close();
            fileIn.close();
        }
        catch(InvalidClassException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File Error");
            alert.setContentText("Old or incompatible project version");
            alert.showAndWait();
            return null;
        }
        catch(IOException i) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File Error");
            alert.setContentText("File not found or corrupt");
            alert.showAndWait();
            return null;
        }
        catch(ClassNotFoundException c) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File Error");
            alert.setContentText("Project class not found");
            alert.showAndWait();
            return null;
        }
        Saver.setSavePath(Paths.get(path).getParent());
        project.setFilename(Paths.get(path).getFileName().toString());
        return project;
    }

}
