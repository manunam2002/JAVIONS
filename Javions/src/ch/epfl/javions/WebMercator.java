package ch.epfl.javions;

/**
 * contient des méthodes permettant de projeter des coordonnées géographiques selon la projection WebMercator
 */
public final class WebMercator {

    /**
     * la coordonnée x correspondant à la longitude donnée en radians au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @param longitude longitude en radians
     * @return la coordonnée x correspondante
     */
    public static double x (int zoomLevel, double longitude){
        return Math.scalb((longitude/(2*Math.PI))+0.5,8+zoomLevel);
    }

    /**
     * la coordonnée y correspondant à la latitude donnée en radians au niveau de zoom donné
     * @param zoomLevel niveau de zoom
     * @param latitude latitude en radians
     * @return la coordonnée y correspondante
     */
    public static double y (int zoomLevel, double latitude){
        return Math.scalb(-(Math2.asinh(Math.tan(latitude)))/(2*Math.PI)+0.5,8+zoomLevel);
    }
}



