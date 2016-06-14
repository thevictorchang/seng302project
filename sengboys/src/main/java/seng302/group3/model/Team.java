package seng302.group3.model;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Default;
import seng302.group3.controller.App;
import seng302.group3.model.io.Editor;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.io.property_sheets.BacklogSheet;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;
import seng302.group3.model.navigation.navigator_items.PersonNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TimePeriodNavigatorItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by cjmarffy on 25/04/15.
 */
@Default(required = false) public class Team extends Element {

    private static final long serialVersionUID = 1806201500006L;

    @Attribute private String shortName; // name used for identification
    @Attribute private String name; // name of team
    private String description; // description of team
    private SerializableObservableList<Person> people = new SerializableObservableList<>();
    private SerializableObservableList<Skill> skills = new SerializableObservableList<>();

    private SerializableObservableList<TimePeriod> timePeriods = new SerializableObservableList<>();


    //private Project project; // project the team belongs to/ is working on
    private Person scrumMaster; //The scrum master of the team
    private Person productOwner; //The product owner of the team
    private Collection<Person> devTeamMembers = new ArrayList<>();
        //The Development Team Members of the team.


    /**
     * Constructor for Team
     */
    public Team() {
        this.shortName = "";
        this.name = "";
        this.description = "";
    }


    /**
     * Constructor for Team
     *
     * @param name
     */
    public Team(String name) {
        this.name = name;
    }

    /**
     * Constructor for Team
     *
     * @param shortName
     * @param longName
     */
    public Team(String shortName, String longName, String description) {
        this.shortName = shortName;
        this.name = longName;
        this.description = description;
    }

