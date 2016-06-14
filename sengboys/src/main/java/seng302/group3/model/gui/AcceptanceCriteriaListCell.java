package seng302.group3.model.gui;

/**
 * Created by ntr24 on 22/07/15.
 */
public class AcceptanceCriteriaListCell extends DragListCell {

    /**
     * Does the setting of text for an acceptance criteria cell with no other formatting.
     * @param item - An acceptance criteria
     * @param empty - A boolean which will tell us if the cell has an object inside.
     */
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.toString());
        }
    }
}
