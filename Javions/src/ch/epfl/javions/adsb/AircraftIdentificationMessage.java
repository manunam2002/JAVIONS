package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage() implements Message {
    public static long timeStampNs;
    public static IcaoAddress icaoAddress;
    public static int category;
    public static CallSign callSign;

    public AircraftIdentificationMessage{

    }




    public static AircraftIdentificationMessage of(RawMessage rawMessage){

        return null;
    }

    @Override
    public long timeStampNs() {
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

    public CallSign callSign() {
        return null;
    }

    public int category() {
        return 0;
    }
}
