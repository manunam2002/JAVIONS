package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PowerWindowTest {

    @Test
    void powerWindowWorksWithGivenInputStream() throws IOException {
        PowerWindow powerWindow = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 10);
    }

    @Test
    void powerWindowConstructorThrowsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream("/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples.bin"),
                0));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream("/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples.bin"),
                65537));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream("/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples.bin"),
                -3));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream("/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples.bin"),
                66000));
    }

    @Test
    void powerWindowSizeWorks(){

    }

    @Test
    void powerWindowPositionWorks(){

    }

    @Test
    void powerWindowIsFullWorks(){

    }

    @Test
    void powerWindowGetWorks(){

    }

    @Test
    void powerWindowAdvanceWorks(){

    }

    @Test
    void powerWindowAdvanceByWorks(){

    }
}