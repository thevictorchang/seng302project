package seng302.group3.model.gui.story_workspace;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by ajd185 on 20/09/15.
 */
public class StoryWorkspace {

    private static StoryWorkspace instance;
    private StoryWorkspaceController controller;

    /**
     * Method to get the instance of the singleton StoryWorkspace
     * @return StoryWorkspace instance
     */
    public static StoryWorkspace getInstance() {
        if (instance == null) {
            instance = new StoryWorkspace();
        }
        return instance;
    }

    /**
     * Constructor to return the StoryWorkspace
     * @return VBox containing the StoryWorkspace
     */
    public Node getStoryWorkspace() {
        VBox vBox = new VBox();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StoryWorkspace.fxml"));
        try {
            VBox layout = loader.load();
            vBox.getChildren().add(layout);
            controller = loader.getController();
            controller.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vBox;
    }

    public StoryWorkspaceController getController() {
        return this.controller;
    }

    /**
     * refreshes the controller to allow for updates within the StoryWorkspace
     */
    public void refresh() {
        if (controller != null) {
            controller.changeRefresh();
        }
    }

}
