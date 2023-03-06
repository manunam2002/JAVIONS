package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

/**
 * représente un calculateur de puissance : un objet capable de calculer les échantillons de puissance
 * du signal à partir des échantillons signés produits par un décodeur d'échantillons
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class PowerComputer {

    private InputStream stream;
    private int batchSize;
    private SamplesDecoder samplesDecoder;
    private short[] batchDecoded;
    private short[] lastSamples = new short[8];

    /**
     * constructeur public
     * @param stream le flot d'entée donné
     * @param batchSize la taille des lots
     * @throws IOException en cas d'erreur d'entrée
     * @throws IllegalArgumentException si la taille des lots n'est pas un multiple de 8 strictement positif
     */
    public PowerComputer(InputStream stream, int batchSize) throws IOException {
        if (batchSize % 8 != 0 || batchSize <= 0) throw new IllegalArgumentException();
        this.stream = stream;
        this.batchSize = batchSize;
        samplesDecoder = new SamplesDecoder(stream,batchSize*2);
        batchDecoded = new short[batchSize*2];
    }

    /**
     * lit depuis le décodeur d'échantillons le nombre d'échantillons nécessaire au calcul d'un lot
     * d'échantillons de puissance, puis les calcule et les place dans le tableau passé en argument
     * @param batch le tavleau où sont placés les échantillons de puissance
     * @return le nombre d'échantillons de puissance placés dans le tableau
     * @throws IOException en cas d'erreur d'entrée
     * @throws IllegalArgumentException si la taille du tableau n'est pas égale à la taille d'un lot
     */
    public int readBatch(int[] batch) throws IOException{
        if (batch.length != batchSize) throw new IllegalArgumentException();
        int decodedSamples = samplesDecoder.readBatch(batchDecoded);
        for(int i = 0 ; i < decodedSamples ; ++i){
            int index = i%8;
            lastSamples[index] = batchDecoded[i];
            if ((i+1)%2 == 0) batch[(i-1)/2] = powerCalculator(index,lastSamples);
        }
        return decodedSamples/2;
    }

    /**
     * calcule un échantillon de puissance
     * @param index l'index du dernier échantillon dans le tableau
     * @param lastSamples les huit derniers échantillons
     * @return l'échantillon de puissance calculé
     */
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

    /**
     * calcule les bons index pour la formule qui calcule l'échantillon de puissance
     * @param index l'index courant
     * @param j l'index à calculer
     * @return le bon index
     */
    private int indexCalculator(int index, int j){
        int indexJ = index-j;
        if (indexJ < 0) indexJ += 8;
        return indexJ;
    }
}