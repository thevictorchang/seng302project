package seng302.group3.model.gui.scrum_board;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import seng302.group3.model.Sprint;

import java.io.IOException;

/**
 * Created by ntr24 on 1/08/15.
 */
public class ScrumBoard {

    public static Image noPerson = null;
    public static Image onePerson = null;
    public static Image threePeople = null;
    public static Image multiplePeople = null;
    public static Image clock = null;
    public static Image editImg = null;
    public static Image chevronRight = null;
    public static Image chevronDown = null;

    public static DoubleProperty scrumboardSizeMultipler = new SimpleDoubleProperty(1.0);


    private static ScrumBoard instance;
    private ScrumBoardController controller;

    private ScrumBoard(){
        if(onePerson == null){
            noPerson = new Image(getClass().getResourceAsStream("/imgs/taskLabel/noPerson.png"));
            onePerson = new Image(getClass().getResourceAsStream("/imgs/taskLabel/onePerson.png"));
            threePeople = new Image(getClass().getResourceAsStream("/imgs/taskLabel/threePeople.png"));
            multiplePeople = new Image(getClass().getResourceAsStream("/imgs/taskLabel/multiplePeople.png"));
            clock = new Image(getClass().getResourceAsStream("/imgs/taskLabel/clock.png"));
            editImg = new Image(getClass().getResourceAsStream("/imgs/pencil.png"));
            chevronRight = new Image(getClass().getResourceAsStream("/imgs/chevron_right.png"));
            chevronDown = new Image(getClass().getResourceAsStream("/imgs/chevron_down.png"));
        }
    }

    /**
     * Singleton returns the instance of Scrum Board
     * @return
     */
    public static ScrumBoard getInstance(){
        if (instance == null){
            instance = new ScrumBoard();
        }
        return  instance;
    }

    /**
     * generates a new scrum board with no sprint selected
     * @return - The Node which contains the Scrum Board view
     */
    public Node getScrumBoard() {
        return getScrumBoard(null);
    }

    /**
     * generates a new scrum board with the given sprint
     * @param sprintToShow - sprint to show in the Scrum Board
     * @return - The Node which contains the Scrum Board view
     */
    public Node getScrumBoard(Sprint sprintToShow){
        VBox vBox = new VBox();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScrumBoard/ScrumBoardMain.fxml"));
        try {

            VBox layout = loader.load();
            vBox.getChildren().add(layout);

            controller = loader.getController();
            controller.initialize(sprintToShow);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vBox;
    }

    /**
     * calls the controller refresh methods
     */
    public void refresh(){
        if(controller != null)
            controller.changeRefresh();
    }


}
