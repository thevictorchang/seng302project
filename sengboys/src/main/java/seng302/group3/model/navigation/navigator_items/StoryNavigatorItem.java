package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Story;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by ntr24 on 15/05/15.
 */
public class StoryNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = 3855468979933993261L;

    Story story;
    private int depth;

    SerializableObservableList<NavigatorItem> nextItems;

    /**
     * constructor sets the story for the nav item
     *
     * @param story - story to set for nav item
     */
    public StoryNavigatorItem(Story story) {
        this.story = story;
        setNext(story.getNavigatorItems());
    }

    /**
     * returns the stories short name
     *
     * @return - stories short name
     */
    public String toString() {
        return this.story.getShortName();
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

    @Override public Story getItem() {
        return this.story;
    }

    @Override public String getName() {
        return this.story.getFullName();
    }

    @Override public String getDescription() {
        return this.story.getPaneString();
    }

    @Override public Node getGrid() {
        return story.getOverviewPaneGrid();
    }
}
