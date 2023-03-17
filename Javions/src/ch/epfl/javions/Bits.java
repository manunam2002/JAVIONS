package ch.epfl.javions;

import java.util.Objects;

/**
 * extrait un sous-ensemble des 64 bits d'une valeur de type long
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class Bits {

    /**
     * qui extrait du vecteur de 64 bits value la plage de size bits commençant au bit d'index start,
     * qu'elle interprète comme une valeur non signée,
     * @param value valeur donnée
     * @param start index de départ
     * @param size taille du vecteur
     * @return le vecteur extrait
     * @throws IllegalArgumentException si la taille n'est pas strictement supérieure à 0 et strictement inférieure à 32
     * @throws IndexOutOfBoundsException si la plage décrite par start et size n'est pas totalement comprise
     * entre 0 (inclus) et 64 (exclu)
     */
    public static int extractUInt(long value, int start, int size){
        if (size <= 0 || size >= Integer.SIZE) throw new IllegalArgumentException();
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        long l = value << Long.SIZE-(size+start);
        long l1 = l >>> Long.SIZE - size;
        return (int) l1;
    }

    /**
     * retourne vrai ssi le bit de value d'index donné vaut 1
     * @param value valeur donnée
     * @param index index
     * @return vrai ssi le bit de value d'index donné vaut 1, faux sinon
     * @throws IndexOutOfBoundsException s'il n'est pas compris entre 0 (inclus) et 64 (exclu)
     */
    public static boolean testBit(long value, int index){
        Objects.checkIndex(index,Long.SIZE);
        long l = value << Long.SIZE - (index+1);
        long l1 = l >>> Long.SIZE - 1;
        return l1 == 1;
    }
}
