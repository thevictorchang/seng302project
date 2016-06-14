package seng302.group3.model.io;

import seng302.group3.controller.App;
import seng302.group3.model.Organisation;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by jwc78 on 16/03/15.
 * Saver class, contains methods to save project and also has methods
 * to find and set the save path of the current project
 */
public class Saver {


    /**
     * Method to serialize a given project to a given path
     *
     * @param path         Path to serialize to
     * @param organisation Project to serialize
     */
    public static void save(String path, Organisation organisation) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path.replace(".org", "") + ".org");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeInt(App.VERSION);
            out.writeObject(organisation);
            out.close();
            fileOut.close();
            setSavePath(Paths.get(path).getParent(), organisation);
        } catch (IOException i) {
            Dialogs d = new Dialogs();
            d.infoDialog("Failed to save the current project", "Save Error");
            System.err.println(i);
        }
    }

    /**
     * Method to serialize a given project to a given path
     *
     * @return Path Save path of the current project
     */
    public static Path getSavePath() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(System.getProperty("user.home") + "/config.properties");
            prop.loadFromXML(input);
        } catch (IOException e) {
            input = null;
        }

        if (input != null) {
            Path path = Paths.get("/");
            try {
                path = Paths.get(prop.getProperty("savepath"));
            } catch (NullPointerException e) {
                App.getMainController().printInfo(
                    "Existing outdated config.properties found, old save name disregarded");
            }
            String checkPath = path.toString().concat("/"+App.getMainController().currentOrganisation.toString()+".org");
            if (new File(path.toString()).exists()) {
                return path;
            }else{
                return Paths.get(System.getProperty("user.home"));
            }
        } else {
            Path path = Paths.get("/");
            return path;
        }
    }

    /**
     * Method to get the path and name of the current organisation
     *
     * @return Path Save path and name of organisation
     */
    public static Path getSaveName() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(System.getProperty("user.home") + "/config.properties");
            prop.loadFromXML(input);
        } catch (IOException e) {
            input = null;
        }

        if (input != null) {
            Path path = Paths.get("/");
            try {
                path = Paths.get(prop.getProperty("savename"));
            } catch (NullPointerException e) {
                App.getMainController().printInfo(
                    "Existing outdated config.properties found, old save name disregarded");
            }
            if (new File(path.toString()).exists()) {
                return path;
            }else{
                return Paths.get("/");
            }

        } else {

            Path path = Paths.get("/");

            return path;
        }
    }

    /**
     * Sets the save path for the current project
     *
     * @param savepath Path Desired save destination
     */
    public static void setSavePath(Path savepath, Organisation organisation) {
        Properties prop = new Properties();
        OutputStream output;
        try {
            output = new FileOutputStream(
                System.getProperty("user.home").toString() + "/config.properties");
            prop.setProperty("savepath", savepath.toString());
            prop.setProperty("savename",
                savepath.toString() + "/" + organisation.getShortName().replace(".org", "") + ".org");
            //prop.setProperty("test", "No loaded files");
            prop.storeToXML(output, null);

        } catch (IOException i) {
            // TODO how do we want to handle this
            //i.printStackTrace();
        }

    }


}
