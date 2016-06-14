package seng302.group3.model.io.property_sheets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seng302.group3.model.AcceptanceCriteria;
import seng302.group3.model.Organisation;

import java.io.IOException;

/**
 * Created by cjm328 on 18/07/15.
 */
public class AcceptanceCriteriaSheet implements EditorSheet {

    private static final String errorStyle = "errorTextField";

    @FXML public javafx.scene.control.TextArea shortNameTextArea;
    @FXML public Label errorLabel;

    private VBox content = new VBox();

    /**
     * Constructor loads the fxml for the editor and sets the controller to this class
     */
    public AcceptanceCriteriaSheet(Organisation organisation) {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/fxml/EditorSheets/AcceptanceCriteriaSheet.fxml"));
        try {
            loader.setController(this);

            VBox layout = loader.load();
            content.getChildren().add(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When editing will populate the TextArea with the previous text.
     * @param object
     */
    @Override
    public void fieldConstruct(Object object) {
        if (object instanceof AcceptanceCriteria) {
            shortNameTextArea.setText(((AcceptanceCriteria) object).getShortName());
        }
    }

    /**
     * Returns the contents of the dialog.
     * @return
     */
    @Override
    public VBox draw() {
        return content;
    }

    /**
     * Checks the fields of the dialog are vaild upon pressing the okay button.
     * @param args
     * @return
     */
    @Override
    public boolean validate(Object... args) {
        boolean result = true;

        shortNameTextArea.setText(shortNameTextArea.getText().toString().trim());

        if (shortNameTextArea.getText()==null) {
            result = false;
            shortNameTextArea.getStyleClass().add(errorStyle);
        }else if(shortNameTextArea.getText().length() < 1) {
            result = false;
            shortNameTextArea.getStyleClass().add(errorStyle);
        }
        else

        {
            shortNameTextArea.getStyleClass().removeAll(errorStyle);
        }

        if (!result) {
            errorLabel.setText("Missing or Incorrect Fields");
        }
        return result;
    }

    /**
     * Pushes the changes made in the dialog out to the lower lever.
     * @param object
     */
    @Override
    public void apply(Object object) {
        if (object instanceof AcceptanceCriteria) {
            AcceptanceCriteria AC = (AcceptanceCriteria) object;
            AC.setShortName(shortNameTextArea.getText());
        }
    }

}
