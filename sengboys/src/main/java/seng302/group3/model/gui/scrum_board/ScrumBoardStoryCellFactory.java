package seng302.group3.model.gui.scrum_board;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import seng302.group3.controller.App;
import seng302.group3.controller.MainController;
import seng302.group3.model.Sprint;
import seng302.group3.model.Story;
import seng302.group3.model.Task;
import seng302.group3.model.gui.TaskListCell;
import seng302.group3.model.io.Dialogs;
import seng302.group3.model.io.Editor;
import seng302.group3.model.navigation.navigator_items.StoryNavigatorItem;

/**
 * Created by ntr24 on 1/08/15.
 */
public class ScrumBoardStoryCellFactory<T> extends ListCell<T>{

    //public static double sizeRation = Screen.getPrimary().getVisualBounds().getMaxX()/1920;
    public static int columnWidth = 300;
    public static DoubleProperty columnWidthProperty = new SimpleDoubleProperty(300.0);
    private static int collapseButtonWidth = 60;
    private ScrumBoardController scrumBoardController;

    public ScrumBoardStoryCellFactory(ScrumBoardController scrumBoardController){
        this.scrumBoardController = scrumBoardController;
    }

    /**
     * This returns the title row of the scrum board. Including the to do, in progress and done labels
     * @return - title row node
     */
    private Node createTitleRow(){
        HBox hBox = new HBox();
        Label storiesLabel = new Label("Stories");
        storiesLabel.prefWidthProperty().bind(columnWidthProperty);
        storiesLabel.setAlignment(Pos.TOP_CENTER);
        storiesLabel.setStyle("-fx-text-fill: black;" + "-fx-font-size: 150%;");

        Label todoLabel = new Label("To Do");
        todoLabel.prefWidthProperty().bind(columnWidthProperty);
        todoLabel.setAlignment(Pos.TOP_CENTER);
        todoLabel.setStyle("-fx-text-fill: black;" + "-fx-font-size: 150%;" + "-fx-background-color: #E26435");


        Label inProgressLabel = new Label("In Progress");
        inProgressLabel.prefWidthProperty().bind(columnWidthProperty);
        inProgressLabel.setAlignment(Pos.TOP_CENTER);
        inProgressLabel.setStyle("-fx-text-fill: black;" + "-fx-font-size: 150%;" + "-fx-background-color: #E79B22");

        Label doneLabel = new Label("Done");
        doneLabel.prefWidthProperty().bind(columnWidthProperty);
        doneLabel.setAlignment(Pos.TOP_CENTER);
        doneLabel.setStyle("-fx-text-fill: black;"+"-fx-font-size: 150%;"+"-fx-background-color: #53A953");

        hBox.getChildren().addAll(storiesLabel, todoLabel, inProgressLabel, doneLabel);
        return hBox;
    }

