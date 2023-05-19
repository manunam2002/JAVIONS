package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * représente un gestionnaire de tuiles OSM, son rôle est d'obtenir les tuiles depuis un serveur de tuile
 * et de les stocker dans un cache mémoire et dans un cache disque
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class TileManager {

    private static final String SLASH = "/";
    private static final String PNG = ".png";
    private final Path path;
    private final String serverName;
    private final Map<TileId, Image> cache;

    /**
     * constructeur public
     *
     * @param path       le chemin d'accès au dossier contenant le cache disque
     * @param serverName le nom du serveur de tuile
     */
    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
        cache = new LinkedHashMap<>(100, 1, false);
    }

    /**
     * retourne l'image associée à l'identité d'une tuile
     *
     * @param tile l'identité d'une tuile
     * @return l'image associée
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    public Image imageForTileAt(TileId tile) throws IOException {
        if (cache.containsKey(tile)) return cache.get(tile);

        if (cache.size() == 100) {
            Iterator<TileId> it = cache.keySet().iterator();
            cache.remove(it.next());
        }

        Path tilePath = Path.of(path.toString() + SLASH + tile.zoom + SLASH + tile.x + SLASH + tile.y + PNG);
        if (Files.exists(tilePath)) {
            try (FileInputStream fis = new FileInputStream(tilePath.toString())) {
                return new Image(new ByteArrayInputStream(fis.readAllBytes()));
            }
        }

        Files.createDirectories(Path.of(path + SLASH + tile.zoom + SLASH + tile.x));
        String urlName = "https://" + serverName + SLASH + tile.zoom + SLASH + tile.x + SLASH + tile.y + PNG;
        URL u = new URL(urlName);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");

        try (InputStream i = c.getInputStream()) {
            byte[] imageBytes = i.readAllBytes();
            try (FileOutputStream s = new FileOutputStream(tilePath.toString())) {
                s.write(imageBytes);
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                cache.put(tile, image);
                return image;
            }
        }
    }

    /**
     * représente l'identité d'une tuile OSM
     *
     * @param zoom le niveau de zoom de la tuile
     * @param x    l'index X de la tuile
     * @param y    l'index Y de la tuile
     */
    public record TileId(int zoom, int x, int y) {

        /**
         * constructeur public
         *
         * @param zoom le niveau de zoom de la tuile
         * @param x    l'index X de la tuile
         * @param y    l'index Y de la tuile
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoom, x, y));
        }

        /**
         * retourne vrai ssi les arguments constituent une identité de tuile valide
         *
         * @param zoom le niveau de zoom de la tuile
         * @param x    l'index X de la tuile
         * @param y    l'index Y de la tuile
         * @return vrai ssi les arguments constituent une identité de tuile valide
         */
        public static boolean isValid(int zoom, int x, int y) {
            int max = 1 << zoom;
            return 0 <= x && x < max && 0 <= y && y < max;
        }
    }
}