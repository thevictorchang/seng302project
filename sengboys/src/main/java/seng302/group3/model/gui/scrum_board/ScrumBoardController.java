package seng302.group3.model.gui.scrum_board;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import seng302.group3.controller.App;
import seng302.group3.model.Organisation;
import seng302.group3.model.Sprint;
import seng302.group3.model.Story;
import seng302.group3.model.navigation.navigator_items.SprintNavigatorItem;
import seng302.group3.model.sorting.Sort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ntr24 on 1/08/15.
 */
public class ScrumBoardController {

    Organisation organisation;

    @FXML public ComboBox SprintComboBox;
    @FXML public ListView scrumBoard;

    public ScrumBoardController(){
        this.organisation = App.getMainController().currentOrganisation;
    }

    public Map<LocalDateTime, ScrumBoardMetaData> metaDataMap = new HashMap<>();

    /**
     * Initializes the scrum board with a sprint to show
     * @param sprintToShow
     */
    public void initialize(Sprint sprintToShow){
        //FilterUtil.autoCompleteComboBox(SprintComboBox, FilterUtil.AutoCompleteMode.CONTAINING);
        SprintComboBox.valueProperty().addListener(SprintComboChangeListener);


        if(organisation != null)
            SprintComboBox.setItems(organisation.getSprints().getObservableList());

        if(sprintToShow != null){
            SprintComboBox.getSelectionModel().select(sprintToShow);
            for(Story story : sprintToShow.getStories()){
                metaDataMap.put(story.getDateCreated(), new ScrumBoardMetaData());
            }
        }
        else
            SprintComboBox.getSelectionModel().select("None");

        scrumBoard.setCellFactory(param -> new ScrumBoardStoryCellFactory(this));
    }


    /**
     * is called whenever new history is added or there is an undo/redo. Refreshes the Scrum Board
     * with the updated data
     */
    public void changeRefresh(){

        organisation = App.getMainController().currentOrganisation;

        if(organisation != null){
            refreshSprintSelection();
        }

    }

    /**
     * Is called by changeRefresh to update the Scrum Board to the sprint selection showing the current
     * state of the program
     */
    private void refreshSprintSelection(){
        Object originalSelection = SprintComboBox.getSelectionModel().getSelectedItem();
        SprintComboBox.setItems(organisation.getSprints().getObservableList());
        SprintComboBox.getSelectionModel().select("None");


        if(originalSelection instanceof SprintNavigatorItem){
            LocalDateTime originalDate = ((SprintNavigatorItem) originalSelection).getItem().getDateCreated();

            for(Object o : SprintComboBox.getItems()){
                if(o instanceof SprintNavigatorItem){
                    if(originalDate.equals(((SprintNavigatorItem) o).getItem().getDateCreated())){
                        SprintComboBox.getSelectionModel().select(o);
                        break;
                    }
                }
            }
        }
    }

    private void addTitleRow(){
        if(scrumBoard.getItems() != null)
            scrumBoard.getItems().add(0, "!TITLE_ROW!");
    }

    /**
     * Change listener for the Sprint Selection Combo Box
     */
    ChangeListener SprintComboChangeListener = (observable, oldValue, newValue) -> {
        Object currentlySelected = SprintComboBox.getSelectionModel().getSelectedItem();
        if(scrumBoard.getItems() != null)

        if(currentlySelected != null && currentlySelected instanceof SprintNavigatorItem){
            Sprint currentSprint = ((SprintNavigatorItem) currentlySelected).getItem();

            scrumBoard.getItems().setAll(currentSprint.getStories().getObservableList());
            Sort.prioritySort(scrumBoard.getItems(),false);

            // Add all the stories that currently dont have meta data
            for(Story story : currentSprint.getStories()){
                if(!metaDataMap.containsKey(story.getDateCreated()))
                    metaDataMap.put(story.getDateCreated(), new ScrumBoardMetaData());
            }
        }
        else{
            scrumBoard.getItems().clear();
        }
        addTitleRow();
    };

}
