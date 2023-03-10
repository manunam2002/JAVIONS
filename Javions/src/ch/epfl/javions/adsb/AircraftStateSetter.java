package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * représente l'état d'un aéronef
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public interface AircraftStateSetter {

    /**
     * change l'horodatage du dernier message reçu de l'aéronef à la valeur donnée
     * @param timeStampNs la valeur donnée
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * change la catégorie de l'aéronef à la valeur donnée
     * @param category la valeur donnée
     */
    void setCategory(int category);

    /**
     * change l'indicatif de l'aéronef à la valeur donnée
     * @param callSign la valeur donnée
     */
    void setCallSign(CallSign callSign);

    /**
     * hange la position de l'aéronef à la valeur donnée
     * @param position la valeur donnée
     */
    void setPosition(GeoPos position);

    /**
     * change l'altitude de l'aéronef à la valeur donnée
     * @param altitude la valeur donnée
     */
    void setAltitude(double altitude);

    /**
     * change la vitesse de l'aéronef à la valeur donnée
     * @param velocity la valeur donnée
     */
    void setVelocity(double velocity);

    /**
     * change la direction de l'aéronef à la valeur donnée
     * @param trackOrHeading la valeur donnée
     */
    void setTrackOrHeading(double trackOrHeading);
}