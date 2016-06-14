package seng302.group3.model.io;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import seng302.group3.controller.App;
import seng302.group3.controller.MainController;
import seng302.group3.model.*;
import seng302.group3.model.gui.story_workspace.StoryWorkspace;
import seng302.group3.model.gui.story_workspace.StoryWorkspaceController;
import seng302.group3.model.io.property_sheets.*;
import seng302.group3.model.navigation.NavigatorItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by epa31 on 17/03/15.
 * Editor class to manage editing of project aspects
 */
public class Editor {

    private VBox dialogLayout = new VBox();
    final Stage dialog = new Stage();

    @FXML VBox content;
    @FXML Button AcceptButton;
    @FXML Button CancelButton;

    private Scene dialogScene;


    /**
     * Constructor loads the fxml for the popup editor base layout which includes window title, ok and cancel buttons
     */
    public Editor() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OkayCancelDialog.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            dialogLayout.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Provides a clean method to Edit an object and performs checks to find the selected type
     *
     * @param currentOrganisation - The currently used organisation object
     * @param selectedItem        - the object wanting to be edited
     */
    public void edit(Organisation currentOrganisation, Object selectedItem) {
        if (selectedItem instanceof Project) {
            EditProject(currentOrganisation, (Project) selectedItem);
        } else if (selectedItem instanceof Release) {
            EditRelease(currentOrganisation, (Release) selectedItem);
        } else if (selectedItem instanceof Team) {
            EditTeam(currentOrganisation, (Team) selectedItem);
        } else if (selectedItem instanceof Person) {
            EditPerson(currentOrganisation, (Person) selectedItem);
        } else if (selectedItem instanceof Skill) {
            EditSkill(currentOrganisation, (Skill) selectedItem);
        } else if (selectedItem instanceof Story) {
            EditStory(currentOrganisation, (Story) selectedItem);
        } else if (selectedItem instanceof Backlog) {
            EditBacklog(currentOrganisation, (Backlog) selectedItem);
        } else if (selectedItem instanceof Sprint) {
            EditSprint(currentOrganisation, (Sprint) selectedItem);
        } else if (selectedItem instanceof Task) {
            EditTask(currentOrganisation, (Task) selectedItem);
        } else if (selectedItem instanceof LoggedTime){
            EditLoggedTime(currentOrganisation, (LoggedTime) selectedItem);
        } else if (selectedItem instanceof Tag)
            EditTag(currentOrganisation, (Tag) selectedItem);
        //TODO: decide how editing tags will work

    }



    /**
     * createPopup where the addListener is defaulted to true. Therefore if we call this function without the addListener
     * param then by default the listeners will be added
     * @param sheet - sheet to populate the window with
     */
    private void createPopup(EditorSheet sheet){
        createPopup(sheet, true);
    }

