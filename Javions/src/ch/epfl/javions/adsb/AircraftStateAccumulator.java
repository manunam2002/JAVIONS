package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftStateAccumulator <T extends AircraftStateSetter> {

    private T stateSetter;

    private AirbornePositionMessage lastPositionMessage;

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
                if (aim.timeStampNs()-lastPositionMessage.timeStampNs() <= 10000){
                    switch (aim.parity()){
                        case 1 -> stateSetter.setPosition(CprDecoder.decodePosition(lastPositionMessage.x(),
                                lastPositionMessage.y(), aim.x(), aim.y(),1));
                        default -> stateSetter.setPosition(CprDecoder.decodePosition(aim.x(), aim.y(),
                                lastPositionMessage.x(), lastPositionMessage.y(), 0));
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
