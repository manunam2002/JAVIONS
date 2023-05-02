package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Consumer;

public class AircraftTableController {

    private ObservableSet<ObservableAircraftState> states;
    private ObjectProperty<ObservableAircraftState> selectedAircraft;
    private TableView<ObservableAircraftState> pane;

    public AircraftTableController(ObservableSet<ObservableAircraftState> states,
                                   ObjectProperty<ObservableAircraftState> selectedAircraft) {
        this.states = states;
        this.selectedAircraft = selectedAircraft;

        pane = new TableView<>();
        pane.getStylesheets().add("table.css");
        pane.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        pane.setTableMenuButtonVisible(true);

        TableColumn<ObservableAircraftState, String> iCAOColumn = new TableColumn<>();
        iCAOColumn.setPrefWidth(60);
        iCAOColumn.setCellValueFactory(f ->
            new ReadOnlyStringWrapper(f.getValue().getIcaoAddress().string()));

        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>();
        iCAOColumn.setPrefWidth(70);
        iCAOColumn.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(CallSign::string));

        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>();
        iCAOColumn.setPrefWidth(90);
        iCAOColumn.setCellValueFactory(f -> {
                if (f.getValue().getAircraftData() != null)
                    return new ReadOnlyStringWrapper(f.getValue().getAircraftData().registration().string());
                return null;
            });

        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>();
        iCAOColumn.setPrefWidth(230);
        iCAOColumn.setCellValueFactory(f -> {
                    if (f.getValue().getAircraftData() != null)
                        return new ReadOnlyStringWrapper(f.getValue().getAircraftData().model());
                    return null;
                });

        TableColumn<ObservableAircraftState, String> typeDesignatorColumn = new TableColumn<>();
        iCAOColumn.setPrefWidth(50);
        iCAOColumn.setCellValueFactory(f -> {
                    if (f.getValue().getAircraftData() != null)
                        return new ReadOnlyStringWrapper(f.getValue().getAircraftData().typeDesignator().string());
                    return null;
                });

        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>();
        iCAOColumn.setPrefWidth(70);
        iCAOColumn.setCellValueFactory(f -> {
                    if (f.getValue().getAircraftData() != null)
                        return new ReadOnlyStringWrapper(f.getValue().getAircraftData().description().string());
                    return null;
                });

        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>();
        longitudeColumn.setPrefWidth(85);
        longitudeColumn.setCellFactory(f -> {
            TableCell<ObservableAircraftState, String> cell =
                    longitudeColumn.getCellFactory().call(longitudeColumn);
            cell.getStyleClass().add("numeric");
            return cell;
        });
        NumberFormat numberFormatPos = NumberFormat.getInstance();
        numberFormatPos.setMaximumFractionDigits(4);
        numberFormatPos.setMinimumFractionDigits(0);
        longitudeColumn.setCellValueFactory(f ->
                f.getValue().positionProperty().map(geoPos -> numberFormatPos.format(geoPos.longitude())));
        longitudeColumn.setComparator((s1, s2) -> {
            if(s1.isEmpty() || s2.isEmpty()) return s1.compareTo(s2);
            try {
                return Double.compare((double)numberFormatPos.parse(s1), (double)numberFormatPos.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>();
        latitudeColumn.setPrefWidth(85);
        latitudeColumn.setCellFactory(f -> {
            TableCell<ObservableAircraftState, String> cell =
                    latitudeColumn.getCellFactory().call(latitudeColumn);
            cell.getStyleClass().add("numeric");
            return cell;
        });
        latitudeColumn.setCellValueFactory(f ->
                f.getValue().positionProperty().map(geoPos -> numberFormatPos.format(geoPos.latitude())));
        latitudeColumn.setComparator((s1, s2) -> {
            if(s1.isEmpty() || s2.isEmpty()) return s1.compareTo(s2);
            try {
                return Double.compare((double)numberFormatPos.parse(s1), (double)numberFormatPos.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>();
        altitudeColumn.setPrefWidth(85);
        altitudeColumn.setCellFactory(f -> {
            TableCell<ObservableAircraftState, String> cell =
                    altitudeColumn.getCellFactory().call(altitudeColumn);
            cell.getStyleClass().add("numeric");
            return cell;
        });
        NumberFormat numberFormatAlt = NumberFormat.getInstance();
        numberFormatAlt.setMaximumFractionDigits(0);
        numberFormatAlt.setMinimumFractionDigits(0);
        altitudeColumn.setCellValueFactory(f ->
                f.getValue().altitudeProperty().map(numberFormatAlt::format));
        altitudeColumn.setComparator((s1, s2) -> {
            if(s1.isEmpty() || s2.isEmpty()) return s1.compareTo(s2);
            try {
                return Double.compare((double)numberFormatAlt.parse(s1), (double)numberFormatAlt.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        TableColumn<ObservableAircraftState, String> velocityColumn = new TableColumn<>();
        velocityColumn.setPrefWidth(85);
        velocityColumn.setCellFactory(f -> {
            TableCell<ObservableAircraftState, String> cell =
                    velocityColumn.getCellFactory().call(velocityColumn);
            cell.getStyleClass().add("numeric");
            return cell;
        });
        velocityColumn.setCellValueFactory(f ->
                f.getValue().velocityProperty().map(numberFormatAlt::format));
        velocityColumn.setComparator((s1, s2) -> {
            if(s1.isEmpty() || s2.isEmpty()) return s1.compareTo(s2);
            try {
                return Double.compare((double)numberFormatAlt.parse(s1), (double)numberFormatAlt.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        states.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        pane.getItems().add(change.getElementAdded());
                        pane.getItems().sort();
                    }
                    if (change.wasRemoved()) {
                        pane.getItems().remove(change.getElementAdded());
                    }
                });

        pane.getItems().addAll(states);

        selectedAircraft.addListener();
    }

    public TableView<ObservableAircraftState> pane(){
        return pane;
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){

    }
}