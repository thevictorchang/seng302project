package seng302.group3.model;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import seng302.group3.controller.App;
import seng302.group3.model.gui.BurndownChart;
import seng302.group3.model.io.Editor;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;
import seng302.group3.model.sorting.Sort;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by vch51 on 14/07/15.
 */
public class Sprint extends Element{

    private static final long serialVersionUID = 2307201500012L;

    private Backlog backlog; //indirectly provides a project too
    private Team team;

    private TimePeriod dates; //instead of two separate LocalDates, uses the already established TimePeriod
    private Release release; // "each sprint is associated with a release"

    private String shortName; //the sprint "goal"
    private String fullName;
    private String description;


    private Double totalStoryPointsCompleted = 0.0;
    private Double pointsPerDay = 0.0;
    private Double hoursPerDay = 0.0;



    private SerializableObservableList<Story> stories = new SerializableObservableList<>(); //"each sprint has zero or more user stories"
    private SerializableObservableList<Task> tasks = new SerializableObservableList<>();
    private SerializableObservableList<Task> unallocatedTasks = new SerializableObservableList<>();

    public Sprint() {
        this.setShortName("");
        this.setFullName("");
        this.setDescription("");
        Sort.sprintStoryPriority(stories);
    }

    public Sprint(String shortName, String fullName, String description, TimePeriod timePeriod,
        Team team, Release release, Backlog backlog){
        this.setShortName(shortName);
        this.setFullName(fullName);
        this.setDescription(description);
        this.dates = timePeriod;
        this.team = team;
        this.release = release;
        this.backlog = backlog;
        Sort.sprintStoryPriority(stories);
    }
    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TimePeriod getDates() {
        return dates;
    }

    public void setDates(TimePeriod dates) {
        this.dates = dates;
    }

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public String getShortName() {
        return shortName;
    }


