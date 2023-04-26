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

public class BaseMapController {

    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private boolean redrawNeeded;
    private Pane pane;
    private Canvas canvas;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters){

        this.tileManager = tileManager;
        this.mapParameters = mapParameters;

        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());

        mapParameters.zoomProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapParameters.minXProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener((p, o, n) -> redrawOnNextPulse());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            Point2D delta = canvas.sceneToLocal(e.getSceneX(),e.getSceneY());
            mapParameters.scroll(delta.getX(),delta.getY());
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-delta.getX(),-delta.getY());
        });

        ObjectProperty<Point2D> start = new SimpleObjectProperty<>(null);
        pane.setOnMousePressed(e -> {
            start.set(new Point2D(e.getX(),e.getY()));
        });
        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(start.get().getX()-e.getX(),start.get().getY()-e.getY());
            start.set(new Point2D(e.getX(),e.getY()));
        });
        pane.setOnMouseReleased(end -> {
            start.set(null);
        });
    }

    public Pane pane(){
        return pane;
    }

    public void centerOn(GeoPos point){
        double deltaX = WebMercator.x(mapParameters.zoom(), point.longitude()) - mapParameters.minX()
                + canvas.getWidth()/2;
        double deltaY = WebMercator.y(mapParameters.zoom(), point.latitude()) - mapParameters.minY()
                + canvas.getHeight()/2;
        mapParameters.scroll(deltaX,deltaY);
    }

    private void redrawIfNeeded(){
        if (!redrawNeeded) return;
        redrawNeeded = false;

        draw();
    }

    private void redrawOnNextPulse(){
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void draw(){

        double height = canvas.getHeight();
        double width = canvas.getWidth();
        int X = (int) Math.floor(mapParameters.minX()/256);
        int Y = (int) Math.floor(mapParameters.minY()/256);
        double deltaX = mapParameters.minX() - X*256;
        double deltaY = mapParameters.minY() - Y*256;
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        for (int i = 0; i <= width+deltaX; i += 256){
            for (int j = 0; j <= height+deltaY; j+= 256){
                try {
                    Image tile = tileManager.imageForTileAt(
                            new TileManager.TileId(mapParameters.zoom(),X + i/256, Y + j/256));
                    graphicsContext.drawImage(tile,i-deltaX,j-deltaY);
                } catch (IOException e) {}
            }
        }
    }
}