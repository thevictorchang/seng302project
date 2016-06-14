package seng302.group3.model.gui;

import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * not for instancing only for extending
 * Created by ntr24 on 22/05/15.
 */
abstract public class DragListCell<T> extends ListCell<T> {


    static ListCell draggingItem;

    /**
     * Is called by children class to setup the list view so that things can be dragged into it when there are no
     * items in the list view
     * @param listView - List view to add the drag events to
     */
    public static void createFactory(ListView listView){
        if(listView != null && listView.getOnDragEntered() == null){
            final Object blankStory = null;
            listView.setOnDragEntered(event -> {
                listView.getItems().add(blankStory);
            });
            listView.setOnDragExited(event -> {
                listView.getItems().removeAll(blankStory);
            });
        }
    }

    /**
     * A cell builder that allows the cell to be dragged with the mouse.
     */
    public DragListCell() {

        ListCell thisCell = this;

        getStyleClass().add("dragCell");

        setOnDragDetected(event -> {
            // if there is nothing to drag return
            if (getItem() == null) {
                return;
            }


            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Here we are using the hashcode to know which item is being dragged
            content.putString(Integer.toString(getItem().hashCode()));

            SnapshotParameters snapshotParams = new SnapshotParameters();
            //snapshotParams.setTransform(Transform.translate(-event.getX(), -event.getY()));
            snapshotParams.setFill(Paint.valueOf("transparent"));

            ListCell src = (ListCell) event.getSource();

            WritableImage snapshot = (src.getGraphic()).snapshot(snapshotParams, null);


            dragboard.setContent(content);
            dragboard.setDragView(snapshot);
            setOpacity(0);
            draggingItem = this;
            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                setOpacity(1);
            }
        });

        setOnDragDropped(event -> {
            draggingItem.setOpacity(1);

            Dragboard dragboard = event.getDragboard();

            // make sure we are dragging something
            if (dragboard.hasString()) {
                // we use the hash code to find the dragged item
                String itemHashcode = dragboard.getString();

                ObservableList<T> items = getListView().getItems();

                Object selectedItem = null;
                for (Object item : draggingItem.getListView().getItems()) {

                    if (item != null && Integer.parseInt(itemHashcode) == item.hashCode()) {
                        selectedItem = item;
                        break;
                    }


                }
                // we found the item we dragged so lets use it
                if (selectedItem != null) {

                    Object droppedOn = getItem();
                    if(droppedOn == null){
                        draggingItem.getListView().getItems().remove(selectedItem);
                        getListView().getItems().add((T) selectedItem);
                    }
                    else{
                        int dropIndex = items.indexOf(getItem());

                        draggingItem.getListView().getItems().remove(selectedItem);
                        items.add(dropIndex, (T) selectedItem);

                        // we do this so that the order is saved after we drag and drop
                        List<T> copyItems = new ArrayList<>(getListView().getItems());
                        getListView().getItems().setAll(copyItems);
                    }
                }
            }


            event.setDropCompleted(true);
            event.consume();

        });

        setOnDragDone(event -> {
            draggingItem.setOpacity(1);
            event.consume();
        });

    }

    /**
     * Updates the position of the item in the draggable list.
     *
     * @param item
     * @param empty
     */
    @Override protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
    }



}
