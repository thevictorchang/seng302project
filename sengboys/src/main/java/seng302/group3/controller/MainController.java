package seng302.group3.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.group3.model.*;
import seng302.group3.model.gui.AllTasks;
import seng302.group3.model.gui.BurndownChart;
import seng302.group3.model.gui.story_workspace.StoryWorkspace;
import seng302.group3.model.gui.scrum_board.ScrumBoard;
import seng302.group3.model.gui.scrum_board.ScrumBoardStoryCellFactory;
import seng302.group3.model.io.*;
import seng302.group3.model.io.property_sheets.StorySheet;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.FolderNavigatorItem;
import seng302.group3.model.search.SearchDialog;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ntr24 on 12/03/15.
 */
public class MainController implements Initializable {

    @FXML private Tab overViewTab;
    @FXML private Tab allTasksTab;
    @FXML private Tab scrumBoardTab;
    @FXML private Tab burndownChartTab;
    @FXML private Tab storyWorkspaceTab;

    @FXML private TabPane tabs;
    @FXML private Separator separatorone;
    @FXML private Separator separatortwo;
    @FXML private CheckMenuItem toolbarAddCheck;
    @FXML private CheckMenuItem toolbarEditCheck;
    @FXML private CheckMenuItem toolbarFileCheck;
    @FXML private CheckMenuItem storyHighlightsCheckItem;
    @FXML private HBox toolbarFileActions;
    @FXML private HBox toolbarAdditions;
    @FXML private HBox toolbarEditor;


    @FXML private Label outputLine;
    @FXML private Label infoText;
    @FXML private MenuItem addPerson;
    @FXML private MenuItem addSkill;
    @FXML private MenuItem addRelease;
    @FXML private CheckMenuItem navigatorCheckItem;

    @FXML private VBox navigator;


    @FXML public ToolBar breadCrumbToolBar;
    @FXML public ComboBox orderSelectionCombo;
    @FXML public ListView<NavigatorItem> navigatorListView;

    @FXML private Label infoLabel;

    @FXML private MenuItem editMenuItem;

    public Organisation currentOrganisation;
    public Organisation revertableOrganisation = null;
    public Object currentlySelected;

    @FXML private MenuItem searchMenuItem;
    @FXML private MenuItem undoMenuItem;
    @FXML private MenuItem redoMenuItem;
    @FXML private MenuItem revertMenuItem;
    @FXML private MenuItem deleteMenuItem;
    @FXML private MenuItem addTagMenuItem;

    @FXML private MenuItem menuBarSaveButton;
    @FXML private MenuItem menuBarSaveAsButton;
    @FXML private MenuItem menuBarLoadButton;

    @FXML private MenuItem menuBarNewOrganisationButton;

    @FXML private MenuItem generateSampleOrgButton;

    @FXML public ObservableList lis;

    private List<Organisation> history = new LinkedList<>();
    private List<String> historyMessages = new LinkedList<>();
    private int historyPosition = -1;

    public static boolean storyStateVisible = true;


    private ContextMenu contextMenu = new ContextMenu();


    private Stage stage;

    /**
     * Constructor for MainController, used in the App class (entry point)
     */
    public MainController() {
    }


    /**
     * setter for the javafx stage
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * getter for the javafx stage
     *
     * @return stage
     */
    public Stage getStage() {
        return stage;
    }

    public double widthScale(){
        return (stage.getWidth()/1920.0)/0.875;
    }

    public double heightScale(){
        return (stage.getHeight()/1080.0)/0.950;
    }

    /**
     * When the 'Exit' item is selected from the 'File' menu bar drop down the program will close
     *
     * @param event - the action event
     */
    public void menuBarExitButton(ActionEvent event) {
        Dialogs d = new Dialogs();
        if (currentOrganisation.getUnsaved()) {
            d.SaveQuitProgram();
        } else {
            d.QuitProgram();
        }
    }

    @FXML public void searchMenuItem(ActionEvent actionEvent) {
        SearchDialog d = new SearchDialog();
        d.searchDialog();
    }

    /**
     * When the 'Undo' item is selected from the 'Edit' drop down menu, the last change is undone.
     *
     * @param actionEvent - the action eventWhen the 'Edit Organisation' item is selected from the 'File' menu bar drop down a property sheet
     *                    which can be used to edit the project will appear
     * @param actionEvent - the action event
     */
    @FXML public void undoMenuItem(ActionEvent actionEvent) {
        if (this.historyPosition > 0) {
            printInfo("Action undone : " + historyMessages.get(historyPosition));
            this.historyPosition--;
            this.currentOrganisation = history.get(historyPosition).serializedCopy();
            this.currentOrganisation.setOrganisationNavigator(navigatorListView, breadCrumbToolBar,
                orderSelectionCombo);
        } else {

            new Dialogs().infoDialog("Nothing to Undo", "Undo Error");
        }
        if (this.historyPosition < 2) {
            getStage().setTitle(currentOrganisation.getShortName());
            currentOrganisation.setUnsaved(false);
        }
        ScrumBoard.getInstance().refresh();
        BurndownChart.getInstance().refresh();
        AllTasks.getInstance().refresh();
        StoryWorkspace.getInstance().refresh();
    }

