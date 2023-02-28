package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftRegistrationTest {

    @Test
    void aircraftRegistrationThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("!"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration("@"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
    }

    @Test
    void aircraftRegistrationThrowsNullPointerException(){
        assertThrows(NullPointerException.class, () -> new AircraftRegistration(null));
    }

    @Test
    void aircraftRegistrationWorksWithValidAddress(){
        AircraftRegistration a = new AircraftRegistration("HB-JDC");
        assertEquals("HB-JDC",a.string());
    }
}