package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator {

    private long timeStampNs = 0;

    private final double timeInterval = 500;

    private PowerWindow powerWindow;

    public AdsbDemodulator(InputStream samplesStream) throws IOException{
        powerWindow = new PowerWindow(samplesStream,1200);
    }

    public RawMessage nextMessage() throws IOException{ //à verifier
        while (!preambleTest()){
            powerWindow.advance();
            timeStampNs += timeInterval;
        }
        powerWindow.advance();
        timeStampNs += timeInterval;
        byte[] message = new byte[14];
        for (int index = 0 ; index < 114 ; ++index){
            if (bitsDecoder(index))
                message[index/8] = (byte) (message[index/8] | 0x01 << (index%8));
        }
        timeStampNs += 1200*timeInterval;
        return new RawMessage(timeStampNs,new ByteString(message));
    }

    private boolean preambleTest(){
        int sumHigh = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
        int sumLow = powerWindow.get(6) + powerWindow.get(16) + powerWindow.get(21) + powerWindow.get(26) +
                powerWindow.get(31) + powerWindow.get(41);
        int sumHighLeft = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int sumHighRight = powerWindow.get(2) + powerWindow.get(12) + powerWindow.get(37) + powerWindow.get(47);
        return (sumHigh >= 2 * sumLow) && (sumHigh>sumHighRight) && (sumHigh>sumHighLeft);
    }

    private boolean bitsDecoder(int index){
        return !(powerWindow.get(80+10*index) < powerWindow.get(85+10*index));
    }
}