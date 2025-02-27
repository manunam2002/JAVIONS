package ch.epfl.javions.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * gère la ligne d'état
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class StatusLineController {

    private static final String LEFT_TEXT = "Aéronefs visibles : ";
    private static final String RIGHT_TEXT = "Messages reçus : ";
    private static final String STATUS_CSS = "status.css";
    private final BorderPane pane;
    private final SimpleIntegerProperty aircraftCountProperty = new SimpleIntegerProperty(0);
    private final SimpleLongProperty messageCountProperty = new SimpleLongProperty(0);

    /**
     * costructeur par défaut
     */
    public StatusLineController() {

        Text left = new Text();
        left.textProperty().bind(aircraftCountProperty.map(i -> LEFT_TEXT +i));

        Text right = new Text();
        right.textProperty().bind(messageCountProperty.map(i -> RIGHT_TEXT +i));

        pane = new BorderPane(null, null, right, null, left);
        pane.getStylesheets().add(STATUS_CSS);
    }

    /**
     * retourne le panneau contenant la ligne d'état
     *
     * @return le panneau contenant la ligne d'état
     */
    public BorderPane pane() {
        return pane;
    }

    /**
     * retourne la propriété contenant le nombre d'aéronefs actuellement visibles
     *
     * @return la propriété contenant le nombre d'aéronefs actuellement visibles
     */
    public SimpleIntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    /**
     * retourne la propriété contenant le nombre de messages reçus depuis le début de l'exécution du programme
     *
     * @return la propriété contenant le nombre de messages reçus depuis le début de l'exécution du programme
     */
    public SimpleLongProperty messageCountProperty() {
        return messageCountProperty;
    }
}