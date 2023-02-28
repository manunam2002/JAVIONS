package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftDataTest {

    @Test
    void aircraftDataTrowsNullPointerException(){
        assertThrows(NullPointerException.class, () -> new AircraftData(null,null,
                null,null,null));
        assertThrows(NullPointerException.class, () -> new AircraftData(new AircraftRegistration("HB-JDC"),
                new AircraftTypeDesignator("A20N"), "Airbus",new AircraftDescription("L2J"),
                null));
    }

    @Test
    void aircraftDataWorksWithValidArguments(){
        AircraftRegistration a0 = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator a1 = new AircraftTypeDesignator("A20N");
        String a2 = "Airbus";
        AircraftDescription a3 = new AircraftDescription("L2J");
        WakeTurbulenceCategory a4 = WakeTurbulenceCategory.LIGHT;

        AircraftData a = new AircraftData(a0,a1,a2,a3,a4);
        assertEquals(a0,a.registration());
        assertEquals(a1,a.typeDesignator());
        assertEquals(a2,a.model());
        assertEquals(a3,a.description());
        assertEquals(a4,a.wakeTurbulenceCategory());
    }
}