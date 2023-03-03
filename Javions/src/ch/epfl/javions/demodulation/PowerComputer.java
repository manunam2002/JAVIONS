package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class PowerComputer {

    private InputStream stream;
    private int batchSize;
    private SamplesDecoder samplesDecoder;
    private short[] batchDecoded;
    private short[] lastSamples = new short[8];

    public PowerComputer(InputStream stream, int batchSize) throws IOException {
        if (batchSize % 8 != 0 || batchSize <= 0) throw new IllegalArgumentException();
        if (stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
        samplesDecoder = new SamplesDecoder(stream,batchSize);
        batchDecoded = new short[batchSize];
    }

    public int readBatch(int[] batch) throws IOException{
        int decodedSamples = samplesDecoder.readBatch(batchDecoded);
        for(int i = 0 ; i < decodedSamples ; ++i){
            int index = i%8;
            lastSamples[index] = batchDecoded[i];
            if (i%2 == 0) batch[i] = powerCalculator(index,lastSamples);
        }
        return decodedSamples/2;
    }

    private int powerCalculator(int index, short[] lastSamples){
        int[] calculatedIndex = new int[8];
        for (int i = 0 ; i < 8 ; i++){
            calculatedIndex[i] = indexCalculator(index,i);
        }
        int evenSamples = lastSamples[calculatedIndex[6]]-lastSamples[calculatedIndex[4]]+
                lastSamples[calculatedIndex[2]]-lastSamples[calculatedIndex[0]];
        int oddSamples = lastSamples[calculatedIndex[7]]-lastSamples[calculatedIndex[5]]+
                lastSamples[calculatedIndex[3]]-lastSamples[calculatedIndex[1]];
        return evenSamples*evenSamples + oddSamples*oddSamples;
    }

    private int indexCalculator(int index, int j){
        int indexJ = index-j;
        if (indexJ < 0) indexJ += 8;
        return indexJ;
    }
}