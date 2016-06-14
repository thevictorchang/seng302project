package seng302.group3.model.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seng302.group3.controller.App;
import seng302.group3.model.*;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.*;
import seng302.group3.model.search.SearchDialog;
import seng302.group3.model.search.SearchResult;

/**
 * Created by cjm328 on 19/09/15.
 */
public class SearchResultListCell<T> extends ListCell<T> {
    Image chevronDown = new Image(getClass().getResourceAsStream("/imgs/chevron_down.png"));
    Image chevronRight = new Image(getClass().getResourceAsStream("/imgs/chevron_right.png"));
    ListView listView;
    Stage dialog;
    SearchDialog searchDialog;

    public SearchResultListCell(Stage dialog, ListView listView, SearchDialog searchDialog) {
        this.dialog = dialog;
        this.listView = listView;
        this.searchDialog = searchDialog;
    }

    /**
     * calls the Drag list cell create factory and gives the cell factory to the list view
     *
     * @param listView - list view we want to add the factory to
     * @param dialog
     */
    public static void createFactory(ListView listView, Stage dialog, SearchDialog searchDialog) {
        listView.setCellFactory(param ->
                        new SearchResultListCell(dialog, listView, searchDialog)
        );
    }

    /**
     * The formatter for the cells in a story. Builds the cells dropdown, text and highlighting.
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        if (empty || item == null) {
            setGraphic(null);
            super.updateItem(item, empty);
        } else if (item instanceof SearchResult) {

            SearchResult searchResult = (SearchResult) item;
            Element elem = searchResult.getElement();
            Organisation org = App.getMainController().currentOrganisation;


            setText(item.toString());
            setGraphic(null);
            super.updateItem(item, false);


            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        if (searchResult.getElement() != null) {

                            NavigatorItem navigatorItem = null;
                            org.getNavigator().homePress();
                            if (elem instanceof Person) {
                                navigatorItem = new PersonNavigatorItem((Person) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("People");
                                foldItem.setNext(org.getPeople());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Team) {
                                navigatorItem = new TeamNavigatorItem((Team) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Teams");
                                foldItem.setNext(org.getTeams());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Project) {
                                navigatorItem = new ProjectNavigatorItem((Project) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Projects");
                                foldItem.setNext(org.getProjects());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Skill) {
                                navigatorItem = new SkillNavigatorItem((Skill) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Skills");
                                foldItem.setNext(org.getSkills());
                                org.getNavigator().updateToSelection(foldItem);
                            } else if (elem instanceof Story) {
                                navigatorItem = new StoryNavigatorItem((Story) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Stories");
                                foldItem.setNext(org.getStories());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Tag) {
                                navigatorItem = new TagNavigatorItem((Tag) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Tags");
                                foldItem.setNext(org.getTags());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Backlog) {
                                navigatorItem = new BacklogNavigatorItem((Backlog) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Backlogs");
                                foldItem.setNext(org.getBacklogs());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Sprint) {
                                navigatorItem = new SprintNavigatorItem((Sprint) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Sprints");
                                foldItem.setNext(org.getSprints());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Release) {
                                navigatorItem = new ReleaseNavigatorItem((Release) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Releases");
                                foldItem.setNext(org.getReleases());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            } else if (elem instanceof Task) {
                                navigatorItem = new TaskNavigatorItem((Task) elem);
                                FolderNavigatorItem foldItem = new FolderNavigatorItem("Tasks");
                                foldItem.setNext(org.getTasks());
                                org.getNavigator().updateToSelection(foldItem);
                                org.getNavigator().updateToSelection(navigatorItem);
                            }
                            dialog.close();
                        }
                    }
                }
            });
        } else {
            final ImageView chev = new ImageView(chevronRight);
            HBox labelBox = new HBox();
            Label label = new Label(item.toString());
            label.setStyle("-fx-font-weight: bolder");

            String s = item.toString();
            if (s.equals("Backlogs")) {
                if (searchDialog.backlogExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("People")) {
                if (searchDialog.peopleExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Projects")) {
                if (searchDialog.projectsExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Releases")) {
                if (searchDialog.releasesExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Skills")) {
                if (searchDialog.skillsExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Sprints")) {
                if (searchDialog.sprintsExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Stories")) {
                if (searchDialog.storiesExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Teams")) {
                if (searchDialog.teamsExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Tasks")) {
                if (searchDialog.tasksExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            } else if (s.equals("Tags")) {
                if (searchDialog.tagsExpanded)
                    chev.setImage(chevronDown);
                else
                    chev.setImage(chevronRight);
            }
            labelBox.getChildren().setAll(chev, label);
            setGraphic(labelBox);
            setText(null);
            super.updateItem(item, empty);
            this.setOnMouseClicked(event -> {
                String s1 = item.toString();
                if (s1.equals("Backlogs")) {
                    if (searchDialog.backlogExpanded) {
                        searchDialog.backlogExpanded = false;
                    } else
                        searchDialog.backlogExpanded = true;
                } else if (s1.equals("People")) {
                    if (searchDialog.peopleExpanded)
                        searchDialog.peopleExpanded = false;
                    else
                        searchDialog.peopleExpanded = true;
                } else if (s1.equals("Projects")) {
                    if (searchDialog.projectsExpanded)
                        searchDialog.projectsExpanded = false;
                    else
                        searchDialog.projectsExpanded = true;
                } else if (s1.equals("Releases")) {
                    if (searchDialog.releasesExpanded)
                        searchDialog.releasesExpanded = false;
                    else
                        searchDialog.releasesExpanded = true;
                } else if (s1.equals("Skills")) {
                    if (searchDialog.skillsExpanded)
                        searchDialog.skillsExpanded = false;
                    else
                        searchDialog.skillsExpanded = true;
                } else if (s1.equals("Sprints")) {
                    if (searchDialog.sprintsExpanded)
                        searchDialog.sprintsExpanded = false;
                    else
                        searchDialog.sprintsExpanded = true;
                } else if (s1.equals("Stories")) {
                    if (searchDialog.storiesExpanded)
                        searchDialog.storiesExpanded = false;
                    else
                        searchDialog.storiesExpanded = true;
                } else if (s1.equals("Teams")) {
                    if (searchDialog.teamsExpanded)
                        searchDialog.teamsExpanded = false;
                    else
                        searchDialog.teamsExpanded = true;
                } else if (s1.equals("Tasks")) {
                    if (searchDialog.tasksExpanded)
                        searchDialog.tasksExpanded = false;
                    else
                        searchDialog.tasksExpanded = true;
                } else if (s1.equals("Tags")) {
                    if (searchDialog.tagsExpanded)
                        searchDialog.tagsExpanded = false;
                    else
                        searchDialog.tagsExpanded = true;
                }
                String temp = searchDialog.SearchField.getText();
                searchDialog.SearchField.setText("");
                searchDialog.SearchField.setText(temp);
            });
        }
    }
}
