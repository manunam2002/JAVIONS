package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage() implements Message {

    public static long timeStampNs;
    public static IcaoAddress icaoAddress;
    public static double altitude;
    public static int parity;
    public static double x;
    public static double y;

    public AirbornePositionMessage{

    }

    //public  static AirbornePositionMessage of(RawMessage rawMessage){

    //}
    @Override
    public long timeStampNs() {
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

    public double altitude() {
        return 0;
    }

    public int parity() {
        return 0;
    }

    public double x() {
        return 0;
    }

    public double y() {
        return 0;
    }
}
