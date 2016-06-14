package seng302.group3.model.History;

import javafx.scene.control.Alert;
import seng302.group3.model.Project;

/**
 * Created by ntr24 on 23/03/15.
 */
abstract public class History {

    /**
     * Takes a project and reverts the last change done to it.
     * @param project - the current project object which is being worked on
     * @return - the project in its previous state
     */
    public static Project undo(Project project){
        if(project.getPastHistory().size() > 0){
            Change change = project.getPastHistory().pop();
            project.getFutureHistory().push(project.getCurrentChange());
            project.setCurrentChange(change);
            project = change.applyChange(project);
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot undo");
            alert.setContentText("There is nothing to undo.");

            alert.showAndWait();
        }

        return project;
    }

    /**
     * Takes a project and reverts the last undo done to it.
     * @param project - the current project object which is being worked on
     * @return - the project in its previous state
     */
    public static Project redo(Project project){
        if(project.getFutureHistory().size() > 0){
            Change change = project.getFutureHistory().pop();
            project.getPastHistory().push(project.getCurrentChange());
            project.setCurrentChange(change);
            project = change.applyChange(project);

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot redo");
            alert.setContentText("There is nothing to redo.");

            alert.showAndWait();
        }
        return project;
    }
}
