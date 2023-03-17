package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AircraftIdentificationMessageTest {

    @Test
    void AircraftIdentificationMessageConstructorThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class,
                () -> new AircraftIdentificationMessage(-1,new IcaoAddress("4D2228"),
                        163,new CallSign("RYR7JD")));
        assertThrows(IllegalArgumentException.class,
                () -> new AircraftIdentificationMessage(-10,new IcaoAddress("4D2228"),
                        163,new CallSign("RYR7JD")));
        assertDoesNotThrow(() -> new AircraftIdentificationMessage(0,new IcaoAddress("4D2228"),
                        163,new CallSign("RYR7JD")));
    }

    @Test
    void AircraftIdentificationMessageConstructorThrowsNullPointerException(){
        assertThrows(NullPointerException.class,
                () -> new AircraftIdentificationMessage(12,null,
                        163,new CallSign("RYR7JD")));
        assertThrows(NullPointerException.class,
                () -> new AircraftIdentificationMessage(12,new IcaoAddress("4D2228"),
                        163,null));
        assertThrows(NullPointerException.class,
                () -> new AircraftIdentificationMessage(12,null,
                        163,null));
        assertDoesNotThrow(() -> new AircraftIdentificationMessage(0,new IcaoAddress("4D2228"),
                163,new CallSign("RYR7JD")));
    }

    @Test
    void AircraftIdentificationMessageOfReturnsNullWithInvalidIdentification(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0xCD,(byte)0xE9,(byte)0x23,(byte)0x4D,(byte)0x84,
                (byte)0xF5,(byte)0xFC,(byte)0x88,(byte)0x20,(byte)0x9B,(byte)0xFD,(byte)0x58};
        RawMessage rawMessage = new RawMessage(0, new ByteString(bytes));
        assertEquals(null,AircraftIdentificationMessage.of(rawMessage));
    }
}