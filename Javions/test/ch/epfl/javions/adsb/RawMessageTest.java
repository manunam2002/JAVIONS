package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class RawMessageTest {

    @Test
    void RawMessageConstructorThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(-1,new ByteString(new byte[14])));
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(-1,new ByteString(new byte[13])));
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(-1,new ByteString(new byte[0])));
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(-1,new ByteString(new byte[15])));
    }

    @Test
    void RawMessageOfWorksWithGivenArray(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0x17,(byte)0xE5,(byte)0xF8,(byte)0x21,(byte)0x00,
                (byte)0x02,(byte)0x00,(byte)0x4B,(byte)0xB8,(byte)0xB1,(byte)0xF1,(byte)0xAC};
        assertNotEquals(null,RawMessage.of(0,bytes));
        byte[] bytes1 = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0x17,(byte)0xF5,(byte)0xF8,(byte)0x21,(byte)0x00,
                (byte)0x02,(byte)0x00,(byte)0x4B,(byte)0xB8,(byte)0xB1,(byte)0xF1,(byte)0xAC};
        assertEquals(null,RawMessage.of(0,bytes1));
    }

    @Test
    void RawMessageSizeWorksWithGivenByte(){
        assertEquals(14,RawMessage.size((byte) 17));
        assertEquals(0,RawMessage.size((byte) 16));
    }

    @Test
    void RawMessageTypeCodeWorksWithGivenLong(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0x17,(byte)0xE5,(byte)0xF8,(byte)0x21,(byte)0x00,
                (byte)0x02,(byte)0x00,(byte)0x4B,(byte)0xB8,(byte)0xB1,(byte)0xF1,(byte)0xAC};
        ByteString byteString = new ByteString(bytes);
        long payload = byteString.bytesInRange(4,11);
        assertEquals(0b11111,RawMessage.typeCode(payload));
    }

    @Test
    void RawMessageDownLinkFormatWorksWIthGivenMessage(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0x17,(byte)0xE5,(byte)0xF8,(byte)0x21,(byte)0x00,
                (byte)0x02,(byte)0x00,(byte)0x4B,(byte)0xB8,(byte)0xB1,(byte)0xF1,(byte)0xAC};
        RawMessage rawMessage = new RawMessage(0,new ByteString(bytes));
        assertEquals(0x8D,rawMessage.downLinkFormat());
    }

    @Test
    void RawMessageIcaoAddressWorksWithGivenMessage(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0x17,(byte)0xE5,(byte)0xF8,(byte)0x21,(byte)0x00,
                (byte)0x02,(byte)0x00,(byte)0x4B,(byte)0xB8,(byte)0xB1,(byte)0xF1,(byte)0xAC};
        RawMessage rawMessage = new RawMessage(0,new ByteString(bytes));
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        System.out.print(icaoAddress);
    }

    @Test
    void RawMessagePayloadWorksWithGivenMessage(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0x17,(byte)0xE5,(byte)0xF8,(byte)0x21,(byte)0x00,
                (byte)0x02,(byte)0x00,(byte)0x4B,(byte)0xB8,(byte)0xB1,(byte)0xF1,(byte)0xAC};
        RawMessage rawMessage = new RawMessage(0,new ByteString(bytes));
        long payload = rawMessage.payload();
        System.out.printf("%x",payload);
    }

    @Test
    void RawMessageTypeCodeWorksWithGivenMessage(){
        byte[] bytes = new byte[] {(byte)0x8D,(byte)0x4B,(byte)0x17,(byte)0xE5,(byte)0xF8,(byte)0x21,(byte)0x00,
                (byte)0x02,(byte)0x00,(byte)0x4B,(byte)0xB8,(byte)0xB1,(byte)0xF1,(byte)0xAC};
        RawMessage rawMessage = new RawMessage(0,new ByteString(bytes));
        int typeCode = rawMessage.typeCode();
        int typeCodeExpected = (bytes[4] & 0xFF) >>> 3;
        assertEquals(typeCodeExpected,typeCode);
    }
}