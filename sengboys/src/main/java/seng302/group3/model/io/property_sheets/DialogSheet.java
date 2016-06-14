package seng302.group3.model.io.property_sheets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by ntr24 on 27/04/15.
 */
public class DialogSheet implements EditorSheet {

    VBox content = new VBox();

    @FXML Label labeltext;

    /**
     * A constructor which loads the layout
     */
    public DialogSheet() {
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/fxml/EditorSheets/DialogSheet.fxml"));
        try {
            loader.setController(this);
            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A setter for the label "labeltext", takes a string to set.
     *
     * @param text
     */
    public void setLabelText(String text) {
        this.labeltext.setMinHeight(120);
        this.labeltext.setText(text);
    }


    @Override public void fieldConstruct(Object object) {
        // not relevent
    }

    @Override public VBox draw() {
        return content;
    }

    @Override public boolean validate(Object... args) {
        return true;
    }

    @Override public void apply(Object object) {}
}
