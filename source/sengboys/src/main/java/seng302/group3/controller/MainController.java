package seng302.group3.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.group3.model.Person;
import javafx.util.Pair;
import seng302.group3.model.History.Change;
import seng302.group3.model.Person;
import seng302.group3.model.History.History;
import seng302.group3.model.Project;
import seng302.group3.model.io.Editor;
import seng302.group3.model.io.Loader;
import seng302.group3.model.io.Saver;
import seng302.group3.model.io.property_sheets.ProjectProperties;

import javax.imageio.ImageIO;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by ntr24 on 12/03/15.
 */
public class MainController implements Initializable {


    @FXML private Label outputLine;
    @FXML private TextArea bodyText;
    @FXML private Label personText;
    @FXML private VBox personInfo;
    @FXML private MenuItem addPerson;
    @FXML private CheckMenuItem Team;
    @FXML private VBox teamList;
    @FXML private ListView<Person> allPeopleListView;
    @FXML private VBox personList;
    @FXML private Label personLabel;

    @FXML private MenuItem editProjectMenuItem;
    public Project currentProject;

    @FXML private MenuItem undoMenuItem;
    @FXML private MenuItem redoMenuItem;

    @FXML private MenuItem menuBarSaveButton;
    @FXML private MenuItem menuBarSaveAsButton;
    @FXML private MenuItem menuBarLoadButton;

    final ObservableList<String> listItems = FXCollections.observableArrayList();
    final ObservableList<Person> allPeopleList = FXCollections.observableArrayList();

