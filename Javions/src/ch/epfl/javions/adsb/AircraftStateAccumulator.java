package ch.epfl.javions.adsb;

public class AircraftStateAccumulator <T extends AircraftStateSetter> {

    private T stateSetter;

    private AirbornePositionMessage lastPositionMessage0;
    private AirbornePositionMessage lastPositionMessage1;


    public AircraftStateAccumulator(T stateSetter){
        if (stateSetter == null) throw new NullPointerException();
        this.stateSetter = stateSetter;
    }

    public T stateSetter(){
        return stateSetter;
    }

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
