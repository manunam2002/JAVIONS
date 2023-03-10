package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator {

    private long timeStampNs = 0;

    private final double timeInterval = 100; //500; //100? 2.3, troisième paragraphe

    private PowerWindow powerWindow;

    public AdsbDemodulator(InputStream samplesStream) throws IOException{
        powerWindow = new PowerWindow(samplesStream,1200);
    }

    public RawMessage nextMessage() throws IOException{ //à verifier
        RawMessage rawMessage = null;
        while (rawMessage == null) {
            while (!preambleTest()) {
                powerWindow.advance();
                timeStampNs += timeInterval;
            }
            powerWindow.advance();
            timeStampNs += timeInterval;
            byte[] message = new byte[14];
            for (int index = 0; index < 8; ++index) {
                if (bitsDecoder(index))
                    message[0] = (byte) (message[0] | 0x80 >>> (index % 8));
            }
            int df = (message[0] & 0xFF) >>> 3;
            if (df == 17) {
                for (int index = 8; index < 112; ++index) {
                    if (bitsDecoder(index))
                        message[index / 8] = (byte) (message[index / 8] | 0x80 >>> (index % 8));
                }
                rawMessage = RawMessage.of(timeStampNs, message);
                if (rawMessage != null) {
                    timeStampNs += 1200 * timeInterval;
                    powerWindow.advanceBy(1200);
                } else {
                    timeStampNs += timeInterval;
                    powerWindow.advance();
                }
            } else {
                timeStampNs += timeInterval;
                powerWindow.advance();
            }
            if (!powerWindow.isFull()) break;
        }
        return rawMessage;
    }

    private boolean preambleTest(){
        int sumHigh = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
        int sumLow = powerWindow.get(6) + powerWindow.get(16) + powerWindow.get(21) + powerWindow.get(26) +
                powerWindow.get(31) + powerWindow.get(41);
        int sumHighLeft = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int sumHighRight = powerWindow.get(2) + powerWindow.get(12) + powerWindow.get(37) + powerWindow.get(47);
        return ((sumHigh >= 2 * sumLow) && (sumHigh>sumHighRight) && (sumHigh>sumHighLeft));
    }

    private boolean bitsDecoder(int index){
        return !(powerWindow.get(80+(10*index)) < powerWindow.get(85+(10*index)));
    }
}