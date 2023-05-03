package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * gère la table des aéronefs
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class AircraftTableController {

    private static final int NUMERIC_COLUMN_WIDTH = 85;
    private final TableView<ObservableAircraftState> pane;
    private Consumer<ObservableAircraftState> doubleClickConsumer;

    /**
     * constructeur public
     * @param states l'ensemble des états des aéronefs qui doivent apparaître sur la vue
     * @param selectedAircraft une propriété JavaFX contenant l'état de l'aéronef sélectionné
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> states,
                                   ObjectProperty<ObservableAircraftState> selectedAircraft) {

        pane = new TableView<>();
        pane.getStylesheets().add("table.css");
        pane.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        pane.setTableMenuButtonVisible(true);

        TableColumn<ObservableAircraftState, String> iCAOColumn = createTextColumn("OACI", 60,
                f -> new ReadOnlyStringWrapper(f.getValue().getIcaoAddress().string()));

        TableColumn<ObservableAircraftState, String> callSignColumn = createTextColumn("Indicatif", 70,
                f -> f.getValue().callSignProperty().map(CallSign::string));

        TableColumn<ObservableAircraftState, String> registerColumn = createTextColumn("Immatriculation", 90,
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(d -> d.registration().string()));

        TableColumn<ObservableAircraftState, String> modelColumn = createTextColumn("Modèle", 230,
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(AircraftData::model));

        TableColumn<ObservableAircraftState, String> typeDesignatorColumn = createTextColumn("Type", 50,
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(d -> d.typeDesignator().string()));

        TableColumn<ObservableAircraftState, String> descriptionColumn = createTextColumn("Description", 70,
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(d -> d.description().string()));

        NumberFormat numberFormatPos = NumberFormat.getInstance();
        numberFormatPos.setMaximumFractionDigits(4);
        numberFormatPos.setMinimumFractionDigits(0);

        NumberFormat numberFormatAlt = NumberFormat.getInstance();
        numberFormatAlt.setMaximumFractionDigits(0);
        numberFormatAlt.setMinimumFractionDigits(0);

        TableColumn<ObservableAircraftState, String> longitudeColumn = createNumberColumn("Longitude (°)",
                numberFormatPos, f ->
                        f.getValue().positionProperty().map(geoPos -> numberFormatPos.format(
                        Units.convertTo(geoPos.longitude(), Units.Angle.DEGREE))));

        TableColumn<ObservableAircraftState, String> latitudeColumn = createNumberColumn("Latitude (°)",
                numberFormatPos, f ->
                        f.getValue().positionProperty().map(geoPos -> numberFormatPos.format(
                        Units.convertTo(geoPos.latitude(), Units.Angle.DEGREE))));

        TableColumn<ObservableAircraftState, String> altitudeColumn = createNumberColumn("Altitude (m)",
                numberFormatAlt, f ->
                        f.getValue().altitudeProperty().map(numberFormatAlt::format));

        TableColumn<ObservableAircraftState, String> velocityColumn = createNumberColumn("Vitesse (km/h)",
                numberFormatAlt, f ->
                        f.getValue().velocityProperty().map(v -> numberFormatAlt.format(
                        Units.convert(v.doubleValue(), Units.Speed.METER_PER_SECOND, Units.Speed.KILOMETER_PER_HOUR))));

        pane.getColumns().setAll(iCAOColumn, callSignColumn, registerColumn, modelColumn, typeDesignatorColumn,
                descriptionColumn, longitudeColumn, latitudeColumn, altitudeColumn, velocityColumn);

        states.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        pane.getItems().add(change.getElementAdded());
                        pane.sort();
                    }
                    if (change.wasRemoved()) {
                        pane.getItems().remove(change.getElementAdded());
                    }
                });

        pane.getItems().addAll(states);

        selectedAircraft.addListener((p, o, n) -> {
            pane.getSelectionModel().select(n);
            if(!Objects.equals(o, n)) pane.scrollTo(n);
        });

        pane.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> selectedAircraft.set(n));

        pane.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY
                    && doubleClickConsumer != null && selectedAircraft.get() != null){
                doubleClickConsumer.accept(selectedAircraft.get());
            }
        });
    }

    /**
     * crée une colonne de texte à ajouter à la table
     * @param title le titre de la colonne
     * @param width la largeur préférée de la colonne
     * @param value la fonction qui définit la valeur de chaque cellule de la colonne
     * @return la colonne de texte
     */
    private TableColumn<ObservableAircraftState, String> createTextColumn(String title, int width,
                                  Callback<TableColumn.CellDataFeatures<ObservableAircraftState, String>,
                                          ObservableValue<String>> value){
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(value);
        return column;
    }

    /**
     * crée une colonne numerique à ajouter à la table
     * @param title le titre de la colonne
     * @param numberFormat le formatteur de nombres qui permet de transformer la valeur de la cellule en une
     *                     chaine de charactères
     * @param value la fonction qui définit la valeur de chaque cellule de la colonne
     * @return la colonne de texte
     */
    private TableColumn<ObservableAircraftState, String> createNumberColumn(String title, NumberFormat numberFormat,
                                   Callback<TableColumn.CellDataFeatures<ObservableAircraftState, String>,
                                           ObservableValue<String>> value){
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(title);
        column.setPrefWidth(NUMERIC_COLUMN_WIDTH);
        column.getStyleClass().add("numeric");
        column.setCellValueFactory(value);
        column.setComparator((s1, s2) -> {
            if(s1.isEmpty() || s2.isEmpty()) return s1.compareTo(s2);
            try {
                return Double.compare(numberFormat.parse(s1).doubleValue(), numberFormat.parse(s2).doubleValue());
            } catch (ParseException e) {
                throw new Error(e);
            }
        });
        return column;
    }

    public TableView<ObservableAircraftState> pane(){
        return pane;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        this.doubleClickConsumer = consumer;
    }
}