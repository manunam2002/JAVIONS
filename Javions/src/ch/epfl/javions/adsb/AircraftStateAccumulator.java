package ch.epfl.javions.adsb;

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
        if (stateSetter == null) throw new NullPointerException();
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
            case AirbornePositionMessage aim -> {
                stateSetter.setLastMessageTimeStampNs(aim.timeStampNs());
                stateSetter.setAltitude(aim.altitude());
                if (aim.parity() == 1) {
                    lastPositionMessage1 = aim;
                    if ((lastPositionMessage0 != null) &&
                            (aim.timeStampNs() - lastPositionMessage0.timeStampNs() <= 10E9)) {
                        stateSetter.setPosition(CprDecoder.decodePosition(lastPositionMessage0.x(),
                                lastPositionMessage0.y(), aim.x(), aim.y(), 1));
                    }
                } else {
                    lastPositionMessage0 = aim;
                    if ((lastPositionMessage1 != null) &&
                            (aim.timeStampNs() - lastPositionMessage1.timeStampNs() <= 10E9)) {
                        stateSetter.setPosition(CprDecoder.decodePosition(aim.x(),
                                aim.y(), lastPositionMessage1.x(), lastPositionMessage1.y(), 0));
                    }
                }
            }
            case AirborneVelocityMessage aim -> {
                stateSetter.setLastMessageTimeStampNs(aim.timeStampNs());
                stateSetter.setVelocity(aim.speed());
                stateSetter.setTrackOrHeading(aim.trackOrHeading());
            }
            default -> {}
        }
    }
}
