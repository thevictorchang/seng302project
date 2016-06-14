package seng302.group3.model;

import javafx.collections.ObservableList;
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
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by epa31 on 29/04/15.
 */
@Default(required = false) public class Project extends Element {

    private static final long serialVersionUID = 1806201500003L;

    @Attribute private String shortName; // non null, unique
    @Attribute private String nameLong;
    private String description;

    private SerializableObservableList<Team> teams = new SerializableObservableList<>();

    private SerializableObservableList<TimePeriod> timePeriods = new SerializableObservableList<>();

    private SerializableObservableList<Release> releases = new SerializableObservableList<>();
    private SerializableObservableList<Person> people = new SerializableObservableList<>();
    private SerializableObservableList<Skill> skills = new SerializableObservableList<>();


    /**
     * This copies the given project into a new project object
     *
     * @param copyData - the project to be copied
     */
    public Project(Project copyData) {
        this.shortName = copyData.getShortName();
        this.nameLong = copyData.getNameLong();
        this.description = copyData.getDescription();
    }

    public Project(String shortName) {
        this.shortName = shortName;
    }

    ObservableList<NavigatorItem> nextNavigatorItems;

    /**
     * Project constructor
     *
     * @param shortName
     * @param longName
     * @param description
     */
    public Project(String shortName, String longName, String description) {
        this.shortName = shortName;
        this.nameLong = longName;
        this.description = description;
    }

    public Project() {
        this.shortName = "";
        this.nameLong = "";
        this.description = "";
    }

    /**
     * Override method of equals function to compare two Project object
     *
     * @return boolean result
     * @param object
     */
    @Override public boolean equals(Object object) {
        if (object instanceof Project) {
            Project project = (Project) object;
            if ((this.getShortName() + this.getNameLong() + this.getDescription() + this.getTeams())
                .equals(project.getShortName() + project.getNameLong() + project.getDescription()
                    + project.getTeams())) {
                return true;
            } else {
                return false;
            }

        }
        return false;
    }


