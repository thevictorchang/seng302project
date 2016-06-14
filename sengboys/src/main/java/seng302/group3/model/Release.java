package seng302.group3.model;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import seng302.group3.controller.App;
import seng302.group3.model.io.Editor;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.*;

import java.time.LocalDate;

/**
 * Created by vch51 on 28/04/15.
 */
public class Release extends Element {

    private static final long serialVersionUID = 1806201500004L;

    private String shortName; // "unique, non-null short name/ID
    private String description;
    private LocalDate releaseDate;
    private Project project; // the Project this release is associated with

    /**
     * Constructor omits the description (so far) as the doc says that only the short name is non-null
     *
     * @param shortName
     * @param releaseDate
     * @param project
     */
    public Release(String shortName, LocalDate releaseDate, Project project) {
        this.shortName = shortName;
        this.releaseDate = releaseDate;
        this.project = project;
    }

    /**
     * Constructor for blank release, used by apply() to create a release from the dialogue box
     */
    public Release() {

    }

    public Release(String shortName, String description, LocalDate date, Project project) {
        this.shortName = shortName;
        this.description = description;
        this.releaseDate = date;
        this.project = project;
    }


    /**
     * getter for description
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * setter for description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Gets the date in the form of a LocalDate class object. Specific Year, Month, etc. can be acquired using
     * dateObject.get(Calendar.YEAR), etc.
     *
     * @return current release date of this Release.
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Setter for releaseDate
     *
     * @param releaseDate
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Getter for the project this release is associated with
     *
     * @return project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets a release's project. Used in apply()
     *
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    /**
     * toString only returns the short name so the menubar button for edit doesn't look ridiculous.
     */ public String toString() {
        return getShortName();
    }

    /**
     * Returns the string to be displayed in the main pane
     *
     * @return the string
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "\nDescription: " + this.description + "\n";

        returnString +=
            "This release is assigned to the project " + this.project.getNameLong() + "\n";

        returnString += "The date of this release is " + this.getReleaseDate().toString();

        if (this.getTags().isEmpty())
            returnString += "This release has 0 Tags\n";
        else {
            returnString += "This release has " + this.getTags().size() + " Tag(s):\n";
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

    public Node getOverviewPaneGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(30);
        grid.getStyleClass().add("gridPane");

        Button editButton = new Button("Edit");
        editButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, this);
        });
        editButton.getStyleClass().add("gridPaneButton");

        Label titleLabel = new Label(this.shortName);
        titleLabel.getStyleClass().add("gridPaneTitle");

        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.getStyleClass().add("gridPaneLabel");
        Label descriptionLabel = new Label(this.description);
        descriptionLabel.getStyleClass().add("gridLabel");

        Label releaseTitle = new Label("Release Date: ");
        releaseTitle.getStyleClass().add("gridPaneLabel");
        Label releaseLabel = new Label(this.releaseDate.toString());
        releaseLabel.getStyleClass().add("gridLabel");

        Label projectButtonTitle = new Label("Project: ");
        projectButtonTitle.getStyleClass().add("gridPaneLabel");
        Button projectButton = new Button(this.getProject().getShortName());
        projectButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, this.getProject());
        });
        projectButton.getStyleClass().add("gridPaneButton");


        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        tagListView.setCellFactory(param -> new releaseListCell());

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle,descriptionLabel,
            releaseTitle,releaseLabel,projectButtonTitle,projectButton,tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle,0,1);
        GridPane.setConstraints(descriptionLabel, 1, 1);

        GridPane.setConstraints(releaseTitle,0,2);
        GridPane.setConstraints(releaseLabel, 1, 2);
        GridPane.setConstraints(projectButtonTitle,0,3);
        GridPane.setConstraints(projectButton, 1, 3);

        GridPane.setColumnSpan(tagListView, 2);

        GridPane.setConstraints(tagsLabel,0,4);
        GridPane.setConstraints(tagListView,0,5);


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

    public class releaseListCell extends ListCell<NavigatorItem> {
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




