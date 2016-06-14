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
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;
import seng302.group3.model.navigation.navigator_items.SkillNavigatorItem;
import seng302.group3.model.navigation.navigator_items.StoryNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;

/**
 * Created by vch51 on 17/03/15.
 * <p>
 * Structure for the Person object. a Person can be added to a Organisation. Each Person has a nameShort, which is a shorter,
 * codename-like identifier (which is unique), a fullName, a userID and a biography. Each Person also has a collection of
 * Skills (to be further implemented in a future story) which can help a manager sort their staff.
 */
@Default(required = false) public class Person extends Element {

    private static final long serialVersionUID = 1806201500002L;

    @Attribute private String shortName = ""; // non null, unique
    @Attribute private String fullName = "";
    @Attribute private String userID = "";

    private SerializableObservableList<Skill> skills = new SerializableObservableList<>();

    private String biography;
    private Team team;
    private Project project; //The project the person belongs to

    private SerializableObservableList<LoggedTime> loggedTimes = new SerializableObservableList<>();



    /**
     * Constructor for empty person class
     */
    public Person() {

    }

    /**
     * This copys the given person into a new person object
     *
     * @param copyData - the person to be copied
     */
    public Person(Person copyData) {
        this.shortName = copyData.getShortName();
        this.fullName = copyData.getFullName();
        this.userID = copyData.getUserID();
        this.skills = (SerializableObservableList) copyData.getSkills();
        this.biography = copyData.getBiography();
    }

    /**
     * Constructor for person
     *
     * @param name
     */
    public Person(String name) {
        this.setShortName(name);
    }

    /**
     * Constructor for person
     *
     * @param name
     * @param full
     */
    public Person(String name, String full) {
        this.setShortName(name);
        this.fullName = full;
    }

    /**
     * Constructor for person
     *
     * @param name
     * @param full
     * @param user
     */
    public Person(String name, String full, String user) {

        this.setShortName(name);
        this.fullName = full;
        this.userID = user;
    }

    /**
     * Getter for the full name of the person
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Setter for the full name
     *
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    /**
     * Getter for the user ID
     *
     * @return the user ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Setter for the user ID
     *
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Getter for the list of skills for a person
     *
     * @return collection of skills
     */
    public Collection<Skill> getSkills() {
        return this.skills;
    }

