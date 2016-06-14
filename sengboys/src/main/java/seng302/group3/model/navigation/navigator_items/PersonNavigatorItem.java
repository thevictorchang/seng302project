package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import seng302.group3.model.Person;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by ntr24 on 4/05/15.
 */
public class PersonNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = -391849799279108702L;

    Person person;
    private int depth;

    SerializableObservableList<NavigatorItem> nextNavigatorItems;

    public PersonNavigatorItem(Person person) {
        this.person = person;
        this.setNext(this.person.getNavigatorItems());
    }

    @Override public String toString() {
        return person.getShortName();
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

    @Override public Person getItem() {
        return person;
    }

    @Override public String getName() {
        return person.getFullName();
    }

    @Override public String getDescription() {
        String description = person.getPaneString();
        return description;
    }

    @Override public Node getGrid() {
        return person.getOverviewPaneGrid();
    }
}
