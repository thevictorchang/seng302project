package seng302.group3.model;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import seng302.group3.controller.App;
import seng302.group3.model.io.Editor;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.io.property_sheets.BacklogSheet;
import seng302.group3.model.navigation.Navigator;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;
import seng302.group3.model.navigation.navigator_items.StoryNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TagNavigatorItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

import static java.lang.Math.abs;

/**
 * Created by vch51 on 15/05/15.
 */
public class Backlog extends Element {

    private static final long serialVersionUID = 1806201500000L;

    //attributes.
    private String shortName;
    private String description;
    private Person productOwner;
    private Set<Integer> priorities;

    private SerializableObservableList<Story> stories = new SerializableObservableList<>();

    // default scale we will use
    private String scale = BacklogSheet.FIBONACCI;

    /**
     * Constructor for Backlog
     */
    public Backlog() {
        this.shortName = "";
        this.description = "";
        this.productOwner = null;
    }

    /**
     * Constructor for Backlog
     * @param shortName
     */
    public Backlog(String shortName) {
        this.shortName = shortName;
        this.description = "";
        this.productOwner = null;
    }

    /**
     * Constructor for Backlog
     * @param shortName
     * @param description
     */
    public Backlog(String shortName, String description) {
        this.shortName = shortName;
        this.description = description;
        this.productOwner = null;
    }

    /**
     * Constructor for Backlog
     * @param shortName
     * @param description
     * @param productOwner
     */
    public Backlog(String shortName, String description, Person productOwner) {
        this.shortName = shortName;
        this.description = description;
        this.productOwner = productOwner;
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
     * Setter for description
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the product owner
     *
     * @return
     */
    public Person getProductOwner() {
        return productOwner;
    }

    /**
     * Setter for the product owner
     *
     * @param productOwner the new Product Owner
     */
    public void setProductOwner(Person productOwner) {
        this.productOwner = productOwner;
    }

    /**
     * Getter for Stories
     *
     * @return a collection of Stories
     */
    public Collection<Story> getStories() {
        return stories;
    }

    /**
     * Setter for stories, may not be used as much as addStory
     *
     * @param stories a collection of Stories
     */
    public void setStories(Collection<Story> stories) {
        this.stories.clear();
        for (Story s : stories) {
            this.addStory(s);
        }
    }

    /**
     * Adds a story to this backlog
     *
     * @param story story to be added
     */
    public void addStory(Story story) {
        this.stories.add(story);
    }

    /**
     * Removes a story from this backlog
     *
     * @param story
     */
    public void removeStory(Story story) {
        this.stories.remove(story);
    }


    /**
     * simply returns the short name of the backlog
     *
     * @return - short name of the backlog
     */
    @Override public String toString() {
        return this.getShortName();
    }

    /**
     * Returns a string that will represent the backlog in the information pane
     *
     * @return information pane-friendly string.
     */
    public String getPaneString() {
        String returnString = "";
        returnString += "\nShort Name: " + this.getShortName() + "\n";

        returnString += "\nProduct Owner: " + this.getProductOwner() + "\n";


        returnString += "\nThis backlog has " + this.stories.size() + " stories \n";
        for (Story story : getStories()) {
            returnString += "    " + story.getFullName() + "\n";
        }

        if (this.getTags().isEmpty())
            returnString += "This backlog has 0 Tags\n";
        else {
            returnString += "This backlog has " + this.getTags().size() + " Tag(s):\n";
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

        Label productOwnerTitle = new Label("Product Owner: ");
        productOwnerTitle.getStyleClass().add("gridPaneLabel");
        Button productOwnerButton = new Button(this.productOwner.getShortName());
        productOwnerButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, productOwner);
        });
        productOwnerButton.getStyleClass().add("gridPaneButton");

        Label storiesLabel = new Label("Stories:");
        storiesLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> storyListView = new ListView<>(stories.getObservableList());
        storyListView.getStyleClass().add("gridPaneListView");
        storyListView.setPrefHeight(53*Math.max(1, storyListView.getItems().size()));
        storyListView.setPlaceholder(new Label("No Stories"));

