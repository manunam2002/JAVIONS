package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AirbornePositionMessageTest {

    @Test
    void AirbornePositionMessageConstructorThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(-1,new IcaoAddress("3C6481"),10370.82,
                        0,0.8675,0.7413));
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(-10,new IcaoAddress("3C6481"),10370.82,
                        0,0.8675,0.7413));
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                        2,0.8675,0.7413));
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                        -1,0.8675,0.7413));
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                        0,1,0.7413));
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                        0,0.8675,1));
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                        0,-0.8675,0.7413));
        assertThrows(IllegalArgumentException.class,
                () -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                        0,0.8675,-0.7413));
        assertDoesNotThrow(() -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                0,0.8675,0.7413));
    }

    @Test
    void AirbornePositionMessageConstructorThrowsNullPointerException(){
        assertThrows(NullPointerException.class,
                () -> new AirbornePositionMessage(0,null ,10370.82,
                        0,0.8675,0.7413));
        assertDoesNotThrow(() -> new AirbornePositionMessage(0,new IcaoAddress("3C6481"),10370.82,
                0,0.8675,0.7413));
    }

    @Test
    void AirbornePositionMessageOfReturnsNullWithInvalidIdentification(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4D,(byte)0x02,(byte)0x21,(byte)0x58,(byte)0x12,(byte)0xF2,
                (byte)0xC9,(byte)0xA1,(byte)0x5F,(byte)0x63,(byte)0xB2,(byte)0x53,(byte)0x66};
        RawMessage rawMessage = new RawMessage(0, new ByteString(bytes));
        assertEquals(null,AirbornePositionMessage.of(rawMessage));
    }

    @Test
    void AirbornePositionAltitudeWorksWithGrayCodes(){
        byte[] bytes1 = new byte[] {(byte)0x8D,(byte)0x39,(byte)0x20,(byte)0x35,(byte)0x59,(byte)0xB2,(byte)0x25,
                (byte)0xF0,(byte)0x75,(byte)0x50,(byte)0xAD,(byte)0xBE,(byte)0x32,(byte)0x8F};
        RawMessage rawMessage1 = new RawMessage(0,new ByteString(bytes1));
        assertEquals(3474.72,AirbornePositionMessage.of(rawMessage1).altitude(),0.001);
        byte[] bytes2 = new byte[] {(byte)0x8D,(byte)0xAE,(byte)0x02,(byte)0xC8,(byte)0x58,(byte)0x64,(byte)0xA5,
                (byte)0xF5,(byte)0xDD,(byte)0x49,(byte)0x75,(byte)0xA1,(byte)0xA3,(byte)0xF5};
        RawMessage rawMessage2 = new RawMessage(0,new ByteString(bytes2));
        assertEquals(7315.20,AirbornePositionMessage.of(rawMessage2).altitude(),0.001);
    }
}