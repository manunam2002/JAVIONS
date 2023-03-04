package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
    void powerWindowSizeWorks() throws IOException{
        PowerWindow powerWindow = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 10);
        assertEquals(10,powerWindow.size());
        PowerWindow powerWindow1 = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 1);
        assertEquals(1,powerWindow1.size());
        PowerWindow powerWindow2 = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 65536);
        assertEquals(65536,powerWindow2.size());
    }

    @Test
    void powerWindowPositionWorks() throws IOException{
        PowerWindow powerWindow = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 10);
        assertEquals(0,powerWindow.position());
        powerWindow.advance();
        assertEquals(1,powerWindow.position());
    }

    @Test
    void powerWindowIsFullWorks() throws IOException{
        PowerWindow powerWindow = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 10);
        assertTrue(powerWindow.isFull());
        powerWindow.advanceBy(65530);
        assertFalse(powerWindow.isFull());
    }

    @Test
    void powerWindowGetWorks() throws IOException{
        PowerWindow powerWindow = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 10);
        assertEquals(73,powerWindow.get(0));
        assertEquals(4226,powerWindow.get(5));
        assertEquals(23825,powerWindow.get(9));
    }

    @Test
    void powerWindowAdvanceWorks() throws IOException{
        PowerWindow powerWindow = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 9);
        assertEquals(36818,powerWindow.get(8));
        assertEquals(73,powerWindow.get(0));
        powerWindow.advance();
        assertEquals(23825,powerWindow.get(8));
        assertEquals(292,powerWindow.get(0));
    }

    @Test
    void powerWindowAdvanceByWorks() throws IOException{
        PowerWindow powerWindow = new PowerWindow(new FileInputStream("/Users/manucristini/EPFLBA2/CS108/" +
                "Projets/Javions/resources/samples.bin"), 6);
        assertEquals(4226,powerWindow.get(5));
        assertEquals(73,powerWindow.get(0));
        powerWindow.advanceBy(4);
        assertEquals(23825,powerWindow.get(5));
        assertEquals(98,powerWindow.get(0));
    }
}