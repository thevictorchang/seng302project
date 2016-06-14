package seng302.group3.model.io.property_sheets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import seng302.group3.model.Sprint;
import seng302.group3.model.Task;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by cjm328 on 20/07/15.
 */
public class PeopleToTaskAllocationSheet implements EditorSheet {


    @FXML private ListView currentPeopleList;
    @FXML private ListView availablePeopleList;
    private VBox content = new VBox();


    private Task task;
    private Sprint sprint;

    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public PeopleToTaskAllocationSheet(Task task, Sprint sprint) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditorSheets/PeopleToTaskAllocationSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);

            currentPeopleList.getSelectionModel().select(null);
            availablePeopleList.getSelectionModel().select(null);
            this.task = task;
            this.sprint = sprint;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Populates the dependency lists for stories from the organisation.
     * @param object
     */
    @Override public void fieldConstruct(Object object) {
        if(this.task != null){
            this.currentPeopleList.getItems().setAll(this.task.getAssignedPeople());
            this.availablePeopleList.getItems().addAll(this.sprint.getTeam().getPeople());
            this.availablePeopleList.getItems().removeAll(this.task.getAssignedPeople());
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
        return true;
    }

    /**
     * Pushes the dependency out to the story.
     * @param object
     */
    @Override
    public void apply(Object object) {
        this.task.setAssignedPeople(currentPeopleList.getItems());
    }



    public void buttonAddPerson(ActionEvent actionEvent) {
        Collection peopleToAdd = availablePeopleList.getSelectionModel().getSelectedItems();
        currentPeopleList.getItems().addAll(peopleToAdd);
        availablePeopleList.getItems().removeAll(peopleToAdd);
    }



    public void buttonRemovePerson(ActionEvent actionEvent) {
        Collection peopleToRemove = currentPeopleList.getSelectionModel().getSelectedItems();
        availablePeopleList.getItems().addAll(peopleToRemove);
        currentPeopleList.getItems().removeAll(peopleToRemove);
    }
}
