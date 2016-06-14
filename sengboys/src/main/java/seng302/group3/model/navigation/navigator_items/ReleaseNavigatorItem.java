package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Release;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by ntr24 on 5/05/15.
 */
public class ReleaseNavigatorItem implements NavigatorItem {
    Release release;
    private int depth;
    SerializableObservableList<NavigatorItem> nextNavigatorItems;

    public ReleaseNavigatorItem(Release release) {
        this.release = release;
    }

    public String toString() {
        return release.getShortName();
    }

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

    @Override public Release getItem() {
        return release;
    }

    @Override public String getName() {
        return release.getShortName();
    }

    @Override public String getDescription() {
        String description = release.getPaneString();
        return description;
    }

    @Override public Node getGrid() {
        return release.getOverviewPaneGrid();
    }
}
