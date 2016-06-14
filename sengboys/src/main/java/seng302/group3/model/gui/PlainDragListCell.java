package seng302.group3.model.gui;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Cell with just the text of the item
 * Created by cjm328 on 2/08/15.
 */
public class PlainDragListCell extends DragListCell{

    private PlainDragListCell(){}

    /**
     * calls the Drag list cell create factory and gives the cell factory to the list view
     * @param listView - list view we want to add the factory to
     */
    public static void createFactory(ListView listView){
        DragListCell.createFactory(listView);
        listView.setCellFactory(param ->
            new PlainDragListCell()
        );
    }

    /**
     * Draws the item with just the items to string value
     * @param item - item which relates to the cell
     * @param empty - boolean if the cell is empty
     */
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty && item != null)
            setGraphic(new Label(item.toString()));
        else
            setGraphic(null);
    }
}
