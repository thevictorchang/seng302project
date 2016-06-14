package seng302.group3.model.io;

import javafx.scene.control.Alert;
import seng302.group3.controller.App;
import seng302.group3.model.Project;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Created by jwc78 on 16/03/15.
 * Saver class, contains methods to save project and also has methods
 * to find and set the save path of the current project
 */
public class Saver {


    /**
     * Method to serialize a given project to a given path
     * @param path Path to serialize to
     * @param project Project to serialize
     */
    public static void save(String path, Project project) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(project);
            out.close();
            fileOut.close();
            setSavePath(Paths.get(path).getParent());
        }
        catch(IOException i) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File Error");
            alert.setContentText("Failed to save the current project");
            alert.showAndWait();
            i.printStackTrace();
        }
    }

    /**
     * Method to serialize a given project to a given path
     * @return Path Save path of the current project
     */
    public static Path getSavePath() {
        Properties prop = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream(System.getProperty("user.home")+"/config.properties");
            prop.loadFromXML(input);
        }
        catch (IOException e){
            input = null;
        }

        if (input != null){

            Path path = Paths.get(prop.getProperty("savepath"));

            return path;

        }
        else {
            try{
                prop.loadFromXML(Saver.class.getResourceAsStream("/config.properties"));
            } catch (IOException i){
                return null;
            }
            Path path = Paths.get(prop.getProperty("savepath"));

            return path;
        }
    }

    /**
     * Sets the save path for the current project
     * @param savepath Path Desired save destination
     */
    public static void setSavePath(Path savepath) {
        Properties prop = new Properties();
        OutputStream output;
        try {
            output = new FileOutputStream(System.getProperty("user.home").toString()+"/config.properties");
            prop.setProperty("savepath", savepath.toString());
            prop.storeToXML(output, null);

        } catch(IOException i) {
            i.printStackTrace();
        }

    }


}
