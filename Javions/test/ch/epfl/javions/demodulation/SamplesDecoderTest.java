package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class SamplesDecoderTest {

    @Test
    void sampleDecoderWorksWithGivenInputStream() throws IOException {
        SamplesDecoder samplesDecoder = new SamplesDecoder(new FileInputStream("/Users/manucristini/EPFLBA2/" +
                "CS108/Projets/Javions/resources/samples.bin"),10);
        short[] readBatch = new short[10];
        samplesDecoder.readBatch(readBatch);
        short[] expectedBatch = {-3,8,-9,-8,-5,-8,-12,-16,-23,-9};
        for (int i = 0 ; i < 10 ; ++i){
            assertEquals(expectedBatch[i],readBatch[i]);
        }
    }

    @Test
    void sampleDecoderConstructorThrowsIllegalArgumentException(){

    }

    @Test
    void sampleDecoderConstructorThrowsNullPointerException(){

    }

    @Test
    void sampleDecoderReadThrowsIllegalArgumentException(){

    }
}