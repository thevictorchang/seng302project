package seng302.group3.model.io.property_sheets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import seng302.group3.model.Organisation;
import seng302.group3.model.Project;
import seng302.group3.model.Team;
import seng302.group3.model.TimePeriod;
import seng302.group3.model.io.Dialogs;
import seng302.group3.model.io.Editor;
import seng302.group3.model.search.FilterListView;
import seng302.group3.model.search.FilterUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by ntr24 on 24/04/15.
 * <p>
 * <p>
 * This class does the checks for adding or editing a Project. These are text field validity,
 * team and the short name being unique.
 * It will also set the FXML loaded layout and populate the fields if needed.
 */


public class ProjectSheet implements EditorSheet {

    private static final String errorStyle = "errorTextField";
    @FXML DatePicker endDatePicker;
    @FXML DatePicker startDatePicker;

    @FXML Button buttonRemoveSkill;
    @FXML Button buttonAddSkill;

    private VBox content = new VBox();

    @FXML TextField shortNameTextField;
    @FXML TextField longNameTextField;
    @FXML TextField descriptionTextField;
    @FXML TextField availableTeamsTextField;
    @FXML TextField currentTeamsTextField;
    @FXML Label errorLabel;

    @FXML private ListView<TimePeriod> allCurrentTeamsListView;
    @FXML private ListView<Team> allAvailableTeamsListView;

    private Collection<Team> tempAvailableTeams = new ArrayList<>();
    private Collection<TimePeriod> tempCurrentTeams = new ArrayList<>();

    final ObservableList<TimePeriod> allCurrentTeamsList = FXCollections.observableArrayList();
    final ObservableList<Team> allAvailableTeamsList = FXCollections.observableArrayList();

    private Collection<TimePeriod> currentTimePeriods = new ArrayList<>();

    private Project editProject = null;

