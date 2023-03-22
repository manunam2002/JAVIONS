package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CprDecoderTest {

    @Test
    void decodePositionThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class,
                () -> CprDecoder.decodePosition(0,0,0,0,-1));
        assertThrows(IllegalArgumentException.class,
                () -> CprDecoder.decodePosition(0,0,0,0,2));
        assertThrows(IllegalArgumentException.class,
                () -> CprDecoder.decodePosition(0,0,0,0,100));
    }

    @Test
    void decodePositionWorksWithGivenExample(){
        double y0 = Math.scalb(94445,-17);
        double y1 = Math.scalb(77558,-17);
        double x0 = Math.scalb(111600,-17);
        double x1 = Math.scalb(108865,-17);
        GeoPos geoPos = CprDecoder.decodePosition(x0,y0,x1,y1,0);
        double xExpected = Units.convertFrom(0.020767,Units.Angle.TURN);
        double yExpected = Units.convertFrom(0.128673,Units.Angle.TURN);
        assertEquals(xExpected,geoPos.longitude(),0.0001);
        assertEquals(yExpected,geoPos.latitude(),0.0001);
    }

    @Test
    void decodePositionReturnNullWithInvalidData(){
        double y0 = Math.scalb(64445,-17);
        double y1 = Math.scalb(77558,-17);
        double x0 = Math.scalb(111600,-17);
        double x1 = Math.scalb(108865,-17);
        assertEquals(null,CprDecoder.decodePosition(x0,y0,x1,y1,0));
    }

    @Test
    void decodePositionReturnsTheExactValues(){
        var x0 = Math.scalb(111600d, -17);
        var y0 = Math.scalb(94445d, -17);
        var x1 = Math.scalb(108865d, -17);
        var y1 = Math.scalb(77558d, -17);
        var p = CprDecoder.decodePosition(x0, y0, x1, y1, 0);
        assertEquals( 89192898, p.longitudeT32());
        assertEquals(552659081,p.latitudeT32());
    }
}