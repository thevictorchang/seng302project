package seng302.group3.model.io.property_sheets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import seng302.group3.model.*;
import seng302.group3.model.gui.AcceptanceCriteriaListCell;
import seng302.group3.model.io.Editor;
import seng302.group3.model.search.FilterUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * Created by ntr24 on 24/04/15.
 */
public class StorySheet implements EditorSheet {

    private static final String errorStyle = "errorTextField";
    private static final String comboErrorStyle = "errorBox";
    @FXML public MenuButton dependencySelectMenu;

    private VBox content = new VBox();

    private Organisation organisation;

    private Story story;

    @FXML public TextField shortNameTextField;
    @FXML public TextField fullNameTextField;
    @FXML public TextField descriptionTextField;
    @FXML public Label errorLabel;

    @FXML public ComboBox personComboBox;
    @FXML public ComboBox backlogComboBox;
    @FXML public ComboBox estimateComboBox;

    @FXML public CheckBox readyCheckBox;
    @FXML public Label readyLabel;

    @FXML public Button buttonDeleteAC;
    @FXML public Button buttonDeleteTask;
    @FXML public Button buttonNewTask;

    @FXML public ListView<AcceptanceCriteria> acceptanceCriteriaListView;

    @FXML public ListView<Story> dependencyListView;

    @FXML public ListView<Task> storyTasksListView;

    private EventHandler changeListener = event -> checkReady();

    // this variable can be set to add additional stories that we want the short name uniqueness checked against
    // ie in the backlog dialog we use this so the newly added stories can be checked against
    Collection<Story> additionalUniqueCheck;

    private String previousShortName = null;
    private Story editingStory = null;
    private Boolean isFromBacklog = false;
    private BacklogSheet backSheet;

    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public StorySheet(Organisation organisation) {

        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/StorySheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);

            FilterUtil.autoCompleteComboBox(personComboBox, FilterUtil.AutoCompleteMode.CONTAINING);
            personComboBox.getItems().addAll(organisation.getPeople());
            personComboBox.getItems().add(0, "None");
            personComboBox.getSelectionModel().select("None");

