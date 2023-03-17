package ch.epfl.javions.adsb;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class PrintRawMessages {
    public static void main(String[] args) throws IOException {
        String f = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples_20230304_1442.bin";
        long start2 = System.currentTimeMillis();
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null){
                if (m.typeCode() > 0 && m.typeCode() < 5){
                    AircraftIdentificationMessage m1 = AircraftIdentificationMessage.of(m);
                    System.out.println(m1);
                }
                if ((m.typeCode() > 8 && m.typeCode() < 19)||(m.typeCode() > 19 && m.typeCode() < 23)){
                    System.out.println(m);
                    AirbornePositionMessage m2 = AirbornePositionMessage.of(m);
                    System.out.println(m2);
                }
            }
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Elapsed Time in milli seconds: "+ (end2-start2));
    }
}