package seng302.group3.model.io.property_sheets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng302.group3.model.*;
import seng302.group3.model.gui.TimeLogRow;
import seng302.group3.model.search.FilterUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * Created by Eddy on 31-Jul-15.
 */
public class TimeLogSheet implements EditorSheet{

    @FXML public Label errorLabel;
    @FXML public ListView<TimeLogRow> timeScrollList;
    @FXML public HBox labels;
    @FXML public HBox newTimeLogHBox;
    private Task task;
    private Organisation organisation;
    private VBox content = new VBox();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private DecimalFormat f = new DecimalFormat("##.0");

    /**
     * If we have a selected task in the navigator we need no choice boxes so they are disabled.
     * Initialises the JavaFx elements in a method call.
     * @param currentlySelected
     * @param organisation
     */
    public TimeLogSheet(Task currentlySelected, Organisation organisation) {
        this.task = currentlySelected;
        this.organisation = organisation;
        initDialog();

        labels.setPadding(new Insets(0,0,0,10));
        newTimeLogHBox.setPadding(new Insets(0, 20, 0, 0));

        for(LoggedTime l:task.getLoggedHours()){
            timeScrollList.getItems().add(makeTimeLogRow(l));
        }
        newTimeLogHBox.getChildren().add(makeTimeLogRow(null).getHBox());
        timeScrollList.setCellFactory(param -> new formatListHBoxRow());
    }

    /**
     * If there is no selection we must provide combo boxes so the user can log time against a task.
     * Adds listener to the choicebox to see when to activate the task box.
     * Initialises the JavaFx elements in a method call.
     * @param organisation
     */
    public TimeLogSheet(Organisation organisation) {
        this.organisation = organisation;
        initDialog();
    }

    /**
     * Gets the team in a sprint for which a task is part of due to the hierarchy.
     * @param organisation
     * @return
     */
    public Team getSprintTeamFromStory(Organisation organisation){
        Team team = null;
        for(Sprint sprint:organisation.getSprints()){
            for(Story s:sprint.getStories()){

                if(s.equals(task.getObjectType())){
                    team = sprint.getTeam();
                }
            }
        }
        return team;
    }

    public Collection<Person> getSprintPeople(){
        return organisation.getPeople();
    }


