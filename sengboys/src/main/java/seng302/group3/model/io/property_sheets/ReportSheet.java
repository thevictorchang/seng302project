package seng302.group3.model.io.property_sheets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import seng302.group3.controller.App;
import seng302.group3.model.*;
import seng302.group3.model.io.Dialogs;
import seng302.group3.model.io.ParameterisedReport;
import seng302.group3.model.io.Saver;
import seng302.group3.model.io.StatusReport;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by cjmarffy on 23/05/15.
 */
public class ReportSheet implements EditorSheet {

    final ObservableList<Object> allOptions = FXCollections.observableArrayList();
    final ObservableList<Person> allPeople = FXCollections.observableArrayList();
    final ObservableList<Team> allTeams = FXCollections.observableArrayList();
    final ObservableList<Project> allProjects = FXCollections.observableArrayList();
    final ObservableList<Release> allReleases = FXCollections.observableArrayList();
    final ObservableList<Skill> allSkills = FXCollections.observableArrayList();
    final ObservableList<Backlog> allBacklogs = FXCollections.observableArrayList();
    final ObservableList<Story> allStories = FXCollections.observableArrayList();
    final ObservableList<Sprint> allSprints = FXCollections.observableArrayList();
    @FXML Label errorLabel;
    @FXML ChoiceBox reportLevelChoice;
    @FXML ListView reportableObjectsView;
    private VBox content = new VBox();


    /**
     * A constructor which load the FXML to create the dialog layout.
     */
    public ReportSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/ReportSheet.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            content.getChildren().add(layout);
            reportLevelChoice.getItems()
                .addAll("Organisation", "Projects", "Teams", "People", "Skills", "Releases",
                    "Backlogs", "Stories", "Sprints");
            allProjects.addAll(App.getMainController().currentOrganisation.getProjects());
            allTeams.addAll(App.getMainController().currentOrganisation.getTeams());
            allPeople.addAll(App.getMainController().currentOrganisation.getPeople());
            allReleases.addAll(App.getMainController().currentOrganisation.getReleases());
            allSkills.addAll(App.getMainController().currentOrganisation.getSkills());
            allBacklogs.addAll(App.getMainController().currentOrganisation.getBacklogs());
            allStories.addAll(App.getMainController().currentOrganisation.getStories());
            allSprints.addAll(App.getMainController().currentOrganisation.getSprints());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Will be called when making a sheet.This sets up the lists of skills. Sets required list options
     * Checks projects of the person and sets the project combo box to reflect this, this then sets the
     * teams combo box to reflect the projects.
     *
     * @param args - Passes the organisation to populate the lists of skills, and the two combo boxes.
     */

    public void listConstruct(Object... args) {
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];
            String noneChoice = "None";
            ObservableList<Organisation> org = FXCollections.observableArrayList();
            org.add(organisation);
            reportableObjectsView.setItems(org);
            reportableObjectsView.setDisable(true);

            reportLevelChoice.setValue("Organisation");
            reportableObjectsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            this.reportLevelChoice.valueProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    reportableObjectsView.setDisable(false);
                    if (reportLevelChoice.getValue() == "Organisation") {
                        reportableObjectsView.setItems(org);
                        reportableObjectsView.setDisable(true);
                    } else if (reportLevelChoice.getValue() == "Projects") {
                        reportableObjectsView.setItems(allProjects);
                    } else if (reportLevelChoice.getValue() == "Teams") {
                        reportableObjectsView.setItems(allTeams);
                    } else if (reportLevelChoice.getValue() == "People") {
                        reportableObjectsView.setItems(allPeople);
                    } else if (reportLevelChoice.getValue() == "Skills") {
                        reportableObjectsView.setItems(allSkills);
                    } else if (reportLevelChoice.getValue() == "Releases") {
                        reportableObjectsView.setItems(allReleases);
                    } else if (reportLevelChoice.getValue() == "Backlogs") {
                        reportableObjectsView.setItems(allBacklogs);
                    } else if (reportLevelChoice.getValue() == "Stories") {
                        reportableObjectsView.setItems(allStories);
                    } else if (reportLevelChoice.getValue() == "Sprints") {
                        reportableObjectsView.setItems(allSprints);
                    }
                    //if string == type we want, populate the list with all things of that type.

                }
            });
        }
    }


    @Override public void fieldConstruct(Object object) {

    }

    @Override public VBox draw() {
        return content;
    }

    /**
     * This looks over the information in the dialog and checks if the inputs are either correct or
     * are allowed by the constraints of the program.
     *
     * @param args - either a organisation or both an organisation and a person depending if adding
     *             or editing.
     * @return - returns true or false depending if the sheet is correct.
     */
    public boolean validate(Object... args) {

        Object selectedTeam = reportLevelChoice.getSelectionModel().getSelectedItem();
        if (selectedTeam.equals("Organisation"))
            return true;
        else {
            if (reportableObjectsView.getSelectionModel().getSelectedItems().size() >= 1) {
                return true;
            } else
                errorLabel.setText("Select at least one item to generate a report from.");
            return false;
        }
    }


    /**
     * Takes the selected values from the dialog and generates the report for them.
     */
    @Override public void apply(Object object) {
        Object selectedTeam = reportLevelChoice.getSelectionModel().getSelectedItem();
        if (selectedTeam.equals("Organisation")) {
            Organisation currentOrganisation = App.getMainController().currentOrganisation;
            if (currentOrganisation != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Export XML Report");
                fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
                fileChooser.setInitialFileName(currentOrganisation.getShortName() + ".xml");
                fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Scrumpy XML Report", "*.xml"));
                File file = fileChooser.showSaveDialog(App.getMainController().getStage());
                if (file != null) {
                    StatusReport.generateOrganisationReport(file.getAbsolutePath().replace(" ", ""),
                        currentOrganisation);
                }
            } else {
                new Dialogs().infoDialog("No Organisation Opened\nCannot export XML",
                    "No Organisation Opened");
            }
        } else {
            Collection<Element> elements =
                reportableObjectsView.getSelectionModel().getSelectedItems();
            ParameterisedReport report = new ParameterisedReport(elements);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export XML Report");
            fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
            fileChooser.setInitialFileName(
                App.getMainController().currentOrganisation.getShortName() + ".xml");
            fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Scrumpy XML Report", "*.xml"));
            File file = fileChooser.showSaveDialog(App.getMainController().getStage());
            if (file != null) {

                StatusReport.generateReport(file.getAbsolutePath().replace(" ", ""), report);
            }
        }

    }
}
