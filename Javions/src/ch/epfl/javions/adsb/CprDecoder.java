package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

/**
 * représente un décodeur de position CPR
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class CprDecoder {

    private static final int latZones0 = 60;
    private static final int latZones1 = 59;

    private static final double minLat = Units.convert(0,Units.Angle.DEGREE,Units.Angle.TURN);

    private static final double maxLat = Units.convert(180,Units.Angle.DEGREE,Units.Angle.TURN);

    private static final double lonZonesCalculator = 1 - Math.cos(2 * Math.PI * (1.0/latZones0));

    /**
     * retourne la positon géographique correspondant aux positions locales normalisées données
     * @param x0 la longitude locale d'un message pair
     * @param y0 la latitude locale d'un message pair
     * @param x1 la longitude locale d'un message impair
     * @param y1 la latitude locale d'un message impair
     * @param mostRecent l'index des positions les plus récentes
     * @return la positon géographique correspondant aux positions locales normalisées données
     * @throws IllegalArgumentException si mostRecent ne vaut pas 0 ou 1
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        if ((mostRecent != 0) && (mostRecent != 1)) throw new IllegalArgumentException();
        int latZone = (int) Math.rint(y0*latZones1 - y1*latZones0);
        int latZone0;
        int latZone1;
        if (latZone < 0){
            latZone0 = latZone+latZones0;
            latZone1 = latZone+latZones1;
        } else {
            latZone0 = latZone;
            latZone1 = latZone;
        }
        double lat0 = (1.0/latZones0) * (latZone0 + y0);
        double lat1 = (1.0/latZones1) * (latZone1 + y1);
        double lat0Rad = Units.convertFrom(lat0,Units.Angle.TURN);
        double lat1Rad = Units.convertFrom(lat1,Units.Angle.TURN);
        if (lat0 < minLat || lat0 > maxLat) return null;
        double a0 = Math.acos(1 - (lonZonesCalculator / (Math.cos(lat0Rad) * Math.cos(lat0Rad))));
        double a1 = Math.acos(1 - (lonZonesCalculator / (Math.cos(lat1Rad) * Math.cos(lat1Rad))));
        int lonZones0;
        if (Double.isNaN(a0)){
            lonZones0 = 1;
            if (!Double.isNaN(a1)) return null;
        } else {
            lonZones0 = (int) Math.floor(2 * Math.PI / a0);
            if (lonZones0 != (int) Math.floor(2 * Math.PI / a1)) return null;
        }
        int lonZones1 = lonZones0 -1;
        int lonZone = (int) Math.rint(x0*lonZones1 - x1*lonZones0);
        int lonZone0;
        int lonZone1;
        if (lonZone < 0){
            lonZone0 = lonZone+lonZones0;
            lonZone1 = lonZone+lonZones1;
        } else {
            lonZone0 = lonZone;
            lonZone1 = lonZone;
        }
        double lon0 = (1.0/lonZones0) * (lonZone0 + x0);
        double lon1 = (1.0/lonZones1) * (lonZone1 + x1);
        if (mostRecent == 0){
            if (lon0 > 0.5) lon0 -= 1;
            if (lat0 > 0.5) lat0 -= 1;
            return new GeoPos((int) Units.convert(lon0,Units.Angle.TURN,Units.Angle.T32),
                    (int) Units.convert(lat0,Units.Angle.TURN,Units.Angle.T32));
        }
        if (lon1 > 0.5) lon1 -= 1;
        if (lat1 > 0.5) lat1 -= 1;
        return new GeoPos((int) Units.convert(lon1,Units.Angle.TURN,Units.Angle.T32),
                (int) Units.convert(lat1,Units.Angle.TURN,Units.Angle.T32));
    }
}
