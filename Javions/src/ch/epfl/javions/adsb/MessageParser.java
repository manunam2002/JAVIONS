package ch.epfl.javions.adsb;

/**
 * transforme les messages ADS-B bruts en messages d'identification, de position en vol ou de vitesse en vol
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class MessageParser {

    /**
     * constructeur privé
     */
    private MessageParser(){}

    /**
     * retourne l'instance correspondant au message brut donné, ou null si le code de type
     * de ce dernier ne correspond à aucun de ces trois types de messages, ou s'il est invalide
     * @param rawMessage le message brut donné
     * @return l'instance correspondant au message brut donné
     */
    public static Message parse(RawMessage rawMessage) {
        long typeCode = rawMessage.typeCode();
        if (typeCode > 0 && typeCode < 5) {
            return AircraftIdentificationMessage.of(rawMessage);
        }
        if ((typeCode > 8 && typeCode < 19) ||
                (typeCode > 19 && typeCode < 23)) {
            return AirbornePositionMessage.of(rawMessage);
        }
        if (typeCode == 19) {
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }
}
