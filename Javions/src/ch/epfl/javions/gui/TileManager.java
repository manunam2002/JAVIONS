package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager {

    private final Path path;
    private final String serverName;

    private final Map<TileId, Image> cache = new LinkedHashMap<>(100,1,false);

    public TileManager(Path path, String serverName){
        this.path = path;
        this.serverName = serverName;
    }

    public Image imageForTileAt(TileId tile) throws IOException {
        if (cache.containsKey(tile)) return cache.get(tile);

        if (cache.size() == 100){
            Iterator<TileId> it = cache.keySet().iterator();
            cache.remove(it.next());
        }

        Path tilePath = Path.of(path.toString()+"/"+tile.zoom+"/"+tile.x+"/"+tile.y+".png");
        if (Files.exists(tilePath)){
            try(FileInputStream s = new FileInputStream(tilePath.toString())) {
                byte[] imageBytes = s.readAllBytes();
                return new Image(new ByteArrayInputStream(imageBytes));
            }
        }
        Files.createDirectories(Path.of(path.toString()+"/"+tile.zoom+"/"+tile.x));
        String urlName = "https://"+serverName+"/"+tile.zoom+"/"+tile.x+"/"+tile.y+".png";
        URL u = new URL(urlName);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent","Javions");
        try (InputStream i = c.getInputStream()){
            byte[] imageBytes = i.readAllBytes();
            FileOutputStream s = new FileOutputStream(tilePath.toString());
            s.write(imageBytes);
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            cache.put(tile,image);
            return image;
        }
    }

    public record TileId(int zoom, int x, int y){

        public static boolean isValid(int zoom, int x, int y){
            int max = 1 << zoom;
            return x >= 0 && x < max && y >= 0 && y < max;
        }
    }
}