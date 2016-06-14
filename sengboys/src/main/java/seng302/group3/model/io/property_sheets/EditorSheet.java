package seng302.group3.model.io.property_sheets;

import javafx.scene.layout.VBox;

/**
 * Created by ntr24 on 24/04/15.
 */
public interface EditorSheet {

    public void fieldConstruct(Object object);

    public VBox draw();

    public boolean validate(Object... args);

    public void apply(Object object);
}
