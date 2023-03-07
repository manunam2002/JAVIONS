package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class PowerComputerTest {

    @Test
    void powerComputerWorksWithGivenInputStream() throws IOException {
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerComputer powerComputer = new PowerComputer(new FileInputStream(name),16);
        int[] readBatch = new int[16];
        powerComputer.readBatch(readBatch);
        int[] expectedBatch = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        for (int i = 0 ; i < 10 ; ++i){
            assertEquals(expectedBatch[i],readBatch[i]);
        }
    }

    @Test
    void powerComputerWorksWithFirst160Values() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerComputer powerComputer = new PowerComputer(new FileInputStream(name),160);
        int[] readBatch = new int[160];
        powerComputer.readBatch(readBatch);
        for (int i = 0 ; i < 160 ; ++i){
            System.out.print(readBatch[i]+" ");
        }
    }

    @Test
    void powerComputerConstructorThrowsIllegalArgumentException(){
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        final String fileName = name;
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(
                new FileInputStream(fileName),-8));
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(
                new FileInputStream(fileName), 0));
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(
                new FileInputStream(fileName), 15));
    }

    @Test
    void powerComputerReadThrowsIllegalArgumentException() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerComputer powerComputer = new PowerComputer(new FileInputStream(name),16);
        int[] readBatch = new int[15];
        assertThrows(IllegalArgumentException.class, () -> powerComputer.readBatch(readBatch));
    }

    @Test
    void powerComputerReadAllSamples() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerComputer powerComputer = new PowerComputer(new FileInputStream(name),1208);
        int[] readBatch = new int[1208];
        powerComputer.readBatch(readBatch);
        for (int i = 0; i < 1201 ; ++i) {
            assertNotEquals(0,readBatch[i]);
        }
        for (int i = 1201; i < 1208 ;++i){
            assertEquals(0,readBatch[i]);
        }
        /////////////////////
    }

    @Test
    void readBatchReturnsTheCorrectNumber() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerComputer powerComputer = new PowerComputer(new FileInputStream(name),1208);
        int[] readBatch = new int[1208];
        assertEquals(1201,powerComputer.readBatch(readBatch));
    }
}