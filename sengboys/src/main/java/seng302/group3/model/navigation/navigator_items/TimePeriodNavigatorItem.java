package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.TimePeriod;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by epa31 on 24/05/15.
 */
public class TimePeriodNavigatorItem implements NavigatorItem {

    TimePeriod timePeriod;
    private int depth;

    public TimePeriodNavigatorItem(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;

    }

    @Override public String toString() {
        return ("" + timePeriod.getStartDate() + " to " + timePeriod.getEndDate() + "\n" +
            timePeriod.getProject() + " - " + timePeriod.getTeam());
    }

    @Override public void setNavigatorDepth(int pos) {
        depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {

    }

    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return null;
    }

    @Override public Object getItem() {
        return timePeriod;
    }

    @Override public String getName() {
        return (timePeriod.getTeam().getShortName() + timePeriod.getStartDate());
    }

    @Override public String getDescription() {
        return (timePeriod.getPaneString());
    }

    @Override public Node getGrid() {
        return timePeriod.getOverviewPaneGrid();
    }
}
