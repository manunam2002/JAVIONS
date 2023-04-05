package ch.epfl.javions;

/**
 * préconditions nécessaires avant de faire appel à une méthode
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class Preconditions {

    /**
     * constructeur privé
     */
    private Preconditions (){}

    /**
     * lève l'exception IllegalArgumentException si son paramètre est faux et ne fait rien sinon
     *
     * @param shouldBeTrue doit être vrai
     * @throws IllegalArgumentException si le paramètre est faux
     */
    public static void checkArgument (boolean shouldBeTrue){
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }

}


