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
import javafx.util.Callback;
import seng302.group3.controller.App;
import seng302.group3.model.*;
import seng302.group3.model.io.Editor;
import seng302.group3.model.search.FilterListView;
import seng302.group3.model.search.FilterUtil;
import seng302.group3.model.sorting.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vch51 on 14/07/15.
 */
public class SprintSheet implements EditorSheet {

    private static final String errorStyle = "errorTextField";
    private static final String errorStyleButton = "errorButton";

    // Orange error (for when 2 sections conflict)
    private static final String conflictStyleBox = "conflictBox";
    private static final String conflictStyleButton = "conflictButton";



    private VBox content = new VBox();

    private TimePeriod dateHolder = new TimePeriod();
    private Organisation organisation;
    private Sprint sprint;

    final ObservableList<Story> allCurrentStoriesList = FXCollections.observableArrayList();
    final ObservableList<Story> allAvailableStoriesList = FXCollections.observableArrayList();

    // Text Fields
    @FXML TextField shortNameTextField;
    @FXML TextField fullNameTextField;
    @FXML TextField descriptionTextField;
    @FXML TextField availableStoriesTextField;
    @FXML TextField currentStoriesTextField;

    // Choice Boxes
    @FXML ComboBox releaseChoice;
    @FXML ComboBox backlogChoice;
    @FXML ComboBox teamChoice;

    // Labels
    @FXML Label errorLabel;
    @FXML Label dateErrorLabel;
    @FXML Label endDateAfterReleaseDateErrorLabel;
    @FXML Label addingStoryNotReadyErrorLabel;
    @FXML Label releaseDate;


    // Buttons
    @FXML Button buttonSelectDates;
    @FXML Button buttonAddStory;
    @FXML Button buttonRemoveStory;

    // List Views
    @FXML ListView<Story> allCurrentStoriesListView;  //Was Private changed for testing
    @FXML ListView<Story> allAvailableStoriesListView;
    @FXML ListView<Task> sprintTasksListView;

    private FilterListView allAvailableStoriesFilterListView;
    private FilterListView allCurrentStoriesFilterListView;

