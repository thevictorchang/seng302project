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

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by Eddy on 31-Jul-15.
 */
public class LoggedTime extends Element{

    private static final long serialVersionUID = 1806201500007L;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Person person = null;
    private String comment = null;
    private LoggedTime pairHours = null; // if null, then not a pair programming session. If not null, then a duplicate will be created for the partner


    public LoggedTime(LocalDateTime endTime, Long effortSeconds){
        this.endTime = endTime;
        this.startTime = endTime.minusSeconds(effortSeconds);
    }

    public LoggedTime(LocalDateTime endTime, LocalDateTime startTime) {
        this.endTime = endTime;
        this.startTime = startTime;
    }

    public LoggedTime(LoggedTime copyData, Person partner) {
        this.person = partner;
        this.startTime = copyData.getStartTime();
        this.endTime = copyData.getEndTime();
        this.comment = copyData.getComment();
        this.pairHours = copyData;
    }


    /**
     * Returns the loggedtime element in minutes
     * @return
     */
    public Long getLoggedTime(){
        return (Duration.between(startTime,endTime).getSeconds()/60);
    }

    /**
     * returns the loggedtime in hours instead of minutes, and as a Double so it cooperates with the task estimate
     * @return
     */
    public Double getLoggedTimeInHours() {

        Double loggedHours = (this.getLoggedTime().doubleValue() / 60); // getLoggedTime returns in minutes, not hours
        return loggedHours;
    }

    @Override
    public void setShortName(String shortName) {}

    @Override
    public String getShortName() {
        return toString();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String toString(){
        return getLoggedTime().toString() + " minutes";
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LoggedTime getPairHours() {
        return pairHours;
    }

    public void setPairHours(LoggedTime pairHours) {
        this.pairHours = pairHours;
    }

    /**
     * Returns the string to be displayed in the main pane
     * @return the string
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "\nHours logged: " + this.getLoggedTime()/60.0 + "\n";
        returnString += "Person: " + this.person + "\n";
        returnString += "Log Comment: " + this.comment + "\n";

        if (this.getTags().isEmpty())
            returnString += "This logged time has 0 Tags\n";
        else {
            returnString += "This logged time has " + this.getTags().size() + " Tag(s):\n";
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

        Label titleLabel = new Label(this.toString());
        titleLabel.getStyleClass().add("gridPaneTitle");
        Label personTitle = new Label("User: ");
        personTitle.getStyleClass().add("gridPaneLabel");
        Button personButton = new Button(this.person.getShortName());
        personButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, person);
        });
        personButton.getStyleClass().add("gridPaneButton");

        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.getStyleClass().add("gridPaneLabel");
        Label descriptionLabel = new Label(this.comment);
        descriptionLabel.getStyleClass().add("gridLabel");

        Label pairTitle = new Label("Pair Hours: ");
        pairTitle.getStyleClass().add("gridPaneLabel");
        Button pairButton = new Button();
        if(pairHours != null){
            pairButton.setText(pairHours.getPerson().getShortName());
            personButton.setOnAction((event) -> {
                new Editor().edit(App.getMainController().currentOrganisation, person);
            });
        }else{
            pairButton.setText("No pair hours");
            pairButton.setDisable(true);
        }

        pairButton.getStyleClass().add("gridPaneButton");

        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPlaceholder(new Label("No Tags"));
        tagListView.setCellFactory(param -> new loggedTimeListCell());
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle,descriptionLabel,personTitle,
            personButton,pairTitle,pairButton,tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle, 0, 1);
        GridPane.setConstraints(descriptionLabel, 1, 1);
        GridPane.setConstraints(personTitle,0,2);
        GridPane.setConstraints(personButton,1,2);
        GridPane.setConstraints(pairTitle,0,3);
        GridPane.setConstraints(pairButton,1,3);
        GridPane.setConstraints(tagsLabel,0,4);
        GridPane.setConstraints(tagListView,0,5);

        GridPane.setColumnSpan(tagListView, 2);

        ColumnConstraints c0 = new ColumnConstraints(400);
        ColumnConstraints c1 = new ColumnConstraints(400);

        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();
        RowConstraints r4 = new RowConstraints();
        RowConstraints r5 = new RowConstraints();

        grid.getColumnConstraints().addAll(c0,c1);
        grid.getRowConstraints().addAll(r1,r2,r3,r4,r5);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class loggedTimeListCell extends ListCell<NavigatorItem> {
        @Override public void updateItem(NavigatorItem item, boolean empty) {
            super.updateItem(item, empty);
            Button itemButton;
            if(empty || item == null){
                setText(null);
                setGraphic(null);
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

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endTime = endDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startTime = startDateTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