        Label tagsLabel = new Label("Tags:");
        tagsLabel.getStyleClass().add("gridPaneLabel");
        ListView<NavigatorItem> tagListView = new ListView<>(this.getTags().getObservableList());
        tagListView.getStyleClass().add("gridPaneListView");
        tagListView.setPrefHeight(53*Math.max(1, tagListView.getItems().size()));
        tagListView.setPlaceholder(new Label("No Tags"));

        storyListView.setCellFactory(param -> new backlogListCell());
        tagListView.setCellFactory(param -> new backlogListCell());

        grid.getChildren().addAll(titleLabel,editButton,descriptionTitle, descriptionLabel,productOwnerTitle,productOwnerButton,
            storiesLabel,storyListView,tagsLabel,tagListView);

        GridPane.setConstraints(titleLabel,0,0);
        GridPane.setConstraints(editButton, 1, 0);
        GridPane.setConstraints(descriptionTitle, 0, 1);
        GridPane.setConstraints(descriptionLabel, 1, 1);
        GridPane.setConstraints(productOwnerTitle,0,2);
        GridPane.setConstraints(productOwnerButton,1,2);
        GridPane.setConstraints(storiesLabel,0,3);
        GridPane.setConstraints(storyListView, 0, 4);
        GridPane.setConstraints(tagsLabel,0,5);
        GridPane.setConstraints(tagListView,0,6);

        GridPane.setColumnSpan(storyListView, 2);
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

    public class backlogListCell extends ListCell<NavigatorItem> {
        @Override public void updateItem(NavigatorItem item, boolean empty) {
            super.updateItem(item, empty);
            Button itemButton;
            if(empty || item == null){
                setText(null);
                setGraphic(null);
            } else if(item instanceof StoryNavigatorItem){
                Story story = (Story) item.getItem();
                itemButton = new Button(story.getShortName());
                itemButton.setOnAction((event) -> {
                    new Editor().edit(App.getMainController().currentOrganisation, story);
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

    /**
     * Handles the navigator contents for this backlog
     *
     * @return
     */
    public SerializableObservableList<NavigatorItem> getNavigatorItems() {
        SerializableObservableList<NavigatorItem> navigatorItems =
            new SerializableObservableList<>();

        FolderNavigatorItem storiesItem = new FolderNavigatorItem("Stories");
        storiesItem.setNext(this.stories);

        navigatorItems.add(storiesItem);
        return navigatorItems;
    }

    /**
     * this loops through the stories in their order and sets their priority
     */
    public void resetPriorities() {
        int p = 0;
        for (Story story : this.getStories()) {
            story.setPriority(p);
            p++;
        }

    }


    /**
     * //updates/sets the priority list for use in the sprint dialog
     */
    private void setPriorities() {
        priorities.clear();
        for (Story story : stories) {
            priorities.add(story.getPriority());
        }
    }

    @Override public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override public String getShortName() {
        return this.shortName;
    }

    /**
     * Gets the string representation of the estimation scale
     * @return Estimation scale
     */
    public String getScale() {
        return scale;
    }

    /**
     * Sets the string representation of the estimation scale
     */
    public void setScale(String scale) {
        this.scale = scale;
    }

    public String scaleToString(Double value) {
        String scaleString;
        switch (getScale()) {
            case BacklogSheet.FIBONACCI:
                scaleString = getScaleValue(value, BacklogSheet.fibonacci);
                break;
            case BacklogSheet.NATURAL:
                scaleString = getScaleValue(value, BacklogSheet.natural);
                break;
            case BacklogSheet.TSHIRTS:
                scaleString = getScaleValue(value, BacklogSheet.tShirts);
                break;
            default:
                scaleString = getScaleValue(value, BacklogSheet.fibonacci);
                break;
        }
        return scaleString;
    }


    public static String getScaleValue(double value, Map<String, Double> scale){
        Iterator it = scale.entrySet().iterator();
        Double closest = 0.0;
        String key = null;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            if(abs((Double) pair.getValue() - value) <= abs(closest - value)){
                closest = (Double) pair.getValue();
                key = (String) pair.getKey();
            }
        }
        return key;
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
    }
}
