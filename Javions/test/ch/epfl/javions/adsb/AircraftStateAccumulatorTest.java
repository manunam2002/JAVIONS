package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AircraftStateAccumulatorTest {

    @Test
    void AircraftStateAccumulatorConstructorThrowsNullPointerException(){
        assertThrows(NullPointerException.class,() -> new AircraftStateAccumulator<AircraftState>(null));
    }

    @Test
    void AircraftStateAccumulatorStateSetterWork(){
        AircraftState aircraftState = new AircraftState();
        AircraftStateAccumulator<AircraftState> aircraftStateAircraftStateAccumulator = new AircraftStateAccumulator<>(aircraftState);
        assertEquals(aircraftState,aircraftStateAircraftStateAccumulator.stateSetter());
    }

    @Test
    void AircraftStateAccumulatorDontSetPositionWithInvalidMessages(){
        RawMessage rawMessage = new RawMessage(0, ByteString.ofHexadecimalString("8D49529958B302E6E15FA352306B"));
        Message message = MessageParser.parse(rawMessage);
        RawMessage rawMessage1 = new RawMessage((long) ((10E9)+1), ByteString.ofHexadecimalString("8D4D222860B985F7F53FAB33CE76"));
        Message message1 = MessageParser.parse(rawMessage1);
        AircraftState aircraftState = new AircraftState();
        AircraftStateAccumulator<AircraftState> aircraftStateAccumulator = new AircraftStateAccumulator<>(aircraftState);
        aircraftStateAccumulator.update(message);
        aircraftStateAccumulator.update(message1);
    }

}