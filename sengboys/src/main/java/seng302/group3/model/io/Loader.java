package seng302.group3.model.io;

import seng302.group3.controller.App;
import seng302.group3.model.*;

import java.io.*;
import java.nio.file.Paths;

/**
 * Created by jwc78 on 16/03/15.
 * Loader class to load files from a previously stored location
 */
public class Loader {

    /**
     * Method to deserialize from a given path
     *
     * @param path Path to deserialize from
     */
    public static Organisation load(String path) {
        Organisation organisation;
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            int ver = in.readInt();
            if(ver > App.VERSION){
                new Dialogs().infoDialog("This save is from a newer version of SCRUMPY, version: " + ver, "Software outdated");
            }
            else{
                organisation = (Organisation) in.readObject();
                App.getMainController().getStage().setTitle(organisation.getShortName());
                App.getMainController().currentOrganisation.setUnsaved(false);
                in.close();
                fileIn.close();

                Saver.setSavePath(Paths.get(path).getParent(), organisation);
                organisation.setFilename(Paths.get(path).getFileName().toString());
                checkTasksCompatible(organisation);
                return organisation;
            }


        } catch (InvalidClassException e) {
            new Dialogs().infoDialog("Old or incompatible save version", "Load Error");
            e.printStackTrace();
            return null;
        } catch (IOException i) {
            new Dialogs().infoDialog("File not found or corrupt", "Load Error");
            return null;
        } catch (ClassNotFoundException c) {
            new Dialogs().infoDialog("Organisation class not found", "Load Error");
            return null;
        }
        return null;
    }

    private static void checkTasksCompatible(Organisation organisation) {
        for (Task task:organisation.getTasks()){
            if(task.getObjectType() == null){
                Element objectT = null;
                for(Story story: organisation.getStories()){
                    if (story.getTasks().contains(task)){
                        objectT = story;
                    }
                }
                if(objectT == null){
                    for(Sprint sprint:organisation.getSprints()){
                        if (sprint.getTasks().contains(task)){
                            objectT = sprint;
                        }
                    }
                }
                task.setObjectType(objectT);
            }
        }
    }

    /**
     * When the program is loaded it will try to automatically load the last save if applicable
     *
     * @param path - The path of the last save
     * @return - The loaded organisation (if Applicable)
     * @throws Exception
     */
    public static Organisation initialLoad(String path) throws Exception {
        Organisation organisation;

        if (!new File(path).exists()) {
            return null;
        } else {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            int ver = in.readInt();
            if(ver > App.VERSION){
                new Dialogs().infoDialog("Last save was from a newer version of SCRUMPY, version: " + ver, "Software outdated");
            }else {
                organisation = (Organisation) in.readObject();
                in.close();
                fileIn.close();

                checkTasksCompatible(organisation);

                organisation.setFilename(Paths.get(path).getFileName().toString());
                return organisation;
            }
            throw new IOException("Couldn't load save");
        }
    }

}
