package seng302.group3.model.search;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import seng302.group3.controller.App;
import seng302.group3.model.gui.SearchResultListCell;
import seng302.group3.model.io.property_sheets.DialogSheet;
import seng302.group3.model.io.property_sheets.EditorSheet;
import seng302.group3.model.sorting.Sort;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Andrew on 19/09/2015.
 */
public class SearchDialog {

    public boolean backlogExpanded = true;
    public boolean peopleExpanded = true;
    public boolean projectsExpanded = true;
    public boolean releasesExpanded = true;
    public boolean skillsExpanded = true;
    public boolean sprintsExpanded = true;
    public boolean storiesExpanded = true;
    public boolean teamsExpanded = true;
    public boolean tasksExpanded = true;
    public boolean tagsExpanded = true;

    private VBox dialogLayout = new VBox();
    final Stage dialog = new Stage();

    @FXML VBox content;
    @FXML Button CancelButton;
    @FXML public TextField SearchField;
    @FXML ListView searchResultsList;

    /**
     * Frame for search which creates the layout of how the dialog is meant to be presented
     * @param sheet
     */
    private void SearchFrame(EditorSheet sheet) {

        // load the stage layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchDialog.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            dialogLayout.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.initStyle(StageStyle.TRANSPARENT);

        dialog.initModality(Modality.APPLICATION_MODAL);
        content.getChildren().add(sheet.draw());
        Scene dialogScene = new Scene(dialogLayout);

        dialogScene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    CancelButton.fire();
                }
            }
        });
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
    }

    /**
     * Checks if a user entered hashtag value is one of our accepted hashtag values.
     * @param hashtagVal
     * @return
     */
    private boolean validHashtag(String hashtagVal) {
        if (hashtagVal.equals("people") ||
                hashtagVal.equals("projects") ||
                hashtagVal.equals("teams") ||
                hashtagVal.equals("tags") ||
                hashtagVal.equals("releases") ||
                hashtagVal.equals("backlogs") ||
                hashtagVal.equals("stories") ||
                hashtagVal.equals("sprints") ||
                hashtagVal.equals("skills") ||
                hashtagVal.equals("tasks")  ||
                hashtagVal.equals("name"))  {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Dialog for the searching functionality, that prompts the user and displays results in a list view.
     */
    public void searchDialog() {
        DialogSheet sheet = new DialogSheet();
        sheet.setLabelText("What would you like to search?\n" +
                "For a more specific search, use the '#' symbol");

        this.SearchFrame(sheet);
        SearchDialog dialogSheet = this;
        dialog.setTitle("Search Organisation");

        CancelButton.setOnAction(event -> {
            dialog.close();
        });

        SearchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String hashtagValue = null;
                SearchResultListCell.createFactory(searchResultsList, dialog, dialogSheet);
                Collection results;
                if (newValue.equals("")) {
                    searchResultsList.getItems().clear();
                    sheet.setLabelText("What would you like to search?\n" +
                            "For a more specific search, use the '#' symbol");
                } else {
                    if (newValue.startsWith("#") && !newValue.contains(" ")) {
                        sheet.setLabelText("#people\t\t#projects\t\t#teams\n" +
                                "#stories\t\t#sprints\t\t#backlogs\n" +
                                "#releases\t\t#skills\t\t#tags\n" +
                                "#tasks\t\t#name");
                    } else {
                        if (newValue.startsWith("#") && newValue.contains(" ")) {
                            if (newValue.contains(" ")) {
                                hashtagValue = newValue.substring(0, newValue.indexOf(" "));
                                hashtagValue = hashtagValue.replace("#", "");
                                hashtagValue = hashtagValue.toLowerCase();
                            }
                            newValue = newValue.replaceAll("#[A-Za-z]+\\s", "");
                            if (validHashtag(hashtagValue)) {
                                results = SearchUtil.globalSearch(App.getMainController().currentOrganisation, newValue, hashtagValue, dialogSheet);
                                sheet.setLabelText("Searching " + hashtagValue + ": " + newValue);
                            } else {
                                results = SearchUtil.globalSearch(App.getMainController().currentOrganisation, newValue, null, dialogSheet);
                                sheet.setLabelText("Bad hashtag. Searching: " + newValue);
                            }
                        } else {
                            results = SearchUtil.globalSearch(App.getMainController().currentOrganisation, newValue, null, dialogSheet);
                            sheet.setLabelText("Searching: " + newValue);
                        }
                        if (!newValue.equals("")) {
                            searchResultsList.getItems().setAll(results);
                            searchResultsList.getItems().sort((o1, o2) -> Sort.searchResultsCompare(o1, o2));
                        } else {
                            searchResultsList.getItems().clear();
                        }
                    }
                }
                searchResultsList.setMaxHeight(37 * searchResultsList.getItems().size());
                if (searchResultsList.getItems().size() <= 16) {
                    dialog.setMinHeight(217 + 37 * searchResultsList.getItems().size());
                    dialog.setMaxHeight(217 + 37 * searchResultsList.getItems().size());
                }
                else{
                    dialog.setMinHeight(600);
                    dialog.setMaxHeight(600);
                }

            }
        });
        Platform.runLater(() -> {
            SearchField.setText(" ");
            SearchField.setText("");
        });
        dialog.showAndWait();

    }

}
