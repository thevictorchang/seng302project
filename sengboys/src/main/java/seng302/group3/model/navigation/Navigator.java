package seng302.group3.model.navigation;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;
import seng302.group3.controller.App;
import seng302.group3.model.*;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;
import seng302.group3.model.navigation.navigator_items.PersonNavigatorItem;
import seng302.group3.model.navigation.navigator_items.StoryNavigatorItem;
import seng302.group3.model.sorting.Sort;

import javax.tools.Tool;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static seng302.group3.controller.App.getMainController;

/**
 * Created by ntr24 on 4/05/15.
 */
public class Navigator implements Serializable {
    private static final long serialVersionUID = 2107201500006L;

    private List<SerializableObservableList<NavigatorItem>> breadCrumbHistory = new LinkedList<>();
    private List<NavigatorItem> itemHistory = new LinkedList<>();
    public LocalDateTime currentlySelected;
    private int breadcrumbPos = 0;

    private transient ToolBar breadCrumbBar;
    private transient ComboBox orderSelectionCombo;
    public transient ListView<NavigatorItem> fxmlListView;
    private transient ContextMenu breadCrumbContextMenu = new ContextMenu();

    // Combo box options
    private static String ALPHABETICAL = "Name";
    private static String CHRONOLOGICAL = "Creation Date";
    private static String PRIORITY = "Priority";

    private static int NUM_BREADCRUMBS = 6;

    /**
     * Navigator constructor
     *
     * @param fxmlListView
     * @param breadCrumbBar
     * @param organisation
     */
    public Navigator(ListView<NavigatorItem> fxmlListView, ToolBar breadCrumbBar,
        ComboBox orderSelectionCombo, Organisation organisation) {
        this.fxmlListView = fxmlListView;
        this.breadCrumbBar = breadCrumbBar;
        this.orderSelectionCombo = orderSelectionCombo;

        createSortingOptions();

        breadCrumbHistory.add(breadcrumbPos, organisation.getNavigatorItems());
    }


    /**
     * Updates the Navigator based on the current selection
     *
     * @param selection
     */
    public void updateToSelection(NavigatorItem selection) {
        SerializableObservableList nextItems = selection.getNext();
        if (nextItems == null) {
            return;
        }

        if (selection instanceof NavigatorItem) {

            breadcrumbPos += 1;
            selection.setNavigatorDepth(breadcrumbPos); // so the navigator item knows its depth
            itemHistory.add(selection); // so we know the history of items
            breadCrumbHistory.add(breadcrumbPos, nextItems);
            this.refresh();
        }
    }

    /**
     * Refreshes the navigator
     *
     * @param fxmlListView
     * @param breadCrumbBar
     */
    public void refresh(ListView<NavigatorItem> fxmlListView, ToolBar breadCrumbBar,
        ComboBox orderSelectionCombo) {

        this.fxmlListView = fxmlListView;
        this.breadCrumbBar = breadCrumbBar;
        this.orderSelectionCombo = orderSelectionCombo;

        this.orderSelectionCombo.valueProperty().addListener(createComboListener());

        this.fxmlListView.setCellFactory(makeCallListener());
        this.fxmlListView.getSelectionModel().selectedItemProperty().addListener(makeChangeListener());

        breadCrumbContextMenu = new ContextMenu();
        refresh();
    }

    /**
     * Refreshes the navigator
     */
    public void refresh() {
        this.initBreadCrumbBar();
        createSortingOptions();
        sortToSelection();

        this.fxmlListView.getSelectionModel().clearSelection();
        this.fxmlListView
            .setItems(this.breadCrumbHistory.get(this.breadcrumbPos).getObservableList());

        App.getMainController().updateInfo(new GridPane());
    }


    /**
     * returns the listener for the combo bocontextMenux
     *
     * @return
     */
    private ChangeListener createComboListener() {
        return (observable, oldValue, newValue) -> {
            sortToSelection();
        };
    }

