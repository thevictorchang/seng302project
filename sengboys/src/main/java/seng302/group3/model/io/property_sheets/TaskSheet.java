package seng302.group3.model.io.property_sheets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import seng302.group3.model.Estimate;
import seng302.group3.model.Task;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by epa31 on 31/07/15.
 */
public class TaskSheet implements EditorSheet{

    @FXML public TextField estimateField;
    @FXML public TextField shortNameTextField;
    @FXML public TextField descriptionTextField;
    @FXML public Label errorLabel;
    @FXML public ComboBox<String> taskTypeComboBox;
    @FXML private TextField commentField;
    @FXML private ListView commentsListView;

    private StorySheet storySheet;

    private static final String errorStyle = "errorTextField";

    private VBox content = new VBox();

    /**
     * Constructor for taskSheet which sets up the sheet and creates a listener for the estimate to only allow
     * integers as entries
     */
    public TaskSheet() {
        {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/EditorSheets/TaskSheet.fxml"));
            try {
                loader.setController(this);

                VBox layout = loader.load();
                content.getChildren().add(layout);

            } catch (IOException e) {
                e.printStackTrace();
            }

            shortNameTextField.setText("");
            descriptionTextField.setText("");

            for (Task.Type type : Task.Type.values()){
                taskTypeComboBox.getItems().add(type.name());
            }
            taskTypeComboBox.getStyleClass().add("dialogCombo");

            estimateField.lengthProperty().addListener(new ChangeListener<Number>(){

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                    if(newValue.intValue() > oldValue.intValue()){
                        char ch = estimateField.getText().charAt(oldValue.intValue());
                        //Check if the new character is a number or other value
                        if(!((ch >= '0' && ch <= '9')| ch == '.' | ch == 'h' | ch == 'm')){
                            estimateField.setText(estimateField.getText().substring(0,estimateField.getText().length()-1));
                        }
                        if (newValue.intValue() == 9) {
                            estimateField.setText(estimateField.getText()
                                    .substring(0, estimateField.getText().length() - 1));
                        }

                    }
                }
            });
        }

    }

    /**
     * Used when editing to populate the fields for the task.
     * @param object
     */
    @Override public void fieldConstruct(Object object) {
        if (object instanceof Task) {
            Task task = (Task) object;
            String estimateString = ""+task.getCurrentEstimate().getValue()/60;
            estimateField.setText(estimateString);
            shortNameTextField.setText(task.getShortName());
            descriptionTextField.setText(task.getDescription());
            commentsListView.getItems().addAll(task.getImpediments());
            taskTypeComboBox.getSelectionModel().select(task.getTaskType().name());
        }

    }

    /**
     * Returns the dialogs contents
     * @return
     */
    @Override public VBox draw() {
        return content;
    }

    /**
     * Checks the text fields for blank fields.
     * @param args
     * @return
     */
    @Override public boolean validate(Object... args) {
        boolean result = true;
        shortNameTextField.setText(shortNameTextField.getText().trim());
        descriptionTextField.setText(descriptionTextField.getText().trim());

        if (shortNameTextField.getText().length() < 1) {
            result = false;
            shortNameTextField.getStyleClass().add(errorStyle);
        } else {
            shortNameTextField.getStyleClass().removeAll(errorStyle);
        }

        if (descriptionTextField.getText().length() < 1) {
            result = false;
            descriptionTextField.getStyleClass().add(errorStyle);
        } else {
            descriptionTextField.getStyleClass().removeAll(errorStyle);
        }

        String estimatePattern =
            "(\\d+[h])(\\d\\d?[m])|(\\d\\d?[m])|(\\d+[h])|(\\d([0-9]+)?(\\.\\d)?)";
        if (estimateField.getText().length() < 1 || !(estimateField.getText().matches(
            estimatePattern))) {
            result = false;
            estimateField.getStyleClass().add(errorStyle);
        } else {
            estimateField.getStyleClass().removeAll(errorStyle);
        }


        if (!result) {
            errorLabel.setText("Missing or Incorrect Fields");
            return result;
        }

        return result;
    }

    private String parseTime(String hr) {
        int k=-1;
        String mins = "";
        try{
            k = Integer.parseInt(hr);
        }
        catch (Exception e){}
        if(!(k > -1)){
            if (hr.matches("(\\d[h])")){
                mins = hr.replaceAll("([h])", "");
                mins = ""+(Integer.parseInt(mins)*60);
            } else if (hr.matches("(\\d[h])(\\d\\d?[m])")){
                mins = hr.replaceAll("([m])", "");
                int i = Integer.parseInt(mins.substring(0,mins.lastIndexOf("h")))*60;
                int j = Integer.parseInt(mins.substring(mins.lastIndexOf("h")+1));
                mins = ""+(i+j);
            } else if(hr.matches("(\\d\\d?[m])")){
                mins = hr.replaceAll("([m])","");
            } else if(hr.matches("(\\d([0-9]+)?(\\.\\d)?)")){
                int i = Integer.parseInt(hr.substring(0,hr.lastIndexOf(".")))*60;
                int j = Integer.parseInt(hr.substring(hr.lastIndexOf(".")+1));
                j = j * 6;
                i = i+j;
                mins = ""+(i);
            }
        }else{
            mins = ""+(k*60);
        }

        return mins;
    }

    /**
     * Sets the given task object's parameters to the fields from the sheet.
     * @param object
     */
    @Override public void apply(Object object) {
        if (object instanceof Task) {
            Task task = (Task) object;
            task.setShortName(shortNameTextField.getText());
            task.setDescription(descriptionTextField.getText());
            task.updateEstimate(new Estimate(Double.parseDouble(parseTime(estimateField.getText()))));
            task.setImpediments(new ArrayList<>(commentsListView.getItems()));
            task.setTaskType(Task.Type.valueOf(
                taskTypeComboBox.getSelectionModel().getSelectedItem()));
        }
    }

    /**
     * when the add comment button is clicked
     * @param actionEvent
     */
    public void addComment(ActionEvent actionEvent) {
        commentField.setText(commentField.getText().toString().trim());
        if(commentField.getText().toString().length() > 0){
            commentField.getStyleClass().removeAll(errorStyle);
            commentsListView.getItems().add(0, commentField.getText().toString());
            commentField.clear();
        }
        else{
            commentField.getStyleClass().add(errorStyle);
        }
    }
}
