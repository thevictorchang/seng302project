package seng302.group3.model.gui;

import javafx.scene.layout.HBox;
import seng302.group3.model.LoggedTime;

/**
 * Created by epa31 on 9/08/15.
 */
public class TimeLogRow {
    HBox hBox;
    LoggedTime loggedTime;
    boolean saved;

    public boolean isSaved() {
        return saved;
    }

    public void setSaved() {
        this.saved = true;
    }

    public void setUnsaved() {
        this.saved = false;
    }

    public TimeLogRow(HBox hBox,LoggedTime loggedTime){
        this.hBox = hBox;
        this.loggedTime = loggedTime;
        this.saved = false;
    }

    public LoggedTime getLoggedTime() {
        return loggedTime;
    }

    public HBox getHBox() {
        return hBox;
    }


}
