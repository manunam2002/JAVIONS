package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {

    private InputStream stream;
    private int windowSize;
    private PowerComputer powerComputer;
    private int[] evenBatch;
    private int[] oddBatch;
    private int position;

    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        if (windowSize <= 0 || windowSize > 65536) throw new IllegalArgumentException();
        this.stream = stream;
        this.windowSize = windowSize;
        powerComputer = new PowerComputer(stream,windowSize);
        evenBatch = new int[windowSize];
        oddBatch = new int[windowSize];
        powerComputer.readBatch(evenBatch);
        powerComputer.readBatch(oddBatch);
    }

    public int size(){
        return windowSize;
    }

    public long position(){
        return position;
    }

    public boolean isFull(){

    }

    public int get(int i){
        if (i <= 0 || i > windowSize) throw new IndexOutOfBoundsException();
        if (i%2 == 0) return evenBatch[i];
        return oddBatch[i];
    }

    public void advance() throws IOException{
        if (!isFull()){
            if (position%2 == 0) oddBatch[position+1] =
        }
    }

    public void advanceBy(int offset) throws IOException{
        if (offset <= 0) throw new IllegalArgumentException();
    }
}