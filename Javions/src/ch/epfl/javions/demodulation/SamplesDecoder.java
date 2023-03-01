package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class SamplesDecoder {

    private InputStream stream;
    private int batchSize;
    private byte[] batchBytes;

    public SamplesDecoder(InputStream stream, int batchSize) throws IOException {
        if (batchSize <= 0) throw new IllegalArgumentException();
        if (stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
        batchBytes = new byte[batchSize*2];
    }

    public int readBatch(short[] batch) throws IOException{
        if (batch.length != batchSize) throw new IllegalArgumentException();
        batchBytes = stream.readNBytes(batchSize*2); //close?
        int count = 0;
        for (int i = 0 ; i < batchSize ; ++i){
            batch[i] = (short) (batchBytes[count+1] << 8);
            batch[i] = (short) (batch[i] | batchBytes[count]);
            batch[i] -= 2048;
            count += 2;
        }
        return batchBytes.length/2;
    }
}