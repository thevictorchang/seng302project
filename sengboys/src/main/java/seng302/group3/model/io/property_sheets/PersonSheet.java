package seng302.group3.model.io.property_sheets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import seng302.group3.model.*;
import seng302.group3.model.search.FilterListView;
import seng302.group3.model.search.FilterUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ntr24 on 24/04/15.
 * <p>
 * This class does the checks for adding or editing a person. These are text field validity,
 * team and project constraints and the short name being unique.
 * It will also set the FXML loaded layout and populate the fields if needed.
 */
public class PersonSheet implements EditorSheet {

    private VBox content = new VBox();

    private static final String errorStyle = "errorTextField";

    @FXML Label errorLabel;

    @FXML ComboBox teamChoice;
    @FXML ComboBox projectChoice;

    @FXML Button buttonRemoveSkill;
    @FXML Button buttonAddSkill;

    @FXML TextField shortNameTextField;
    @FXML TextField fullNameTextField;
    @FXML TextField userIDTextField;
    @FXML TextField availableSkillsTextField;
    @FXML TextField currentSkillsTextField;

    @FXML ListView<Skill> allCurrentSkillsListView;  //Was Private changed for testing
    @FXML ListView<Skill> allAvailableSkillsListView;

    private Collection<Skill> tempAvailableSkills = new ArrayList<>();
    private Collection<Skill> tempCurrentSkills = new ArrayList<>();

    final ObservableList<Skill> allCurrentSkillsList = FXCollections.observableArrayList();
    final ObservableList<Skill> allAvailableSkillsList = FXCollections.observableArrayList();

    private FilterListView allAvailableSkillsFilterListView;
    private FilterListView allCurrentSkillsFilterListView;

    Person editingPerson = null;

    boolean isSMPO;

    /**
     * A constructor which load the FXML to create the dialog layout.
     */
    public PersonSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/PersonSheet.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            content.getChildren().add(layout);
            allAvailableSkillsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            allCurrentSkillsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            FilterUtil.autoCompleteComboBox(teamChoice, FilterUtil.AutoCompleteMode.CONTAINING);
            FilterUtil.autoCompleteComboBox(projectChoice, FilterUtil.AutoCompleteMode.CONTAINING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        allAvailableSkillsFilterListView = new FilterListView(allAvailableSkillsListView, availableSkillsTextField, FilterUtil.AutoCompleteMode.CONTAINING);
        allCurrentSkillsFilterListView = new FilterListView(allCurrentSkillsListView, currentSkillsTextField, FilterUtil.AutoCompleteMode.CONTAINING);
    }

    /**
     * When a person is edited the fields are populated with information from the person.
     *
     * @param object - takes an object, this should be of type person for fields to initialize.
     */
    @Override public void fieldConstruct(Object object) {
        if (object instanceof Person) {
            editingPerson = (Person) object;

            //Set text
            shortNameTextField.setText(editingPerson.getShortName());
            fullNameTextField.setText(editingPerson.getFullName());
            userIDTextField.setText(editingPerson.getUserID());

            //Set choiceboxes
            teamChoice.setValue(editingPerson.getTeam());
            projectChoice.setValue(editingPerson.getProject());

            //Set lists
            tempCurrentSkills.addAll(editingPerson.getSkills());
            allCurrentSkillsList.addAll(tempCurrentSkills);
            allCurrentSkillsFilterListView.setData(allCurrentSkillsList);

            //A persons team is part of a project
            if (editingPerson.getTeam() != null
                && editingPerson.getTeam().getTimePeriods().size() > 0 && (
                (editingPerson.getTeam().getScrumMaster() != null
                    && editingPerson.getTeam().getScrumMaster() == editingPerson) || (
                    editingPerson.getTeam().getProductOwner() != null
                        && editingPerson.getTeam().getProductOwner() == editingPerson))) {
                //Check if the person is a PO or SM

                //Set the choiceboxes to disabled and the text to be PO or SM
                projectChoice.getItems().add("PO or SM");
                projectChoice.valueProperty().setValue("PO or SM");
                projectChoice.setDisable(true);
                teamChoice.getItems().add("PO or SM");
                teamChoice.valueProperty().setValue("PO or SM");
                teamChoice.setDisable(true);
            } else {
                //Set the choice boxes to the proper team and project which are allocated
                if (editingPerson.getProject() != null) {
                    projectChoice.valueProperty().setValue(editingPerson.getProject());
                } else {
                    projectChoice.getItems().remove("None");
                    projectChoice.valueProperty().setValue("None");
                }
                if (editingPerson.getTeam() != null) {
                    teamChoice.valueProperty().setValue(editingPerson.getTeam());
                    projectChoice.setDisable(true);
                } else {
                    teamChoice.getItems().remove("None");
                    teamChoice.valueProperty().setValue("None");
                }
            }
        }
    }

