package seng302.group3.model.gui;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.simpleframework.xml.Order;
import seng302.group3.controller.App;
import seng302.group3.model.Organisation;
import seng302.group3.model.Sprint;
import seng302.group3.model.Story;
import seng302.group3.model.Task;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.SprintNavigatorItem;
import seng302.group3.model.navigation.navigator_items.StoryNavigatorItem;
import seng302.group3.model.navigation.navigator_items.TaskNavigatorItem;
import seng302.group3.model.sorting.Sort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by epa31 on 8/09/15.
 */
public class AllTasksController {
    @FXML public ComboBox SprintComboBox;
    @FXML public ListView<NavigatorItem> allTasks;
    @FXML public ComboBox GroupingComboBox;
    @FXML public ComboBox OrderComboBox;
    @FXML public CheckBox showAllocatedCheck;

    @FXML public CheckBox showTaskTypeCheck;

    public Map<Story, Boolean> storyShown = new HashMap<Story, Boolean>();

    private static String ALPHABETICAL = "Name";
    private static String CHRONOLOGICAL = "Creation Date";
    private static String ESTIMATE = "Estimate";
    private static String GROUPED = "Grouped";
    private static String UNALLOCATED = "Unallocated";
    private static String UNGROUPED = "Ungrouped";

    @FXML public HBox r1;
    @FXML public HBox r2;
    @FXML public HBox r3;
    @FXML public HBox r4;
    @FXML public ColumnConstraints d1;
    @FXML public ColumnConstraints d2;
    @FXML public ColumnConstraints d3;
    @FXML public ColumnConstraints d4;



    private boolean grouped;

    private Organisation organisation;

    /**
     * Set initial listeners to the sprint/grouping/sorting comboBoxes.
     * Initialises the items in all of the starting gui elements.
     * @param sprintToShow
     */
    public void initialize(Sprint sprintToShow) {
        GroupingComboBox.valueProperty().addListener(GroupingComboChangeListener);
        OrderComboBox.valueProperty().addListener(OrderComboChangeListener);
        SprintComboBox.valueProperty().addListener(SprintComboChangeListener);
        organisation = App.getMainController().currentOrganisation;
        allTasks.setCellFactory(param -> new AllTasksListCell(allTasks,this));
        showAllocatedCheck.selectedProperty().setValue(false);
        showAllocatedCheck.selectedProperty().addListener(ShowAllocatedListener);
        showTaskTypeCheck.selectedProperty().addListener(ShowTaskTypeListener);

        if(organisation != null)
            SprintComboBox.setItems(organisation.getSprints().getObservableList());

        if(sprintToShow != null){
            SprintComboBox.getSelectionModel().select(sprintToShow);
            for(Story story : sprintToShow.getStories()){
                storyShown.put(story,Boolean.TRUE);
                allTasks.getItems().addAll(story.getTasks().getObservableList());
            }
        }
        else
            SprintComboBox.getSelectionModel().select("None");

        OrderComboBox.getItems().addAll(ALPHABETICAL,CHRONOLOGICAL, UNALLOCATED, ESTIMATE);
        OrderComboBox.getSelectionModel().select(ALPHABETICAL);
        GroupingComboBox.getItems().addAll(GROUPED, UNGROUPED);
        GroupingComboBox.getSelectionModel().select(GROUPED);
    }

    /**
     * On any change this makes sure to update all of the necessary gui elements.
     */
    public void changeRefresh() {
        organisation = App.getMainController().currentOrganisation;
        if(SprintComboBox.getSelectionModel().getSelectedItem() instanceof SprintNavigatorItem)
            taskTypeSprintChange();
        if(organisation != null){
            refreshSprintSelection();
        }
    }

