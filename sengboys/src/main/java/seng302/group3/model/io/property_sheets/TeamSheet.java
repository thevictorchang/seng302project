package seng302.group3.model.io.property_sheets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import seng302.group3.model.*;
import seng302.group3.model.search.FilterListView;
import seng302.group3.model.search.FilterUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cjm328 on 4/05/15.
 */
public class TeamSheet implements EditorSheet {

    private static final String errorStyle = "errorTextField";
    @FXML Label actionLabel;
    @FXML ChoiceBox projectChoice;

    @FXML Button buttonRemovePerson;
    @FXML Button buttonAddPerson;

    private VBox content = new VBox();

    @FXML TextField shortNameTextField;
    @FXML TextField longNameTextField;
    @FXML TextField descriptionTextField;
    @FXML Label errorLabel;
    @FXML ListView<Person> allCurrentPeopleListView;
    @FXML ListView<Person> allAvailablePeopleListView;
    @FXML TextField availablePeopleTextField;

    private Person productOwner;
    private Person scrumMaster;
    private Collection<Person> devMembers = new ArrayList<>();

    private Team editingTeam = null;

    private Collection<TimePeriod> timePeriods = new ArrayList<>();


    private Collection<Person> tempAvailablePeople = new ArrayList<>();
    private Collection<Person> tempCurrentPeople = new ArrayList<>();

    final ObservableList<Person> allCurrentPeopleList = FXCollections.observableArrayList();
    final ObservableList<Person> allAvailablePeopleList = FXCollections.observableArrayList();

    private FilterListView allAvailablePeopleFilterListView;

