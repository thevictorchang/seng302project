package seng302.group3.model.io.property_sheets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import seng302.group3.model.Project;
import seng302.group3.model.Team;
import seng302.group3.model.TimePeriod;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by epa31 on 24/05/15.
 */
public class DatePickerSheet implements EditorSheet {

    @FXML DatePicker endDatePicker;
    @FXML DatePicker startDatePicker;
    VBox content = new VBox();

    private Collection<TimePeriod> currentTimePeriods = new ArrayList<>();
    private Team currentTeam;
    private boolean editing;


    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public DatePickerSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/DatePickerSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.startDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
                                LocalDate newValue) {
                endDatePicker.setDisable(false);
                runDayFactoryEnd(endDatePicker);
            }
        });

        this.endDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
                                LocalDate newValue) {
                runDayFactoryStart(startDatePicker);
            }
        });
    }

    /**
     * Runs the daycell factory on both pickers and sets the current attributes to given objects
     *
     * @param timePeriods
     * @param currentTeam
     */
    public void setupTimePeriods(Collection<TimePeriod> timePeriods, Team currentTeam) {
        this.currentTimePeriods = timePeriods;
        this.currentTeam = currentTeam;

        runDayFactoryEnd(endDatePicker);
        runDayFactoryStart(startDatePicker);

    }

    public void setupTimePeriod(TimePeriod t) {
        startDatePicker.setValue(t.getStartDate());
        endDatePicker.setValue(t.getEndDate());
    }

    /**
     * Sets up two listeners which run the correct cell factories for each date picker
     * These are fired upon selecting a date and they modify the other date picker
     *
     * @param object
     */
    @Override public void fieldConstruct(Object object) {

        editing = (boolean) object;

        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        endDatePicker.setDisable(true);




    }

    /**
     * Returns the vbox of this sheet.
     *
     * @return
     */
    @Override public VBox draw() {
        return content;
    }

    /**
     * Checks that both of the date pickers have valid dates when you press okay
     *
     * @param args
     * @return
     */
    @Override public boolean validate(Object... args) {

        boolean isValid = true;

        if (endDatePicker.getValue() == null) {
            endDatePicker.getStyleClass().add("errorBox");
            isValid = false;
        } else {
            endDatePicker.getStyleClass().removeAll("errorBox");
        }
        if (startDatePicker.getValue() == null) {
            startDatePicker.getStyleClass().add("errorBox");
            isValid = false;
        } else {
            startDatePicker.getStyleClass().removeAll("errorBox");
        }
        return isValid;
    }

    /**
     * Using the given timeperiod it sets the start and end dates
     *
     * @param object
     */
    @Override public void apply(Object object) {
        if (object instanceof TimePeriod) {
            TimePeriod timePeriod = (TimePeriod) object;
            timePeriod.setStartDate(startDatePicker.getValue());
            timePeriod.setEndDate(endDatePicker.getValue());

        }
    }

    /**
     * Runs the cell factory for the end date picker
     *
     * @param datePicker
     */
    private void runDayFactoryEnd(DatePicker datePicker) {
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override public DateCell call(DatePicker param) {
                return new dayFormatCellEnd();
            }
        });
    }

    /**
     * Runs the cell factory for the start date picker
     *
     * @param datePicker
     */
    private void runDayFactoryStart(DatePicker datePicker) {
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override public DateCell call(DatePicker param) {
                return new dayFormatCell();
            }
        });
    }

    /**
     * Provides formatting and rules to the enddatepicker.
     */
    public class dayFormatCellEnd extends DateCell {
        @Override public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);


            if (startDatePicker.getValue() != null) {
                if (item.isBefore(startDatePicker.getValue()) || item
                    .isEqual(startDatePicker.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: lightgray;");
                }
            }
            if ((currentTeam != null)) {


                Collection<TimePeriod> allTimePeriods = new ArrayList<>();
                for (TimePeriod t : currentTimePeriods) {
                    if (t.getTeam() == currentTeam) {
                        allTimePeriods.add(t);
                    }
                }

                if(!editing){
                    allTimePeriods.addAll(currentTeam.getTimePeriods());
                }

                boolean isFirstTimePeriod = true;
                LocalDate closestDate = null;

                for (TimePeriod timePeriod : allTimePeriods) {
                    if (timePeriod.containsDate(item)) {
                        setDisable(true);
                        setStyle("-fx-background-color: lightgray;");
                    }

                    if (isFirstTimePeriod) {
                        if (startDatePicker.getValue() != null) {
                            if (timePeriod.getStartDate().isAfter(startDatePicker.getValue())) {
                                closestDate = timePeriod.getStartDate();
                                isFirstTimePeriod = false;
                            } else {
                                closestDate = LocalDate.of(9999, 12, 30);
                            }
                        }
                    } else if (startDatePicker.getValue() != null) {
                        if (timePeriod.getStartDate().isBefore(closestDate) && timePeriod
                            .getStartDate().isAfter(startDatePicker.getValue())) {
                            closestDate = timePeriod.getStartDate();
                        }
                    }
                }
                if (closestDate != null && closestDate.isAfter(startDatePicker.getValue())) {
                    if (item.isAfter(closestDate)) {
                        setDisable(true);
                        setStyle("-fx-background-color: lightgray;");
                    }
                }
            }
        }
    }


    /**
     * Provides formatting rules and actions to the startdatepicker.
     */
    public class dayFormatCell extends DateCell {
        @Override public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);

            if (endDatePicker.getValue() != null) {
                if (item.isAfter(endDatePicker.getValue()) || item
                    .isEqual(endDatePicker.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: lightgray;");
                }
            }
            if ((currentTeam != null)) {
                Collection<TimePeriod> allTimePeriods = new ArrayList<>();
                for (TimePeriod t : currentTimePeriods) {
                    if (t.getTeam() == currentTeam) {
                        allTimePeriods.add(t);
                    }
                }
                if(!editing){
                    allTimePeriods.addAll(currentTeam.getTimePeriods());
                }

                for (TimePeriod timePeriod : allTimePeriods) {
                    if (timePeriod.containsDate(item)) {
                        setDisable(true);
                        setStyle("-fx-background-color: lightgray;");
                    }
                }
            }
        }
    }
}
