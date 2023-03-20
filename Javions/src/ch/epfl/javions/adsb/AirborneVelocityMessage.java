package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) {

    public AirborneVelocityMessage{


    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),0,0);
    }
}
