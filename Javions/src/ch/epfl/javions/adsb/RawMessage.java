package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

/**
 * représente un message ADS-B
 * @param timeStampNs l'horodatage du message exprimé en nanosecondes depuis une origine donnée
 * @param bytes les octets du message
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {

    /**
     * la longueur en octets des messages ADS-B
     */
    public static final int LENGTH = 14;

    private final static Crc24 CRC_24 = new Crc24(Crc24.GENERATOR);

    /**
     * constructeur compact
     * @param timeStampNs l'horodatage du message exprimé en nanosecondes depuis une origine donnée
     * @param bytes les octets du message
     * @throws IllegalArgumentException si l'horodatage est (strictement) négatif,
     * ou si la chaîne d'octets ne contient pas LENGTH octets
     */
    public RawMessage{
        Preconditions.checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH);
    }

    /**
     * qui retourne le message ADS-B brut avec l'horodatage et les octets donnés,
     * ou null si le CRC24 des octets ne vaut pas 0
     * @param timeStampNs l'horodatage du message exprimé en nanosecondes depuis une origine donnée
     * @param bytes les octets du message
     * @return le message ADS-B brut
     */
    public static RawMessage of(long timeStampNs, byte[] bytes){
        if (CRC_24.crc(bytes) != 0) return null;
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    /**
     * retourne la taille d'un message dont le premier octet est celui donné
     * @param byte0 l'attribut DF
     * @return la taille d'un message
     */
    public static int size(byte byte0){
        if (( (byte0 >>> 3) & 0x1F) == 17) return LENGTH;
        return 0;
    }

    /**
     * qui retourne le code de type de l'attribut ME
     * @param payload l'attribut ME
     * @return le code de type
     */
    public static int typeCode(long payload){
        long typeCode = (payload >>> 51) & 0x1F;
        return (int) typeCode;
    }

    /**
     * retourne le format du message
     * @return le format du message
     */
    public int downLinkFormat(){
        return (bytes.byteAt(0) >>> 3);
    }

    /**
     * retourne l'adresse OACI de l'expéditeur du message
     * @return l'adresse OACI de l'expéditeur du message
     */
    public IcaoAddress icaoAddress(){
        int iCAO = (int) bytes.bytesInRange(1,4);
        return new IcaoAddress(HexFormat.of().withUpperCase().toHexDigits(iCAO, 6));
    }

    /**
     * retourne l'attribut ME du message
     * @return l'attribut ME du message
     */
    public long payload(){
        return bytes.bytesInRange(4,11);
    }

    /**
     * retourne le code de type du message
     * @return le code de type du message
     */
    public int typeCode(){
        return typeCode(payload());
    }
}