    /**
     * this method handles the options which appear within the combo box
     */
    private void createSortingOptions() {
        // if we dont have the other options then add them
        if (!orderSelectionCombo.getItems().contains(ALPHABETICAL)) {
            orderSelectionCombo.getItems().addAll(ALPHABETICAL, CHRONOLOGICAL);
            orderSelectionCombo.getSelectionModel().select(ALPHABETICAL);
        }

        if(itemHistory.size() > 1 && itemHistory.get(this.breadcrumbPos - 2)
            .getItem() instanceof Sprint
            && itemHistory.get(this.breadcrumbPos -1 ).getName().equals("Unallocated Tasks")) {
        // in this case we are looking at the stories for a backlog so have the priority option
            // if PRIORITY isn't in the list then add it and select it
            if (!orderSelectionCombo.getItems().contains(PRIORITY)) {
                orderSelectionCombo.getItems().add(PRIORITY);
                // this select means that navigating to a backlogs stories defaults to priority display
                orderSelectionCombo.getSelectionModel().select(PRIORITY);
            }
        } else if (orderSelectionCombo.getItems().contains(PRIORITY)) {
            if (orderSelectionCombo.getSelectionModel().getSelectedItem().equals(PRIORITY))
                orderSelectionCombo.getSelectionModel().select(ALPHABETICAL);
            orderSelectionCombo.getItems().remove(PRIORITY);
        }

    }

    /**
     * sorts the navigator view based on the current combo box selection
     */
    private void sortToSelection() {
        Object selectedSort = orderSelectionCombo.getSelectionModel().getSelectedItem();

        // Lets sort by Alphabetical representation
        if (selectedSort.equals(ALPHABETICAL)) {
            Sort.alphabeticalSort(
                this.breadCrumbHistory.get(this.breadcrumbPos).getObservableList(), false);
        }
        // Lets sort Chronologically
        else if (selectedSort.equals(CHRONOLOGICAL)) {
            Sort.chronologicalSort(
                this.breadCrumbHistory.get(this.breadcrumbPos).getObservableList(), false);
        }
        // Lets sort by Priority
        else if (selectedSort.equals(PRIORITY)) {
            Sort.prioritySort(this.breadCrumbHistory.get(this.breadcrumbPos).getObservableList(), false);
        }
    }

    public void homePress(){
        breadcrumbPos = 0;
        itemHistory.clear();
        breadCrumbHistory = new LinkedList<>(breadCrumbHistory.subList(0, 1));
        refresh();
    }

