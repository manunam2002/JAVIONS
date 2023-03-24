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
            int count1 = 0;
            int count2 = 0;
            int count3 = 0;
            while ((m = d.nextMessage()) != null){
                if (m.typeCode() > 0 && m.typeCode() < 5){
                    AircraftIdentificationMessage m1 = AircraftIdentificationMessage.of(m);
                    //System.out.println(m1);
                    ++count1;
                }
                if ((m.typeCode() > 8 && m.typeCode() < 19)||(m.typeCode() > 19 && m.typeCode() < 23)){
                    AirbornePositionMessage m2 = AirbornePositionMessage.of(m);
                    System.out.println(m);
                    ++count2;
                }
                if ((m.typeCode() == 19)){
                    AirborneVelocityMessage m3 = AirborneVelocityMessage.of(m);
                    //System.out.println(m3);
                    ++count3;
                }
            }
            System.out.println("Nb of AircraftIdentificationMessages : " +count1);
            System.out.println("Nb of AirbornePositionMessages : " +count2);
            System.out.println("Nb of AirborneVelocityMessage : "+count3);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Elapsed Time in milli seconds: "+ (end2-start2));
    }
}