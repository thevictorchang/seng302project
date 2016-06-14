package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Backlog;
import seng302.group3.model.Sprint;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by Andrew on 22/07/2015.
 */
public class SprintNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = 1824341515281037076L;
    Sprint sprint;
    private int depth;
    SerializableObservableList<NavigatorItem> nextNavigatorItems;

    public SprintNavigatorItem(Sprint sprint) {
        this.sprint = sprint;
        setNext(sprint.getNavigatorItems());
    }

    public String toString() { return sprint.getShortName(); }

    @Override public void setNavigatorDepth(int pos) {
        depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {
        this.nextNavigatorItems = nextItems;
    }

    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return this.nextNavigatorItems;
    }

    @Override public Sprint getItem() {
        return sprint;
    }

    @Override public String getName() {
        return sprint.getShortName();
    }

    @Override public String getDescription() {
        String description = sprint.getPaneString();
        return description;
    }

    @Override public Node getGrid() {
        return sprint.getOverviewPaneGrid();
    }
}
