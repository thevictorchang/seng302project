package seng302.group3.model.io.property_sheets;

import com.sun.javafx.css.StyleManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PropertySheet;
import seng302.group3.model.Project;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by epa31 on 18/03/15.
 */
public class ProjectProperties extends VBox {

    private Map<String, Object> projectEditorMap = new LinkedHashMap<>();
    /**
     * Fills the map with the fields 'Short Name', 'Full Name' and 'Description'.
     * The fields are auto filled with the current person data.
     * @param project - the project being edited.
     */
    public ProjectProperties(Project project){
        projectEditorMap.put("1. Name#Short Name", project.getNameShort());
        projectEditorMap.put("1. Name#Long Name", project.getNameLong());
        projectEditorMap.put("2. Description#Description", project.getDescription());
        setup();
    }
     /**
     * Fills the map with the fields 'Short Name', 'Full Name' and 'Description'.
     */
    public ProjectProperties() {
        projectEditorMap.put("1. Name#Short Name", "");
        projectEditorMap.put("1. Name#Long Name", "");
        projectEditorMap.put("2. Description#Description", "");
        setup();
    }
    /**
     * Creates a property sheet inside a Vbox that contains the fields specifies in the map.
     */
    private void setup(){
        ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
        for (final String key : projectEditorMap.keySet()) {
            list.add(new CustomPropertyItem(key));
        }
        PropertySheet sheet = new PropertySheet(list);
        StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("propertysheet.css").toExternalForm());

        VBox.setVgrow(sheet, Priority.ALWAYS);
        this.getChildren().add(sheet);
        this.setPrefHeight(180);
    }
    /**
     * Getter for the short name of the project
     * @return the short name
     */
    public String getShortName(){
        return (String) projectEditorMap.get("1. Name#Short Name");
    }
    /**
     * Getter for the full name of the project
     * @return the full name
     */
    public String getLongName(){
        return (String) projectEditorMap.get("1. Name#Long Name");
    }
    /**
     * Getter for the description of the project
     * @return the description
     */
    public String getDescription(){
        return (String) projectEditorMap.get("2. Description#Description");
    }



    class CustomPropertyItem implements PropertySheet.Item {
        private String key;
        private String category, name;

        public CustomPropertyItem(String key) {
            this.key = key;

            String[] skey = key.split("#");
            category = skey[0];
            name = skey[1];
        }

        @Override
        public Class<?> getType() {
            return projectEditorMap.get(key).getClass();
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public Object getValue() {
            return projectEditorMap.get(key);
        }

        @Override
        public void setValue(Object value) {
            projectEditorMap.put(key, value);
        }
    }
}
