package seng302.group3.model.io.property_sheets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import seng302.group3.model.Backlog;
import seng302.group3.model.Organisation;
import seng302.group3.model.Story;

import java.io.IOException;

/**
 * Created by cjm328 on 20/07/15.
 */
public class DependencySheet implements EditorSheet {

    private static final String errorStyle = "errorTextField";
    private static final String comboErrorStyle = "errorBox";

    private VBox content = new VBox();

    private Organisation organisation;

    @FXML public Label errorLabel;

    @FXML public ListView<Story> dependencyListView;

    private StorySheet storySheet = null;
    private Story baseStory = null;
    private Boolean fromBacklog = false;
    private BacklogSheet backSheet = null;

    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public DependencySheet(Organisation organisation, StorySheet storySheet, Story baseStory) {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/EditorSheets/DependencySheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);

            dependencyListView.getSelectionModel().select(null);
            this.organisation = organisation;
            this.storySheet = storySheet;
            this.baseStory = baseStory;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Populates the dependency lists for stories from the organisation.
     * @param object
     */
    @Override public void fieldConstruct(Object object) {
        if(this.fromBacklog == true){
            dependencyListView.getItems().addAll(backSheet.allCurrentStoriesListView.getItems());
            dependencyListView.getItems().remove(baseStory);
            dependencyListView.getItems().removeAll(storySheet.getDependencyListView().getItems());
        }
        else{
            if(storySheet.backlogComboBox.getSelectionModel().getSelectedItem() == null || !(storySheet.backlogComboBox.getSelectionModel().getSelectedItem() instanceof Backlog)){
                dependencyListView.getItems().setAll();
            }
            else{
                dependencyListView.getItems().addAll(((Backlog) storySheet.backlogComboBox.getSelectionModel().getSelectedItem()).getStories());
                dependencyListView.getItems().remove(baseStory);
                dependencyListView.getItems().removeAll(storySheet.getDependencyListView().getItems());
            }
        }
    }


    /**
     * Returns the content of the dialog.
     * @return
     */
    @Override public VBox draw() {
        return content;
    }

    /**
     * Checks the dialogs fields to see if they are valid inputs e.g. non-null
     * @param args
     * @return
     */
    @Override public boolean validate(Object... args) {
        boolean result = false;
        if(dependencyListView.getSelectionModel().getSelectedItem() == null){
            errorLabel.setText("Select a story");
            return false;
        }else if(dependencyListView.getSelectionModel().getSelectedItem().checkDependencyCycle(baseStory)){
            errorLabel.setText("Error: Cyclic dependencies");
            return false;
        }else{
            return true;
        }

    }

    /**
     * Pushes the dependency out to the story.
     * @param object
     */
    @Override
    public void apply(Object object) {
        storySheet.getDependencyListView().getItems().add(dependencyListView.getSelectionModel().getSelectedItem());
    }

    public void setFromBackLog(BacklogSheet bSheet){
        fromBacklog = true;
        backSheet = bSheet;
    }


}
