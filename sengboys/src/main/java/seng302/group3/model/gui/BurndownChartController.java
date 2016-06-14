package seng302.group3.model.gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import org.mockito.cglib.core.Local;
import seng302.group3.controller.App;
import seng302.group3.model.DateAxis;
import seng302.group3.model.Organisation;
import seng302.group3.model.Sprint;
import seng302.group3.model.TimePeriod;
import seng302.group3.model.navigation.navigator_items.SprintNavigatorItem;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by jwc78 on 3/08/15.
 */
public class BurndownChartController {

    Organisation organisation;

    @FXML LineChart burndown;
    @FXML ComboBox BurndownSprint;
    @FXML CheckBox burnupToggle;
    @FXML DateAxis dateAxis;
    @FXML NumberAxis hourAxis;
    @FXML Button velocityLabel;


    private XYChart.Series<Date, Double> totalHoursSeries = new XYChart.Series<Date, Double>();
    private XYChart.Series<Date, Double> estimateSeries = new XYChart.Series<Date, Double>();
    private XYChart.Series<Date, Double> velocitySeries = new XYChart.Series<Date, Double>();
    private XYChart.Series<Date, Double> projectedVelocitySeries = new XYChart.Series<Date, Double>();

    public String period = "WEEK";
    public boolean provisional = false;

    private Double lastTotalHours = 0.0;
    private Double lastTotalEstimate = 0.0;

    public BurndownChartController(){
        this.organisation = App.getMainController().currentOrganisation;
    }