    /**
     * Will be called when making a sheet.This sets up the lists of skills. Sets required list options
     * Checks projects of the person and sets the project combo box to reflect this, this then sets the
     * teams combo box to reflect the projects.
     *
     * @param args - Passes the organisation to populate the lists of skills, and the two combo boxes.
     */

    public void skillsConstruct(Object... args) {
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];
            String noneChoice = "None";
            if (allCurrentSkillsList.size() < 1) {
                allCurrentSkillsFilterListView.setData(allCurrentSkillsList);
            }
            tempAvailableSkills.addAll(organisation.getSkills());
            tempAvailableSkills.removeAll(allCurrentSkillsList);
            allAvailableSkillsList.addAll(tempAvailableSkills);


            allAvailableSkillsFilterListView.setData(allAvailableSkillsList);

            teamChoice.getItems().addAll(organisation.getTeams());
            teamChoice.getItems().add(noneChoice);
            projectChoice.getItems().addAll(organisation.getProjects());
            projectChoice.getItems().add(noneChoice);


            this.teamChoice.valueProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                    if (FilterUtil.getComboBoxValue(teamChoice) instanceof Team) {
                        Team newT = (Team) FilterUtil.getComboBoxValue(teamChoice);
                        projectChoice.valueProperty().setValue(noneChoice);
                        projectChoice.setDisable(true);

                    } else {
                        projectChoice.setDisable(false);
                    }
                }
            });
        }
    }

    /**
     * Updates the lists of skills.
     */
    public void updateSkills() {
        allAvailableSkillsList.setAll(tempAvailableSkills);
        allCurrentSkillsList.setAll(tempCurrentSkills);
        allAvailableSkillsFilterListView.setData(allAvailableSkillsList);
        allCurrentSkillsFilterListView.setData(allCurrentSkillsList);
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

        if (args.length < 1) {
            System.err.println("Not passing any args");
            return false;
        }

        boolean result = true;

        //Checks the naming fields
        shortNameTextField.setText(shortNameTextField.getText().trim());
        fullNameTextField.setText(fullNameTextField.getText().trim());
        userIDTextField.setText(userIDTextField.getText().trim());

        if (shortNameTextField.getText().length() < 1) {
            result = false;
            shortNameTextField.getStyleClass().add(errorStyle);
        } else {
            shortNameTextField.getStyleClass().removeAll(errorStyle);
        }

        if (fullNameTextField.getText().length() < 1) {
            result = false;
            fullNameTextField.getStyleClass().add(errorStyle);
        } else {
            fullNameTextField.getStyleClass().removeAll(errorStyle);
        }

        if (userIDTextField.getText().length() < 1) {
            result = false;
            userIDTextField.getStyleClass().add(errorStyle);
        } else {
            userIDTextField.getStyleClass().removeAll(errorStyle);
        }

        Object selectedProject = FilterUtil.getComboBoxValue(projectChoice);
        if (selectedProject == null || selectedProject.equals("None")) {
            selectedProject = null;
        }

        Object selectedTeam = FilterUtil.getComboBoxValue(teamChoice);
        if (selectedTeam == null || selectedTeam.equals("None"))
            selectedTeam = null;

        if (result)
            errorLabel.setText("");
        else {
            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        }


        // uniqueness check
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];

            if (organisation
                .uniqueName(shortNameTextField.getText(), Organisation.uniqueType.UNIQUE_PERSON)) {
                shortNameTextField.getStyleClass().removeAll(errorStyle);
                return true;
            } else {
                if (args.length > 1 && args[1] instanceof Person) {
                    Person person = (Person) args[1];
                    if (shortNameTextField.getText().equals(person.getShortName())) {
                        shortNameTextField.getStyleClass().removeAll(errorStyle);
                        return true;
                    }
                }
                errorLabel.setText("Short name non-unique");
                shortNameTextField.getStyleClass().add(errorStyle);
            }

        }
        return false;
    }


    /**
     * Takes the values from the dialog and pushes them to the person. if we are adding to a
     * project or team it will catch the changes here and make them.
     *
     * @param object - a person object.
     */
    @Override public void apply(Object object) {
        if (object instanceof Person) {
            Person person = (Person) object;
            person.setShortName(shortNameTextField.getText());
            person.setFullName(fullNameTextField.getText());
            person.setUserID(userIDTextField.getText());
            person.setSkills(tempCurrentSkills);

            //If we have a selected Project
            if (FilterUtil.getComboBoxValue(projectChoice) != "None") {
                if (FilterUtil.getComboBoxValue(projectChoice) instanceof Project) {
                    Project project = (Project) FilterUtil.getComboBoxValue(projectChoice);
                    // If this persons project is changed
                    Team t = person.getTeam();
                    person.setTeam(null);
                    if (person.getProject() != project) {
                        person.setTeam(t);
                        //If the person had a project
                        if (person.getProject() != null)
                            person.getProject().removePerson(person);
                        person.setProject(project);
                        project.addPerson(person);
                    }
                    //Update the current project
                    else {
                        person.setTeam(t);
                        person.getProject().updateSkills();
                    }
                }
            }
            //We have selected none. If the person had a project, remove ties.
            else {
                if (person.getProject() != null) {
                    person.getProject().removePerson(person);
                    person.getProject().updateSkills();
                    person.setProject(null);
                }
            }
            if (FilterUtil.getComboBoxValue(teamChoice) != "None") {
                if (FilterUtil.getComboBoxValue(teamChoice) instanceof Team) {

                    Team team = (Team) FilterUtil.getComboBoxValue(teamChoice);
                    if (person.getTeam() != team) {
                        if (person.getTeam() != null)
                            person.getTeam().removePerson(person);
                            if(person.getTeam() != null){
                                for(TimePeriod timePeriod : person.getTeam().getTimePeriods()){
                                    timePeriod.getProject().removePerson(person);
                                }
                            }
                        person.setTeam(team);
                        team.addPerson(person);
                    } else {
                        person.getTeam().updateSkills();
                        for(TimePeriod timePeriod:person.getTeam().getTimePeriods()){
                            timePeriod.getProject().updateSkills();
                        }
                    }
                    if(team.getTimePeriods().size() > 0){
                        for(TimePeriod timePeriod:team.getTimePeriods()){
                            if(timePeriod.getProject().getPeople().contains(person)){
                                //Make sure not to double person up.
                            }else{
                                timePeriod.getProject().addPerson(person);
                            }
                        }
                    }
                }else{
                    if (person.getTeam() != null) {
                        person.getTeam().updateSkills();
                    }
                }
            } else {
                if (person.getTeam() != null) {
                    for(TimePeriod timePeriod:person.getTeam().getTimePeriods()){
                        timePeriod.getProject().removePerson(person);
                    }
                    person.getTeam().removePerson(person);
                    person.getTeam().updateSkills();
                    person.setTeam(null);

                }
            }

        }
    }

    /**
     * Will take all selected skills from the available list and add them to the current list.
     *
     * @param actionEvent - pressing the left(add) arrow.
     */
    public void buttonAddSkill(ActionEvent actionEvent) {
        Collection<Skill> currentSkills =
            allAvailableSkillsListView.getSelectionModel().getSelectedItems();
        if (currentSkills != null) {
            tempCurrentSkills.addAll(currentSkills);
            tempAvailableSkills.removeAll(currentSkills);
            this.updateSkills();
        }

        currentSkillsTextField.setText("");
        availableSkillsTextField.setText("");
    }

    /**
     * Will take all selected skills from the current list and add them to the available list.
     *
     * @param actionEvent
     */
    public void buttonRemoveSkill(ActionEvent actionEvent) {
        Collection<Skill> currentSkills =
            allCurrentSkillsListView.getSelectionModel().getSelectedItems();
        Team team = null;
        if (editingPerson != null)
            team = editingPerson.getTeam();

        if (currentSkills != null) {
            for (Skill s : currentSkills) {
                if (s.getShortName().equals("PO")) {
                    if ((team != null) && (team.getProductOwner() == editingPerson)) {
                        errorLabel.setText("Cannot remove PO skill from active Product Owner");
                        continue;
                    }
                } else if ((s.getShortName().equals("SM"))) {
                    if ((team != null) && (team.getScrumMaster() == editingPerson)) {
                        errorLabel.setText("Cannot remove SM skill from active Scrum Master");
                        continue;
                    }
                }
                tempAvailableSkills.add(s);
                tempCurrentSkills.remove(s);

            }
            this.updateSkills();
        }
        currentSkillsTextField.setText("");
        availableSkillsTextField.setText("");
    }
}
