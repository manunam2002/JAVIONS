package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.aircraft.IcaoAddress;

public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;

    private final static Crc24 crc24 = new Crc24(Crc24.GENERATOR);

    public RawMessage{
        if (timeStampNs < 0 || bytes.size() != LENGTH) throw new IllegalArgumentException();
    }

    public static RawMessage of(long timeStampNs, byte[] bytes){
        if (crc24.crc(bytes) != 0) return null;
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    public static int size(byte byte0){
        if (byte0 == 17) return LENGTH;
        return 0;
    }

    public static int typeCode(long payload){
        long typeCode = payload >>> 51;
        return (int) typeCode;
    }

    public int downLinkFormat(){
        return bytes.byteAt(0);
    }

    //à verifier
    public IcaoAddress icaoAddress(){
        int iCAO = (int) bytes.bytesInRange(1,3); //1,4?
        return new IcaoAddress(Integer.toHexString(iCAO));
    }

    public long payload(){
        return bytes.bytesInRange(4,10);
    } //4,11?

    public int typeCode(){
        return typeCode(payload());
    }
}
