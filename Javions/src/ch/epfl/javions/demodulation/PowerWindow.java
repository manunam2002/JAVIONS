package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class PowerWindow {

    private InputStream stream;
    private int windowSize;
    private PowerComputer powerComputer;
    private int[] Batch1;
    private int[] Batch2;
    private int position;
    private int currentBatch;
    private final int batchSize = 65536;

    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        if (windowSize <= 0 || windowSize > batchSize) throw new IllegalArgumentException();
        this.stream = stream;
        this.windowSize = windowSize;
        powerComputer = new PowerComputer(stream,batchSize);
        Batch1 = new int[batchSize];
        Batch2 = new int[batchSize];
        powerComputer.readBatch(Batch1);
        currentBatch = 0;
    }

    public int size(){
        return windowSize;
    }

    public long position(){
        return (long) currentBatch*batchSize + position;
    }

    public boolean isFull(){
        if (position+windowSize >= batchSize) return false;
        return true;
    }

    public int get(int i){
        if (i <= 0 || i > windowSize) throw new IndexOutOfBoundsException();
        int index = position+i;
        if (index >= batchSize){
            return Batch2[index-batchSize];
        }
        return Batch1[index];
    }

    public void advance() throws IOException{
        ++position;
        if (position+windowSize >= batchSize){
            powerComputer.readBatch(Batch2);
        }
        if (position >= batchSize){
            Batch1 = Arrays.copyOf(Batch2,batchSize);
            position = 0;
            ++currentBatch;
        }
    }

    public void advanceBy(int offset) throws IOException{
        if (offset <= 0) throw new IllegalArgumentException();
        for(int i = 0 ; i < offset ; ++i){
            advance();
        }
    }
}