            FilterUtil.autoCompleteComboBox(backlogComboBox, FilterUtil.AutoCompleteMode.CONTAINING);
            backlogComboBox.getItems()
                .addAll(organisation.getBacklogs());
            backlogComboBox.getItems().add(0, "None");
            backlogComboBox.getSelectionModel().select("None");
            backlogComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                enableEstimate();
                checkReady();
            });
            estimateComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                checkReady();
            });

            estimateComboBox.getSelectionModel().select("-");
            estimateComboBox.setDisable(true);

            acceptanceCriteriaListView.getSelectionModel().select(null);
            acceptanceCriteriaListView.setCellFactory(param -> new AcceptanceCriteriaListCell());
            dependencyListView.getSelectionModel().select(null);

            readyCheckBox.setDisable(true);
            readyCheckBox.setAllowIndeterminate(false);
            readyCheckBox.setIndeterminate(false);

            shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                checkReady();
            });

            this.organisation = organisation;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method checks if there is a backlog set and if there are some Acceptance Criteria. If there is then the estimate
     * combo box can be set
     */
    private void enableEstimate(){
        if((FilterUtil.getComboBoxValue(backlogComboBox) != null)&&!(FilterUtil.getComboBoxValue(backlogComboBox).equals("None"))){
            if(acceptanceCriteriaListView.getItems().size() > 0){
                estimateComboBox.setDisable(false);
                String scale = ((Backlog) FilterUtil.getComboBoxValue(backlogComboBox)).getScale();
                if(scale == null) scale = BacklogSheet.FIBONACCI;
                if(isFromBacklog){
                    scale = (String) backSheet.scaleComboBox.getSelectionModel().getSelectedItem();
                }

                Map<String, Double> scaleMap;

                double value = -1.0;
                if(editingStory != null)
                    value = editingStory.getEstimate();


                switch(scale){
                    case BacklogSheet.FIBONACCI:
                        scaleMap = BacklogSheet.fibonacci;
                        estimateComboBox.setItems(FXCollections.observableArrayList(BacklogSheet.fibonacci.keySet()));
                        break;
                    case BacklogSheet.TSHIRTS:
                        scaleMap = BacklogSheet.tShirts;
                        estimateComboBox.setItems(FXCollections.observableArrayList(BacklogSheet.tShirts.keySet()));
                        break;
                    case BacklogSheet.NATURAL:
                        scaleMap = BacklogSheet.natural;
                        estimateComboBox.setItems(FXCollections.observableArrayList(BacklogSheet.natural.keySet()));
                        break;
                    default:
                        scaleMap = BacklogSheet.fibonacci;
                        estimateComboBox.setItems(FXCollections.observableArrayList(BacklogSheet.fibonacci.keySet()));
                        break;
                }

                estimateComboBox.getSelectionModel().select(getScaleValue(value, scaleMap));

            }else{
                estimateComboBox.setDisable(true);
            }
        }
        else{
            estimateComboBox.setDisable(true);
        }
    }

    /**
     * On editing a story this populates the fields with the existing information.
     * @param object
     */
    @Override public void fieldConstruct(Object object) {
        if (object instanceof Story) {
            story = (Story) object;
            editingStory = story;

            shortNameTextField.setText(story.getShortName());
            previousShortName = story.getShortName();
            fullNameTextField.setText(story.getFullName());
            descriptionTextField.setText(story.getDescription());

            if (story.getCreatorPerson() != null) {
                personComboBox.getSelectionModel().select(story.getCreatorPerson());
                personComboBox.setDisable(true);
            }

            if (story.getBacklog() != null) {
                backlogComboBox.getSelectionModel().select(story.getBacklog());
            }
            acceptanceCriteriaListView.getItems().addAll(story.getAcceptanceCriteria());
            dependencyListView.getItems().addAll(story.getDependencies());
            storyTasksListView.getItems().addAll(story.getTasks());

            checkReady();
            enableEstimate();
            readyCheckBox.setSelected(story.isReady());



        }

    }

    /**
     * given the stories value and the scale we find the value in the scale for the story
     * @param value - double value for the estimate
     * @param scale - map of the scale we are using
     * @return String repr for the estimate using the backlogs scale
     */
    private String getScaleValue(double value, Map<String, Double> scale){
        Iterator it = scale.entrySet().iterator();
        Double closest = 0.0;
        String key = null;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            if(abs((Double) pair.getValue() - value) <= abs(closest - value)){
                closest = (Double) pair.getValue();
                key = (String) pair.getKey();
            }
        }
        return key;
    }

    /**
     * when we call the new story dialog from the backlog dialog we want to set the backlog selection to the new backlog
     * and disable the dropdown
     */
    public void setCalledFromBacklog(Backlog backlog, BacklogSheet bSheet) {
        backlogComboBox.getSelectionModel().select(backlog);
        backlogComboBox.setDisable(true);
        isFromBacklog = true;
        backSheet = bSheet;
    }

    /**
     * Gives the VBox which contains the dialogs parts.
     * @return
     */
    @Override public VBox draw() {
        return content;
    }

    /**
     * Checks the fields to see if the inputs are considered bad.
     * @param args
     * @return
     */
    @Override public boolean validate(Object... args) {
        boolean result = true;

        shortNameTextField.setText(shortNameTextField.getText().trim());
        fullNameTextField.setText(fullNameTextField.getText().trim());
        descriptionTextField.setText(descriptionTextField.getText().trim());

        if (shortNameTextField.getText().length() < 1) {
            result = false;
            shortNameTextField.getStyleClass().add(errorStyle);
        } else
            shortNameTextField.getStyleClass().removeAll(errorStyle);

        if (fullNameTextField.getText().length() < 1) {
            result = false;
            fullNameTextField.getStyleClass().add(errorStyle);
        } else
            fullNameTextField.getStyleClass().removeAll(errorStyle);

        if (descriptionTextField.getText().length() < 1) {
            result = false;
            descriptionTextField.getStyleClass().add(errorStyle);
        } else
            descriptionTextField.getStyleClass().removeAll(errorStyle);

        // if there is a null text error
        if (!result) {
            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        } else {
            if (personComboBox.getSelectionModel().getSelectedItem().equals("None")) {
                result = false;
                personComboBox.getStyleClass().add(comboErrorStyle);
            } else {
                personComboBox.getStyleClass().removeAll(comboErrorStyle);
            }
        }

        if (!result) {
            errorLabel.setText("Must assign a person to this story");
            return result;
        } else {
            result = uniquenessCheck();
        }

        // if there is a non unique short name error
        if (!result)
            errorLabel.setText("Short Name is not unique");

        return result;
    }

    /**
     * Pushes out the data from the fields inside of the dialog.
     * @param object
     */
    @Override public void apply(Object object) {
        if (object instanceof Story) {
            Story story = (Story) object;

            story.setShortName(shortNameTextField.getText());
            story.setFullName(fullNameTextField.getText());
            story.setDescription(descriptionTextField.getText());
            Person person = (Person) FilterUtil.getComboBoxValue(personComboBox);
            story.setCreatorPerson(person);
            story.setAcceptanceCriteria(acceptanceCriteriaListView.getItems());
            story.setDependencies(dependencyListView.getItems());

            for (Task task : storyTasksListView.getItems()){
                task.setObjectType(story);
            }
            story.setTasks(storyTasksListView.getItems());
            story.setReady(readyCheckBox.isSelected());

            Object backlogObj = FilterUtil.getComboBoxValue(backlogComboBox);

            String estimate = (String) estimateComboBox.getSelectionModel().getSelectedItem().toString();

            if (backlogObj instanceof Backlog) {
                Backlog backlog = (Backlog) backlogObj;

                // if the backlog previously had a backlog thats different from the new one,
                // remove the story from the old back log
                if (story.getBacklog() != null && story.getBacklog() != backlog) {
                    story.getBacklog().getStories().remove(story);
                    backlog.addStory(story);
                } else if (story.getBacklog() == null) {
                    backlog.addStory(story);
                }

                // if the backlog is changing
                if(story.getBacklog() != backlog){
                    // the priority of the story is the last in the list
                    story.setPriority(backlog.getStories().size() - 1);
                }

                story.setBacklog(backlog);

                // depending on the scale the back log is using we need to set the estimate value of the story
                switch (backlog.getScale()){
                    case BacklogSheet.FIBONACCI:
                        story.setEstimate(BacklogSheet.fibonacci.get(estimate));
                        break;
                    case BacklogSheet.TSHIRTS:
                        story.setEstimate(BacklogSheet.tShirts.get(estimate));
                        break;
                    case BacklogSheet.NATURAL:
                        story.setEstimate(BacklogSheet.natural.get(estimate));
                        break;
                    default:
                        story.setEstimate(BacklogSheet.fibonacci.get(estimate));
                        break;
                }

            } else {
                story.getDependencies().clear();
                if (story.getBacklog() != null) {
                    story.getBacklog().getStories().remove(story);

                    // if the story had a backlog remove the story from that backlog
                    for(Story s : story.getBacklog().getStories()){
                        if(s.getDependencies().contains(story)) {
                            s.removeDependency(story);
                        }
                    }
                }
                story.setBacklog(null);

            }
        }
    }

    /**
     * Checks the uniqueness of the shortname.
     * @return
     */
    private boolean uniquenessCheck() {
        // if the shortname isnt unique and has changed from the previous
        if (shortNameTextField.getText().equals(previousShortName) || organisation
            .uniqueName(shortNameTextField.getText(), Organisation.uniqueType.UNIQUE_STORY)) {

            // now check if there is additional check required
            if (this.additionalUniqueCheck != null) {
                for (Story story : additionalUniqueCheck) {
                    if (story.getShortName().equals(shortNameTextField.getText())) {
                        shortNameTextField.getStyleClass().add(errorStyle);
                        return false;
                    }
                }
            }

            shortNameTextField.getStyleClass().removeAll(errorStyle);
            return true;
        }

        shortNameTextField.getStyleClass().add(errorStyle);

        return false;
    }

    /**
     * If we may need to check that the uniqueness will not pick up on itself.
     * @param additionalUniqueCheck
     */
    public void setAdditionalUniqueCheck(Collection<Story> additionalUniqueCheck) {
        this.additionalUniqueCheck = additionalUniqueCheck;
    }

    /**
     * Check that the story is able to be made into a ready state.
     */
    private void checkReady() {
        //if (estimateComboBox.getSelectionModel().getSelectedItem() != null) {
        if (estimateComboBox.getSelectionModel().getSelectedItem().toString().trim() == "-") {

            //readyLabel.setTooltip(new Tooltip("No acceptance criteria"));
            readyCheckBox.setSelected(false);
            readyCheckBox.setDisable(true);
            //}
        } else if (acceptanceCriteriaListView.getItems().size() < 1) {
        //readyLabel.setTooltip(new Tooltip("No acceptance criteria"));
        readyCheckBox.setSelected(false);
        readyCheckBox.setDisable(true);
        }
        else if (FilterUtil.getComboBoxValue(backlogComboBox) == "None") {
            //readyLabel.setTooltip(new Tooltip("No backlog selected"));
            readyCheckBox.setSelected(false);
            readyCheckBox.setDisable(true);
        }
        else if (FilterUtil.getComboBoxValue(personComboBox) == "None") {
            //readyLabel.setTooltip(new Tooltip("No person selected"));
            readyCheckBox.setSelected(false);
            readyCheckBox.setDisable(true);
        }
        else if (shortNameTextField.getText() == null || shortNameTextField.getText().toString().trim().length() < 1) {
            //readyLabel.setTooltip(new Tooltip("No short name"));
            readyCheckBox.setSelected(false);
            readyCheckBox.setDisable(true);
        }
        else {
            //readyLabel.setTooltip(new Tooltip("Click to mark as ready"));
            //readyCheckBox.setTooltip(new Tooltip("Click to mark as ready"));
            readyCheckBox.setDisable(false);
        }
    }

    /**
     * An action event from a button which will create a new acceptance-criteria dialog.
     * @param actionEvent
     */
    public void buttonNewAC(ActionEvent actionEvent) {
        AcceptanceCriteria newAc = new AcceptanceCriteria();

        // The okay button was clicked
        if (makeEditor().CreateACFromStory(newAc) == true) {
            // add to the current stories list
            acceptanceCriteriaListView.getItems().add(newAc);
            checkReady();
            enableEstimate();
        }
    }

    /**
     * An action event from a button which will create a new dependency dialog dialog.
     * @param actionEvent
     */
    public void buttonNewDependency(ActionEvent actionEvent) {
        if(isFromBacklog == true)
            new Editor().CreateDependencyFromStoryFromBacklog(story, this, backSheet);
        else
            new Editor().CreateDependencyFromStory(story, this);
    }

    /**
     * Will bring up the acceptance criteria dialog of an already created AC.
     * @param actionEvent
     */
    public void buttonEditAC(ActionEvent actionEvent) {
        if(acceptanceCriteriaListView.getSelectionModel().getSelectedItem() instanceof AcceptanceCriteria){
            AcceptanceCriteria selectedAc = acceptanceCriteriaListView.getSelectionModel().getSelectedItem();
            //acceptanceCriteriaListView.getItems().remove(selectedAc);
            new Editor().CreateACFromStory(selectedAc);
            // add to the current stories list
            //acceptanceCriteriaListView.getItems().add(selectedAc);
            ObservableList<AcceptanceCriteria> tempList = acceptanceCriteriaListView.getItems();
            acceptanceCriteriaListView.setItems(null);
            acceptanceCriteriaListView.setItems(tempList);
            acceptanceCriteriaListView.getSelectionModel().selectFirst();
        }


    }

    /**
     * Will remove the acceptance criteria from the story.
     * @param actionEvent
     */
    public void buttonDeleteAC(ActionEvent actionEvent) {
        if(acceptanceCriteriaListView.getSelectionModel().getSelectedItem() instanceof AcceptanceCriteria) {
            acceptanceCriteriaListView
                .getItems().remove(acceptanceCriteriaListView.getSelectionModel().getSelectedItem());
            enableEstimate();
            checkReady();
        }
    }

    /**
     * Will check all fields to see if the differ from what was the initial values from an add operation.
     * @return
     */
    public boolean hasChangedOnAdd() {
        boolean changed = false;
        if(!shortNameTextField.getText().trim().equals("")){
            changed = true;
        }
        else if(!fullNameTextField.getText().trim().equals("")){
            changed = true;
        }
        else if(!descriptionTextField.getText().trim().equals("")){
            changed = true;
        }
        else if(!FilterUtil.getComboBoxValue(personComboBox).equals("None")){
            changed = true;
        }
        else if(!FilterUtil.getComboBoxValue(backlogComboBox).equals("None")){
            changed = true;
        }
        else if(acceptanceCriteriaListView.getItems().size() > 0){
            changed = true;
        }
        return changed;
    }

    /**
     * Will check all fields to see if the differ from what was the initial values from an edit operation.
     * @return
     */
    public boolean hasChangedOnEdit(){
        boolean changed = false;
        if(!shortNameTextField.getText().trim().equals(editingStory.getShortName())){
            changed = true;
        }
        else if(!fullNameTextField.getText().trim().equals(editingStory.getFullName())){
            changed = true;
        }
        else if(!descriptionTextField.getText().trim().equals(editingStory.getDescription())){
            changed = true;
        }
        else if(!acceptanceCriteriaListView.getItems().equals(editingStory.getAcceptanceCriteria())){
            changed = true;
        }

        return changed;
    }

    /**
     * Returns all of the dependencies.
     * @return
     */
    public ListView<Story> getDependencyListView() {
        return dependencyListView;
    }

    /**
     * Is the action event made from pressing the delete button.
     * @param actionEvent
     */
    public void buttonDeleteDependency(ActionEvent actionEvent) {
        if(dependencyListView.getSelectionModel().getSelectedItem() instanceof Story){
            dependencyListView.getItems().remove(dependencyListView.getSelectionModel().getSelectedItem());
        }
    }

    public void buttonNewTask(ActionEvent actionEvent) {
        Task newTask = new Task("","",0);
        // The okay button was clicked
        if (makeEditor().CreateTask(newTask, organisation)) {
            // add to the current stories list
            storyTasksListView.getItems().add(newTask);
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
        if(storyTasksListView.getSelectionModel().getSelectedItem() instanceof Task) {
            storyTasksListView
                    .getItems().remove(storyTasksListView.getSelectionModel().getSelectedItem());
        }
    }

    public void buttonEditTask(ActionEvent actionEvent) {
        if(storyTasksListView.getSelectionModel().getSelectedItem() instanceof Task){
            Task task = storyTasksListView.getSelectionModel().getSelectedItem();
            //acceptanceCriteriaListView.getItems().remove(selectedAc);
            new Editor().CreateTask(task, organisation);
            // add to the current stories list
            //acceptanceCriteriaListView.getItems().add(selectedAc);
            ObservableList<Task> tempList = storyTasksListView.getItems();
            storyTasksListView.setItems(null);
            storyTasksListView.setItems(tempList);
            storyTasksListView.getSelectionModel().selectFirst();
        }
    }





}


