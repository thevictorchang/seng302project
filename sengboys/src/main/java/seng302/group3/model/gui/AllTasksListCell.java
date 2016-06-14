package seng302.group3.model.gui;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import seng302.group3.controller.App;
import seng302.group3.model.*;
import seng302.group3.model.io.Editor;
import seng302.group3.model.navigation.NavigatorItem;
import seng302.group3.model.navigation.navigator_items.*;

import java.awt.*;
import java.util.Set;

/**
 * Created by cjm328 on 19/09/15.
 */
public class AllTasksListCell<T> extends ListCell<T> {
    final Image chevronDown = new Image(getClass().getResourceAsStream("/imgs/chevron_down.png"));
    final Image chevronRight = new Image(getClass().getResourceAsStream("/imgs/chevron_right.png"));
    ListView listView;
    AllTasksController allTasksController;

    public AllTasksListCell(ListView listView, AllTasksController allTasksController) {
        this.listView = listView;
        this.allTasksController = allTasksController;
    }

    /**
     * calls the Drag list cell create factory and gives the cell factory to the list view
     *
     * @param listView - list view we want to add the factory to
     */
    public static void createFactory(ListView listView, AllTasksController allTasksController) {
        listView.setCellFactory(param ->
                        new AllTasksListCell(listView, allTasksController)
        );
    }