    /**
     * initializes the BurndownChart with a specific Sprint
     */
    public void initialize() {

        BurndownSprint.valueProperty().addListener(SprintComboChangeListener);
        burnupToggle.selectedProperty().addListener(burnupToggleListener);

        velocityLabel.setOnAction(event -> {
            Object selection = BurndownSprint.getSelectionModel().getSelectedItem();
            if (selection instanceof SprintNavigatorItem) {
                Sprint sprint = ((SprintNavigatorItem) selection).getItem();
                if (period == "WEEK") {
                    period = "DAY";
                }
                else if (period == "DAY") {
                    period = "SPRINT";
                }
                else if (period == "SPRINT") {
                    period = "WEEK";
                }
                if (sprint.getDates().getEndDate().isAfter(LocalDate.now())) {
                    provisional = true;
                }
                else {
                    provisional = false;
                }
                sprint.displaySprintVelocity(period, provisional);
            }
        });


        burnupToggle.setSelected(true);
        if(organisation != null) {
            BurndownSprint.setItems(organisation.getSprints().getObservableList());
        }
        if(BurndownSprint.getItems().size() > 0) {
            BurndownSprint.getSelectionModel().select(0);
            Object selection = BurndownSprint.getSelectionModel().getSelectedItem();
            Sprint sprint = ((SprintNavigatorItem) selection).getItem();
            burndown.getData().clear();
            totalHoursSeries =  new XYChart.Series<Date, Double>();
            estimateSeries =  new XYChart.Series<Date, Double>();
            velocitySeries = new XYChart.Series<Date, Double>();
            projectedVelocitySeries = new XYChart.Series<Date, Double>();
            populateLoggedHours(sprint);
            populateEstimates(sprint);
            populateVelocity(sprint);
            velocitySeries.getData().add(new XYChart.Data(Date.from(
                sprint.getDates().getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                0.0));
            totalHoursSeries.setName(sprint.getFullName() + " Total Hours");
            estimateSeries.setName(sprint.getFullName() + " Burndown");
            velocitySeries.setName(sprint.getFullName() + " Velocity");
            projectedVelocitySeries.setName(sprint.getFullName() + " Projected Velocity");
            burndown.getData().add(velocitySeries);
            burndown.getData().add(estimateSeries);
            burndown.getData().add(totalHoursSeries);
            burndown.getData().add(projectedVelocitySeries);
            if (sprint.getDates().getEndDate().isAfter(LocalDate.now())) {
                provisional = true;
            }
            else {
                provisional = false;
            }
            sprint.displaySprintVelocity(period, provisional);
            DecimalFormat df = new DecimalFormat("#.0");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            for (XYChart.Series<Date, Double> series : (ObservableList<XYChart.Series>) burndown.getData()) {
                for (XYChart.Data<Date, Double> data : series.getData()) {
                    Tooltip.install(data.getNode(), new Tooltip(formatter.format(data.getXValue().toInstant().atZone(
                        ZoneId.systemDefault()).toLocalDate()) + ", " + df.format(
                        data.getYValue())));
                }
            }
        }
        else {
            BurndownSprint.getSelectionModel().select("None");
            burndown.getData().clear();
            burndown.setTitle("");

            if(BurndownChart.getInstance().getController() != null){
                BurndownChart.getInstance().getController().setVelocityLabel(
                    "Sprint Velocity: No Sprint Selected");
            }
        }
    }

    /**
     * method to refresh certain elements of the BurndownChart
     */
    public void changeRefresh() {
        this.organisation = App.getMainController().currentOrganisation;

        if (organisation != null) {
            refreshSprintSelection();
        }
    }

    /**
     * Is called by changeRefresh to update the Scrum Board to the sprint selection showing the current
     * state of the program
     */
    private void refreshSprintSelection(){
        Object originalSelection = BurndownSprint.getSelectionModel().getSelectedItem();
        BurndownSprint.setItems(organisation.getSprints().getObservableList());
        BurndownSprint.getSelectionModel().select("None");


        if(originalSelection instanceof SprintNavigatorItem){
            LocalDateTime originalDate = ((SprintNavigatorItem) originalSelection).getItem().getDateCreated();

            for(Object o : BurndownSprint.getItems()){
                if(o instanceof SprintNavigatorItem){
                    if(originalDate.equals(((SprintNavigatorItem) o).getItem().getDateCreated())){
                        BurndownSprint.getSelectionModel().select(o);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Populates the data series for the burnup part of the chart
     * @param sprint
     */
    private void populateLoggedHours(Sprint sprint) {
        TimePeriod sprintDates = sprint.getDates();
        LocalDate sprintStart = sprintDates.getStartDate();
        LocalDate currentDay = sprintStart;
        LocalDate sprintEnd = sprintDates.getEndDate();
        Double totalhours = 0.0;
        hourAxis.setUpperBound(totalhours + 2.0);
        hourAxis.setLowerBound(0.0);
        while (!currentDay.equals(sprintEnd.plusDays(1))) {
            totalhours += sprint.getHoursOnThisDay(currentDay);
            lastTotalHours = totalhours;
            if (currentDay.isBefore(LocalDate.now()) || currentDay.isEqual(LocalDate.now())){
                totalHoursSeries.getData().add(new XYChart.Data(Date.from(currentDay.atStartOfDay(ZoneId.systemDefault()).toInstant()), totalhours));
            }
            currentDay = currentDay.plusDays(1);
        }
    }

    /**
     * Populates the burndown using the time spent so far removed from the task estimates sum
     * @param sprint
     */
    private void populateEstimates(Sprint sprint) {
        TimePeriod sprintDates = sprint.getDates();
        LocalDate sprintStart = sprintDates.getStartDate();
        LocalDate currentDay = sprintStart;
        LocalDate sprintEnd = sprintDates.getEndDate();
        Double estimateTotalHours = sprint.getSprintEstimateMinutes()/60;
        hourAxis.setUpperBound((estimateTotalHours)+2.0);
        hourAxis.setLowerBound(0.0);
        velocitySeries.getData().add(new XYChart.Data(Date.from(currentDay.atStartOfDay(ZoneId.systemDefault()).toInstant()), estimateTotalHours));
        while (!currentDay.equals(sprintEnd.plusDays(1))) {
            estimateTotalHours -= sprint.getHoursOnThisDay(currentDay);
            if (currentDay.isBefore(LocalDate.now()) || currentDay.isEqual(LocalDate.now())){
                if (estimateTotalHours < 0.0) {
                    estimateSeries.getData().add(new XYChart.Data(Date.from(currentDay.atStartOfDay(ZoneId.systemDefault()).toInstant()), 0.0));
                }
                else {
                    estimateSeries.getData().add(new XYChart.Data(Date.from(currentDay.atStartOfDay(ZoneId.systemDefault()).toInstant()), estimateTotalHours));
                    lastTotalEstimate = estimateTotalHours;
                }
            }
            currentDay = currentDay.plusDays(1);
        }
    }

    private void populateVelocity(Sprint sprint) {
        TimePeriod sprintDates = sprint.getDates();
        LocalDate sprintStart = sprintDates.getStartDate();
        LocalDate currentDay = sprintStart;
        LocalDate today = LocalDate.now();
        LocalDate sprintEnd = sprintDates.getEndDate();
        Double estimateTotalHours = sprint.getSprintEstimateMinutes()/60;
        Period duration = LocalDate.from(sprintStart).until(LocalDate.from(sprintEnd));
        Period durationTillPresent = LocalDate.from(sprintStart).until(LocalDate.from(today));
        int days = duration.getDays();
        int daysSinceStart = durationTillPresent.getDays();
        Double m = (lastTotalEstimate-estimateTotalHours)/daysSinceStart;
        Double c = estimateTotalHours;
        Double xIntercept = c/m;
        if (xIntercept == Double.NEGATIVE_INFINITY || xIntercept == Double.POSITIVE_INFINITY){
            xIntercept = 0.0;
        }
        //Double daysSinceStart = -xIntercept;
        //LocalDate expectedCompletionDate = sprintStart.plusDays(daysSinceStart.intValue());
        //Double hourVelocityPerDay = sprint.getHoursPerDay();
        //Double value = lastTotalHours;
        Double endYValue = m*(days) + c;
        //if (hourVelocityPerDay > 0.0) {
        if (sprintEnd.isAfter(today)) {
            projectedVelocitySeries.getData().add(new XYChart.Data(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    lastTotalEstimate));
            projectedVelocitySeries.getData().add(new XYChart.Data(
                Date.from(sprintEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()), endYValue));
        }

            /*while (!currentDay.equals(sprintEnd.plusDays(1))) {
                if (value >= 0.0) {
                    projectedVelocitySeries.getData().add(new XYChart.Data(Date.from(currentDay.atStartOfDay(ZoneId.systemDefault()).toInstant()), value));
                }
                value = value - hourVelocityPerDay;
                currentDay = currentDay.plusDays(1);
            }*/
        //}
    }

    /**
     * Called by the change listeners to refresh the burndown and display new information
     */
    public void refreshBurndown() {
        Object selection = BurndownSprint.getSelectionModel().getSelectedItem();
        if (selection instanceof SprintNavigatorItem) {
            Sprint sprint = ((SprintNavigatorItem) selection).getItem();
            burndown.getData().clear();
            totalHoursSeries =  new XYChart.Series<Date, Double>();
            estimateSeries =  new XYChart.Series<Date, Double>();
            velocitySeries = new XYChart.Series<Date, Double>();
            projectedVelocitySeries = new XYChart.Series<Date, Double>();
            populateLoggedHours(sprint);
            populateEstimates(sprint);
            populateVelocity(sprint);
            burndown.setTitle(sprint.getFullName());
            velocitySeries.getData().add(new XYChart.Data(Date.from(
                sprint.getDates().getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                0.0));
            totalHoursSeries.setName(sprint.getFullName() + " Total Hours");
            estimateSeries.setName(sprint.getFullName() + " Burndown");
            velocitySeries.setName(sprint.getFullName() + " Velocity");
            projectedVelocitySeries.setName(sprint.getFullName() + " Projected Velocity");
            burndown.getData().add(velocitySeries);
            burndown.getData().add(estimateSeries);
            if (burnupToggle.isSelected() == true) {
                burndown.getData().add(totalHoursSeries);
                burndown.getData().add(projectedVelocitySeries);
            }
            if (sprint.getDates().getEndDate().isAfter(LocalDate.now())) {
                provisional = true;
            }
            else {
                provisional = false;
            }
            sprint.displaySprintVelocity(period, provisional);

            DecimalFormat df = new DecimalFormat("#.0");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy");
            for (XYChart.Series<Date, Double> series : (ObservableList<XYChart.Series>) burndown.getData()) {
                for (XYChart.Data<Date, Double> data : series.getData()) {
                    Tooltip.install(data.getNode(), new Tooltip(formatter.format(data.getXValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) + ", " + df.format(
                        data.getYValue())));
                }
            }
        }
        else {
            BurndownSprint.getSelectionModel().select("None");
            burndown.getData().clear();
            burndown.setTitle("");
        }
    }

    /*class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(int priorValue, int value) {
            setPrefSize(15, 15);

            final Label label = new Label("hello");//createDataThresholdLabel(priorValue, value);

            setOnMouseEntered(mouseEvent -> {
                getChildren().setAll(label);
                setCursor(Cursor.NONE);
                toFront();
            });
            setOnMouseExited(mouseEvent -> {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            });
        }

        /*private Label createDataThresholdLabel(int priorValue, int value) {
            final Label label = new Label(value + "");
            label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

            if (priorValue == 0) {
                label.setTextFill(Color.DARKGRAY);
            } else if (value > priorValue) {
                label.setTextFill(Color.FORESTGREEN);
            } else {
                label.setTextFill(Color.FIREBRICK);
            }

            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }*/

    /**
     * Change listener for the Sprint Selection Combo Box
     */
    ChangeListener SprintComboChangeListener = (observable, oldValue, newValue) -> refreshBurndown();

    /**
     * Change listener for the burnup toggle checkbox
     */
    ChangeListener burnupToggleListener = (observable, oldValue, newValue) -> refreshBurndown();


    public void setVelocityLabel(String string) {
        velocityLabel.textProperty().setValue(string);
    }
}
