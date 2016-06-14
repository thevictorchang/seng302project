package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import seng302.group3.model.LoggedTime;
import seng302.group3.model.Task;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by Eddy on 31-Jul-15.
 */
public class LoggedTimeNavigatorItem implements NavigatorItem {
    LoggedTime loggedTime;
    private int depth;

    public LoggedTimeNavigatorItem(LoggedTime loggedTime) {
        this.loggedTime = loggedTime;
    }

    public String toString() {
        return this.loggedTime.toString();
    }

    @Override public void setNavigatorDepth(int pos) {
        this.depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return this.depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {
    }

    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return null;
    }

    @Override public Object getItem() {
        return this.loggedTime;
    }

    @Override public String getName() {
        return this.loggedTime.getShortName();
    }

    @Override public String getDescription() {
        return this.loggedTime.getPaneString();
    }

    @Override public Node getGrid() {
        return loggedTime.getOverviewPaneGrid();
    }
}
