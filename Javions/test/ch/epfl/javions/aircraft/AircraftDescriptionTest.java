package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDescriptionTest {

    @Test
    void aircraftDescriptionThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("W2J"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L9J"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("L2J_"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription(""));
    }

    @Test
    void aircraftDescriptionWorkWithNullString(){
        AircraftDescription a = new AircraftDescription(null);
        assertEquals(null,a.string());
    }

    @Test
    void aircraftDescriptionWorksWithValidAddress(){
        AircraftDescription a = new AircraftDescription("L2J");
        assertEquals("L2J",a.string());
    }
}