package seng302.group3.model;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import seng302.group3.controller.App;
import seng302.group3.model.io.Editor;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;

import java.time.LocalDate;

/**
 * Created by epa31 on 15/05/15.
 */
public class TimePeriod extends Element {

    private static final long serialVersionUID = 1806201500007L;

    private LocalDate startDate;
    private LocalDate endDate;

    private Project project;

    private Team team;

    /**
     * A constructor that takes both a start and end date to set the Dates. Also sets the project
     *
     * @param startDate
     * @param endDate
     */
    public TimePeriod(LocalDate startDate, LocalDate endDate, Project project) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
        this.team = null;
    }

    /**
     * A constructor that takes both a start and end date to set the Dates. Also sets the team
     *
     * @param startDate
     * @param endDate
     */
    public TimePeriod(LocalDate startDate, LocalDate endDate, Team team) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.team = team;
        this.project = null;
    }

    /**
     * A constructor that takes both a start and end date to set the Dates. Also sets the team and project
     *
     * @param startDate
     * @param endDate
     */
    public TimePeriod(LocalDate startDate, LocalDate endDate, Project project, Team team) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.team = team;
        this.project = project;
    }

    /**
     * A constructor that takes both a start and end date to set the Dates.
     *
     * @param startDate
     * @param endDate
     */
    public TimePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * A constructor with n arguments that sets the dates to null.
     */
    public TimePeriod() {
        this.endDate = null;
        this.startDate = null;
    }

    /**
     * A setter for start date.
     *
     * @param startDate
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Getter for startdate
     *
     * @return
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * getter for endDatePicker
     *
     * @return
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Setter for end date.
     *
     * @param endDate
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * A setter for projects
     *
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * A getter for projects.
     *
     * @return
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * A getter for team.
     *
     * @return
     */
    public Team getTeam() {
        return team;
    }

    /**
     * A setter for team
     *
     * @param team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Takes another time period and checks if it overlaps with the current time period
     *
     * @param timePeriod
     * @return - True if there is an overlap, false otherwise
     */
    public boolean checkDateOverlap(TimePeriod timePeriod) {
        boolean overlap = false;
        //Checks if start date is in the period
        if (timePeriod.getStartDate().isBefore(endDate)) {
            if (timePeriod.getStartDate().isAfter(startDate)) {
                overlap = true;
            }
        }
        //Checks if the end date is in the period
        if (timePeriod.getEndDate().isBefore(endDate)) {
            if (timePeriod.getEndDate().isAfter(startDate)) {
                overlap = true;
            }
        }

        //Check if the time period given contains the current one
        if (startDate.isBefore(timePeriod.getEndDate())) {
            if (startDate.isAfter(timePeriod.getStartDate())) {
                overlap = true;
            }
        }
        if (endDate.isBefore(timePeriod.getEndDate())) {
            if (endDate.isAfter(timePeriod.getStartDate())) {
                overlap = true;
            }
        }

        return overlap;

    }

    /**
     * Takes a date to see if is in the current time period.
     *
     * @param date
     * @return - True if the date is in the time period, false otherwise
     */
    public boolean containsDate(LocalDate date) {
        boolean contains = false;
        if (date.isBefore(endDate) || date.isEqual(endDate)) {
            if (date.isAfter(startDate) || date.isEqual(startDate)) {
                contains = true;
            }
        }
        return contains;
    }

    /**
     * Returns a string form of the combination between the start and end date
     *
     * @return
     */
    public String getTimePeriod() {
        return (this.startDate + " " + (startDate.until(endDate)));
    }

    /**
     * The tostring for timePeriods
     *
     * @return
     */
    @Override public String toString() {

        if (project != null && team != null) {
            return (project.getShortName() + " - " + team.getShortName() + " - " + startDate
                + " to " + endDate);
        }
        if (project != null) {
            return (project.getShortName() + " - " + startDate + " - " + endDate);
        }
        if (team != null) {
            return (team.getShortName() + " - " + startDate + " - " + endDate);
        }
        return ("" + startDate + " - " + endDate);
    }


    /**
     * this method for time period doesnt do anything, but as we extend
     *
     * @param shortName
     */
    @Override public void setShortName(String shortName) {

    }

    /**
     * equivalent to toString
     *
     * @return
     */
    @Override public String getShortName() {
        return this.toString();
    }

    public String getPaneString() {
        String paneString = "";

        paneString += ("Start Time: " + startDate + "\n");
        paneString += ("End Time: " + endDate + "\n");
        paneString += ("Associated Team: " + team + "\n");
        paneString += ("Associated Project: " + project + "\n");

        if (this.getTags().isEmpty())
            paneString += "This person has 0 Tags\n";
        else {
            paneString += "This person has " + this.getTags().size() + " Tag(s):\n";
            for (Tag tag : this.getTags()) {
                paneString += "   " + tag.getShortName() + "(" + tag.getColour() + ")\n";
            }
        }
        return paneString;
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

        Label titleLabel = new Label("Time Period");
        titleLabel.getStyleClass().add("gridPaneTitle");

        Label startDateTitle = new Label("Start Date: ");
        startDateTitle.getStyleClass().add("gridPaneLabel");
        Label startDateLabel = new Label(startDate.toString());
        startDateLabel.getStyleClass().add("gridLabel");

        Label endDateTitle = new Label("End Date: ");
        endDateTitle.getStyleClass().add("gridPaneLabel");
        Label endDateLabel = new Label(this.endDate.toString());
        endDateLabel.getStyleClass().add("gridLabel");

        Label projectButtonTitle = new Label("Project: ");
        projectButtonTitle.getStyleClass().add("gridPaneLabel");
        Button projectButton = new Button(this.getProject().getShortName());
        projectButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, this.getProject());
        });
        projectButton.getStyleClass().add("gridPaneButton");

        Label teamButtonTitle = new Label("Team: ");
        teamButtonTitle.getStyleClass().add("gridPaneLabel");
        Button teamButton = new Button(this.getTeam().getShortName());
        teamButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation,team);
        });
        teamButton.getStyleClass().add("gridPaneButton");


        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        tagListView.setCellFactory(param -> new timePeriodListCell());

        grid.getChildren().addAll(titleLabel,editButton,startDateTitle,startDateLabel,
            endDateTitle,endDateLabel,projectButtonTitle,projectButton,teamButtonTitle,
            teamButton,tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(startDateTitle,0,1);
        GridPane.setConstraints(startDateLabel, 1, 1);
        GridPane.setConstraints(endDateTitle,0,2);
        GridPane.setConstraints(endDateLabel, 1, 2);

        GridPane.setConstraints(projectButtonTitle,0,3);
        GridPane.setConstraints(projectButton, 1, 3);
        GridPane.setConstraints(teamButtonTitle,0,4);
        GridPane.setConstraints(teamButton, 1, 4);

        GridPane.setConstraints(tagsLabel,0,5);
        GridPane.setConstraints(tagListView,0,6);

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

        grid.getColumnConstraints().addAll(c0,c1);
        grid.getRowConstraints().addAll(r1,r2,r3,r4,r5,r6,r7);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class timePeriodListCell extends ListCell<NavigatorItem> {
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
