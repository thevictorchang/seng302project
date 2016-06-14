package seng302.group3.model.io;


import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import seng302.group3.model.History.Change;
import seng302.group3.model.Person;
import seng302.group3.model.Project;
import seng302.group3.model.io.property_sheets.PersonProperties;
import seng302.group3.model.io.property_sheets.ProjectProperties;

import java.util.Optional;

/**
 * Created by epa31 on 17/03/15.
 * Editor class to manage editing of project aspects
 */
public class Editor  {

    /**
     * Enum for types of editing
     */
    public static enum editorType {
        PROJECT, PERSON
    }

    /**
     * Method to generate a property sheet for the given type and object
     * @param type editorType Type of object being edited
     * @param editorObject Object Object to be edited
     */
    public static Object createPropertySheet(editorType type, Object editorObject){

        final Dialog<Pair<String, String>> dialog = new Dialog<>();

        switch (type){
            case PROJECT:
                if(editorObject != null && editorObject instanceof Project){
                    dialog.setTitle("Project Settings");

                    Project project = (Project) editorObject;
                    final ProjectProperties properties = new ProjectProperties(project);

                    ButtonType saveButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
                    saveButton.setOnAction(e ->{
                        if(project.getCurrentChange() != null && project.getCurrentChange().getType() != Change.changeType.EDIT_PROJECT){
                            project.addChange(new Change(Change.changeType.EDIT_PROJECT, project));
                        }
                        project.setShortName(properties.getShortName());
                        project.setLongName(properties.getLongName());
                        project.setDescription(properties.getDescription());
                        project.addChange(new Change(Change.changeType.EDIT_PROJECT, project));


                    });

                    Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                    closeButton.setOnAction(e -> {
                        // this is to check if the user has canceled the new project.
                        // no new project should be made in this instance
                        project.setShortName(null);
                    });

                    dialog.getDialogPane().setContent(properties);

                    String previousShortname = project.getNameShort();
                    dialog.showAndWait();

                    if(project.getNameShort() == null){
                        project.setShortName(previousShortname);
                        return null; // if the cancel button was pressed return null
                    }
                    return project;
                }
                return null;
            case PERSON:
                if(editorObject == null){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cannot edit person");
                    alert.setContentText("No person selected to edit");

                    alert.showAndWait();
                }
                else if(editorObject instanceof Person){
                    dialog.setTitle("Person Settings");

                    Person person = (Person) editorObject;
                    final PersonProperties properties = new PersonProperties(person);

                    ButtonType saveButtonType = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
                    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
                    saveButton.setOnAction(e ->{
                        person.setNameShort(properties.getShortName());
                        person.setFullName(properties.getFullName());
                        person.setUserID(properties.getUserID());
                    });

                    Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
                    closeButton.setOnAction(e -> {
                        // this is to check if the user has canceled the add person.
                        // no new person should be made in this instance
                        person.setUserID(null);
                    });


                    dialog.getDialogPane().setContent(properties);
                    String previousUserID = person.getUserID();
                    dialog.showAndWait();
                    if(person.getUserID() == null){
                        person.setUserID(previousUserID);
                        return null;
                    }
                    return person;

                }
                return null;
        }



        return editorObject;
    }
}


