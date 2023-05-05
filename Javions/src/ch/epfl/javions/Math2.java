package ch.epfl.javions;

/**
 * permet d'effectuer certains calculs mathématiques
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class Math2 {

    /**
     * constructeur privé
     */
    private Math2(){}

    /**
     * limite la valeur v à l'intervalle allant de min à max
     * @param min valeur minimale de l'intervalle
     * @param v valeur donnée
     * @param max valeur maximale de l'intervalle
     * @return min si v est inférieure à min, max si v est supérieure à max, et v sinon
     * @throws IllegalArgumentException si min est (strictement) supérieur à max
     */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min <= max);
        return Math.min(Math.max(v, min),max);
    }

    /**
     * sinus hyperbolique
     * @param x argument
     * @return sinus hyperbolique réciproque de son argument x
     */
    public static double asinh(double x){
        return Math.log(x + Math.hypot(1, x));
    }
}