    /**
     * Returns a story row as an expanded row with tasks and pie chart representation
     * @param story - story to display
     * @return - the expanded story row
     */
    private Node expandedStoryRow(Story story){
        HBox wholeRow = new HBox();
        VBox storyBox = new VBox();
        wholeRow.getStyleClass().addAll("scrumBoardStoryList");

        ScrollPane tasksScroll = new ScrollPane();
        HBox tasksBox = new HBox();
        ListView toDo = new ListView();
        toDo.getStyleClass().addAll("scrumBoardTaskList");
        TaskListCell.createFactory(toDo, Task.Status.READY, story, scrumBoardController);

        ListView inProg = new ListView();
        inProg.getStyleClass().addAll("scrumBoardTaskList");
        TaskListCell.createFactory(inProg, Task.Status.IN_PROGRESS, story, scrumBoardController);

        ListView done = new ListView();
        done.getStyleClass().addAll("scrumBoardTaskList");
        TaskListCell.createFactory(done, Task.Status.COMPLETED, story, scrumBoardController);

        ScrumBoardMetaData metaData = scrumBoardController.metaDataMap.get(story.getDateCreated());

        Label storyLabel = new Label(story.toString());
        storyLabel.maxWidthProperty().bind(columnWidthProperty);
        storyLabel.prefWidthProperty().bind(columnWidthProperty.subtract(collapseButtonWidth));
        storyLabel.wrapTextProperty().setValue(true);
        storyLabel.setAlignment(Pos.TOP_CENTER);
        storyLabel.getStyleClass().addAll("scrumBoardStoryList");
        storyLabel.setStyle("-fx-text-fill: black;" + "-fx-font-size: 150%;");

        Button addTaskButton = new Button("Add Task");
        addTaskButton.setOnAction((event) -> {
            Task task = new Task("", "");
            if (new Editor().CreateTask(task, App.controller.currentOrganisation)) {
                task.setObjectType(story);
                story.addTask(task);
                App.getMainController().addHistory(App.getMainController().currentOrganisation.serializedCopy(),
                        "Added Task '" + task.getShortName() + "'");

            }
        });
        addTaskButton.getStyleClass().addAll("addTaskButton");
        ///addTaskButton.setPrefSize(columnWidth/2 -10 , 40);
        addTaskButton.prefWidthProperty().bind(columnWidthProperty.divide(2).subtract(10));
        addTaskButton.setPrefHeight(40);

        ComboBox storyStatus = new ComboBox();
        storyStatus.getItems().addAll(Story.StoryStatus.NOT_STARTED, Story.StoryStatus.IN_PROGRESS, Story.StoryStatus.DONE);
        storyStatus.getSelectionModel().select(story.getStoryStatus());
        storyStatus.getStyleClass().addAll("scrumBoardStoryStatusCombo");

        ///storyStatus.setPrefSize(columnWidth/2 -10 , 40);
        storyStatus.prefWidthProperty().bind(columnWidthProperty.divide(2).subtract(10));
        storyStatus.setPrefHeight(40);

        storyStatus.setOnAction((event) -> {
            Story.StoryStatus prevState = story.getStoryStatus();
            if(storyStatus.getSelectionModel().getSelectedItem() != prevState){
                if(storyStatus.getSelectionModel().getSelectedItem() == Story.StoryStatus.DONE){
                    if(inProg.getItems().isEmpty() && toDo.getItems().isEmpty()){
                        story.setStoryStatus((Story.StoryStatus) storyStatus.getSelectionModel().getSelectedItem());
                        App.getMainController().addHistory(App.getMainController().currentOrganisation.serializedCopy(),
                                "Set '" + story.getShortName() + "' to Done");
                    }
                    else{
                        if(new Dialogs().storyToDoneDialog(story)){
                            story.setStoryStatus((Story.StoryStatus) storyStatus.getSelectionModel().getSelectedItem());
                            App.getMainController().addHistory(App.getMainController().currentOrganisation.serializedCopy(),
                                    "Set '" + story.getShortName() + "' and it's tasks to Done");
                        }else {

                            storyStatus.getSelectionModel().select(prevState);
                            scrumBoardController.changeRefresh();

                        }

                    }
                }
                else {
                    story.setStoryStatus((Story.StoryStatus) storyStatus.getSelectionModel().getSelectedItem());
                    App.getMainController().addHistory(App.getMainController().currentOrganisation.serializedCopy(),
                            "Set '" + story.getShortName() + "' and it's tasks to " + storyStatus.getSelectionModel().getSelectedItem());
                }
            }
        });

        HBox buttonRow = new HBox();
        buttonRow.setSpacing(10);
        buttonRow.getChildren().addAll(addTaskButton, storyStatus);

        storyBox.getChildren().addAll(buttonRow, metaData.getPieChart());
        storyBox.maxWidthProperty().bind(columnWidthProperty);
        storyBox.prefWidthProperty().bind(columnWidthProperty);
        ///storyBox.setMaxWidth(columnWidth);
        ///storyBox.setPrefWidth(columnWidth);

        double listHeight = metaData.getlistHeight(story);
        toDo.setMinHeight(listHeight);
        inProg.setMinHeight(listHeight);
        done.setMinHeight(listHeight);

        for(Task t: story.getTasks()) {
            switch (t.getProgressStatus()) {
                case READY:
                    toDo.getItems().add(t);
                    break;
                case COMPLETED:
                    done.getItems().add(t);
                    break;
                case IN_PROGRESS:
                    inProg.getItems().add(t);
                    break;
                default:
                    toDo.getItems().add(t);
                    break;
            }
        }

        tasksBox.getChildren().addAll(toDo, inProg, done);
        tasksBox.setStyle("-fx-background-color: #ebebeb");

        ///tasksBox.setPrefSize(columnWidth * 3, Math.max(listHeight, columnWidth + 5));
        tasksBox.prefWidthProperty().bind(columnWidthProperty.multiply(3));
        if(listHeight > columnWidthProperty.get()){
            tasksBox.prefHeightProperty().bind(new SimpleDoubleProperty(listHeight));
        }else{
            tasksBox.prefHeightProperty().bind(columnWidthProperty.add(5));
        }

        tasksScroll.prefHeightProperty().bind(columnWidthProperty);
        ///tasksScroll.setPrefHeight(columnWidth);
        tasksScroll.prefWidthProperty().bind(columnWidthProperty.multiply(3).add(20));
        ///tasksScroll.setPrefWidth(3 * columnWidth + 20);
        tasksScroll.setContent(tasksBox);
        tasksScroll.getStyleClass().addAll("scrumBoardScrollPane");

        tasksScroll.setStyle("-fx-background-color: #ebebeb");


        wholeRow.getChildren().addAll(storyBox, tasksScroll);

        Button collapseButton = new Button();
        collapseButton.setGraphic(new ImageView(ScrumBoard.chevronDown));
        collapseButton.getStyleClass().addAll("chevronstyle");
        collapseButton.setOnAction(event -> {
            metaData.setVisibility(ScrumBoardMetaData.Visibility.COLLAPSED);
            setRowGraphic(collapsedStory(story));
        });


        VBox outputRow = new VBox();
        HBox titlePart = new HBox(collapseButton, storyLabel);
        titlePart.getStyleClass().addAll("scrumBoardStoryItemGraphic");
        wholeRow.getStyleClass().addAll("scrumBoardStoryItemGraphicDetail");

        titlePart.setOnMouseClicked(event -> {
            collapseButton.fire();
        });

        outputRow.getChildren().addAll(titlePart, wholeRow);

        return outputRow;
    }