    public Double getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(Double hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public void addTotalStoryPointsCompleted(Double toAdd) {
        this.totalStoryPointsCompleted = this.totalStoryPointsCompleted + toAdd;
    }

    public void removeTotalStoryPointsCompleted(Double toRemove) {
        this.totalStoryPointsCompleted = this.totalStoryPointsCompleted - toRemove;
    }

    /**
     * A setter for the tasks list.
     * @param tasks
     */
    public void setTasks(Collection<Task> tasks) {
        this.tasks.setItems(tasks);
    }

    /**
     * Returns true if the sprint was in dates in the past, true otherwise.
     * @return
     */
    public boolean hasEnded(){
        if(LocalDate.now().isAfter(dates.getEndDate())){
            return true;
        }
        return false;
    }

    /**
     * Returns true if the sprint is current. Otherwise false.
     * @return
     */
    public boolean isActive(){
        if(dates.containsDate(LocalDate.now()))
            return true;
        return false;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Getter for Stories
     *
     * @return a collection of Stories
     */
    public SerializableObservableList<Story> getStories() {
        return stories;
    }

    public void addStory(Story s) {
        this.stories.add(s);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStories(Collection<Story> stories) {
        this.stories.setItems(stories);
    }

    /**
     * Adds a given task to the list.
     * @param task
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public SerializableObservableList<Task> getUnallocatedTasks() {
        return unallocatedTasks;
    }

    /**
     * A getter for tasks which returns a serializableObservable list of tasks
     * @return
     */
    public SerializableObservableList<Task> getTasks() {
        return tasks;
    }

    /**
     * Handles the navigator contents for this sprint
     *
     * @return
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
                new SerializableObservableList<>();

        FolderNavigatorItem storiesItem = new FolderNavigatorItem("Stories");
        storiesItem.setNext(this.stories);

        FolderNavigatorItem releaseItem = new FolderNavigatorItem("Release");
        SerializableObservableList<Release> r = new SerializableObservableList<>();
        r.add(release);
        releaseItem.setNext(r);

        FolderNavigatorItem teamItem = new FolderNavigatorItem("Team");
        SerializableObservableList<Team> t = new SerializableObservableList<>();
        t.add(team);
        teamItem.setNext(t);

        FolderNavigatorItem taskItems = new FolderNavigatorItem("Sprint Tasks");
        taskItems.setNext(this.tasks);

        FolderNavigatorItem unallocatedItems = new FolderNavigatorItem("Unallocated Tasks");
        updateUnallocatedTasks();
        unallocatedItems.setNext(unallocatedTasks);

        navigatorItems.add(unallocatedItems);
        navigatorItems.add(taskItems);
        navigatorItems.add(storiesItem);
        navigatorItems.add(releaseItem);
        navigatorItems.add(teamItem);
        return navigatorItems;
    }

    public void updateUnallocatedTasks() {
        unallocatedTasks.clear();
        for (Story story : stories){
            unallocatedTasks.addAll(story.getTasks().stream().filter(task
                    -> task.getAssignedPeople().size() < 1).collect(Collectors.toList()));
        }
    }

    public double getTotalPairHours() {
        Double totalPairHours = 0.0;
        for (Story story : this.getStories()) {
            for (Task task : story.getTasks()) {
                for (LoggedTime loggedTime : task.getLoggedHours()) {
                    if (loggedTime.getPairHours() != null) {
                        totalPairHours += loggedTime.getLoggedTimeInHours();
                    }
                }
            }
        }

        return totalPairHours;

    }


    public String getPaneString() {
        String returnString = "";

        // Description

        returnString += "\nDescription: " + this.getDescription() + "\n";

        // Dates
        returnString += "\nSprint start date: " + this.getDates().getStartDate() + "\n";
        returnString += "\nSprint end date: " + this.getDates().getEndDate() + "\n";

        // Release
        returnString += "\nRelease: " + this.getRelease() + "\n";

        // Backlog
        returnString += "\nThis sprint has " + this.stories.size() + " stories \n";
        for (Story story : getStories()) {
            returnString += "    " + story.getFullName() + "\n";
        }

        // Team
        returnString += "\nTeam assigned: " + this.team.getShortName() + "\n";

        if (this.getTags().isEmpty())
            returnString += "This sprint has 0 Tags\n";
        else {
            returnString += "This sprint has " + this.getTags().size() + " Tag(s):\n";
            for (Tag tag : this.getTags()) {
                returnString += "   " + tag.getShortName() + "(" + tag.getColour() + ")\n";
            }
        }

        String pairProgrammingString = "";
        double totalHoursPaired = getTotalPairHours();

        for (Story story : this.getStories()) {
            pairProgrammingString += "  " + story.getShortName() + ": \n";
            for (Task task : story.getTasks()) {
                for (LoggedTime loggedTime : task.getLoggedHours()) {
                    if (loggedTime.getPairHours() != null) { // if this loggedtime object IS pair programming\

                        pairProgrammingString += "      ";
                        pairProgrammingString += loggedTime.getPerson() + " and " + loggedTime.getPairHours().getPerson() +
                                " in task " + task.getShortName() + " - " + loggedTime.getLoggedTimeInHours() + " hours ";
                        pairProgrammingString += "(" + loggedTime.getLoggedTimeInHours()/totalHoursPaired  + "%) \n";
                    }
                }
            }
        }

        returnString += "\nPair Programming hours: " + totalHoursPaired + " Total Hours" + '\n' + pairProgrammingString;

        String taskTypeString = "\nNumber of People Allocated For each Task Type: \n";
        Integer unspecifiedCount = 0;
        Integer developmentCount = 0;
        Integer testingCount = 0;
        Integer designCount = 0;
        for (Story story : this.getStories()) {
            for(Task task : story.getTasks()){
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
        }
        taskTypeString += "  Unspecified Tasks: " + unspecifiedCount + '\n';
        taskTypeString += "  Development Tasks: " + developmentCount + '\n';
        taskTypeString += "  Testing Tasks: " + testingCount + '\n';
        taskTypeString += "  Design Tasks: " + designCount + '\n';

        returnString += taskTypeString;

        return returnString;
    }

    public double getTotalMinutesLogged() {
        Double totalMinutes = 0.0;
        for (Story story : this.getStories()) {
            for (Task task : story.getTasks()) {
                for (LoggedTime loggedTime : task.getLoggedHours()) {
                    totalMinutes += loggedTime.getLoggedTime();
                }
            }
        }
        return totalMinutes;
    }

    public double getHoursOnThisDay(LocalDate date) {

        Double totalHours = 0.0;
        for (Story story : this.getStories()) {
            for (Task task : story.getTasks()) {
                for (LoggedTime loggedTime : task.getLoggedHours()) { // every instance of logged hours
                    if (loggedTime.getEndTime().toLocalDate().equals(date)) { //if this piece of loggedtime ends on the date passed into the method
                        totalHours += loggedTime.getLoggedTimeInHours();
                    }
                }
            }
        }
        for (Task task : this.getTasks()) {
            for (LoggedTime loggedTime : task.getLoggedHours()) {
                if (loggedTime.getEndTime().toLocalDate().equals(date)) { //if this piece of loggedtime ends on the date passed into the method
                    totalHours += loggedTime.getLoggedTimeInHours();
                }
            }
        }
        return totalHours;
    }

    public Double getSprintEstimateMinutes() {
        Double estimate = 0.0;
        for (Story story : getStories()) {
            for (Task task : story.getTasks()) {
                if (task.getCurrentEstimate() != null) {
                    estimate += task.getCurrentEstimate().getValue();
                }
            }
        }
        for (Task task : this.getTasks()) {
            if (task.getCurrentEstimate() != null) {
                estimate += task.getCurrentEstimate().getValue();
            }
        }
        return estimate;
    }


    public double calculateVelocityPoints(){
        Double totalHours = 0.0;
        Double totalPoints = 0.0;
        Collection<LoggedTime> loggedHoursList = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.from(this.getDates().getStartDate().atStartOfDay());
        LocalDateTime endDate = LocalDateTime.MIN;
        for (Story story : this.getStories()) {
            for (Task task : story.getTasks()) {
                for (LoggedTime loggedTime : task.getLoggedHours()) { // every instance of logged hours
                    loggedHoursList.add(loggedTime);
                    if (story.getStoryStatus() == Story.StoryStatus.DONE) {
                        totalHours += loggedTime.getLoggedTimeInHours();
                    }
                }
            }
            if (story.getStoryStatus() == Story.StoryStatus.DONE) {
                totalPoints += story.getEstimate();
            }
        }
        for (Task task : this.getTasks()) {
            for (LoggedTime loggedTime : task.getLoggedHours()) {
                loggedHoursList.add(loggedTime);
                totalHours += loggedTime.getLoggedTimeInHours();
            }
        }

        for (LoggedTime loggedTime : loggedHoursList) {
            if (loggedTime.getEndTime().isAfter(ChronoLocalDateTime.from(endDate))) {
                endDate = loggedTime.getEndTime();
            }
        }
        Period duration = LocalDate.from(startDate).until(LocalDate.from(endDate));
        Double days = duration.getDays() + 0.0;
        Double weeks = days/7.0;
        Double pointsPerWeek = totalStoryPointsCompleted/weeks;
        return pointsPerWeek;
    }

    public void displaySprintVelocity(String period, boolean provisional) {
        Double totalHours = 0.0;
        Double totalPoints = 0.0;
        Collection<LoggedTime> loggedHoursList = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.from(this.getDates().getStartDate().atStartOfDay());
        LocalDateTime endDate = LocalDateTime.MIN;
        for (Story story : this.getStories()) {
            for (Task task : story.getTasks()) {
                for (LoggedTime loggedTime : task.getLoggedHours()) { // every instance of logged hours
                    loggedHoursList.add(loggedTime);
                    if (story.getStoryStatus() == Story.StoryStatus.DONE) {
                        totalHours += loggedTime.getLoggedTimeInHours();
                    }
                }
            }
            if (story.getStoryStatus() == Story.StoryStatus.DONE) {
                totalPoints += story.getEstimate();
            }
        }
        for (Task task : this.getTasks()) {
            for (LoggedTime loggedTime : task.getLoggedHours()) {
                loggedHoursList.add(loggedTime);
                totalHours += loggedTime.getLoggedTimeInHours();
            }
        }

        for (LoggedTime loggedTime : loggedHoursList) {
            if (loggedTime.getEndTime().isAfter(ChronoLocalDateTime.from(endDate))) {
                endDate = loggedTime.getEndTime();
            }
        }

        //
        Period duration = LocalDate.from(startDate).until(LocalDate.from(endDate));
        Double days = duration.getDays() + 0.0;
        Double weeks = days/7.0;
        Double pointsPerWeek = totalStoryPointsCompleted/weeks;
        pointsPerDay = totalStoryPointsCompleted/days;
        hoursPerDay = totalHours/days;
        if (pointsPerDay == null) {
            pointsPerDay = 0.0;
        }
        if (pointsPerWeek == null) {
            pointsPerWeek = 0.0;
        }
        if (BurndownChart.getInstance().getController() != null) {
            if (period == "DAY") {
                if (provisional) {
                    BurndownChart.getInstance().getController().setVelocityLabel(
                        "Sprint Velocity: " + this.getBacklog().scaleToString(pointsPerDay) + " per day (Provisional)");
                }
                else {
                    BurndownChart.getInstance().getController().setVelocityLabel(
                        "Sprint Velocity: " + this.getBacklog().scaleToString(pointsPerDay) + " per day");
                }
            }
            else if (period == "WEEK") {
                if (provisional) {
                    BurndownChart.getInstance().getController().setVelocityLabel(
                        "Sprint Velocity: " + this.getBacklog().scaleToString(pointsPerWeek) + " per week (Provisional)");
                }
                else {
                    BurndownChart.getInstance().getController().setVelocityLabel(
                        "Sprint Velocity: " + this.getBacklog().scaleToString(pointsPerWeek) + " per week");
                }
            }
            else if (period == "SPRINT") {
                if (provisional) {
                    BurndownChart.getInstance().getController().setVelocityLabel(
                        "Sprint Velocity: " + this.getBacklog().scaleToString(totalPoints) + " for the Sprint (Provisional)");
                }
                else {
                    BurndownChart.getInstance().getController().setVelocityLabel(
                        "Sprint Velocity: " + this.getBacklog().scaleToString(totalPoints) + " for the Sprint");
                }
            }
        }
    }



    /**
     * When reading the object we need to make sure none of the attributes have lost their values
     * @param stream
     */
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        stream.defaultReadObject();
        if (this.stories == null)
            this.stories = new SerializableObservableList<>();
        if (this.tasks == null)
            this.tasks = new SerializableObservableList<>();
        if (this.unallocatedTasks == null)
            this.unallocatedTasks = new SerializableObservableList<>();
        if (this.totalStoryPointsCompleted == null)
            this.totalStoryPointsCompleted = 0.0;
        if (this.pointsPerDay == null)
            pointsPerDay = 0.0;
        if (this.hoursPerDay == null)
            hoursPerDay = 0.0;

    }

    @Override
    public String toString() {
        return this.getShortName();
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

        Label startDateTitle = new Label("Start Date: ");
        startDateTitle.getStyleClass().add("gridPaneLabel");
        Label startDateLabel = new Label(dates.getStartDate().toString());
        startDateLabel.getStyleClass().add("gridLabel");
        Label endDateTitle = new Label("End Date: ");
        endDateTitle.getStyleClass().add("gridPaneLabel");
        Label endDateLabel = new Label(dates.getEndDate().toString());
        endDateLabel.getStyleClass().add("gridLabel");

        Label releaseButtonTitle = new Label("Release: ");
        releaseButtonTitle.getStyleClass().add("gridPaneLabel");
        Button releaseButton = new Button(this.release.getShortName());
        releaseButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, this.release);
        });
        releaseButton.getStyleClass().add("gridPaneButton");

