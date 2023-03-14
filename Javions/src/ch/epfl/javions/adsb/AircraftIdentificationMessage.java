package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress,
        int category, CallSign callSign) implements Message {

    public AircraftIdentificationMessage{
        if (icaoAddress == null || callSign == null) throw new NullPointerException();
        if (timeStampNs < 0) throw new IllegalArgumentException();
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        int category1 = (rawMessage.bytes().byteAt(4)) & 0x7;
        int category2 = (14-rawMessage.typeCode())<<4;
        category1 = category2 | category1;
        StringBuilder callSign1 = new StringBuilder();
        long callSignValue = rawMessage.bytes().bytesInRange(5,11);
        for (int i = 0 ; i < 8 ; ++i){
            int c = (int) ((callSignValue >>> ((7-i)*6)) & 0x3F);
            char c1;
            if (c > 0 && c < 27){
                c1 = (char)(c +64);
            } else {
                if ((c > 47 && c < 58)||(c == 32)){
                    c1 = (char)c;
                } else {
                    return null;
                }
            }
            callSign1.append(c1);
        }
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(),rawMessage.icaoAddress(),
                category1,new CallSign(callSign1.toString()));
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    public CallSign callSign() {
        return callSign;
    }

    public int category() {
        return category;
    }
}
