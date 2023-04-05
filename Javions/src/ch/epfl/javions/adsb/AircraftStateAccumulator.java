package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import java.util.Objects;

/**
 * représente un objet accumulant les messages ADS-B provenant d'un seul aéronef
 * afin de déterminer son état au cours du temps
 * @param <T> paramètre générique de l'objet représentant l'état modifiable d'un aéronef
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class AircraftStateAccumulator <T extends AircraftStateSetter> {

    private final T stateSetter;

    private AirbornePositionMessage lastPositionMessage0;

    private AirbornePositionMessage lastPositionMessage1;


    /**
     * constructeur public
     * @param stateSetter l'état modifiable donné
     * @throws NullPointerException si l'état modifiable donné est nul
     */
    public AircraftStateAccumulator(T stateSetter){
        Objects.requireNonNull(stateSetter);
        this.stateSetter = stateSetter;
    }

    /**
     * retourne l'état modifiable de l'aéronef passé à son constructeur
     * @return l'état modifiable de l'aéronef passé à son constructeur
     */
    public T stateSetter(){
        return stateSetter;
    }

    /**
     * met à jour l'état modifiable en fonction du message donné
     * @param message le message donné
     */
    public void update(Message message){
        switch (message){

            case AircraftIdentificationMessage aim -> {
                stateSetter.setLastMessageTimeStampNs(aim.timeStampNs());
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }

            case AirbornePositionMessage apm -> {
                stateSetter.setLastMessageTimeStampNs(apm.timeStampNs());
                stateSetter.setAltitude(apm.altitude());
                if (apm.parity() == 1) {
                    lastPositionMessage1 = apm;
                    if ((lastPositionMessage0 != null) &&
                            (apm.timeStampNs() - lastPositionMessage0.timeStampNs() <= 10E9)) {
                        GeoPos position = CprDecoder.decodePosition(lastPositionMessage0.x(),
                                lastPositionMessage0.y(), apm.x(), apm.y(), 1);
                        if (position != null) stateSetter.setPosition(position);
                    }
                } else {
                    lastPositionMessage0 = apm;
                    if ((lastPositionMessage1 != null) &&
                            (apm.timeStampNs() - lastPositionMessage1.timeStampNs() <= 10E9)) {
                        GeoPos position = CprDecoder.decodePosition(apm.x(),
                                apm.y(), lastPositionMessage1.x(), lastPositionMessage1.y(), 0);
                        if (position != null) stateSetter.setPosition(position);
                    }
                }
            }

            case AirborneVelocityMessage avm -> {
                stateSetter.setLastMessageTimeStampNs(avm.timeStampNs());
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }

            default -> {}
        }
    }
}
