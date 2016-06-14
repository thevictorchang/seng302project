package seng302.group3.model;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import seng302.group3.controller.App;
import seng302.group3.model.io.Editor;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;
import seng302.group3.model.navigation.navigator_items.LoggedTimeNavigatorItem;
import seng302.group3.model.navigation.navigator_items.PersonNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by epa31 on 31/07/15.
 */
public class Task extends Element {

    private static final long serialVersionUID = 3107201500006L;

    private String shortName;
    private String description;
    private SerializableObservableList<LoggedTime> loggedHours = new SerializableObservableList<>();
    private SerializableObservableList<Estimate> estimates = new SerializableObservableList<>();
    private Status progressStatus = Status.READY;
    private Type taskType = Type.UNSPECIFIED;
    private int estimateMinutes = 0;
    private SerializableObservableList<NavigatorItem> navigatorItems;
    private SerializableObservableList<Person> assignedPeople = new SerializableObservableList<>();

    private Element objectType;

    private Collection<String> impediments = new ArrayList<>();

    //private double spent


    /**
     * Constructor for task which sets the shortName to be non-null.
     */
    public Task(){
        shortName = "";
    }

    public Task(String shortName, String description) {
        this.shortName = shortName;
        this.description = description;

    }

    public Task(String shortName, String description, int estimate) {
        this.shortName = shortName;
        this.description = description;
        this.estimateMinutes = estimate;
    }

    /**
     * Setter for description of the task
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public void updateEstimate(Estimate estimate) {
        this.estimates.add(estimate);
    }

    public SerializableObservableList<Estimate> getEstimates() {
        return this.estimates;
    }

    public Estimate getCurrentEstimate() {
        Estimate estimate = new Estimate(0.0);
        if (estimates.size() != 0) {
            estimate = estimates.get(estimates.size()-1);
        }
        return estimate;
    }

    public Element getObjectType() {
        return objectType;
    }

    public void setObjectType(Element objectType) {
        this.objectType = objectType;
    }

    public Type getTaskType() {
        return taskType;
    }

    public void setTaskType(Type taskType) {
        this.taskType = taskType;
    }

    /**
     * Adds a given log of time to the loggedhours list.
     * @param time
     */
    public void addHourLog(LoggedTime time){
        loggedHours.add(time);
    }

    /**
     * Makes the next step in the navigator which will display all of the times logged to this task.
     * @return
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
                new SerializableObservableList<>();

        FolderNavigatorItem loggedTimeItems = new FolderNavigatorItem("Logged Time");
        loggedTimeItems.setNext(this.loggedHours);

        navigatorItems.add(loggedTimeItems);
        return navigatorItems;
    }

    /**
     * enum used for the status of the task.
     */
    public static enum Status {
        COMPLETED, IN_PROGRESS, READY
    }

    /**
     * enum used for the type of the task.
     */
    public static enum Type {
        UNSPECIFIED("Unspecified"),
        DEVELOPMENT("Development"),
        TESTING("Testing"),
        DESIGN("Design");
        Type(String name) {
            try {
                Field fieldName = getClass().getSuperclass().getDeclaredField("name");
                fieldName.setAccessible(true);
                fieldName.set(this, name);
                fieldName.setAccessible(false);
            } catch (Exception e) {}
        }
    }

    /**
     * Set the shortName of the task to the given string.
     * @param shortName
     */
    @Override public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Return the shortName string.
     * @return
     */
    @Override public String getShortName() {
        return shortName;
    }

    public double getEstimateMinutes() {
        return estimateMinutes;
    }