    /**
     * creates the popup window and populates a given sheet into the popup content.
     *
     * @param sheet - sheet to populate the window with
     * @param addListener - true will set the okay button to have a listener on enter and escape for cancel
     */
    private void createPopup(EditorSheet sheet, boolean addListener) {
        dialog.initModality(Modality.APPLICATION_MODAL);

        content.getChildren().add(sheet.draw());

        dialogScene = new Scene(dialogLayout);

        // give the choice to enable the button listeners.
        if(addListener){
            dialogScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
                @Override public void handle(KeyEvent event) {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        AcceptButton.fire();
                    } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                        CancelButton.fire();
                    }
                }
            });
        }

        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }


    /**
     * Creates a popup for when making a new project
     *
     * @param controller - reference to the main controller used for refreshing the view after the project has been made
     */

    public void NewOrganisation(MainController controller) {

        Organisation organisation = new Organisation("untitled");

        controller.newOrganisation(organisation);
        controller.clearHistory();

        App.getMainController().addHistory(organisation.serializedCopy(), "New Organisation");

        // Creating a revert point
        App.getMainController().revertableOrganisation = organisation;

        App.getMainController().getStage()
            .setTitle(organisation.getShortName());//Set title of window

        organisation.decrementOrgId(); //Decrement for the serialized copy
        App.getMainController().addHistory(organisation.serializedCopy(), "New Organisation");
        organisation.incrementOrgId(); //Increment to bring it back to normal again

        // Creating a revert point
        App.getMainController().revertableOrganisation = organisation;
        App.getMainController().getStage().setTitle(organisation.getShortName());
        App.getMainController().currentOrganisation.setUnsaved(false);

    }


    /**
     * creates a popup for adding a new person to an organisation
     *
     * @param organisation - organisation to which the new person will be added
     */
    public void AddPerson(Organisation organisation) {
        PersonSheet sheet = new PersonSheet();
        dialog.setTitle("New Person");

        sheet.skillsConstruct(organisation);




        AcceptButton.setOnAction(event -> {
            if (sheet.validate(organisation)) {
                Person person = new Person();


                sheet.apply(person);
                organisation.addPerson(person);
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Added person '" + person.getShortName() + "'");
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController().updateInfo(person.getOverviewPaneGrid());


                dialog.close();
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    /**
     * creates a popup window for editing the currently selected person
     *
     * @param organisation - organisation which the person belongs to
     * @param person       - person which is being edited
     */
    public void EditPerson(Organisation organisation, Person person) {
        PersonSheet sheet = new PersonSheet();
        dialog.setTitle("Edit Person");

        sheet.fieldConstruct(person);
        sheet.skillsConstruct(organisation);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(organisation, person)) {
                sheet.apply(person);
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Edited person '" + person.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController().updateInfo(person.getOverviewPaneGrid());
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        this.createPopup(sheet);
    }

    /**
     * creates a popup for adding a new Project to an organisation
     *
     * @param organisation - organisation to which the new Project will be added
     */
    public void AddProject(Organisation organisation) {
        ProjectSheet sheet = new ProjectSheet();
        dialog.setTitle("New Project");

        sheet.teamsConstruct(organisation);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(organisation)) {
                Project project = new Project();
                //if (organisation.getCurrentChange() != null && organisation.getCurrentChange().getType() != Change.changeType.EDIT_PROJECT) {
                //    organisation.addChange(new Change(Change.changeType.EDIT_PROJECT, organisation, project));
                //}

                sheet.apply(project);
                organisation.addProject(project);

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Added project '" + project.getShortName() + "'");
                //organisation.addChange(new Change(Change.changeType.EDIT_PROJECT, organisation, project));
                //App.getMainController().updateInfo(project.getOverviewPaneGrid());
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());

                dialog.close();
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    /**
     * creates a popup window for editing the currently selected person
     *
     * @param organisation - organisation which the person belongs to
     * @param project      - person which is being edited
     */
    public void EditProject(Organisation organisation, Project project) {
        ProjectSheet sheet = new ProjectSheet();
        dialog.setTitle("Edit Project");
        sheet.fieldConstruct(project);
        sheet.teamsConstruct(organisation);
        AcceptButton.setOnAction(event -> {
            if (sheet.validate(organisation, project)) {
                sheet.apply(project);

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Edited project '" + project.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController().updateInfo(project.getOverviewPaneGrid());
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        this.createPopup(sheet);
    }

    /**
     * creates a popup for adding a new Team to an organisation
     *
     * @param organisation - organisation to which the new Team will be added
     */
    public void AddTeam(Organisation organisation) {
        TeamSheet sheet = new TeamSheet();
        dialog.setTitle("New Team");


        sheet.peopleConstruct(organisation);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(organisation)) {
                Team team = new Team();

                sheet.apply(team);
                organisation.addTeam(team);

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Added Team '" + team.getShortName() + "'");
                //App.getMainController().updateInfo(team.getOverviewPaneGrid());
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());

                dialog.close();
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    /**
     * creates a popup window for editing the currently selected Team
     *
     * @param currentOrganisation - organisation which the person belongs to
     * @param team                - Team which is being edited
     */
    public void EditTeam(Organisation currentOrganisation, Team team) {
        TeamSheet sheet = new TeamSheet();
        dialog.setTitle("Edit Team");

        sheet.fieldConstruct(team);
        sheet.peopleConstruct(currentOrganisation);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation, team)) {
                sheet.apply(team);

                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                    "Edited Team '" + team.getShortName() + "'");

                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController().updateInfo(team.getOverviewPaneGrid());
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    /**
     * creates a popup for adding a new Skill to an organisation
     *
     * @param organisation - organisation to which the new Skill will be added
     */
    public void AddSkill(Organisation organisation) {
        SkillSheet sheet = new SkillSheet();
        dialog.setTitle("New Skill");

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(organisation)) {
                Skill skill = new Skill();

                sheet.apply(skill);

                organisation.addSkill(skill);
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Added Skill '" + skill.getShortName() + "'");
                //App.getMainController().updateInfo(skill.getOverviewPaneGrid());
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }



    public void OpenTags(Organisation currentOrganisation, Object object) {
        TagSheet sheet = new TagSheet(object);
        sheet.setElementToEdit((Element) object);
        dialog.setTitle("Tags for " + sheet.getElementToEdit().getShortName());

        sheet.fieldConstruct(object);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation)) {
                sheet.apply(sheet.getElementToEdit());


                    // we only want the history thing to say that we've added tags if we actually have
                    App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                            "Changed tags of '" + sheet.getElementToEdit().getShortName() + "'");

                dialog.close();
            }
        });

        CancelButton.setOnAction(event -> {
            dialog.close();

        });

        this.createPopup(sheet);


    }

    /**
     * creates a dialog for adding a tag to an element. Should pop up when "add tag" button is clicked.
     * todo
     * @param currentOrganisation
     * @param object - the element that we are adding a tag to
     */
    public void NewTag(Organisation currentOrganisation, Object object) {
        AddTagSheet sheet = new AddTagSheet();
        dialog.setTitle("Add Tag");
        Tag createdTag = new Tag();
        Element element = (Element) object;

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation)) {
                if (element.getTags().size() <= 7) {
                    sheet.apply(createdTag); // create the tag
                    currentOrganisation.addTag(createdTag); //also add it to the organisation
                    App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                            ("Created Tag " + createdTag.getShortName()));
                } else {
                    new Dialogs().infoDialog("Elements cannot have more than 7 tags",
                            "Tag limit reached");
                }
                dialog.close();
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    /**
     * creates a dialog for adding a tag to an element. Should pop up when "add tag" button is clicked.
     * todo
     * @param currentOrganisation
     * @param object - the element that we are adding a tag to
     */
    public void NewTagFromTagSheet(Tag createdTag,Organisation currentOrganisation, Object object) {
        AddTagSheet sheet = new AddTagSheet();
        dialog.setTitle("Add Tag");
        Element element = (Element) object;

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation)) {
                if (element.getTags().size() <= 7) {
                    sheet.apply(createdTag); // create the tag
                    currentOrganisation.addTag(createdTag); //also add it to the organisation
                    App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                        ("Created Tag " + createdTag.getShortName()));
                } else {
                    new Dialogs().infoDialog("Elements cannot have more than 7 tags",
                        "Tag limit reached");
                }
                dialog.close();
            }
        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    public void EditTag(Organisation currentOrganisation, Tag selectedTag) {

        AddTagSheet sheet = new AddTagSheet();
        dialog.setTitle("Edit Tag");

        sheet.fieldConstruct(selectedTag);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation, selectedTag)) {

                sheet.apply(selectedTag);

                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                        "Edited Tag '" + selectedTag.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());

            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }




    /**
     * creates a popup window for editing the currently selected Skill
     *
     * @param currentOrganisation Organisation which the person belongs to
     * @param selectedItem        Skill which is being edited
     */
    public void EditSkill(Organisation currentOrganisation, Skill selectedItem) {
        SkillSheet sheet = new SkillSheet();
        dialog.setTitle("Edit Skill");

        sheet.fieldConstruct(selectedItem);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation, selectedItem)) {

                sheet.apply(selectedItem);

                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                    "Edited skill '" + selectedItem.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController().updateInfo(selectedItem.getOverviewPaneGrid());
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    /**
     * Generates a sheet to add a release to a given organisation
     *
     * @param organisation The organisation being added to
     */
    public void AddRelease(Organisation organisation) {
        if(organisation.getProjects().size() > 0){
            ReleaseSheet sheet = new ReleaseSheet();
            dialog.setTitle("New Release");

            sheet.projectConstruct(organisation);

            AcceptButton.setOnAction((event -> {
                if (sheet.validate(organisation)) {
                    Release release = new Release();


                    sheet.apply(release);
                    organisation.addRelease(release);
                    App.getMainController().addHistory(organisation.serializedCopy(),
                        "Added release '" + release.getShortName() + "'");
                    //App.getMainController().updateInfo(release.getOverviewPaneGrid());
                    App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());

                    dialog.close();
                }
            }));

            CancelButton.setOnAction(event -> {
                dialog.close();
            });
            this.createPopup(sheet);
        }else{
            Dialogs d = new Dialogs();
            String objects = "Project";
            d.noRequiredObjectsDialog(objects);
        }
    }


    /**
     * creates a popup window for editing the currently selected Release
     *
     * @param currentOrganisation - organisation which the person belongs to
     * @param toEdit              - Releases which is being edited
     */
    public void EditRelease(Organisation currentOrganisation, Release toEdit) {
        ReleaseSheet sheet = new ReleaseSheet();
        dialog.setTitle("Edit Release");

        sheet.fieldConstruct(toEdit);
        sheet.projectConstruct(currentOrganisation);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation, toEdit)) {

                sheet.apply(toEdit);
                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                    "Edited release '" + toEdit.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController().updateInfo(toEdit.getOverviewPaneGrid());
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }


    /**
     * creates a popup for adding a new story to an organisation
     *
     * @param organisation - organisation to which the new Story will be added
     */
    public void NewStory(Organisation organisation) {
        if(organisation.getBacklogs().size() > 0){
            StorySheet sheet = new StorySheet(organisation);
            dialog.setTitle("New Story");

            AcceptButton.setOnAction(event -> {
                if (sheet.validate()) {
                    Story story = new Story();

                    sheet.apply(story);

                    organisation.addStory(story);
                    App.getMainController().addHistory(organisation.serializedCopy(),
                        "Added Story '" + story.getShortName() + "'");


                    dialog.close();
                    App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                    //App.getMainController().updateInfo(story.getOverviewPaneGrid());
                }

            });

            CancelButton.setOnAction(event -> {
                if(sheet.hasChangedOnAdd()){
                    Dialogs D = new Dialogs();
                    boolean accepted = D.dialogChangesMadeDialog();
                    if(accepted){
                        dialog.close();
                    }
                }else {
                    dialog.close();
                }
            });
            this.createPopup(sheet);
        }else {
            Dialogs d = new Dialogs();
            String objects = "Backlogs ";
            d.noRequiredObjectsDialog(objects);
        }

    }

    /**
     * creates a popup window for editing the currently selected Story
     *
     * @param currentOrganisation Organisation which the story belongs to
     * @param selectedItem        Story which is being edited
     */
    public void EditStory(Organisation currentOrganisation, Story selectedItem) {
        StorySheet sheet = new StorySheet(currentOrganisation);
        dialog.setTitle("Edit Story");

        sheet.fieldConstruct(selectedItem);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                sheet.apply(selectedItem);
                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                    "Edited Story '" + selectedItem.getShortName() + "'");
                dialog.close();
                //App.getMainController()
                //    .updateInfo(selectedItem.getOverviewPaneGrid());
                    StoryWorkspace.getInstance().refresh();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
            }

        });

        CancelButton.setOnAction(event -> {
            if(sheet.hasChangedOnEdit()){
                Dialogs D = new Dialogs();
                boolean accepted = D.dialogChangesMadeDialog();
                if(accepted){
                    dialog.close();
                }
            }else {
                dialog.close();
            }
        });
        this.createPopup(sheet);
    }

    /**
     * creates a popup window for replicating the currently selected Story
     *
     * @param currentOrganisation Organisation which the story belongs to
     * @param selectedItem        Story which is being replicated
     */
    public void ReplicateStory(Organisation currentOrganisation, Story selectedItem) {
        StorySheet sheet = new StorySheet(currentOrganisation);
        dialog.setTitle("Replicate Story");

        sheet.fieldConstruct(selectedItem);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                sheet.apply(selectedItem);
                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                    "Replicated Story '" + selectedItem.getShortName());
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController()
                //    .updateInfo(selectedItem.getOverviewPaneGrid());
                StoryWorkspace.getInstance().refresh();
            }

        });

        CancelButton.setOnAction(event -> {
            if(sheet.hasChangedOnEdit()){
                Dialogs D = new Dialogs();
                boolean accepted = D.dialogChangesMadeDialog();
                if(accepted){
                    dialog.close();
                }
            }else {
                dialog.close();
            }
        });
        this.createPopup(sheet);
    }

    /**
     * creates a popup window for editing the currently selected Story
     *
     * @param currentOrganisation Organisation which the story belongs to
     * @param selectedItem        Story which is being edited
     */
    public void EditStoryFromBacklog(Organisation currentOrganisation, Story selectedItem, BacklogSheet bSheet) {
        StorySheet sheet = new StorySheet(currentOrganisation);
        dialog.setTitle("Edit Story");


        sheet.setCalledFromBacklog(new Backlog(bSheet.shortNameTextField.getText()), bSheet);
        sheet.fieldConstruct(selectedItem);
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                sheet.apply(selectedItem);
                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                        "Edited Story '" + selectedItem.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController()
                //        .updateInfo(selectedItem.getOverviewPaneGrid());
            }

        });

        CancelButton.setOnAction(event -> {
            if(sheet.hasChangedOnEdit()){
                Dialogs D = new Dialogs();
                boolean accepted = D.dialogChangesMadeDialog();
                if(accepted){
                    dialog.close();
                }
            }else {
                dialog.close();
            }
        });
        this.createPopup(sheet);
    }


    /**
     * This method is called from the backlog dialog when adding a new story to the backlog
     *
     * @param newStory      - the new story that has been made for the backlog
     * @param additionCheck - the new stories added to the backlog arent within the organisation
     */
    public void CreateStoryFromBacklog(Story newStory, Collection<Story> additionCheck,BacklogSheet bSheet) {
        StorySheet sheet = new StorySheet(App.getMainController().currentOrganisation);
        dialog.setTitle("Create Story");

        sheet.fieldConstruct(newStory);
        sheet.setCalledFromBacklog(new Backlog(bSheet.shortNameTextField.getText()), bSheet);

        sheet.setAdditionalUniqueCheck(additionCheck);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                sheet.apply(newStory);
                //App.getMainController().currentOrganisation.addStory(newStory);
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            // this is so we know that this story was not added
            if(sheet.hasChangedOnAdd()){
                Dialogs D = new Dialogs();
                boolean accepted = D.dialogChangesMadeDialog();
                if(accepted){
                    newStory.setShortName(null);
                    dialog.close();
                }
            }else {
                newStory.setShortName(null);
                dialog.close();
            }
        });
        this.createPopup(sheet);
    }


    /**
     * This method is called from the story dialog when adding a new AC to the backlog
     *
     * @param newAC      - the new AC that has been made for the story
     */
    public boolean CreateACFromStory(AcceptanceCriteria newAC) {
        AcceptanceCriteriaSheet sheet = new AcceptanceCriteriaSheet(App.getMainController().currentOrganisation);
        dialog.setTitle("Create Acceptance Criterion");
        final boolean[] outputArray = {false};
        sheet.fieldConstruct(newAC);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                sheet.apply(newAC);

                outputArray[0] = true;

                dialog.close();

            }

        });

        CancelButton.setOnAction(event -> {
            // this is so we know that this story was not added
            dialog.close();

        });

        // we only want the escape key not the enter key to trigger the buttons
        CancelButton.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ESCAPE)){
                    CancelButton.fire();
                }
            }
        });

        this.createPopup(sheet, false);

        return outputArray[0];
    }

    /**
     /* This method is called from the story dialog when adding a new Dependency to the story
     */
    public void CreateDependencyFromStory(Story baseStory, StorySheet storySheet) {
        DependencySheet sheet = new DependencySheet(App.getMainController().currentOrganisation, storySheet, baseStory);
        dialog.setTitle("Select dependency");

        sheet.fieldConstruct(baseStory);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                sheet.apply(baseStory);

                dialog.close();
            }

        });

        CancelButton.setOnAction(event -> {
            // this is so we know that this story was not added
            //newAC.setShortName(null);
            dialog.close();

        });
        this.createPopup(sheet);
    }

    /**
     /* This method is called from the story dialog when adding a new Dependency to the story, when the story was made from a backlog
     */
    public void CreateDependencyFromStoryFromBacklog(Story baseStory, StorySheet storySheet, BacklogSheet backSheet) {
        DependencySheet sheet = new DependencySheet(App.getMainController().currentOrganisation, storySheet, baseStory);
        dialog.setTitle("Select dependency");
        sheet.setFromBackLog(backSheet);
        sheet.fieldConstruct(baseStory);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                sheet.apply(null);

                dialog.close();
            }

        });

        CancelButton.setOnAction(event -> {
            // this is so we know that this story was not added
            //newAC.setShortName(null);
            dialog.close();

        });
        this.createPopup(sheet);
    }

    /**
     * This is for creating a TimePeriod within a project dialog. Contains two date pickers
     *
     * @param newTimePeriod - The timeperiod which will have the dates applied to it
     * @param timePeriods   - The list of all timePeriods for the project so dates can be formatted
     * @param currentTeam   - The team which the timeperiod will connect to the project
     */
    public void CreateTimePeriodProject(TimePeriod newTimePeriod,
        Collection<TimePeriod> timePeriods, Team currentTeam, Boolean editing) {
        DatePickerSheet sheet = new DatePickerSheet();
        dialog.setTitle("Pick Start and End Date");

        sheet.setupTimePeriods(timePeriods, currentTeam);
        sheet.fieldConstruct(editing);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                sheet.apply(newTimePeriod);
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            newTimePeriod.setStartDate(null);
            dialog.close();
        });
        this.createPopup(sheet);

    }

    /**
     * Creates the datepicker sheet for the Sprint dialog and uses a timeperiod to hold the start
     * and end dates for the period.
     * @param timePeriod
     */
    public void SetDatesForSprint(TimePeriod timePeriod) {

        DatePickerSheet sheet = new DatePickerSheet();
        sheet.setupTimePeriod(timePeriod);
        dialog.setTitle("Pick Start and End Date");

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                sheet.apply(timePeriod);
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            timePeriod.setShortName(null);
            dialog.close();
        });
        this.createPopup(sheet);

    }

    /**
     * A dialog for adding a sprint.
     * @param organisation
     */
    public void AddSprint(Organisation organisation) {
        boolean result = false;
        for(Release r:organisation.getReleases()){
            if(r.getProject().getTeams().size() > 0)
                result = true;
        }
        if(result && organisation.getBacklogs().size() > 0){
            SprintSheet sheet = new SprintSheet(organisation);
            dialog.setTitle("New Sprint");

            sheet.fieldConstruct(organisation);

            // If the user hits the accept button...
            AcceptButton.setOnAction((event-> {

                // If sheet is validated
                if (sheet.validate(organisation)) {

                    // Make a new sprint
                    Sprint sprint = new Sprint();

                    // Apply properties
                    sheet.apply(sprint);
                    organisation.addSprint(sprint);

                    // Set up history elements and send message to status bar
                    App.getMainController().addHistory(organisation.serializedCopy(),
                        "Added sprint " + sprint.getShortName());

                    // Display on the main pane
                    //App.getMainController().updateInfo(sprint.getOverviewPaneGrid());
                    App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());

                    // Close the dialog
                    dialog.close();
                }

            }));
            // FIX -> Make the cancel button work for new sprint window - ajd185
            CancelButton.setOnAction(event -> dialog.close());

            this.createPopup(sheet);
        }else {
            Dialogs d = new Dialogs();
            String objects = "";
            if(organisation.getBacklogs().size() < 1)
                objects += "Backlog, ";
            if(!result)
                objects += "Teams in a Project associated with a release ";
            d.noRequiredObjectsDialog(objects);
        }

    }

    /**
     * A dialog for editing a sprint.
     * @param currentOrganisation
     * @param toEdit
     */
    public void EditSprint(Organisation currentOrganisation, Sprint toEdit) {
        SprintSheet sheet = new SprintSheet(currentOrganisation);
        dialog.setTitle("Edit Sprint");

        sheet.fieldConstruct(toEdit);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation, toEdit)) {

                sheet.apply(toEdit);
                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                        "Edited Sprint " + toEdit.getShortName());
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                //App.getMainController().updateInfo(toEdit.getOverviewPaneGrid());
            }

        });

        CancelButton.setOnAction(event -> dialog.close());

        this.createPopup(sheet);
    }

    /**
     * Generates a sheet to add a backlog to a given organisation=
     *
     * @param organisation
     */
    public void AddBacklog(Organisation organisation) {
        boolean result = false;
        for(Team t:organisation.getTeams()){
            if(t.getProductOwner() != null)
                result = true;
        }
        if(result){
            BacklogSheet sheet = new BacklogSheet(organisation); // storiesConstruct is inside here
            dialog.setTitle("New backlog");

            AcceptButton.setOnAction((event -> {
                if (sheet.validate(organisation)) {
                    Backlog backlog = new Backlog();

                    sheet.apply(backlog);
                    organisation.addBacklog(backlog);
                    App.getMainController().addHistory(organisation.serializedCopy(),
                        "Added backlog " + backlog.getShortName());
                    //App.getMainController().updateInfo(backlog.getOverviewPaneGrid());

                    StoryWorkspace.getInstance().getController().refreshBacklogSelection(backlog);
                    App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
                    dialog.close();
                }
            }));

            CancelButton.setOnAction(event -> {
                if(sheet.hasChangedOnAdd()){
                    Dialogs D = new Dialogs();
                    boolean accepted = D.dialogChangesMadeDialog();
                    if(accepted){
                        dialog.close();
                    }
                }else {
                    dialog.close();
                }
            });
            this.createPopup(sheet);
        }else{
            Dialogs d = new Dialogs();
            String objects = "Product Owners";
            d.noRequiredObjectsDialog(objects);
        }


    }



    /**
     * creates a popup window for editing the currently selected Release
     *
     * @param currentOrganisation - organisation which the person belongs to
     * @param toEdit              - Releases which is being edited
     */
    public void EditBacklog(Organisation currentOrganisation, Backlog toEdit) {
        BacklogSheet sheet = new BacklogSheet(currentOrganisation);
        dialog.setTitle("Edit backlog");

        sheet.fieldConstruct(toEdit);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation, toEdit)) {

                sheet.apply(toEdit);
                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                    "Edited backlog " + toEdit.getShortName());
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());

                StoryWorkspace.getInstance().getController().refreshBacklogSelection(toEdit);
            }

        });

        CancelButton.setOnAction(event -> {
            if(sheet.hasChangedOnEdit()){
                Dialogs D = new Dialogs();
                boolean accepted = D.dialogChangesMadeDialog();
                if(accepted){
                    dialog.close();
                }
            }else {
                dialog.close();
            }

        });
        this.createPopup(sheet);
    }


    /**
     * Creates a dialog for generating an xml report for user defined objects.
     */
    public void GenerateReport(Organisation currentOrganisation) {
        ReportSheet sheet = new ReportSheet();
        dialog.setTitle("Generate XML report");

        sheet.listConstruct(currentOrganisation);

        AcceptButton.setOnAction((event -> {
            if (sheet.validate(currentOrganisation)) {

                sheet.apply(currentOrganisation);

                dialog.close();
            }
        }));

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);

    }

    /**
     * Creates a task dialog which will be made from a storysheet.
     * @param task
     * @param organisation
     * @return
     */
    public boolean CreateTask(Task task , Organisation organisation) {
        TaskSheet sheet = new TaskSheet();
        dialog.setTitle("Create Task");
        final boolean[] outputArray = {false};
        sheet.fieldConstruct(task);
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                sheet.apply(task);
                organisation.addTask(task);
                outputArray[0] = true;
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
        return outputArray[0];
    }

    public void EditTask(Organisation currentOrganisation, Task selectedItem) {
        TaskSheet sheet = new TaskSheet();
        dialog.setTitle("Edit Task");

        sheet.fieldConstruct(selectedItem);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate(currentOrganisation, selectedItem)) {

                sheet.apply(selectedItem);

                App.getMainController().addHistory(currentOrganisation.serializedCopy(),
                        "Edited Task '" + selectedItem.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfoPane(App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }


    /**
     * If when logging hours we have a task selection use this dialog initialisation which makes the log dialog.
     * @param currentlySelected
     */
    public void logHoursWithSelection(Task currentlySelected, Organisation organisation) {
        CancelButton.setVisible(false);
        CancelButton.managedProperty().bind(CancelButton.visibleProperty());

        if(currentlySelected.getObjectType() instanceof Story){
            logHoursStory(currentlySelected,organisation);
        }else{
            logHoursSprint(currentlySelected,organisation);
        }
        App.getMainController().updateInfoPane(
            App.getMainController().navigatorListView.getSelectionModel().getSelectedItem());

    }

    private void logHoursSprint(Task currentlySelected, Organisation organisation){
        TimeLogSheet sheet = new TimeLogSheet(currentlySelected, organisation);
        dialog.setTitle("Add Time to Task: " + currentlySelected.getShortName()
            + ", from Sprint: " + currentlySelected.getObjectType().getShortName());
        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {
                sheet.apply(currentlySelected);
                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Added hours to Task '" + currentlySelected.getShortName() + "'");
                dialog.close();
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    private void logHoursStory(Task currentlySelected, Organisation organisation){
        boolean result = false;
        for(Story s:organisation.getCurrentStories()) {
            if (s.getTasks().contains(currentlySelected)) {
                result = true;
            }
        }
        if(result){
            TimeLogSheet sheet = new TimeLogSheet(currentlySelected, organisation);
            dialog.setTitle("Add Time to Task: " + currentlySelected.getShortName()
                + ", from Story: " + currentlySelected.getObjectType().getShortName());
            AcceptButton.setOnAction(event -> {
                if (sheet.validate()) {
                    sheet.apply(currentlySelected);
                    App.getMainController().addHistory(organisation.serializedCopy(),
                        "Added hours to Task '" + currentlySelected.getShortName() + "'");
                    dialog.close();
                }
            });

            dialog.setOnCloseRequest(event -> {
                if (sheet.validate()) {
                    sheet.apply(currentlySelected);
                    App.getMainController().addHistory(organisation.serializedCopy(),
                        "Added hours to Task '" + currentlySelected.getShortName() + "'");
                    dialog.close();
                }
                event.consume();
            });
            //CancelButton.setOnAction(event -> {
            //    dialog.close();
            //});

            this.createPopup(sheet);
        }else {
            Dialogs d = new Dialogs();
            String objects = "The story which this task is part of has no current sprint";
            d.noRequiredObjectsDialog(objects);
        }
    }

    public void assignPeopleToTask(Task task, Sprint sprint){
        PeopleToTaskAllocationSheet sheet = new PeopleToTaskAllocationSheet(task, sprint);
        dialog.setTitle("Allocate to " + task.getShortName());

        sheet.fieldConstruct(null);

        AcceptButton.setOnAction(event -> {
            if(sheet.validate()){
                sheet.apply(task);
                dialog.close();
                App.getMainController().addHistory(App.getMainController().currentOrganisation.serializedCopy(),
                        "Edited people assigned to '" + task.getShortName() + "'");
            }
        });
        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);
    }

    /**
     * If when logging hours we have a task selection use this dialog initialisation which makes the log dialog.
     * @param currentlySelected
     */
    public void EditLoggedTime(Organisation organisation,LoggedTime currentlySelected) {
        for(Task t:organisation.getTasks()){
            if(t.getLoggedHours().contains(currentlySelected)){
                logHoursWithSelection(t,organisation);
            }
        }
        /*TimeLogSheet sheet = new TimeLogSheet(organisation);
        dialog.setTitle("Edit Logged Time");

        sheet.fieldConstruct(currentlySelected);

        AcceptButton.setOnAction(event -> {
            if (sheet.validate()) {

                sheet.apply(new int[0]);

                App.getMainController().addHistory(organisation.serializedCopy(),
                    "Edited Logged Time '" + currentlySelected.getShortName() + "'");
                dialog.close();
                App.getMainController().updateInfo(currentlySelected.getShortName(),
                    currentlySelected.getPaneString());
            }

        });

        CancelButton.setOnAction(event -> {
            dialog.close();
        });
        this.createPopup(sheet);*/
    }
}