    /**
     * When the 'Redo' item is selected from the 'Edit' drop down menu, the last undone change is redone.
     *
     * @param actionEvent - the action event
     */
    @FXML public void redoMenuItem(ActionEvent actionEvent) {
        if (this.historyPosition < history.size() - 1) {
            this.historyPosition++;
            this.currentOrganisation = history.get(historyPosition).serializedCopy();
            this.currentOrganisation.setOrganisationNavigator(navigatorListView, breadCrumbToolBar,
                orderSelectionCombo);
            printInfo("Action redone : " + historyMessages.get(historyPosition));
            getStage().setTitle("*" + currentOrganisation.getShortName());
            currentOrganisation.setUnsaved(true);

        } else {
            new Dialogs().infoDialog("Nothing to Redo", "Redo Error");
        }
        ScrumBoard.getInstance().refresh();
        BurndownChart.getInstance().refresh();
        AllTasks.getInstance().refresh();
        StoryWorkspace.getInstance().refresh();
    }


    /**
     * @param o
     */
    public void addHistory(Organisation o, String message) {
        getStage().setTitle("*" + o.getShortName());
        o.setUnsaved(true);
        if (this.historyPosition > -1) {
            this.history = this.history.subList(0, this.historyPosition + 1);
            historyMessages = historyMessages.subList(0, this.historyPosition + 1);
        }
        this.history.add(this.currentOrganisation);
        historyMessages.add(message);
        printInfo(message);
        this.historyPosition++;
        this.currentOrganisation = o;

        this.currentOrganisation.incrementOrgId();
        this.currentOrganisation
            .setOrganisationNavigator(navigatorListView, breadCrumbToolBar, orderSelectionCombo);

        // we want to select the old thing we had selected
        if(o.getNavigator().currentlySelected != null) {
            for (NavigatorItem item : o.getNavigator().fxmlListView.getItems()) {
                if(item.getItem() instanceof Element &&
                        ((Element)item.getItem()).getDateCreated().equals(o.getNavigator().currentlySelected)){
                    o.getNavigator().fxmlListView.getSelectionModel().select(item);
                }
            }
        }else{
            o.getNavigator().fxmlListView.getSelectionModel().clearSelection();
        }

        ScrumBoard.getInstance().refresh();
        BurndownChart.getInstance().refresh();
        AllTasks.getInstance().refresh();
        StoryWorkspace.getInstance().refresh();
    }

    @FXML public void updateInfoPane(NavigatorItem selection) {
        if (selection != null) {
            overViewTab.setContent(selection.getGrid());
            //infoLabel.setText(selection.getName());
            //infoText.setText(selection.getDescription());
        }
    }



    @FXML public void menuBarNavigatorToggle(ActionEvent actionEvent) {
        navigator.managedProperty().bind(navigator.visibleProperty());
        navigator.setVisible(navigatorCheckItem.isSelected());
        breadCrumbToolBar.setVisible(navigatorCheckItem.isSelected());
    }


    /**
     * When the 'Save' item is selected from the 'File' menu bar drop down the project will be serialized
     *
     * @param event - the action event
     */
    @FXML public void menuBarSaveButton(ActionEvent event) {
        currentOrganisation.updatePeopleProjectAllocation();
        if(currentOrganisation.getSaved() && !currentOrganisation.getUnsaved()){
            return;
        }
        System.out.println(Saver.getSavePath().toString());
        if (Saver.getSaveName().toString().contains(currentOrganisation.getShortName())
            && currentOrganisation.getSaved()){
            new Dialogs().SaveDialog(currentOrganisation);

        }
        else
            menuBarSaveAsButton(event);
    }


