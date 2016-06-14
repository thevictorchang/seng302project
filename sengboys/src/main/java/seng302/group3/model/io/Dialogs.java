package seng302.group3.model.io;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import seng302.group3.controller.App;
import seng302.group3.model.*;
import seng302.group3.model.io.property_sheets.DialogSheet;
import seng302.group3.model.io.property_sheets.EditorSheet;
import seng302.group3.model.search.SearchUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * Created by ntr24 on 27/04/15.
 */
public class Dialogs<T> {

    final Stage dialog = new Stage();

    @FXML VBox content;
    @FXML Button AcceptButton;
    @FXML javafx.scene.control.TextField stringTextField;
    @FXML Button CancelButton;
    @FXML Button UnassignButton;
    @FXML Button DeleteButton;
    @FXML LineChart Calendar;
    @FXML javafx.scene.control.TextField SearchField;
    @FXML ListView searchResultsList;

    private VBox dialogLayout = new VBox();

    /**
     * creates a frame for the popup dialog that has an okay and a cancel button with key shortcuts
     *
     * @param sheet - the sheet that you wish to populate the popup with
     */
    private void OkayCancelFrame(EditorSheet sheet) {

        // load the stage layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OkayCancelDialog.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            dialogLayout.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.initModality(Modality.APPLICATION_MODAL);
        content.getChildren().add(sheet.draw());
        Scene dialogScene = new Scene(dialogLayout);

        dialogScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    AcceptButton.fire();
                } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                    CancelButton.fire();
                }
            }
        });
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
    }


    /**
     * creates a frame for the popup dialog that has an unassign button, a delete button and a cancel button with key shortcuts
     *
     * @param sheet - the sheet that you wish to populate the popup with
     */
    private void ThreeOptionFrame(EditorSheet sheet) {
        // load the stage layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ThreeOptionDialog.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            dialogLayout.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.initModality(Modality.APPLICATION_MODAL);
        content.getChildren().add(sheet.draw());
        Scene dialogScene = new Scene(dialogLayout);

        dialogScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    UnassignButton.fire();
                } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                    CancelButton.fire();
                }
            }
        });
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
    }


    /**
     * creates a frame for the popup dialog with just an okay button with a keyboard shortcut
     *
     * @param sheet - the sheet that you wish to populate the popup with
     */
    private void OkayFrame(EditorSheet sheet) {
        // load the stage layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OkayDialog.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            dialogLayout.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.initModality(Modality.APPLICATION_MODAL);
        content.getChildren().add(sheet.draw());
        Scene dialogScene = new Scene(dialogLayout);

        dialogScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    AcceptButton.fire();
                }
            }
        });
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
    }

    /**
     * creates a frame for the popup dialog with just an okay button and a line chart - for use in displaying team -project allocation
     *
     * @param sheet - the sheet that you wish to populate the popup with
     */
    private void TeamAllocationFrame(EditorSheet sheet) {
        // load the stage layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProjectTeamAllocationDialog.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            dialogLayout.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(dialogLayout);

        dialogScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    AcceptButton.fire();
                }
            }
        });
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
    }


    /**
     * creates a popup dialog used for quitting SCRUMPY
     */
    public void QuitProgram() {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you want to quit SCRUMPY?");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Quit SCRUMPY");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                dialog.close();
                Platform.exit();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    /**
     * creates a popup dialog used for quitting SCRUMPY with unsaved changes
     */
    public void SaveQuitProgram() {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("You have unsaved changes, are you sure you want to quit SCRUMPY?");

        this.ThreeOptionFrame(sheet);

        dialog.setTitle("Save Changes");
        UnassignButton.setText("Save".toUpperCase());
        UnassignButton.setOnAction(event -> {
            if (sheet.validate()) {
                if (Saver.getSaveName().toString().contains(App.getMainController().currentOrganisation.getShortName())
                        && App.getMainController().currentOrganisation.getSaved()) {

                    if (new Dialogs().SaveDialog(App.getMainController().currentOrganisation) == true) {
                        dialog.close();
                        Platform.exit();
                    }
                } else {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Organisation");
                    fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
                    fileChooser.setInitialFileName(
                            App.getMainController().currentOrganisation.getShortName() + ".org");
                    fileChooser.getExtensionFilters()
                            .add(new FileChooser.ExtensionFilter("Scrumpy Organisation File", "*.org"));
                    File file = fileChooser.showSaveDialog(App.getMainController().getStage());
                    if (file != null) {
                        App.getMainController().currentOrganisation.setSaved(Boolean.TRUE);
                        String fileName = file.toPath().getFileName().toString();
                        App.getMainController().currentOrganisation
                                .setShortName(file.toPath().getFileName().toString().replace(".org", ""));

                        App.getMainController().getStage().setTitle(
                                App.getMainController().currentOrganisation
                                        .getShortName()); //Update the window title
                        App.getMainController().currentOrganisation.setUnsaved(false);
                        App.getMainController().currentOrganisation.setFilename(fileName);
                        Saver.save(file.getAbsolutePath().replace(" ", ""),
                                App.getMainController().currentOrganisation);

                        //Created a revertable point
                        App.getMainController().revertableOrganisation =
                                App.getMainController().currentOrganisation.serializedCopy();
                    }
                    if (App.getMainController().revertableOrganisation != null
                            && App.getMainController().revertableOrganisation.getOrgId() != App
                            .getMainController().currentOrganisation.getOrgId()) {

                    } else {
                        dialog.close();
                        Platform.exit();
                    }
                }
            }

        });
        DeleteButton.setText("Quit".toUpperCase());
        DeleteButton.setOnAction(event -> {
            if (sheet.validate()) {
                dialog.close();
                Platform.exit();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();

    }

    /**
     * Dialog that asks the user if they want to continue with setting a story to the done state, and in the process
     * set all of the stories tasks to done
     * @param story - the Story in question
     * @return
     */
    public boolean storyToDoneDialog(Story story){
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Setting this Story to done will also set it's tasks to done."+"\n"+"Do you want to continue?");
        final boolean[] result = {false};
        this.OkayCancelFrame(sheet);
        dialog.setTitle("Set Story to Done");

        AcceptButton.setText("Mark Story Done");
        AcceptButton.setOnAction(event -> {
            for (Task t : story.getTasks()){
                t.setProgressStatus(Task.Status.COMPLETED);
            }
            result[0] = true;
            dialog.close();

        });
        CancelButton.setText("Cancel");
        CancelButton.setOnAction(event -> {
            result[0] = false;
            dialog.close();
        });

        dialog.showAndWait();
        return result[0];
    }

    /**
     * creates a popup dialog used for creating a new organisation with unsaved changes
     */
    public void SaveNewOrg() {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText(
            "You have unsaved changes, are you sure you want to create a new organisation?");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Save Changes");
        AcceptButton.setText("Save");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                if (Saver.getSavePath().toString().contains(App.getMainController().currentOrganisation.getShortName())
                        && App.getMainController().currentOrganisation.getSaved()) {

                    if (new Dialogs().SaveDialog(App.getMainController().currentOrganisation) == true) {
                        dialog.close();
                        Platform.exit();
                    }
                } else {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Organisation");
                    fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
                    fileChooser.setInitialFileName(
                            App.getMainController().currentOrganisation.getShortName() + ".org");
                    fileChooser.getExtensionFilters()
                            .add(new FileChooser.ExtensionFilter("Scrumpy Organisation File", "*.org"));
                    File file = fileChooser.showSaveDialog(App.getMainController().getStage());
                    if (file != null) {
                        App.getMainController().currentOrganisation.setSaved(Boolean.TRUE);
                        String fileName = file.toPath().getFileName().toString();

                        App.getMainController().currentOrganisation
                                .setShortName(file.toPath().getFileName().toString().replace(".org", ""));

                        App.getMainController().getStage().setTitle(
                                App.getMainController().currentOrganisation
                                        .getShortName()); //Update the window title

                        App.getMainController().currentOrganisation.setUnsaved(false);
                        App.getMainController().currentOrganisation.setFilename(fileName);

                        Saver.save(file.getAbsolutePath().replace(" ", ""),
                                App.getMainController().currentOrganisation);

                        //Created a revertable point
                        App.getMainController().revertableOrganisation =
                                App.getMainController().currentOrganisation.serializedCopy();
                    }
                    if (App.getMainController().revertableOrganisation != null
                            && App.getMainController().revertableOrganisation.getOrgId() != App
                            .getMainController().currentOrganisation.getOrgId()) {

                    } else {
                        dialog.close();
                        Platform.exit();
                    }
                }

            }

        });
        CancelButton.setText("Continue without saving");
        CancelButton.setOnAction(event -> {
            if (sheet.validate()) {
                dialog.close();
                Editor e = new Editor();
                e.NewOrganisation(App.getMainController());
            }

        });

        dialog.showAndWait();

    }

    /**
     * Called when the save function (NOT Save As) is used, creates a dialogue asking user if they want to override the
     * organisation file they currently have saved, and saves it using the Saver.
     *
     * @param organisation - The organisation file the user wants overwritten
     */
    public boolean SaveDialog(Organisation organisation) {
        final boolean[] returnval = {false};
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you want to override the previous save?");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Save Organisation");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                App.getMainController().currentOrganisation.setSaved(true);
                App.getMainController().currentOrganisation.setUnsaved(false);
                Saver.save(Saver.getSavePath().toAbsolutePath().toString() + '/' + organisation
                        .getFilename().replace(".org", ""), organisation);

                // Creating a revert point
                App.getMainController().revertableOrganisation = organisation.serializedCopy();
                App.getMainController().getStage()
                        .setTitle(App.getMainController().currentOrganisation.getShortName());
                returnval[0] = true;
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        dialog.showAndWait();
        return returnval[0];
    }


    /**
     * creates a basic okay popup with an information string and a title string
     *
     * @param info  - the information string to be used
     * @param title - the title of the popup
     */
    public void infoDialog(String info, String title) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText(info);

        this.OkayFrame(sheet);

        dialog.setTitle(title);
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                dialog.close();
            }

        });

        dialog.showAndWait();
    }

    /**
     * creates a basic okay popup that shows Team allocations on a line graph
     *
     * @param times  - the the time periods to show the allocations for
     */
    public void showAllocations(Collection<TimePeriod> times) {
        DialogSheet sheet = new DialogSheet();
        Collection<Team> teams = new ArrayList<>();
        LocalDate earliest = LocalDate.of(9999,1,1);
        LocalDate latest = LocalDate.of(0001,1,1);
        this.TeamAllocationFrame(sheet);
        for(TimePeriod t: times){
            if(!teams.contains(t.getTeam())){
                teams.add(t.getTeam());
            }
            if(t.getStartDate().isBefore(earliest))
                earliest = t.getStartDate();
            if(t.getEndDate().isAfter(latest))
                latest = t.getEndDate();
            XYChart.Series<Date, String> series = new XYChart.Series<Date, String>();
            series.setName(t.toString());
            series.getData().add(new XYChart.Data(Date.from(t.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()), t.getTeam().getName()));
            series.getData().add(new XYChart.Data(Date.from(t.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()), t.getTeam().getName()));
            Calendar.getData().add(series);
        }
        Calendar.setMinWidth(((double) ((earliest.until(latest).getYears()*365) + (earliest.until(latest).getMonths()*30) + (earliest.until(latest).getDays())))*10);
        if(Calendar.getMinWidth() < 880)
            Calendar.setMinWidth(880);
        Calendar.setLegendVisible(false);

        dialog.setTitle("Team Allocations");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                dialog.close();
            }

        });

        dialog.showAndWait();
    }

    /**
     * Dialog for revert which validates the button pressed
     */
    public void revertChangesDialog() {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you wish to revert all changes?" + "\n" + "You will lose all changes. This cannot be undone.");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Revert");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                App.getMainController().revert();
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            if (sheet.validate()) {
                dialog.close();
            }
        });

        dialog.showAndWait();
    }

    /*
    public boolean deleteElementWithTagsDialog(Delete deletionObj, Organisation organisation, Element element) {
        DialogSheet sheet = new DialogSheet();
        boolean confirm = true;
        sheet.setLabelText("This element has " + element.getTags().size() + "tags, these will be unassociated upon" +
                "delete. Proceed?");
        this.OkayCancelFrame(sheet);
        dialog.setTitle("Warning");
        AcceptButton.setOnAction(event -> {
            dialog.close();
        });





    }
    */

    /**
     * Creates a dialog that warns the user whether or not he/she wants to delete
     * the selected item. The dialog also shows the list of elements the deletion will
     * affect
     *
     * @param deletionObj  - delete instance used to handle the delete operations
     * @param organisation - the organisation the skill belongs to
     * @param skill        - the skill to be deleted
     */
    public void deleteSkillDialog(Delete deletionObj, Organisation organisation, Skill skill) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText(
            "Are you sure you wish to delete this skill? \n".concat(deletionObj.thingsToBeDeleted())
                .concat(" object(s) reference this skill"));

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Skill");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                //if (organisation.getCurrentChange() != null && organisation.getCurrentChange().getType() != Change.changeType.EDIT_SKILL) {
                //   organisation.addChange(new Change(Change.changeType.EDIT_SKILL, organisation, skill));
                //}

                deletionObj.executeDelete();
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted skill " + skill.getShortName());
                //organisation.addChange(new Change(Change.changeType.EDIT_SKILL, organisation, skill));
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    /**
     * Shows a confirmation dialog when trying to delete a team
     *
     * @param deletionObj  - delete instance used to handle the delete operations
     * @param organisation - the organisation the skill belongs to
     * @param team         - the skill to be deleted
     */
    public void deleteTeamDialog(Delete deletionObj, Organisation organisation, Team team) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText(
            "Are you sure you wish to delete this team? \n".concat(deletionObj.thingsToBeDeleted())
                .concat(" person(s) are members of this team"));

        this.ThreeOptionFrame(sheet);

        dialog.setTitle("Delete Team");
        UnassignButton.setText("Un-assign Members".toUpperCase());
        UnassignButton.setOnAction(event -> {
            if (sheet.validate()) {


                deletionObj.executePartialDelete();
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted team " + team.getShortName() + " (Team members unassigned)");
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });
        DeleteButton.setText("Delete Members".toUpperCase());
        DeleteButton.setOnAction(event -> {
            if (sheet.validate()) {

                //if (organisation.getCurrentChange() != null && organisation.getCurrentChange().getType() != Change.changeType.EDIT_TEAM) {
                //    organisation.addChange(new Change(Change.changeType.EDIT_TEAM, organisation, team));
                //}

                deletionObj.executeDelete();
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted team " + team.getShortName() + " (Team members deleted)");
                //organisation.addChange(new Change(Change.changeType.EDIT_TEAM, organisation, team));
                App.getMainController().currentOrganisation.getNavigator().refresh();

                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    /**
     * Creates the dialogues that pop up when the user tries to delete a Project.
     * Then un-assigns and deletes.
     *
     * @param deletionObj  - delete instance used to handle the delete operations
     * @param organisation - the organisation the skill belongs to
     * @param project      - the project to be deleted
     */
    public void deleteProjectDialog(Delete deletionObj, Organisation organisation,
        Project project) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText(
            "Are you sure you wish to delete this project? \nReleases cannot exist without being assigned a project, and will be deleted \n"
                .concat(deletionObj.thingsToBeDeleted())
                .concat(" object(s) are part of this project"));

        this.ThreeOptionFrame(sheet);

        dialog.setTitle("Delete Project");
        UnassignButton.setText("Un-assign Members and Teams".toUpperCase());
        UnassignButton.setOnAction(event -> {
            if (sheet.validate()) {

                //Include code to add delete change to the undo stack
                //if (organisation.getCurrentChange() != null && organisation.getCurrentChange().getType() != Change.changeType.EDIT_PROJECT) {
                //    organisation.addChange(new Change(Change.changeType.EDIT_PROJECT, organisation, project));
                //}

                deletionObj.executePartialDelete();

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Delete Project " + project.getShortName() + " (Members and Teams unassigned)");
                //organisation.addChange(new Change(Change.changeType.EDIT_PROJECT, organisation, project));
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });
        DeleteButton.setText("Delete Members and Teams".toUpperCase());
        DeleteButton.setOnAction(event -> {
            if (sheet.validate()) {

                deletionObj.executeDelete();

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted Project " + project.getShortName() + " (Members and Teams deleted)");
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    public void deleteTagDialog(Delete deletionObj, Organisation organisation, Tag tag) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you wish to delete this tag? \n" +
        tag.getElements().size() + " element(s) use this tag");

        this.OkayCancelFrame(sheet);
        dialog.setTitle("Delete Tag");

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                deletionObj.executeDelete();

                App.getMainController().addHistory(organisation.serializedCopy(), "Deleted Tag " + tag.getShortName());
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();

    }
    /**
     * Creates the dialogues that pop up when the user tries to delete a Person.
     * Then un-assigns and deletes.
     *
     * @param deletionObj  - delete instance used to handle the delete operations
     * @param organisation - the organisation the skill belongs to
     * @param person       - the person to be deleted
     */
    public void deletePersonDialog(Delete deletionObj, Organisation organisation, Person person) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you wish to delete this person? \n"
            .concat(deletionObj.thingsToBeDeleted()).concat(" object(s) reference this person"));

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Person");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                deletionObj.executeDelete();
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted Person " + person.getShortName());
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    /**
     * Creates the dialogues that pop up when the user tries to delete a Release.
     * Then un-assigns and deletes.
     *
     * @param deletionObj  - delete instance used to handle the delete operations
     * @param organisation - the organisation the skill belongs to
     * @param release      - the release to be deleted
     */

    public void deleteReleaseDialog(Delete deletionObj, Organisation organisation,
        Release release) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you wish to delete this release? \n");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Release");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                deletionObj.executeDelete();

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted release " + release.getShortName());
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }


    /**
     * Creates the dialogues that pop up when the user tries to delete a Story.
     * Then un-assigns and deletes.
     *
     * @param deletionObj  - delete instance used to handle the delete operations
     * @param organisation - the organisation the Story belongs to
     * @param story        - the story to be deleted
     */

    public void deleteStoryDialog(Delete deletionObj, Organisation organisation, Story story) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you wish to delete this story? \n");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Story");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                deletionObj.executeDelete();

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted story '" + story.getShortName() + "'");
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    /**
     * Creates a dialog that pops up when the user tries to delete a sprint
     *
     * @param deletionObj
     * @param organisation
     * @param sprint
     */
    public void deleteSprintDialog(Delete deletionObj, Organisation organisation, Sprint sprint) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you want to delete this sprint? \n");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Sprint");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                deletionObj.executeDelete();

                App.getMainController().addHistory(organisation.serializedCopy(),
                        "Deleted sprint '" + sprint.getShortName() + "'");
                App.getMainController().currentOrganisation.getNavigator().refresh();
                dialog.close();
            }
        });

        // If cancel is hit, close the dialog without making any changes
        CancelButton.setOnAction(event -> dialog.close());

        dialog.showAndWait();
    }

    /**
     * A deletion dialog for use when deleting a connection between team and project (timeperiod)
     *
     * @param deleteObject - delete instance used to handle the delete operations
     * @param organisation - The current Organisation
     * @param timePeriod   - the TimePeriod to delete
     */
    public void deleteTimePeriodDialog(Delete deleteObject, Organisation organisation,
        TimePeriod timePeriod) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you want to delete this team to project allocation? \n");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Time Allocation");

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                deleteObject.executeDelete();

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted Time Allocation" + timePeriod.toString() + "'");
                App.getMainController().currentOrganisation.getNavigator().refresh();

                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    /**
     * A deletion dialog for a backlog.This calls the delete to be performed
     *
     * @param deleteObject - delete instance used to handle the delete operations
     * @param organisation - The current Organisation
     * @param backlog      - The backlog object to be deleted
     */
    public void deleteBacklogDialog(Delete deleteObject, Organisation organisation,
        Backlog backlog) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you want to delete this Backlog? \n");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Backlog");

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                deleteObject.executeDelete();

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Deleted Backlog '" + backlog.getShortName() + "'");
                App.getMainController().currentOrganisation.getNavigator().refresh();

                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    /**
     * A dialog which will be made upon a cancel button press when a check that there have been
     * changes made to the dialog.
     * @return
     */
    public boolean dialogChangesMadeDialog(){
        final boolean[] Accepted = {false};
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you want to quit dialog, You DO have unsaved changes");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Quit Dialog");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                Accepted[0] = true;
                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
        return Accepted[0];
    }

    /**
     * A simple message dialog for when no tasks have been made and we try to add hours.
     */
    public void noRequiredObjectsDialog(String objects){
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("The required objects are not available to make the dialog: " + objects);

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Required Objects");
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                dialog.close();
            }

        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }

    public void deleteTaskDialog(Delete delete, Organisation currentOrg, Task task) {
            DialogSheet sheet = new DialogSheet();
            sheet.setLabelText("Are you sure you want to delete this Task? \n" +
                "All the logged times attached will be deleted");

            this.OkayCancelFrame(sheet);

            dialog.setTitle("Delete Task");

            AcceptButton.setOnAction(event -> {
                if (sheet.validate()) {
                    delete.executeDelete();

                    App.getMainController().addHistory(currentOrg.serializedCopy(),
                        "Deleted task '" + task.getShortName() + "'");
                    App.getMainController().currentOrganisation.getNavigator().refresh();

                    dialog.close();
                }
            });
            CancelButton.setOnAction(event -> {
                dialog.close();
            });

            dialog.showAndWait();
    }

    public void deleteLoggedTimeDialog(Delete delete, Organisation currentOrg,
        LoggedTime loggedTime) {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("Are you sure you want to delete this Logged Time? \n");

        this.OkayCancelFrame(sheet);

        dialog.setTitle("Delete Logged Time");

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                delete.executeDelete();

                App.getMainController().addHistory(currentOrg.serializedCopy(),
                    "Deleted Logged Time '" + loggedTime.getShortName() + "'");
                App.getMainController().currentOrganisation.getNavigator().refresh();

                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        dialog.showAndWait();
    }
}
