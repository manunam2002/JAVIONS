package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private BorderPane pane;

    private SimpleIntegerProperty aircraftCountProperty = new SimpleIntegerProperty(0);

    private SimpleLongProperty messageCountProperty = new SimpleLongProperty(0);

    public StatusLineController(){

        pane = new BorderPane();
        pane.getStylesheets().add("status.css");

        Text left = new Text();
        left.textProperty().bind(Bindings.createStringBinding(() ->
                "Aéronefs visibles : "+aircraftCountProperty.get()));
        pane.leftProperty().set(left);

        Text right = new Text();
        right.textProperty().bind(Bindings.createStringBinding(() ->
                "Messages reçus : "+messageCountProperty.get()));
        pane.rightProperty().set(right);
    }

    public BorderPane pane() {
        return pane;
    }

    public SimpleIntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    public SimpleLongProperty messageCountProperty() {
        return messageCountProperty;
    }
}