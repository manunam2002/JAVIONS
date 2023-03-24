package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * représente un message ADS-B d'identification et de catégorie
 * @param timeStampNs l'horodatage du message, en nanosecondes
 * @param icaoAddress l'adresse OACI de l'expéditeur du message
 * @param category la catégorie d'aéronef de l'expéditeur
 * @param callSign l'indicatif de l'expéditeur
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress,
        int category, CallSign callSign) implements Message {

    /**
     * constructeur compact
     * @param timeStampNs l'horodatage du message, en nanosecondes
     * @param icaoAddress l'adresse OACI de l'expéditeur du message
     * @param category la catégorie d'aéronef de l'expéditeur
     * @param callSign l'indicatif de l'expéditeur
     * @throws NullPointerException si icaoAddress ou callSign sont nuls
     * @throws IllegalArgumentException si timeStampNs est strictement inférieure à 0
     */
    public AircraftIdentificationMessage{
        if (icaoAddress == null || callSign == null) throw new NullPointerException();
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * retourne le message d'identification correspondant au message brut donné,
     * ou null si au moins un des caractères de l'indicatif qu'il contient est invalide
     * @param rawMessage le message brut donné
     * @return le message d'identification correspondant au message brut donné
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        int category1 = (rawMessage.bytes().byteAt(4)) & 0x7;
        int category2 = ((14-rawMessage.typeCode())&0b1111)<<4;
        category1 = category2 | category1;
        StringBuilder callSign1 = new StringBuilder();
        long callSignValue = rawMessage.bytes().bytesInRange(5,11);
        for (int i = 0 ; i < 8 ; ++i){
            int c = (int) ((callSignValue >>> ((7-i)*6)) & 0x3F);
            char c1;
            if (c > 0 && c < 27){
                c1 = (char)(c +64);
            } else {
                if ((c > 47 && c < 58)||(c == 32)){
                    c1 = (char)c;
                } else {
                    return null;
                }
            }
            callSign1.append(c1);
        }
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(),rawMessage.icaoAddress(),
                category1,new CallSign(callSign1.toString().stripTrailing()));
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    /**
     * retourne l'indicatif de l'expéditeur du message
     * @return l'indicatif de l'expéditeur du message
     */
    public CallSign callSign() {
        return callSign;
    }

    /**
     * retourne la catégorie de l'expéditeur du message
     * @return la catégorie de l'expéditeur du message
     */
    public int category() {
        return category;
    }
}
