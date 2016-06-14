package seng302.group3.model.search;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Created by cjm328 on 21/09/15.
 */
public class FilterListView<T> {
    private ListView listView;
    private TextField textField;
    private FilterUtil.AutoCompleteMode mode;
    private ObservableList<T> data;

    /**
     * Creates a FilterListView object which associates a listview with a textfield so thet the textfield filters the listview.
     * @param listView - the list view to pe filtered
     * @param textField - the textfield to filter the list view
     * @param mode - the filtering mode (containing or starting with the search term)
     */
    public FilterListView(ListView<T> listView, TextField textField, FilterUtil.AutoCompleteMode mode) {
        this.listView = listView;
        this.textField = textField;
        this.mode = mode;
        this.textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ObservableList<T> list = FXCollections.observableArrayList();
                for (T aData : data) {

                    if (mode.equals(FilterUtil.AutoCompleteMode.STARTS_WITH) && aData.toString().toLowerCase().startsWith(newValue.toLowerCase())) {
                        list.add(aData);
                    } else if (mode.equals(FilterUtil.AutoCompleteMode.CONTAINING) && aData.toString().toLowerCase().contains(newValue.toLowerCase())) {
                        list.add(aData);
                    }
                }
                String t = textField.getText();

                listView.setItems(list);
                textField.setText(t);
            }
        });
    }

    /**
     * Sets the data of filterlistview object and updates the listview, this is done because otherwise so that other parts of code changing
     * the list view items was causing inconsistent filter results.
     * @param newData
     */
    public void setData(ObservableList<T> newData){
        data = newData;
        listView.setItems(data);
    }

}