    /**
     * Constructor - loads fxml and populates comboboxes
     * @param organisation
     */
    public SprintSheet(Organisation organisation) {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/EditorSheets/SprintSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
            allAvailableStoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            allCurrentStoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //adds every release into the release choice
        for(Release release : organisation.getReleases()) {
            releaseChoice.getItems().add(release);
        }

        for(Backlog backlog : organisation.getBacklogs()) {
            backlogChoice.getItems().add(backlog);
        }

        /*for(Team team: organisation.getTeams()) {
            teamChoice.getItems().add(team);
        }*/
        teamChoice.setDisable(true);

        //sets the default "None" choice in the choiceboxes
        FilterUtil.autoCompleteComboBox(releaseChoice, FilterUtil.AutoCompleteMode.CONTAINING);
        releaseChoice.getItems().add("None");
        releaseChoice.getSelectionModel().select("None");
        FilterUtil.autoCompleteComboBox(backlogChoice, FilterUtil.AutoCompleteMode.CONTAINING);
        backlogChoice.getItems().add("None");
        backlogChoice.getSelectionModel().select("None");

        teamChoice.getItems().add("None");
        teamChoice.getSelectionModel().select("None");

        allAvailableStoriesFilterListView = new FilterListView(allAvailableStoriesListView, availableStoriesTextField, FilterUtil.AutoCompleteMode.CONTAINING);
        allCurrentStoriesFilterListView = new FilterListView(allCurrentStoriesListView, currentStoriesTextField, FilterUtil.AutoCompleteMode.CONTAINING);

        updateStories();

        this.organisation = organisation;

        releaseChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(FilterUtil.getComboBoxValue(releaseChoice) instanceof Release){
                teamChoice.setDisable(false);
                teamChoice.getItems().setAll(((Release) FilterUtil.getComboBoxValue(releaseChoice)).getProject().getTeams());
                releaseDate.setText(((Release) FilterUtil.getComboBoxValue(releaseChoice)).getReleaseDate().toString());

            }else{
                teamChoice.getItems().setAll("None");
                teamChoice.setDisable(true);
            }
        });
    }

    /**
     * On Edit this method populates the sheet with the existing data
     * @param object
     */
    @Override public void fieldConstruct(Object object) {

        allCurrentStoriesFilterListView.setData(allCurrentStoriesList);
        this.backlogChoice.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (FilterUtil.getComboBoxValue(backlogChoice) instanceof Backlog && oldValue != newValue) {
                Backlog newBacklog = (Backlog) FilterUtil.getComboBoxValue(backlogChoice);

                allAvailableStoriesList.setAll(newBacklog.getStories());
                allAvailableStoriesFilterListView.setData(allAvailableStoriesList);
            }
            else{
                allAvailableStoriesList.clear();
                allAvailableStoriesFilterListView.setData(allAvailableStoriesList);
            }
            allCurrentStoriesList.clear();
            allCurrentStoriesFilterListView.setData(allCurrentStoriesList);
            updateStories();
        });
        if (object instanceof Sprint) {
            sprint = (Sprint) object;

            shortNameTextField.setText(sprint.getShortName());
            fullNameTextField.setText(sprint.getFullName());
            descriptionTextField.setText(sprint.getDescription());

            // Date entry
            //dateHolder = sprint.getDates();
            dateHolder = new TimePeriod(sprint.getDates().getStartDate(),
                sprint.getDates().getEndDate(),sprint.getDates().getProject(),sprint.getDates().getTeam());
            buttonSelectDates.setText(dateHolder.toString());
            allCurrentStoriesList.setAll(sprint.getStories());
            if (sprint.getRelease() != null) {
                releaseChoice.getSelectionModel().select(sprint.getRelease());
            }

            if (sprint.getTeam() != null) {
                teamChoice.getSelectionModel().select(sprint.getTeam());
            }
            if (sprint.getBacklog() != null) {
                backlogChoice.getSelectionModel().select(sprint.getBacklog());
                allCurrentStoriesList.setAll(sprint.getStories());
                allCurrentStoriesFilterListView.setData(allCurrentStoriesList);
                allAvailableStoriesList.setAll(sprint.getBacklog().getStories());
                allAvailableStoriesList.removeAll(allCurrentStoriesList);
                allAvailableStoriesFilterListView.setData(allAvailableStoriesList);


            }

            sprintTasksListView.getItems().addAll(sprint.getTasks());
        }
        updateStories();



    }

    /**
     * Prepares the VBox which is the dialogs parts to be returned.
     * @return
     */
    @Override public VBox draw() {
        return content;

    }

    /**
     * Checks the dialogs fields to see if there are no inputs which are considered bad.
     * @param args
     * @return
     */
    @Override public boolean validate(Object... args) {
        Organisation org;
        if(args[0] instanceof Organisation){
            org = (Organisation) args[0];
        }
        else{
            org = App.getMainController().currentOrganisation;
        }
        boolean result = true;
        boolean fieldsEntered = true;
        boolean unique = true;

        shortNameTextField.setText(shortNameTextField.getText().trim());
        fullNameTextField.setText(fullNameTextField.getText().trim());
        descriptionTextField.setText(descriptionTextField.getText().trim());

        //checks the shortname text field is at least one character long
        if (shortNameTextField.getText().length() < 1) {
            result = false;
            fieldsEntered = false;
            shortNameTextField.getStyleClass().add(errorStyle);
        } else {
            shortNameTextField.getStyleClass().removeAll(errorStyle);
        }

        //checks the fullname text field is at least one character long
        if (fullNameTextField.getText().length() < 1) {
            result = false;
            fieldsEntered = false;
            fullNameTextField.getStyleClass().add(errorStyle);
        } else {
            fullNameTextField.getStyleClass().removeAll(errorStyle);
        }

        //checks the fullname text field is at least one character long
        if (descriptionTextField.getText().length() < 1) {
            result = false;
            fieldsEntered = false;
            descriptionTextField.getStyleClass().add(errorStyle);
        } else {
            descriptionTextField.getStyleClass().removeAll(errorStyle);
        }


        //checks if a release (that isn't "None") has been selected
        if ((FilterUtil.getComboBoxValue(releaseChoice) == null) || (FilterUtil.getComboBoxValue(releaseChoice) == "None")) {
            result = false;
            fieldsEntered = false;
            releaseChoice.getStyleClass().add(errorStyle);
        } else {
            releaseChoice.getStyleClass().removeAll(errorStyle);
        }

        //checks if a backlog (that isn't "None") has been selected
        if ((FilterUtil.getComboBoxValue(backlogChoice)) == null || (FilterUtil.getComboBoxValue(backlogChoice) == "None")) {
            result = false;
            fieldsEntered = false;
            backlogChoice.getStyleClass().add(errorStyle);

        } else {
            backlogChoice.getStyleClass().removeAll(errorStyle);
        }

        //checks if a team (that isn't "None") has been selected
        if ((teamChoice.getSelectionModel().getSelectedItem()) == null || (teamChoice.getSelectionModel().getSelectedItem() == "None")) {
            result = false;
            fieldsEntered = false;
            teamChoice.getStyleClass().add(errorStyle);

        } else {

            if ((FilterUtil.getComboBoxValue(releaseChoice) == null) || (FilterUtil.getComboBoxValue(releaseChoice) == "None")) {
                result = false;
                fieldsEntered = false;
                releaseChoice.getStyleClass().add(errorStyle);
            } else {
                releaseChoice.getStyleClass().removeAll(errorStyle);
                Release release = (Release) FilterUtil.getComboBoxValue(releaseChoice);
                Project project = release.getProject();
                Team team = (Team) teamChoice.getSelectionModel().getSelectedItem();
                boolean withinTime = false;
                Collection<TimePeriod> fittingTimePeriods = new ArrayList<>();
                for (TimePeriod t:team.getTimePeriods()){
                    for (TimePeriod t2: project.getTimePeriods()){
                        if (t == t2){
                            fittingTimePeriods.add(t2);
                        }
                    }
                }
                if (dateHolder.getStartDate()!=null && dateHolder.getEndDate()!=null){
                    for (TimePeriod matchTimes: fittingTimePeriods) {
                        if(matchTimes.containsDate(dateHolder.getStartDate()) && matchTimes.containsDate(dateHolder.getEndDate())){
                            withinTime = true;
                        }
                    }
                }

                if (withinTime){
                    teamChoice.getStyleClass().removeAll(errorStyle);
                    errorLabel.setText("");
                }
                else{
                    result = false;
                    teamChoice.getStyleClass().add(errorStyle);
                    errorLabel.setText("Team is leaving the project during the sprint");
                }
            }


        }

        // If the start / end date hasn't been entered
        if (dateHolder.getStartDate() == null || dateHolder.getEndDate() == null) {
            buttonSelectDates.getStyleClass().add(errorStyleButton);
        } else {
            buttonSelectDates.getStyleClass().removeAll(errorStyleButton);
        }

        //checks if the release date is after the sprint end date
        if (!((FilterUtil.getComboBoxValue(releaseChoice)) == null || (FilterUtil.getComboBoxValue(releaseChoice) == "None"))) {

            Release release = (Release) FilterUtil.getComboBoxValue(releaseChoice);
            if (dateHolder.getEndDate() == null) {
                result = false;
                endDateAfterReleaseDateErrorLabel.setText("Select a date");

            } else {
                if (dateHolder.getEndDate().isAfter(release.getReleaseDate())) {
                    result = false;
                    endDateAfterReleaseDateErrorLabel.setText("Sprint must end before release date");
                    buttonSelectDates.getStyleClass().add(conflictStyleButton);
                    releaseChoice.getStyleClass().add(conflictStyleBox);

                } else {
                    endDateAfterReleaseDateErrorLabel.setText("");
                    buttonSelectDates.getStyleClass().removeAll(conflictStyleButton);
                    releaseChoice.getStyleClass().removeAll(conflictStyleBox);
                }
            }
        }

        //checks if a release (that isn't "None") has been selected
        if ((FilterUtil.getComboBoxValue(releaseChoice)) == null || (FilterUtil.getComboBoxValue(releaseChoice) == "None")) {
            result = false;
            fieldsEntered = false;
            buttonSelectDates.getStyleClass().removeAll(conflictStyleButton);
            releaseChoice.getStyleClass().removeAll(conflictStyleBox);
            endDateAfterReleaseDateErrorLabel.setText("");
            releaseChoice.getStyleClass().add(errorStyle);
        } else {
            releaseChoice.getStyleClass().removeAll(errorStyle);
        }

        if (org.uniqueName(shortNameTextField.getText(), Organisation.uniqueType.UNIQUE_SPRINT) == false && this.sprint != null) {
            unique = false;
            fieldsEntered = false;
            endDateAfterReleaseDateErrorLabel.setText("Short Name must be unique");
            shortNameTextField.getStyleClass().add(errorStyle);

            if (args.length > 1 && args[1] instanceof Sprint) {
                Sprint sprint = (Sprint) args[1];
                if (shortNameTextField.getText().equals(sprint.getShortName())) {
                    shortNameTextField.getStyleClass().removeAll(errorStyle);
                    unique = true;
                }
            }
        } else {
            if (!shortNameTextField.getText().equals("")){
                shortNameTextField.getStyleClass().removeAll(errorStyle);
            }

        }

        // Check if fields have been entered
        if (!fieldsEntered) {
            errorLabel.setText("Missing or Incorrect Fields");
        } else {
            if (errorLabel.getText().equals("Missing or Incorrect Fields"))
                errorLabel.setText("");
        }

        if(!unique && result){
            result = false;
        }


        return result;

    }

    /**
     * Takes an object, casts it to "of type sprint" and applies all the set properties
     * defined by the values on the sheet
     * @param object The sprint object that is passed
     */
    @Override public void apply(Object object) {
        if (object instanceof Sprint) {
            // Cast to sprint type
            Sprint sprint = (Sprint) object;

            // Set all the properties
            sprint.setShortName(shortNameTextField.getText());
            sprint.setFullName(fullNameTextField.getText());
            sprint.setDescription(descriptionTextField.getText());
            sprint.setDates(dateHolder);

            for (Task task : sprintTasksListView.getItems()){
                task.setObjectType(sprint);
            }
            sprint.setTasks(sprintTasksListView.getItems());
            // Clearing the stories in the sprint before we start adding to it
            sprint.getStories().clear();

            // For each story in the current stories list...
            //add it
            allCurrentStoriesList.forEach(sprint::addStory);
            Sort.sprintStoryPriority(sprint.getStories());

            // If we have a selected release
            if (FilterUtil.getComboBoxValue(releaseChoice) != "None") {
                if (FilterUtil.getComboBoxValue(releaseChoice) instanceof Release) {

                    // Make the release
                    Release release = (Release) FilterUtil.getComboBoxValue(releaseChoice);

                    // Assign the sprint release
                    sprint.setRelease(release);
                }
            }


            // If we have a selected backlog
            if (FilterUtil.getComboBoxValue(backlogChoice) != "None") {
                if (FilterUtil.getComboBoxValue(backlogChoice) instanceof Backlog) {

                    // Make the backlog
                    Backlog backlog = (Backlog) FilterUtil.getComboBoxValue(backlogChoice);

                    // Assign the sprint backlog
                    sprint.setBacklog(backlog);
                }
            }

            // If we have a selected team
            if (teamChoice.getSelectionModel().getSelectedItem() != "None") {
                if (teamChoice.getSelectionModel().getSelectedItem() instanceof Team) {

                    // Make the team
                    Team team = (Team) teamChoice.getSelectionModel().getSelectedItem();

                    // Assign the sprint team
                    sprint.setTeam(team);
                }
            }
        }
    }

    /**
     * Setter for date holder
     *
     * @param dateHolder - Time period date holder contains start and end dates
     */
    public void setDateHolder(TimePeriod dateHolder) {
        this.dateHolder = dateHolder;
    }

    /**
     * Brings up the date-picker dialog which gives the ability to pick a start and end date.
     * @param actionEvent
     */
    public void buttonSelectDates(ActionEvent actionEvent) {
        new Editor().SetDatesForSprint(dateHolder);
        // If dates have been selected
        if (dateHolder.getStartDate() != null && dateHolder.getEndDate() != null) {
            // Set the text of the button to what the dates are
            buttonSelectDates.setText(dateHolder.toString());
        }
    }

    /**
     * Updates the Available Stories List View
     */
    public void updateStories() {
        this.allAvailableStoriesListView.setCellFactory(makeCallListener());
        if (organisation != null) {
            for (Sprint sprinth : organisation.getSprints()) {
                for (Story story : sprinth.getStories()) {
                    if(sprinth != sprint){
                        if (allAvailableStoriesList.contains(story)) {
                            allAvailableStoriesList.remove(story);
                        }
                    }

                }
            }
        }
        allAvailableStoriesFilterListView.setData(allAvailableStoriesList);
        allCurrentStoriesFilterListView.setData(allCurrentStoriesList);
    }

    /**
     * Moves a story from available to current
     * @param actionEvent
     */
    public void buttonAddStory(ActionEvent actionEvent) {

        //the selected stories
        Collection<Story> currentStories =
                allAvailableStoriesListView.getSelectionModel().getSelectedItems();

        boolean allStoriesReady = true;

        for (Story story : currentStories) {
            if (!story.isReady()) {
                allStoriesReady = false; // if any selected story is not ready, allStoriesReady will be false
            }
        }

        if (allStoriesReady) {
            if (currentStories != null) {
                Integer index = allCurrentStoriesListView.getSelectionModel().getSelectedIndex() + 1;
                if (index > 0) {
                    allCurrentStoriesList.addAll(index, currentStories);
                } else {
                    allCurrentStoriesList.addAll(currentStories);
                }
                allAvailableStoriesList.removeAll(currentStories);

            }
        } else {
            addingStoryNotReadyErrorLabel.setText("Story/stories must be ready to be added to a sprint");

        }
        updateStories();
        currentStoriesTextField.setText("");
        availableStoriesTextField.setText("");
    }

    /**
     * Moves the story from current to available
     * @param actionEvent
     */
    public void buttonRemoveStory(ActionEvent actionEvent) {
        Collection<Story> currentStories =
                allCurrentStoriesListView.getSelectionModel().getSelectedItems();
        if (currentStories != null) {

            allAvailableStoriesList.addAll(currentStories);
            allAvailableStoriesFilterListView.setData(allAvailableStoriesList);
            allCurrentStoriesList.removeAll(currentStories);
        }
        updateStories();
        currentStoriesTextField.setText("");
        availableStoriesTextField.setText("");
    }


    //

    public void buttonNewTask(ActionEvent actionEvent) {
        Task newTask = new Task("","",0);
        // The okay button was clicked
        if (makeEditor().CreateTask(newTask, organisation)) {
            // add to the current stories list
            sprintTasksListView.getItems().add(newTask);
        }
    }

    /**
     * used to get a new editor so that it can be mocked with Mockito
     * @return
     */
    public Editor makeEditor(){
        return new Editor();
    }

    public void buttonDeleteTask(ActionEvent actionEvent) {
        if(sprintTasksListView.getSelectionModel().getSelectedItem() instanceof Task) {
            sprintTasksListView
                .getItems().remove(sprintTasksListView.getSelectionModel().getSelectedItem());
        }
    }

    public void buttonEditTask(ActionEvent actionEvent) {
        if(sprintTasksListView.getSelectionModel().getSelectedItem() instanceof Task){
            Task task = sprintTasksListView.getSelectionModel().getSelectedItem();
            //acceptanceCriteriaListView.getItems().remove(selectedAc);
            new Editor().CreateTask(task, organisation);
            // add to the current stories list
            //acceptanceCriteriaListView.getItems().add(selectedAc);
            ObservableList<Task> tempList = sprintTasksListView.getItems();
            sprintTasksListView.setItems(null);
            sprintTasksListView.setItems(tempList);
            sprintTasksListView.getSelectionModel().selectFirst();
        }


    }

    //





    /**
     * Returns the call listener - implemented for bug fixing purposes
     * @return
     */
    private Callback<ListView<Story>, ListCell<Story>> makeCallListener() {
        return param -> {
            final ListCell<Story> cell = new ListCell<Story>() {
                @Override protected void updateItem(Story item, boolean b) {
                    super.updateItem(item, b);
                    if (item != null) {
                        setText(item.toString());
                        String position = "";
                        if (!item.isReady()) {
                            position = "nonValid";
                            this.getStyleClass().add(position);
                        } else {
                            this.getStyleClass().remove(position);
                        }
                    } else {
                        setText(null);
                        this.getStyleClass().remove("nonValid");
                        this.setGraphic(null);
                    }

                    if (allCurrentStoriesList.contains(item)) {
                        setText(item.toString());
                    }

                }
            };

            return cell;
        };
    }
}
