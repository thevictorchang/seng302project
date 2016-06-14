package seng302.group3.model.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import seng302.group3.model.Sprint;

import java.io.IOException;
import java.util.Date;

/**
 * Created by jwc78 on 3/08/15.
 */
public class BurndownChart {

    private static BurndownChart instance = null;

    private BurndownChartController controller;

    private BurndownChart() {

    }

    /**
     * Method to get the instance of the singleton BurndownChart
     * @return BurndownChart instance
     */
    public static BurndownChart getInstance() {
        if (instance == null) {
            instance = new BurndownChart();
        }
        return instance;
    }


    /**
     * Constructor to return the BurndownChart
     * @return VBox containing the BurndownChart
     */
    public Node getBurndownChart() {
        VBox vBox = new VBox();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BurndownChart.fxml"));
        try {
            VBox layout = loader.load();
            vBox.getChildren().add(layout);
            controller = loader.getController();
            controller.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vBox;
    }


    public BurndownChartController getController() {
        return controller;
    }

    /**
     * refreshes the controller to allow for updates within the BurndownChart
     */
    public void refresh() {
        if (controller != null) {
            controller.changeRefresh();
        }
    }

}
