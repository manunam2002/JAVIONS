package ch.epfl.javions.adsb;

import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public class MessageParser {
    public static Message parse(RawMessage rawMessage) {
        if (rawMessage.typeCode() > 0 && rawMessage.typeCode() < 5) {
            return AircraftIdentificationMessage.of(rawMessage);
        }
        if ((rawMessage.typeCode() > 8 && rawMessage.typeCode() < 19) ||
                (rawMessage.typeCode() > 19 && rawMessage.typeCode() < 23)) {
            return AirbornePositionMessage.of(rawMessage);
        }
        if (rawMessage.typeCode() == 19) {
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }
}
