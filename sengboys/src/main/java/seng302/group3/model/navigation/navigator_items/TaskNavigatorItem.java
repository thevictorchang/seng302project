package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Task;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by Eddy on 31-Jul-15.
 */
public class TaskNavigatorItem implements NavigatorItem {
    Task task;
    private int depth;
    private SerializableObservableList<NavigatorItem> nextItems;

    public TaskNavigatorItem(Task task) {
        this.task = task;
        setNext(task.getNavigatorItems());
    }

    public String toString() {
        return this.task.getShortName();
    }

    @Override public void setNavigatorDepth(int pos) {
        this.depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return this.depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {this.nextItems = nextItems;
    }
    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return nextItems;
    }

    @Override public Object getItem() {
        return this.task;
    }

    @Override public String getName() {
        return this.task.getShortName();
    }

    @Override public String getDescription() {
        return this.task.getPaneString();
    }

    @Override public Node getGrid() {
        return task.getOverviewPaneGrid();
    }
}
