package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

public final class AircraftStateManager {

    private Map<IcaoAddress, AircraftStateSetter> map;
    private final AircraftDatabase aircraftDatabase;

    public AircraftStateManager(AircraftDatabase aircraftDatabase){
        this.aircraftDatabase = aircraftDatabase;
    }

    public ObservableList<AircraftStateSetter> states(){
        List<AircraftStateSetter> list = List.copyOf(map.values());
        return FXCollections.unmodifiableObservableList(FXCollections.observableList(list));
    }
}