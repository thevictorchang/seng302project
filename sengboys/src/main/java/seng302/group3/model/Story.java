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
import seng302.group3.model.navigation.navigator_items.StoryNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ntr24 on 15/05/15.
 */
@Default(required = false)
public class Story extends Element {

    private static final long serialVersionUID = 1806201500006L;

    @Attribute private String shortName;
    @Attribute private String fullName;
    private String description;
    private int priority = -1;

    // the stories estimated value between 0 and 1
    @Attribute private double estimate = -1;

    @Attribute private boolean ready = false;


    private SerializableObservableList<AcceptanceCriteria> acceptanceCriteria = new SerializableObservableList<>();
    private SerializableObservableList<Task> tasks = new SerializableObservableList<>();
    private SerializableObservableList<Story> dependencies = new SerializableObservableList<>();

    private Person creatorPerson;
    private Backlog backlog;
    private int localPriority;


    /**
     * enum for StoryStatus that has been tweaked to give nice strings as the toString, for use in the scrum board
     * story state combobox.
     */
    public static enum StoryStatus {
        DONE("Done"), NOT_STARTED("Not Started"), IN_PROGRESS("In Progress");
        private String value;

        StoryStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.getValue();
        }

        public static StoryStatus getEnum(String value) {
            for(StoryStatus v : values())
                if(v.getValue().equalsIgnoreCase(value)) return v;
            throw new IllegalArgumentException();
        }
    }

    private StoryStatus storyStatus = StoryStatus.NOT_STARTED;
    private StoryStatus prevStoryStatus = StoryStatus.NOT_STARTED;

    /**
     * Constructor with no arguments. Sets short name, long name and description to ""
     */
    public Story() {
        this.setShortName("");
        this.setFullName("");
        this.setDescription("");
    }

    /**
     * Constructor sets the short name, full name and description of a new story
     *
     * @param shortName   - Short name for the story
     * @param fullName    - Full name for the story
     * @param description - Description for the story
     */
    public Story(String shortName, String fullName, String description) {
        this.setShortName(shortName);
        this.setFullName(fullName);
        this.setDescription(description);
    }

    public Story(String story1, String s, String s1, Person person) {
        this.setShortName(story1);
        this.setFullName(s);
        this.setDescription(s1);
        this.creatorPerson = person;

    }

    /**
     * Getter for Acceptance criteria
     * @return Acceptance Criteria
     */
    public SerializableObservableList<AcceptanceCriteria> getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    /**
     * Setter for Acceptance criteria
     * @param acceptanceCriteria
     */
    public void setAcceptanceCriteria(Collection<AcceptanceCriteria> acceptanceCriteria) {
        this.acceptanceCriteria.setItems(acceptanceCriteria);
    }

    /**
     * Adder for Acceptance criteria
     * @param newAC - the new acceptance criteria
     */
    public void addAcceptanceCriteria(AcceptanceCriteria newAC) {
        this.acceptanceCriteria.add(newAC);
    }

    public SerializableObservableList<Story> getDependencies() {
        return dependencies;
    }



    public void setDependencies(Collection<Story> dependencies) {
        this.dependencies.setItems(dependencies);
    }

    public void removeDependency(Story s){
        this.dependencies.remove(s);
    }

    /**
     * Logic to recursively check if making this a dependency of story will create a cycle.
     * @param story
     * @return
     */
    public boolean checkDependencyCycle(Story story){
        boolean result = false;
        if(this == story){
            return true;
        }
        for(Story s : dependencies){
            if(result == false)
                result = s.checkDependencyCycle(story);
            else
                break;
        }
        return result;
    }


    /**
     * Get the short name for the story
     *
     * @return - Short name of the story
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * set the short name of the story
     *
     * @param shortName - new Short name for the story
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * get the full name of the story
     *
     * @return - the stories full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * set the full name of the story
     *
     * @param fullName - new Full name for the story
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * gets the description of the story
     *
     * @return - the stories description
     */
    public String getDescription() {
        return description;
    }

    /**
     * set the description of the story
     *
     * @param description - new description for the story
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * gets the priority for the story
     *
     * @return - the stories priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * sets the stories priority
     *
     * @param priority - new priority for the story
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }


    /**
     * toString returns just the stories short name
     *
     * @return - the stories short name
     */
    public String toString() {
        return this.getShortName();
    }


    /**
     * creates a string representation of the story to be shown as a place holder in the detail pane
     *
     * @return - String representation of the Story to be shown in the detail pane
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "Description: " + this.description + "\n\n";
        returnString += "Created By: " + this.getCreatorPerson().getFullName() + "\n\n";
        if (this.acceptanceCriteria.isEmpty())
            returnString += "This story has 0 acceptance criteria\n";
        else {
            returnString += "This story has " + this.getAcceptanceCriteria().size() + " acceptance criteria:\n";
            for (AcceptanceCriteria a : this.getAcceptanceCriteria()) {
                returnString += "   " + a.getShortName() + "\n";
            }
        }
        returnString += "\n\n";
        if (this.dependencies.isEmpty())
            returnString += "This story has 0 dependencies\n";
        else {
            returnString += "This story has " + this.getDependencies().size() + " dependencies:\n";
            for (Story s : this.getDependencies()) {
                returnString += "   " + s.getShortName() + "\n";
            }
        }

        if (this.getTags().isEmpty())
            returnString += "This story has 0 Tags\n";
        else {
            returnString += "This story has " + this.getTags().size() + " Tag(s):\n";
            for (Tag tag : this.getTags()) {
                returnString += "   " + tag.getShortName() + "(" + tag.getColour() + ")\n";
            }
        }

        String pairProgrammingString = "";
        double totalHoursPaired = 0.0;

        for (Task task : this.getTasks()) {
            for (LoggedTime loggedTime : task.getLoggedHours()) {
                if (loggedTime.getPairHours() != null) { // if this loggedtime object IS pair programming
                    pairProgrammingString += loggedTime.getPerson() + " and " + loggedTime.getPairHours().getPerson() +
                            " in task " + task.getShortName() + " - " + loggedTime.getLoggedTimeInHours() + " hours" + "\n";
                    totalHoursPaired += loggedTime.getLoggedTimeInHours();
                }
            }
        }
        returnString += "\nPair Programming hours: " + totalHoursPaired + " Total Hours" + '\n' + pairProgrammingString;

        String taskTypeString = "\nNumber of People Allocated For each Task Type: \n";
        Integer unspecifiedCount = 0;
        Integer developmentCount = 0;
        Integer testingCount = 0;
        Integer designCount = 0;
        for(Task task : this.getTasks()){
            if(task.getTaskType().equals(Task.Type.UNSPECIFIED)){
                unspecifiedCount += 1;
            } else if(task.getTaskType().equals(Task.Type.DEVELOPMENT)){
                developmentCount += 1;
            } else if(task.getTaskType().equals(Task.Type.TESTING)){
                testingCount += 1;
            } else if(task.getTaskType().equals(Task.Type.DESIGN)) {
                designCount += 1;
            }
        }

        taskTypeString += "  Unspecified Tasks: " + unspecifiedCount + '\n';
        taskTypeString += "  Development Tasks: " + developmentCount + '\n';
        taskTypeString += "  Testing Tasks: " + testingCount + '\n';
        taskTypeString += "  Design Tasks: " + designCount + '\n';

        returnString += taskTypeString;

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

        Label titleLabel = new Label(shortName);
        titleLabel.getStyleClass().add("gridPaneTitle");
        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.getStyleClass().add("gridPaneLabel");
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("gridLabel");

        Label creatorButtonTitle = new Label("Creator: ");
        creatorButtonTitle.getStyleClass().add("gridPaneLabel");
        Button creatorButton = new Button(this.creatorPerson.getShortName());
        creatorButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, creatorPerson);
        });
        creatorButton.getStyleClass().add("gridPaneButton");


        Label returnTitle = new Label("Story Information: ");
        returnTitle.getStyleClass().add("gridPaneLabel");
        //
        String returnString = "";
        String pairProgrammingString = "";
        double totalHoursPaired = 0.0;

        for (Task task : this.getTasks()) {
            for (LoggedTime loggedTime : task.getLoggedHours()) {
                if (loggedTime.getPairHours() != null) { // if this loggedtime object IS pair programming
                    pairProgrammingString += loggedTime.getPerson() + " and " + loggedTime.getPairHours().getPerson() +
                        " in task " + task.getShortName() + " - " + loggedTime.getLoggedTimeInHours() + " hours" + "\n";
                    totalHoursPaired += loggedTime.getLoggedTimeInHours();
                }
            }
        }
        returnString += "\nPair Programming hours: " + totalHoursPaired + " Total Hours" + '\n' + pairProgrammingString;

        String taskTypeString = "\nNumber of People Allocated For each Task Type: \n";
        Integer unspecifiedCount = 0;
        Integer developmentCount = 0;
        Integer testingCount = 0;
        Integer designCount = 0;
        for(Task task : this.getTasks()){
            if(task.getTaskType().equals(Task.Type.UNSPECIFIED)){
                unspecifiedCount += 1;
            } else if(task.getTaskType().equals(Task.Type.DEVELOPMENT)){
                developmentCount += 1;
            } else if(task.getTaskType().equals(Task.Type.TESTING)){
                testingCount += 1;
            } else if(task.getTaskType().equals(Task.Type.DESIGN)) {
                designCount += 1;
            }
        }

        taskTypeString += "  Unspecified Tasks: " + unspecifiedCount + '\n';
        taskTypeString += "  Development Tasks: " + developmentCount + '\n';
        taskTypeString += "  Testing Tasks: " + testingCount + '\n';
        taskTypeString += "  Design Tasks: " + designCount + '\n';

        returnString += taskTypeString;
        Label returnLabel = new Label(returnString);
        returnLabel.getStyleClass().add("gridLabel");
        //
        Label acceptanceCriteriaTitle = new Label("Acceptance Criteria: ");
        acceptanceCriteriaTitle.getStyleClass().add("gridPaneLabel");

        String acceptanceCriteriaString = "";
        if (this.acceptanceCriteria.isEmpty())
            acceptanceCriteriaString += "This story has 0 acceptance criteria\n";
        else {
            acceptanceCriteriaString += "This story has " + this.getAcceptanceCriteria().size() + " acceptance criteria:\n";
            for (AcceptanceCriteria a : this.getAcceptanceCriteria()) {
                acceptanceCriteriaString += "   " + a.getShortName() + "\n";
            }
        }
        Label acceptanceCriteriaLabel = new Label(acceptanceCriteriaString);
        acceptanceCriteriaLabel.getStyleClass().add("gridLabel");

        Label dependenciesLabel = new Label("Dependencies:");
        dependenciesLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> dependenciesListView = new ListView<>(dependencies.getObservableList());
        dependenciesListView.getStyleClass().add("gridPaneListView");
        dependenciesListView.setPrefHeight(53*Math.max(1, dependenciesListView.getItems().size()));
        dependenciesListView.setPlaceholder(new Label("No Dependencies"));

        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        tagListView.setCellFactory(param -> new storyListCell());
        dependenciesListView.setCellFactory(param -> new storyListCell());

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle,descriptionLabel,
            creatorButtonTitle,creatorButton, returnTitle, returnLabel, acceptanceCriteriaTitle,
            acceptanceCriteriaLabel, dependenciesLabel,dependenciesListView,tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle,0,1);
        GridPane.setConstraints(descriptionLabel, 1, 1);
        GridPane.setConstraints(creatorButtonTitle,0,2);
        GridPane.setConstraints(creatorButton, 1, 2);
        GridPane.setConstraints(returnTitle, 0, 3);
        GridPane.setConstraints(returnLabel, 1, 3);

        GridPane.setConstraints(acceptanceCriteriaTitle,0,4);
        GridPane.setConstraints(acceptanceCriteriaLabel,1,4);
        GridPane.setConstraints(dependenciesLabel,0,5);
        GridPane.setConstraints(dependenciesListView,0,6);
        GridPane.setConstraints(tagsLabel,0,7);
        GridPane.setConstraints(tagListView,0,8);

        GridPane.setColumnSpan(dependenciesListView, 2);
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

    public class storyListCell extends ListCell<NavigatorItem> {
        @Override public void updateItem(NavigatorItem item, boolean empty) {
            super.updateItem(item, empty);
            Button itemButton;
            if(empty || item == null){
                setText(null);
                setGraphic(null);
            }else if(item instanceof TagNavigatorItem){
                Tag tag = (Tag) item.getItem();
                itemButton = new Button(tag.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, tag);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            }else if(item instanceof StoryNavigatorItem){
                Story story = (Story) item.getItem();
                itemButton = new Button(story.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, story);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }

    /**
     * get the creator person who is set to this story
     *
     * @return - Person assigned to this story
     */
    public Person getCreatorPerson() {
        return creatorPerson;
    }

    /**
     * set the creator person for this story
     *
     * @param creatorPerson - person to set as the creator of this story
     */
    public void setCreatorPerson(Person creatorPerson) {
        this.creatorPerson = creatorPerson;
    }


    /**
     * gets the backlog this story is currently assigned to
     *
     * @return - backlog the story is assigned to
     */
    public Backlog getBacklog() {
        return backlog;
    }

    /**
     * sets the backlog the story should be assigned to
     *
     * @param backlog - new backlog to assign the story to
     */
    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    /**
     * Returns the estimate value for this story, a value between 0 and 1.
     * @return - estimate value
     */
    public double getEstimate() {
        return estimate;
    }

    /**
     * set the estimate value for the story
     * @param estimate - estimate value between 0 and 1
     */
    public void setEstimate(double estimate) {
        this.estimate = estimate;
    }

    /**
     * Check if the story is classed as ready
     * @return boolean ready
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Mark the story as ready or not ready
     * @param ready - boolean which indicates readiness
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }


    /**
     * When reading the object we need to make sure none of the attributes have lost their values
     * @param stream
     */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.acceptanceCriteria == null)
            this.acceptanceCriteria = new SerializableObservableList<>();
        if(this.dependencies == null)
            this.dependencies = new SerializableObservableList<>();
        if(this.tasks == null)
            this.tasks = new SerializableObservableList<>();
        if(this.storyStatus == null)
            this.storyStatus = StoryStatus.NOT_STARTED;
    }

    /**
     * Checks the story priority given against itself to see if there is a higher priority, then calls this method
     * with its own dependencies.
     * @param priority
     * @return
     */
    public boolean dependsOnLowerPriority(int priority) {
        boolean result = false;
        if(priority < this.priority){
            return true;
        }
        for(Story s : dependencies){
            if(result == false)
                result = s.dependsOnLowerPriority(priority);
        }
        return result;
    }

    /**
     * A setter for the tasks list.
     * @param tasks
     */
    public void setTasks(Collection<Task> tasks) {
        this.tasks.setItems(tasks);
    }

    /**
     * Makes the next step in the navigator which will contain a list of the tasks.
     * @return
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
                new SerializableObservableList<>();

        FolderNavigatorItem taskItems = new FolderNavigatorItem("Tasks");
        taskItems.setNext(this.tasks);

        navigatorItems.add(taskItems);
        return navigatorItems;
    }

    /**
     * Adds a given task to the list.
     * @param task
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * A getter for tasks which returns a serializableObservable list of tasks
     * @return
     */
    public SerializableObservableList<Task> getTasks() {
        return tasks;
    }

    /**
     * Getter for the Story Status
     * @return
     */
    public StoryStatus getStoryStatus() {
        return storyStatus;
    }

    /**
     * Setter for the story Status
     * @param storyStatus
     */
    public void setStoryStatus(StoryStatus storyStatus) {
        this.prevStoryStatus = this.storyStatus;
        this.storyStatus = storyStatus;
        if (storyStatus == StoryStatus.DONE) {
            if (this.prevStoryStatus == StoryStatus.NOT_STARTED || this.prevStoryStatus == StoryStatus.IN_PROGRESS) {
                //Add story points value to total completed in sprint
                for (Sprint sprint : App.getMainController().currentOrganisation.getSprints()) {
                    for (Story containingStory : sprint.getStories()) {
                        if (containingStory == this) {
                            sprint.addTotalStoryPointsCompleted(this.getEstimate());
                        }
                    }
                }
            }
        }
        else if (storyStatus == StoryStatus.IN_PROGRESS || storyStatus == StoryStatus.NOT_STARTED) {
            if (this.prevStoryStatus == StoryStatus.DONE) {
                //Remove story points value from total completed in sprint
                for (Sprint sprint : App.getMainController().currentOrganisation.getSprints()) {
                    if (sprint.getStories().contains(this)) {
                        sprint.removeTotalStoryPointsCompleted(this.getEstimate());
                    }
                }
            }
        }
    }
}
