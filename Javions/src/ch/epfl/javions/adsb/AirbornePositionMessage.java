package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude,
                                      int parity, double x, double y) implements Message {

    private static byte[] index = new byte[]{9,3,10,4,11,5,6,0,7,1,8,2};

    public AirbornePositionMessage{
        if (icaoAddress == null) throw new NullPointerException();
        if (timeStampNs < 0 || (parity != 0 && parity != 1) || (x < 0 || x >= 1) || (y < 0 || y >= 1))
            throw new IllegalArgumentException();
    }

    public static AirbornePositionMessage of(RawMessage rawMessage){
        long me = rawMessage.bytes().bytesInRange(5,11);
        int LON_CPR = (int) (me & 0x1FFFF);
        int LAT_CPR = (int) ((me >>> 17) & 0x1FFFF);
        int FORMAT = (int) ((me >>> 34) & 0x1);
        int ALT = (int) ((me >>> 36) & 0xFFF);
        double altitude;
        if (Bits.testBit(ALT,4)){
            int alt = ((Bits.extractUInt(ALT,0,4)) | ((Bits.extractUInt(ALT,5,7))<<4));
            altitude = Units.convertFrom((-1000 + alt*25),Units.Length.FOOT);
        } else {
            int orderedALT = 0;
            for (int i = 0 ; i < 12 ; ++i){
                if (Bits.testBit(ALT,i)) orderedALT = (orderedALT | (1 << index[i]))&0xFFF;
            }

        }
        return null;
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    public double altitude() {
        return altitude;
    }

    public int parity() {
        return parity;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }
}
