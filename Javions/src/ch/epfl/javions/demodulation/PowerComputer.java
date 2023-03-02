package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class PowerComputer {

    private InputStream stream;
    private int batchSize;
    private byte[] batchBytes;
    private short[] lastSamples = new short[8];
    private int oldIndex = 0;

    public PowerComputer(InputStream stream, int batchSize){
        if (batchSize % 8 != 0 || batchSize <= 0) throw new IllegalArgumentException();
        if (stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
        batchBytes = new byte[batchSize*2];
    }

    public int readBatch(int[] batch) throws IOException{
        SamplesDecoder samplesDecoder = new SamplesDecoder(stream,batchSize);
        short[] batchDecoded = new short[batchSize];
        samplesDecoder.readBatch(batchDecoded);
        for(int i = 0 ; i < batchDecoded.length ; ++i){
            lastSamples[oldIndex] = batchDecoded[i];
            ++oldIndex;
            if (oldIndex == 8) oldIndex = 0;
            int pn = (int) (Math.pow((lastSamples[oldIndex]- lastSamples[3]+ lastSamples[5]- lastSamples[7]),2) +
                    Math.pow((lastSamples[0]- lastSamples[2]+ lastSamples[4]- lastSamples[6]),2));
            batch[i/8] = pn;
        }
        return batchDecoded.length;
    }
}