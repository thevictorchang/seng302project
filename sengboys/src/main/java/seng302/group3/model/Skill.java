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
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;

/**
 * Created by vch51 on 24/03/15.
 * People have skills, and in the 'edit person' part of the app skills can be added/removed,
 * skills may be used as dependencies for roles later
 */
@Default(required = false) public class Skill extends Element {

    private static final long serialVersionUID = 1806201500005L;

    @Attribute private String name;
    private String description;

    /**
     * Constructor for Skill
     *
     * @param name
     * @param description
     */
    public Skill(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor for Skill
     *
     * @param name
     */
    public Skill(String name) {
        this.name = name;
    }

    /**
     * Constructor for Skill
     */
    public Skill() {

    }


    /**
     * Getter for the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * toString
     *
     * @return toString of skill
     */
    @Override public String toString() {
        return this.getShortName();
    }

    /**
     * Returns the string to be displayed in the main pane
     *
     * @return the string
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "\nDescription: " + this.description + "\n";

        if (this.getTags().isEmpty())
            returnString += "This skill has 0 Tags\n";
        else {
            returnString += "This skill has " + this.getTags().size() + " Tag(s):\n";
            for (Tag tag : this.getTags()) {
                returnString += "   " + tag.getShortName() + "(" + tag.getColour() + ")\n";
            }
        }

        return returnString;
    }

    @Override public void setShortName(String shortName) {
        this.name = shortName;
    }

    @Override public String getShortName() {
        return this.name;
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

        Label titleLabel = new Label(this.name);
        titleLabel.getStyleClass().add("gridPaneTitle");

        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.getStyleClass().add("gridPaneLabel");
        Label descriptionLabel = new Label(this.description);
        descriptionLabel.getStyleClass().add("gridLabel");

        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        tagListView.setCellFactory(param -> new skillListCell());

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle,descriptionLabel,
            tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle,0,1);
        GridPane.setConstraints(descriptionLabel, 1, 1);

        GridPane.setConstraints(tagsLabel,0,2);
        GridPane.setConstraints(tagListView,0,3);

        GridPane.setColumnSpan(tagListView, 2);

        ColumnConstraints c0 = new ColumnConstraints(400);
        ColumnConstraints c1 = new ColumnConstraints(400);

        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();
        RowConstraints r3 = new RowConstraints();
        RowConstraints r4 = new RowConstraints();

        grid.getColumnConstraints().addAll(c0,c1);
        grid.getRowConstraints().addAll(r1,r2,r3,r4);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }

    public class skillListCell extends ListCell<NavigatorItem> {
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
