package seng302.group3.model.io.property_sheets;

import com.sun.javafx.css.StyleManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PropertySheet;
import seng302.group3.model.Person;
import seng302.group3.model.Project;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by cjm328 on 23/03/15.
 */
public class PersonProperties extends VBox {

    private Map<String, Object> personEditorMap = new LinkedHashMap<>();
    /**
     * Fills the map with the fields 'Short Name', 'Full Name' and 'User ID'.
     * The fields are auto filled with the current person data.
     * @param person - the person being edited.
     */
    public PersonProperties(Person person){
        personEditorMap.put("1. Name#Short Name", person.getNameShort());
        personEditorMap.put("1. Name#Full Name", person.getFullName());
        personEditorMap.put("2. UserID#UserID", person.getUserID());
        setup();
    }
    /**
     * Fills the map with the fields 'Short Name', 'Full Name' and 'User ID'.
     */
    public PersonProperties() {
        personEditorMap.put("1. Name#Short Name", "");
        personEditorMap.put("1. Name#Full Name", "");
        personEditorMap.put("2. UserID#UserID", "");
        setup();
    }
    /**
     * Creates a property sheet inside a Vbox that contains the fields specifies in the map.
     */
    private void setup(){
        ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
        for (final String key : personEditorMap.keySet()) {
            list.add(new CustomPropertyItem(key));
        }
        PropertySheet sheet = new PropertySheet(list);
        StyleManager.getInstance().addUserAgentStylesheet(PropertySheet.class.getResource("propertysheet.css").toExternalForm());

        VBox.setVgrow(sheet, Priority.ALWAYS);
        this.getChildren().add(sheet);
        this.setPrefHeight(180);
    }
    /**
     * Getter for the short name of the person
     * @return the short name
     */
    public String getShortName(){
        return (String) personEditorMap.get("1. Name#Short Name");
    }
    /**
     * Getter for the full name of the person
     * @return the full name
     */
    public String getFullName(){
        return (String)personEditorMap.get("1. Name#Full Name");
    }
    /**
     * Getter for the user ID of the person
     * @return the user id
     */
    public String getUserID(){
        return (String) personEditorMap.get("2. UserID#UserID");
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
            return personEditorMap.get(key).getClass();
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
            return personEditorMap.get(key);
        }

        @Override
        public void setValue(Object value) {
            personEditorMap.put(key, value);
        }
    }
}
