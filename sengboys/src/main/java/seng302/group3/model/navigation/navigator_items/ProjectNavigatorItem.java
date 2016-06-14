package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Project;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by ntr24 on 4/05/15.
 */
public class ProjectNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = 2755610536015230236L;

    Project project;
    private int depth;

    SerializableObservableList<NavigatorItem> nextNavigatorItems;

    public ProjectNavigatorItem(Project project) {
        this.project = project;
        this.setNext(project.getNavigatorItems());
    }

    @Override public String toString() {
        return project.getShortName();
    }

    @Override public void setNavigatorDepth(int pos) {
        depth = pos;
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

    @Override public Project getItem() {
        return project;
    }

    @Override public String getName() {
        return project.getNameLong();
    }

    @Override public String getDescription() {
        String description = project.getPaneString();
        return description;
    }

    @Override public Node getGrid() {
        return project.getOverviewPaneGrid();
    }
}
