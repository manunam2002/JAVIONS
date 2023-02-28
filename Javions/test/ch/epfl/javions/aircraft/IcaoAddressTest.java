package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IcaoAddressTest {

    @Test
    void icaoAddressThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B18140"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B181"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B1814B"));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
    }

    @Test
    void icaoAddressThrowsNullPointerException(){
        assertThrows(NullPointerException.class, () -> new IcaoAddress(null));
    }

    @Test
    void icaoAdressWorksWithValidAddress(){
        IcaoAddress a = new IcaoAddress("4B1814");
        assertEquals("4B1814",a.string());
    }
}