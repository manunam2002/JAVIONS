package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * représente la base de données mictronics des aéronefs
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class AircraftDatabase {

    private final static String REGEX = ",";

    private final String fileName;

    /**
     * constructeur public, retourne un objet représentant la base de données
     * mictronics, stockée dans le fichier de nom donné
     * @param fileName nom du ficher
     * @throws NullPointerException si le paramètre est nul
     */
    public AircraftDatabase(String fileName){
        this.fileName = Objects.requireNonNull(fileName);
    }

    /**
     * retourne les données de l'aéronef dont l'adresse OACI est celle donnée,
     * ou null si aucune entrée n'existe dans la base pour cette adresse
     * @param address l'adresse OACI donnée
     * @return  les données de l'aéronef
     * @throws IOException en cas d'erreur d'entrée
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        String firstString = address.string().substring(4, 6);
        try (ZipFile z = new ZipFile(fileName);
             InputStream stream = z.getInputStream(z.getEntry(firstString + ".csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            String line = null;
            int comparator = 1;
            while (comparator > 0 && (line = buffer.readLine()) != null) {
                if (line.startsWith(address.string())) {
                    comparator = address.string().compareTo(line.substring(0, 6));
                }
            }
            String[] aircraftData;
            if (comparator == 0) {
                aircraftData = line.split(REGEX, -1);
            } else {
                return null;
            }
            return new AircraftData(new AircraftRegistration(aircraftData[1]),
                    new AircraftTypeDesignator(aircraftData[2]),
                    aircraftData[3],
                    new AircraftDescription(aircraftData[4]),
                    WakeTurbulenceCategory.of(aircraftData[5]));
        }
    }

}