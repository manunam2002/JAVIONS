package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CallSignTest {

    @Test
    void callSignThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new CallSign("4B14B14B14B14B14B14B14B14B14B1"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("AB@"));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("Y5!"));
    }

    @Test
    void callSignThrowsNullPointerException(){
        assertThrows(NullPointerException.class, () -> new CallSign(null));
    }

    @Test
    void callSignWorksWithValidAddress(){
        CallSign a = new CallSign("4B1814");
        assertEquals("4B1814",a.string());
    }
}