    public SerializableObservableList<LoggedTime> getLoggedHours() {
        return loggedHours;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Return the shortName string.
     * @return
     */
    public String toString(){
        return this.getShortName();
    }



    /**
     * When reading the object we need to make sure none of the attributes have lost their values
     * @param stream
     */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.loggedHours == null)
            this.loggedHours = new SerializableObservableList<>();
        if (this.estimates == null)
            this.estimates = new SerializableObservableList<>();
        if(this.assignedPeople == null)
            this.assignedPeople = new SerializableObservableList<>();
        if(impediments == null)
            impediments = new ArrayList<>();
        if(taskType == null)
            taskType = Type.UNSPECIFIED;
    }

    public String getPaneString() {
        String returnString = "";

        //estimate
        returnString += "\n Estimated hours: " + this.getCurrentEstimate().getValue()/60.0 + "\n";

        if (this.getTags().isEmpty())
            returnString += "This task has 0 Tags\n";
        else {
            returnString += "This task has " + this.getTags().size() + " Tag(s):\n";
            for (Tag tag : this.getTags()) {
                returnString += "   " + tag.getShortName() + "(" + tag.getColour() + ")\n";
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

        Label titleLabel = new Label(shortName);
        titleLabel.getStyleClass().add("gridPaneTitle");
        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.getStyleClass().add("gridPaneLabel");
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("gridLabel");

        Label taskTypeTitle  = new Label("Task Type: ");
        taskTypeTitle.getStyleClass().add("gridPaneLabel");
        Label taskType = new Label(getTaskType().name());
        taskType.getStyleClass().add("gridLabel");

        Label estimateTitle = new Label("Estimate: ");
        estimateTitle.getStyleClass().add("gridPaneLabel");
        Label estimate = new Label(estimates.get(estimates.size() - 1).getValue().toString() + " minutes");
        estimate.getStyleClass().add("gridLabel");

        Label parentButtonTitle = new Label();
        parentButtonTitle.getStyleClass().add("gridPaneLabel");

        if(objectType instanceof Sprint){
            parentButtonTitle.setText("Sprint: ");
        }else if(objectType instanceof Story){
            parentButtonTitle.setText("Story: ");
        }

        Button parentButton = new Button(this.objectType.getShortName());
        parentButton.getStyleClass().add("gridPaneButton");
        parentButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, objectType);
        });


        Label loggedTimeLabel = new Label("Logged Time:");
        loggedTimeLabel.getStyleClass().add("gridPaneLabel");
        Button loggedTimeButton = new Button("Log Time");
        loggedTimeButton.getStyleClass().add("gridPaneButton");
        loggedTimeButton.setOnAction((event) -> {
            App.getMainController().newTimeLogButton(this);
        });
        ListView<NavigatorItem> loggedListView = new ListView<>(this.getLoggedHours().getObservableList());
        loggedListView.getStyleClass().add("gridPaneListView");
        loggedListView.setPrefHeight(53*Math.max(1, loggedListView.getItems().size()));
        loggedListView.setPlaceholder(new Label("No Logged Time"));

        Label assignedPeopleLabel = new Label("Assigned People:");
        assignedPeopleLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> assignedPeopleListView = new ListView<>(this.getAssignedPeople().getObservableList());
        assignedPeopleListView.getStyleClass().add("gridPaneListView");
        assignedPeopleListView.setPrefHeight(53*Math.max(1, assignedPeopleListView.getItems().size()));
        assignedPeopleListView.setPlaceholder(new Label("No Assigned People"));

        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        loggedListView.setCellFactory(param -> new taskListCell());
        assignedPeopleListView.setCellFactory(param -> new taskListCell());
        tagListView.setCellFactory(param -> new taskListCell());

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle,descriptionLabel,
            taskTypeTitle, taskType, estimateTitle, estimate, parentButtonTitle, parentButton, loggedTimeLabel, loggedTimeButton,
            loggedListView, assignedPeopleLabel, assignedPeopleListView, tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle,0,1);
        GridPane.setConstraints(descriptionLabel, 1, 1);

        GridPane.setConstraints(taskTypeTitle,0,2);
        GridPane.setConstraints(taskType, 1, 2);

        GridPane.setConstraints(estimateTitle,0,3);
        GridPane.setConstraints(estimate, 1, 3);

        GridPane.setConstraints(parentButtonTitle,0,4);
        GridPane.setConstraints(parentButton, 1, 4);

        GridPane.setConstraints(loggedTimeLabel,0,5);
        GridPane.setConstraints(loggedTimeButton,1,5);
        GridPane.setConstraints(loggedListView,0,6);

        GridPane.setConstraints(assignedPeopleLabel,0, 7);
        GridPane.setConstraints(assignedPeopleListView,0,8);

        GridPane.setConstraints(tagsLabel,0,9);
        GridPane.setConstraints(tagListView,0,10);

        GridPane.setColumnSpan(assignedPeopleListView, 2);
        GridPane.setColumnSpan(loggedListView, 2);
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
        grid.getRowConstraints().addAll(r1,r2,r3,r4,r5,r6, r7, r8, r9, r10);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class taskListCell extends ListCell<NavigatorItem> {
        @Override public void updateItem(NavigatorItem item, boolean empty) {
            super.updateItem(item, empty);
            Button itemButton;
            if(empty || item == null){
                setText(null);
                setGraphic(null);
            }else if(item instanceof PersonNavigatorItem){
                Person person = (Person) item.getItem();
                itemButton = new Button(person.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, person);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);
            }
            else if(item instanceof LoggedTimeNavigatorItem) {
                LoggedTime loggedTime = (LoggedTime) item.getItem();
                itemButton = new Button(loggedTime.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, loggedTime);
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

    public Status getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(Status progressStatus) {
        this.progressStatus = progressStatus;
    }

    public SerializableObservableList<Person> getAssignedPeople() {
        return assignedPeople;
    }

    public void setAssignedPeople(Collection<Person> assignedPeople) {
        this.assignedPeople.setItems(assignedPeople);
    }


    /**
     * sets the impediments of the task
     * @param impediments - impediments on the task
     */
    public void setImpediments(Collection<String> impediments){
        this.impediments = impediments;
    }

    /**
     * gets the tasks impediments
     * @return - the impediments
     */
    public Collection getImpediments(){
        return impediments;
    }
}
