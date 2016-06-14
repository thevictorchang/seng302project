package seng302.group3.model.gui;

import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import seng302.group3.controller.App;
import seng302.group3.model.Story;
import seng302.group3.model.Task;
import seng302.group3.model.gui.scrum_board.ScrumBoard;
import seng302.group3.model.gui.scrum_board.ScrumBoardController;
import seng302.group3.model.gui.scrum_board.ScrumBoardStoryCellFactory;
import seng302.group3.model.gui.scrum_board.TaskLabel;

/**
 * Created by cjm328 on 2/08/15.
 */
public class TaskListCell extends DragListCell {

    private Task.Status columnType;
    private Story storyRow;
    private ScrumBoardController scrumBoardController;


    private TaskListCell(Task.Status columnType, Story storyRow, ScrumBoardController scrumBoardController){
        this.columnType = columnType;
        this.storyRow = storyRow;
        this.scrumBoardController = scrumBoardController;
    }

    /**
     * calls the Drag list cell create factory and gives the cell factory to the list view
     * @param listView - list view we want to add the factory to
     */
    public static void createFactory(ListView listView, Task.Status columnType, Story storyRow, ScrumBoardController scrumBoardController){
        DragListCell.createFactory(listView);
        listView.setCellFactory(param ->
                        new TaskListCell(columnType, storyRow, scrumBoardController)
        );

        listView.setMaxHeight(200);
        listView.setPrefWidth(ScrumBoardStoryCellFactory.columnWidth);
    }

    /**
     * upon dragging between columns on the scrum board the task status will be set to the respective column.
     * @param item - task
     * @param empty - if the item is null
     */
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty && item != null) {
            setPadding(new Insets(5, 10, 5, 10));

            setGraphic(new TaskLabel(item, this.scrumBoardController));
            setStyle("-fx-background-color: transparent");

            if(item instanceof Task){
                Task t = (Task) item;
                Task.Status previousStatus = t.getProgressStatus();
                switch (this.columnType) {
                    case COMPLETED:
                        t.setProgressStatus(Task.Status.COMPLETED);
                        break;
                    case IN_PROGRESS:
                        t.setProgressStatus(Task.Status.IN_PROGRESS);
                        break;
                    case READY:
                        t.setProgressStatus(Task.Status.READY);
                        break;
                    default:
                        t.setProgressStatus(Task.Status.READY);
                        break;
                }
                Task.Status newStatus = t.getProgressStatus();



                if(previousStatus != newStatus){
                    App.getMainController().addHistory(App.getMainController().currentOrganisation.serializedCopy(), "Changed task status");
                }

                //TODO - If tasks keep track of the story they belong to we can make this more efficient
                else {
                    for(Story s : App.getMainController().currentOrganisation.getStories()){
                        if(s.getTasks().contains(t)){
                            if(s != this.storyRow){
                                ScrumBoard.getInstance().refresh();
                            }
                        }
                    }
                }
            }

        }
        else
            setGraphic(null);
    }
}
