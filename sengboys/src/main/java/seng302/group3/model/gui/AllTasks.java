package seng302.group3.model.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import seng302.group3.model.Sprint;

import java.io.IOException;

/**
 * Created by epa31 on 8/09/15.
 */
public class AllTasks {

    private static AllTasks instance;
    private AllTasksController controller;

    private AllTasks() {

    }

    /**
     * Method to get the instance of the singleton AllTasks
     * @return AllTasks instance
     */
    public static AllTasks getInstance() {
        if (instance == null) {
            instance = new AllTasks();
        }
        return instance;
    }

    public Node getAllTasks(){
        return getAllTasks(null);
    }

    /**
     * Constructor to return the AllTasks
     * @return VBox containing the AllTasks
     */
    public Node getAllTasks(Sprint sprintToShow) {
        VBox vBox = new VBox();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AllTasks.fxml"));
        try {
            VBox layout = loader.load();
            vBox.getChildren().add(layout);
            controller = loader.getController();
            controller.initialize(sprintToShow);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vBox;
    }


    /**
     * refreshes the controller to allow for updates within the AllTasks
     */
    public void refresh() {
        if (controller != null) {
            controller.changeRefresh();
        }
    }

}
