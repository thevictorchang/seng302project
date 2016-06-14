package seng302.group3.model.gui.scrum_board;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import seng302.group3.controller.App;
import seng302.group3.model.Backlog;
import seng302.group3.model.Story;
import seng302.group3.model.Task;
import seng302.group3.model.io.property_sheets.BacklogSheet;

/**
 * Created by ntr24 on 8/08/15.
 */
public class ScrumBoardMetaData {
    public static enum Visibility {COLLAPSED, EXPANDED}

    private Visibility visibility = Visibility.COLLAPSED;
    private PieChart pieChart;
    private PieChart.Data todo = new PieChart.Data("To Do", 0);
    private PieChart.Data inProg = new PieChart.Data("In Progress", 0);
    private PieChart.Data done = new PieChart.Data("Done", 0);
    private String storySize = "0";
    StackPane pieBox = new StackPane();
    Label pieLabel = new Label();

    public ScrumBoardMetaData(){
        pieChart = new PieChart();
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(todo, inProg, done);
        pieChart.setData(pieChartData);
        pieChart.setAnimated(true);

        pieChart.setPrefSize((ScrumBoardStoryCellFactory.columnWidth-80)* App.controller.widthScale(), (ScrumBoardStoryCellFactory.columnWidth-80)* App.controller.widthScale());
        pieBox.setPrefSize((ScrumBoardStoryCellFactory.columnWidth - 80) * App.controller.widthScale(), (ScrumBoardStoryCellFactory.columnWidth - 80) * App.controller.widthScale());
        pieLabel.setPrefSize((ScrumBoardStoryCellFactory.columnWidth - 80) * App.controller.widthScale(), (ScrumBoardStoryCellFactory.columnWidth - 80) * App.controller.widthScale());
        pieLabel.setContentDisplay(ContentDisplay.CENTER);
        pieLabel.setTextAlignment(TextAlignment.CENTER);
        pieLabel.setStyle("-fx-font-size: 500%; -fx-alignment: center; -fx-text-fill: black");
        pieChart.labelsVisibleProperty().setValue(false);
        pieChart.legendVisibleProperty().setValue(false);


    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void updatePieChart(Story story){
        int toDoNum = 0;
        int inProgNum = 0;
        int doneNum = 0;
        for(Task t: story.getTasks()) {
            switch (t.getProgressStatus()) {
                case READY:
                    toDoNum = toDoNum + t.getEstimates().get(0).getValue().intValue();
                    break;
                case COMPLETED:
                    doneNum = doneNum + t.getEstimates().get(0).getValue().intValue();
                    break;
                case IN_PROGRESS:
                    inProgNum = inProgNum + t.getEstimates().get(0).getValue().intValue();
                    break;
                default:
                    toDoNum = toDoNum + t.getEstimates().get(0).getValue().intValue();
                    break;
            }
        }

        todo.setPieValue(toDoNum);
        inProg.setPieValue(inProgNum);
        done.setPieValue(doneNum);

        switch(story.getBacklog().getScale()){
            case BacklogSheet.FIBONACCI:
                storySize = Backlog.getScaleValue(story.getEstimate(), BacklogSheet.fibonacci);
                break;
            case BacklogSheet.TSHIRTS:
                storySize = Backlog.getScaleValue(story.getEstimate(), BacklogSheet.tShirts);
                break;
            case BacklogSheet.NATURAL:
                storySize = Backlog.getScaleValue(story.getEstimate(), BacklogSheet.natural);
                break;
            default:
                storySize = Backlog.getScaleValue(story.getEstimate(), BacklogSheet.fibonacci);
                break;
        }
        pieLabel.setText(storySize);
    }

    public StackPane getPieChart() {
        pieBox.getChildren().clear();
        pieBox.getChildren().addAll(pieChart, pieLabel);
        return pieBox;
    }

    public double getlistHeight(Story story){
        double toDoNum = 0;
        double inProgNum = 0;
        double doneNum = 0;
        for(Task t: story.getTasks()) {
            switch (t.getProgressStatus()) {
                case READY:
                    toDoNum++;
                    break;
                case COMPLETED:
                    doneNum++;
                    break;
                case IN_PROGRESS:
                    inProgNum++;
                    break;
                default:
                    toDoNum++;
                    break;
            }
        }
        return 70.0 * Math.max(Math.max(toDoNum, inProgNum), doneNum) + 70.0;
    }
}
