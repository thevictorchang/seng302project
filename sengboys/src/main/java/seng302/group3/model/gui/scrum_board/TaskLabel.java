package seng302.group3.model.gui.scrum_board;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng302.group3.controller.App;
import seng302.group3.model.Task;
import seng302.group3.model.io.Editor;
import seng302.group3.model.navigation.navigator_items.SprintNavigatorItem;

/**
 * Created by ntr24 on 4/08/15.
 */
public class TaskLabel extends Label{
    private static final int xInsets = 20;
    private static final int yInsets = 10;

    public TaskLabel(Object item, final ScrumBoardController scrumBoardController) {
        HBox vBox = new HBox();
        Button logTimeButton;
        Button allocatePeopleButton;
        Button editButton;
        Label taskName;

        if(item instanceof Task){
            Task task = (Task) item;
            logTimeButton = new Button();
            logTimeButton.setGraphic(new ImageView(ScrumBoard.clock));
            logTimeButton.getStyleClass().add("taskButtons");

            taskName = new Label(task.getShortName());
            logTimeButton.setOnAction((event) -> {
                App.getMainController().newTimeLogButton(task);
            });

            allocatePeopleButton = new Button();
            allocatePeopleButton.getStyleClass().addAll("taskButtons");
            allocatePeopleButton.tooltipProperty().setValue(new Tooltip(task.getAssignedPeople().toString()));
            int numPeople = task.getAssignedPeople().size();
            HBox assignPeopleHbox = new HBox();
            Label assignPeopleLabel = new Label("" + numPeople);
            assignPeopleLabel.setStyle("-fx-text-fill: black;");
            assignPeopleHbox.getChildren().add(assignPeopleLabel);
            if(task.getAssignedPeople().size() <= 0){
                assignPeopleHbox.getChildren().add(new ImageView(ScrumBoard.noPerson));
            } else if(task.getAssignedPeople().size() <= 1){
                assignPeopleHbox.getChildren().add(new ImageView(ScrumBoard.onePerson));
            } else if(task.getAssignedPeople().size() <= 4){
                assignPeopleHbox.getChildren().add(new ImageView(ScrumBoard.threePeople));
            } else{
                assignPeopleHbox.getChildren().add(new ImageView(ScrumBoard.multiplePeople));
            }

            editButton = new Button();
            editButton.getStyleClass().addAll("taskButtons");
            editButton.setGraphic(new ImageView(ScrumBoard.editImg));
            editButton.setOnAction(event -> {
                new Editor().edit(App.getMainController().currentOrganisation, task);
            });

            allocatePeopleButton.setGraphic(assignPeopleHbox);

            allocatePeopleButton.setOnAction((event) -> {
                new Editor().assignPeopleToTask(task, ((SprintNavigatorItem) scrumBoardController.SprintComboBox.getValue()).getItem());
            });

            //setTooltip(new Tooltip(task.getDescription()));
        }
        else {
            logTimeButton = new Button();
            taskName = new Label();
            allocatePeopleButton = new Button();
            editButton = new Button();
        }

        HBox buttonsHBox = new HBox(logTimeButton, allocatePeopleButton);
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);

        taskName.getStyleClass().addAll("scrumBoardStoryList");
        taskName.setStyle("-fx-text-fill: black;" + "-fx-font-size: 110%;");
        taskName.setAlignment(Pos.CENTER_LEFT);

        vBox.getChildren().addAll(taskName, editButton, buttonsHBox);
        vBox.setPadding(new Insets(yInsets, xInsets, yInsets, xInsets));
        ///taskName.setPrefWidth(ScrumBoardStoryCellFactory.columnWidth);
        taskName.prefWidthProperty().bind(ScrumBoardStoryCellFactory.columnWidthProperty);

        setGraphic(vBox);
        //setPrefWidth(ScrumBoardStoryCellFactory.columnWidth - 25);
        prefWidthProperty().bind(ScrumBoardStoryCellFactory.columnWidthProperty.subtract(25));

        getStyleClass().add("taskLabel");


    }

}
