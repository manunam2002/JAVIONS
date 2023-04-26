package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

public class MapParameters {

    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    public MapParameters(int zoom, int minX, int minY){
        Preconditions.checkArgument(zoom >= 6 && zoom <= 19);
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    public ReadOnlyIntegerProperty zoomProperty(){
        return zoom;
    }

    public int zoom(){
        return zoom.get();
    }

    public ReadOnlyDoubleProperty minXProperty(){
        return minX;
    }

    public double minX(){
        return minX.get();
    }

    public ReadOnlyDoubleProperty minYProperty(){
        return minY;
    }

    public double minY(){
        return minY.get();
    }

    public void scroll(double X, double Y){
        minX.set(minX()+X);
        minY.set(minY()+Y);
    }

    public void changeZoomLevel(int delta){
        int clampedDelta = Math2.clamp(6-zoom(),delta,19-zoom());
        if (clampedDelta == 0) return;
        zoom.set(zoom() + clampedDelta);
        minX.set(Math.scalb(minX(),clampedDelta));
        minY.set(Math.scalb(minY(),clampedDelta));
    }
}