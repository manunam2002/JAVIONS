package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

/**
 * représente un décodeur d'échantillons : un objet capable de transformer les octets
 * provenant de la AirSpy en des échantillons de 12 bits signés
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class SamplesDecoder {

    private InputStream stream;
    private int batchSize;
    private byte[] batchBytes;

    /**
     * constructeur public
     * @param stream le flot d'entrée donné
     * @param batchSize la taille des lots
     * @throws IOException en cas d'erreur d'entrée
     * @throws IllegalArgumentException si la taille des lots n'est pas strictement positive
     * @throws NullPointerException si le flot est nul
     */
    public SamplesDecoder(InputStream stream, int batchSize) throws IOException {
        if (batchSize <= 0) throw new IllegalArgumentException();
        if (stream == null) throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
        batchBytes = new byte[batchSize*2];
    }

    /**
     * lit depuis le flot passé au constructeur le nombre d'octets correspondant à un lot
     * puis convertit ces octets en échantillons signés
     * @param batch le tableau ou sont placés les échantillons
     * @return le nombre d'échantillons converti
     * @throws IOException en cas d'erreur d'entrée
     * @throws IllegalArgumentException si la taille du tableau passé en argument n'est pas égale à la taille d'un lot
     */
    public int readBatch(short[] batch) throws IOException{
        if (batch.length != batchSize) throw new IllegalArgumentException();
        int bytesRead = stream.readNBytes(batchBytes, 0, batchSize*2);
        for (int i = 0 ; i < batchSize ; ++i){
            batch[i] = (short) (0xFF & batchBytes[2*i]);
            batch[i] = (short) (batch[i] | batchBytes[2*i+1] << 8);
            batch[i] -= 2048;
        }
        return bytesRead/2;
    }
}