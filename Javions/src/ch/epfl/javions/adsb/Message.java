package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * représente un message ADS-B
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public interface Message {

    /**
     * qui retourne l'horodatage du message en nanosecondes
     * @return l'horodatage du message en nanosecondes
     */
    long timeStampNs();

    /**
     * retourne l'adresse OACI de l'expéditeur du message
     * @return l'adresse OACI de l'expéditeur du message
     */
    IcaoAddress icaoAddress();

}