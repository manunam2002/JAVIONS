package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * gère l'affichage et l'interaction avec le fond de carte
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class BaseMapController {

    private static final int TILE_PIXELS = 256;
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private boolean redrawNeeded;
    private final Pane pane;
    private final Canvas canvas;

    /**
     * contructeur public
     *
     * @param tileManager   le gestionnaire de tuiles à utiliser pour obtenir les tuiles de la carte
     * @param mapParameters les paramètres de la portion visible de la carte
     */
    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {

        this.tileManager = tileManager;
        this.mapParameters = mapParameters;

        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        addListeners(mapParameters);

        addEventHandlers(mapParameters);
    }

    /**
     * retourne le panneau JavaFX affichant le fond de carte
     *
     * @return le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
        return pane;
    }

    /**
     * déplace la portion visible de la carte afin qu'elle soit centrée en le point donné
     *
     * @param point le point donné
     */
    public void centerOn(GeoPos point) {
        double deltaX = WebMercator.x(mapParameters.zoom(), point.longitude()) - (mapParameters.minX()
                + (canvas.getWidth()/2));
        double deltaY = WebMercator.y(mapParameters.zoom(), point.latitude()) - (mapParameters.minY()
                + (canvas.getHeight()/2));
        mapParameters.scroll(deltaX, deltaY);
    }

    /**
     * ajoute tous les gestionnaires dévènements
     * @param mapParameters les paramètres de la carte
     */
    private void addEventHandlers(MapParameters mapParameters) {
        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            Point2D delta = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());
            mapParameters.scroll(delta.getX(), delta.getY());
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-delta.getX(), -delta.getY());
        });

        ObjectProperty<Point2D> start = new SimpleObjectProperty<>(null);

        pane.setOnMousePressed(e -> start.set(new Point2D(e.getX(), e.getY())));
        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(start.get().getX() - e.getX(), start.get().getY() - e.getY());
            start.set(new Point2D(e.getX(), e.getY()));
        });
        pane.setOnMouseReleased(end -> start.set(null));
    }

    /**
     * ajoute tous les auditeurs
     * @param mapParameters les paramètres de la carte
     */
    private void addListeners(MapParameters mapParameters) {
        canvas.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());

        mapParameters.zoomProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapParameters.minXProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener((p, o, n) -> redrawOnNextPulse());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    /**
     * redessinne la carte si c'est nécessaire
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        draw();
    }

    /**
     * redessinne la carte au prochain battement
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * dessinne la carte sur le panneau
     */
    private void draw() {

        int X = (int) Math.floor(mapParameters.minX() / TILE_PIXELS);
        int Y = (int) Math.floor(mapParameters.minY() / TILE_PIXELS);
        double deltaX = mapParameters.minX() - X * TILE_PIXELS;
        double deltaY = mapParameters.minY() - Y * TILE_PIXELS;
        double height = (canvas.getHeight() + deltaY)/ TILE_PIXELS;
        double width = (canvas.getWidth() + deltaX)/ TILE_PIXELS;
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        for (int i = 0; i <= width; i += 1) {
            for (int j = 0; j <= height; j += 1) {
                try {
                    Image tile = tileManager.imageForTileAt(
                            new TileManager.TileId(mapParameters.zoom(), X + i, Y + j));
                    graphicsContext.drawImage(tile, (i * TILE_PIXELS) - deltaX, (j * TILE_PIXELS) - deltaY);
                } catch (IOException e) {
                }
            }
        }
    }
}