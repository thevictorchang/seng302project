package seng302.group3.model.search;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.io.SerializableObservableList;

public class FilterListViewTest{
    ListView testListView;
    TextField testText;
    ObservableList testData;
    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception {
        testListView = new ListView();
        testText = new TextField();
        SerializableObservableList testOData = new SerializableObservableList();
        testData = testOData.getObservableList();
        testData.add("abc");
        testData.add("bcd");
        testData.add("cde");
    }

    @After
    public void tearDown() throws Exception {
        testListView = null;
        testText = null;
        testData = null;
    }

    @Test
    public void testFilterKeepsGoodContainingResult() throws Exception {
        FilterListView filterListView = new FilterListView(testListView, testText, FilterUtil.AutoCompleteMode.CONTAINING);
        filterListView.setData(testData);
        testText.setText("b");
        Assert.assertTrue("The listview items contain the string abc still", testListView.getItems().contains("abc"));
    }

    @Test
    public void testFilterRemovesBadContainingResult() throws Exception {
        FilterListView filterListView = new FilterListView(testListView, testText, FilterUtil.AutoCompleteMode.CONTAINING);
        filterListView.setData(testData);
        testText.setText("b");
        Assert.assertFalse("The listview items contain the string cde still", testListView.getItems().contains("cde"));
    }
    @Test
    public void testFilterKeepsGoodStartsWithResult() throws Exception {
        FilterListView filterListView = new FilterListView(testListView, testText, FilterUtil.AutoCompleteMode.STARTS_WITH);
        filterListView.setData(testData);
        testText.setText("a");
        Assert.assertTrue("The combobox items contain the string abc still", testListView.getItems().contains("abc"));
    }

    @Test
    public void testFilterRemovesBadStartsWithResult() throws Exception {
        FilterListView filterListView = new FilterListView(testListView, testText, FilterUtil.AutoCompleteMode.STARTS_WITH);
        filterListView.setData(testData);
        testText.setText("a");
        Assert.assertFalse("The combobox items contain the string bcd still", testListView.getItems().contains("bcd"));
    }
}