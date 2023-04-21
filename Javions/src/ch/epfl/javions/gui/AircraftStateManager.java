package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

/**
 * garde à jour les états d'un ensemble d'aéronefs en fonction des messages reçus d'eux
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class AircraftStateManager {

    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> map = new HashMap<>();

    private final ObservableSet<ObservableAircraftState> observableAircraftStates = FXCollections.observableSet();
    private final ObservableSet<ObservableAircraftState> unmodifiableObservableAircraftStates =
            FXCollections.unmodifiableObservableSet(observableAircraftStates);

    private final AircraftDatabase aircraftDatabase;

    private long lastMessageTimeStampNs;

    /**
     * constructeur public
     * @param aircraftDatabase la base de données contenant les caractéristiques fixes des aéronefs
     */
    public AircraftStateManager(AircraftDatabase aircraftDatabase){
        this.aircraftDatabase = aircraftDatabase;
    }

    /**
     * retourne l'ensemble observable, mais non modifiable, des états observables des aéronefs
     * dont la position est connue
     * @return l'ensemble des états observables des aéronefs dont la position est connue
     */
    public ObservableSet<ObservableAircraftState> states(){
        return unmodifiableObservableAircraftStates;
    }

    /**
     * utilise le message donné pour mettre à jour l'état de l'aéronef qui l'a envoyé ou crée cet état
     * lorsque le message est le premier reçu
     * @param message le message donné
     * @throws IOException en cas d'erreur d'entrée-sortie
     */
    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress icaoAddress = message.icaoAddress();
        if (map.containsKey(icaoAddress)){
            map.get(icaoAddress).update(message);
            ObservableAircraftState aircraftState = map.get(icaoAddress).stateSetter();
            if (aircraftState.getPosition() != null){
                observableAircraftStates.add(aircraftState);
            }
        } else {
            AircraftStateAccumulator<ObservableAircraftState> aircraftStateAccumulator =
                    new AircraftStateAccumulator<>(new ObservableAircraftState
                            (icaoAddress,aircraftDatabase.get(icaoAddress)));
            aircraftStateAccumulator.update(message);
            ObservableAircraftState aircraftState = aircraftStateAccumulator.stateSetter();
            map.put(icaoAddress,aircraftStateAccumulator);
            if (aircraftState.getPosition() != null){
                observableAircraftStates.add(aircraftState);
            }
        }
        lastMessageTimeStampNs = message.timeStampNs();
    }

    /**
     * supprime de l'ensemble des états observables tous ceux correspondant à des aéronefs dont aucun message
     * n'a été reçu dans la minute précédant la réception du dernier message mis à jour
     */
    public void purge(){
        observableAircraftStates.removeIf(observableAircraftState ->
                observableAircraftState.getLastMessageTimeStampNs() - lastMessageTimeStampNs
                        >= Units.Time.MINUTE * 10E9);
    }
}