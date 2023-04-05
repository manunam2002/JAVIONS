package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Comparator;

public class AircraftStateManagerTest {

    @Test
    void readGivenFile() throws IOException {
        String filename = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/messages_20230318_0915.bin";
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(filename)))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }
        } catch (EOFException e) { /* nothing to do */ }
    }

    @Test
    void AircraftStateManagerWorksOnGivenFile() throws IOException{
        String aircraftDataBase = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/aircraft.zip";
        AircraftStateManager aircraftStateManager = new AircraftStateManager(new AircraftDatabase(aircraftDataBase));
        String filename = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/messages_20230318_0915.bin";
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(filename)))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                RawMessage rawMessage = new RawMessage(timeStampNs,message);
                Message message1 = MessageParser.parse(rawMessage);
                aircraftStateManager.updateWithMessage(message1);
                aircraftStateManager.purge();
                for (ObservableAircraftState observableAircraftState :
                        aircraftStateManager.states()) {
                    if (observableAircraftState.getAircraftData() != null &&
                            observableAircraftState.getIcaoAddress() != null &&
                            observableAircraftState.getAircraftData().registration() != null &&
                            observableAircraftState.getAircraftData().model() != null){
                        System.out.printf("%6s   %6s   %31s   %8f   %8f   %5f   %3f\n",
                                observableAircraftState.getIcaoAddress().string(),
                                observableAircraftState.getAircraftData().registration().string(),
                                observableAircraftState.getAircraftData().model(),
                                Units.convertTo(observableAircraftState.getPosition().longitude(),Units.Angle.DEGREE),
                                Units.convertTo(observableAircraftState.getPosition().latitude(),Units.Angle.DEGREE),
                                observableAircraftState.getAltitude(),
                                Units.convert(observableAircraftState.getVelocity(),Units.Speed.METER_PER_SECOND,Units.Speed.KILOMETER_PER_HOUR));
                    }
                }
                if (timeStampNs > 2000000000) break;
            }
        } catch (EOFException e) { /* nothing to do */ }
    }

    private static class AddressComparator
            implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1,
                           ObservableAircraftState o2) {
            String s1 = o1.getIcaoAddress().string();
            String s2 = o2.getIcaoAddress().string();
            return s1.compareTo(s2);
        }
    }
}