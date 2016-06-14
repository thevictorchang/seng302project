package seng302.group3.model.navigation;

import javafx.scene.Node;
import seng302.group3.model.io.SerializableObservableList;

import java.io.Serializable;

/**
 * Created by ntr24 on 24/04/15.
 */
public interface NavigatorItem extends Serializable {
    /**
     * NavigatorItem interface string representation
     *
     * @return
     */
    public String toString();

    /**
     * Sets the navigation depth of the Navigator
     *
     * @param pos
     */
    public void setNavigatorDepth(int pos);

    /**
     * gets the depth of the Navigator
     *
     * @return
     */
    public int getNavigatorDepth();

    /**
     * Sets the collection of navigatorItems one level deeper in the navigator
     *
     * @param nextItems
     */
    public void setNext(SerializableObservableList<NavigatorItem> nextItems);

    /**
     * Gets the collection of next navigatorItems
     *
     * @return collection of next navigatorItems
     */
    public SerializableObservableList<NavigatorItem> getNext();

    /**
     * Gets the item represented by this navigatorItem
     *
     * @return item represented by this navigatorItem
     */
    public Object getItem();

    /**
     * gets the name of this navigatorItem in a suitable form
     *
     * @return name of this navigatorItem in a suitable form
     */
    public String getName();

    /**
     * gets the description of this navigatorItem
     *
     * @return description of this navigatorItem
     */
    public String getDescription();

    public Node getGrid();
}