    /**
     * This copys the given team into a new team object
     *
     * @param copyData - the team to be copied
     */
    public Team(Team copyData) {
        this.shortName = copyData.getShortName();
        this.name = copyData.getNameLong();
        this.description = copyData.getDescription();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for name
     *
     * @return Name
     */
    public String getNameLong() {
        return name;
    }

    /**
     * Setter for name
     *
     * @param name
     */
    public void setLongName(String name) {
        this.name = name;
    }

    /**
     * Getter for description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for people in the list of people assigned to the team
     *
     * @return People in the collection
     */
    public Collection<Person> getPeople() {
        return people;
    }

    /**
     * Remove a Person from the people list
     *
     * @param person
     */
    public void removePerson(Person person) {
        this.people.remove(person);
        if(devTeamMembers.contains(person))
            devTeamMembers.remove(person);
        if(productOwner == person)
            productOwner = null;
        if(scrumMaster == person)
            scrumMaster = null;
        if (this.timePeriods != null)
            for (TimePeriod t : timePeriods) {
                t.getProject().updateSkills();
            }
        updateSkills();
    }

    /**
     * Returns the project (if any) that this team is assigned to at the current time.
     *
     * @return
     */
    public Project getCurrentProject() {
        Project project = null;
        for (TimePeriod t : timePeriods) {
            if (t.containsDate(LocalDate.now())) {
                project = t.getProject();
            }

        }
        return project;
    }


    /**
     * Add a new Person to the people list
     *
     * @param person
     */
    public void addPerson(Person person) {
        this.people.add(person);
        updateSkills();
    }

    /**
     * Set a team member as the scrum master of the team
     *
     * @param scrumMaster - the team member you want to set as scrum master
     */
    public void setScrumMaster(Person scrumMaster) {
        this.scrumMaster = scrumMaster;

    }

    /**
     * Get the teams scrum master
     *
     * @return - the teams scrum master
     */
    public Person getScrumMaster() {
        return scrumMaster;
    }

    /**
     * Set a team member to be the product owner of the team
     *
     * @param productOwner - the team member you want to set as product owner
     */
    public void setProductOwner(Person productOwner) {
        this.productOwner = productOwner;
    }

    /**
     * Removes unset timeperiods from the current team. This is a basic update for Teams
     * time periods.
     */
    public void removeUnsetTimePeriods() {
        for (Iterator<TimePeriod> it = timePeriods.iterator(); it.hasNext(); ) {
            TimePeriod timePeriod = it.next();
            if (timePeriod.getProject() == null || timePeriod.getTeam() == null) {
                it.remove();
            }
        }
    }

    /**
     * Get the teams product owner
     *
     * @return - the teams product owner
     */
    public Person getProductOwner() {
        return productOwner;
    }

    /**
     * Add a team member to be a development team member
     *
     * @param newDevMember - the team member you want to make a development team member
     */
    public void addDevTeamMember(Person newDevMember) {
        this.devTeamMembers.add(newDevMember);
    }

    /**
     * Remove a team member from the list of development team members
     *
     * @param devMember -development team member you want to remove
     */
    public void removeDevTeamMember(Person devMember) {
        this.devTeamMembers.remove(devMember);
    }

    /**
     * Set the skills of the team
     *
     * @param skills - the skills to be set
     */
    public void setSkills(Collection<Skill> skills) {
        this.skills.setItems(skills);
    }

    /**
     * Return the skills that the team has among its members
     *
     * @return collection of skills the team has
     */
    public Collection<Skill> getSkills() {
        return skills;
    }

    /**
     * A getter for time period
     *
     * @return
     */
    public Collection<TimePeriod> getTimePeriods() {
        return this.timePeriods;
    }

    /**
     * A setter for timePeriod
     *
     * @param timePeriod
     */
    public void setTimePeriod(Collection<TimePeriod> timePeriod) {
        this.timePeriods = (SerializableObservableList) timePeriod;
    }

    /**
     * Add something to the time period collection.
     *
     * @param timePeriod
     */
    public void addTimePeriod(TimePeriod timePeriod) {
        this.timePeriods.add(timePeriod);

    }

    /**
     * Add something to the time period collection. and a project is set
     *
     * @param timePeriod
     */
    public void addTimePeriod(TimePeriod timePeriod, Project project, Team team) {
        this.timePeriods.add(timePeriod);
        timePeriod.setProject(project);
        timePeriod.setTeam(team);

    }

    /**
     * remove a timeperiod from the collection.
     *
     * @param timePeriod
     */
    public void removeTimePeriod(TimePeriod timePeriod) {
        this.timePeriods.remove(timePeriod);
    }


    /**
     * Update the skills of the team from the people in the team.
     */
    public void updateSkills() {
        skills.clear();
        for (Person person : people) {
            for (Skill skill : person.getSkills()) {
                this.skills.add(skill);
            }
        }
    }

    /**
     * A setter for people
     *
     * @param people
     */
    public void setPeople(Collection<Person> people) {
        this.people.setItems(people);
    }

    /**
     * Returns and observable list of all the items within the navigator for team
     *
     * @return
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
            new SerializableObservableList<>();

        FolderNavigatorItem peopleItem = new FolderNavigatorItem("People");
        peopleItem.setNext(this.people);

        FolderNavigatorItem skillsItem = new FolderNavigatorItem("Skills");
        skillsItem.setNext(this.skills);

        FolderNavigatorItem timePeriodItem = new FolderNavigatorItem("Allocated Times");
        timePeriodItem.setNext(timePeriods);

        navigatorItems.add(peopleItem);
        navigatorItems.add(skillsItem);
        navigatorItems.add(timePeriodItem);
        return navigatorItems;
    }


    /**
     * Checker for if the team is valid
     *
     * @return
     */
    public boolean validTeam() {
        if ((this.productOwner != null) && (this.scrumMaster != null)) {
            return true;
        }
        return false;
    }



    /**
     * toString
     *
     * @return String representation of Team
     */
    @Override public String toString() {
        return this.getShortName();
    }

    /**
     * Sets the people who have the dev team role
     *
     * @param devTeam
     */
    public void setDevTeam(Collection<Person> devTeam) {
        this.devTeamMembers = devTeam;
    }

    /**
     * Gets the people with the dev team role
     *
     * @return
     */
    public Collection<Person> getDevTeamMembers() {
        return devTeamMembers;
    }

    /**
     * Returns the string to be displayed in the main pane
     *
     * @return the string
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "\nDescription: " + this.description + "\n";


        if (this.timePeriods == null)
            returnString += "This team is not assigned to a project";
        else {
            for (TimePeriod t : timePeriods) {
                returnString += "\n";
                returnString +=
                    ("This team is assigned to the project " + t.getProject().getNameLong()) + "\n";
                returnString += (" With timeperiod " + t.getStartDate() + " to " + t.getEndDate());
            }
        }

        //Need to update string with new timeperiods/projects

        returnString += "\n\n";
        returnString += "This team \"" + this.getNameLong() + "\" contains:\n\n";

        if (this.people.isEmpty())
            returnString += "   0 People\n";
        else {
            returnString += "   " + this.getPeople().size() + " People:\n";
            for (Person p : this.getPeople()) {
                returnString += "       " + p.getFullName() + "\n";
            }
        }
        returnString += "\n";
        if (this.skills.isEmpty())
            returnString += "   0 Skills\n";
        else {
            returnString += "   " + this.getSkills().size() + " Skills:\n";
            for (Skill s : this.getSkills()) {
                returnString += "       " + s.getShortName() + "\n";
            }
        }
        if (this.getScrumMaster() != null)
            returnString += "\nThe team's Scrum Master is: " + this.getScrumMaster();
        else
            returnString += "\nThe team does not have a Scrum Master";
        if (this.getProductOwner() != null)
            returnString += "\nThe team's Product Owner is: " + this.getProductOwner();
        else
            returnString += "\nThe team does not have a Product Owner\n";

        if (this.getTags().isEmpty())
            returnString += "This team has 0 Tags\n";
        else {
            returnString += "This team has " + this.getTags().size() + " Tag(s):\n";
            for (Tag tag : this.getTags()) {
                returnString += "   " + tag.getShortName() + "(" + tag.getColour() + ")\n";
            }
        }

        return returnString;
    }

    @Override public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override public String getShortName() {
        return this.shortName;
    }


    /**
     * When reading the object we need to make sure none of the attributes have lost their values
     * @param stream
     */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.people == null)
            this.people = new SerializableObservableList<>();
        if (this.skills == null)
            this.skills = new SerializableObservableList<>();
        if (this.timePeriods == null)
            this.timePeriods = new SerializableObservableList<>();
        if(this.devTeamMembers == null)
            this.devTeamMembers = new ArrayList<>();
    }

    public double getMedianVelocity(){
        ArrayList<Double> vals = new ArrayList<>();
        for (Sprint s: App.getMainController().currentOrganisation.getSprints()){
            if (s.getTeam() == this){
                vals.add(s.calculateVelocityPoints());
            }
        }
        Collections.sort(vals);
        if (vals.size() >= 1){
            return vals.get((vals.size()-1)/2);
        }
        else
            return  0.0;

    }

    public Node getOverviewPaneGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(30);
        grid.getStyleClass().add("gridPane");

        Button editButton = new Button("Edit");
        editButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, this);
        });
        editButton.getStyleClass().add("gridPaneButton");

        Label titleLabel = new Label(shortName);
        titleLabel.getStyleClass().add("gridPaneTitle");
        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.getStyleClass().add("gridPaneLabel");
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("gridLabel");

        Label shortNameTitle = new Label("Short Name: ");
        shortNameTitle.getStyleClass().add("gridPaneLabel");
        Label shortNameLabel = new Label(this.shortName);
        shortNameLabel.getStyleClass().add("gridLabel");


        //calculates median velocities
        String velocityString = "Fibonacci: ";
        velocityString += Backlog.getScaleValue(getMedianVelocity(), BacklogSheet.fibonacci);
        velocityString +="/Natural: ";
        velocityString += Backlog.getScaleValue(getMedianVelocity(), BacklogSheet.natural);
        velocityString +="/T-Shirt: ";
        velocityString += Backlog.getScaleValue(getMedianVelocity(), BacklogSheet.tShirts);
        Label velocityTitle = new Label("Median Velocity: ");
        velocityTitle.getStyleClass().add("gridPaneLabel");
        Label velocityLabel = new Label(velocityString);
        velocityLabel.getStyleClass().add("gridLabel");

        Label assignedPeopleLabel = new Label("People:");
        assignedPeopleLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> assignedPeopleListView = new ListView<>(people.getObservableList());
        assignedPeopleListView.getStyleClass().add("gridPaneListView");
        assignedPeopleListView.setPrefHeight(53*Math.max(1, assignedPeopleListView.getItems().size()));
        assignedPeopleListView.setPlaceholder(new Label("No Tags"));

        Label allocationsLabel = new Label("Allocations:");
        allocationsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> allocationsListView = new ListView<>(timePeriods.getObservableList());
        allocationsListView.getStyleClass().add("gridPaneListView");
        allocationsListView.setPrefHeight(53*Math.max(1, allocationsListView.getItems().size()));
        allocationsListView.setPlaceholder(new Label("No Tags"));

        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        allocationsListView.setCellFactory(param -> new Team.teamListCell());
        assignedPeopleListView.setCellFactory(param -> new Team.teamListCell());
        tagListView.setCellFactory(param -> new Team.teamListCell());

        grid.getChildren().addAll(titleLabel, editButton, shortNameTitle, shortNameLabel, descriptionTitle, descriptionLabel,
                velocityTitle, velocityLabel, assignedPeopleLabel, assignedPeopleListView, allocationsLabel, allocationsListView,
                tagsLabel, tagListView);

        GridPane.setConstraints(titleLabel, 0, 0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(shortNameTitle, 0, 1);
        GridPane.setConstraints(shortNameLabel, 1, 1);
        GridPane.setConstraints(descriptionTitle, 0, 2);
        GridPane.setConstraints(descriptionLabel, 1, 2);
        GridPane.setConstraints(velocityTitle, 0, 3);
        GridPane.setConstraints(velocityLabel, 1, 3);

        GridPane.setConstraints(assignedPeopleLabel, 0, 4);
        GridPane.setConstraints(assignedPeopleListView, 0, 5);

        GridPane.setConstraints(allocationsLabel, 0, 6);
        GridPane.setConstraints(allocationsListView, 0, 7);

        GridPane.setConstraints(tagsLabel, 0, 8);
        GridPane.setConstraints(tagListView, 0, 9);

        GridPane.setColumnSpan(allocationsListView, 2);
        GridPane.setColumnSpan(assignedPeopleListView, 2);
        GridPane.setColumnSpan(tagListView, 2);


        ColumnConstraints c0 = new ColumnConstraints(400);
        ColumnConstraints c1 = new ColumnConstraints(400);

        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();
        RowConstraints r4 = new RowConstraints();
        RowConstraints r5 = new RowConstraints();
        RowConstraints r6 = new RowConstraints();
        RowConstraints r7 = new RowConstraints();
        RowConstraints r8 = new RowConstraints();
        RowConstraints r9 = new RowConstraints();


        grid.getColumnConstraints().addAll(c0, c1);
        grid.getRowConstraints().addAll(r1, r2, r3, r4, r5, r6, r7, r8, r9);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;

    }

    public class teamListCell extends ListCell<NavigatorItem> {
        @Override public void updateItem(NavigatorItem item, boolean empty) {
            super.updateItem(item, empty);
            Button itemButton;
            if(empty || item == null){
                setText(null);
                setGraphic(null);
            }else if(item instanceof PersonNavigatorItem){
                Person person = (Person) item.getItem();
                if (person == person.getTeam().getProductOwner())
                    itemButton = new Button(person.getShortName() + " (PO)");
                else if (person == person.getTeam().getScrumMaster())
                    itemButton = new Button(person.getShortName() + " (SM)");
                else
                    itemButton = new Button(person.getShortName());

                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, person);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            }
            else if(item instanceof TimePeriodNavigatorItem) {
                TimePeriod timePeriod = (TimePeriod) item.getItem();
                itemButton = new Button(timePeriod.getProject().getShortName() + " (" + timePeriod.getTimePeriod() + ")");
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, timePeriod.getProject());
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            }else if(item instanceof TagNavigatorItem) {
                Tag tag = (Tag) item.getItem();
                itemButton = new Button(tag.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, tag);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            }else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
