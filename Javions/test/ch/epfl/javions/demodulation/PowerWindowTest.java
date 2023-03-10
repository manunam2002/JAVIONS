package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class PowerWindowTest {

    @Test
    void powerWindowWorksWithGivenInputStream() throws IOException {
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerWindow powerWindow = new PowerWindow(new FileInputStream(name), 10);
    }

    @Test
    void powerWindowConstructorThrowsIllegalArgumentException(){
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        final String fileName = name;
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream(fileName), 0));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream(fileName), 65537));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream(fileName), -3));
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(
                new FileInputStream(fileName), 66000));
    }

    @Test
    void powerWindowSizeWorks() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerWindow powerWindow = new PowerWindow(new FileInputStream(name), 10);
        assertEquals(10,powerWindow.size());
        PowerWindow powerWindow1 = new PowerWindow(new FileInputStream(name), 1);
        assertEquals(1,powerWindow1.size());
        PowerWindow powerWindow2 = new PowerWindow(new FileInputStream(name), 65536);
        assertEquals(65536,powerWindow2.size());
    }

    @Test
    void powerWindowPositionWorks() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerWindow powerWindow = new PowerWindow(new FileInputStream(name), 10);
        assertEquals(0,powerWindow.position());
        powerWindow.advance();
        assertEquals(1,powerWindow.position());
        powerWindow.advanceBy(8);
        assertEquals(9,powerWindow.position());
        powerWindow.advanceBy(1191);
        assertEquals(1200,powerWindow.position());
    }

    // à contrôler !!
    @Test
    void powerWindowIsFullWorks() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerWindow powerWindow = new PowerWindow(new FileInputStream(name), 10);
        assertTrue(powerWindow.isFull());
        powerWindow.advanceBy(1000);
        assertTrue(powerWindow.isFull());
        powerWindow.advanceBy(64530);
        assertFalse(powerWindow.isFull());

        String f = "/Users/manucristini/EPFLBA2/CS108/Projets/Javions/resources/samples_20230304_1442.bin";
        PowerWindow powerWindow1 = new PowerWindow(new FileInputStream(f), 1200);
        assertTrue(powerWindow1.isFull());
        powerWindow1.advanceBy(74998799);
        assertTrue(powerWindow1.isFull());
        powerWindow1.advance();
        assertFalse(powerWindow1.isFull());
    }

    @Test
    void powerWindowGetWorks() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerWindow powerWindow = new PowerWindow(new FileInputStream(name), 10);
        assertEquals(73,powerWindow.get(0));
        assertEquals(4226,powerWindow.get(5));
        assertEquals(23825,powerWindow.get(9));
        powerWindow.advanceBy(1201);
        assertEquals(0,powerWindow.get(0));
    }

    @Test
    void powerWindowAdvanceWorks() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerWindow powerWindow = new PowerWindow(new FileInputStream(name), 9);
        assertEquals(36818,powerWindow.get(8));
        assertEquals(73,powerWindow.get(0));
        powerWindow.advance();
        assertEquals(23825,powerWindow.get(8));
        assertEquals(292,powerWindow.get(0));
        powerWindow.advance();
        assertEquals(23825,powerWindow.get(7));
        assertEquals(65,powerWindow.get(0));
    }

    @Test
    void powerWindowAdvanceByWorks() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        PowerWindow powerWindow = new PowerWindow(new FileInputStream(name), 6);
        assertEquals(4226,powerWindow.get(5));
        assertEquals(73,powerWindow.get(0));
        powerWindow.advanceBy(4);
        assertEquals(23825,powerWindow.get(5));
        assertEquals(98,powerWindow.get(0));
        powerWindow.advanceBy(5);
        assertEquals(23825,powerWindow.get(0));
    }
}