    /**
     * Returns a story row as expanded but with no tasks or pie chart
     * @param story - story to display
     * @return - the expanded story row with no tasks
     */
    private Node noTasksStoryRow(Story story){
        ScrumBoardMetaData metaData = scrumBoardController.metaDataMap.get(story.getDateCreated());

        Button collapseButton = new Button();
        collapseButton.setGraphic(new ImageView(ScrumBoard.chevronDown));
        collapseButton.getStyleClass().addAll("chevronstyle");
        collapseButton.setOnAction(event -> {
            metaData.setVisibility(ScrumBoardMetaData.Visibility.COLLAPSED);

            setRowGraphic(collapsedStory(story));
        });

        Label storyLabel = new Label(story.toString());
        storyLabel.setMaxWidth(columnWidth);
        storyLabel.setPrefWidth(columnWidth - collapseButtonWidth);
        storyLabel.wrapTextProperty().setValue(true);
        storyLabel.setAlignment(Pos.TOP_CENTER);
        storyLabel.getStyleClass().addAll("scrumBoardStoryList");
        storyLabel.setStyle("-fx-text-fill: black;"+"-fx-font-size: 150%;");

        HBox titleRow = new HBox(collapseButton, storyLabel);

        Button addTaskButton = new Button("Add Task");
        addTaskButton.setOnAction((event) -> {
            Task task = new Task("", "");
            if (new Editor().CreateTask(task, App.controller.currentOrganisation)) {
                story.addTask(task);
                App.getMainController().addHistory(App.getMainController().currentOrganisation.serializedCopy(),
                        "Added Task '" + task.getShortName() + "'");
            }
        });
        addTaskButton.setAlignment(Pos.CENTER);

        addTaskButton.setTextAlignment(TextAlignment.CENTER);
        addTaskButton.getStyleClass().addAll("addTaskButton");
        addTaskButton.setPrefSize(columnWidth/2 - 10, 40);


        Label noTasksLabel = new Label("Currently No Tasks");
        noTasksLabel.setAlignment(Pos.CENTER);
        noTasksLabel.setTextAlignment(TextAlignment.CENTER);
        noTasksLabel.setPrefWidth(columnWidth);
        noTasksLabel.setStyle("-fx-text-fill: black;" + "-fx-font-size: 150%");

        titleRow.getStyleClass().addAll("scrumBoardStoryItemGraphic");

        VBox content = new VBox(noTasksLabel, addTaskButton);
        content.getStyleClass().addAll("scrumBoardStoryItemGraphicDetail");
        content.setSpacing(5);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPrefWidth(columnWidth);

        titleRow.setOnMouseClicked(event -> {
            collapseButton.fire();
        });

        VBox storyVBox = new VBox(titleRow, content);

        return storyVBox;
    }

