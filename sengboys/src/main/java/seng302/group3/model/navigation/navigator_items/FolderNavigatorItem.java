package seng302.group3.model.navigation.navigator_items;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import seng302.group3.controller.App;
import seng302.group3.model.io.SerializableObservableList;
import seng302.group3.model.navigation.NavigatorItem;

/**
 * Created by ntr24 on 4/05/15.
 */
public class FolderNavigatorItem implements NavigatorItem {
    private static final long serialVersionUID = -2297863343930881408L;

    String name;
    SerializableObservableList<NavigatorItem> nextNavigatorItems;

    private int depth;

    public FolderNavigatorItem(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public void setNext(SerializableObservableList nextItems) {
        this.nextNavigatorItems = nextItems;
    }

    @Override public void setNavigatorDepth(int pos) {
        depth = pos;
    }

    @Override public int getNavigatorDepth() {
        return depth;
    }

    @Override public SerializableObservableList getNext() {
        return this.nextNavigatorItems;
    }

    @Override public Object getItem() {
        return null;
    }

    @Override public String getName() {
        return name;
    }

    @Override public String getDescription() {
        String description = "Folder for " + this.getName() + "\n";
        if (this.getNext() != null && !this.getNext().isEmpty()) {
            description += "\nContaining:\n";
            for (Object n : this.getNext()) {
                description += "    " + n.toString() + "\n";
            }
        }


        return description;
    }

    @Override public Node getGrid() {
        GridPane grid = new GridPane();

        grid.setVgap(30);
        grid.getStyleClass().add("gridPane");
        Label labelName = new Label(this.getName());
        labelName.getStyleClass().add("gridPaneTitle");
        Label descriptionLabel = new Label(this.getDescription());
        descriptionLabel.getStyleClass().add("gridPaneLabel");

        grid.getChildren().addAll(labelName,descriptionLabel);

        GridPane.setConstraints(labelName,0,0);
        GridPane.setConstraints(descriptionLabel, 0, 1);

        RowConstraints r1 = new RowConstraints();
        RowConstraints r2 = new RowConstraints();

        grid.getRowConstraints().addAll(r1,r2);

        ScrollPane scrollPane = new ScrollPane(grid);
        return scrollPane;
    }
}
