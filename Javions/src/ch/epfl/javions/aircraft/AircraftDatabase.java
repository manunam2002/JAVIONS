package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AircraftDatabase {

    private String fileName;

    public AircraftDatabase(String fileName){
        if (fileName == null) throw new NullPointerException();
        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        String name = getClass().getResource("/aircraft.zip").getFile();
        try (ZipFile z = new ZipFile(name)){
            String firstString = address.string().substring(0,2);
            InputStream stream = z.getInputStream(z.getEntry(firstString+".csv"));
            Reader reader = new InputStreamReader(stream, UTF_8);
            BufferedReader buffer = new BufferedReader(reader);
            String line = "";
            int comparator = -1;
            while (comparator < 0 && (line = buffer.readLine()) != null){
                if (line.startsWith(address.string())){
                    comparator = address.string().compareTo(line.substring(0,6));
                }
            }
            String[] aircraftData = new String[6];
            if (comparator == 0){
                String regex = ",";
                aircraftData = line.split(regex);
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