    private Stage stage;
    private boolean listToggle = true;

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    /**
     * When the 'Exit' item is selected from the 'File' menu bar drop down the program will close
     * @param event - the action event
     */
    public void menuBarExitButton(ActionEvent event) {
        //this is the code for a 'are you sure' popup.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit SCRUMPY");
        alert.setHeaderText("Exit SCRUMPY");
        alert.setContentText("Are you sure you want to quit SCRUMPY?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Platform.exit();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
    /**
     * When the 'Undo' item is selected from the 'Edit' drop down menu, the last change is undone.
     * @param actionEvent - the action eventWhen the 'Edit Project' item is selected from the 'File' menu bar drop down a property sheet
     * which can be used to edit the project will appear
     * @param actionEvent - the action event
     */
    @FXML public void undoMenuItem(ActionEvent actionEvent){
        Change change = this.currentProject.getCurrentChange();
        this.printInfo("Undid " + change.toString());
        this.currentProject = History.undo(this.currentProject);
        refreshView();
    }

    /**
     * When the 'Redo' item is selected from the 'Edit' drop down menu, the last undone change is redone.
     * @param actionEvent - the action event
     */
    @FXML public void redoMenuItem(ActionEvent actionEvent){
        Change change = this.currentProject.getCurrentChange();
        this.printInfo("Redid " + change.toString());
        this.currentProject = History.redo(this.currentProject);
        refreshView();
    }

    /**
     * Refreshes the view, at this stage only refreshing the people list.
     *
     */
    private void refreshView() {
        updatePeopleList();

        Person selectPerson = this.allPeopleListView.getSelectionModel().getSelectedItem();
        if(selectPerson != null){
            personText.setText("\n"+"UserID: "+"\n"+selectPerson.getUserID()+"\n"+"Full Name: "+"\n"+selectPerson.getFullName()+"\n"+"Short Name: "+"\n"+selectPerson.getNameShort());
        }else{
            personText.setText("No Person selected");
        }

    }

    @FXML public void menuBarListToggle(ActionEvent actionEvent) {
        toggleTeamMenuItem();
    }

    /**
     * When the menu item Display - team is selected it will toggle the Team view in the main window.
     *
     */
    private void toggleTeamMenuItem(){
        if(currentProject != null)
            updatePeopleList();
        teamList.managedProperty().bind(teamList.visibleProperty());
        teamList.setVisible(Team.isSelected());


    }
    /**
     * Clears the GUI people list and repopulates it with the people from the project.
     *
     */
    private void updatePeopleList(){
        allPeopleList.clear();
        allPeopleList.addAll(currentProject.getPeople());
    }

    /**
     * When the 'Save' item is selected from the 'File' menu bar drop down the project will be serialized
     * @param event - the action event
     */
    @FXML public void menuBarSaveButton(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Project");
        alert.setHeaderText("Saving Project");
        alert.setContentText("Are you sure you wish to save?");
        Optional<ButtonType> result = alert.showAndWait();
        String filename = currentProject.getFilename();
        if (result.get() == ButtonType.OK){
            Saver.save(Saver.getSavePath().toAbsolutePath().toString()+'/'+filename, currentProject);//new Project("hello", "hello world", "testing"));
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * When the 'Save As...' item is selected from the 'File' menu bar drop down the project will be serialized with
     *  path specified by filechooser
     * @param event - the action event
     */
     @FXML public void menuBarSaveAsButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project");
        fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scrumpy Project File", "*.proj"));
        File file = fileChooser.showSaveDialog(this.stage);
        if (file != null) {
            currentProject.setFilename(file.toPath().getFileName().toString());
            Saver.save(file.getAbsolutePath(), currentProject);//new Project("hello", "hello world", "testing"));
            menuBarSaveButton.setDisable(false);
        }
    }

    /**
     * When the 'Load' item is selected from the 'File' menu bar drop down the project will be serialized
     * @param event - the action event
     */
    @FXML public void menuBarLoadButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Project");
        fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            currentProject = Loader.load(file.getAbsolutePath());
            if (currentProject != null) {
                menuBarSaveButton.setDisable(false);
                menuBarSaveAsButton.setDisable(false);

                editProjectMenuItem.setDisable(false);
                undoMenuItem.setDisable(false);
                redoMenuItem.setDisable(false);

                addPerson.setDisable(false);
                refreshView();
            }
        }
    }

    /**
     * Initialises the states of menu items and performs other set up.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allPeopleListView.setItems(allPeopleList);
        Team.setSelected(true);

        editProjectMenuItem.setDisable(true);
        undoMenuItem.setDisable(true);
        redoMenuItem.setDisable(true);

        menuBarSaveButton.setDisable(true);
        menuBarSaveAsButton.setDisable(true);

        addPerson.setDisable(true);


        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));

        allPeopleListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Person>() {
            /**
             * When a new person is selected on the list of people, the value in the
             * text view to the right od the list changes to represent the selected person.
             * @param observable
             * @param oldValue - the old selected value
             * @param newValue - the new selected value
             */
            @Override
            public void changed(ObservableValue<? extends Person> observable, Person oldValue, Person newValue) {
                // Your action here
                if (newValue != null){

                    personText.setText("\n"+"UserID: "+"\n"+newValue.getUserID()+"\n"+"Full Name: "+"\n"+newValue.getFullName()+"\n"+"Short Name: "+"\n"+newValue.getNameShort());
                }

            }
        });
    }

    public void printInfo(String info){
        outputLine.setText(info);
    }


    /**
     * When the 'Edit Project' item is selected from the 'File' menu bar drop down a property sheet
     * which can be used to edit the project will appear
     * @param actionEvent - the action event
     */
    public void menuBarEditButton(ActionEvent actionEvent) {
        Project editedProject = (Project) Editor.createPropertySheet(Editor.editorType.PROJECT, currentProject);
        if(editedProject != null){
            currentProject = editedProject;
        }

    }

    /**
     * When the 'Project' item is selected from the 'File' menu bar drop down and the 'New' sub menu,
     * a property sheet which can be used to create and edit the project will appear
     * @param actionEvent - the action event
     */
    public void menuBarNewProjectButton(ActionEvent actionEvent) {
        Project newProject = (Project) Editor.createPropertySheet(Editor.editorType.PROJECT, new Project());
        if(newProject != null){
            currentProject = newProject;

            editProjectMenuItem.setDisable(false);
            undoMenuItem.setDisable(false);
            redoMenuItem.setDisable(false);

            addPerson.setDisable(false);
            refreshView();

            menuBarSaveAsButton.setDisable(false);
        }

    }

    /**
     * When the 'Person' item is selected from the 'Add' menu bar drop down a property sheet which can
     * be used to create and edit the person, and add it to the project will appear
     * @param event - the action event
     */
    public void menuBarNewPersonButton(ActionEvent event) {
        if(this.currentProject != null){
            int sizeBefore = this.currentProject.getPeople().size();

            Person newPerson = (Person) Editor.createPropertySheet(Editor.editorType.PERSON, new Person(""));
            if(newPerson != null){
                Change lastState = new Change(Change.changeType.EDIT_PERSON, this.currentProject, newPerson);

                this.currentProject.addPerson(newPerson);
                if(sizeBefore < this.currentProject.getPeople().size()){
                    if(this.currentProject.getCurrentChange().getType() != Change.changeType.EDIT_PERSON){
                        this.currentProject.addChange(lastState);
                    }
                    this.currentProject.addChange(new Change(Change.changeType.EDIT_PERSON, this.currentProject, newPerson));
                    this.updatePeopleList();
                }
            }

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Project Error");
            alert.setHeaderText("Cannot make a person");
            alert.setContentText("No project loaded.");

            alert.showAndWait();
        }

    }
    /**
     * A sub menu of the 'File' drop down menu.
     */
    public void menuBarNewButton(ActionEvent actionEvent) {

    }

    public void menuBarEditPersonButton(ActionEvent actionEvent) {
        if(this.currentProject != null && this.allPeopleListView.getSelectionModel().getSelectedItem() != null){
            Person toEdit = this.allPeopleListView.getSelectionModel().getSelectedItem();
            Change lastState = new Change(Change.changeType.EDIT_PERSON, this.currentProject, toEdit);

            Person result = (Person) Editor.createPropertySheet(Editor.editorType.PERSON, new Person(toEdit));
            if(result != null){
                this.currentProject.removePerson(toEdit);
                this.currentProject.addPerson(result);
                if(this.currentProject.getCurrentChange().getType() != Change.changeType.EDIT_PERSON){
                    this.currentProject.addChange(lastState);
                }
                this.currentProject.addChange(new Change(Change.changeType.EDIT_PERSON, this.currentProject, result));
                this.updatePeopleList();
            }

        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Project Error");
            alert.setHeaderText("Cannot make a person");
            alert.setContentText("No project loaded.");

            alert.showAndWait();
        }
    }
}