    /**
     * The formatter for the cells in a story. Builds the cells dropdown, text and highlighting.
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            super.updateItem(item, empty);
        } else if (item instanceof NavigatorItem) {

            NavigatorItem navigatorItem = (NavigatorItem) item;
            Element elem = (Element) navigatorItem.getItem();
            Organisation org = App.getMainController().currentOrganisation;


            if (elem instanceof Task) {
                Task task = (Task) elem;

                GridPane grid = getTaskGrid(task, allTasksController);

                //setText(item.toString() + " - " + task.getAssignedPeople().size());
                setGraphic(grid);
                super.updateItem(item, false);

                this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            if (navigatorItem.getItem() != null) {

                                NavigatorItem navigatorItem = null;
                                org.getNavigator().homePress();
                                if (elem instanceof Story) {
                                    navigatorItem = new StoryNavigatorItem((Story) elem);
                                    FolderNavigatorItem foldItem = new FolderNavigatorItem("Stories");
                                    foldItem.setNext(org.getStories());
                                    org.getNavigator().updateToSelection(foldItem);
                                    org.getNavigator().updateToSelection(navigatorItem);
                                } else if (elem instanceof Task) {
                                    navigatorItem = new TaskNavigatorItem((Task) elem);
                                    FolderNavigatorItem foldItem = new FolderNavigatorItem("Tasks");
                                    foldItem.setNext(org.getTasks());
                                    org.getNavigator().updateToSelection(foldItem);
                                    org.getNavigator().updateToSelection(navigatorItem);
                                }
                                //dialog.close();
                            }
                        }
                    }
                });
            } else if (elem instanceof Story) {
                Story story = (Story) elem;

                GridPane gridPane = getStoryGrid(story,allTasksController);

                setGraphic(gridPane);
                setText(null);
                super.updateItem(item, empty);
                this.setOnMouseClicked(event -> {
                    if (allTasksController.storyShown.get(story)) {
                        allTasksController.storyShown.put(story, Boolean.FALSE);
                    } else{
                        allTasksController.storyShown.put(story, Boolean.TRUE);
                    }
                    allTasksController.changeRefresh();
                });
            }
        }
    }

    /**
     * Gets the grid for the cell when the cell contains a task.
     * @param task
     * @param allTasksController
     * @return
     */
    private GridPane getTaskGrid(Task task, AllTasksController allTasksController) {

        //Name, Assigned People, Status, Estimate, Total Hours, logging hours, impediements

        GridPane grid = new GridPane();
        grid.setHgap(5);

        Button textButton = new Button();
        Button peopleButton = new Button();
        Button estimateButton = new Button();



        Button taskTypeButton = new Button();

        textButton.setText(task.getShortName());
        peopleButton.setText("" + task.getAssignedPeople().size());
        estimateButton.setText("" + task.getCurrentEstimate().getValue() / 60.0);
        taskTypeButton.setText(task.getTaskType().toString());

        double totalHours = 0;
        for(LoggedTime loggedTime : task.getLoggedHours()){
            totalHours += loggedTime.getLoggedTimeInHours();
        }

        Button logTimeButton = new Button();
        logTimeButton.setText(""+totalHours);

        logTimeButton.getStyleClass().add("okayButton");
        textButton.getStyleClass().add("okayButton");
        peopleButton.getStyleClass().add("okayButton");
        estimateButton.getStyleClass().add("okayButton");
        taskTypeButton.getStyleClass().add("okayButton");




        if(task.getAssignedPeople().size() == 0){
            peopleButton.getStyleClass().remove("okayButton");
            peopleButton.getStyleClass().add("noPeopleButton");
        }else{
            peopleButton.getStyleClass().remove("noPeopleButton");
            peopleButton.getStyleClass().add("okayButton");
        }


        Rectangle box = new Rectangle(5,30);
        if(task.getProgressStatus().equals(Task.Status.COMPLETED))
            box.setFill(javafx.scene.paint.Paint.valueOf("Green"));
        else if(task.getProgressStatus().equals(Task.Status.IN_PROGRESS))
            box.setFill(javafx.scene.paint.Paint.valueOf("Orange"));
        else
            box.setFill(javafx.scene.paint.Paint.valueOf("Red"));


        grid.getChildren().addAll(box, textButton,peopleButton,estimateButton,logTimeButton);

        if(allTasksController.showTaskTypeCheck.selectedProperty().get()){
            HBox taskTypeBox = new HBox();
            Rectangle boxTaskType = new Rectangle(5,30);
            if(task.getTaskType().equals(Task.Type.UNSPECIFIED)){
                boxTaskType.setFill(javafx.scene.paint.Paint.valueOf("#212121"));
            } else if(task.getTaskType().equals(Task.Type.DEVELOPMENT)){
                boxTaskType.setFill(javafx.scene.paint.Paint.valueOf("#616161"));
            } else if(task.getTaskType().equals(Task.Type.TESTING)){
                boxTaskType.setFill(javafx.scene.paint.Paint.valueOf("#9e9e9e"));
            } else if(task.getTaskType().equals(Task.Type.DESIGN)){
                boxTaskType.setFill(javafx.scene.paint.Paint.valueOf("#e0e0e0"));
            }
            taskTypeBox.getChildren().addAll(boxTaskType,taskTypeButton);
            grid.getChildren().add(taskTypeBox);
            GridPane.setConstraints(taskTypeBox,5,0);
        }else{
            grid.getChildren().add(taskTypeButton);
            GridPane.setConstraints(taskTypeButton,5,0);
        }

        GridPane.setConstraints(box,0,0);
        GridPane.setConstraints(textButton, 1, 0);
        GridPane.setConstraints(peopleButton, 2, 0);
        GridPane.setConstraints(estimateButton,3,0);
        GridPane.setConstraints(logTimeButton,4,0);


        ColumnConstraints c0 = new ColumnConstraints();
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        ColumnConstraints c3 = new ColumnConstraints();
        ColumnConstraints c4 = new ColumnConstraints();
        ColumnConstraints c5 = new ColumnConstraints();

        c0.setPercentWidth(2);
        c1.setPercentWidth(27);
        c2.setPercentWidth(22);
        c3.setPercentWidth(17);
        c4.setPercentWidth(16);
        c5.setPercentWidth(17);


        c0.setMaxWidth(5);
        c1.setMaxWidth(265);
        c2.setMaxWidth(240);
        c3.setMaxWidth(165);
        c4.setMaxWidth(140);
        c5.setMaxWidth(165);

        grid.getColumnConstraints().addAll(c0,c1,c2,c3,c4,c5);


        logTimeButton.setOnAction((event) -> {
            App.getMainController().newTimeLogButton(task);
        });
        textButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, task);
        });
        peopleButton.setOnAction((event) -> {
            Sprint sprint =
                (Sprint) ((SprintNavigatorItem) allTasksController.SprintComboBox.getSelectionModel().getSelectedItem()).getItem();
            new Editor().assignPeopleToTask(task, sprint);
        });
        estimateButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, task);
        });
        taskTypeButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, task);
        });

        return grid;
    }


    /**
     * Gets the grid for the cell when the cell contains a story item.
     * @param story
     * @param allTasksController
     * @return
     */
    private GridPane getStoryGrid(Story story, AllTasksController allTasksController) {


        GridPane grid = new GridPane();
        grid.setHgap(5);

        Button labelButton = new Button();

        final ImageView chev = new ImageView(chevronRight);

        labelButton.getStyleClass().add("okayButton");
        labelButton.setText(story.toString());
        labelButton.setStyle("-fx-font-weight: bolder");

        if ((allTasksController.storyShown.get(story) != null) && !(allTasksController.storyShown.get(story))) {
            chev.setImage(chevronRight);
        }
        else{
            chev.setImage(chevronDown);
        }
        HBox hBox = new HBox();
        hBox.getChildren().addAll(chev,labelButton);


        if(allTasksController.showTaskTypeCheck.selectedProperty().get()){

            Integer unspecifiedCount = 0;
            Integer developmentCount = 0;
            Integer testingCount = 0;
            Integer designCount = 0;
            for(Task task : story.getTasks()){
                if(allTasksController.showAllocatedCheck.isSelected()){
                    if(task.getTaskType().equals(Task.Type.UNSPECIFIED)){
                        unspecifiedCount += 1;
                    } else if(task.getTaskType().equals(Task.Type.DEVELOPMENT)){
                        developmentCount += 1;
                    } else if(task.getTaskType().equals(Task.Type.TESTING)){
                        testingCount += 1;
                    } else if(task.getTaskType().equals(Task.Type.DESIGN)){
                        designCount += 1;
                    }
                }else{
                    if (task.getAssignedPeople().size() == 0){
                        if(task.getTaskType().equals(Task.Type.UNSPECIFIED)){
                            unspecifiedCount += 1;
                        } else if(task.getTaskType().equals(Task.Type.DEVELOPMENT)){
                            developmentCount += 1;
                        } else if(task.getTaskType().equals(Task.Type.TESTING)){
                            testingCount += 1;
                        } else if(task.getTaskType().equals(Task.Type.DESIGN)){
                            designCount += 1;
                        }
                    }
                }

            }
            Double totalCount = 0.0 + unspecifiedCount + designCount + developmentCount + testingCount;

            HBox r1 = new HBox();
            r1.setStyle("-fx-border-color: #212121");
            r1.setStyle("-fx-background-color: #212121");
            HBox r2 = new HBox();
            r2.setStyle("-fx-border-color: #616161");
            r2.setStyle("-fx-background-color: #616161");
            HBox r3 = new HBox();
            r3.setStyle("-fx-border-color: #9e9e9e");
            r3.setStyle("-fx-background-color: #9e9e9e");
            HBox r4 = new HBox();
            r4.setStyle("-fx-border-color: #e0e0e0");
            r4.setStyle("-fx-background-color: #e0e0e0");

            Tooltip tooltip1 = new Tooltip("Unspecified");
            Tooltip.install(r1,tooltip1);
            Tooltip tooltip2 = new Tooltip("Development");
            Tooltip.install(r2,tooltip2);
            Tooltip tooltip3 = new Tooltip("Testing");
            Tooltip.install(r3,tooltip3);
            Tooltip tooltip4 = new Tooltip("Design");
            Tooltip.install(r4,tooltip4);

            grid.getChildren().addAll(r1, r2, r3, r4);

            r1.setPrefHeight(4);
            r2.setPrefHeight(4);
            r3.setPrefHeight(4);
            r4.setPrefHeight(4);

            GridPane.setConstraints(r1, 0, 1);
            GridPane.setConstraints(r2, 1, 1);
            GridPane.setConstraints(r3, 2, 1);
            GridPane.setConstraints(r4, 3, 1);

            ColumnConstraints d1 = new ColumnConstraints();
            ColumnConstraints d2 = new ColumnConstraints();
            ColumnConstraints d3 = new ColumnConstraints();
            ColumnConstraints d4 = new ColumnConstraints();

            d1.setPercentWidth((unspecifiedCount / totalCount) * 100);
            d2.setPercentWidth((developmentCount / totalCount) * 100);
            d3.setPercentWidth((testingCount / totalCount) * 100);
            d4.setPercentWidth((designCount / totalCount) * 100);

            grid.getColumnConstraints().addAll(d1,d2,d3,d4);


            grid.getChildren().add(hBox);
            GridPane.setConstraints(hBox, 0, 0);
            GridPane.setColumnSpan(hBox,4);

            ColumnConstraints c0 = new ColumnConstraints();

            c0.setPercentWidth(100);

            grid.getColumnConstraints().addAll(c0);

        }else{

            grid.getChildren().add(hBox);
            ColumnConstraints c0 = new ColumnConstraints();

            GridPane.setConstraints(hBox,0,0);

            c0.setPercentWidth(100);

            grid.getColumnConstraints().addAll(c0);
        }


        labelButton.setOnAction((event) -> {
            new Editor().edit(App.getMainController().currentOrganisation, story);
        });

        return grid;
    }



}