    /**
     * Getter for nameLong
     *
     * @return nameLong
     */
    public String getNameLong() {
        return nameLong;
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
     * Getter for teams using timeperiods
     *
     * @return
     */
    public Collection<Team> getTeams() {
        return this.teams;
    }



    /**
     * Setter for nameLong
     *
     * @param nameLong
     */
    public void setNameLong(String nameLong) {
        this.nameLong = nameLong;
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
     * Remove a team from the teams list
     *
     * @param team
     */
    public void removeTeam(Team team, TimePeriod timePeriod) {
        this.teams.remove(team);
        timePeriods.remove(timePeriod);
        updateSkills();
    }

    /**
     * Adds a release to the project
     *
     * @param release
     */
    public void addRelease(Release release) {
        this.releases.add(release);
    }

    /**
     * Gets the releases in the project
     *
     * @return the releases
     */
    public Collection<Release> getReleases() {
        return releases;
    }

    /**
     * removes a release from the project
     *
     * @param release - release to remove from the project
     */
    public void removeRelease(Release release) {
        this.releases.remove(release);
    }

    /**
     * A getter for timeperiods
     *
     * @return
     */
    public SerializableObservableList<TimePeriod> getTimePeriods() {
        return timePeriods;
    }

    /**
     * A setter for time periods
     *
     * @param timePeriods
     */
    public void setTimePeriods(SerializableObservableList<TimePeriod> timePeriods) {
        this.timePeriods = timePeriods;
    }

    /**
     * A method which adds a given time period to the list timeperiods
     *
     * @param timePeriod
     */
    public void addTimePeriod(TimePeriod timePeriod) {
        if (timePeriod.getTeam() != null) {
            timePeriod.setProject(this);
            this.checkNewTeam(timePeriod);
            this.timePeriods.add(timePeriod);
        }
    }

    /**
     * Removes the timeperiod and then checks calls checkLastTeam
     * @param timePeriod
     */
    public void removeTimePeriod(TimePeriod timePeriod){
        timePeriods.remove(timePeriod);
        checkLastTeam(timePeriod);
    }

    /**
     * Checks to see if the timeperiod given is the last connection to a team.
     *
     * @param timePeriod
     */
    public void checkLastTeam(TimePeriod timePeriod) {
        //Checks to see if the time period is the last link to the team
        boolean last = true;
        for (Iterator<TimePeriod> it = timePeriods.iterator(); it.hasNext(); ) {
            TimePeriod itTime = it.next();
            if (itTime.getTeam() == timePeriod.getTeam()) {

                last = false;
            }
        }
        if (last) {
            teams.remove(timePeriod.getTeam());
            for (Person person : timePeriod.getTeam().getPeople()) {
                person.setProject(person.getTeam().getCurrentProject());
                this.removePerson(person);
            }
            updateSkills();
        }
    }

    /**
     * Checks the given time period to see if the team involved is already in the project and
     * if it is not then adds and updates.
     *
     * @param timePeriod
     */
    public void checkNewTeam(TimePeriod timePeriod) {

        boolean first = true;
        for (Iterator<Team> it = teams.iterator(); it.hasNext(); ) {
            Team itTeam = it.next();
            if (itTeam == timePeriod.getTeam()) {
                first = false;
            }
        }
        if (first) {
            teams.add(timePeriod.getTeam());
            for (Person person : timePeriod.getTeam().getPeople()) {
                person.setProject(person.getTeam().getCurrentProject());
                this.addPerson(person);
            }
            updateSkills();
        }
    }



    /**
     * toString
     *
     * @return toString of Project
     */
    @Override public String toString() {
        return this.getShortName();
    }


    /**
     * Returns a list of the navigator items which will fill when this project is selected in the
     * Navigator
     *
     * @return
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
            new SerializableObservableList<>();

        FolderNavigatorItem releasesItem = new FolderNavigatorItem("Releases");
        releasesItem.setNext(this.releases);

        FolderNavigatorItem teamsItem = new FolderNavigatorItem("Teams");
        teamsItem.setNext(this.teams);

        FolderNavigatorItem peopleItem = new FolderNavigatorItem("People");
        peopleItem.setNext(this.people);

        FolderNavigatorItem skillsItem = new FolderNavigatorItem("Skills");
        skillsItem.setNext(this.skills);

        FolderNavigatorItem timePeriodItem = new FolderNavigatorItem("Allocated Times");
        timePeriodItem.setNext(timePeriods);

        FolderNavigatorItem currentTeamsItem = new FolderNavigatorItem("Current Allocations");
        currentTeamsItem.setNext(this.getCurrentTeams());


        navigatorItems.add(releasesItem);
        navigatorItems.add(teamsItem);
        navigatorItems.add(peopleItem);
        navigatorItems.add(skillsItem);
        navigatorItems.add(timePeriodItem);
        navigatorItems.add(currentTeamsItem);
        return navigatorItems;
    }

    /**
     * Gets the people working on the project
     *
     * @return the people working on the project.
     */
    public Collection<Person> getPeople() {
        return this.people;
    }

    /**
     * Update the skills of the team from the people in the team.
     */
    public void updateSkills() {
        skills.clear();
        for (Person person : people) {
            for (Skill skill : person.getSkills()) {
                if (!skills.contains(skill)) {
                    this.skills.add(skill);
                }
            }
        }
    }

    /**
     * Return the skills that people in the project have
     *
     * @return skills
     */
    public Collection<Skill> getSkills() {
        return skills;
    }

    /**
     * Remove a Person from the people list
     *
     * @param person
     */
    public void removePerson(Person person) {
        this.people.remove(person);
        updateSkills();
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
     * Returns the string to be displayed in the main pane
     *
     * @return the string
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "\nDescription: " + this.description + "\n";
        returnString += "This project \"" + this.nameLong + "\" contains:\n\n";
        if (this.teams.isEmpty())
            returnString += "   0 Teams\n";
        else {
            returnString += "   " + this.getTeams().size() + " Team(s):\n";
            for (Team t : this.getTeams()) {
                returnString += "       " + t.getNameLong() + "\n";
            }
        }
        returnString += "\n";
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
            returnString += "   " + this.getSkills().size() + " Skill(s):\n";
            for (Skill s : this.getSkills()) {
                returnString += "       " + s.getShortName() + "\n";
            }
        }
        returnString += "\n";
        if (this.releases.isEmpty())
            returnString += "   0 Releases\n";
        else {
            returnString += "   " + this.getReleases().size() + " Release(s):\n";
            for (Release r : this.getReleases()) {
                returnString += "       " + r.getShortName() + "\n";
            }
        }

        if (this.getTags().isEmpty())
            returnString += "This project has 0 Tags\n";
        else {
            returnString += "This project has " + this.getTags().size() + " Tag(s):\n";
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
     * Sets the current list of teams to a given list
     *
     * @param teams
     */
    public void setTeams(Collection<Team> teams) {
        this.teams = (SerializableObservableList) teams;
    }

    /**
     * Finds and returns all teams that are currently assigned to this project at the current time
     *
     * @return
     */
    public SerializableObservableList<Team> getCurrentTeams() {
        SerializableObservableList<Team> currentTeams = new SerializableObservableList<>();
        for (TimePeriod timePeriod : timePeriods) {
            if (timePeriod.containsDate(LocalDate.now())) {
                currentTeams.add(timePeriod.getTeam());
            }
        }
        return currentTeams;
    }


    /**
     * When reading the object we need to make sure none of the attributes have lost their values
     * @param stream
     */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.teams == null)
            this.teams = new SerializableObservableList<>();
        if (this.timePeriods == null)
            this.timePeriods = new SerializableObservableList<>();
        if (this.releases == null)
            this.releases = new SerializableObservableList<>();
        if (this.people == null)
            this.people = new SerializableObservableList<>();
        if (this.skills == null)
            this.skills = new SerializableObservableList<>();
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


        Label titleLabel = new Label(this.nameLong);
        titleLabel.getStyleClass().add("gridPaneTitle");


        Label shortNameTitle = new Label("Short Name: ");
        shortNameTitle.getStyleClass().add("gridPaneLabel");
        Label shortNameLabel = new Label(this.shortName);
        shortNameLabel.getStyleClass().add("gridLabel");
        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.getStyleClass().add("gridPaneLabel");
        Label descriptionLabel = new Label(this.description);
        descriptionLabel.getStyleClass().add("gridLabel");


        Label teamsLabel = new Label("Teams");
        ListView<NavigatorItem> teamsListView = new ListView<>(teams.getObservableList());
        teamsListView.getStyleClass().add("gridPaneListView");
        teamsListView.setPrefHeight(53*Math.max(1, teamsListView.getItems().size()));
        teamsListView.setPlaceholder(new Label("No Tags"));

        Label peopleLabel = new Label("People");
        ListView<NavigatorItem> peopleListView = new ListView<>(people.getObservableList());
        peopleListView.getStyleClass().add("gridPaneListView");
        peopleListView.setPrefHeight(53*Math.max(1, peopleListView.getItems().size()));
        peopleListView.setPlaceholder(new Label("No Tags"));

        Label skillsLabel = new Label("Skills");
        ListView<NavigatorItem> skillsListView = new ListView<>(skills.getObservableList());
        skillsListView.getStyleClass().add("gridPaneListView");
        skillsListView.setPrefHeight(53*Math.max(1, skillsListView.getItems().size()));
        skillsListView.setPlaceholder(new Label("No Tags"));

        Label releasesLabel = new Label("Releases");
        ListView<NavigatorItem> releaseListView = new ListView<>(releases.getObservableList());
        releaseListView.getStyleClass().add("gridPaneListView");
        releaseListView.setPrefHeight(53*Math.max(1, releaseListView.getItems().size()));
        releaseListView.setPlaceholder(new Label("No Tags"));

        Label tagsLabel = new Label("Tags");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        teamsListView.setCellFactory(param -> new projectListCell());
        peopleListView.setCellFactory(param -> new projectListCell());
        skillsListView.setCellFactory(param -> new projectListCell());
        releaseListView.setCellFactory(param -> new projectListCell());
        tagListView.setCellFactory(param -> new projectListCell());

        grid.getChildren().addAll(titleLabel,editButton,shortNameTitle,shortNameLabel,descriptionTitle,
            descriptionLabel,teamsLabel,teamsListView,peopleLabel,peopleListView,
            skillsLabel,skillsListView,releasesLabel,releaseListView, tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(shortNameTitle,0,1);
        GridPane.setConstraints(shortNameLabel, 1, 1);
        GridPane.setConstraints(descriptionTitle,0,2);
        GridPane.setConstraints(descriptionLabel, 1, 2);

        GridPane.setConstraints(teamsLabel,0,3);
        GridPane.setConstraints(teamsListView,0,4);
        GridPane.setConstraints(peopleLabel,0,5);
        GridPane.setConstraints(peopleListView,0,6);
        GridPane.setConstraints(skillsLabel,0,7);
        GridPane.setConstraints(skillsListView,0,8);
        GridPane.setConstraints(releasesLabel,0,9);
        GridPane.setConstraints(releaseListView,0,10);
        GridPane.setConstraints(tagsLabel,0,11);
        GridPane.setConstraints(tagListView,0,12);

        GridPane.setColumnSpan(teamsListView, 2);
        GridPane.setColumnSpan(peopleListView, 2);
        GridPane.setColumnSpan(skillsListView, 2);
        GridPane.setColumnSpan(releaseListView, 2);
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
        RowConstraints r10 = new RowConstraints();
        RowConstraints r11 = new RowConstraints();
        RowConstraints r12 = new RowConstraints();
        RowConstraints r13 = new RowConstraints();

        grid.getColumnConstraints().addAll(c0,c1);
        grid.getRowConstraints().addAll(r1,r2,r3,r4,r5,r6,r7,r8,r9,r10,r11,r12,r13);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class projectListCell extends ListCell<NavigatorItem> {
        @Override public void updateItem(NavigatorItem item, boolean empty) {
            super.updateItem(item, empty);
            Button itemButton;
            if(empty || item == null){
                setText(null);
                setGraphic(null);
            }else if(item instanceof TeamNavigatorItem){
                Team team = (Team) item.getItem();
                itemButton = new Button(team.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, team);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            }else if(item instanceof PersonNavigatorItem){
                Person person = (Person) item.getItem();
                itemButton = new Button(person.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, person);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            }else if(item instanceof SkillNavigatorItem){
                Skill skill = (Skill) item.getItem();
                itemButton = new Button(skill.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, skill);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            } else if(item instanceof ReleaseNavigatorItem){
                Release release = (Release) item.getItem();
                itemButton = new Button(release.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, release);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            } else if(item instanceof TagNavigatorItem){
                Tag tag = (Tag) item.getItem();
                itemButton = new Button(tag.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, tag);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
