package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageParserTest {

    @Test
    void MessageParserReturnsTheRightNumberOfEachMessage() throws IOException {
        String f = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            int count1 = 0;
            int count2 = 0;
            int count3 = 0;
            int count4 = 0;
            RawMessage m;
            while ((m = d.nextMessage()) != null){
                Message m1 = MessageParser.parse(m);
                if (m1 == null) {
                    ++count4;
                    continue;
                }
                switch (m1){
                    case AirbornePositionMessage aim -> ++count1;
                    case AircraftIdentificationMessage aim -> ++count2;
                    case AirborneVelocityMessage aim -> ++count3;
                    default -> {}
                }
            }
            assertEquals(14,count2);
            assertEquals(137,count1);
            assertEquals(147,count3);
            assertEquals(86,count4);
        }
    }
}