    /**
     * This initalises the JavaFx in the FXML sheet and sets the initial values for the elements.
     * Adds a listener for the duration box which limits it to being an integer value.
     */
    private void initDialog(){
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/EditorSheets/TimeLogSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a given string for a know format of time and then gives it back as a string of minutes.
     * @param hr
     * @return
     */
    public String parseTime(String hr) {
        int k=-1;
        String mins = "";
        try{
            k = Integer.parseInt(hr);
        }
        catch (Exception e){}
        if(!(k > -1)){
            if (hr.matches("(\\d+[h])")){
                mins = hr.replaceAll("([h])", "");
                mins = ""+(Integer.parseInt(mins)*60);
            } else if (hr.matches("(\\d+[h])(\\d\\d?[m])")){
                mins = hr.replaceAll("([m])", "");
                int i = Integer.parseInt(mins.substring(0,mins.lastIndexOf("h")))*60;
                int j = Integer.parseInt(mins.substring(mins.lastIndexOf("h")+1));
                mins = ""+(i+j);
            } else if(hr.matches("(\\d\\d?[m])")){
                mins = hr.replaceAll("([m])","");
            } else if(hr.matches("(\\d([0-9]+)?(\\.\\d)?)")){
                int i = Integer.parseInt(hr.substring(0,hr.lastIndexOf(".")))*60;
                int j = Integer.parseInt(hr.substring(hr.lastIndexOf(".")+1));
                j = j * 6;
                i = i+j;
                mins = ""+(i);
            }
        }else{
            mins = ""+(k*60);
        }

        return mins;
    }

    /**
     * Builds the hbox which a logged time will be created and edited in. Sets listeners and actions
     * on a save or a delete.
     * @param loggedTime
     * @return
     */
    public TimeLogRow makeTimeLogRow (LoggedTime loggedTime){
        boolean saved = false;
        boolean fromSprint;

        if(task.getObjectType() instanceof Story){
            fromSprint = false;
        }else {
            fromSprint = true;
        }

        if (loggedTime == null){
            loggedTime = new LoggedTime(LocalDateTime.now(), LocalDateTime.now());
        }else{
            saved = true;
        }

        HBox hbox = new HBox();
        HBox hboxPartner = new HBox();
        hbox.setSpacing(2);
        TimeLogRow timeLogRow = new TimeLogRow(hbox,loggedTime);
        if(saved){
            timeLogRow.setSaved();
        }
        TimeLogRow timeLogRowPartner = new TimeLogRow(hbox, loggedTime);


        ComboBox personChoice = new ComboBox<>();
        FilterUtil.autoCompleteComboBox(personChoice, FilterUtil.AutoCompleteMode.CONTAINING);
        ComboBox partnerChoice = new ComboBox<>();
        FilterUtil.autoCompleteComboBox(partnerChoice, FilterUtil.AutoCompleteMode.CONTAINING);
        DatePicker endDatePicker = new DatePicker();
        TextField endTimeField = new TextField();
        TextField durationTextField = new TextField();
        TextField commentField = new TextField();
        Button saveButton = new Button();
        Button deleteButton = new Button();
        Person nonePerson = new Person("None");


        Collection<Person> people;
        Collection<Person> partners;

        if(!fromSprint){
            people = getSprintTeamFromStory(organisation).getPeople();
            partners = getSprintTeamFromStory(organisation).getPeople();
        }
        else {
            people = getSprintPeople();
        }
        personChoice.getItems().addAll(people);
        partnerChoice.getItems().addAll(people);


        if(loggedTime.getPerson() != null){
            personChoice.setValue(loggedTime.getPerson());
        }

        if ((loggedTime.getPairHours() != null) && (loggedTime.getPairHours().getPerson() != null)) {
            partnerChoice.setValue(loggedTime.getPairHours().getPerson());
        }

        endDatePicker.setValue(loggedTime.getEndTime().toLocalDate());
        endTimeField.setText(loggedTime.getEndTime().toLocalTime().format(formatter));
        if(loggedTime.getLoggedTimeInHours() > 0) {
            durationTextField.setText((f.format(loggedTime.getLoggedTimeInHours())));
        } else {
            durationTextField.setPromptText("eg '2h30m'");
        }

        if(loggedTime.getComment() != null){
            commentField.setText(loggedTime.getComment());
        }

        saveButton.setText("Save");
        deleteButton.setText("Delete");

        if(timeLogRow.isSaved()){
            saveButton.setVisible(false);
            saveButton.managedProperty().bind(saveButton.visibleProperty());
            deleteButton.setVisible(true);
            deleteButton.managedProperty().bind(deleteButton.visibleProperty());
            personChoice.setDisable(true);
            partnerChoice.setDisable(true);
            personChoice.setMaxWidth(100);
            partnerChoice.setMaxWidth(100);
            endDatePicker.setMaxWidth(125);
            endTimeField.setMaxWidth(60);
            durationTextField.setMaxWidth(90);
            commentField.setMaxWidth(165);
        }else{
            saveButton.setVisible(true);
            saveButton.managedProperty().bind(saveButton.visibleProperty());
            deleteButton.setVisible(false);
            deleteButton.managedProperty().bind(deleteButton.visibleProperty());
            personChoice.setMaxWidth(100);
            partnerChoice.setMaxWidth(100);
            endDatePicker.setMaxWidth(130);
            endTimeField.setMaxWidth(60);
            durationTextField.setMaxWidth(100);
            commentField.setMaxWidth(190);
        }

        personChoice.setId("person");
        partnerChoice.setId("partner");
        endDatePicker.setId("endDate");
        endTimeField.setId("endTime");
        durationTextField.setId("duration");
        commentField.setId("comment");
        saveButton.setId("save");
        deleteButton.setId("delete");

        durationTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                String newValue) {
                if (newValue.length() == 9) {
                    durationTextField.setText(durationTextField.getText()
                        .substring(0, durationTextField.getText().length() - 1));
                }
                timeLogRow.setUnsaved();
                saveButton.setVisible(true);
                saveButton.managedProperty().bind(saveButton.visibleProperty());
                deleteButton.setVisible(false);
                deleteButton.managedProperty().bind(deleteButton.visibleProperty());
            }
        });
        personChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (FilterUtil.getComboBoxValue(personChoice) instanceof Person) {
                timeLogRow.setUnsaved();
                saveButton.setVisible(true);
                saveButton.managedProperty().bind(saveButton.visibleProperty());
                deleteButton.setVisible(false);
                deleteButton.managedProperty().bind(deleteButton.visibleProperty());


                partnerChoice.getItems().setAll(people);
                partnerChoice.getItems().add(nonePerson);
                partnerChoice.getItems().remove(FilterUtil.getComboBoxValue(personChoice));
                partnerChoice.getSelectionModel().select(nonePerson);
            }
        });
        /*partnerChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            timeLogRow.setUnsaved();
            saveButton.setVisible(true);
            saveButton.managedProperty().bind(saveButton.visibleProperty());
            deleteButton.setVisible(false);
            deleteButton.managedProperty().bind(deleteButton.visibleProperty());
        });*/
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            timeLogRow.setUnsaved();
            saveButton.setVisible(true);
            saveButton.managedProperty().bind(saveButton.visibleProperty());
            deleteButton.setVisible(false);
            deleteButton.managedProperty().bind(deleteButton.visibleProperty());
        });
        endTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            timeLogRow.setUnsaved();
            saveButton.setVisible(true);
            saveButton.managedProperty().bind(saveButton.visibleProperty());
            deleteButton.setVisible(false);
            deleteButton.managedProperty().bind(deleteButton.visibleProperty());
        });
        commentField.textProperty().addListener((observable, oldValue, newValue) -> {
            timeLogRow.setUnsaved();
            saveButton.setVisible(true);
            saveButton.managedProperty().bind(saveButton.visibleProperty());
            deleteButton.setVisible(false);
            deleteButton.managedProperty().bind(deleteButton.visibleProperty());
        });

        saveButton.setOnAction((event) -> {
            if(validateHBox(personChoice,endTimeField,durationTextField,commentField)){
                timeLogRow.setSaved();
                boolean editing;
                if(!(timeScrollList.getItems().contains(timeLogRow))){
                    timeScrollList.getItems().add(timeLogRow);
                    newTimeLogHBox.getChildren().remove(hbox);
                    newTimeLogHBox.getChildren().add(makeTimeLogRow(null).getHBox());
                    editing = false;
                    personChoice.setMaxWidth(100);
                    partnerChoice.setMaxWidth(100);
                    endDatePicker.setMaxWidth(125);
                    endTimeField.setMaxWidth(60);
                    durationTextField.setMaxWidth(90);
                    commentField.setMaxWidth(165);
                }else{
                    editing = true;
                    hbox.getStyleClass().removeAll("errorBox");
                }

                personChoice.setDisable(true);
                partnerChoice.setDisable(true);

                saveLog(timeLogRow.getLoggedTime(),
                    ((Person) FilterUtil.getComboBoxValue(personChoice)), endDatePicker.getValue(),
                    endTimeField.getText(), durationTextField.getText(), commentField.getText(),
                    editing);
                saveButton.setVisible(false);
                saveButton.managedProperty().bind(saveButton.visibleProperty());
                deleteButton.setVisible(true);
                deleteButton.managedProperty().bind(deleteButton.visibleProperty());

                /*
                If we save with a partner we need to make a separate row with the partner.
                 */
                Person selectedPartner = (Person) FilterUtil.getComboBoxValue(partnerChoice);
                if (selectedPartner != nonePerson && selectedPartner != null) {
                    LoggedTime partnerLog = new LoggedTime(timeLogRow.getLoggedTime(), selectedPartner);
                    timeLogRow.getLoggedTime().setPairHours(partnerLog);
                    saveLog(partnerLog, selectedPartner, endDatePicker.getValue(), endTimeField.getText(), durationTextField.getText()
                    , commentField.getText(), editing);

                    timeScrollList.getItems().add(makeTimeLogRow(partnerLog));
                }
            }
        });
        deleteButton.setOnAction((event) -> {
            if(timeScrollList.getItems().contains(timeLogRow)){
                timeScrollList.getItems().remove(timeLogRow);

                LoggedTime time = timeLogRow.getLoggedTime();
                for(Task t:organisation.getTasks()){
                    if(t.getLoggedHours().contains(time)){
                        t.getLoggedHours().remove(time);
                        break;
                    }
                }
                time.getPerson().getLoggedTimes().remove(time);
                time.setPerson(null);

            }else{
                commentField.setText("");
                durationTextField.setText("");
                personChoice.setValue(null);
                endDatePicker.setValue(LocalDate.now());
                endTimeField.setText(LocalTime.now().format(formatter));
            }
        });

        hbox.getChildren().addAll(personChoice,partnerChoice, endDatePicker, endTimeField, durationTextField,
            commentField, saveButton, deleteButton);
        personChoice.getStyleClass().add("dialogCombo");
        partnerChoice.getStyleClass().add("dialogCombo");
        endDatePicker.getStyleClass().add("dialogCombo");
        saveButton.getStyleClass().add("dialogButton");
        deleteButton.getStyleClass().add("dialogButton");

        return timeLogRow;
    }



    /**
     * Takes the parts of the TimeLogRow and validates their fields to meet the criteria.
     * @param personChoice
     * @param endTimeField
     * @param durationTextField
     * @param commentField
     * @return
     */
    private boolean validateHBox(ComboBox<Person> personChoice,
        TextField endTimeField, TextField durationTextField, TextField commentField) {
        boolean result = true;

        String pattern = "(\\d\\d\\:\\d\\d)";
        if(!endTimeField.getText().matches(pattern)){
            result = false;
            endTimeField.getStyleClass().add("errorBox");
        }else{
            endTimeField.getStyleClass().removeAll("errorBox");
        }

        String durationPattern =
            "(\\d+[h])(\\d\\d?[m])|(\\d\\d?[m])|(\\d+[h])|(\\d([0-9]+)?(\\.\\d)?)";
        if (!durationTextField.getText().matches(durationPattern) || durationTextField.getText().equals("0") ||
            durationTextField.getText().equals("0h") || durationTextField.getText().equals("0m") ||
            durationTextField.getText().equals("0h0m") || durationTextField.getText().equals("0.0")) {
            durationTextField.getStyleClass().add("errorTextField");
            result = false;
            errorLabel.setText("Duration format must be in the form (1h, 1.2, 1, 1h30m, 30m)");
        }else{
            durationTextField.getStyleClass().removeAll("errorTextField");
            errorLabel.setText("");
        }
        if(commentField.getText().equals("")){
            commentField.getStyleClass().add("errorTextField");
            result = false;
            errorLabel.setText("Must include a comment to the time log");
        }else{
            commentField.getStyleClass().removeAll("errorTextField");
            errorLabel.setText("");
        }
        if(personChoice.getValue() != null){
            personChoice.getStyleClass().removeAll("errorBox");
            errorLabel.setText("");
        }else {
            personChoice.getStyleClass().add("errorBox");
            result = false;
            errorLabel.setText("Must log time to a person");
        }
        return result;
    }

    @Override
    public void fieldConstruct(Object object) {
    }

    @Override
    public VBox draw() {
        return content;
    }

    /**
     * Checks to see if any of the fields have not been saved.
     * @param args
     * @return
     */
    @Override
    public boolean validate(Object... args) {
        boolean result = true;
        for (TimeLogRow row:timeScrollList.getItems()){
            if(!(row.isSaved())){
                row.getHBox().getStyleClass().add("errorBox");
                result = false;
            }else{
                row.getHBox().getStyleClass().removeAll("errorBox");
            }
        }
        return result;
    }

    /**
     * Does some date formatting and takes the element values and sets the loggedTime to the given values.
     * Adds this log to the task.
     * @param object
     */
    @Override
    public void apply(Object object) {
    }

    /**
     * Save log takes the components from the HBox in the TimeLogRow along with the timeLog to save
     * to the data model.
     * @param loggedTime
     * @param selectedPerson
     * @param endDate
     * @param time
     * @param duration
     * @param comment
     * @param editing
     */
    private void saveLog(LoggedTime loggedTime, Person selectedPerson, LocalDate endDate, String time,
        String duration, String comment, boolean editing) {

        LocalTime endTime = LocalTime.parse(time, formatter);

        LocalDateTime endDateTime = LocalDateTime.of(endDate,endTime);
        LocalDateTime startDateTime = endDateTime.minusMinutes(Integer.parseInt(parseTime(duration)));

        loggedTime.setEndDateTime(endDateTime);
        loggedTime.setStartDateTime(startDateTime);
        loggedTime.setComment(comment);

        loggedTime.setPerson(selectedPerson);
        loggedTime.setComment(comment);



        if(editing){
            for(Person p:organisation.getPeople()){
                if(p.getLoggedTimes().contains(loggedTime)){
                    p.getLoggedTimes().remove(loggedTime);
                    selectedPerson.getLoggedTimes().add(loggedTime);
                }
            }
        }else{
            selectedPerson.getLoggedTimes().add(loggedTime);
            task.addHourLog(loggedTime);
        }
    }

    /**
     * Shows the hbox of the TimeLogRow
     */
    public class formatListHBoxRow extends ListCell<TimeLogRow>{
        @Override public void updateItem(TimeLogRow item, boolean empty) {
            super.updateItem(item, empty);
            if(item != null && !empty){
                setGraphic(item.getHBox());
            }else{
                setGraphic(null);
            }
        }
    }
}
