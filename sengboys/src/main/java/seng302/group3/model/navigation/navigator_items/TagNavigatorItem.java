package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Story;
import seng302.group3.model.Tag;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by ntr24 on 15/05/15.
 */
public class TagNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = -2823040801507280718L;

    Tag tag;
    private int depth;

    SerializableObservableList<NavigatorItem> nextItems;

    /**
     * constructor sets the tag for the nav item
     *
     * @param tag - tag to set for nav item
     */
    public TagNavigatorItem(Tag tag) {
        this.tag = tag;
        setNext(tag.getNavigatorItems());
    }

    /**
     * returns the stories short name
     *
     * @return - stories short name
     */
    public String toString() {
        return this.tag.getShortName();
    }

    @Override public void setNavigatorDepth(int pos) {
        this.depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return this.depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {
        this.nextItems = nextItems;
    }

    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return nextItems;
    }

    @Override public Tag getItem() {
        return this.tag;
    }

    @Override public String getName() {
        return this.tag.getShortName();
    }

    @Override public String getDescription() {
        String description = tag.getPaneString();
        return description;
    }

    @Override public Node getGrid() {
        return tag.getOverviewPaneGrid();
    }
}
