package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftDatabaseTest {

    @Test
    void aircraftDatabaseThrowsNullPointerException(){
        assertThrows(NullPointerException.class, () -> new AircraftDatabase(null));
    }

    @Test
    void aircraftDatabaseWorksWithValidAddress() throws IOException {
        AircraftDatabase a = new AircraftDatabase("/aircraft.zip");
        AircraftData a1 = a.get(new IcaoAddress("09C008"));
        assertEquals("XT-BFA",a1.registration().string());
        assertEquals("B722",a1.typeDesignator().string());
        assertEquals("BOEING 727-200",a1.model());
        assertEquals("L3J",a1.description().string());
        assertEquals(WakeTurbulenceCategory.MEDIUM,a1.wakeTurbulenceCategory());

        AircraftDatabase b = new AircraftDatabase("/aircraft.zip");
        AircraftData b1 = a.get(new IcaoAddress("E941FF"));
        assertEquals("CP-3080",b1.registration().string());
        assertEquals("C208",b1.typeDesignator().string());
        assertEquals("CESSNA 208 Caravan",b1.model());
        assertEquals("L1T",b1.description().string());
        assertEquals(WakeTurbulenceCategory.LIGHT,b1.wakeTurbulenceCategory());
    }

    @Test
    void aircraftDatabaseWorksWithUnknownAddress() throws IOException {
        AircraftDatabase a = new AircraftDatabase("/aircraft.zip");
        AircraftData a1 = a.get(new IcaoAddress("07C008"));
        assertEquals(null, a1);
    }
}