package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTypeDesignatorTest {

    @Test
    void aircraftTypeDesignatorThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A20NN"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("A20NA20NA20NA20NA20N"));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator(""));
    }

    @Test
    void aircraftTypeDesignatorWorkWithNullString(){
        AircraftTypeDesignator a = new AircraftTypeDesignator(null);
        assertEquals(null,a.string());
    }

    @Test
    void aircraftTypeDesignatorWorksWithValidAddress(){
        AircraftTypeDesignator a = new AircraftTypeDesignator("A20N");
        assertEquals("A20N",a.string());
    }
}