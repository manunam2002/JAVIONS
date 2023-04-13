package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AircraftStateManagerTest {

    final char[] directions = new char[]{'↑','↗','→','↘','↓','↙','←','↖'};

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
                //System.out.printf("%13d: %s\n", timeStampNs, message);
                if (message.bytesInRange(1,4) == 0x4D2442){
                    System.out.printf("%13d: %s\n", timeStampNs, message);
                }
            }
        } catch (EOFException e) { /* nothing to do */ }
    }

    @Test
    void AircraftStateManagerWorksOnGivenFile() throws IOException, InterruptedException{
        String aircraftDataBase = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/aircraft.zip";
        AircraftStateManager aircraftStateManager = new AircraftStateManager(new AircraftDatabase(aircraftDataBase));
        String filename = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/messages_20230318_0915.bin";
        List<ObservableAircraftState> list = new ArrayList<>();
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
                if (System.nanoTime() < message1.timeStampNs()){
                    Thread.sleep(message1.timeStampNs() - System.nanoTime());
                }
                aircraftStateManager.updateWithMessage(message1);
                aircraftStateManager.purge();

                if (aircraftStateManager.states().size() == 8){
                    list.addAll(aircraftStateManager.states());
                    list.sort(new AddressComparator());

                    for (ObservableAircraftState observableAircraftState :
                            list) {
                        String callSign = observableAircraftState.getCallSign() == null ? "" : observableAircraftState.getCallSign().string();
                        if (observableAircraftState.getAircraftData() != null &&
                                observableAircraftState.getIcaoAddress() != null &&
                                observableAircraftState.getAircraftData().registration() != null &&
                                observableAircraftState.getAircraftData().model() != null){
                            System.out.printf("%6s   %6s   %6s   %31s   %8f   %8f   %5f   %3f   %1s\n",
                                    observableAircraftState.getIcaoAddress().string(),
                                    callSign,
                                    observableAircraftState.getAircraftData().registration().string(),
                                    observableAircraftState.getAircraftData().model(),
                                    Units.convertTo(observableAircraftState.getPosition().longitude(),Units.Angle.DEGREE),
                                    Units.convertTo(observableAircraftState.getPosition().latitude(),Units.Angle.DEGREE),
                                    observableAircraftState.getAltitude(),
                                    Units.convert(observableAircraftState.getVelocity(),Units.Speed.METER_PER_SECOND,Units.Speed.KILOMETER_PER_HOUR),
                                    returnDirection(observableAircraftState.getTrackOrHeading()));
                        }
                    }
                    break;
                }
            }
        } catch (EOFException e) { /* nothing to do */ }
    }

    private char returnDirection(double trackOrHeading){
        if (trackOrHeading > 15*Math.PI/8 || trackOrHeading < Math.PI/8) return directions[0];
        if (trackOrHeading > Math.PI/8 && trackOrHeading < 3*Math.PI/8) return directions[1];
        if (trackOrHeading > 3*Math.PI/8 && trackOrHeading < 5*Math.PI/8) return directions[2];
        if (trackOrHeading > 5*Math.PI/8 && trackOrHeading < 7*Math.PI/8) return directions[3];
        if (trackOrHeading > 7*Math.PI/8 && trackOrHeading < 9*Math.PI/8) return directions[4];
        if (trackOrHeading > 9*Math.PI/8 && trackOrHeading < 11*Math.PI/8) return directions[5];
        if (trackOrHeading > 11*Math.PI/8 && trackOrHeading < 13*Math.PI/8) return directions[6];
        if (trackOrHeading > 13*Math.PI/8 && trackOrHeading < 15*Math.PI/8) return directions[7];
        return ' ';
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