    private FilterListView allAvailableTeamsFilterListView;
    private FilterListView allCurrentTeamsFilterListView;


    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public ProjectSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/ProjectSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        allAvailableTeamsFilterListView = new FilterListView(allAvailableTeamsListView, availableTeamsTextField, FilterUtil.AutoCompleteMode.CONTAINING);
        allCurrentTeamsFilterListView = new FilterListView(allCurrentTeamsListView, currentTeamsTextField, FilterUtil.AutoCompleteMode.CONTAINING);

    }

    /**
     * sets the fields of the editor to values fetched from an existing project
     *
     * @param object - expects an object of type Organisation to fetch field values from
     */
    @Override public void fieldConstruct(Object object) {
        if (object instanceof Project) {
            Project project = (Project) object;

            //Set name fields
            shortNameTextField.setText(project.getShortName());
            longNameTextField.setText(project.getNameLong());
            descriptionTextField.setText(project.getDescription());

            currentTimePeriods.addAll(project.getTimePeriods());

            //Add a projects current teams to the left list
            tempCurrentTeams.addAll(project.getTimePeriods());
            allCurrentTeamsList.addAll(tempCurrentTeams);
            allCurrentTeamsFilterListView.setData(allCurrentTeamsList);

            //Makes a reference to the project we are editing
            editProject = project;
        }
    }

    /**
     * When a project is added or edited this will populate fields that will always need to be filled,
     * it will initialize the ability to select multiple teams in the lists. This also does a
     * highlight of teams that are unable to be added to the project because of role requirements.
     *
     * @param args
     */
    public void teamsConstruct(Object... args) {
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];
            if (allCurrentTeamsList.size() < 1) {
                allCurrentTeamsFilterListView.setData(allCurrentTeamsList);
            }
            tempAvailableTeams.addAll(organisation.getTeams()); // add every tea mfrom organisation
            tempAvailableTeams.removeAll(allCurrentTeamsList);
            allAvailableTeamsList.addAll(tempAvailableTeams);

            allAvailableTeamsFilterListView.setData(allAvailableTeamsList);

            //startDatePicker.setDisable(true);

            updateTeams();
        }
    }


    /**
     * Updates the lists of teams and the cells
     */
    public void updateTeams() {
        allAvailableTeamsList.clear();
        allAvailableTeamsList.addAll(tempAvailableTeams);
        allAvailableTeamsFilterListView.setData(allAvailableTeamsList);
        allCurrentTeamsList.clear();
        allCurrentTeamsList.addAll(tempCurrentTeams);
        allCurrentTeamsFilterListView.setData(allCurrentTeamsList);
        this.allAvailableTeamsListView.setCellFactory(makeCallListener());
        this.allCurrentTeamsListView.setCellFactory(makeCallListenerTimePeriod());
    }

    /**
     * returns the content loaded in the constructor
     *
     * @return - VBox containing the editor layout
     */
    @Override public VBox draw() {
        return content;
    }


    /**
     * checks the fields for validity and returns true if are valid. If false updates layout to show missing fields
     *
     * @return
     */
    @Override public boolean validate(Object... args) {
        if (args.length < 1) {
            System.err.println("Not passing any args");
            return false;
        }

        boolean result = true;
        shortNameTextField.setText(shortNameTextField.getText().trim());
        longNameTextField.setText(longNameTextField.getText().trim());
        descriptionTextField.setText(descriptionTextField.getText().trim());

        if (shortNameTextField.getText().length() < 1) {
            result = false;
            shortNameTextField.getStyleClass().add(errorStyle);
        } else {
            shortNameTextField.getStyleClass().removeAll(errorStyle);
        }

        if (longNameTextField.getText().length() < 1) {
            result = false;
            longNameTextField.getStyleClass().add(errorStyle);
        } else {
            longNameTextField.getStyleClass().removeAll(errorStyle);
        }

        if (descriptionTextField.getText().length() < 1) {
            result = false;
            descriptionTextField.getStyleClass().add(errorStyle);
        } else {
            descriptionTextField.getStyleClass().removeAll(errorStyle);
        }

        if (!result) {
            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        }

        // uniqueness check
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];

            if (organisation
                .uniqueName(shortNameTextField.getText(), Organisation.uniqueType.UNIQUE_PROJECT)) {
                shortNameTextField.getStyleClass().removeAll(errorStyle);
                return true;
            } else {
                if (args.length > 1 && args[1] instanceof Project) {
                    Project project = (Project) args[1];
                    if (shortNameTextField.getText().equals(project.getShortName())) {
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
     * applys the field values to a project
     *
     * @param object
     */
    @Override public void apply(Object object) {
        if (object instanceof Project) {
            Project project = (Project) object;

            //Apply name changes
            project.setShortName(shortNameTextField.getText());
            project.setNameLong(longNameTextField.getText());
            project.setDescription(descriptionTextField.getText());



            for (Iterator<TimePeriod> it = project.getTimePeriods().iterator(); it.hasNext(); ) {
                TimePeriod timePeriod = it.next();
                //If removed. Delete connections then
                if (!tempCurrentTeams.contains(timePeriod)) {
                    timePeriod.getTeam().removeTimePeriod(timePeriod);
                    it.remove();
                    project.checkLastTeam(timePeriod);
                }
            }

            for (TimePeriod timePeriod : tempCurrentTeams) {
                //if added. New time period
                if (!project.getTimePeriods().contains(timePeriod)) {
                    //timePeriod.setProject(project);
                    timePeriod.getTeam().addTimePeriod(timePeriod);
                    project.addTimePeriod(timePeriod);
                    //project.checkNewTeam(timePeriod);
                }
            }

            project.updateSkills();
        }
    }

    /**
     * Checks a given team to see if it contains a set PO and SM.
     *
     * @param team
     * @return
     */
    private boolean checkRoles(Team team) {
        if ((team.getProductOwner() == null) || (team.getScrumMaster() == null)) {
            return false;
        }
        return true;
    }


    /**
     * When we press the add team button it checks that this team meets correct criteria and if so
     * it will move it,
     *
     * @param actionEvent
     */
    public void buttonAddTeam(ActionEvent actionEvent) {
        Team currentTeam = allAvailableTeamsListView.getSelectionModel().getSelectedItem();
        //Check we have a selection
        if (currentTeam != null) {
            if (checkRoles(currentTeam)) {

                TimePeriod newTimePeriod = new TimePeriod();
                boolean editing;
                if(editProject != null){
                    editing = true;
                }else{
                    editing = false;
                }
                new Editor()
                    .CreateTimePeriodProject(newTimePeriod, currentTimePeriods, currentTeam, editing);

                //cancel was clicked
                if (newTimePeriod.getStartDate() == null) {
                    errorLabel.setText("No Dates Selected");
                } else {
                    errorLabel.setText("");
                    newTimePeriod.setTeam(currentTeam);
                    currentTimePeriods.add(newTimePeriod);
                    tempCurrentTeams.add(newTimePeriod);
                    updateTeams();
                }
            } else {
                errorLabel.setText("Team requires valid Scrum Master and Product Owner");
            }
        } else {
            errorLabel.setText("No team selected in available teams");
        }
        currentTeamsTextField.setText("");
        availableTeamsTextField.setText("");
    }


    /**
     * When the remove Button is pressed a team will be moved to the available list from current.
     *
     * @param actionEvent
     */
    public void buttonRemoveTeam(ActionEvent actionEvent) {
        TimePeriod currentTeam = allCurrentTeamsListView.getSelectionModel().getSelectedItem();
        if (currentTeam != null) {
            currentTimePeriods.remove(currentTeam);
            tempCurrentTeams.remove(currentTeam);
            this.updateTeams();
        }
        currentTeamsTextField.setText("");
        availableTeamsTextField.setText("");
    }


    /**
     * Runs the call listener which will then color the cells appropriately.
     *
     * @return
     */
    private Callback<ListView<Team>, ListCell<Team>> makeCallListener() {
        return param -> {
            final ListCell<Team> cell = new ListCell<Team>() {
                @Override protected void updateItem(Team item, boolean b) {
                    super.updateItem(item, b);
                    if (item != null && !item.toString().equals("")) {
                        setText(item.toString());
                        if (!checkRoles(item)) {
                            this.getStyleClass().add("nonValid");
                        } else {
                            this.getStyleClass().remove("nonValid");
                        }
                    } else {
                        setText(null);
                        this.getStyleClass().remove("nonValid");
                        this.setGraphic(null);
                    }

                    //if (allCurrentTeamsList.contains(item)) {
                    //    setText(item.toString());
                    //}

                }
            };

            return cell;
        };
    }

    /**
     * Returns the formatted cell with the correct string.
     *
     * @return
     */
    private Callback<ListView<TimePeriod>, ListCell<TimePeriod>> makeCallListenerTimePeriod() {
        return param -> {
            final ListCell<TimePeriod> cell = new ListCell<TimePeriod>() {
                @Override
                protected void updateItem(TimePeriod item, boolean b) {
                    super.updateItem(item, b);
                    if (item != null) {
                        setText(item.getTeam().toString() + "\n" + item.getTimePeriod());
                    }

                }
            };
            return cell;
        };
    }

    /**
     * Brings up the time-series dialog which shows team allocations over a time period.
     * @param actionEvent
     */
    public void buttonShowAllocation(ActionEvent actionEvent) {
        new Dialogs().showAllocations(currentTimePeriods);
    }

}
