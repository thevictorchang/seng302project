package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Team;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by ntr24 on 5/05/15.
 */
public class TeamNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = -9018806239909712844L;

    Team team;
    private int depth;

    SerializableObservableList<NavigatorItem> nextNavigatorItems;

    public TeamNavigatorItem(Team team) {
        this.team = team;
        this.setNext(this.team.getNavigatorItems());
    }

    public String toString() {
        return this.team.getShortName();
    }

    @Override public void setNavigatorDepth(int pos) {
        this.depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return depth;
    }

    @Override public void setNext(SerializableObservableList<NavigatorItem> nextItems) {
        nextNavigatorItems = nextItems;
    }

    @Override public SerializableObservableList<NavigatorItem> getNext() {
        return nextNavigatorItems;
    }

    @Override public Team getItem() {
        return team;
    }

    @Override public String getName() {
        return team.getNameLong();
    }

    @Override public String getDescription() {
        String description = team.getPaneString();
        return description;
    }

    @Override public Node getGrid() {
        return team.getOverviewPaneGrid();
    }
}
