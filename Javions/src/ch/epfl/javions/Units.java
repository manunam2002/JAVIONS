package ch.epfl.javions;

/**
 * contient les définitions des préfixes SI utiles au projet
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class Units {

    /**
     * constructeur privé
     */
    private Units(){}

    public static final double CENTI = 1e-2;

    public static final double KILO = 1e3;

    /**
     * contient les définitions des unités d'angle
     */
    public final static class Angle{

        /**
         * constructeur privé
         */
        private Angle(){}

        public static final double RADIAN = 1;

        public static final double TURN = 2*Math.PI*RADIAN;

        public static final double DEGREE = TURN/360;

        public static final double T32 = Math.scalb(TURN,-32);
    }

    /**
     * contient les définitions des unités de longueur
     */
    public final static class Length {

        /**
         * constructeur privé
         */
        private Length(){}

        public static final double METER = 1;

        public static final double CENTIMETER = CENTI*METER;

        public static final double KILOMETER = KILO*METER;

        public static final double INCH = 2.54*CENTIMETER;

        public static final double FOOT = 12*INCH;

        public static final double NAUTICAL_MILE = 1852*METER;
    }

    /**
     * contient les définitions des unités de temps
     */
    public final static class Time{

        /**
         * constructeur privé
         */
        private Time(){}

        public static final double SECOND = 1;

        public static final double MINUTE = 60*SECOND;

        public static final double HOUR = 60*MINUTE;
    }

    /**
     * contient les définitions des unités de vitesse
     */
    public final static class Speed{

        /**
         * constructeur privé
         */
        private Speed(){}

        //à verifier
        public static final double METER_PER_SECOND = Length.METER/Time.SECOND;

        public static final double KNOT = Length.NAUTICAL_MILE/Time.HOUR;

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