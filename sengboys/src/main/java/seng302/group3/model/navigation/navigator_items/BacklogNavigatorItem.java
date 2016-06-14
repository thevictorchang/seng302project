package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Backlog;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by ntr24 on 18/05/15.
 */
public class BacklogNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = 2104962256820852626L;

    private Backlog backlog;
    private int depth;

    SerializableObservableList<NavigatorItem> nextItems;

    public BacklogNavigatorItem(Backlog backlog) {
        this.backlog = backlog;
        setNext(backlog.getNavigatorItems());
    }

    /**
     * returns the backlogs short name
     *
     * @return - the backlogs short name
     */
    public String toString() {
        return backlog.toString();
    }

    @Override public void setNavigatorDepth(int pos) {
        depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {
        this.nextItems = nextItems;
    }

    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return nextItems;
    }

    @Override public Object getItem() {
        return backlog;
    }

    @Override public String getName() {
        return backlog.getShortName();
    }

    @Override public String getDescription() {
        return backlog.getPaneString();
    }
    @Override public Node getGrid(){
        return backlog.getOverviewPaneGrid();
    }
}