    /**
     * When the 'Save As...' item is selected from the 'File' menu bar drop down the project will be serialized with
     * path specified by filechooser
     *
     * @param event - the action event
     */
    @FXML public void menuBarSaveAsButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Organisation");
        fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
        fileChooser.setInitialFileName(currentOrganisation.getShortName().replace(".org", "") + ".org");
        fileChooser.getExtensionFilters()
            .add(new FileChooser.ExtensionFilter("Scrumpy Organisation File", "*.org"));
        File file = fileChooser.showSaveDialog(this.stage);
        if (file != null) {
            currentOrganisation.setSaved(Boolean.TRUE);
            printInfo("Saved");
            String fileName = file.toPath().getFileName().toString();
            String shortName = file.toPath().getFileName().toString().replace(".org",
                "");
            shortName = shortName.replaceAll("\\s+","");
            currentOrganisation.setShortName(shortName); //Set the name of the organisation to the name of the save file
            getStage().setTitle(currentOrganisation.getShortName()); //Update the window title
            currentOrganisation.setUnsaved(false);
            currentOrganisation.setFilename(fileName);
            Saver.save(file.getAbsolutePath().replace(" ", ""), currentOrganisation);

            //Created a revertable point
            revertableOrganisation = currentOrganisation.serializedCopy();

            menuBarSaveButton.setDisable(false);
        }
    }

    /**
     * When the 'Load' item is selected from the 'File' menu bar drop down the project will be serialized
     *
     * @param event - the action event
     */
    @FXML public void menuBarLoadButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Organisation");

        File file;
        try{
            fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
            file = fileChooser.showOpenDialog(this.stage);
        } catch (Exception e) {
            fileChooser.setInitialDirectory(Paths.get("/").toFile());
            file = fileChooser.showOpenDialog(this.stage);
        }

        if (file != null) {
            Organisation tempOrg = currentOrganisation;
            currentOrganisation = Loader.load(file.getAbsolutePath());
            if (currentOrganisation != null) {
                clearHistory();
                this.currentOrganisation
                    .setOrganisationNavigator(navigatorListView, breadCrumbToolBar,
                        orderSelectionCombo);
                addHistory(currentOrganisation.serializedCopy(),
                    "Opened Organisation '" + currentOrganisation.getShortName() + "'");
                revertableOrganisation = currentOrganisation.serializedCopy();
                menuBarSaveButton.setDisable(false);
                menuBarSaveAsButton.setDisable(false);

                undoMenuItem.setDisable(false);
                redoMenuItem.setDisable(false);
                getStage().setTitle(currentOrganisation.getShortName());
                currentOrganisation.setUnsaved(false);
                addPerson.setDisable(false);
                if (currentOrganisation.getPeople().size() != 0) {
                    addSkill.setDisable(false);
                }
            } else {
                currentOrganisation = tempOrg;
            }
        }
    }


    /**
     * Export button to generate XML report
     *
     * @param event - the action event
     */
    @FXML public void menuBarExportButton(ActionEvent event) {
        Editor e = new Editor();
        e.GenerateReport(currentOrganisation);
    }

    /**
     * Button to import an XML report: currently not functional
     *
     * @param event - the action event
     */
    @FXML public void menuBarImportButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import from XML");
        fileChooser.setInitialDirectory(Saver.getSavePath().toAbsolutePath().toFile());
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            currentOrganisation = StatusReport.importOrganisationXML(file.getAbsolutePath());
            if (currentOrganisation != null) {
                clearHistory();
                this.currentOrganisation
                    .setOrganisationNavigator(navigatorListView, breadCrumbToolBar,
                        orderSelectionCombo);
                addHistory(currentOrganisation.serializedCopy(),
                    "Opened Organisation '" + currentOrganisation.getShortName() + "'");
                revertableOrganisation = currentOrganisation.serializedCopy();
                menuBarSaveButton.setDisable(false);
                menuBarSaveAsButton.setDisable(false);

                undoMenuItem.setDisable(false);
                redoMenuItem.setDisable(false);

                addPerson.setDisable(false);
                if (currentOrganisation.getPeople().size() != 0) {
                    addSkill.setDisable(false);
                }
            }
        }
    }

    /**
     * initializes the tab with the Scrum board
     */
    private void initScrumBoard(){
        this.scrumBoardTab.setContent(ScrumBoard.getInstance().getScrumBoard());
        this.tabs.getSelectionModel().select(this.scrumBoardTab);

        this.stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                ScrumBoard.scrumboardSizeMultipler.set(stage.getWidth() / 1700.0);
                if (ScrumBoard.scrumboardSizeMultipler.get() > 1.0)
                    ScrumBoard.scrumboardSizeMultipler.set(1.0);
                
                ScrumBoardStoryCellFactory.columnWidthProperty.set(ScrumBoardStoryCellFactory.columnWidth * ScrumBoard.scrumboardSizeMultipler.get());
                //ScrumBoard.scrumboardPosition.set(stage.getWidth() * ScrumBoard.scrumboardSizeMultipler.get());
            }
        });
    }

    private void initBurndownChart(){
        this.burndownChartTab.setContent(BurndownChart.getInstance().getBurndownChart());
    }

    private void initAllTasks(){
        this.allTasksTab.setContent(AllTasks.getInstance().getAllTasks());
    }

    private void initStoryWorkspace() {
        this.storyWorkspaceTab.setContent(StoryWorkspace.getInstance().getStoryWorkspace());
    }

    /**
     * Initialises the states of menu items and performs other set up, including setting the keyboard shortcuts for
     * later
     */
    @Override public void initialize(URL url, ResourceBundle rb) {

        //ListView scrumTitles = new ListView();
        //scrumBoardList.add(scrumTitles);
        navigatorCheckItem.setSelected(true);
        toolbarFileCheck.setSelected(true);
        toolbarEditCheck.setSelected(true);
        toolbarAddCheck.setSelected(true);
        storyHighlightsCheckItem.setSelected(true);

        //Tab detatcher
        //TabPaneDetacher.create().makeTabsDetachable(tabs);

        //addPerson.setDisable(true);
        addSkill.setDisable(false);
        searchMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        editMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN));
        menuBarSaveButton.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuBarSaveAsButton.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN));
        menuBarLoadButton
            .setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        generateSampleOrgButton.setAccelerator(new KeyCodeCombination(KeyCode.SLASH, KeyCombination.CONTROL_DOWN));

        editMenuItem.setDisable(true);

        // ContextMenu Initialisation
        Menu newItem = new Menu("New...");
        MenuItem edit = new MenuItem("Edit");
        MenuItem revert = new MenuItem("Revert");
        MenuItem delete = new MenuItem("Delete");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        MenuItem openTags = new MenuItem("Tags");
        addNewMenuItems(newItem);
        contextMenu.getItems()
            .addAll(newItem, new SeparatorMenuItem(), undo, redo, new SeparatorMenuItem(), edit,
                delete, openTags);
        edit.setOnAction(secondaryEvent -> {
            menuButtonEdit(secondaryEvent);
        });
        revert.setOnAction(secondaryEvent -> {
            menuBarRevertButton(secondaryEvent);
        });
        delete.setOnAction(secondaryEvent -> {
            menuBarDeleteButton(secondaryEvent);
        });
        undo.setOnAction(secondaryEvent -> {
            undoMenuItem(secondaryEvent);
        });
        redo.setOnAction(secondaryEvent -> {
            redoMenuItem(secondaryEvent);
        });
        openTags.setOnAction(secondaryEvent -> {
            openTagsButton(secondaryEvent);
        });

        this.navigatorListView.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                ObservableList<NavigatorItem> selectedItems =
                    navigatorListView.getSelectionModel().getSelectedItems();
                if (selectedItems.size() > 0 && selectedItems.get(0) != null) {
                    Object selectedItem = selectedItems.get(0).getItem();

                    if (selectedItem != null) {
                        currentlySelected = selectedItem;
                        editMenuItem.setText("Edit " + selectedItem.toString());
                        editMenuItem.setDisable(false);
                    } else {
                        currentlySelected = null;
                        editMenuItem.setDisable(true);
                        editMenuItem.setText("Edit");
                    }

                }
            });

        this.navigatorListView
            .addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    App.getMainController().getContextMenu().hide();
                    if (event.getButton() == MouseButton.SECONDARY) {
                        if (navigatorListView.getSelectionModel().getSelectedItem() != null) {
                            if (navigatorListView.getSelectionModel().getSelectedItem().getItem() != null) {
                                edit.setDisable(false);
                                delete.setDisable(false);
                                openTags.setDisable(false);
                            } else {
                                edit.setDisable(true);
                                delete.setDisable(true);
                                openTags.setDisable(true);
                            }
                            if (navigatorListView.getSelectionModel().getSelectedItem().getItem() instanceof Tag) {
                                openTags.setDisable(true);

                            }
                        }
                        App.getMainController().getContextMenu()
                            .show(navigatorListView, event.getScreenX(), event.getScreenY());
                    }
                }
            });

        this.navigatorListView.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DOWN) {
                    updateInfoPane(navigatorListView.getSelectionModel().getSelectedItem());
                } else if (event.getCode() == KeyCode.UP) {
                    updateInfoPane(navigatorListView.getSelectionModel().getSelectedItem());
                }
            }
        });

    }

    /**
     * adds the menu items for the context menu item 'New'
     *
     * @param newItem - The menu that will contain the new items
     */
    public void addNewMenuItems(Menu newItem) {
        MenuItem person = new MenuItem("Person");
        MenuItem skill = new MenuItem("Skill");
        MenuItem team = new MenuItem("Team");

        MenuItem project = new MenuItem("Project");
        MenuItem release = new MenuItem("Release");
        MenuItem story = new MenuItem("Story");
        MenuItem backlog = new MenuItem("Backlog");
        MenuItem sprint = new MenuItem("Sprint");

        project.setOnAction(secondaryEvent -> {
            menuBarNewProjectButton(secondaryEvent);
        });
        release.setOnAction(secondaryEvent -> {
            menuBarNewReleaseButton(secondaryEvent);
        });
        team.setOnAction(secondaryEvent -> {
            menuBarNewTeamButton(secondaryEvent);
        });
        person.setOnAction(secondaryEvent -> {
            menuBarNewPersonButton(secondaryEvent);
        });
        skill.setOnAction(secondaryEvent -> {
            menuBarNewSkillButton(secondaryEvent);
        });
        backlog.setOnAction(secondaryEvent -> {
            menuBarNewBacklogButton(secondaryEvent);
        });
        story.setOnAction(secondaryEvent -> {
            menuBarNewStoryButton(secondaryEvent);
        });
        sprint.setOnAction(secondaryEvent -> {
            menuBarNewSprintButton(secondaryEvent);
        });

        newItem.getItems().addAll(person, skill, team, project, release, story, backlog, sprint);
    }


    /**
     * sets the text of the info label at the bottom on the window. ie an undo change
     *
     * @param info - String to be printed on the label
     */
    public void printInfo(String info) {
        outputLine.setText(info);
    }


    /**
     * When the 'Organisation' item is selected from the 'File' menu bar drop down and the 'New' sub menu,
     * a property sheet which can be used to create and edit the project will appear
     */
    public void menuBarNewOrganisationButton() {
        Dialogs d = new Dialogs();
        if (currentOrganisation.getUnsaved()) {
            d.SaveNewOrg();
        } else {
            Editor e = new Editor();
            e.NewOrganisation(this);
        }

    }


    /**
     * called by the editor when a new organisation has been made
     *
     * @param organisation - the new organisation to focus the program on
     */
    public void newOrganisation(Organisation organisation) {
        /* Uncomment this to generate some big data when making a new org
        for(int i = 0; i < 5000; i++){
            Person p = new Person("Person " + i, "Person " + i + " full", "p" + i);
            for(int j = 0; j < 5; j++){
                Skill s = new Skill("Skill " + (j + (i * 5)), "Skill desc");
                p.addSkill(s);
                organisation.addSkill(s);
            }
            organisation.addPerson(p);
        }*/

        this.currentOrganisation = organisation;
        this.currentOrganisation
            .newOrganisationNavigator(navigatorListView, breadCrumbToolBar, orderSelectionCombo);


        undoMenuItem.setDisable(false);
        redoMenuItem.setDisable(false);

        addPerson.setDisable(false);

        menuBarSaveAsButton.setDisable(false);

        Skill productOwner = new Skill("PO", "Product Owner");
        Skill scrumMaster = new Skill("SM", "Scrum Master");

        currentOrganisation.addSkill(productOwner);
        currentOrganisation.addSkill(scrumMaster);

    }

    /**
     * When the 'Person' item is selected from the 'File new' menu bar drop down a property sheet which can
     * be used to create and edit the person, and add it to the project will appear
     *
     * @param event - the action event
     */
    public void menuBarNewPersonButton(ActionEvent event) {
        newPerson();
    }
    private void newPerson(){
        if (this.currentOrganisation != null) {
            Editor e = new Editor();
            e.AddPerson(this.currentOrganisation);
            addSkill.setDisable(false);

        } else {
            new Dialogs()
                .infoDialog("No Organisation Opened\nCannot make Person", "No Organisation Opened");

        }
    }

    /**
     * When the 'Release' item is selected from the File new menu bar drop down a proerty sheet which can be used to create
     * and edit the release, and add it to the project will appear. If there is no currently loaded organisation or
     * the organisation has no projects, appropriate dialogue boxes will appear.
     *
     * @param event
     */
    public void menuBarNewReleaseButton(ActionEvent event) {
        newRelease();
    }
    private void newRelease(){
        if ((this.currentOrganisation != null) && !(this.currentOrganisation.getProjects().isEmpty())) {
            Editor e = new Editor();
            e.AddRelease(
                this.currentOrganisation); //calls AddRelease method from Editor with current organisation
            addRelease.setDisable(false);

        } else if (this.currentOrganisation == null) {
            new Dialogs().infoDialog("No Organisation Opened\nCannot make Release",
                "No Organisation Opened");

        } else {
            new Dialogs().infoDialog("No Projects in Organisation\nCannot make Release",
                "No Projects Loaded");

        }
    }

    /**
     * When the 'Skill' item is selected from the 'file new' menu bar drop down a property sheet which can
     * be used to create and edit the Skill, and add it to the project will appear
     *
     * @param actionEvent - the action event
     */

    public void menuBarNewSkillButton(ActionEvent actionEvent) {
        newSkill();
    }
    private void newSkill(){
        if (this.currentOrganisation != null) {
            Editor e = new Editor();
            e.AddSkill(this.currentOrganisation);

        } else {
            new Dialogs()
                .infoDialog("No Organisation Opened\nCannot make Skill", "No Organisation Opened");

        }
    }

    /**
     * When the 'Project' item is selected from the 'file new' menu bar drop down a property sheet which can
     * be used to create and edit the Project, and add it to the project will appear
     *
     * @param actionEvent - the action event
     */
    public void menuBarNewProjectButton(ActionEvent actionEvent) {
        newProject();
    }
    private void newProject(){
        if (this.currentOrganisation != null) {
            Editor e = new Editor();
            e.AddProject(this.currentOrganisation);
            //addSkill.setDisable(false);

        } else {
            new Dialogs().infoDialog("No Organisation Opened\nCannot make Project",
                "No Organisation Opened");

        }
    }


    /**
     * When the 'navigatorCheckItem' item is selected from the 'file new' menu bar drop down a property sheet which can
     * be used to create and edit the navigatorCheckItem, and add it to the project will appear
     *
     * @param actionEvent - the action event
     */
    public void menuBarNewTeamButton(ActionEvent actionEvent) {
        newTeam();
    }
    private void newTeam(){
        if (this.currentOrganisation != null) {
            Editor e = new Editor();
            e.AddTeam(this.currentOrganisation);

        } else {
            new Dialogs()
                .infoDialog("No Organisation Opened\nCannot make Team", "No Organisation Opened");

        }
    }

    /**
     * When the 'Story' item is selected from the 'file new' menu bar drop down a property sheet which can
     * be used to create and edit the Story, and add it to the organisation will appear
     *
     * @param actionEvent - the action event
     */
    public void menuBarNewStoryButton(ActionEvent actionEvent) {
        newStory();

    }
    private void newStory(){
        if (this.currentOrganisation != null)
            new Editor().NewStory(currentOrganisation);
        else
            new Dialogs()
                .infoDialog("No Organisation Opened\nCannot make Story", "No Organisation Opened");
    }

    /**
     * When the 'Backlog' item is selected from the 'file new' menu bar drop down a property sheet which can
     * be used to create and edit the backlog, and add it to the organisation will appear
     *
     * @param actionEvent - the action event
     */
    public void menuBarNewBacklogButton(ActionEvent actionEvent) {
        newBacklog();
    }
    private void newBacklog(){
        if (this.currentOrganisation != null) {
            Editor e = new Editor();
            e.AddBacklog(this.currentOrganisation);

        } else {
            new Dialogs().infoDialog("No Organisation Opened\nCannot make Backlog",
                "No Organisation Opened");

        }
    }

    /**
     * When the 'Revert' item is selected from the 'edit' menu bar drop down a property sheet which can
     * be used to revert all the changes since the last creation/load of the organisation.
     * <p>
     * This particular function calls a dialog box; the dialog box then checks what button is pressed
     * and if the 'Okay' button is pressed, the revert() function in main controller is called.
     *
     * @param event - the ActionEvent
     */
    @FXML public void menuBarRevertButton(ActionEvent event) {
        if (revertableOrganisation != null
            && revertableOrganisation.getOrgId() != currentOrganisation.getOrgId()) {
            new Dialogs().revertChangesDialog();
        } else {
            new Dialogs().infoDialog("Nothing to Revert", "Revert Error");
        }
    }

    /**
     * When the 'Sprint' item is selected from the 'file new' menu bar drop down a property sheet which can
     * be used to create and edit the sprint, and add it to the organisation will appear
     *
     * @param actionEvent - the action event
     */
    public void menuBarNewSprintButton(ActionEvent actionEvent) {
        newSprint();
    }
    private void newSprint(){
        if (this.currentOrganisation != null) {
            Editor e = new Editor();
            e.AddSprint(this.currentOrganisation);

        } else {
            new Dialogs().infoDialog("No Organisation Opened\nCannot make Backlog",
                "No Organisation Opened");

        }
    }

    /**
     * The actions taken to revert the changes made
     */
    public void revert() {
        clearHistory();
        this.revertableOrganisation
            .setOrganisationNavigator(navigatorListView, breadCrumbToolBar, orderSelectionCombo);

        revertableOrganisation.decrementOrgId(); //Decrementing for the serialised copy
        addHistory(revertableOrganisation.serializedCopy(), "Reverted all changes made");
        revertableOrganisation.incrementOrgId(); //Incrementing to bring it back to normal
        getStage().setTitle(currentOrganisation.getShortName());
        currentOrganisation.setUnsaved(false);

    }


    /**
     * When the MenuBar edit delete button is pressed, or the delete hotkey, or delete from the right click object menu,
     * a dialog appears asking if the user would like to delete the targeted object.
     *
     * @param event
     */
    @FXML public void menuBarDeleteButton(ActionEvent event) {
        ObservableList<NavigatorItem> selectedItems =
            navigatorListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() > 0 && selectedItems.get(0) != null) {
            NavigatorItem navItem = navigatorListView.getSelectionModel().getSelectedItem();
            Object selectedItem = navItem.getItem();
            if (selectedItem != null) {
                new Delete(selectedItem, currentOrganisation);
            }

        }
    }

    /**
     * When the MenuBar openTags button is pressed,or add tag from the right click object menu,
     * a dialog appears asking if the user would like to delete the targeted object.
     *
     * @param actionEvent
     */
    @FXML public void openTagsButton(ActionEvent actionEvent) {
        tagMaker();
    }

    public void tagMaker(){
        ObservableList<NavigatorItem> selectedItems =
            navigatorListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() > 0 && selectedItems.get(0) != null) {
            Object selectedItem = selectedItems.get(0).getItem();
            if (selectedItem != null) {
                Editor e = new Editor();
                e.OpenTags(currentOrganisation, selectedItem);
            }
        } else if (currentlySelected != null) {
            Editor e = new Editor();
            e.OpenTags(currentOrganisation, currentlySelected);
        }
    }

    /**
     * When the MenuBar openTags button is pressed,or add tag from the right click object menu,
     * a dialog appears asking if the user would like to delete the targeted object.
     *
     * @param actionEvent
     */
    @FXML public void addTagButton(ActionEvent actionEvent) {
        ObservableList<NavigatorItem> selectedItems =
                navigatorListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() > 0 && selectedItems.get(0) != null) {
            Object selectedItem = selectedItems.get(0).getItem();
            if (selectedItem != null) {
                Editor e = new Editor();
                e.NewTag(currentOrganisation, selectedItem);
            }
        } else if (currentlySelected != null) {
            Editor e = new Editor();
            e.NewTag(currentOrganisation, currentlySelected);
        }
    }


    /**
     * When the menubar edit edit [Object] is pressed, or edit from the right click menu, calls the appropriate edit
     * method for the object.
     *
     * @param actionEvent
     */
    public void menuButtonEdit(ActionEvent actionEvent) {
        edit();
    }

    public void edit(){
        ObservableList<NavigatorItem> selectedItems =
            navigatorListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() > 0 && selectedItems.get(0) != null) {
            Object selectedItem = selectedItems.get(0).getItem();
            if (selectedItem != null) {
                Editor e = new Editor();
                e.edit(this.currentOrganisation, selectedItem);
            }
        } else if (currentlySelected != null) {
            Editor e = new Editor();
            e.edit(this.currentOrganisation, currentlySelected);
        }
    }

    /**
     * When shortcut is used, demo organisation is generated
     */
    public void generateSampleOrgButton(ActionEvent event) {
        clearHistory();
        currentOrganisation = new Organisation("Group 3");
        this.currentOrganisation.newOrganisationNavigator(navigatorListView, breadCrumbToolBar,
            orderSelectionCombo);
        revertableOrganisation = currentOrganisation.serializedCopy();
        App.getMainController()
            .addHistory(currentOrganisation.serializedCopy(), "New Organisation");
        try {
            long l = 0L;
            Skill productOwner = new Skill("PO", "Product Owner");
            Thread.sleep(l,10);
            Skill scrumMaster = new Skill("SM", "Scrum Master");
            Thread.sleep(l,10);
            currentOrganisation.addSkill(productOwner);
            currentOrganisation.addSkill(scrumMaster);


            Person PO = new Person("PO", "ProductOwner", "po302");
            Thread.sleep(l,10);
            PO.addSkill(productOwner);

            Person SM = new Person("SM", "Scrum Master", "sm302");
            Thread.sleep(l,10);
            SM.addSkill(scrumMaster);

            currentOrganisation.addPerson(PO);
            currentOrganisation.addPerson(SM);

            /*Integer i;
            for(i=0;i<100000;i++){
                String s = i.toString();
                Person m = new Person(s, "Scrum Master", "sm302");
                currentOrganisation.addPerson(m);
            }*/


            Project projectOne = new Project("ProjectOne", "1", "1");
            Thread.sleep(l,10);
            Project projectTwo = new Project("ProjectTwo", "2", "2");
            Thread.sleep(l,10);
            Project projectThree = new Project("ProjectThree", "3", "3");
            Thread.sleep(l,10);


            currentOrganisation.addProject(projectOne);
            currentOrganisation.addProject(projectTwo);
            currentOrganisation.addProject(projectThree);

            Backlog backlog1 = new Backlog("Backlog1", "1", PO);
            Thread.sleep(l,10);
            Backlog backlog2 = new Backlog("Backlog2", "2", PO);
            Thread.sleep(l,10);
            Backlog backlog3 = new Backlog("Backlog3", "3", PO);
            Thread.sleep(l,10);

            currentOrganisation.addBacklog(backlog1);
            currentOrganisation.addBacklog(backlog2);
            currentOrganisation.addBacklog(backlog3);

            Person ed = new Person("Edward", "Edward Armstrong", "epa31");
            Thread.sleep(l,10);
            Person chris = new Person("Chris", "Christopher Marffy", "cjm328");
            Thread.sleep(l,10);
            Person andy = new Person("Andrew", "Andrew Dolan", "ajd185");
            Thread.sleep(l,10);
            Person jake = new Person("Jake", "Jake Crouchley", "jwc78");
            Thread.sleep(l,10);
            Person nick = new Person("Nick", "Nick Russell", "ntr24");
            Thread.sleep(l,10);
            Person victor = new Person("Victor", "Victor Chang", "vch51");
            Thread.sleep(l,10);

            currentOrganisation.addPerson(ed);
            currentOrganisation.addPerson(chris);
            currentOrganisation.addPerson(andy);
            currentOrganisation.addPerson(jake);
            currentOrganisation.addPerson(nick);
            currentOrganisation.addPerson(victor);

            Team team1 = new Team("Group 3", "SENG302 Group 3", "Group 3 for the SENG302 group project");
            Thread.sleep(l,10);
            Team team2 = new Team("team2", "2", "2");
            Thread.sleep(l,10);
            Team team3 = new Team("team3", "3", "3");
            Thread.sleep(l,10);

            currentOrganisation.addTeam(team1);
            currentOrganisation.addTeam(team2);
            currentOrganisation.addTeam(team3);

            Release release1 = new Release("Release1","1", LocalDate.of(2015,11,11),projectOne);
            Thread.sleep(l,10);
            Release release2 = new Release("Release2","2", LocalDate.of(2016,11,11),projectTwo);
            Thread.sleep(l,10);
            currentOrganisation.addRelease(release1);
            currentOrganisation.addRelease(release2);

            team2.addPerson(PO);
            PO.setTeam(team2);
            team2.addPerson(SM);
            SM.setTeam(team2);
            team2.setProductOwner(PO);
            team2.setScrumMaster(SM);

            TimePeriod timePeriod = new TimePeriod(LocalDate.now(),LocalDate.now().plusWeeks(1),projectTwo,team2);
            Thread.sleep(l,10);
            team2.addTimePeriod(timePeriod);
            projectTwo.addTimePeriod(timePeriod);

            Sprint sprint = new Sprint("Sprint1","1","1",new TimePeriod(LocalDate.of(2011,10,10),
                LocalDate.of(2018,11,10)),team2,release2,backlog2);
            Thread.sleep(l,10);
            currentOrganisation.addSprint(sprint);

            Story story1 = new Story("Story1", "1", "1",ed);
            Thread.sleep(l,10);
            Story story2 = new Story("Story2", "2", "2",ed);
            Thread.sleep(l,10);
            Story story3 = new Story("Story3", "3", "3",ed);
            Thread.sleep(l,10);

            AcceptanceCriteria ac1 = new AcceptanceCriteria("AC1");
            Thread.sleep(l, 10);
            AcceptanceCriteria ac2 = new AcceptanceCriteria("AC2");
            Thread.sleep(l, 10);
            AcceptanceCriteria ac3 = new AcceptanceCriteria("AC3");
            Thread.sleep(l, 10);

            currentOrganisation.addStory(story1);
            currentOrganisation.addStory(story2);
            currentOrganisation.addStory(story3);

            Task task1= new Task("task1", "1");
            Thread.sleep(l,10);
            Task task2 = new Task("task2", "1");
            Thread.sleep(l,10);
            Task task3 = new Task("task3", "1");
            Thread.sleep(l,10);
            currentOrganisation.addTask(task1);
            currentOrganisation.addTask(task2);
            currentOrganisation.addTask(task3);




            story1.addTask(task1);
            story1.addTask(task2);
            story1.setBacklog(backlog1);
            story1.setEstimate(3);
            story1.setReady(true);
            backlog1.addStory(story1);

            story2.addTask(task3);
            story2.setBacklog(backlog2);
            story2.setEstimate(5);
            story2.setReady(true);
            backlog2.addStory(story2);

            story1.addAcceptanceCriteria(ac1);
            story2.addAcceptanceCriteria(ac2);
            story3.addAcceptanceCriteria(ac3);

            sprint.addStory(story2);


            getStage().setTitle(currentOrganisation.getShortName());

            outputLine.setText("Sample Data Set Created");
            currentOrganisation.setUnsaved(true);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * Getter for context menu
     *
     * @return
     */
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }


    /**
     * clears the history. Used for when a new organisation is created or an organisation is opened
     */
    public void clearHistory() {
        this.history.clear();
        this.historyMessages.clear();
        this.historyPosition = -1;
    }

    /**
     * Updates the main pane with new information, useful for after editing an object.
     */
    public void updateInfo(Node grid) {
        //this.infoLabel.setText(title);
        //this.infoText.setText(info);
        overViewTab.setContent(grid);
    }

    public void initLoad() {
        clearHistory();

        try {
            currentOrganisation = Loader.initialLoad(Saver.getSaveName().toString());
            if(currentOrganisation != null){
                this.currentOrganisation.setOrganisationNavigator(navigatorListView, breadCrumbToolBar,
                    orderSelectionCombo);

                addHistory(currentOrganisation.serializedCopy(),
                    "Opened Organisation '" + currentOrganisation.getShortName() + "'");
                revertableOrganisation = currentOrganisation.serializedCopy();
            }else{
                newOrgFromLoad();
            }

        } catch (Exception e) {
            newOrgFromLoad();
        }

        getStage().setTitle(currentOrganisation.getShortName());
        currentOrganisation.setUnsaved(false);

        initScrumBoard();
        initBurndownChart();
        initAllTasks();
        initStoryWorkspace();
    }

    private void newOrgFromLoad(){
        currentOrganisation = new Organisation("untitled");
        this.currentOrganisation.newOrganisationNavigator(navigatorListView, breadCrumbToolBar,
            orderSelectionCombo);

        App.getMainController()
            .addHistory(currentOrganisation.serializedCopy(), "New Organisation");
        revertableOrganisation = currentOrganisation.serializedCopy();
        Skill productOwner = new Skill("PO", "Product Owner");
        Skill scrumMaster = new Skill("SM", "Scrum Master");

        currentOrganisation.addSkill(productOwner);
        currentOrganisation.addSkill(scrumMaster);
    }

    public void menuBarToolBarFileToggle(ActionEvent actionEvent) {
        toolbarFileActions.setVisible(toolbarFileCheck.isSelected());
        toolbarFileActions.managedProperty().bind(toolbarFileActions.visibleProperty());

        separatorone.setVisible(toolbarFileCheck.isSelected());
        separatorone.managedProperty().bind(separatorone.visibleProperty());
    }

    public void menuBarToolBarEditToggle(ActionEvent actionEvent) {
        toolbarEditor.setVisible(toolbarEditCheck.isSelected());
        toolbarEditor.managedProperty().bind(toolbarEditor.visibleProperty());

        separatortwo.setVisible(toolbarEditCheck.isSelected());
        separatortwo.managedProperty().bind(separatortwo.visibleProperty());
    }

    public void menuBarToolBarAddToggle(ActionEvent actionEvent) {
        toolbarAdditions.setVisible(toolbarAddCheck.isSelected());
        toolbarAdditions.managedProperty().bind(toolbarAdditions.visibleProperty());
    }

    public void storyHighlightsToggle(ActionEvent actionEvent) {
        storyStateVisible = !storyStateVisible;
        int index = navigatorListView.getSelectionModel().getSelectedIndex();
        currentOrganisation.getNavigator().refresh();
        navigatorListView.getSelectionModel().select(index);
    }

    public void menuBarNewTimeLogButton(ActionEvent actionEvent) {
        if(currentlySelected instanceof Task){
            newTimeLogButton((Task) currentlySelected);
        }else{
            new Dialogs().infoDialog("No task selected\nCannot log hours",
                "No Task Selected");
        }
    }
    public void newTimeLogButton(Task task){
        if (this.currentOrganisation != null) {
            Editor e = new Editor();
            if(task != null){
                e.logHoursWithSelection(task, currentOrganisation);
            }
        } else {
            new Dialogs().infoDialog("No Organisation Opened\nCannot log hours",
                    "No Organisation Opened");

        }
    }


    public void addItemFolder(FolderNavigatorItem selection) {
        String l = selection.getName();

        if(l.equals("Projects"))
            newProject();
        else if(l.equals("Releases") )
            newRelease();
        else if(l.equals("Teams"))
            newTeam();
        else if(l.equals("People"))
            newPerson();
        else if(l.equals("Skills"))
            newSkill();
        else if(l.equals("Backlogs"))
            newBacklog();
        else if(l.equals("Stories"))
            newStory();
        else if(l.equals("Sprints"))
            newSprint();
        else if(l.equals("Logged Time"))
            newTimeLogButton(null);

    }
}
