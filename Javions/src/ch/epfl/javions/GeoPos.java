package ch.epfl.javions;

/**
 * représente des coordonnées géographiques
 * @param longitudeT32 longitude exprimée en T32
 * @param latitudeT32 latitude exprimée en T32
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {

    /**
     * latitude maximale en T32 (2^30)
     */
    private final static int MAX_LATITUDE = 1 << 30;

    /**
     * constructeur public compact
     * @param longitudeT32 longitude exprimée en T32
     * @param latitudeT32 latitude exprimée en T32
     * @throws IllegalArgumentException si la latitude reçue est invalide
     */
    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }

    /**
     * vérifie la latitude
     * @param latitudeT32 latitude exprimée en T32
     * @return vrai ssi la valeur passée est valide
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        return latitudeT32 >= -MAX_LATITUDE && latitudeT32 <= MAX_LATITUDE;
    }

    /**
     * longitude en radians
     * @return longitude en radians
     */
    public double longitude(){
        return Units.convertFrom(longitudeT32,Units.Angle.T32);
    }

    /**
     * latitude en radians
     * @return latitude en radians
     */
    public double latitude(){
        return Units.convertFrom(latitudeT32,Units.Angle.T32);
    }

    @Override
    public String toString() {
        return "(" + Units.convertTo(longitude(),Units.Angle.DEGREE) + "°, " +
                "" + Units.convertTo(latitude(),Units.Angle.DEGREE) + "°)";
    }
}
