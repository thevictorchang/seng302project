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
import seng302.group3.model.navigation.navigator_items.*;

import java.io.Serializable;

/**
 * Created by vch51 on 1/09/15.
 */
public class Tag extends Element implements Serializable {

    private static final long serialVersionUID = 1109201500012L;

    private String shortName;
    private String description;
    private String colour;
    private SerializableObservableList<Element> elements = new SerializableObservableList<>();

    public Tag(){

    }
    public Tag(String shortName, String description, String colour){
        this.shortName = shortName;
        this.description = description;
        this.colour = colour;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SerializableObservableList<Element> getElements() {
        return this.elements;
    }

    public void setElements(SerializableObservableList<Element> newElements) {
        this.elements = newElements;
    }

    public void addElement(Element element) {
        this.elements.add(element);
    }

    public void removeElement(Element element) {
        this.elements.remove(element);
    }

    public String getPaneString() {
        String returnString = "";
        returnString += "\nDescription: " + this.description + "\n";
        returnString += "\nColour: " + this.colour + "\n";
        returnString += "Associated elements: " + this.getElements();

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

        Label colourTitle = new Label("Colour: ");
        colourTitle.getStyleClass().add("gridPaneLabel");
        Label colourLabel = new Label(colour);
        colourLabel.getStyleClass().add("gridLabel");

        Label associatedLabel = new Label("Associated:");
        associatedLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> associatedListView = new ListView<>(elements.getObservableList());
        associatedListView.getStyleClass().add("gridPaneListView");
        associatedListView.setPrefHeight(53*Math.max(1, associatedListView.getItems().size()));
        associatedListView.setPlaceholder(new Label("No Associated Elements"));

        associatedListView.setCellFactory(param -> new tagListCell());

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle,descriptionLabel,colourTitle,
            colourLabel,associatedLabel,associatedListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle,0,1);
        GridPane.setConstraints(descriptionLabel, 1, 1);
        GridPane.setConstraints(colourTitle,0,2);
        GridPane.setConstraints(colourLabel, 1, 2);

        GridPane.setConstraints(associatedLabel,0,3);
        GridPane.setConstraints(associatedListView,0,4);

        GridPane.setColumnSpan(associatedListView, 2);

        ColumnConstraints c0 = new ColumnConstraints(400);
        ColumnConstraints c1 = new ColumnConstraints(400);

        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();
        RowConstraints r4 = new RowConstraints();
        RowConstraints r5 = new RowConstraints();
        RowConstraints r6 = new RowConstraints();

        grid.getColumnConstraints().addAll(c0,c1);
        grid.getRowConstraints().addAll(r1,r2,r3,r4,r5,r6);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class tagListCell extends ListCell<NavigatorItem> {
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

            }else if(item instanceof BacklogNavigatorItem){
                Backlog backlog = (Backlog) item.getItem();
                itemButton = new Button(backlog.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, backlog);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);

            }else if(item instanceof LoggedTimeNavigatorItem){
                LoggedTime loggedTime = (LoggedTime) item.getItem();
                itemButton = new Button(loggedTime.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, loggedTime);
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

            }else if(item instanceof ProjectNavigatorItem){
                Project project = (Project) item.getItem();
                itemButton = new Button(project.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, project);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);

            }else if(item instanceof ReleaseNavigatorItem){
                Release release = (Release) item.getItem();
                itemButton = new Button(release.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, release);
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

            }else if(item instanceof StoryNavigatorItem){
                Story story = (Story) item.getItem();
                itemButton = new Button(story.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, story);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);

            }else if(item instanceof SprintNavigatorItem){
                Sprint sprint = (Sprint) item.getItem();
                itemButton = new Button(sprint.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, sprint);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);

            }else if(item instanceof TaskNavigatorItem){
                Task task = (Task) item.getItem();
                itemButton = new Button(task.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, task);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);

            }else if(item instanceof TeamNavigatorItem){
                Team team = (Team) item.getItem();
                itemButton = new Button(team.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, team);
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);

            }else if(item instanceof TimePeriodNavigatorItem){
                TimePeriod timePeriod = (TimePeriod) item.getItem();
                itemButton = new Button(timePeriod.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, timePeriod.getProject());
                });
                itemButton.getStyleClass().add("gridPaneListButton");
                setGraphic(itemButton);

            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }

    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
                new SerializableObservableList<>();

        FolderNavigatorItem elementItems = new FolderNavigatorItem("Elements");


        elementItems.setNext(this.elements);

        navigatorItems.add(elementItems);
        return navigatorItems;
    }



    /**
     * To make the listview in the tags dialog better
     */
    @Override
    public String toString() {
        return shortName;
    }
}
