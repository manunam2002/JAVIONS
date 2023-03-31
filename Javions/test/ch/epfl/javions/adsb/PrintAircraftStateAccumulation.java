package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class PrintAircraftStateAccumulation {
    public static void main(String[] args) throws IOException {
        String f = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples_20230304_1442.bin";
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        IcaoAddress expectedAddress1 = new IcaoAddress("4B17E5");
        IcaoAddress expectedAddress2 = new IcaoAddress("495299");
        IcaoAddress expectedAddress3 = new IcaoAddress("39D300");
        IcaoAddress expectedAddress4 = new IcaoAddress("4241A9");
        IcaoAddress expectedAddress5 = new IcaoAddress("4B1A00");
        try (InputStream s = new FileInputStream(f)){
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a = new AircraftStateAccumulator<>(new AircraftState());
            while ((m = d.nextMessage()) != null){
                if (!m.icaoAddress().equals(expectedAddress5)) continue;
                Message pm = MessageParser.parse(m);
                if (pm != null) a.update(pm);
            }
        }
    }
}