    /**
     * Returns a story row collapsed so it only shows the title of the story
     * @param story - story to display
     * @return - the collapsed story row
     */
    private Node collapsedStory(Story story){
        ScrumBoardMetaData metaData = scrumBoardController.metaDataMap.get(story.getDateCreated());

        Button expandButton = new Button();
        expandButton.setGraphic(new ImageView(ScrumBoard.chevronRight));
        expandButton.getStyleClass().addAll("chevronstyle");
        expandButton.setOnAction(event -> {
            metaData.setVisibility(ScrumBoardMetaData.Visibility.EXPANDED);

            if (story.getTasks().size() > 0) {
                setRowGraphic(expandedStoryRow(story));
                metaData.updatePieChart(story);
            } else {
                setRowGraphic(noTasksStoryRow(story));
            }
        });

        Label storyLabel = new Label(story.toString());
        storyLabel.setMaxWidth(columnWidth);
        storyLabel.setPrefWidth(columnWidth - collapseButtonWidth);
        storyLabel.wrapTextProperty().setValue(true);
        storyLabel.setAlignment(Pos.TOP_CENTER);
        storyLabel.getStyleClass().addAll("scrumBoardStoryList");
        storyLabel.setStyle("-fx-text-fill: black;"+"-fx-font-size: 150%;");

        HBox output = new HBox(expandButton, storyLabel);
        output.getStyleClass().add("scrumBoardStoryItemGraphic");
        output.setOnMouseClicked(event -> {
            expandButton.fire();
        });

        return output;
    }

    /**
     * sets the graphic of the row for the given node
     * @param graphic - graphic to show in the row
     */
    private void setRowGraphic(Node graphic){
        VBox row = new VBox();
        row.getChildren().addAll(graphic);
        row.setPrefWidth(columnWidth * 4 + 50);
        row.setMaxWidth(columnWidth * 4 + 50);
        setGraphic(row);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if(item == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        if(item.equals("!TITLE_ROW!")){
            setText(null);
            setGraphic(createTitleRow());
        }
        else{
            Story story = ((StoryNavigatorItem) item).getItem();


            ScrumBoardMetaData metaData = scrumBoardController.metaDataMap.get(story.getDateCreated());
            if(metaData == null){
                scrumBoardController.metaDataMap.put(story.getDateCreated(), new ScrumBoardMetaData());
                metaData = scrumBoardController.metaDataMap.get(story.getDateCreated());
            }

            switch (metaData.getVisibility()){
                case COLLAPSED:
                    setRowGraphic(collapsedStory(story));
                    break;
                case EXPANDED:
                    if(story.getTasks().size() > 0) {
                        setRowGraphic(expandedStoryRow(story));
                        metaData.updatePieChart(story);
                    }
                    else{
                        setRowGraphic(noTasksStoryRow(story));
                    }
                    break;
                default:
                    break;
            }
            setText(null);
        }
    }
}