    /**
     * If we have a sprint change or need to mimic a change this is used to automatically change the
     * comboBox data.
     */
    private void refreshSprintSelection() {
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

    /**
     * Refreshes the sorting method of the list view based on the current sorting selection.
     * @param sortSelection
     */
    public void refreshSort(String sortSelection){
        if (sortSelection.equals(ALPHABETICAL))
            Sort.alphabeticalSort(allTasks.getItems(), grouped);
        else if (sortSelection.equals(CHRONOLOGICAL))
            Sort.chronologicalSort(allTasks.getItems(), grouped);
        else if (sortSelection.equals(UNALLOCATED))
            Sort.assignedPeopleSort(allTasks.getItems(), grouped);
        else if (sortSelection.equals(ESTIMATE))
            Sort.taskEstimateSort(allTasks.getItems(), grouped);
    }

    /**
     * Change listener for the Order Selection Combo Box
     */
    ChangeListener OrderComboChangeListener = (observable, oldValue, newValue) -> {
        Object currentlySelected = OrderComboBox.getSelectionModel().getSelectedItem();
        if(currentlySelected != null && currentlySelected instanceof String){
            //Choose which sort to do.
            String sortSelection = (String) currentlySelected;
            refreshSort(sortSelection);
        }
    };

    /**
     * Change listener for the ShowAllocated Check which calls the refresh.
     */
    ChangeListener ShowAllocatedListener = (observable, oldValue, newValue) -> {
        if (oldValue != newValue && newValue != null){
            changeRefresh();
        }
    };

    /**
     * Creates a gui bar which provides a representation of the distribution of tasks in the given
     * Sprint.
     */
    private void taskTypeSprintChange(){
        if(showTaskTypeCheck.isSelected()){
            r1.setVisible(true);
            r2.setVisible(true);
            r3.setVisible(true);
            r4.setVisible(true);
            Integer unspecifiedCount = 0;
            Integer developmentCount = 0;
            Integer testingCount = 0;
            Integer designCount = 0;
            for(Story story:((SprintNavigatorItem) SprintComboBox.getSelectionModel().getSelectedItem()).getItem().getStories()){
                for(Task task : story.getTasks()){
                    if(showAllocatedCheck.isSelected()){
                        if(task.getTaskType().equals(Task.Type.UNSPECIFIED)){
                            unspecifiedCount += 1;
                        } else if(task.getTaskType().equals(Task.Type.DEVELOPMENT)){
                            developmentCount += 1;
                        } else if(task.getTaskType().equals(Task.Type.TESTING)){
                            testingCount += 1;
                        } else if(task.getTaskType().equals(Task.Type.DESIGN)){
                            designCount += 1;
                        }
                    }else{
                        if (task.getAssignedPeople().size() == 0){
                            if(task.getTaskType().equals(Task.Type.UNSPECIFIED)){
                                unspecifiedCount += 1;
                            } else if(task.getTaskType().equals(Task.Type.DEVELOPMENT)){
                                developmentCount += 1;
                            } else if(task.getTaskType().equals(Task.Type.TESTING)){
                                testingCount += 1;
                            } else if(task.getTaskType().equals(Task.Type.DESIGN)){
                                designCount += 1;
                            }
                        }
                    }

                }
            }
            Double totalCount = 0.0 + unspecifiedCount + designCount + developmentCount + testingCount;

            Tooltip tooltip1 = new Tooltip("Unspecified");
            Tooltip.install(r1,tooltip1);
            Tooltip tooltip2 = new Tooltip("Development");
            Tooltip.install(r2,tooltip2);
            Tooltip tooltip3 = new Tooltip("Testing");
            Tooltip.install(r3,tooltip3);
            Tooltip tooltip4 = new Tooltip("Design");
            Tooltip.install(r4,tooltip4);

            d1.setPercentWidth((unspecifiedCount / totalCount) * 100);
            d2.setPercentWidth((developmentCount / totalCount) * 100);
            d3.setPercentWidth((testingCount / totalCount) * 100);
            d4.setPercentWidth((designCount / totalCount) * 100);
        }
    }

    /**
     * Change listener for ShowTaskType Check which calls the refresh and checks to see if we should
     * be showing the task distribution bar.
     */
    ChangeListener ShowTaskTypeListener = (observable, oldValue, newValue) -> {
        if (oldValue != newValue && newValue != null){
            changeRefresh();
        }
        if(!(showTaskTypeCheck.isSelected())){
            r1.setVisible(false);
            r2.setVisible(false);
            r3.setVisible(false);
            r4.setVisible(false);
        }
    };

    /**
     * Change listener for the Grouping Selection Combo Box
     */
    ChangeListener GroupingComboChangeListener = (observable, oldValue, newValue) -> {
        Object currentlySelected = GroupingComboBox.getSelectionModel().getSelectedItem();
        if(currentlySelected != null && currentlySelected instanceof String){
            if(currentlySelected.equals(GROUPED))
                grouped = true;
            else if(currentlySelected.equals(UNGROUPED))
                grouped = false;
            allTasks.getItems().clear();

            Object currentlySelectedSprint = SprintComboBox.getSelectionModel().getSelectedItem();
            if(currentlySelectedSprint instanceof SprintNavigatorItem) {
                Sprint currentSprint = ((SprintNavigatorItem) currentlySelectedSprint).getItem();
                allTasks.getItems().clear();
                //Sort.sprintStoryPriority(currentSprint.getStories());
                for (NavigatorItem navigatorItem : currentSprint.getStories().getObservableList()){
                    StoryNavigatorItem story = (StoryNavigatorItem) navigatorItem;
                    if (grouped)
                        allTasks.getItems().add(navigatorItem);
                    if((storyShown.get(story.getItem())!=null)&&(!(storyShown.get(story.getItem())))){
                    }else{
                        storyShown.put(story.getItem(),Boolean.TRUE);
                        for (NavigatorItem tNavigatorItem: story.getItem().getTasks().getObservableList()){
                            TaskNavigatorItem taskNavigatorItem = (TaskNavigatorItem) tNavigatorItem;
                            if(showAllocatedCheck.isSelected()){
                                allTasks.getItems().add(taskNavigatorItem);
                            }else{
                                Task task = (Task) taskNavigatorItem.getItem();
                                if (task.getAssignedPeople().size() == 0){
                                    allTasks.getItems().add(taskNavigatorItem);
                                }
                            }
                        }
                        //allTasks.getItems().addAll(story.getItem().getTasks().getObservableList());
                    }

                }
                Object currentlySelectedSort = OrderComboBox.getSelectionModel().getSelectedItem();
                String sortSelection = (String) currentlySelectedSort;
                refreshSort(sortSelection);
            }
        }
    };

    /**
     * Change listener for the Sprint Selection Combo Box
     */
    ChangeListener SprintComboChangeListener = (observable, oldValue, newValue) -> {
        Object currentlySelected = SprintComboBox.getSelectionModel().getSelectedItem();
        //if(allTasks.getItems() != null)

        if(currentlySelected != null && currentlySelected instanceof SprintNavigatorItem){
            Sprint currentSprint = ((SprintNavigatorItem) currentlySelected).getItem();
            allTasks.getItems().clear();
            //Sort.sprintStoryPriority(currentSprint.getStories());
            for (NavigatorItem navigatorItem:currentSprint.getStories().getObservableList()){
                StoryNavigatorItem story = (StoryNavigatorItem) navigatorItem;
                if(grouped)
                    allTasks.getItems().add(navigatorItem);
                if((storyShown.get(story.getItem())!=null)&&(!(storyShown.get(story.getItem())))){
                }else{
                    storyShown.put(story.getItem(),Boolean.TRUE);
                    for (NavigatorItem tNavigatorItem: story.getItem().getTasks().getObservableList()){
                        TaskNavigatorItem taskNavigatorItem = (TaskNavigatorItem) tNavigatorItem;
                        if(showAllocatedCheck.isSelected()){
                            allTasks.getItems().add(taskNavigatorItem);
                        }else{
                            Task task = (Task) taskNavigatorItem.getItem();
                            if (task.getAssignedPeople().size() == 0){
                                allTasks.getItems().add(taskNavigatorItem);
                            }
                        }
                    }
                }


            }
            Object currentlySelectedSort = OrderComboBox.getSelectionModel().getSelectedItem();
            String sortSelection = (String) currentlySelectedSort;
            refreshSort(sortSelection);

        }
        else{
            allTasks.getItems().clear();
        }
    };



}