    /**
     * Method for adding a skill to the skills collection for a person
     *
     * @param skill
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
        if (this.getTeam() != null)
            this.getTeam().updateSkills();
        if (this.getProject() != null)
            this.getProject().updateSkills();
    }

    /**
     * Method for removing a skill from the collection of skills
     *
     * @param skill
     */
    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
        if (this.getTeam() != null)
            this.getTeam().updateSkills();
        if (this.getProject() != null)
            this.getProject().updateSkills();
    }

    /**
     * Getter for the biography of a person
     *
     * @return
     */
    public String getBiography() {
        return biography;
    }

    /**
     * Setter for the biography of a person
     *
     * @param biography
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * Getter for team a person belongs to
     *
     * @return the team the person is in
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * Setter for the team a person belongs to
     *
     * @param team - the team the person is going into
     */
    public void setTeam(Team team) {
        //this.team.removePerson(this);
        this.team = team;
    }

    /**
     * Sets the project the person is working on
     *
     * @param project - project the person is being added to
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * the Person will have a project which is the teams current project if it has a team.
     */
    public void updateProject() {
        if (team != null) {
            this.project = team.getCurrentProject();
        }
    }

    /**
     * Gets the project that the person is working on
     *
     * @return the project the person is working on
     */
    public Project getProject() {
        if (team != null) {
            return team.getCurrentProject();
        } else {
            return project;
        }
    }


    /**
     * Returns and observable list of all the items within the navigator for person
     *
     * @return
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
            new SerializableObservableList<>();

        FolderNavigatorItem skillsItem = new FolderNavigatorItem("Skills");
        skillsItem.setNext(this.skills);

        FolderNavigatorItem loggedTimesItem = new FolderNavigatorItem("Logged Time");
        loggedTimesItem.setNext(this.loggedTimes);

        navigatorItems.add(skillsItem);
        navigatorItems.add(loggedTimesItem);

        return navigatorItems;
    }


    /**
     * toString
     *
     * @return toString of Person
     */
    @Override public String toString() {
        return this.getShortName();
    }

    public void setSkills(Collection<Skill> skills) {
        this.skills.setItems(skills);
    }

    /**
     * Returns the string to be displayed in the main pane
     *
     * @return the string
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "\nUser ID: " + this.userID + "\n";

        if (this.getProject() == null)
            returnString += "This person is not assigned to a project";
        else
            returnString += "This person is assigned to the project " + this.getProject().getNameLong();

        returnString += "\n\n";

        if (this.team == null)
            returnString += "This person is not assigned to a team";
        else
            returnString += "This person is assigned to the team " + this.team.getNameLong();

        returnString += "\n\n";

        if (this.skills.isEmpty())
            returnString += "This person has 0 Skills\n";
        else {
            returnString += "This person has " + this.getSkills().size() + " Skill(s):\n";
            for (Skill s : this.getSkills()) {
                returnString += "   " + s.getShortName() + "\n";
            }
        }

        if (this.getTags().isEmpty())
            returnString += "This person has 0 Tags\n";
        else {
            returnString += "This person has " + this.getTags().size() + " Tag(s):\n";
            for (Tag tag : this.getTags()) {
                returnString += "   " + tag.getShortName() + "(" + tag.getColour() + ")\n";
            }
        }

        returnString += "\nPair Hours\n";

        double sprintPairHours = 0.0;
        double storyPairHours;
        for(Sprint sprint: App.getMainController().currentOrganisation.getSprints())
            if (sprint.getTeam() == this.team) {
                for (Task task : sprint.getTasks())
                    for (LoggedTime loggedTime : task.getLoggedHours())
                        if (loggedTime.getPairHours() != null && loggedTime.getPerson() == this)
                            sprintPairHours += loggedTime.getLoggedTimeInHours();
                for (Story story : sprint.getStories()) {
                    storyPairHours  = 0.0;
                    for (Task task : story.getTasks())
                        for (LoggedTime loggedTime : task.getLoggedHours()) {
                            if (loggedTime.getPairHours() != null){
                                if(loggedTime.getPerson() == this)
                                    storyPairHours += loggedTime.getLoggedTimeInHours();
                            }


                        }
                    returnString += "Story - " + story.getShortName()+ ": " + storyPairHours + " hours\n";
                }
            }

        returnString += "Sprint - " + sprintPairHours + " hours\n";

        if(team != null){
            for(Person person:this.getTeam().getPeople()) {
                if (person != this) {
                    double personPairHours = 0.0;
                    for (Sprint sprint : App.getMainController().currentOrganisation.getSprints()) {
                        if (sprint.getTeam() == this.team) {
                            for (Task task : sprint.getTasks())
                                for (LoggedTime loggedTime : task.getLoggedHours())
                                    if (loggedTime.getPairHours() != null
                                        && loggedTime.getPerson() == this
                                        && loggedTime.getPairHours().getPerson() == person) {
                                        personPairHours += loggedTime.getLoggedTimeInHours();
                                    }
                            for (Story story : sprint.getStories())
                                for (Task task : story.getTasks())
                                    for (LoggedTime loggedTime : task.getLoggedHours())
                                        if (loggedTime.getPairHours() != null
                                            && loggedTime.getPairHours().getPerson() == person
                                            && loggedTime.getPerson() == this) {
                                            personPairHours += loggedTime.getLoggedTimeInHours();
                                        }
                        }
                    }
                    returnString += "Person - "+person+": " + personPairHours + " hours\n";
                }
            }
        }


        return returnString;
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

        Label titleLabel = new Label(this.fullName);
        titleLabel.getStyleClass().add("gridPaneTitle");

        Label shortNameTitle = new Label("Short Name: ");
        shortNameTitle.getStyleClass().add("gridPaneLabel");
        Label shortNameLabel = new Label(this.shortName);
        shortNameLabel.getStyleClass().add("gridLabel");
        Label userIDTitle = new Label("User ID: ");
        userIDTitle.getStyleClass().add("gridPaneLabel");
        Label userIDLabel = new Label(this.userID);
        userIDLabel.getStyleClass().add("gridLabel");

        Label projectButtonTitle = new Label("Project: ");
        projectButtonTitle.getStyleClass().add("gridPaneLabel");
        Button projectButton;
        if(this.getProject() != null){
            projectButton = new Button(this.getProject().getShortName());
            projectButton.setOnAction((event) -> {
                new Editor().edit(App.getMainController().currentOrganisation, this.getProject());
            });
        }else{
            projectButton = new Button("No Project Allocated");
            projectButton.setDisable(true);
        }
        projectButton.getStyleClass().add("gridPaneButton");

        Label teamButtonTitle = new Label("Team: ");
        teamButtonTitle.getStyleClass().add("gridPaneLabel");
        Button teamButton;
        if(this.getTeam() != null){
            teamButton = new Button(this.getTeam().getShortName());
            teamButton.setOnAction((event) -> {
                new Editor().edit(App.getMainController().currentOrganisation, this.getTeam());
            });
        }else{
            teamButton = new Button("No Project Allocated");
            teamButton.setDisable(true);
        }
        teamButton.getStyleClass().add("gridPaneButton");

        //SHORTEN THIS?
        String returnString = "";
        Label pairTitle = new Label("Pair Hours: ");
        pairTitle.getStyleClass().add("gridPaneLabel");

        double sprintPairHours = 0.0;
        double storyPairHours;
        for(Sprint sprint: App.getMainController().currentOrganisation.getSprints())
            if (sprint.getTeam() == this.team) {
                for (Task task : sprint.getTasks())
                    for (LoggedTime loggedTime : task.getLoggedHours())
                        if (loggedTime.getPairHours() != null && loggedTime.getPerson() == this)
                            sprintPairHours += loggedTime.getLoggedTimeInHours();
                for (Story story : sprint.getStories()) {
                    storyPairHours  = 0.0;
                    for (Task task : story.getTasks())
                        for (LoggedTime loggedTime : task.getLoggedHours()) {
                            if (loggedTime.getPairHours() != null){
                                if(loggedTime.getPerson() == this)
                                    storyPairHours += loggedTime.getLoggedTimeInHours();
                            }


                        }
                    returnString += "Story - " + story.getShortName()+ ": " + storyPairHours + " hours\n";
                }
            }

        returnString += "Sprint - " + sprintPairHours + " hours\n";

        if(team != null){
            for(Person person:this.getTeam().getPeople()) {
                if (person != this) {
                    double personPairHours = 0.0;
                    for (Sprint sprint : App.getMainController().currentOrganisation.getSprints()) {
                        if (sprint.getTeam() == this.team) {
                            for (Task task : sprint.getTasks())
                                for (LoggedTime loggedTime : task.getLoggedHours())
                                    if (loggedTime.getPairHours() != null
                                        && loggedTime.getPerson() == this
                                        && loggedTime.getPairHours().getPerson() == person) {
                                        personPairHours += loggedTime.getLoggedTimeInHours();
                                    }
                            for (Story story : sprint.getStories())
                                for (Task task : story.getTasks())
                                    for (LoggedTime loggedTime : task.getLoggedHours())
                                        if (loggedTime.getPairHours() != null
                                            && loggedTime.getPairHours().getPerson() == person
                                            && loggedTime.getPerson() == this) {
                                            personPairHours += loggedTime.getLoggedTimeInHours();
                                        }
                        }
                    }
                    returnString += "Person - "+person+": " + personPairHours + " hours\n";
                }
            }
        }

        Label returnLabel = new Label(returnString);
        //
        returnLabel.getStyleClass().add("gridLabel");

        Label skillsLabel = new Label("Skills");
        ListView<NavigatorItem> skillsListView = new ListView<>(skills.getObservableList());
        skillsListView.getStyleClass().add("gridPaneListView");
        skillsListView.setPrefHeight(53*Math.max(1, skillsListView.getItems().size()));
        skillsListView.setPlaceholder(new Label("No Skills"));

        Label tagsLabel = new Label("Tags");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        skillsListView.setCellFactory(param -> new backlogListCell());
        tagListView.setCellFactory(param -> new backlogListCell());



        grid.getChildren().addAll(titleLabel,editButton,shortNameTitle,shortNameLabel,userIDTitle,
            userIDLabel,projectButtonTitle,projectButton,teamButtonTitle,teamButton,
            skillsLabel,skillsListView,tagsLabel,tagListView,returnLabel,pairTitle);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(shortNameTitle,0,1);
        GridPane.setConstraints(shortNameLabel, 1, 1);
        GridPane.setConstraints(userIDTitle,0,2);
        GridPane.setConstraints(userIDLabel, 1, 2);
        GridPane.setConstraints(projectButtonTitle,0,3);
        GridPane.setConstraints(projectButton, 1, 3);
        GridPane.setConstraints(teamButtonTitle,0,4);
        GridPane.setConstraints(teamButton, 1, 4);
        GridPane.setConstraints(returnLabel,1,5);
        GridPane.setConstraints(pairTitle,0,5);
        GridPane.setConstraints(skillsLabel,0,6);
        GridPane.setConstraints(skillsListView,0,7);
        GridPane.setConstraints(tagsLabel,0,8);
        GridPane.setConstraints(tagListView,0,9);

        GridPane.setColumnSpan(skillsListView, 2);
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


        grid.getColumnConstraints().addAll(c0,c1);
        grid.getRowConstraints().addAll(r1,r2,r3,r4,r5,r6,r7,r8,r9,r10);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class backlogListCell extends ListCell<NavigatorItem> {
        @Override public void updateItem(NavigatorItem item, boolean empty) {
            super.updateItem(item, empty);
            Button itemButton;
            if(empty || item == null){
                setText(null);
                setGraphic(null);
            } else if(item instanceof SkillNavigatorItem){
                Skill skill = (Skill) item.getItem();
                itemButton = new Button(skill.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, skill);
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


    @Override public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override public String getShortName() {
        return shortName;
    }

    /**
     * When reading the object we need to make sure none of the attributes have lost their values
     * @param stream
     */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.skills == null)
            this.skills = new SerializableObservableList<>();
        if (this.loggedTimes == null)
            this.loggedTimes = new SerializableObservableList<>();
    }

    public SerializableObservableList<LoggedTime> getLoggedTimes() {
        return loggedTimes;
    }

    public void setLoggedTimes(SerializableObservableList<LoggedTime> loggedTimes) {
        this.loggedTimes = loggedTimes;
    }
}
