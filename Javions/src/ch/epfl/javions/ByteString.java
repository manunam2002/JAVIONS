package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * représente une chaîne d'octets
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class ByteString {

    private static final HexFormat HEX_FORMAT = HexFormat.of();

    private final byte[] bytes;

    /**
     * constructeur public
     * @param bytes tableau de bytes copié et affecté à l'attribut
     */
    public ByteString (byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * retourne la chaîne d'octets dont la chaîne passée en argument est la représentation hexadécimale
     * @param hexString chaine de caractères
     * @return chaîne d'octets
     * @throws IllegalArgumentException si la chaîne donnée n'est pas de longueur paire
     * @throws NumberFormatException si elle contient un caractère qui n'est pas un chiffre hexadécimal
     */
    public static ByteString ofHexadecimalString(String hexString){
        try {
            byte[] bytesArray = HEX_FORMAT.parseHex(hexString);
            return new ByteString(bytesArray);
        } catch (IllegalArgumentException e) {
            throw new NumberFormatException();
        }
    }

    /**
     * retourne la taille de la chaîne
     * @return la taille de la chaîne
     */
    public int size(){
        return bytes.length;
    }

    /**
     * retourne l'octet à l'index donné
     * @param index index
     * @return octet à l'index donné
     * @throws IndexOutOfBoundsException si l'index est invalide
     */
    public int byteAt(int index){
        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * retourne les octets compris entre les index fromIndex (inclus) et toIndex (exclu)
     * sous la forme d'une valeur de type long
     * @param fromIndex index de départ
     * @param toIndex index d'arrivée
     * @return valeur des octets selectionnés sous la forme de type long
     * @throws IndexOutOfBoundsException si la plage décrite par fromIndex et toIndex n'est pas totalement
     * comprise entre 0 et la taille de la chaîne
     * @throws IllegalArgumentException si la différence entre toIndex et fromIndex n'est pas strictement inférieure
     * au nombre d'octets contenus dans une valeur de type long
     */
    public long bytesInRange(int fromIndex, int toIndex){
        Objects.checkFromToIndex(fromIndex,toIndex,bytes.length);
        Preconditions.checkArgument(toIndex-fromIndex < Long.BYTES);
        long value = 0;
        for (int i = 0 ; i < toIndex-fromIndex ; ++i){
            long l = (long) byteAt(toIndex - i - 1) << Byte.SIZE*i;
            value = value | l;
        }
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ByteString){
            return Arrays.equals(this.bytes, ((ByteString) obj).bytes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return HEX_FORMAT.withUpperCase().formatHex(bytes);
    }
}

