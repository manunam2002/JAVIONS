package ch.epfl.javions;

/**
 * contient la definitions des prefixes SI utiles au projet
 */
public final class Units {

    /**
     * constructeur privé
     */
    private Units(){}

    /**
     * centi
     */
    public static final double CENTI = 1e-2;

    /**
     * kilo
     */
    public static final double KILO = 1e3;

    /**
     * contient les définitions des unités d'angle
     */
    public static class Angle{

        /**
         * radian
         */
        public static final double RADIAN = 1;

        /**
         * turn
         */
        public static final double TURN = 2*Math.PI*RADIAN;

        /**
         * degrée
         */
        public static final double DEGREE = TURN/360;

        /**
         * t32
         */
        public static final double T32 = Math.scalb(TURN,-32);
    }

    /**
     * contient les définitions des unités de longueur
     */
    public static class Length {

        /**
         * mètre
         */
        public static final double METER = 1;

        /**
         * centimètre
         */
        public static final double CENTIMETER = CENTI*METER;

        /**
         * kilomètre
         */
        public static final double KILOMETER = KILO*METER;

        /**
         * pouce
         */
        public static final double INCH = 2.54*CENTIMETER;

        /**
         * pied
         */
        public static final double FOOT = 12*INCH;

        /**
         * mile nautique
         */
        public static final double NAUTICAL_MILE = 1852*METER;
    }

    /**
     * contient les définitions des unités de temps
     */
    public static class Time{

        /**
         * seconde
         */
        public static final double SECOND = 1;

        /**
         * minute
         */
        public static final double MINUTE = 60*SECOND;

        /**
         * heure
         */
        public static final double HOUR = 60*MINUTE;
    }

    /**
     * contient les définitions des unités de vitesse
     */
    public static class Speed{

        /**
         * mètre par seconde
         */
        public static final double METER_PER_SECOND = Length.METER/Time.SECOND;

        /**
         * noeud
         */
        public static final double KNOT = Length.NAUTICAL_MILE/Time.HOUR;

        /**
         * kilomètre par heure
         */
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER/Time.HOUR;
    }

    /**
     * convertit la valeur donnée, exprimée dans l'unité fromUnit, en l'unité toUnit
     * @param value la valeur donnée
     * @param fromUnit l'unité de départ
     * @param toUnit l'unité d'arrivée
     * @return la valeur dans l'unité d'arrivée
     */
    public static double convert(double value, double fromUnit, double toUnit){
        return value * (fromUnit / toUnit);
    }

    /**
     * convertit la valeur donnée, exprimée dans l'unité fromUnit, en l'unité de base
     * @param value la valeur donnée
     * @param fromUnit l'unité de départ
     * @return la valeur dans l'unité de base
     */
    public static double convertFrom(double value, double fromUnit){
        return value * fromUnit;
    }

    /**
     * convertit la valeur donnée, exprimée dans l'unité de base, en l'unité toUnit
     * @param value la valeur donnée
     * @param toUnit l'unité d'arrivée
     * @return la valeur dans l'unité d'arrivée
     */
    public static double convertTo(double value, double toUnit){
        return value * (1 / toUnit);
    }
}