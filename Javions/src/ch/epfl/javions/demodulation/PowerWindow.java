package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * représente une fenêtre de taille fixe sur une séquence d'échantillons de puissance
 * produits par un calculateur de puissance
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class PowerWindow {

    private final int windowSize;

    private final PowerComputer powerComputer;

    private int[] Batch1;

    private final int[] Batch2;

    private int position = 0;

    private int currentBatch = 0;

    private final static int BATCH_SIZE = 65536;

    private int batchRead = 0;

    /**
     * constructeur public
     * @param stream le flot d'entrée donné
     * @param windowSize la taille de la fenêtre
     * @throws IOException en cas d'erreur d'entrée
     * @throws IllegalArgumentException si la taille de la fenêtre donnée n'est pas comprise entre 0 (exclu)
     * et 65536 (la taille d'un lot)
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);
        this.windowSize = windowSize;
        powerComputer = new PowerComputer(stream, BATCH_SIZE);
        Batch1 = new int[BATCH_SIZE];
        Batch2 = new int[BATCH_SIZE];
        batchRead += powerComputer.readBatch(Batch1);
    }

    /**
     * retourne la taille de la fenêtre
     * @return la taille de la fenêtre
     */
    public int size(){
        return windowSize;
    }

    /**
     * retourne la position actuelle de la fenêtre par rapport au début du flot de valeurs de puissance
     * @return la position actuelle
     */
    public long position(){
        return position;
    }

    /**
     * retourne vrai ssi la fenêtre contient autant d'échantillons que sa taille
     * @return vrai ssi la fenêtre contient autant d'échantillons que sa taille
     */
    public boolean isFull(){
        return ((position + windowSize) <= batchRead);
    }

    /**
     * retourne l'échantillon de puissance à l'index donné de la fenêtre
     * @param i l'index donné
     * @return l'échantillon de puissance
     * @throws IndexOutOfBoundsException si cet index n'est pas compris entre 0 (inclus)
     * et la taille de la fenêtre (exclu)
     */
    public int get(int i){
        Objects.checkIndex(i,windowSize);
        int index = position + i - currentBatch*BATCH_SIZE;
        if (index >= BATCH_SIZE){
            return Batch2[index - BATCH_SIZE];
        }
        return Batch1[index];
    }

    /**
     * avance la fenêtre d'un échantillon
     * @throws IOException en cas d'erreur d'entrée
     */
    public void advance() throws IOException{
        ++position;
        if (position + windowSize == BATCH_SIZE * (currentBatch + 1)){
            batchRead += powerComputer.readBatch(Batch2);
        }
        if (position == BATCH_SIZE * (currentBatch + 1)){
            Batch1 = Arrays.copyOf(Batch2, BATCH_SIZE);
            ++currentBatch;
        }
    }

    /**
     * avance la fenêtre du nombre d'échantillons donné
     * @param offset le nombre d'échantillons donné
     * @throws IOException en cas d'erreur d'entrée
     * @throws IllegalArgumentException si l'offset est négatif
     */
    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset >= 0);
        for(int i = 0 ; i < offset ; ++i){
            advance();
        }
    }
}