package ch.epfl.javions;

/**
 * contient des méthodes permettant de projeter des coordonnées géographiques selon la projection WebMercator
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class WebMercator {

    /**
     * constructeur privé
     */
    private WebMercator(){}

    /**
     * la coordonnée x correspondant à la longitude donnée en radians au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @param longitude longitude en radians
     * @return la coordonnée x correspondante
     */
    public static double x (int zoomLevel, double longitude){
        return value(( Units.convertTo(longitude,Units.Angle.TURN)) + 0.5, zoomLevel);
    }

    /**
     * la coordonnée y correspondant à la latitude donnée en radians au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @param latitude latitude en radians
     * @return la coordonnée y correspondante
     */
    public static double y (int zoomLevel, double latitude){
        return value(- (Math2.asinh(Math.tan(latitude))) / (2*Math.PI) + 0.5, zoomLevel);
    }

    private static double value(double v, int zoomLevel){
        return Math.scalb(v, 8 + zoomLevel);
    }
}



