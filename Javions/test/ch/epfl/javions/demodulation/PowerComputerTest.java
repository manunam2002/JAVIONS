package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class PowerComputerTest {

    @Test
    void powerComputerWorksWithGivenInputStream() throws IOException {
        PowerComputer powerComputer = new PowerComputer(new FileInputStream("/Users/manucristini/EPFLBA2/" +
                "CS108/Projets/Javions/resources/samples.bin"),16);
        int[] readBatch = new int[16];
        powerComputer.readBatch(readBatch);
        int[] expectedBatch = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        for (int i = 0 ; i < 10 ; ++i){
            assertEquals(expectedBatch[i],readBatch[i]);
        }
    }

    @Test
    void powerComputerConstructorThrowsIllegalArgumentException(){

    }

    @Test
    void powerComputerReadThrowsIllegalArgumentException(){

    }
}