        Label backlogButtonTitle = new Label("Backlog: ");
        backlogButtonTitle.getStyleClass().add("gridPaneLabel");
        Button backlogButton = new Button(this.backlog.getShortName());
        backlogButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, this.backlog);
        });
        backlogButton.getStyleClass().add("gridPaneButton");

        Label teamButtonTitle = new Label("Team: ");
        teamButtonTitle.getStyleClass().add("gridPaneLabel");
        Button teamButton = new Button(this.team.getShortName());
        teamButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, this.team);
        });
        teamButton.getStyleClass().add("gridPaneButton");


        Label returnTitle = new Label("Sprint Information: ");
        returnTitle.getStyleClass().add("gridPaneLabel");
        //SHORTEN THIS? No
        String returnString = "";
        String pairProgrammingString = "";
        double totalHoursPaired = 0.0;

        for (Story story : this.getStories()) {
            pairProgrammingString += "  " + story.getShortName() + ": \n";
            for (Task task : story.getTasks()) {
                for (LoggedTime loggedTime : task.getLoggedHours()) {
                    if (loggedTime.getPairHours() != null) { // if this loggedtime object IS pair programming
                        pairProgrammingString += "      ";
                        pairProgrammingString += loggedTime.getPerson() + " and " + loggedTime.getPairHours().getPerson() +
                            " in task " + task.getShortName() + " - " + loggedTime.getLoggedTimeInHours() + " hours" + "\n";
                        totalHoursPaired += loggedTime.getLoggedTimeInHours();
                    }
                }
            }
        }

        returnString += "\nPair Programming hours: " + totalHoursPaired + " Total Hours" + '\n' + pairProgrammingString;

        String taskTypeString = "\nNumber of People Allocated For each Task Type: \n";
        Integer unspecifiedCount = 0;
        Integer developmentCount = 0;
        Integer testingCount = 0;
        Integer designCount = 0;
        for (Story story : this.getStories()) {
            for(Task task : story.getTasks()){
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
        }
        taskTypeString += "  Unspecified Tasks: " + unspecifiedCount + '\n';
        taskTypeString += "  Development Tasks: " + developmentCount + '\n';
        taskTypeString += "  Testing Tasks: " + testingCount + '\n';
        taskTypeString += "  Design Tasks: " + designCount + '\n';

        returnString += taskTypeString;
        Label returnLabel = new Label(returnString);
        returnLabel.getStyleClass().add("gridLabel");
        //

        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        tagListView.setCellFactory(param -> new sprintListCell());

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle,descriptionLabel,
            startDateTitle,startDateLabel,endDateTitle,endDateLabel,releaseButtonTitle,releaseButton
            ,backlogButtonTitle,backlogButton,teamButtonTitle,teamButton, returnTitle, returnLabel, tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle,0,1);
        GridPane.setConstraints(descriptionLabel, 1, 1);

        GridPane.setConstraints(startDateTitle,0,2);
        GridPane.setConstraints(startDateLabel, 1, 2);
        GridPane.setConstraints(endDateTitle,0,3);
        GridPane.setConstraints(endDateLabel, 1, 3);
        GridPane.setConstraints(releaseButtonTitle,0,4);
        GridPane.setConstraints(releaseButton, 1, 4);
        GridPane.setConstraints(backlogButtonTitle,0,5);
        GridPane.setConstraints(backlogButton, 1, 5);
        GridPane.setConstraints(teamButtonTitle,0,6);
        GridPane.setConstraints(teamButton, 1, 6);
        GridPane.setConstraints(returnTitle,0,7);
        GridPane.setConstraints(returnLabel, 1, 7);

        GridPane.setColumnSpan(tagListView, 2);

        GridPane.setConstraints(tagsLabel,0,8);
        GridPane.setConstraints(tagListView,0,9);


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

        grid.getColumnConstraints().addAll(c0,c1);
        grid.getRowConstraints().addAll(r1,r2,r3,r4,r5,r6,r7,r8,r9,r10);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class sprintListCell extends ListCell<NavigatorItem> {
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
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
