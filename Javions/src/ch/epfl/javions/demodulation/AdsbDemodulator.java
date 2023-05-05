package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * représente un démodulateur de messages ADS-B
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class AdsbDemodulator {

    private static final byte[] MESSAGE = new byte[RawMessage.LENGTH];

    private long timeStampNs = 0;

    private final static int TIME_INTERVAL = 100;

    private final static int WINDOW_SIZE = 1200;

    private static final int MESSAGE_LENGTH = 112;

    private final PowerWindow powerWindow;

    /**
     * constructeur public
     *
     * @param samplesStream le flot donné
     * @throws IOException en cas d'erreur d'entrée
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
    }

    /**
     * retourne le prochain message ADS-B du flot d'échantillons passé au constructeur ou null s'il n'y en a plus
     *
     * @return le prochain message ADS-B du flot d'échantillons
     * @throws IOException en cas d'erreur d'entrée
     */
    public RawMessage nextMessage() throws IOException {
        RawMessage rawMessage = null;

        while (rawMessage == null) {
            if (!powerWindow.isFull()) break;

            while (!preambleTest()) {
                powerWindow.advance();
                timeStampNs += TIME_INTERVAL;
            }
            powerWindow.advance();
            timeStampNs += TIME_INTERVAL;

            Arrays.fill(MESSAGE, (byte) 0);
            for (int index = 0; index < Byte.SIZE; ++index) {
                if (bitsDecoder(index))
                    MESSAGE[0] = (byte) (MESSAGE[0] | 0x80 >>> (index % Byte.SIZE));
            }

            if (RawMessage.size(MESSAGE[0]) != 0) {
                for (int index = Byte.SIZE; index < MESSAGE_LENGTH; ++index) {
                    if (bitsDecoder(index))
                        MESSAGE[index / Byte.SIZE] = (byte) (MESSAGE[index / Byte.SIZE] | 0x80 >>> (index % Byte.SIZE));
                }
                rawMessage = RawMessage.of(timeStampNs, MESSAGE);
                if (rawMessage != null) {
                    timeStampNs += WINDOW_SIZE * TIME_INTERVAL;
                    powerWindow.advanceBy(WINDOW_SIZE);
                } else {
                    timeStampNs += TIME_INTERVAL;
                    powerWindow.advance();
                }
            } else {
                powerWindow.advance();
                timeStampNs += TIME_INTERVAL;
            }
        }
        return rawMessage;
    }

    /**
     * vérifie si un préambule commence au début de la fenêtre
     *
     * @return true si un préambule commence au début de la fenêtre, false sinon
     */
    private boolean preambleTest() {
        int sumHigh = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
        int sumLow = powerWindow.get(6) + powerWindow.get(16) + powerWindow.get(21) + powerWindow.get(26)
                + powerWindow.get(31) + powerWindow.get(41);
        int sumHighLeft = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int sumHighRight = powerWindow.get(2) + powerWindow.get(12) + powerWindow.get(37) + powerWindow.get(47);
        return ( (sumHigh >= 2 * sumLow) && (sumHigh > sumHighRight) && (sumHigh > sumHighLeft) );
    }

    /**
     * compare la puissance du signal au centre de deux périodes de 0.5 millisecondes
     *
     * @param index l'index donné
     * @return true si l'inpulsion transmise correspond à 1, false sinon
     */
    private boolean bitsDecoder(int index) {
        return powerWindow.get( 80 + (10 * index) ) >= powerWindow.get( 85 + (10 * index) );
    }
}