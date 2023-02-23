package ch.epfl.javions;

/**
 * représente des coordonnées géographiques
 * @param longitudeT32 longitude exprimée en T32
 * @param latitudeT32 latitude exprimée en T32
 */
public record GeoPos(int longitudeT32, int latitudeT32) {

    /**
     * constructeur public compact
     * @param longitudeT32 longitude exprimée en T32
     * @param latitudeT32 latitude exprimée en T32
     * @throws IllegalArgumentException si la latitude reçue est invalide
     */
    public GeoPos {
        if (!isValidLatitudeT32(latitudeT32)) throw new IllegalArgumentException();
    }

    /**
     * vérifie la latitude
     * @param latitudeT32 latitude exprimée en T32
     * @return vrai ssi la valeur passée est valide
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        if (latitudeT32 >= -Math.pow(2,30) && latitudeT32 <= Math.pow(2,30)){
            return true;
        } else return false;
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
        return "("+Units.convertTo(longitude(),Units.Angle.DEGREE)+"°, "+Units.convertTo(latitude(),Units.Angle.DEGREE)+"°)";
    }
}