    Image POBlank = new Image(getClass().getResourceAsStream("/imgs/roles/PO_Blank.png"));
    Image POFilled = new Image(getClass().getResourceAsStream("/imgs/roles/PO_Filled.png"));
    Image SMBlank = new Image(getClass().getResourceAsStream("/imgs/roles/SM_Blank.png"));
    Image SMFilled = new Image(getClass().getResourceAsStream("/imgs/roles/SM_Filled.png"));
    Image DBlank = new Image(getClass().getResourceAsStream("/imgs/roles/D_Blank.png"));
    Image DFilled = new Image(getClass().getResourceAsStream("/imgs/roles/D_Filled.png"));

    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public TeamSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/TeamSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        allCurrentPeopleListView.setCellFactory(param -> new positionFormatCell());
        allAvailablePeopleListView.setCellFactory(param -> new positionFormatCell());
        allAvailablePeopleFilterListView= new FilterListView(allAvailablePeopleListView, availablePeopleTextField, FilterUtil.AutoCompleteMode.CONTAINING);

    }

    /**
     * sets the fields of the editor to values fetched from an existing team
     *
     * @param object - expects an object of type Organisation to fetch field values from
     */
    @Override public void fieldConstruct(Object object) {
        if (object instanceof Team) {
            Team team = (Team) object;


            //set text
            shortNameTextField.setText(team.getShortName());
            longNameTextField.setText(team.getNameLong());
            descriptionTextField.setText(team.getDescription());

            //set lists
            tempCurrentPeople.addAll(team.getPeople());
            allCurrentPeopleList.addAll(tempCurrentPeople);
            allCurrentPeopleListView.setItems(allCurrentPeopleList);


            if (team.getProductOwner() != null) {
                this.productOwner = team.getProductOwner();
            }
            if (team.getScrumMaster() != null) {
                this.scrumMaster = team.getScrumMaster();
            }
            if (!(team.getDevTeamMembers().size() < 1)) {
                this.devMembers = team.getDevTeamMembers();
            }
            //Makes a reference to the team being edited.
            editingTeam = team;

        }
    }


    /**
     * Constructs the dialog by adding in available people for the team.
     * Adds proper projects to the combo box.
     *
     * @param args
     */
    public void peopleConstruct(Object... args) {
        if (args[0] instanceof Organisation) {
            Organisation organisation = (Organisation) args[0];
            if (allCurrentPeopleList.size() < 1) {
                allCurrentPeopleListView.setItems(allCurrentPeopleList);

            }
            tempAvailablePeople.addAll(organisation.getPeople());
            tempAvailablePeople.removeAll(allCurrentPeopleList);
            allAvailablePeopleList.addAll(tempAvailablePeople);

            allAvailablePeopleListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            allCurrentPeopleListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            //allAvailablePeopleListView.setItems(allAvailablePeopleList);
            allAvailablePeopleFilterListView.setData(allAvailablePeopleList);
        }
    }

    /**
     * Updates the lists in the dialog.
     */
    public void updatePeople() {
        allAvailablePeopleList.clear();
        allAvailablePeopleList.addAll(tempAvailablePeople);
        allCurrentPeopleList.clear();
        allCurrentPeopleList.addAll(tempCurrentPeople);
        allAvailablePeopleFilterListView.setData(allAvailablePeopleList);

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
    public boolean validate(Object... args) {

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
                .uniqueName(shortNameTextField.getText(), Organisation.uniqueType.UNIQUE_TEAM)) {
                shortNameTextField.getStyleClass().removeAll(errorStyle);
                return true;
            } else {
                //Needs cleaning up
                if (args.length > 1 && args[1] instanceof Team) {
                    Team team = (Team) args[1];
                    if (shortNameTextField.getText().equals(team.getShortName())) {
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
     * applies the field values to a team
     *
     * @param object
     */
    @Override public void apply(Object object) {
        if (object instanceof Team) {
            Team team = (Team) object;
            team.setShortName(shortNameTextField.getText());
            team.setLongName(longNameTextField.getText());
            team.setDescription(descriptionTextField.getText());
            //team.setPeople(tempCurrentPeople);

            team.setProductOwner(productOwner);
            team.setScrumMaster(scrumMaster);
            team.setDevTeam(devMembers);


            //Look at available people (people not in the team)
            for (Person person : tempAvailablePeople) {
                //determine if the person has moved from in the team to no longer in the team
                if (team.getPeople().contains(person)) {
                    //Remove person from team and set their project to null
                    team.removePerson(person);
                    person.setTeam(null);
                    person.setProject(null);
                }
            }

            for (Person person : tempCurrentPeople) {
                if (!team.getPeople().contains(person)) {
                    //Add person to team and make their project
                    //TODO may need checks to see if it was already in a project or team but this may
                    //TODO not be a viable situation
                    team.addPerson(person);
                    person.setTeam(team);
                    //person.setProject(team.getCurrentProject());
                }
            }

            team.updateSkills();
            for (TimePeriod timePeriod : team.getTimePeriods()) {
                timePeriod.getProject().updateSkills();
            }

            team.updateSkills();

        }
    }

    /**
     * returns true is person can be added to a team
     *
     * @param person
     * @return
     */
    private boolean checkPersonAvailablity(Person person) {
        boolean result = true;

        if (person.getTeam() != null && person.getTeam() == editingTeam) {
            return true;
        }
        if (person.getProject() != null || person.getTeam() != null) {
            return false;
        }

        return result;
    }


    /**
     * Is fired when the left arrow button is pressed. lets a user add people to a team.
     *
     * @param actionEvent
     */
    public void buttonAddPerson(ActionEvent actionEvent) {
        Collection<Person> currentPeople =
            allAvailablePeopleListView.getSelectionModel().getSelectedItems();
        if (currentPeople != null) {
            if (currentPeople.size() == 1) {
                Person currentPerson = (Person) currentPeople.toArray()[0];
                if (checkPersonAvailablity(currentPerson)) {
                    tempAvailablePeople.remove(currentPerson);
                    tempCurrentPeople.add(currentPerson);
                    // if there isn't a product owner and the person has the skill to be one. assign them
                    if (productOwner == null) {
                        for (Skill s : currentPerson.getSkills()) {
                            if (s.getShortName().equals("PO")) {
                                productOwner = currentPerson;
                                break;
                            }
                        }
                    }
                    // if there isn't a SM and the person isn't the PO and they have SM skill. Assign them
                    if (scrumMaster == null && currentPerson != productOwner) {
                        for (Skill s : currentPerson.getSkills()) {
                            if (s.getShortName().equals("SM")) {
                                scrumMaster = currentPerson;
                                break;
                            }
                        }
                    }
                    this.updatePeople();
                    actionLabel.setText("Added " + currentPerson + " to current people");
                    errorLabel.setText("");

                } else {
                    errorLabel.setText("Current person already assigned to a Project/Team");
                    actionLabel.setText("");
                }
            } else if (currentPeople.size() > 1) {
                boolean goodSelection = true;
                for (Person person : currentPeople) {
                    if (!checkPersonAvailablity(person)) {
                        errorLabel.setText(person.getShortName() + " cannot be added");
                        actionLabel.setText("");
                        goodSelection = false;
                        break;
                    }
                }
                if (goodSelection) {
                    tempAvailablePeople.removeAll(currentPeople);
                    tempCurrentPeople.addAll(currentPeople);
                    // if there isn't a product owner and the person has the skill to be one. assign them
                    for (Person person : currentPeople) {
                        if (productOwner == null) {
                            for (Skill s : person.getSkills()) {
                                if (s.getShortName().equals("PO")) {
                                    productOwner = person;
                                    break;
                                }
                            }
                        }
                        // if there isn't a SM and the person isnt the PO and they have SM skill. Assign them
                        if (scrumMaster == null && person != productOwner) {
                            for (Skill s : person.getSkills()) {
                                if (s.getShortName().equals("SM")) {
                                    scrumMaster = person;
                                    break;
                                }
                            }
                        }
                    }
                    this.updatePeople();
                    errorLabel.setText("");
                    actionLabel.setText("Added multiple people to the team");
                }

            }


        } else {
            errorLabel.setText("No person selected in available people");
            actionLabel.setText("");
        }
    }

    /**
     * Removes a person from the current people list in the dialog
     *
     * @param actionEvent
     */
    public void buttonRemovePerson(ActionEvent actionEvent) {
        Collection<Person> currentPeople =
            allCurrentPeopleListView.getSelectionModel().getSelectedItems();
        boolean partOfProject = false;
        if (currentPeople != null) {
            if (currentPeople.size() == 1) {
                Person currentPerson = (Person) currentPeople.toArray()[0];
                if (currentPerson.equals(scrumMaster)) {
                    //If a scrum master is in a project they cannot be removed

                    if (currentPerson.getTeam() != null && currentPerson.getTeam().getTimePeriods().size() > 0) {
                        partOfProject = true;
                    } else {
                        scrumMaster = null;
                        errorLabel.setText("Removed Scrum master from team");
                    }
                } else if (currentPerson.equals(productOwner)) {
                    //If a product owner is in a project they cannot be removed
                    if (currentPerson.getTeam() != null && currentPerson.getTeam().getTimePeriods().size() > 0) {
                        partOfProject = true;
                    } else {
                        productOwner = null;
                        errorLabel.setText("Removed product owner from team");
                    }
                } else if (devMembers.contains(currentPerson)) {
                    devMembers.remove(currentPerson);
                    errorLabel.setText("Removed dev member from team");
                } else {
                    errorLabel.setText("Removed " + currentPerson + " from team");
                }
                actionLabel.setText("");

                if (!partOfProject) {
                    tempAvailablePeople.add(currentPerson);
                    tempCurrentPeople.remove(currentPerson);

                } else {
                    errorLabel.setText("Cannot remove a SM or PO from a team in a project");
                }


            } else if (currentPeople.size() > 1) {
                for (Person person : currentPeople) {
                    if (person.equals(scrumMaster)) {
                        if (person.getProject() != null) {
                            partOfProject = true;
                        } else {
                            scrumMaster = null;
                        }

                    } else if (person.equals(productOwner)) {
                        if (person.getProject() != null) {
                            partOfProject = true;
                        } else {
                            productOwner = null;
                        }

                    } else if (devMembers.contains(person)) {
                        devMembers.remove(person);
                    }
                }
                if (!partOfProject) {
                    tempCurrentPeople.removeAll(currentPeople);
                    tempAvailablePeople.addAll(currentPeople);
                    actionLabel.setText("Removed multiple people from the team");
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Cannot remove a SM or PO from a team in a project");
                    actionLabel.setText("");
                }


            }
            this.updatePeople();

        } else {
            errorLabel.setText("No person selected in current people");
            actionLabel.setText("");
        }

    }


    /**
     * Sets the product owner for the dialog
     *
     * @param person
     */
    private void setPO(Person person) {
        //Does a check to see if PO or SM is active
        if (person.getTeam() != null && person.getTeam().getTimePeriods().size() > 0){
            errorLabel.setText("Cannot remove a PO from a team in a project");
            actionLabel.setText("");
        }else{
            if (person.equals(productOwner)) {
                productOwner = null;
                actionLabel.setText("Removed " + person + " from product owner");
                errorLabel.setText("");
            } else {
                if (devMembers.contains(person)) {
                    devMembers.remove(person);
                }
                if (scrumMaster != null && scrumMaster.equals(person)) {
                    scrumMaster = null;
                }
                productOwner = person;

                actionLabel.setText("Made " + person + " the product owner");
                errorLabel.setText("");

                allCurrentPeopleListView.setCellFactory(param -> new positionFormatCell());
            }
        }
    }

    /**
     * Sets the Scrum Master for the team dialog
     *
     * @param person
     */
    private void setSM(Person person) {
        //Does a check to see if PO or SM is active
        if (person.getTeam() != null && person.getTeam().getTimePeriods().size() > 0){
            errorLabel.setText("Cannot remove a SM from a team in a project");
            actionLabel.setText("");
        } else {
            if (person.equals(scrumMaster)) {
                scrumMaster = null;
                actionLabel.setText("Removed " + person + " from scrum master");
                errorLabel.setText("");
            } else {
                if (devMembers.contains(person)) {
                    devMembers.remove(person);
                }
                if (productOwner != null && productOwner.equals(person)) {
                    productOwner = null;
                }
                scrumMaster = person;
                actionLabel.setText("Made " + person + " the scrum master");
                errorLabel.setText("");

                allCurrentPeopleListView.setCellFactory(param -> new positionFormatCell());
            }
        }
    }

    /**
     * Adds the fiven person into the list of dev memebers
     *
     * @param person
     */
    public void setDev(Person person) {

        //Check to see if the person is an active PO or SM
        if((person == scrumMaster || person == productOwner)
            && person.getTeam() != null && person.getTeam().getTimePeriods().size() > 0){
            errorLabel.setText("Cannot remove a SM or PO from a team in a project");
            actionLabel.setText("");
        }else{
            if (person == scrumMaster) {
                scrumMaster = null;
            } else if (person == productOwner) {
                productOwner = null;
            }

            if (!devMembers.contains(person)) {
                devMembers.add(person);
                actionLabel.setText("Added " + person + " to the dev team");
                errorLabel.setText("");

                allCurrentPeopleListView.setCellFactory(param -> new positionFormatCell());
            } else {
                devMembers.remove(person);
                actionLabel.setText("Removed " + person + " from the dev team");
                errorLabel.setText("");

                allCurrentPeopleListView.setCellFactory(param -> new positionFormatCell());
            }
        }
    }


    /**
     * Formats the list cells to check if the people can move. Also shows the dev, SM and PO buttons
     */
    public class positionFormatCell extends ListCell<Person> {
        @Override public void updateItem(Person item, boolean empty) {
            super.updateItem(item, empty);

            this.getStyleClass().removeAll("nonValid");

            GridPane grid = new GridPane();

            Label textLabel = new Label();

            // the PO circle image will either be filled or blank
            ImageView POImageView = new ImageView(POBlank);
            if (productOwner == item) {
                POImageView.setImage(POFilled);
            }


            // clicking the image will set this person as the PO
            POImageView.setOnMouseClicked(event -> {
                if (allCurrentPeopleList.contains(item))
                    setPO(item);
            });

            // Hovering over the PO circle highlights it
            POImageView.setOnMouseEntered(event -> POImageView.setImage(POFilled));
            POImageView.setOnMouseExited(event -> {
                if (productOwner == item)
                    POImageView.setImage(POFilled);
                else
                    POImageView.setImage(POBlank);
            });

            // the SM circle image will either be filled or blank
            ImageView SMImageView = new ImageView(SMBlank);
            if (scrumMaster == item) {
                SMImageView.setImage(SMFilled);
            }

            // hovering over the SM circle highlights it
            SMImageView.setOnMouseEntered(event -> SMImageView.setImage(SMFilled));
            SMImageView.setOnMouseExited(event -> {
                if (scrumMaster == item)
                    SMImageView.setImage(SMFilled);
                else
                    SMImageView.setImage(SMBlank);
            });

            // clicking the image will set this person as SM
            SMImageView.setOnMouseClicked(event -> {
                if (allCurrentPeopleList.contains(item))
                    setSM(item);
            });

            //The Dev team circle will either be filled or blank
            ImageView DImageView = new ImageView(DBlank);
            if (devMembers.contains(item)) {
                DImageView.setImage(DFilled);
            }

            // hovering over the D circle highlights it
            DImageView.setOnMouseEntered(event -> DImageView.setImage(DFilled));
            DImageView.setOnMouseExited(event -> {
                if (devMembers.contains(item))
                    DImageView.setImage(DFilled);
                else
                    DImageView.setImage(DBlank);
            });

            // clicking the image will toggle adding the person to the dev team
            DImageView.setOnMouseClicked(event -> {
                if (allCurrentPeopleList.contains(item))
                    setDev(item);
            });

            POImageView.setFitHeight(16);
            POImageView.setFitWidth(16);
            SMImageView.setFitHeight(16);
            SMImageView.setFitWidth(16);
            DImageView.setFitHeight(16);
            DImageView.setFitWidth(16);

            textLabel.setText(item == null ? "" : item.getShortName());
            GridPane.setConstraints(textLabel, 0, 0);

            grid.setHgap(5);
            grid.getChildren().add(textLabel);
            if (item != null) {
                if (allAvailablePeopleList.contains(item)) {
                    if (!checkPersonAvailablity(item)) {
                        this.getStyleClass().add("nonValid");
                    }
                }

                // this section checks if the person is SM or PO to add the labels
                boolean isSM = false;
                boolean isPO = false;
                for (Skill s : item.getSkills()) {
                    if (s.getShortName().equals("SM")) {
                        isSM = true;
                    } else if (s.getShortName().equals("PO")) {
                        isPO = true;
                    }
                }

                if (isPO) {
                    grid.getChildren().add(POImageView);
                    GridPane.setConstraints(POImageView, 1, 0);
                }
                if (isSM) {
                    grid.getChildren().add(SMImageView);
                    GridPane.setConstraints(SMImageView, 2, 0);
                }

                grid.getChildren().add(DImageView);
                GridPane.setConstraints(DImageView, 3, 0);
            }
            ColumnConstraints c1 = new ColumnConstraints();
            ColumnConstraints c2 = new ColumnConstraints();
            ColumnConstraints c3 = new ColumnConstraints();
            ColumnConstraints c4 = new ColumnConstraints();
            c1.setPercentWidth(60);
            c2.setPercentWidth(13);
            c3.setPercentWidth(13);
            c4.setPercentWidth(13);
            grid.getColumnConstraints().addAll(c1, c2, c3, c4);

            this.setMaxWidth(140);
            this.setMinWidth(140);
            grid.setMaxWidth(140);
            grid.setMinWidth(140);
            setGraphic(grid);
        }
    }
}
