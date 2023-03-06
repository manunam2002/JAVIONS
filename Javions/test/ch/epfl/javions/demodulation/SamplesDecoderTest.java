package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class SamplesDecoderTest {

    @Test
    void sampleDecoderWorksWithGivenInputStream() throws IOException {
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        SamplesDecoder samplesDecoder = new SamplesDecoder(new FileInputStream(name),10);
        short[] readBatch = new short[10];
        samplesDecoder.readBatch(readBatch);
        short[] expectedBatch = {-3,8,-9,-8,-5,-8,-12,-16,-23,-9};
        for (int i = 0 ; i < 10 ; ++i){
            assertEquals(expectedBatch[i],readBatch[i]);
        }
    }

    @Test
    void sampleDecoderConstructorThrowsIllegalArgumentException(){
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        final String fileName = name;
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(
                new FileInputStream(fileName), -3));
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(
                new FileInputStream(fileName), 0));
    }

    @Test
    void sampleDecoderConstructorThrowsNullPointerException(){
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 10));
    }

    @Test
    void sampleDecoderReadThrowsIllegalArgumentException() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        SamplesDecoder samplesDecoder = new SamplesDecoder(new FileInputStream(name),10);
        short[] readBatch = new short[9];
        assertThrows(IllegalArgumentException.class, () -> samplesDecoder.readBatch(readBatch));
        short[] readBatch1 = new short[0];
        assertThrows(IllegalArgumentException.class, () -> samplesDecoder.readBatch(readBatch1));
    }

    @Test
    void sampleDecoderReadAllSamples() throws IOException{
        String name = getClass().getResource("/samples.bin").getFile();
        name = URLDecoder.decode(name, UTF_8);
        SamplesDecoder samplesDecoder = new SamplesDecoder(new FileInputStream(name),2402);
        short[] readBatch = new short[2402];
        samplesDecoder.readBatch(readBatch);
        for (short i : readBatch) {
            assertNotEquals(-2048,i);
        }
        SamplesDecoder samplesDecoder1 = new SamplesDecoder(new FileInputStream(name),2403);
        short[] readBatch1 = new short[2403];
        samplesDecoder1.readBatch(readBatch1);
        assertEquals(-2048,readBatch1[2402]);
    }
}