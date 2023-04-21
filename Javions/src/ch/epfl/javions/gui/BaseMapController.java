package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
        pane = new Pane();
        canvas = new Canvas();
        pane.getChildren().add(canvas);
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        addListeners();
    }

    public Pane pane(){
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        double height = canvas.getHeight();
        double width = canvas.getWidth();

        int X = (int) Math.floor(mapParameters.minX()/256);
        int Y = (int) Math.floor(mapParameters.minY()/256);
        int X1 = (int) Math.floor((mapParameters.minX()+width)/256);
        int Y1 = (int) Math.floor((mapParameters.minY()-height)/256);

        for (int i = X ; i < X1; ++i){
            for (int j = Y ; j > Y1 ; --j){
                try {
                    Image tile = tileManager.imageForTileAt(new TileManager.TileId(mapParameters.zoom(), i, j));
                    canvas.getGraphicsContext2D().drawImage(tile,i,j);
                } catch (IOException ioException){}
            }
        }
        return pane;
    }

    public void centerOn(GeoPos point){
        double deltaX = WebMercator.x(mapParameters.zoom(), point.longitude()) - mapParameters.minX();
        double deltaY = WebMercator.y(mapParameters.zoom(), point.latitude()) - mapParameters.minY();
        mapParameters.scroll(deltaX,deltaY);
    }

    private void redrawIfNeeded(){
        if (!redrawNeeded) return;
        redrawNeeded = false;

        pane();
    }

    private void redrawOnNextPulse(){
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void addListeners(){
        mapParameters.zoomProperty().addListener( e -> redrawOnNextPulse());
        mapParameters.minXProperty().addListener( e -> redrawOnNextPulse());
        mapParameters.minYProperty().addListener( e -> redrawOnNextPulse());
    }
}