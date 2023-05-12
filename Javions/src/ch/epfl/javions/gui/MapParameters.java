package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

/**
 * représente les paramètres de la portion de la carte visible dans l'interface graphique
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public class MapParameters {

    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;

    /**
     * contructeur public
     *
     * @param zoom le niveau de zoom
     * @param minX la coordonnée x du coin haut-gauche de la portion visible de la carte
     * @param minY la coordonnée y du coin haut-gauche de la portion visible de la carte
     */
    public MapParameters(int zoom, int minX, int minY) {
        Preconditions.checkArgument(6 <= zoom && zoom <= 19);

        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    /**
     * retourne la propriété du niveau de zoom
     *
     * @return la propriété du niveau de zoom
     */
    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    /**
     * retourne la valeur du niveau de zoom
     *
     * @return la valeur du niveau de zoom
     */
    public int zoom() {
        return zoom.get();
    }

    /**
     * retourne la propriété de la coordonnée x du coin haut-gauche de la portion visible de la carte
     *
     * @return la propriété de la coordonnée x du coin haut-gauche de la portion visible de la carte
     */
    public ReadOnlyDoubleProperty minXProperty() {
        return minX;
    }

    /**
     * retourne la valeur de la coordonnée x du coin haut-gauche de la portion visible de la carte
     *
     * @return la valeur de la coordonnée x du coin haut-gauche de la portion visible de la carte
     */
    public double minX() {
        return minX.get();
    }

    /**
     * retourne la propriété de la coordonnée y du coin haut-gauche de la portion visible de la carte
     *
     * @return la propriété de la coordonnée y du coin haut-gauche de la portion visible de la carte
     */
    public ReadOnlyDoubleProperty minYProperty() {
        return minY;
    }

    /**
     * retourne la valeur de la coordonnée y du coin haut-gauche de la portion visible de la carte
     *
     * @return la valeur de la coordonnée y du coin haut-gauche de la portion visible de la carte
     */
    public double minY() {
        return minY.get();
    }

    /**
     * translate le coin haut-gauche de la portion de carte affichée d'un vecteur
     *
     * @param X la coordonnée x du vecteur
     * @param Y la coordonnée y du vecteur
     */
    public void scroll(double X, double Y) {
        minX.set(minX() + X);
        minY.set(minY() + Y);
    }

    /**
     * ajoute une différence de niveau de zoom au niveau de zoom actuel en garantissant  qu'il reste entre 6 et 19
     *
     * @param delta la différence de niveau de zoom
     */
    public void changeZoomLevel(int delta) {
        int clampedDelta = Math2.clamp(6 - zoom(), delta, 19 - zoom());
        if (clampedDelta == 0) return;
        // à verifier

        zoom.set(zoom() + clampedDelta);
        minX.set(Math.scalb(minX(), clampedDelta));
        minY.set(Math.scalb(minY(), clampedDelta));
    }
}