    /**
     * Initialises the breadcrumb navigator bar
     */
    private void initBreadCrumbBar() {

        Image homeImage = new Image(getClass().getResourceAsStream("/imgs/home.png"));
        Button aloneButton = new Button("", new ImageView(homeImage));
        aloneButton.setOnAction(event -> {
            breadcrumbPos = 0;
            itemHistory.clear();
            breadCrumbHistory = new LinkedList<>(breadCrumbHistory.subList(0, 1));
            refresh();
        });

        this.breadCrumbBar.getItems().clear();
        this.breadCrumbBar.getItems().add(aloneButton);

        if (itemHistory.size() < 1) {
            aloneButton.getStyleClass().addAll("item", "alone");
        } else {
            aloneButton.getStyleClass().addAll("item", "first");

            int i = 0;

            // This if handles the ellipsis
            if (itemHistory.size() - NUM_BREADCRUMBS > 0) {
                i = itemHistory.size() - NUM_BREADCRUMBS;
                Button previousButton = new Button("...");
                previousButton.getStyleClass().addAll("item", "middle");
                previousButton.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        breadcrumbPos = breadcrumbPos - NUM_BREADCRUMBS;
                        itemHistory = new LinkedList<>(itemHistory.subList(0, breadcrumbPos));
                        breadCrumbHistory =
                            new LinkedList<>(breadCrumbHistory.subList(0, breadcrumbPos + 1));
                        refresh();
                    } else if (event.getButton() == MouseButton.SECONDARY) {

                        breadCrumbContextMenu.hide();
                        breadCrumbContextMenu.getItems().clear();

                        for (NavigatorItem item : itemHistory.subList(0, itemHistory.size() - NUM_BREADCRUMBS)) {
                            MenuItem menuItem = new MenuItem(item.toString());
                            breadCrumbContextMenu.getItems().add(0, menuItem);
                            menuItem.setOnAction(event1 -> {
                                breadcrumbPos =
                                    breadcrumbPos - (itemHistory.size() - itemHistory.indexOf(item)
                                        - 1);
                                itemHistory =
                                    new LinkedList<>(itemHistory.subList(0, breadcrumbPos));
                                breadCrumbHistory = new LinkedList<>(
                                    breadCrumbHistory.subList(0, breadcrumbPos + 1));
                                refresh();
                            });
                        }
                        breadCrumbContextMenu
                            .show(breadCrumbBar, event.getScreenX(), event.getScreenY());

                    }

                });
                this.breadCrumbBar.getItems().add(previousButton);
            }

            for (; i < this.itemHistory.size() - 1; i++) {
                NavigatorItem item = this.itemHistory.get(i);
                Button crumbButton = new Button(createCrumbString(item.toString()));
                crumbButton.getStyleClass().addAll("item", "middle");

                crumbButton.setOnAction(event -> {
                    breadcrumbPos = item.getNavigatorDepth();
                    itemHistory = new LinkedList<>(itemHistory.subList(0, breadcrumbPos));
                    breadCrumbHistory =
                        new LinkedList<>(breadCrumbHistory.subList(0, breadcrumbPos + 1));
                    refresh();
                });
                this.breadCrumbBar.getItems().add(crumbButton);
            }

            NavigatorItem item = this.itemHistory.get(this.itemHistory.size() - 1);
            Button lastButton = new Button(createCrumbString(item.toString()));
            lastButton.getStyleClass().addAll("item", "last");
            this.breadCrumbBar.getItems().add(lastButton);
        }
    }


    /**
     * Generates the string to be placed on the breadcrumb buttons. If the string is too long it will be cut with '...'
     *
     * @param fullString - String to be formatted
     * @return - formatted String
     */
    private String createCrumbString(String fullString) {
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

        Font font = new Font("Tahoma", Font.PLAIN, 12);

        int textwidth = (int) (font.getStringBounds(fullString, frc).getWidth());

        if (textwidth < 95) {
            return fullString;
        }
        textwidth = 0;
        String output = "";
        int i = 0;
        while (textwidth < 85) {
            output += fullString.charAt(i);
            textwidth = (int) (font.getStringBounds(output, frc).getWidth());
            i++;
        }
        output += "...";

        return output;
    }


    private ChangeListener<NavigatorItem> makeChangeListener(){
        return (observable, oldValue, newValue) -> {
            if(newValue != null && newValue.getItem() instanceof Element){
                currentlySelected = ((Element)newValue.getItem()).getDateCreated();
            }
        };
    }

    /**
     * returns a CallBacklistener used for the listview. This handles the navigation of the navigator
     *
     * @return - new CallBack listener for the navigator
     */
    private Callback<ListView<NavigatorItem>, ListCell<NavigatorItem>> makeCallListener() {
        return param -> {
            final ListCell<NavigatorItem> cell = new ListCell<NavigatorItem>() {


                /**
                 * Overwritten method for updateItem
                 * @param item
                 * @param b
                 */
                @Override protected void updateItem(NavigatorItem item, boolean b) {
                    super.updateItem(item, b);
                    if (item != null) {
                        HBox hBox;
                        GridPane grid;
                        setText(null);
                        //TODO: add tag icon, add tooltip to icon then make it open tag maintencne dialog on click

                        if(item instanceof StoryNavigatorItem){
                            Story story = (Story) item.getItem();
                            // If we have selected to show story state highlights
                            if(App.getMainController().storyStateVisible)
                                hBox = highlightStories(story);
                            else
                                hBox = new HBox();

                            grid = getGrid(hBox, item.toString(), "story", item);

                            setGraphic(grid);

                        } else if(item instanceof PersonNavigatorItem) {
                            Person person = (Person) item.getItem();
                            hBox = highlightPeople(person);
                            grid = getGrid(hBox, item.toString(), "person", item);

                            setGraphic(grid);

                        } else{
                            grid = getGrid(new HBox(),item.toString(),"", item);
                            //setText(item.toString());
                            setGraphic(grid);
                        }
                    } else {
                        setText(null);
                        setGraphic(null);
                    }

                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                /**
                 * overwritten method for handling mouse click
                 * @param event
                 */
                @Override public void handle(MouseEvent event) {
                    App.getMainController().getContextMenu().hide();
                    if (event.getButton() == MouseButton.SECONDARY) {
                        NavigatorItem selection = cell.getItem();
                        if (!(selection instanceof FolderNavigatorItem)) {
                            App.getMainController().getContextMenu()
                                .show(cell, Side.BOTTOM, event.getX(), event.getY());
                        }
                    } else {
                        if (event.getClickCount() == 2) {
                            if (cell.getItem() != null) {
                                /*if(App.getMainController().navigatorType){
                                    NavigatorItem selection = cell.getItem();
                                    if ((selection instanceof FolderNavigatorItem)) {
                                        if(event.getX() < cell.getWidth()*0.8){
                                            App.getMainController().addItemFolder((FolderNavigatorItem) selection);
                                        }
                                    }else{
                                        App.getMainController().edit();
                                    }
                                }else{
                                }*/
                                updateToSelection(cell.getItem());

                            }
                        }
                        else if(event.getClickCount() == 3){
                            cell.getListView().getSelectionModel().select(null);
                        }
                        else{
                            NavigatorItem selection = cell.getItem();
                            if (selection instanceof NavigatorItem) {
                                getMainController().updateInfoPane(selection);
                            }
                        }
                    }
                }
            });
            return cell;
        };
    }

    /**
     * Builds a grid used for specific objects in the navigator. Creates a layout based on what is
     * being given to the grid.
     * @param hBox
     * @param label
     * @param type
     * @return
     */
    private GridPane getGrid(HBox hBox, String label, String type, NavigatorItem item) {
        GridPane grid = new GridPane();
        HBox tagHBox = new HBox();

        Image tag = new Image(getClass().getResourceAsStream("/imgs/tag.png"));
        Image tagNone = new Image(getClass().getResourceAsStream("/imgs/tagNone.png"));
        ImageView tagImageView = new ImageView(tag);
        tagImageView.setFitWidth(16);

        //if navigator item is an actual element:
        if ((item.getItem() instanceof Element) && (!(item.getItem() instanceof Tag))) {
            Element element = (Element) item.getItem();
            GridPane tooltipGrid = new GridPane();
            tooltipGrid.setVgap(3);

            if (element.getTags().size() == 0) { // if the element has no tags
                Label noTagLabel = new Label("No Tags");
                tagImageView.setImage(tagNone);
                noTagLabel.setStyle("-fx-font-size: 100%");
                tooltipGrid.getChildren().add(noTagLabel);

                GridPane.setConstraints(noTagLabel, 0, 0);
                ColumnConstraints d1 = new ColumnConstraints();
                d1.setPercentWidth(100);
                RowConstraints r1 = new RowConstraints();
                r1.setPercentHeight(100);
                tooltipGrid.getColumnConstraints().add(d1);
                tooltipGrid.getRowConstraints().add(r1);

            } else {
                Integer i = 0;
                ColumnConstraints d1 = new ColumnConstraints();
                ColumnConstraints d2 = new ColumnConstraints();

                d1.setPrefWidth(100);
                d2.setPrefWidth(16);

                tooltipGrid.getColumnConstraints().addAll(d1, d2);
                for (Tag elementTag : element.getTags()) {
                    String nameStr = "/imgs/tags/tag"+ elementTag.getColour() +".png";

                    Image tag1 = new Image(getClass().getResourceAsStream(nameStr));
                    ImageView tagImageView1 = new ImageView(tag1);

                    Label label1 = new Label(elementTag.getShortName());
                    label1.setStyle("-fx-font-size: 100%");

                    tooltipGrid.getChildren().addAll(label1,tagImageView1);

                    GridPane.setConstraints(label1, 0, i);
                    GridPane.setConstraints(tagImageView1, 1, i);

                    RowConstraints r1 = new RowConstraints();
                    r1.setPrefHeight(16);
                    tooltipGrid.getRowConstraints().add(r1);

                    i++;
                }
            }

            tagHBox.getChildren().add(tagImageView);

            Tooltip tooltip = new Tooltip();
            tooltip.setGraphic(tooltipGrid);
            Tooltip.install(tagHBox, tooltip);
            tagHBox.setMaxWidth(16);
            tagHBox.setPrefWidth(16);

            tagImageView.setOnMouseClicked(event ->{
                App.getMainController().tagMaker();
            });

        }

        grid.setHgap(5);
        Label textLabel = new Label();
        textLabel.setText(label);
        grid.getChildren().add(textLabel);
        grid.getChildren().add(hBox);
        grid.getChildren().add(tagHBox);





        GridPane.setConstraints(textLabel, 0, 0);
        GridPane.setConstraints(tagHBox, 1, 0);
        GridPane.setConstraints(hBox, 2, 0);


        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        ColumnConstraints cTag = new ColumnConstraints();



        if(type.equals("person")){
            c1.setPrefWidth(145);
            cTag.setPrefWidth(16);
            c2.setPrefWidth(48);
        }else if(type.equals("story")){
            c1.setPrefWidth(172);
            cTag.setPrefWidth(16);
            c2.setPrefWidth(16);
        }else{
            c1.setPrefWidth(188);
            cTag.setPrefWidth(16);
            c2.setPrefWidth(0);
        }
        grid.getColumnConstraints().addAll(c1, cTag, c2);


        return grid;
    }

    /**
     * Gives the cell a highlight image for people to show what position they can fill and which
     * postion that they do fill.
     * @param person
     * @return
     */
    private HBox highlightPeople(Person person) {
        HBox hBox = new HBox();
        Image POBlank = new Image(getClass().getResourceAsStream("/imgs/roles/PO_Blank.png"));
        Image POFilled = new Image(getClass().getResourceAsStream("/imgs/roles/PO_Filled.png"));
        Image SMBlank = new Image(getClass().getResourceAsStream("/imgs/roles/SM_Blank.png"));
        Image SMFilled = new Image(getClass().getResourceAsStream("/imgs/roles/SM_Filled.png"));
        Image DBlank = new Image(getClass().getResourceAsStream("/imgs/roles/D_Blank.png"));
        Image DFilled = new Image(getClass().getResourceAsStream("/imgs/roles/D_Filled.png"));
        Image Blank = new Image(getClass().getResourceAsStream("/imgs/roles/Blank.png"));

        ImageView poImageView = new ImageView(Blank);
        ImageView dImageView = new ImageView(DBlank);
        ImageView smImageView = new ImageView(Blank);

        for(Skill s: person.getSkills()){
            if(s.getShortName().equals("SM")){
                smImageView = new ImageView(SMBlank);
            }else if(s.getShortName().equals("PO")){
                poImageView = new ImageView(POBlank);
            }
        }
        if(person.getTeam() != null){
            if(person.getTeam().getProductOwner() == person){
                poImageView = new ImageView(POFilled);
            } else if(person.getTeam().getScrumMaster() == person){
                smImageView = new ImageView(SMFilled);
            } else if(person.getTeam().getDevTeamMembers().contains(person)) {
                dImageView = new ImageView(DFilled);
            }
        }

        dImageView.setFitHeight(16);
        dImageView.setFitWidth(16);
        poImageView.setFitHeight(16);
        poImageView.setFitWidth(16);
        smImageView.setFitHeight(16);
        smImageView.setFitWidth(16);


        hBox.getChildren().add(poImageView);
        hBox.getChildren().add(new Label(" "));
        hBox.getChildren().add(smImageView);
        hBox.getChildren().add(new Label(" "));
        hBox.getChildren().add(dImageView);


        return hBox;
    }

    /**
     * Gives the navigator cell a highlight image depending on the state of the story.
     * @param story
     * @return
     */
    private HBox highlightStories(Story story) {
        HBox hBox = new HBox();
        Image Red = new Image(getClass().getResourceAsStream("/imgs/highlights/RedB.png"));
        Image Green = new Image(getClass().getResourceAsStream("/imgs/highlights/GreenB.png"));
        Image Orange = new Image(getClass().getResourceAsStream("/imgs/highlights/OrangeB.png"));
        ImageView rImageView = new ImageView(Red);
        ImageView gImageView = new ImageView(Green);
        ImageView oImageView = new ImageView(Orange);
        rImageView.setFitHeight(16);
        rImageView.setFitWidth(16);
        gImageView.setFitHeight(16);
        gImageView.setFitWidth(16);
        oImageView.setFitHeight(16);
        oImageView.setFitWidth(16);
        //Check to see which image to add
        if(story.dependsOnLowerPriority(story.getPriority())){
            hBox.getChildren().add(rImageView);
        }
        else if(story.isReady()){
            hBox.getChildren().add(gImageView);
        }
        else if(story.getAcceptanceCriteria().size() > 0 && story.getEstimate() > -1){
            hBox.getChildren().add(oImageView);
        }
        return hBox;
    }

    /**
     * Writes object to output stream
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

    }

    /**
     * Reads object from input stream
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
