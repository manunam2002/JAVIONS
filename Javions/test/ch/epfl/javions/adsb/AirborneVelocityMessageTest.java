package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AirborneVelocityMessageTest {

    @Test
    void AirborneVelocityMessageConstructorThrowsRightExceptions(){
        assertThrows(IllegalArgumentException.class, () ->
                new AirborneVelocityMessage(-1, new IcaoAddress("4B6789"), 217.3,1.3));
        assertThrows(IllegalArgumentException.class, () ->
                new AirborneVelocityMessage(0, new IcaoAddress("4B6789"), -217.3,1.3));
        assertThrows(IllegalArgumentException.class, () ->
                new AirborneVelocityMessage(0, new IcaoAddress("4B6789"), 217.3,-1.3));
        assertThrows(IllegalArgumentException.class, () ->
                new AirborneVelocityMessage(-1, new IcaoAddress("4B6789"), -217.3,-1.3));
        assertThrows(NullPointerException.class, () ->
                new AirborneVelocityMessage(0, null, 217.3,1.3));
    }

    @Test
    void AirborneVelocityMessageOfReturnsNullWithInvalidData(){
        RawMessage m1 = new RawMessage(100775400,
                ByteString.ofHexadecimalString("8D000300000CE72C00089058AD77"));
        RawMessage m2 = new RawMessage(100775400,
                ByteString.ofHexadecimalString("8D39D300990C000000089058AD77"));
        AirborneVelocityMessage m10 = AirborneVelocityMessage.of(m1);
        AirborneVelocityMessage m20 = AirborneVelocityMessage.of(m2);
        assertNull(m10);
        assertNull(m20);
    }

    @Test
    void AirborneVelocityMessageWorksWithBookExamples(){
        RawMessage rawMessage = new RawMessage(0,ByteString.ofHexadecimalString("8D485020994409940838175B284F"));
        RawMessage rawMessage1 = new RawMessage(0,ByteString.ofHexadecimalString("8DA05F219B06B6AF189400CBC33F"));
        AirborneVelocityMessage airborneVelocityMessage = AirborneVelocityMessage.of(rawMessage);
        AirborneVelocityMessage airborneVelocityMessage1 = AirborneVelocityMessage.of(rawMessage1);

        assertEquals(159.20, Units.convert(airborneVelocityMessage.speed(),Units.Speed.METER_PER_SECOND,Units.Speed.KNOT),0.01);
        assertEquals(182.88,Units.convertTo(airborneVelocityMessage.trackOrHeading(),Units.Angle.DEGREE),0.01);
        assertEquals(375,Units.convert(airborneVelocityMessage1.speed(),Units.Speed.METER_PER_SECOND,Units.Speed.KNOT));
        assertEquals(243.98,Units.convertTo(airborneVelocityMessage1.trackOrHeading(),Units.Angle.DEGREE),0.01);
    }
}