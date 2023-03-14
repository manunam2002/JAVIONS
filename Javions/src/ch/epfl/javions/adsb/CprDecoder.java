package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

public class CprDecoder {

    private static final int latZones0 = 60;
    private static final int latZones1 = 59;

    private static final double lonZonesCalculator = 1 - Math.cos(2 * Math.PI * (1.0/latZones0));

    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        if ((mostRecent != 0) && (mostRecent != 1)) throw new IllegalArgumentException();
        double latZone = Math.rint(y0*latZones1 - y1*latZones0);
        double latZone0;
        double latZone1;
        if (latZone < 0){
            latZone0 = latZone+latZones0;
            latZone1 = latZone+latZones1;
        } else {
            latZone0 = latZone;
            latZone1 = latZone;
        }
        double latZone0Rad = Units.convertFrom(latZone0,Units.Angle.TURN);
        double lat0 = (1.0/latZones0) * (latZone0 + y0);
        double lat1 = (1.0/latZones1) * (latZone1 + y1);
        double lonZones = Math.acos(1 - (lonZonesCalculator / (Math.cos(latZone0Rad) * Math.cos(latZone0Rad))));
        double lonZones0 = Math.floor(2 * Math.PI / lonZones);
        double lonZones1 = lonZones0 -1;
        double lonZone = Math.rint(x0*lonZones1 - x1*lonZones0);
        double lonZone0;
        double lonZone1;
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
            //return new GeoPos(Units.convert(lon0,Units.Angle.TURN,Units.Angle.T32),
            //        Units.convert(lat0,Units.Angle.TURN,Units.Angle.T32));
        }
        return null;
    }
}
