package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.demodulation.AdsbDemodulator;

/**
 * représente un décodeur de position CPR
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class CprDecoder {

    /**
     * constructeur privé
     */
    private CprDecoder(){}

    private static final int LAT_ZONES_0 = 60;
    private static final int LAT_ZONES_1 = 59;

    private static final double LON_ZONES_CALCULATOR = 1 - Math.cos(2 * Math.PI * (1.0/ LAT_ZONES_0));

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
        Preconditions.checkArgument((mostRecent == 0) || (mostRecent == 1));
        int latZoneNum = (int) Math.rint(y0*LAT_ZONES_1 - y1*LAT_ZONES_0);
        int latZoneNum0;
        int latZoneNum1;
        if (latZoneNum < 0){
            latZoneNum0 = latZoneNum+ LAT_ZONES_0;
            latZoneNum1 = latZoneNum+ LAT_ZONES_1;
        } else {
            latZoneNum0 = latZoneNum;
            latZoneNum1 = latZoneNum;
        }
        double lat0 = (1.0/ LAT_ZONES_0) * (latZoneNum0 + y0);
        double lat1 = (1.0/ LAT_ZONES_1) * (latZoneNum1 + y1);
        double lat0Rad = Units.convertFrom(lat0,Units.Angle.TURN);
        double lat1Rad = Units.convertFrom(lat1,Units.Angle.TURN);
        double a0 = Math.acos(1 - (LON_ZONES_CALCULATOR / (Math.cos(lat0Rad) * Math.cos(lat0Rad))));
        double a1 = Math.acos(1 - (LON_ZONES_CALCULATOR / (Math.cos(lat1Rad) * Math.cos(lat1Rad))));
        double lon0;
        double lon1;
        if (Double.isNaN(a0)){
            if (!Double.isNaN(a1)) return null;
            lon0 = x0;
            lon1 = x1;
        } else {
            int lonZones0 = (int) Math.floor(2 * Math.PI / a0);
            if (lonZones0 != (int) Math.floor(2 * Math.PI / a1)) return null;
            int lonZones1 = lonZones0 -1;
            int lonZoneNum = (int) Math.rint(x0*lonZones1 - x1*lonZones0);
            int lonZoneNum0;
            int lonZoneNum1;
            if (lonZoneNum < 0){
                lonZoneNum0 = lonZoneNum+lonZones0;
                lonZoneNum1 = lonZoneNum+lonZones1;
            } else {
                lonZoneNum0 = lonZoneNum;
                lonZoneNum1 = lonZoneNum;
            }
            lon0 = (1.0/lonZones0) * (lonZoneNum0 + x0);
            lon1 = (1.0/lonZones1) * (lonZoneNum1 + x1);
        }

        if (mostRecent == 0){
            return positionCalculator(lon0,lat0);
        }
        return positionCalculator(lon1,lat1);
    }

    /**
     * retourne la position calculée à partir des longitudes et latitudes obtenues par le décodage ou null si la
     * latitude n'est pas valide
     * @param longitude la longitude obtenue
     * @param latitude la latitude obtenue
     * @return la position calculée ou null si la latitude n'est pas valide
     */
    private static GeoPos positionCalculator(double longitude, double latitude){
        if (longitude > 0.5) longitude -= 1;
        if (latitude > 0.5) latitude -= 1;
        int lonT32 = (int) Math.rint(Units.convert(longitude,Units.Angle.TURN,Units.Angle.T32));
        int latT32 = (int) Math.rint(Units.convert(latitude,Units.Angle.TURN,Units.Angle.T32));
        if (!GeoPos.isValidLatitudeT32(latT32)) return null;
        return new GeoPos(lonT32,latT32);
    }
}
